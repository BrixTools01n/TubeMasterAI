/**
 * TubeMaster AI - Account Deletion Portal Client Script
 * Manages dynamic multi-step ownership verification and deletion receipts.
 */

document.addEventListener('DOMContentLoaded', async () => {
  // Elements - Step 1
  const formStep1 = document.getElementById('deletion-form-step1');
  const emailInput = document.getElementById('account-email');
  const reasonInput = document.getElementById('deletion-reason');
  const confirmCheckbox = document.getElementById('confirm-checkbox');
  const submitStep1Btn = document.getElementById('submit-step1-btn');
  const emailError = document.getElementById('email-error');
  const confirmError = document.getElementById('confirm-error');

  // Elements - Step 2
  const formStep2 = document.getElementById('deletion-form-step2');
  const codeInput = document.getElementById('verification-code');
  const submitStep2Btn = document.getElementById('submit-step2-btn');
  const cancelVerifyBtn = document.getElementById('cancel-verify-btn');
  const codeError = document.getElementById('code-error');
  const verifyDisplayEmail = document.getElementById('verify-display-email');
  const verifyDisplayReqId = document.getElementById('verify-display-reqid');

  // Common UI Elements
  const statusContainer = document.getElementById('status-container');
  const formCardTitle = document.getElementById('form-card-title');
  const formCardDesc = document.getElementById('form-card-desc');
  const stepPill1 = document.getElementById('step-pill-1');
  const stepPill2 = document.getElementById('step-pill-2');
  const stepPill3 = document.getElementById('step-pill-3');

  // Links & Support
  const navPrivacyLink = document.getElementById('nav-privacy-link');
  const navSupportLink = document.getElementById('nav-support-link');
  const footerPrivacyLink = document.getElementById('footer-privacy-link');
  const footerSupportLink = document.getElementById('footer-support-link');
  const supportEmailBtn = document.getElementById('support-email-btn');
  const supportEmailText = document.getElementById('support-email-text');
  const currentYearSpan = document.getElementById('current-year');

  if (currentYearSpan) {
    currentYearSpan.textContent = new Date().getFullYear();
  }

  // Active Session State
  let activeRequestId = null;
  let activeEmail = null;
  let activeReason = null;

  // 1. Fetch Safe Public Configuration
  try {
    const res = await fetch('/api/config');
    if (res.ok) {
      const config = await res.json();
      if (config.supportEmail) {
        const mailtoHref = `mailto:${encodeURIComponent(config.supportEmail)}?subject=${encodeURIComponent('TubeMaster AI - Account Support / Data Inquiry')}`;
        if (navSupportLink) navSupportLink.href = mailtoHref;
        if (footerSupportLink) footerSupportLink.href = mailtoHref;
        if (supportEmailBtn) supportEmailBtn.href = mailtoHref;
        if (supportEmailText) supportEmailText.textContent = config.supportEmail;
      }
      if (config.privacyPolicyUrl) {
        if (navPrivacyLink) navPrivacyLink.href = config.privacyPolicyUrl;
        if (footerPrivacyLink) footerPrivacyLink.href = config.privacyPolicyUrl;
      }
    }
  } catch (err) {
    console.warn('Could not fetch server config:', err);
  }

  // Helpers
  function validateEmail(email) {
    const regex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$/;
    return regex.test(email.trim().toLowerCase()) && email.length <= 254;
  }

  function clearErrors() {
    if (emailError) emailError.classList.remove('visible');
    if (confirmError) confirmError.classList.remove('visible');
    if (codeError) codeError.classList.remove('visible');
    if (emailInput) emailInput.classList.remove('is-invalid');
    if (confirmCheckbox) confirmCheckbox.classList.remove('is-invalid');
    if (codeInput) codeInput.classList.remove('is-invalid');
  }

  function showStatus(type, title, message) {
    if (!statusContainer) return;
    statusContainer.className = `status-container status-${type}`;
    statusContainer.innerHTML = `
      <div class="status-title">${title}</div>
      <div>${message}</div>
    `;
    statusContainer.style.display = 'block';
  }

  function hideStatus() {
    if (!statusContainer) return;
    statusContainer.style.display = 'none';
    statusContainer.innerHTML = '';
  }

  // ==========================================================================
  // STAGE 1: SUBMIT DELETION REQUEST & SEND VERIFICATION CODE
  // ==========================================================================
  if (formStep1) {
    formStep1.addEventListener('submit', async (e) => {
      e.preventDefault();
      clearErrors();
      hideStatus();

      const email = emailInput.value.trim();
      const reason = reasonInput ? reasonInput.value : '';
      const confirmed = confirmCheckbox.checked;

      let hasError = false;

      if (!validateEmail(email)) {
        if (emailError) emailError.classList.add('visible');
        emailInput.classList.add('is-invalid');
        hasError = true;
      }

      if (!confirmed) {
        if (confirmError) confirmError.classList.add('visible');
        confirmCheckbox.classList.add('is-invalid');
        hasError = true;
      }

      if (hasError) return;

      // Submit request to Stage 1 API
      submitStep1Btn.disabled = true;
      submitStep1Btn.classList.add('loading');

      try {
        const response = await fetch('/api/account-deletion/request', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, reason, confirmed: true })
        });

        const data = await response.json();

        if (response.ok && data.success) {
          activeRequestId = data.requestId;
          activeEmail = email;
          activeReason = reason;

          // Transition to Step 2
          formStep1.style.display = 'none';
          formStep2.style.display = 'flex';

          if (stepPill1) {
            stepPill1.classList.remove('active');
            stepPill1.classList.add('completed');
          }
          if (stepPill2) {
            stepPill2.classList.add('active');
          }

          if (formCardTitle) formCardTitle.textContent = 'Verify Account Ownership';
          if (formCardDesc) formCardDesc.textContent = 'Enter the 6-digit verification code to confirm ownership and authorize deletion.';

          if (verifyDisplayEmail) verifyDisplayEmail.textContent = data.maskedEmail;
          if (verifyDisplayReqId) verifyDisplayReqId.textContent = data.requestId;

          showStatus(
            'warning',
            'Verification Code Dispatched',
            `A 6-digit code has been dispatched for <strong>${data.maskedEmail}</strong>. Please check your inbox and enter the code below.`
          );

          if (codeInput) {
            codeInput.focus();
          }
        } else {
          showStatus(
            'error',
            'Request Error',
            data.message || 'Unable to initiate account deletion. Please verify your details.'
          );
        }
      } catch (err) {
        showStatus(
          'error',
          'Network Connection Error',
          'Could not communicate with the TubeMaster AI deletion service. Please check your network connection.'
        );
      } finally {
        submitStep1Btn.disabled = false;
        submitStep1Btn.classList.remove('loading');
      }
    });
  }

  // ==========================================================================
  // STAGE 2: VERIFY CODE & EXECUTE PERMANENT DELETION
  // ==========================================================================
  if (formStep2) {
    formStep2.addEventListener('submit', async (e) => {
      e.preventDefault();
      clearErrors();
      hideStatus();

      const code = codeInput.value.trim();

      if (!code || code.length < 6) {
        if (codeError) codeError.classList.add('visible');
        codeInput.classList.add('is-invalid');
        return;
      }

      submitStep2Btn.disabled = true;
      submitStep2Btn.classList.add('loading');

      try {
        const response = await fetch('/api/account-deletion/verify', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            requestId: activeRequestId,
            code,
            confirmed: true
          })
        });

        const data = await response.json();

        if (response.ok && data.success && data.status === 'DELETION_COMPLETED') {
          // Complete Deletion Success
          formStep2.style.display = 'none';

          if (stepPill2) {
            stepPill2.classList.remove('active');
            stepPill2.classList.add('completed');
          }
          if (stepPill3) {
            stepPill3.classList.add('active');
            stepPill3.classList.add('completed');
          }

          if (formCardTitle) formCardTitle.textContent = 'Account & Data Deleted';
          if (formCardDesc) formCardDesc.textContent = 'Your TubeMaster AI account has been permanently removed.';

          statusContainer.className = 'status-container status-success';
          statusContainer.innerHTML = `
            <div class="status-title">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
              TubeMaster AI Account Deleted
            </div>
            <div class="receipt-card" style="margin-top: 1rem;">
              <p>Your TubeMaster AI account (<strong>${data.maskedEmail}</strong>) and all associated personal data have been permanently deleted.</p>
              
              <div class="verify-info-box">
                <div class="verify-info-row">
                  <span class="verify-info-label">Audit Receipt:</span>
                  <span class="verify-info-val audit-id-badge">${data.requestId}</span>
                </div>
                <div class="verify-info-row">
                  <span class="verify-info-label">Completed Timestamp:</span>
                  <span class="verify-info-val">${new Date().toUTCString()}</span>
                </div>
                <div class="verify-info-row">
                  <span class="verify-info-label">Final Status:</span>
                  <span class="verify-info-val" style="color: var(--status-success-text);">PERMANENTLY_DELETED</span>
                </div>
              </div>

              <div class="receipt-item">
                <div class="receipt-icon">✓</div>
                <div>
                  <div class="receipt-title">User Account & Profile Purged</div>
                  <div class="receipt-desc">Authentication credentials, email mappings, and active session tokens revoked.</div>
                </div>
              </div>

              <div class="receipt-item">
                <div class="receipt-icon">✓</div>
                <div>
                  <div class="receipt-title">AI History & Drafts Deleted</div>
                  <div class="receipt-desc">Generated video scripts, titles, tags, and saved tool outputs permanently removed.</div>
                </div>
              </div>

              <div class="receipt-item">
                <div class="receipt-icon">✓</div>
                <div>
                  <div class="receipt-title">Preferences & Quotas Reset</div>
                  <div class="receipt-desc">Usage quotas and custom workspace settings cleared.</div>
                </div>
              </div>
            </div>
          `;
          statusContainer.style.display = 'block';

        } else {
          showStatus(
            'error',
            'Verification Failed',
            data.message || 'Invalid verification code or expired request. Please try again.'
          );
        }
      } catch (err) {
        showStatus(
          'error',
          'Verification Error',
          'Unable to complete deletion request. Please check your internet connection.'
        );
      } finally {
        submitStep2Btn.disabled = false;
        submitStep2Btn.classList.remove('loading');
      }
    });
  }

  // Cancel & Return to Step 1
  if (cancelVerifyBtn) {
    cancelVerifyBtn.addEventListener('click', () => {
      hideStatus();
      clearErrors();
      formStep2.style.display = 'none';
      formStep1.style.display = 'flex';

      if (stepPill1) {
        stepPill1.classList.add('active');
        stepPill1.classList.remove('completed');
      }
      if (stepPill2) {
        stepPill2.classList.remove('active');
        stepPill2.classList.remove('completed');
      }

      if (formCardTitle) formCardTitle.textContent = 'Submit Deletion Request';
      if (formCardDesc) formCardDesc.textContent = 'Enter the email address registered with your TubeMaster AI account to initiate the deletion workflow.';
    });
  }
});
