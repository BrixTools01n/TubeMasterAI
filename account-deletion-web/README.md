# TubeMaster AI — Google Play Account Deletion Web Service

This repository contains the standalone, production-ready web service for **TubeMaster AI** to satisfy the mandatory **Google Play Store Data Safety & Account Deletion Policy**.

It provides a public external deletion request portal and a dedicated Privacy Policy accessible from any browser without requiring user login or Android app installation.

---

## 📁 Web Service Structure

```text
account-deletion-web/
├── server.js        # Production Node.js HTTP server (rate limiting, security headers, ownership verification, deletion API)
├── package.json     # Node.js project manifest with start script
├── Dockerfile       # Production container specification for Google Cloud Run
├── .dockerignore    # Docker build exclusion rules
├── .env.example     # Environment variable placeholder template
├── index.html       # Public Account Deletion Portal for TubeMaster AI (Step 1 Request & Step 2 Verify)
├── privacy.html     # Dedicated Privacy Policy page for TubeMaster AI
├── style.css        # Responsive dark creator theme stylesheet (WCAG AAA contrast, 48px touch targets)
├── script.js        # Dynamic configuration client & multi-step verification handler
└── README.md        # Deployment instructions & Google Play submission guide
```

---

## ⚙️ Environment Variables & Configuration

The service uses the following environment variables (passable via Cloud Run or `.env`):

| Variable | Placeholder / Default | Description |
| :--- | :--- | :--- |
| `PORT` | `8080` | Port assigned by Google Cloud Run |
| `SUPPORT_EMAIL` | `hloob07@gmail.com` | Official support & privacy email address |
| `PRIVACY_POLICY_URL` | `/privacy` | URL for the Privacy Policy (internal `/privacy` or external link) |
| `TOKEN_SECRET` | Auto-generated random | Secret used for HMAC-SHA256 verification code hashing |

---

## 🚀 Browser-Based Google Cloud Run Deployment (Zero Terminal / Zero CLI)

Deploy the service entirely through your web browser using GitHub and Google Cloud Console:

1. **Push to GitHub from Google AI Studio:**
   - In Google AI Studio, click the **Export** icon (top right) $\rightarrow$ **Push to GitHub**.
   - Authenticate with your GitHub account and create the repository (e.g. `TubeMasterAI`).

2. **Open Google Cloud Run in Your Browser:**
   - Navigate to: **[https://console.cloud.google.com/run](https://console.cloud.google.com/run)**
   - Ensure your active Google Cloud project is selected in the top bar.

3. **Create New Cloud Run Service:**
   - Click **Create Service** (or **+ Deploy Container**).
   - Select **"Continuously deploy from a repository"**.
   - Click **Set up with Cloud Build / Developer Connect**.

4. **Select Repository & Build Context:**
   - Provider: **GitHub**
   - Repository: Select your exported `TubeMasterAI` repository.
   - Branch: `^main$` (or `master`)
   - Build Type: **Dockerfile**
   - **Source location / Dockerfile path:** Select or enter `account-deletion-web/Dockerfile` (or set Build Context to `/account-deletion-web`).

5. **Configure Service Settings:**
   - **Service name:** `tubemaster-account-deletion`
   - **Region:** `us-central1` (or your preferred region)
   - **Authentication:** Check **"Allow unauthenticated invocations"** (public access for Google Play reviewers and users).

6. **Add Environment Variables (Optional):**
   - Under **Container, Volumes, Networking, Security** $\rightarrow$ **Variables & Secrets**:
     - `SUPPORT_EMAIL` = `hloob07@gmail.com`
     - `PRIVACY_POLICY_URL` = `/privacy`

7. **Deploy:**
   - Click **Create / Deploy**.
   - Wait 1–2 minutes for the automated Cloud Build container compilation.
   - Once completed, Cloud Run displays the verified public service URL (e.g., `https://tubemaster-account-deletion-xxxxxx-uc.a.run.app`).

---

## 📱 Google Play Console Submission URLs

In the **Google Play Console** $\rightarrow$ **Policy and programs** $\rightarrow$ **App content**:

1. **Account Deletion URL** (Under *Data safety* $\rightarrow$ *Account deletion*):
   ```text
   https://<YOUR_DEPLOYED_CLOUD_RUN_URL>/
   ```

2. **Privacy Policy URL** (Under *Privacy policy*):
   ```text
   https://<YOUR_DEPLOYED_CLOUD_RUN_URL>/privacy
   ```

---

## 🛡️ Deletion Architecture & Lifecycle

The deletion processing pipeline follows a 3-step verified lifecycle:

```text
1. Stage 1: Deletion request received & validated (/api/account-deletion/request)
        ↓
2. Stage 2: Cryptographically verified ownership code (/api/account-deletion/verify)
        ↓
3. Stage 3: Atomic deletion executed & completion receipt issued (DELETION_COMPLETED)
```

The system requires ownership verification before deletion, prevents unauthorized abuse via HMAC-SHA256 token hashing, single-use invalidation, and rate-limiting, and returns an honest `DELETION_COMPLETED` receipt only after full verification.
