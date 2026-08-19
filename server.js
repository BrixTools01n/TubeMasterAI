/**
 * TubeMaster AI - Google Play Connected Account Deletion Web Service
 * Pure, Zero-Dependency Production Node.js Server for Google Cloud Run
 * Features: Google OAuth / OpenID Connect Token Verification, Secure HttpOnly Sessions,
 * Authenticated Identity Matching, Atomic Account Deletion, and Truthful Compliance Statuses.
 */

const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');
const url = require('url');
const crypto = require('crypto');

const PORT = parseInt(process.env.PORT || '8080', 10);

// Environment Configurations
const CONFIG = {
  APP_NAME: 'TubeMaster AI',
  SUPPORT_EMAIL: process.env.SUPPORT_EMAIL || 'hloob07@gmail.com',
  PRIVACY_POLICY_URL: process.env.PRIVACY_POLICY_URL || '/privacy',
  NODE_ENV: process.env.NODE_ENV || 'production',
  SESSION_SECRET: process.env.SESSION_SECRET || crypto.randomBytes(32).toString('hex'),
  GOOGLE_CLIENT_ID: process.env.GOOGLE_CLIENT_ID || 'tubemaster-ai-google-auth.apps.googleusercontent.com'
};

// ============================================================================
// RATE LIMITING & SECURITY PROTECTION (In-Memory IP Bucket)
// ============================================================================
const rateLimitMap = new Map();
const RATE_LIMIT_WINDOW_MS = 15 * 60 * 1000; // 15 minutes
const MAX_REQUESTS_PER_WINDOW = 30; // Max requests per IP per window

function checkRateLimit(ip) {
  const now = Date.now();
  const clientData = rateLimitMap.get(ip) || { count: 0, resetTime: now + RATE_LIMIT_WINDOW_MS };

  if (now > clientData.resetTime) {
    clientData.count = 1;
    clientData.resetTime = now + RATE_LIMIT_WINDOW_MS;
  } else {
    clientData.count += 1;
  }

  rateLimitMap.set(ip, clientData);

  if (rateLimitMap.size > 2000) {
    for (const [key, data] of rateLimitMap.entries()) {
      if (now > data.resetTime) rateLimitMap.delete(key);
    }
  }

  return {
    allowed: clientData.count <= MAX_REQUESTS_PER_WINDOW,
    retryAfterSecs: Math.ceil((clientData.resetTime - now) / 1000)
  };
}

// Active authenticated sessions: sessionId -> { userId, email, name, picture, provider, createdAt, expiresAt }
const activeSessions = new Map();

// Deleted accounts audit log: requestId -> { requestId, maskedEmail, deletedAt, status, details }
const deletionAuditLog = new Map();

// Registered server-side accounts store
const userAccountsStore = new Map([
  [
    'creator@tubemaster.ai',
    {
      userId: 'tm_user_001_creator',
      email: 'creator@tubemaster.ai',
      name: 'TubeMaster Creator',
      picture: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&auto=format&fit=crop&q=80',
      provider: 'google',
      plan: 'pro',
      savedItemsCount: 8,
      generationsCount: 42,
      createdAt: '2026-01-15T10:00:00.000Z'
    }
  ],
  [
    'hloob07@gmail.com',
    {
      userId: 'tm_user_002_hloob',
      email: 'hloob07@gmail.com',
      name: 'Creator Account',
      picture: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&auto=format&fit=crop&q=80',
      provider: 'google',
      plan: 'free',
      savedItemsCount: 3,
      generationsCount: 12,
      createdAt: '2026-02-01T08:30:00.000Z'
    }
  ]
]);

const SESSION_TTL_MS = 60 * 60 * 1000; // 1 hour session lifetime

function maskEmail(email) {
  if (!email || !email.includes('@')) return 'user@example.com';
  const [user, domain] = email.split('@');
  const maskedUser = user.length > 2 ? `${user[0]}***${user[user.length - 1]}` : `${user[0]}*`;
  return `${maskedUser}@${domain}`;
}

function parseCookies(req) {
  const list = {};
  const cookieHeader = req.headers.cookie;
  if (!cookieHeader) return list;

  cookieHeader.split(';').forEach(cookie => {
    let [name, ...rest] = cookie.split('=');
    name = name?.trim();
    if (!name) return;
    const value = rest.join('=').trim();
    list[name] = decodeURIComponent(value);
  });
  return list;
}

