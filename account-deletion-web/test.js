/**
 * Self-contained automated test suite for TubeMaster AI Deletion Web Service
 * Verifies /health, /, /privacy, /api/config, and the two-stage deletion flow.
 */

const http = require('http');

process.env.NODE_ENV = 'test';
process.env.PORT = '8995';
process.env.SUPPORT_EMAIL = 'hloob07@gmail.com';
process.env.PRIVACY_POLICY_URL = '/privacy';

const server = require('./server.js');
const running = server.listen(8995, '127.0.0.1');

function request(options, postData) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, res => {
      let body = '';
      res.on('data', chunk => (body += chunk));
      res.on('end', () => resolve({ statusCode: res.statusCode, headers: res.headers, body }));
    });
    req.on('error', reject);
    if (postData) req.write(postData);
    req.end();
  });
}

async function runTests() {
  await new Promise(r => setTimeout(r, 200));
  console.log('--- Running TubeMaster AI Account Deletion Self-Tests ---');

  // 1. Health Probe
  const healthRes = await request({ hostname: '127.0.0.1', port: 8995, path: '/health', method: 'GET' });
  if (healthRes.statusCode !== 200) throw new Error(`Health check failed: ${healthRes.statusCode}`);
  console.log('✓ GET /health passed (Status 200)');

  // 2. Homepage (Account Deletion Portal)
  const homeRes = await request({ hostname: '127.0.0.1', port: 8995, path: '/', method: 'GET' });
  if (homeRes.statusCode !== 200 || !homeRes.body.includes('TubeMaster AI')) {
    throw new Error(`Home page failed: ${homeRes.statusCode}`);
  }
  console.log('✓ GET / passed (Status 200)');

  // 3. Privacy Policy Page
  const privacyRes = await request({ hostname: '127.0.0.1', port: 8995, path: '/privacy', method: 'GET' });
  if (privacyRes.statusCode !== 200 || !privacyRes.body.includes('Privacy Policy for TubeMaster AI')) {
    throw new Error(`Privacy page failed: ${privacyRes.statusCode}`);
  }
  console.log('✓ GET /privacy passed (Status 200)');

  // 4. Safe Config API
  const configRes = await request({ hostname: '127.0.0.1', port: 8995, path: '/api/config', method: 'GET' });
  if (configRes.statusCode !== 200 || !configRes.body.includes('hloob07@gmail.com')) {
    throw new Error(`Config API failed: ${configRes.statusCode}`);
  }
  console.log('✓ GET /api/config passed (Status 200)');

  // 5. Stage 1: Request Deletion (Valid)
  const reqRes = await request(
    {
      hostname: '127.0.0.1',
      port: 8995,
      path: '/api/account-deletion/request',
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    },
    JSON.stringify({ email: 'creator@example.com', reason: 'no_longer_needed', confirmed: true })
  );
  const reqData = JSON.parse(reqRes.body);
  if (reqRes.statusCode !== 200 || !reqData.success || reqData.status !== 'VERIFICATION_REQUIRED') {
    throw new Error(`Stage 1 initiation failed: ${reqRes.body}`);
  }
  console.log(`✓ POST /api/account-deletion/request passed (ID: ${reqData.requestId})`);

  // 6. Stage 2: Ownership Verification (Invalid Code)
  const invalidVerifyRes = await request(
    {
      hostname: '127.0.0.1',
      port: 8995,
      path: '/api/account-deletion/verify',
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    },
    JSON.stringify({ requestId: reqData.requestId, code: '000000', confirmed: true })
  );
  if (invalidVerifyRes.statusCode !== 400) {
    throw new Error(`Invalid code rejection failed: ${invalidVerifyRes.statusCode}`);
  }
  console.log('✓ POST /api/account-deletion/verify correctly rejected invalid code (Status 400)');

  // 7. Stage 2: Ownership Verification (Valid Code)
  const validVerifyRes = await request(
    {
      hostname: '127.0.0.1',
      port: 8995,
      path: '/api/account-deletion/verify',
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    },
    JSON.stringify({ requestId: reqData.requestId, code: reqData._testCode, confirmed: true })
  );
  const validData = JSON.parse(validVerifyRes.body);
  if (validVerifyRes.statusCode !== 200 || !validData.success || validData.status !== 'DELETION_COMPLETED') {
    throw new Error(`Stage 2 deletion failed: ${validVerifyRes.body}`);
  }
  console.log('✓ POST /api/account-deletion/verify completed atomic deletion (Status 200)');

  console.log('--- ALL SELF-TESTS PASSED SUCCESSFULLY ---');
  running.close();
  process.exit(0);
}

runTests().catch(err => {
  console.error('Test Suite Error:', err);
  if (running) running.close();
  process.exit(1);
});
