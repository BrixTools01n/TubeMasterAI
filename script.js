/**
 * TubeMaster AI - Connected Account Deletion Web Portal Script
 * Handles Google OAuth Identity Verification, Session Management, Account Selection,
 * and Atomic Account Deletion.
 */

(function () {
  'use strict';

  // DOM Elements
  const viewStep1 = document.getElementById('view-step-1');
  const viewStep2 = document.getElementById('view-step-2');
  const viewStep3 = document.getElementById('view-step-3');
  const viewStep4 = document.getElementById('view-step-4');
  const viewStep5 = document.getElementById('view-step-5');

  const btnStartRemoval = document.getElementById('btn-start-removal');
  const btnBackStep1 = document.getElementById('btn-back-step-1');
  const btnGoogleAuthDirect = document.getElementById('btn-google-auth-direct');
  const btnContinueToDelete = document.getElementById('btn-continue-to-delete');
  const btnLogout = document.getElementById('btn-logout');
  const btnCancelDelete = document.getElementById('btn-cancel-delete');
  const btnFinalDelete = document.getElementById('btn-final-delete');
  const deleteConfirmCheckbox = document.getElementById('delete-confirm-checkbox');

  const userAvatarImg = document.getElementById('user-avatar-img');
  const userDisplayName = document.getElementById('user-display-name');
  const userDisplayEmail = document.getElementById('user-display-email');

  const successRefId = document.getElementById('success-ref-id');
  const successTimestamp = document.getElementById('success-timestamp');
  const successMaskedEmail = document.getElementById('success-masked-email');
  const globalAlert = document.getElementById('global-alert');

  // Application State
  let currentUser = null;

  // Set Current Copyright Year
  const yearEl = document.getElementById('current-year');
  if (yearEl) yearEl.textContent = new Date().getFullYear();

  function showView(viewElement) {
    hideAlert();
    [viewStep1, viewStep2, viewStep3, viewStep4, viewStep5].forEach(v => {
      if (v) {
        v.style.display = 'none';
        v.classList.remove('active');
      }
    });
    if (viewElement) {
      viewElement.style.display = 'block';
      viewElement.classList.add('active');
    }
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

  function renderUserProfile(user) {
    currentUser = user;
    if (userDisplayName) userDisplayName.textContent = user.name || 'TubeMaster Creator';
    if (userDisplayEmail) userDisplayEmail.textContent = user.email || 'creator@example.com';
    if (userAvatarImg) {
      userAvatarImg.src = user.picture || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&auto=format&fit=crop&q=80';
      userAvatarImg.alt = user.name || 'Google Profile';
    }
  }

  async function submitGoogleAuth(credentialPayload) {
    try {
      hideAlert();
      const response = await fetch('/api/auth/google', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ credential: credentialPayload })
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        throw new Error(data.message || 'Google identity verification failed.');
      }

      renderUserProfile(data.user);
      showView(viewStep3);
    } catch (err) {
      showAlert(err.message || 'Authentication failed. Please try again.');
    }
  }

  window.handleGoogleSignInCallback = function (response) {
    if (response && response.credential) {
      submitGoogleAuth(response.credential);
    }
  };

  async function checkActiveSession() {
    try {
      const response = await fetch('/api/me');
      if (response.ok) {
        const data = await response.json();
        if (data.authenticated && data.user) {
          renderUserProfile(data.user);
          showView(viewStep3);
          return;
        }
      }
    } catch (_) {}
    showView(viewStep1);
  }

  if (btnStartRemoval) {
    btnStartRemoval.addEventListener('click', () => {
      showView(viewStep2);
    });
  }

  if (btnBackStep1) {
    btnBackStep1.addEventListener('click', () => {
      showView(viewStep1);
    });
  }

  if (btnGoogleAuthDirect) {
    btnGoogleAuthDirect.addEventListener('click', async () => {
      if (window.google && window.google.accounts && window.google.accounts.id) {
        window.google.accounts.id.prompt((notification) => {
          if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
            fallbackInteractiveGoogleAuth();
          }
        });
      } else {
        fallbackInteractiveGoogleAuth();
      }
    });
  }

  function fallbackInteractiveGoogleAuth() {
    const email = prompt('Enter your Google Account email used with TubeMaster AI:', 'creator@tubemaster.ai');
    if (!email) return;
    const cleanEmail = email.trim();
    if (!cleanEmail.includes('@')) {
      alert('Please enter a valid email address.');
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

  if (btnContinueToDelete) {
    btnContinueToDelete.addEventListener('click', () => {
      if (!currentUser) {
        showView(viewStep2);
        return;
      }
      if (deleteConfirmCheckbox) deleteConfirmCheckbox.checked = false;
      if (btnFinalDelete) btnFinalDelete.disabled = true;
      showView(viewStep4);
    });
  }

  if (btnLogout) {
    btnLogout.addEventListener('click', async () => {
      try {
        await fetch('/api/account/logout', { method: 'POST' });
      } catch (_) {}
      currentUser = null;
      showView(viewStep1);
    });
  }

  if (btnCancelDelete) {
    btnCancelDelete.addEventListener('click', () => {
      showView(viewStep3);
    });
  }

  if (deleteConfirmCheckbox && btnFinalDelete) {
    deleteConfirmCheckbox.addEventListener('change', (e) => {
      btnFinalDelete.disabled = !e.target.checked;
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
        const response = await fetch('/api/account/delete', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            confirmed: true,
            reason: 'user_portal_deletion'
          })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
          throw new Error(data.message || 'Account deletion failed.');
        }

        if (successRefId) successRefId.textContent = data.requestId || 'TM-DEL-20260819-SUCCESS';
        if (successTimestamp) successTimestamp.textContent = new Date(data.completedAt || Date.now()).toUTCString();
        if (successMaskedEmail) successMaskedEmail.textContent = data.maskedEmail || (currentUser ? currentUser.email : 'c***r@example.com');

        currentUser = null;
        showView(viewStep5);
      } catch (err) {
        showAlert(err.message || 'An error occurred during account deletion. Please try again.');
        btnFinalDelete.disabled = false;
      } finally {
        btnFinalDelete.classList.remove('loading');
      }
    });
  }

  checkActiveSession();

})();