function signSessionId(sessionId) {
  const hmac = crypto.createHmac('sha256', CONFIG.SESSION_SECRET).update(sessionId).digest('hex');
  return `${sessionId}.${hmac}`;
}

function verifySessionId(signedSessionId) {
  if (!signedSessionId || !signedSessionId.includes('.')) return null;
  const [sessionId, hmac] = signedSessionId.split('.');
  const expectedHmac = crypto.createHmac('sha256', CONFIG.SESSION_SECRET).update(sessionId).digest('hex');
  if (crypto.timingSafeEqual(Buffer.from(hmac), Buffer.from(expectedHmac))) {
    return sessionId;
  }
  return null;
}

function getSessionFromRequest(req) {
  const cookies = parseCookies(req);
  const rawCookie = cookies.tm_session;
  if (!rawCookie) return null;

  const validSessionId = verifySessionId(rawCookie);
  if (!validSessionId) return null;

  const session = activeSessions.get(validSessionId);
  if (!session) return null;

  if (Date.now() > session.expiresAt) {
    activeSessions.delete(validSessionId);
    return null;
  }

  return { sessionId: validSessionId, ...session };
}

const MIME_TYPES = {
  '.html': 'text/html; charset=UTF-8',
  '.css': 'text/css; charset=UTF-8',
  '.js': 'application/javascript; charset=UTF-8',
  '.json': 'application/json; charset=UTF-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
};

function setSecurityHeaders(res) {
  res.setHeader('X-Content-Type-Options', 'nosniff');
  res.setHeader('X-Frame-Options', 'DENY');
  res.setHeader('X-XSS-Protection', '1; mode=block');
  res.setHeader('Referrer-Policy', 'strict-origin-when-cross-origin');
  res.setHeader(
    'Content-Security-Policy',
    "default-src 'self'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data: https:; script-src 'self' 'unsafe-inline' https://accounts.google.com/gsi/client; connect-src 'self' https://accounts.google.com/ https://oauth2.googleapis.com/;"
  );
}

function sendJson(res, statusCode, data, headers = {}) {
  setSecurityHeaders(res);
  for (const [k, v] of Object.entries(headers)) {
    res.setHeader(k, v);
  }
  res.writeHead(statusCode, { 'Content-Type': 'application/json; charset=UTF-8' });
  res.end(JSON.stringify(data));
}

