package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.payment.PaymentConfig
import com.example.payment.Plan
import com.example.ui.components.AdminAuthDialog
import com.example.ui.components.PaymentMethodSheet
import com.example.ui.components.ToastBanner
import com.example.ui.components.TubeMasterHeader
import com.example.ui.components.UpgradeDialog
import com.example.ui.navigation.AppBottomBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PitchBlack
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TubeMasterApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TubeMasterApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedPlatform by viewModel.selectedPlatformFilter.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val activeTool by viewModel.activeTool.collectAsState()
    val toolInputs by viewModel.toolInputs.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentResult by viewModel.currentResult.collectAsState()
    val isResultSaved by viewModel.isResultSaved.collectAsState()
    val generationError by viewModel.generationError.collectAsState()
    val toolHistory by viewModel.toolHistory.collectAsState()

    val isPro by viewModel.isPro.collectAsState()
    val subscriptionStatus by viewModel.subscriptionStatus.collectAsState()
    val dailyCount by viewModel.dailyGenerationsCount.collectAsState()
    val limitReachedAt by viewModel.limitReachedAt.collectAsState()
    val selectedLanguage by viewModel.appLanguage.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val savedItems by viewModel.savedItems.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val showUpgradeDialog by viewModel.showUpgradeDialog.collectAsState()
    val upgradeReason by viewModel.upgradeReason.collectAsState()
    val showAdminAuth by viewModel.showAdminAuth.collectAsState()
    val adminAuthError by viewModel.adminAuthError.collectAsState()

    var activePaymentPlan by remember { mutableStateOf<Plan?>(null) }

    val isFullScreenFlow = currentScreen == AppScreen.SPLASH ||
            currentScreen == AppScreen.ONBOARDING ||
            currentScreen == AppScreen.AUTH
    val isAdminView = currentScreen == AppScreen.ADMIN || currentScreen == AppScreen.ADMIN_PANEL

    // Handle system back navigation
    BackHandler(
        enabled = currentScreen == AppScreen.GENERATOR ||
                isAdminView ||
                currentScreen == AppScreen.NOTIFICATIONS
    ) {
        when {
            currentScreen == AppScreen.GENERATOR -> viewModel.closeTool()
            isAdminView -> viewModel.exitAdminPanel()
            currentScreen == AppScreen.NOTIFICATIONS -> viewModel.navigateTo(AppScreen.PROFILE)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .statusBarsPadding(),
        topBar = {
            if (!isFullScreenFlow && currentScreen != AppScreen.GENERATOR && !isAdminView) {
                TubeMasterHeader(
                    isPro = isPro,
                    dailyCount = dailyCount,
                    onProClick = { viewModel.navigateTo(AppScreen.PRICING) }
                )
            }
        },
        bottomBar = {
            if (!isFullScreenFlow && currentScreen != AppScreen.GENERATOR && !isAdminView) {
                AppBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    savedCount = savedItems.size,
                    isPro = isPro
                )
            }
        },
        containerColor = PitchBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullScreenFlow) PaddingValues(0.dp) else innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = androidx.compose.animation.core.tween(com.example.ui.theme.MotionDurations.BASE, easing = com.example.ui.theme.MotionCurves.Decelerate)) +
                            slideInVertically(
                                animationSpec = androidx.compose.animation.core.tween(com.example.ui.theme.MotionDurations.BASE, easing = com.example.ui.theme.MotionCurves.PremiumSnappy),
                                initialOffsetY = { (it * 0.04f).toInt() }
                            ) togetherWith
                            fadeOut(animationSpec = androidx.compose.animation.core.tween(com.example.ui.theme.MotionDurations.FAST, easing = com.example.ui.theme.MotionCurves.Accelerate))
                },
                label = "ScreenTransition",
                modifier = Modifier.fillMaxSize()
            ) { screen ->
                when (screen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = { viewModel.onSplashFinished() }
                        )
                    }

                    AppScreen.ONBOARDING -> {
                        OnboardingScreen(
                            currentLanguage = selectedLanguage,
                            onLanguageChange = { viewModel.setLanguage(it) },
                            onFinishOnboarding = { viewModel.completeOnboarding() }
                        )
                    }

                    AppScreen.AUTH -> {
                        AuthScreen(viewModel = viewModel)
                    }

                    AppScreen.HOME -> {
                        HomeScreen(
                            isPro = isPro,
                            dailyCount = dailyCount,
                            limitReachedAt = limitReachedAt,
                            searchQuery = searchQuery,
                            language = selectedLanguage,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onNavigate = { viewModel.navigateTo(it) },
                            onPlatformSelect = { platform ->
                                viewModel.setPlatformFilter(platform)
                                viewModel.navigateTo(AppScreen.TOOLS)
                            },
                            onToolClick = { tool -> viewModel.openTool(tool) },
                            onUpgradeClick = { viewModel.navigateTo(AppScreen.PRICING) }
                        )
                    }

                    AppScreen.TOOLS -> {
                        ToolsScreen(
                            selectedPlatform = selectedPlatform,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            isPro = isPro,
                            language = selectedLanguage,
                            onPlatformSelect = { viewModel.setPlatformFilter(it) },
                            onCategorySelect = { viewModel.setCategoryFilter(it) },
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onToolClick = { tool -> viewModel.openTool(tool) }
                        )
                    }

                    AppScreen.GENERATOR -> {
                        val tool = activeTool
                        if (tool != null) {
                            GeneratorScreen(
                                tool = tool,
                                inputs = toolInputs,
                                isGenerating = isGenerating,
                                result = currentResult,
                                isSaved = isResultSaved,
                                error = generationError,
                                history = toolHistory,
                                isPro = isPro,
                                language = selectedLanguage,
                                onInputChange = { fieldId, value ->
                                    viewModel.updateToolInput(fieldId, value)
                                },
                                onGenerate = { viewModel.generate() },
                                onSave = { viewModel.saveCurrentResult() },
                                onCopy = { text, label -> viewModel.copyToClipboard(text, label) },
                                onBack = { viewModel.closeTool() }
                            )
                        }
                    }

                    AppScreen.SAVED -> {
                        SavedScreen(
                            savedItems = savedItems,
                            language = selectedLanguage,
                            onCopy = { text, label -> viewModel.copyToClipboard(text, label) },
                            onDelete = { viewModel.deleteSavedItem(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onClearAll = { viewModel.clearAllSaved() },
                            onNavigate = { viewModel.navigateTo(it) },
                            onOpenTool = { tool -> viewModel.openTool(tool) }
                        )
                    }

                    AppScreen.PRICING -> {
                        PricingScreen(
                            isPro = isPro,
                            subscriptionStatus = subscriptionStatus,
                            language = selectedLanguage,
                            onOpenPaymentSheet = { plan ->
                                activePaymentPlan = plan
                            }
                        )
                    }

                    AppScreen.PROFILE -> {
                        ProfileScreen(viewModel = viewModel)
                    }

                    AppScreen.ADMIN, AppScreen.ADMIN_PANEL -> {
                        AdminPanelScreen(viewModel = viewModel)
                    }

                    AppScreen.NOTIFICATIONS -> {
                        NotificationsScreen(viewModel = viewModel)
                    }
                }
            }

            // Floating Feedback Toast Banner
            ToastBanner(
                message = toastMessage,
                onDismiss = { viewModel.dismissToast() },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Upgrade Modal Dialog
            if (showUpgradeDialog) {
                UpgradeDialog(
                    reason = upgradeReason,
                    onDismiss = { viewModel.dismissUpgradeDialog() },
                    onUpgrade = {
                        viewModel.dismissUpgradeDialog()
                        activePaymentPlan = PaymentConfig.PRO_ANNUAL
                    }
                )
            }

            // Discreet Admin Authentication Modal
            if (showAdminAuth) {
                AdminAuthDialog(
                    errorMessage = adminAuthError,
                    onDismiss = { viewModel.closeAdminAuth() },
                    onAuthenticate = { password ->
                        viewModel.authenticateAdmin(password)
                    }
                )
            }

            // Payment Bottom Sheet
            activePaymentPlan?.let { plan ->
                PaymentMethodSheet(
                    plan = plan,
                    userName = user?.name ?: "Creator",
                    language = selectedLanguage,
                    onUserNameChange = { /* managed via UserEntity */ },
                    onDismiss = { activePaymentPlan = null },
                    onPaymentInitiated = { method, refId ->
                        viewModel.recordPaymentInitiation(plan, method, refId)
                    }
                )
            }
        }
    }
}
