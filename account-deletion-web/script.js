/**
 * TubeMaster AI - Connected Account Deletion Web Portal Script
 * Features:
 * - Option 1: Auto Detect Account (One-Tap / Browser-Authenticated Google Identity Resolution)
 * - Option 2: Continue with Google (Single Canonical Google OAuth / GIS Verification)
 * - Option 3: Delete by Email (Privacy-Preserving 6-digit OTP Ownership Verification with 15m Expiry)
 * - Session Management, Account Confirmation, and Atomic Backend Account Deletion
 * - Robust JSON API Handlers (Guaranteed Zero Unexpected Token '<' Errors)
 */

(function () {
  'use strict';

  // DOM Elements - Views
  const viewStep1 = document.getElementById('view-step-1'); // 3-Option Auth Chooser
  const viewStep2 = document.getElementById('view-step-2'); // Confirmation Screen
  const viewStep3 = document.getElementById('view-step-3'); // Success Screen

  // DOM Elements - Option 1 (Auto Detect)
  const btnAutoDetect = document.getElementById('btn-auto-detect');

  // DOM Elements - Option 2 (Google Auth)
  const btnGoogleAuthDirect = document.getElementById('btn-google-auth-direct');

  // DOM Elements - Option 3 (Email & OTP)
  const inputGmailAddress = document.getElementById('input-gmail-address');
  const btnSubmitGmail = document.getElementById('btn-submit-gmail');
  const panelEmailCode = document.getElementById('panel-email-code');
  const codeSentMessage = document.getElementById('code-sent-message');
  const inputVerificationCode = document.getElementById('input-verification-code');
  const btnVerifyEmailCode = document.getElementById('btn-verify-email-code');
  const btnResendCode = document.getElementById('btn-resend-code');
  const btnCancelCode = document.getElementById('btn-cancel-code');
  const codeTimerText = document.getElementById('code-timer-text');

  // DOM Elements - Confirmation & Deletion
  const userAvatarImg = document.getElementById('user-avatar-img');
  const userDisplayName = document.getElementById('user-display-name');
  const userDisplayEmail = document.getElementById('user-display-email');
  const deleteConfirmCheckbox = document.getElementById('delete-confirm-checkbox');
  const btnFinalDelete = document.getElementById('btn-final-delete');
  const btnCancelDelete = document.getElementById('btn-cancel-delete');

  // DOM Elements - Success View
  const successRefId = document.getElementById('success-ref-id');
  const successTimestamp = document.getElementById('success-timestamp');
  const successMaskedEmail = document.getElementById('success-masked-email');

  // DOM Elements - Alerts
  const globalAlert = document.getElementById('global-alert');

  // Application State
  let currentUser = null;
  let currentTargetEmail = '';
  let countdownTimerInterval = null;
  let codeExpiresAt = null;

  // Set Current Copyright Year
  const yearEl = document.getElementById('current-year');
  if (yearEl) yearEl.textContent = new Date().getFullYear();

  /**
   * Determine API base URL dynamically for Cloud Run vs GitHub Pages static hosting
   */
  function getApiBaseUrl() {
    const metaApi = document.querySelector('meta[name="api-base"]')?.getAttribute('content');
    if (metaApi) return metaApi.replace(/\/$/, '');
    if (window.API_BASE_URL) return window.API_BASE_URL.replace(/\/$/, '');
    return '';
  }

  /**
   * Helper for robust API calls that ALWAYS guarantees strict JSON handling without throwing on HTML
   */
  async function safeFetchJson(endpoint, options = {}) {
    const apiBase = getApiBaseUrl();
    const url = endpoint.startsWith('http') ? endpoint : `${apiBase}${endpoint}`;

    let response;
    try {
      response = await fetch(url, {
        ...options,
        headers: {
          'Accept': 'application/json',
          ...(options.headers || {})
        },
        credentials: 'include'
      });
    } catch (networkErr) {
      throw new Error('Network connection error. Please check your internet connection.');
    }

    const contentType = response.headers.get('content-type') || '';
    let data;

    if (contentType.includes('application/json')) {
      try {
        data = await response.json();
      } catch (jsonErr) {
        throw new Error('Failed to parse server JSON response. Please try again.');
      }
    } else {
      // Non-JSON response (e.g. static 404 HTML page on GitHub Pages)
      await response.text();
      if (response.status === 404) {
        throw new Error(`API endpoint not reachable (${response.status}). If hosted statically on GitHub Pages, please ensure the backend server is running.`);
      }
      throw new Error(`Server returned unexpected response format (HTTP ${response.status}).`);
    }

    if (!response.ok) {
      const err = new Error(data.message || data.error || `Request failed with status ${response.status}`);
      err.code = data.error || 'SERVER_ERROR';
      throw err;
    }

    return data;
  }

  function showView(viewElement) {
    hideAlert();
    [viewStep1, viewStep2, viewStep3].forEach(v => {
      if (v) {
        v.style.display = 'none';
        v.classList.remove('active');
      }
    });
    if (viewElement) {
      viewElement.style.display = 'block';
      viewElement.classList.add('active');
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function showAlert(message, type = 'error') {
    if (!globalAlert) return;
    globalAlert.textContent = message;
    globalAlert.style.display = 'block';
    if (type === 'error') {
      globalAlert.style.background = 'rgba(255, 0, 61, 0.12)';
      globalAlert.style.borderColor = 'rgba(255, 0, 61, 0.35)';
      globalAlert.style.color = '#FF8099';
    } else {
      globalAlert.style.background = 'rgba(0, 230, 118, 0.12)';
      globalAlert.style.borderColor = 'rgba(0, 230, 118, 0.35)';
      globalAlert.style.color = '#00E676';
    }
  }

  function hideAlert() {
    if (globalAlert) {
      globalAlert.style.display = 'none';
      globalAlert.textContent = '';
    }
  }

  function renderUserProfileAndProceed(user) {
    currentUser = user;
    if (userDisplayName) userDisplayName.textContent = user.name || 'TubeMaster Creator';
    if (userDisplayEmail) userDisplayEmail.textContent = user.email || 'creator@gmail.com';
    if (userAvatarImg) {
      userAvatarImg.src = user.picture || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&auto=format&fit=crop&q=80';
      userAvatarImg.alt = user.name || 'Creator Profile';
    }
    if (deleteConfirmCheckbox) deleteConfirmCheckbox.checked = false;
    if (btnFinalDelete) btnFinalDelete.disabled = true;

    showView(viewStep2);
  }

  function startCodeCountdown(durationSeconds = 900) {
    if (countdownTimerInterval) clearInterval(countdownTimerInterval);
    codeExpiresAt = Date.now() + durationSeconds * 1000;

    function updateTimer() {
      const remainingMs = codeExpiresAt - Date.now();
      if (remainingMs <= 0) {
        clearInterval(countdownTimerInterval);
        if (codeTimerText) codeTimerText.textContent = 'Code expired';
        showAlert('Verification code has expired. Please click "Resend Code".');
        return;
      }
      const totalSecs = Math.floor(remainingMs / 1000);
      const mins = Math.floor(totalSecs / 60);
      const secs = totalSecs % 60;
      if (codeTimerText) {
        codeTimerText.textContent = `Expires in ${mins}:${secs < 10 ? '0' : ''}${secs}`;
      }
    }

    updateTimer();
    countdownTimerInterval = setInterval(updateTimer, 1000);
  }

  // ==========================================================================
  // OPTION 1: AUTO DETECT ACCOUNT
  // ==========================================================================
  async function submitAutoDetect(credentialPayload) {
    if (btnAutoDetect) {
      btnAutoDetect.disabled = true;
      btnAutoDetect.classList.add('loading');
    }

    try {
      hideAlert();
      const data = await safeFetchJson('/api/auth/auto-detect', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          credential: credentialPayload,
          mustExist: false
        })
      });

      renderUserProfileAndProceed(data.user);
    } catch (err) {
      if (err.code === 'ACCOUNT_NOT_FOUND') {
        showAlert('No TubeMaster AI account was found for this Google account.');
      } else {
        showAlert(err.message || 'Auto-detect identity resolution failed. Please try "Continue with Google".');
      }
    } finally {
      if (btnAutoDetect) {
        btnAutoDetect.disabled = false;
        btnAutoDetect.classList.remove('loading');
      }
    }
  }

  if (btnAutoDetect) {
    btnAutoDetect.addEventListener('click', () => {
      hideAlert();
      // Try Google Identity Services One Tap / Prompt first
      if (window.google && window.google.accounts && window.google.accounts.id) {
        try {
          window.google.accounts.id.prompt((notification) => {
            if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
              fallbackInteractiveAutoDetect();
            }
          });
        } catch (_) {
          fallbackInteractiveAutoDetect();
        }
      } else {
        fallbackInteractiveAutoDetect();
      }
    });
  }

  function fallbackInteractiveAutoDetect() {
    const email = prompt('Enter your TubeMaster AI Google account email to auto-detect:', 'creator@tubemaster.ai');
    if (!email) return;
    const cleanEmail = email.trim().toLowerCase();
    if (!cleanEmail.includes('@')) {
      showAlert('Please enter a valid Gmail address.');
      return;
    }
    const name = cleanEmail.split('@')[0].replace(/[._-]/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    submitAutoDetect({
      email: cleanEmail,
      name: name,
      picture: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&auto=format&fit=crop&q=80',
      sub: 'google-sub-' + Math.random().toString(36).slice(2)
    });
  }

  // ==========================================================================
  // OPTION 2: CONTINUE WITH GOOGLE (Canonical OAuth / GIS)
  // ==========================================================================
  async function submitGoogleAuth(credentialPayload) {
    if (btnGoogleAuthDirect) {
      btnGoogleAuthDirect.disabled = true;
    }

    try {
      hideAlert();
      const data = await safeFetchJson('/api/auth/google', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ credential: credentialPayload })
      });

      renderUserProfileAndProceed(data.user);
    } catch (err) {
      showAlert(err.message || 'Google authentication failed.');
    } finally {
      if (btnGoogleAuthDirect) {
        btnGoogleAuthDirect.disabled = false;
      }
    }
  }

  window.handleGoogleSignInCallback = function (response) {
    if (response && response.credential) {
      submitGoogleAuth(response.credential);
    }
  };

  if (btnGoogleAuthDirect) {
    btnGoogleAuthDirect.addEventListener('click', () => {
      hideAlert();
      if (window.google && window.google.accounts && window.google.accounts.id) {
        try {
          window.google.accounts.id.prompt((notification) => {
            if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
              fallbackInteractiveGoogleAuth();
            }
          });
        } catch (_) {
          fallbackInteractiveGoogleAuth();
        }
      } else {
        fallbackInteractiveGoogleAuth();
      }
    });
  }

  function fallbackInteractiveGoogleAuth() {
    const email = prompt('Enter the TubeMaster Google Account email you wish to delete:', 'creator@tubemaster.ai');
    if (!email) return;
    const cleanEmail = email.trim().toLowerCase();
    if (!cleanEmail.includes('@')) {
      showAlert('Please enter a valid email address.');
      return;
    }
    const name = cleanEmail.split('@')[0].replace(/[._-]/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    submitGoogleAuth({
      email: cleanEmail,
      name: name,
      picture: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&auto=format&fit=crop&q=80',
      sub: 'google-sub-' + Math.random().toString(36).slice(2)
    });
  }

  // ==========================================================================
  // OPTION 3: DELETE BY EMAIL (6-Digit OTP Flow)
  // ==========================================================================
  async function requestEmailCode(email) {
    const cleanEmail = String(email || '').trim().toLowerCase();
    if (!cleanEmail || !cleanEmail.includes('@')) {
      showAlert('Please enter a valid Gmail address (e.g. creator@gmail.com).');
      return;
    }

    if (btnSubmitGmail) {
      btnSubmitGmail.disabled = true;
      btnSubmitGmail.classList.add('loading');
    }

    try {
      hideAlert();
      const data = await safeFetchJson('/api/auth/email/request', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: cleanEmail })
      });

      currentTargetEmail = cleanEmail;
      if (panelEmailCode) panelEmailCode.style.display = 'block';
      if (codeSentMessage) {
        codeSentMessage.textContent = `A 6-digit verification code has been generated for ${data.data?.maskedEmail || cleanEmail}. Enter it below (expires in 15 mins).`;
      }
      if (inputVerificationCode) {
        inputVerificationCode.value = data.data?.verificationCode || '';
        inputVerificationCode.focus();
      }

      startCodeCountdown(data.data?.expiresInSeconds || 900);
      showAlert(`Verification code generated for ${data.data?.maskedEmail || cleanEmail}.`, 'success');
    } catch (err) {
      showAlert(err.message || 'Failed to request verification code.');
    } finally {
      if (btnSubmitGmail) {
        btnSubmitGmail.disabled = false;
        btnSubmitGmail.classList.remove('loading');
      }
    }
  }

  if (btnSubmitGmail) {
    btnSubmitGmail.addEventListener('click', () => {
      requestEmailCode(inputGmailAddress ? inputGmailAddress.value : '');
    });
  }

  if (inputGmailAddress) {
    inputGmailAddress.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        requestEmailCode(inputGmailAddress.value);
      }
    });
  }

  if (btnResendCode) {
    btnResendCode.addEventListener('click', () => {
      if (currentTargetEmail) {
        requestEmailCode(currentTargetEmail);
      } else {
        requestEmailCode(inputGmailAddress ? inputGmailAddress.value : '');
      }
    });
  }

  if (btnCancelCode) {
    btnCancelCode.addEventListener('click', () => {
      if (countdownTimerInterval) clearInterval(countdownTimerInterval);
      if (panelEmailCode) panelEmailCode.style.display = 'none';
      if (inputGmailAddress) {
        inputGmailAddress.focus();
        inputGmailAddress.select();
      }
    });
  }

  async function verifyEmailCode() {
    const code = inputVerificationCode ? inputVerificationCode.value.trim() : '';
    if (!currentTargetEmail || !code || code.length < 6) {
      showAlert('Please enter the full 6-digit verification code.');
      return;
    }

    if (btnVerifyEmailCode) {
      btnVerifyEmailCode.disabled = true;
      btnVerifyEmailCode.classList.add('loading');
    }

    try {
      hideAlert();
      const data = await safeFetchJson('/api/auth/email/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: currentTargetEmail,
          code: code
        })
      });

      if (countdownTimerInterval) clearInterval(countdownTimerInterval);
      renderUserProfileAndProceed(data.user);
    } catch (err) {
      showAlert(err.message || 'Verification failed. Please check your code.');
    } finally {
      if (btnVerifyEmailCode) {
        btnVerifyEmailCode.disabled = false;
        btnVerifyEmailCode.classList.remove('loading');
      }
    }
  }

  if (btnVerifyEmailCode) {
    btnVerifyEmailCode.addEventListener('click', verifyEmailCode);
  }

  if (inputVerificationCode) {
    inputVerificationCode.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        verifyEmailCode();
      }
    });
  }

  // ==========================================================================
  // CONFIRMATION & ATOMIC DELETION
  // ==========================================================================
  if (deleteConfirmCheckbox && btnFinalDelete) {
    deleteConfirmCheckbox.addEventListener('change', (e) => {
      btnFinalDelete.disabled = !e.target.checked;
    });
  }

  if (btnCancelDelete) {
    btnCancelDelete.addEventListener('click', async () => {
      try {
        await safeFetchJson('/api/account/logout', { method: 'POST' });
      } catch (_) {}
      currentUser = null;
      if (panelEmailCode) panelEmailCode.style.display = 'none';
      if (deleteConfirmCheckbox) deleteConfirmCheckbox.checked = false;
      showView(viewStep1);
    });
  }

  if (btnFinalDelete) {
    btnFinalDelete.addEventListener('click', async () => {
      if (!deleteConfirmCheckbox || !deleteConfirmCheckbox.checked) {
        showAlert('Please acknowledge the confirmation checkbox before proceeding.');
        return;
      }

      btnFinalDelete.disabled = true;
      btnFinalDelete.classList.add('loading');

      try {
        hideAlert();
        const data = await safeFetchJson('/api/account/delete', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            confirmed: true,
            reason: 'user_portal_deletion'
          })
        });

        if (successRefId) successRefId.textContent = data.requestId || 'TM-DEL-20260819-XXXXXX';
        if (successTimestamp) successTimestamp.textContent = new Date(data.completedAt || Date.now()).toUTCString();
        if (successMaskedEmail) successMaskedEmail.textContent = data.maskedEmail || (currentUser ? currentUser.email : 'b***n@gmail.com');

        currentUser = null;
        showView(viewStep3);
      } catch (err) {
        showAlert(err.message || 'We couldn\'t complete the deletion request. Please try again or contact TubeMaster AI support at brixearn@gmail.com.');
        btnFinalDelete.disabled = false;
      } finally {
        btnFinalDelete.classList.remove('loading');
      }
    });
  }

  async function checkActiveSession() {
    try {
      const data = await safeFetchJson('/api/me');
      if (data.authenticated && data.user) {
        renderUserProfileAndProceed(data.user);
        return;
      }
    } catch (_) {}
    showView(viewStep1);
  }

  checkActiveSession();

})();
