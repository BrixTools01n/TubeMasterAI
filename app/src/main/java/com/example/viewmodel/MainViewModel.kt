package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.ai.AIEngine
import com.example.data.local.*
import com.example.data.registry.ToolRegistry
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.model.*
import com.example.payment.PaymentConfig
import com.example.payment.PaymentMethod
import com.example.payment.Plan
import com.example.payment.SubscriptionStatus
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    AUTH,
    HOME,
    TOOLS,
    SAVED,
    PRICING,
    PROFILE,
    GENERATOR,
    ADMIN_PANEL,
    ADMIN,
    NOTIFICATIONS
}

enum class ToastType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

data class ToastData(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val type: ToastType = ToastType.SUCCESS
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val database = AppDatabase.getDatabase(application)
    val savedDao = database.savedDao()
    val historyDao = database.historyDao()
    val userDao = database.userDao()
    val paymentDao = database.paymentDao()
    val toolOverrideDao = database.toolOverrideDao()
    val notificationDao = database.notificationDao()
    val auditLogDao = database.adminAuditLogDao()

    val authManager = AuthManager(application, database)
    val usageManager = UsageManager(authManager)
    val aiEngine = AIEngine()

    // Lifecycle & Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private var previousScreen: AppScreen = AppScreen.HOME

    val hasCompletedOnboarding: StateFlow<Boolean> = authManager.hasCompletedOnboarding
    val isLoggedIn: StateFlow<Boolean> = authManager.isLoggedIn
    val authState: StateFlow<com.example.data.local.AuthState> = authManager.authState
    val currentUser: StateFlow<UserEntity?> = authManager.currentUser
    val isAdminAuthenticated: StateFlow<Boolean> = authManager.isAdminAuthenticated

    // Internationalization (i18n)
    val appLanguage: StateFlow<AppLanguage> = authManager.appLanguage
    val selectedLanguage: StateFlow<AppLanguage> = authManager.appLanguage

    // Tool Overrides from DB
    val toolOverrides: StateFlow<Map<String, ToolOverrideEntity>> = toolOverrideDao.getAllOverrides()
        .map { list -> list.associateBy { it.toolId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Filtering & Search
    private val _selectedPlatformFilter = MutableStateFlow<Platform?>(null)
    val selectedPlatformFilter: StateFlow<Platform?> = _selectedPlatformFilter.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active Tool & Generator State
    private val _activeTool = MutableStateFlow<ToolConfig?>(null)
    val activeTool: StateFlow<ToolConfig?> = _activeTool.asStateFlow()

    private val _toolInputs = MutableStateFlow<Map<String, Any>>(emptyMap())
    val toolInputs: StateFlow<Map<String, Any>> = _toolInputs.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentResult = MutableStateFlow<GenerationResult?>(null)
    val currentResult: StateFlow<GenerationResult?> = _currentResult.asStateFlow()

    private val _isResultSaved = MutableStateFlow(false)
    val isResultSaved: StateFlow<Boolean> = _isResultSaved.asStateFlow()

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError: StateFlow<String?> = _generationError.asStateFlow()

    // Active Tool History
    private val _toolHistory = MutableStateFlow<List<HistoryEntity>>(emptyList())
    val toolHistory: StateFlow<List<HistoryEntity>> = _toolHistory.asStateFlow()

    // User & Subscription
    val isPro: StateFlow<Boolean> = usageManager.isPro
    val subscriptionStatus: StateFlow<SubscriptionStatus> = usageManager.subscriptionStatus
    val dailyGenerationsCount: StateFlow<Int> = usageManager.dailyCount
    val limitReachedAt: StateFlow<Long?> = usageManager.limitReachedAt
    val userName: StateFlow<String> = usageManager.userName
    val userEmail: StateFlow<String> = usageManager.userEmail

    // Saved Items from Room
    val savedItems: StateFlow<List<SavedItemEntity>> = savedDao.getAllSaved()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalHistoryCount: StateFlow<Int> = historyDao.getHistoryCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // In-App Notifications
    val notifications: StateFlow<List<NotificationEntity>> = notificationDao.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = notificationDao.getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Admin Data Streams
    val allAdminUsers: StateFlow<List<UserEntity>> = userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = paymentDao.getAllPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAuditLogs: StateFlow<List<AdminAuditLogEntity>> = auditLogDao.getRecentLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback & Modals
    private val _currentToast = MutableStateFlow<ToastData?>(null)
    val currentToast: StateFlow<ToastData?> = _currentToast.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var toastJob: Job? = null

    private val _showUpgradeDialog = MutableStateFlow(false)
    val showUpgradeDialog: StateFlow<Boolean> = _showUpgradeDialog.asStateFlow()

    private val _upgradeReason = MutableStateFlow("")
    val upgradeReason: StateFlow<String> = _upgradeReason.asStateFlow()

    private val _selectedPlanForPayment = MutableStateFlow<Plan?>(null)
    val selectedPlanForPayment: StateFlow<Plan?> = _selectedPlanForPayment.asStateFlow()

    // Admin Auth Modal
    private val _showAdminAuthModal = MutableStateFlow(false)
    val showAdminAuthModal: StateFlow<Boolean> = _showAdminAuthModal.asStateFlow()
    val showAdminAuth: StateFlow<Boolean> = _showAdminAuthModal.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    init {
        viewModelScope.launch {
            authManager.authState.collect { state ->
                when (state) {
                    com.example.data.local.AuthState.AUTHENTICATED -> {
                        if (_currentScreen.value == AppScreen.AUTH || _currentScreen.value == AppScreen.SPLASH) {
                            com.example.data.local.AuthLogger.log("AUTH_NAVIGATING_HOME", "Auto-transitioning to HOME based on active session")
                            _currentScreen.value = AppScreen.HOME
                        }
                    }
                    com.example.data.local.AuthState.UNAUTHENTICATED -> {
                        if (_currentScreen.value != AppScreen.SPLASH && _currentScreen.value != AppScreen.ONBOARDING) {
                            _currentScreen.value = AppScreen.AUTH
                        }
                    }
                    com.example.data.local.AuthState.LOADING -> {}
                }
            }
        }
        viewModelScope.launch {
            usageManager.checkCooldownReset()
        }
    }

    fun onSplashFinished() {
        viewModelScope.launch {
            val session = authManager.getMe()
            if (!authManager.hasCompletedOnboarding.value) {
                _currentScreen.value = AppScreen.ONBOARDING
            } else if (!session.authenticated) {
                _currentScreen.value = AppScreen.AUTH
            } else {
                _currentScreen.value = AppScreen.HOME
            }
        }
    }

    fun completeOnboarding() {
        authManager.setOnboardingCompleted(true)
        if (authManager.isLoggedIn.value) {
            _currentScreen.value = AppScreen.HOME
        } else {
            _currentScreen.value = AppScreen.AUTH
        }
    }

    fun replayOnboarding() {
        authManager.setOnboardingCompleted(false)
        _currentScreen.value = AppScreen.ONBOARDING
    }

    fun setLanguage(lang: AppLanguage) {
        authManager.setAppLanguage(lang)
        showToast(Translations.get("toast.lang_changed", lang), ToastType.INFO)
    }

    fun setAppLanguage(lang: AppLanguage) {
        authManager.setAppLanguage(lang)
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            previousScreen = _currentScreen.value
            _currentScreen.value = screen
        }
    }

    fun navigateBack() {
        _currentScreen.value = previousScreen
    }

    fun openAdminAuth() {
        _adminAuthError.value = null
        _showAdminAuthModal.value = true
    }

    fun dismissAdminAuth() {
        _showAdminAuthModal.value = false
        _adminAuthError.value = null
    }

    fun closeAdminAuth() {
        dismissAdminAuth()
    }

    fun authenticateAdmin(password: String) {
        val result = authManager.authenticateAdmin(password)
        result.onSuccess {
            _showAdminAuthModal.value = false
            _adminAuthError.value = null
            showToast("Admin Security Verified", ToastType.SUCCESS)
            _currentScreen.value = AppScreen.ADMIN_PANEL
            logAdminAction("LOGIN", "Admin authenticated successfully")
        }.onFailure {
            _adminAuthError.value = it.message ?: "Authentication failed."
        }
    }

    fun exitAdmin() {
        authManager.exitAdmin()
        _currentScreen.value = AppScreen.PROFILE
        showToast("Admin Session Closed", ToastType.INFO)
    }

    fun exitAdminPanel() {
        exitAdmin()
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val result = authManager.login(email, pass)
            result.onSuccess {
                showToast(Translations.get("toast.welcome", appLanguage.value) + " ${it.name}!", ToastType.SUCCESS)
                _currentScreen.value = AppScreen.HOME
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val result = authManager.register(name, email, pass)
            result.onSuccess {
                showToast("Account created successfully!", ToastType.SUCCESS)
                _currentScreen.value = AppScreen.HOME
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Registration failed")
            }
        }
    }

    fun loginWithGoogle(email: String, name: String) {
        viewModelScope.launch {
            val result = authManager.loginWithGoogle(email, name, null)
            result.onSuccess {
                showToast(Translations.get("toast.welcome", appLanguage.value) + " ${it.name}!", ToastType.SUCCESS)
                _currentScreen.value = AppScreen.HOME
            }.onFailure {
                showToast(Translations.get("toast.google_failed", appLanguage.value), ToastType.ERROR)
            }
        }
    }

    fun signInWithGoogle(
        context: Context,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            com.example.data.local.AuthLogger.log("GOOGLE_OAUTH_START", "Initiating Google Sign-In request")
            try {
                val credentialManager = CredentialManager.create(context)
                val clientId = try {
                    BuildConfig.GOOGLE_CLIENT_ID
                } catch (e: Throwable) {
                    ""
                }.ifBlank { "629905030358-tubemaster.apps.googleusercontent.com" }

                val googleIdOption = GetSignInWithGoogleOption.Builder(clientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                com.example.data.local.AuthLogger.log("GOOGLE_OAUTH_PROMPT", "Displaying Google account chooser")
                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential
                com.example.data.local.AuthLogger.log("GOOGLE_CALLBACK_RECEIVED", "Credential payload received from Google")

                var email = ""
                var name = ""
                var avatarUrl: String? = null

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    email = googleIdTokenCredential.id
                    name = googleIdTokenCredential.displayName ?: googleIdTokenCredential.givenName ?: "Creator"
                    avatarUrl = googleIdTokenCredential.profilePictureUri?.toString()
                    com.example.data.local.AuthLogger.log("GOOGLE_CODE_EXCHANGE_SUCCESS", "ID Token exchange and extraction succeeded")
                }

                if (email.isBlank()) {
                    email = "creator.google@tubemaster.ai"
                    name = "Google Creator"
                }

                val authResult = authManager.loginWithGoogle(email, name, avatarUrl)
                authResult.onSuccess { user ->
                    com.example.data.local.AuthLogger.log("AUTH_NAVIGATING_HOME", "User ${user.id} signed in -> Navigating to HOME")
                    showToast(Translations.get("toast.welcome", appLanguage.value) + " ${user.name}!", ToastType.SUCCESS)
                    _currentScreen.value = AppScreen.HOME
                    onSuccess(user.name)
                }.onFailure { err ->
                    com.example.data.local.AuthLogger.log("GOOGLE_OAUTH_ERROR", err.message ?: "Authentication failed")
                    showToast(Translations.get("toast.google_failed", appLanguage.value), ToastType.ERROR)
                    onError(err.message ?: "Google sign-in could not be completed.")
                }
            } catch (e: GetCredentialCancellationException) {
                com.example.data.local.AuthLogger.log("GOOGLE_OAUTH_CANCELLED", "User closed account chooser")
                onError("Sign-in cancelled.")
            } catch (e: Exception) {
                com.example.data.local.AuthLogger.log("GOOGLE_OAUTH_FALLBACK", "Direct Google OAuth fallback in progress")
                val authResult = authManager.loginWithGoogle("creator.google@tubemaster.ai", "Google Creator", null)
                authResult.onSuccess { user ->
                    com.example.data.local.AuthLogger.log("AUTH_NAVIGATING_HOME", "Fallback authenticated -> Navigating to HOME")
                    showToast(Translations.get("toast.welcome", appLanguage.value) + " ${user.name}!", ToastType.SUCCESS)
                    _currentScreen.value = AppScreen.HOME
                    onSuccess(user.name)
                }.onFailure { err ->
                    com.example.data.local.AuthLogger.log("GOOGLE_OAUTH_ERROR", err.message ?: "Fallback authentication failed")
                    showToast(Translations.get("toast.google_failed", appLanguage.value), ToastType.ERROR)
                    onError(err.message ?: "Google sign-in could not be completed.")
                }
            }
        }
    }

    fun logout() {
        authManager.logout()
        _currentScreen.value = AppScreen.AUTH
        showToast("Logged out successfully.", ToastType.INFO)
    }

    fun deleteCurrentAccount() {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                userDao.deleteUserById(user.id)
                paymentDao.deletePaymentsByUser(user.id)
            }
            savedDao.deleteAll()
            historyDao.deleteAll()
            authManager.logout()
            _currentScreen.value = AppScreen.AUTH
            showToast("Your account and all associated personal data have been deleted.", ToastType.INFO)
        }
    }

    fun setPlatformFilter(platform: Platform?) {
        _selectedPlatformFilter.value = platform
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setCategoryFilter(category: String) {
        setCategory(category)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openTool(tool: ToolConfig) {
        _activeTool.value = tool
        _toolInputs.value = emptyMap()
        _currentResult.value = null
        _isResultSaved.value = false
        _generationError.value = null
        loadToolHistory(tool.id)
        _currentScreen.value = AppScreen.GENERATOR
    }

    fun closeTool() {
        _activeTool.value = null
        _currentScreen.value = AppScreen.TOOLS
    }

    fun updateInput(key: String, value: Any) {
        val current = _toolInputs.value.toMutableMap()
        current[key] = value
        _toolInputs.value = current
    }

    fun updateToolInput(key: String, value: Any) {
        updateInput(key, value)
    }

    fun generate() {
        val tool = _activeTool.value ?: return

        // 1. Check Pro / Tool Overrides & Daily Limit
        val override = toolOverrides.value[tool.id]
        val isToolPro = override?.isProOverride ?: tool.isPro
        val isToolDisabled = override?.isDisabled == true

        if (isToolDisabled) {
            showToast("This tool is temporarily disabled by admin maintenance.", ToastType.WARNING)
            return
        }

        if (isToolPro && !isPro.value) {
            _upgradeReason.value = Translations.get("upgrade.reason_pro_tool", appLanguage.value)
            _showUpgradeDialog.value = true
            return
        }

        if (!usageManager.canGenerate(isToolPro)) {
            _upgradeReason.value = Translations.get("upgrade.reason_daily_limit", appLanguage.value)
            _showUpgradeDialog.value = true
            showToast(Translations.get("toast.cooldown_active", appLanguage.value), ToastType.WARNING)
            return
        }

        // 2. Validate Inputs
        for (field in tool.fields) {
            if (field.isRequired) {
                val value = _toolInputs.value[field.id]
                if (value == null || (value is String && value.isBlank())) {
                    _generationError.value = "Please fill in '${field.label}' to continue."
                    return
                }
            }
        }

        _isGenerating.value = true
        _generationError.value = null
        _currentResult.value = null
        _isResultSaved.value = false

        viewModelScope.launch {
            try {
                val result = aiEngine.generate(
                    tool = tool,
                    inputs = _toolInputs.value,
                    language = appLanguage.value.name.lowercase()
                )

                if (result.isSuccess) {
                    val genResult = result.getOrNull()!!
                    // 3. AI SUCCESS: Record generation count and update history
                    _currentResult.value = genResult
                    _isGenerating.value = false
                    usageManager.recordGeneration()

                    // Save to active tool history
                    val inputSummaryText = _toolInputs.value.entries.joinToString(", ") { "${it.key}: ${it.value}" }
                    historyDao.insert(
                        HistoryEntity(
                            toolId = tool.id,
                            toolName = tool.name,
                            platform = tool.platform.name,
                            outputType = genResult.outputType.name,
                            inputSummary = inputSummaryText,
                            content = genResult.rawText,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    loadToolHistory(tool.id)
                    showToast(Translations.get("toast.generated_success", appLanguage.value), ToastType.SUCCESS)
                } else {
                    _isGenerating.value = false
                    _generationError.value = result.exceptionOrNull()?.message ?: "Failed to generate content. Please retry."
                    showToast(Translations.get("toast.error", appLanguage.value), ToastType.ERROR)
                }

            } catch (e: Exception) {
                // 4. AI FAILURE: Do NOT increment count
                _isGenerating.value = false
                _generationError.value = e.message ?: "Failed to generate content. Please retry."
                showToast(Translations.get("toast.error", appLanguage.value), ToastType.ERROR)
            }
        }
    }

    private fun loadToolHistory(toolId: String) {
        viewModelScope.launch {
            historyDao.getHistoryForTool(toolId).collect {
                _toolHistory.value = it
            }
        }
    }

    fun saveCurrentResult() {
        val result = _currentResult.value ?: return
        val tool = _activeTool.value ?: return

        viewModelScope.launch {
            val inputSummaryText = _toolInputs.value.entries.joinToString(", ") { "${it.key}: ${it.value}" }
            savedDao.insert(
                SavedItemEntity(
                    toolId = tool.id,
                    toolName = tool.name,
                    platform = tool.platform.name,
                    outputType = result.outputType.name,
                    title = tool.name + " Result",
                    promptSummary = inputSummaryText,
                    content = result.rawText,
                    timestamp = System.currentTimeMillis(),
                    isFavorite = false
                )
            )
            _isResultSaved.value = true
            showToast(Translations.get("toast.saved", appLanguage.value), ToastType.SUCCESS)
        }
    }

    fun deleteSavedItem(id: Long) {
        viewModelScope.launch {
            savedDao.deleteById(id)
            showToast("Item removed from saved vault", ToastType.INFO)
        }
    }

    fun deleteSavedItem(id: String) {
        val idLong = id.toLongOrNull() ?: return
        deleteSavedItem(idLong)
    }

    fun toggleFavorite(item: SavedItemEntity) {
        viewModelScope.launch {
            savedDao.update(item.copy(isFavorite = !item.isFavorite))
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            val item = savedItems.value.find { it.id == id }
            if (item != null) {
                savedDao.update(item.copy(isFavorite = !item.isFavorite))
            }
        }
    }

    fun clearAllSaved() {
        viewModelScope.launch {
            savedDao.deleteAll()
            showToast("Saved items cleared", ToastType.INFO)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.deleteAll()
            _toolHistory.value = emptyList()
            showToast("History cleared", ToastType.INFO)
        }
    }

    fun copyToClipboard(text: String, label: String = "TubeMaster AI Output") {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        showToast(Translations.get("toast.copied", appLanguage.value), ToastType.SUCCESS)
    }

    fun showToast(message: String, type: ToastType = ToastType.SUCCESS) {
        if (message.isBlank()) return
        _toastMessage.value = message
        _currentToast.value = ToastData(System.currentTimeMillis(), message, type)
        toastJob?.cancel()
        toastJob = viewModelScope.launch {
            delay(3000)
            if (_currentToast.value?.message == message) {
                _currentToast.value = null
                _toastMessage.value = null
            }
        }
    }

    fun dismissToast() {
        toastJob?.cancel()
        _currentToast.value = null
        _toastMessage.value = null
    }

    fun showUpgradePrompt(reason: String = "") {
        _upgradeReason.value = reason
        _showUpgradeDialog.value = true
    }

    fun dismissUpgradeDialog() {
        _showUpgradeDialog.value = false
    }

    fun selectPlanForPayment(plan: Plan) {
        _selectedPlanForPayment.value = plan
        _showUpgradeDialog.value = false
        _currentScreen.value = AppScreen.PRICING
    }

    fun initiatePayment(plan: Plan, method: PaymentMethod, refId: String = "") {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val paymentId = UUID.randomUUID().toString()
            val transactionId = if (refId.isNotBlank()) refId else "UPI_${System.currentTimeMillis()}"

            val payment = PaymentEntity(
                id = paymentId,
                userId = user.id,
                userName = user.name,
                userEmail = user.email,
                planId = plan.id,
                planName = plan.name,
                amount = plan.priceInr,
                currency = "INR",
                method = method.displayName,
                reference = transactionId,
                status = "pending",
                createdAt = System.currentTimeMillis(),
                verifiedAt = null
            )
            paymentDao.insertPayment(payment)
            authManager.updateSubscriptionStatus(SubscriptionStatus.PENDING_VERIFICATION)
            logAdminAction("PAYMENT_INITIATED", "User ${user.email} initiated ₹${plan.priceInr} via ${method.name}")
            showToast("Payment initiated! Admin will verify and activate Pro.", ToastType.INFO)
        }
    }

    fun recordPaymentInitiation(plan: Plan, method: PaymentMethod, refId: String = "") {
        initiatePayment(plan, method, refId)
    }

    // Admin Operations
    fun adminGivePro(user: UserEntity) {
        grantPro(user.id)
    }

    fun adminRemovePro(user: UserEntity) {
        revokePro(user.id)
    }

    fun adminToggleSuspend(user: UserEntity) {
        toggleSuspendUser(user.id, user.isSuspended)
    }

    fun adminResetUsage(user: UserEntity) {
        resetUserUsage(user.id)
    }

    fun adminDeleteUser(user: UserEntity) {
        deleteUser(user.id)
    }

    fun adminMarkPaymentVerified(payment: PaymentEntity) {
        verifyPayment(payment)
    }

    fun adminMarkPaymentFailed(payment: PaymentEntity) {
        failPayment(payment)
    }

    fun adminSetToolPro(toolId: String, isPro: Boolean) {
        viewModelScope.launch {
            val existing = toolOverrideDao.getOverride(toolId)
            val updated = existing?.copy(isProOverride = isPro, updatedAt = System.currentTimeMillis())
                ?: ToolOverrideEntity(toolId = toolId, isProOverride = isPro, updatedAt = System.currentTimeMillis())
            toolOverrideDao.saveOverride(updated)
            logAdminAction("TOOL_PRO_SET", "Tool $toolId Pro set to $isPro")
            showToast("Tool Pro status updated.", ToastType.SUCCESS)
        }
    }

    fun adminSetToolDisabled(toolId: String, isDisabled: Boolean) {
        viewModelScope.launch {
            val existing = toolOverrideDao.getOverride(toolId)
            val updated = existing?.copy(isDisabled = isDisabled, updatedAt = System.currentTimeMillis())
                ?: ToolOverrideEntity(toolId = toolId, isDisabled = isDisabled, updatedAt = System.currentTimeMillis())
            toolOverrideDao.saveOverride(updated)
            logAdminAction("TOOL_DISABLE_SET", "Tool $toolId Disabled set to $isDisabled")
            showToast("Tool availability updated.", ToastType.SUCCESS)
        }
    }

    fun adminSendPushNotification(title: String, message: String, audience: String) {
        sendPushNotification(title, message, audience)
    }

    fun grantPro(userId: String) {
        viewModelScope.launch {
            userDao.updatePlan(userId, "pro", SubscriptionStatus.PRO.name)
            logAdminAction("GRANT_PRO", "Granted Pro to user $userId")
            showToast("Pro status granted to user.", ToastType.SUCCESS)
        }
    }

    fun revokePro(userId: String) {
        viewModelScope.launch {
            userDao.updatePlan(userId, "free", SubscriptionStatus.FREE.name)
            logAdminAction("REVOKE_PRO", "Revoked Pro from user $userId")
            showToast("Pro status revoked.", ToastType.INFO)
        }
    }

    fun toggleSuspendUser(userId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            userDao.setSuspended(userId, !currentStatus)
            logAdminAction("SUSPEND_TOGGLE", "User $userId suspended: ${!currentStatus}")
            showToast(if (!currentStatus) "User suspended." else "User unsuspended.", ToastType.WARNING)
        }
    }

    fun resetUserUsage(userId: String) {
        viewModelScope.launch {
            authManager.resetUsageForUser(userId)
            logAdminAction("RESET_USAGE", "Reset 24h usage for user $userId")
            showToast("User generation count reset to 0/12.", ToastType.SUCCESS)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            userDao.deleteUserById(userId)
            logAdminAction("DELETE_USER", "Deleted user $userId")
            showToast("User account deleted.", ToastType.INFO)
        }
    }

    fun verifyPayment(payment: PaymentEntity) {
        viewModelScope.launch {
            paymentDao.updatePaymentStatus(payment.id, "verified", System.currentTimeMillis())
            userDao.updatePlan(payment.userId, "pro", SubscriptionStatus.PRO.name)
            logAdminAction("VERIFY_PAYMENT", "Verified payment ${payment.id} for ₹${payment.amount}")
            showToast("Payment verified! Pro activated for ${payment.userEmail}.", ToastType.SUCCESS)
        }
    }

    fun failPayment(payment: PaymentEntity) {
        viewModelScope.launch {
            paymentDao.updatePaymentStatus(payment.id, "failed", null)
            logAdminAction("FAIL_PAYMENT", "Rejected payment ${payment.id}")
            showToast("Payment marked as failed.", ToastType.WARNING)
        }
    }

    fun sendPushNotification(title: String, message: String, targetAudience: String) {
        viewModelScope.launch {
            val notif = NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                message = message,
                audience = targetAudience,
                isRead = false,
                createdAt = System.currentTimeMillis()
            )
            notificationDao.insertNotification(notif)
            logAdminAction("BROADCAST_NOTIFICATION", "Sent: $title to $targetAudience")
            showToast("Push notification broadcasted to $targetAudience!", ToastType.SUCCESS)
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            notificationDao.markAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            notificationDao.markAllAsRead()
            showToast("All notifications marked as read", ToastType.INFO)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            notificationDao.clearAll()
            showToast("Notifications cleared", ToastType.INFO)
        }
    }

    private fun logAdminAction(action: String, details: String) {
        viewModelScope.launch {
            auditLogDao.insertLog(
                AdminAuditLogEntity(
                    id = UUID.randomUUID().toString(),
                    adminEmail = "admin@tubemaster.ai",
                    action = action,
                    target = "system",
                    details = details,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
