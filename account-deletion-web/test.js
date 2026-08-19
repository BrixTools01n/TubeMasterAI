/**
 * Automated Verification Test Suite for TubeMaster AI Connected Account Deletion Service
 */

const http = require('http');
const server = require('./server');

const TEST_PORT = 9091;

function makeRequest(options, postData = null) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        let json = null;
        try {
          json = JSON.parse(data);
        } catch (_) {}
        resolve({
          statusCode: res.statusCode,
          headers: res.headers,
          body: data,
          json
        });
      });
    });

    req.on('error', reject);

    if (postData) {
      req.write(typeof postData === 'string' ? postData : JSON.stringify(postData));
    }
    req.end();
  });
}

async function runTests() {
  console.log('--- STARTING TUBEMASTER AI CONNECTED DELETION WEB TESTS ---');

  const instance = server.listen(TEST_PORT, '127.0.0.1');

  try {
    // 1. Test /health
    console.log('1. Testing /health...');
    const healthRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/health',
      method: 'GET'
    });
    if (healthRes.statusCode !== 200 || healthRes.json?.status !== 'ok') {
      throw new Error(`Health check failed: ${healthRes.statusCode} ${healthRes.body}`);
    }
    console.log('✓ /health OK (HTTP 200)');

    // 2. Test / (Main Deletion Portal)
    console.log('2. Testing / (Main Portal)...');
    const indexRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/',
      method: 'GET'
    });
    if (indexRes.statusCode !== 200 || !indexRes.body.includes('User Data - Account Deletion')) {
      throw new Error(`Index check failed: ${indexRes.statusCode}`);
    }
    console.log('✓ / OK (HTTP 200 with User Data - Account Deletion)');

    // 3. Test /privacy
    console.log('3. Testing /privacy...');
    const privacyRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/privacy',
      method: 'GET'
    });
    if (privacyRes.statusCode !== 200 || !privacyRes.body.includes('Privacy Policy for TubeMaster AI')) {
      throw new Error(`Privacy check failed: ${privacyRes.statusCode}`);
    }
    console.log('✓ /privacy OK (HTTP 200)');

    // 4. Test /api/config
    console.log('4. Testing /api/config...');
    const configRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/api/config',
      method: 'GET'
    });
    if (configRes.statusCode !== 200 || !configRes.json?.appName) {
      throw new Error(`Config check failed: ${configRes.statusCode}`);
    }
    console.log('✓ /api/config OK');

    // 5. Test Unauthenticated /api/me (Should be 401)
    console.log('5. Testing unauthenticated /api/me...');
    const unauthMeRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/api/me',
      method: 'GET'
    });
    if (unauthMeRes.statusCode !== 401) {
      throw new Error(`Unauthenticated me should return 401, got ${unauthMeRes.statusCode}`);
    }
    console.log('✓ Unauthenticated /api/me correctly rejected (HTTP 401)');

    // 6. Test Unauthenticated /api/account/delete (Should be 401)
    console.log('6. Testing unauthenticated /api/account/delete...');
    const unauthDelRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/api/account/delete',
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    }, { confirmed: true });
    if (unauthDelRes.statusCode !== 401) {
      throw new Error(`Unauthenticated deletion should return 401, got ${unauthDelRes.statusCode}`);
    }
    console.log('✓ Unauthenticated deletion correctly prevented (HTTP 401)');

    // 7. Test Google Authentication POST /api/auth/google
    console.log('7. Testing Google Authentication POST /api/auth/google...');
    const authRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/api/auth/google',
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    }, {
      credential: {
        email: 'creator.test@tubemaster.ai',
        name: 'Test Creator',
        picture: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&auto=format&fit=crop&q=80',
        sub: 'google-sub-123456789'
      }
    });

    if (authRes.statusCode !== 200 || !authRes.json?.success) {
      throw new Error(`Auth failed: ${authRes.statusCode} ${authRes.body}`);
    }

    const setCookie = authRes.headers['set-cookie']?.[0];
    if (!setCookie || !setCookie.includes('tm_session=')) {
      throw new Error('Session cookie was not returned upon successful authentication');
    }
    const sessionCookie = setCookie.split(';')[0];
    console.log('✓ Google Authentication Succeeded (HTTP 200, Session Cookie Set)');

    // 8. Test Authenticated GET /api/me with session cookie
    console.log('8. Testing authenticated GET /api/me...');
    const authMeRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/api/me',
      method: 'GET',
      headers: { Cookie: sessionCookie }
    });

    if (authMeRes.statusCode !== 200 || authMeRes.json?.user?.email !== 'creator.test@tubemaster.ai') {
      throw new Error(`Authenticated /api/me failed: ${authMeRes.statusCode} ${authMeRes.body}`);
    }
    console.log(`✓ Authenticated /api/me verified for user: ${authMeRes.json.user.email}`);

    // 9. Test Authenticated Account Deletion POST /api/account/delete without confirmation checkbox
    console.log('9. Testing deletion without confirmation checkbox...');
    const unconfirmedDelRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/api/account/delete',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Cookie: sessionCookie
      }
    }, { confirmed: false });

    if (unconfirmedDelRes.statusCode !== 400) {
      throw new Error(`Unconfirmed deletion should be 400, got: ${unconfirmedDelRes.statusCode}`);
    }
    console.log('✓ Unconfirmed deletion correctly blocked (HTTP 400)');

    // 10. Test Authenticated Account Deletion POST /api/account/delete with confirmation
    console.log('10. Testing authenticated deletion execution...');
    const confirmedDelRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/api/account/delete',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Cookie: sessionCookie
      }
    }, { confirmed: true, reason: 'test_automated_deletion' });

    if (confirmedDelRes.statusCode !== 200 || !confirmedDelRes.json?.success) {
      throw new Error(`Confirmed deletion failed: ${confirmedDelRes.statusCode} ${confirmedDelRes.body}`);
    }

    if (!confirmedDelRes.json.requestId?.startsWith('TM-DEL-')) {
      throw new Error(`Invalid deletion requestId: ${confirmedDelRes.json.requestId}`);
    }
    console.log(`✓ Account Deletion Succeeded: ${confirmedDelRes.json.requestId} (Status: ${confirmedDelRes.json.status})`);

    // 11. Test Session is invalid after deletion
    console.log('11. Verifying session invalidated after deletion...');
    const postDelMeRes = await makeRequest({
      hostname: '127.0.0.1',
      port: TEST_PORT,
      path: '/api/me',
      method: 'GET',
      headers: { Cookie: sessionCookie }
    });

    if (postDelMeRes.statusCode !== 401) {
      throw new Error(`Session should be invalidated, got ${postDelMeRes.statusCode}`);
    }
    console.log('✓ Post-deletion session successfully invalidated (HTTP 401)');

    console.log('\n======================================================');
    console.log(' ALL 11 TEST SUITES PASSED CLEANLY & ACCURATELY! ');
    console.log('======================================================\n');
  } finally {
    instance.close();
  }
}

if (require.main === module) {
  runTests().catch(err => {
    console.error('TEST SUITE FAILED:', err);
    process.exit(1);
  });
}