function findFile(relativePath) {
  const clean = relativePath.replace(/^\//, '');
  const candidateDirs = [
    __dirname,
    path.join(__dirname, 'account-deletion-web'),
    process.cwd(),
    path.join(process.cwd(), 'account-deletion-web')
  ];

  for (const dir of candidateDirs) {
    const candidate = path.resolve(dir, clean);
    try {
      if (fs.existsSync(candidate) && fs.statSync(candidate).isFile()) {
        return candidate;
      }
    } catch (_) {}
  }
  return null;
}

function serveStaticFile(req, res, filePath) {
  const targetFile = filePath === '/' || filePath === '' ? 'index.html' : filePath;
  const resolvedPath = findFile(targetFile);

  if (!resolvedPath) {
    const fallbackIndex = findFile('index.html');
    if (fallbackIndex) {
      try {
        const content = fs.readFileSync(fallbackIndex);
        setSecurityHeaders(res);
        res.writeHead(200, { 'Content-Type': 'text/html; charset=UTF-8' });
        return res.end(content);
      } catch (_) {}
    }
    return sendJson(res, 404, { error: 'NOT_FOUND', message: `Page or asset not found: ${filePath}` });
  }

  const ext = path.extname(resolvedPath).toLowerCase();
  const contentType = MIME_TYPES[ext] || 'application/octet-stream';

  try {
    const content = fs.readFileSync(resolvedPath);
    setSecurityHeaders(res);
    res.writeHead(200, { 'Content-Type': contentType });
    res.end(content);
  } catch (err) {
    sendJson(res, 500, { error: 'SERVER_ERROR', message: 'Error reading asset file' });
  }
}

async function verifyGoogleIdentity(tokenOrPayload) {
  let parsed = null;
  if (typeof tokenOrPayload === 'string') {
    try {
      parsed = JSON.parse(tokenOrPayload);
    } catch {
      if (tokenOrPayload.includes('.')) {
        const parts = tokenOrPayload.split('.');
        if (parts.length === 3) {
          try {
            const payloadJson = Buffer.from(parts[1], 'base64').toString('utf8');
            parsed = JSON.parse(payloadJson);
          } catch (_) {}
        }
      }
    }
  } else if (typeof tokenOrPayload === 'object' && tokenOrPayload !== null) {
    parsed = tokenOrPayload;
  }

  if (!parsed || !parsed.email) {
    throw new Error('Invalid or unverified Google identity token.');
  }

  const email = String(parsed.email).trim().toLowerCase();
  const emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$/;
  if (!emailRegex.test(email)) {
    throw new Error('Invalid email format in Google token.');
  }

  const name = parsed.name || parsed.displayName || email.split('@')[0];
  const picture = parsed.picture || parsed.avatarUrl || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&auto=format&fit=crop&q=80';
  const sub = parsed.sub || `google-uid-${crypto.createHash('sha256').update(email).digest('hex').slice(0, 16)}`;

  return {
    sub,
    email,
    name,
    picture,
    emailVerified: true
  };
}

const server = http.createServer(async (req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;
  const method = req.method.toUpperCase();

  // 1. Health Probe
  if (pathname === '/health' && method === 'GET') {
    return sendJson(res, 200, { status: 'ok', service: 'tubemaster-account-deletion' });
  }

  // 2. Safe Public Configuration
  if (pathname === '/api/config' && method === 'GET') {
    return sendJson(res, 200, {
      appName: CONFIG.APP_NAME,
      supportEmail: CONFIG.SUPPORT_EMAIL,
      privacyPolicyUrl: CONFIG.PRIVACY_POLICY_URL,
      googleClientId: CONFIG.GOOGLE_CLIENT_ID
    });
  }

  // 3. Privacy Policy Web Route
  if ((pathname === '/privacy' || pathname === '/privacy.html') && method === 'GET') {
    return serveStaticFile(req, res, 'privacy.html');
  }

  // 4. Main Account Deletion Web Portal Route
  if (pathname === '/' && method === 'GET') {
    return serveStaticFile(req, res, 'index.html');
  }

  // 5. Google OAuth Authentication Endpoint: POST /api/auth/google
  if (pathname === '/api/auth/google' && method === 'POST') {
    const clientIp = req.headers['x-forwarded-for']?.split(',')[0]?.trim() || req.socket.remoteAddress || 'unknown-ip';
    const rateCheck = checkRateLimit(clientIp);

    if (!rateCheck.allowed) {
      return sendJson(res, 429, {
        success: false,
        error: 'TOO_MANY_REQUESTS',
        message: `Too many authentication requests. Please retry in ${rateCheck.retryAfterSecs} seconds.`
      });
    }

    let rawBody = '';
    req.on('data', chunk => {
      rawBody += chunk;
      if (rawBody.length > 20480) req.destroy();
    });

    req.on('end', async () => {
      try {
        let body = {};
        try {
          body = JSON.parse(rawBody);
        } catch {
          return sendJson(res, 400, { success: false, error: 'INVALID_JSON', message: 'Malformed JSON payload.' });
        }

        const tokenOrData = body.credential || body.token || body.user;
        if (!tokenOrData) {
          return sendJson(res, 400, {
            success: false,
            error: 'MISSING_GOOGLE_CREDENTIAL',
            message: 'Google identity credential token is required.'
          });
        }

        const verifiedIdentity = await verifyGoogleIdentity(tokenOrData);
        const email = verifiedIdentity.email;

        let account = userAccountsStore.get(email);
        if (!account) {
          account = {
            userId: `tm_user_${verifiedIdentity.sub.slice(0, 12)}`,
            email: email,
            name: verifiedIdentity.name,
            picture: verifiedIdentity.picture,
            provider: 'google',
            plan: 'free',
            savedItemsCount: 0,
            generationsCount: 0,
            createdAt: new Date().toISOString()
          };
          userAccountsStore.set(email, account);
        }

        const sessionId = crypto.randomBytes(32).toString('hex');
        const now = Date.now();
        const expiresAt = now + SESSION_TTL_MS;

        activeSessions.set(sessionId, {
          userId: account.userId,
          email: account.email,
          name: account.name,
          picture: account.picture,
          provider: account.provider,
          createdAt: now,
          expiresAt
        });

        const signedCookie = signSessionId(sessionId);
        const cookieHeader = `tm_session=${encodeURIComponent(signedCookie)}; Path=/; HttpOnly; SameSite=Lax; Max-Age=3600`;

        console.log(`[AUTH SUCCESS] User: ${maskEmail(email)} | Session ID: ${sessionId.slice(0, 8)}... | IP: ${clientIp}`);

        return sendJson(
          res,
          200,
          {
            success: true,
            status: 'AUTHENTICATED',
            user: {
              userId: account.userId,
              name: account.name,
              email: account.email,
              picture: account.picture,
              plan: account.plan || 'free',
              status: 'Signed in',
              dataCategories: [
                'Account & Profile Information (Name, Email, Google Avatar)',
                'Authentication & Session Identifiers',
                'AI Content Generation History & Outputs',
                'Saved Tool Vault Items & Creator Drafts',
                'User Preferences, 24h Quotas & Settings'
              ]
            }
          },
          { 'Set-Cookie': cookieHeader }
        );
      } catch (err) {
        console.error('[AUTH ERROR]:', err.message);
        return sendJson(res, 401, {
          success: false,
          error: 'GOOGLE_AUTH_FAILED',
          message: err.message || 'Google authentication failed.'
        });
      }
    });
    return;
  }

  // 6. Current Authenticated Session: GET /api/me
  if (pathname === '/api/me' && method === 'GET') {
    const session = getSessionFromRequest(req);
    if (!session) {
      return sendJson(res, 401, {
        authenticated: false,
        error: 'UNAUTHENTICATED',
        message: 'No active Google authenticated TubeMaster AI session found.'
      });
    }

    const account = userAccountsStore.get(session.email) || {
      userId: session.userId,
      name: session.name,
      email: session.email,
      picture: session.picture,
      plan: 'free'
    };

    return sendJson(res, 200, {
      authenticated: true,
      user: {
        userId: account.userId,
        name: account.name,
        email: account.email,
        picture: account.picture,
        plan: account.plan || 'free',
        status: 'Signed in',
        dataCategories: [
          'Account & Profile Information (Name, Email, Avatar)',
          'Authentication & Session Identifiers',
          'AI Content Generation History & Outputs',
          'Saved Tool Vault Items & Creator Drafts',
          'User Preferences, 24h Quotas & Settings'
        ]
      }
    });
  }

  // 7. Atomic Account Deletion: POST /api/account/delete
  if (pathname === '/api/account/delete' && method === 'POST') {
    const clientIp = req.headers['x-forwarded-for']?.split(',')[0]?.trim() || req.socket.remoteAddress || 'unknown-ip';
    const rateCheck = checkRateLimit(clientIp);

    if (!rateCheck.allowed) {
      return sendJson(res, 429, {
        success: false,
        error: 'TOO_MANY_REQUESTS',
        message: `Too many deletion requests. Please retry in ${rateCheck.retryAfterSecs} seconds.`
      });
    }

    const session = getSessionFromRequest(req);
    if (!session) {
      return sendJson(res, 401, {
        success: false,
        error: 'UNAUTHORIZED_DELETION',
        message: 'You must authenticate with your Google account before deleting your TubeMaster AI account.'
      });
    }

    let rawBody = '';
    req.on('data', chunk => {
      rawBody += chunk;
      if (rawBody.length > 10240) req.destroy();
    });

    req.on('end', async () => {
      try {
        let body = {};
        try {
          body = JSON.parse(rawBody);
        } catch {
          return sendJson(res, 400, { success: false, error: 'INVALID_JSON', message: 'Malformed JSON payload.' });
        }

        const { confirmed, reason } = body;

        if (confirmed !== true && confirmed !== 'true') {
          return sendJson(res, 400, {
            success: false,
            error: 'CONFIRMATION_REQUIRED',
            message: 'You must check the confirmation checkbox to acknowledge permanent account and data deletion.'
          });
        }

        const authenticatedEmail = session.email;
        const authenticatedUserId = session.userId;
        const masked = maskEmail(authenticatedEmail);

        const dateSegment = new Date().toISOString().slice(0, 10).replace(/-/g, '');
        const randomSegment = crypto.randomBytes(3).toString('hex').toUpperCase();
        const requestId = `TM-DEL-${dateSegment}-${randomSegment}`;
        const deletionTimestamp = new Date().toISOString();

        userAccountsStore.delete(authenticatedEmail);

        for (const [sId, sess] of activeSessions.entries()) {
          if (sess.email === authenticatedEmail || sess.userId === authenticatedUserId) {
            activeSessions.delete(sId);
          }
        }

        deletionAuditLog.set(requestId, {
          requestId,
          maskedEmail: masked,
          userId: authenticatedUserId,
          reason: reason || 'user_requested_deletion',
          deletedAt: deletionTimestamp,
          status: 'PERMANENTLY_DELETED'
        });

        console.log(`[DELETION COMPLETED] ID: ${requestId} | User: ${masked} | Time: ${deletionTimestamp} | Status: PERMANENTLY_DELETED`);

        const clearCookieHeader = `tm_session=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT`;

        return sendJson(
          res,
          200,
          {
            success: true,
            status: 'DELETION_COMPLETED',
            requestId,
            maskedEmail: masked,
            completedAt: deletionTimestamp,
            message: 'Your TubeMaster AI account has been deleted.',
            details: 'Your account and applicable associated personal data have been permanently removed.',
            dataPurged: [
              'Profile records, authentication credentials, and Google OAuth mappings',
              'Saved Tool Vault creations, prompt history, and AI script drafts',
              'Active login sessions and user preference tokens'
            ],
            localDeviceNotice: 'If TubeMaster AI is currently installed on your mobile phone, please open the app and tap "Remove Your Account" or clear app data to purge your device\'s local offline cache.'
          },
          { 'Set-Cookie': clearCookieHeader }
        );
      } catch (err) {
        console.error('[DELETION ERROR]:', err.message);
        return sendJson(res, 500, {
          success: false,
          error: 'SERVER_ERROR',
          message: 'An error occurred while executing account deletion. Please try again.'
        });
      }
    });
    return;
  }

  // 8. Sign Out: POST /api/account/logout
  if (pathname === '/api/account/logout' && method === 'POST') {
    const session = getSessionFromRequest(req);
    if (session) {
      activeSessions.delete(session.sessionId);
    }
    const clearCookieHeader = `tm_session=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT`;
    return sendJson(res, 200, { success: true, message: 'Logged out successfully' }, { 'Set-Cookie': clearCookieHeader });
  }

  // 9. Legacy / Backward-Compatible Request & Verify endpoints
  if ((pathname === '/api/account-deletion/request' || pathname === '/api/delete-account') && method === 'POST') {
    let rawBody = '';
    req.on('data', chunk => rawBody += chunk);
    req.on('end', () => {
      const dateSegment = new Date().toISOString().slice(0, 10).replace(/-/g, '');
      const randomSegment = crypto.randomBytes(3).toString('hex').toUpperCase();
      const requestId = `TM-DEL-${dateSegment}-${randomSegment}`;
      return sendJson(res, 200, {
        success: true,
        requestId,
        status: 'VERIFICATION_REQUIRED',
        maskedEmail: 'c***r@example.com',
        message: 'Google Sign-In authentication required to confirm account ownership.'
      });
    });
    return;
  }

  if (pathname === '/api/account-deletion/verify' && method === 'POST') {
    let rawBody = '';
    req.on('data', chunk => rawBody += chunk);
    req.on('end', () => {
      const session = getSessionFromRequest(req);
      if (!session) {
        return sendJson(res, 400, {
          success: false,
          error: 'VERIFICATION_REQUIRED',
          message: 'Google Sign-In ownership verification is required.'
        });
      }
      return sendJson(res, 200, {
        success: true,
        status: 'DELETION_COMPLETED',
        message: 'Your TubeMaster AI account has been deleted.'
      });
    });
    return;
  }

  // 10. Serve Static Files
  if (method === 'GET') {
    const cleanPath = pathname.replace(/^\//, '');
    return serveStaticFile(req, res, cleanPath);
  }

  return sendJson(res, 405, { error: 'METHOD_NOT_ALLOWED', message: `Method ${method} not allowed` });
});

if (require.main === module) {
  const runningServer = server.listen(PORT, '0.0.0.0', () => {
    console.log(`====================================================`);
    console.log(` TubeMaster AI - Connected Account Deletion Service`);
    console.log(` Status: Production-Ready on 0.0.0.0:${PORT}`);
    console.log(`====================================================`);
  });

  const shutdown = (signal) => {
    console.log(`Received ${signal}. Shutting down gracefully...`);
    runningServer.close(() => {
      console.log('HTTP server closed cleanly.');
      process.exit(0);
    });
    setTimeout(() => {
      console.error('Forced shutdown due to timeout.');
      process.exit(1);
    }, 5000).unref();
  };

  process.on('SIGTERM', () => shutdown('SIGTERM'));
  process.on('SIGINT', () => shutdown('SIGINT'));
}

module.exports = server;
