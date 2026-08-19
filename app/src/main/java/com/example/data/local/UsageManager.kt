package com.example.data.local

import com.example.i18n.AppLanguage
import com.example.payment.SubscriptionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsageManager(
    private val authManager: AuthManager
) {
    companion object {
        const val FREE_DAILY_LIMIT = 12
        const val ROLLING_WINDOW_MS = 24 * 60 * 60 * 1000L
        const val FREE_MAX_SAVED = 50
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    val isPro: StateFlow<Boolean> = authManager.currentUser.map { user ->
        user?.plan == "pro" || user?.subscriptionStatus == SubscriptionStatus.PRO.name
    }.stateIn(scope, SharingStarted.Eagerly, false)

    val subscriptionStatus: StateFlow<SubscriptionStatus> = authManager.currentUser.map { user ->
        try {
            SubscriptionStatus.valueOf(user?.subscriptionStatus ?: SubscriptionStatus.FREE.name)
        } catch (e: Throwable) {
            SubscriptionStatus.FREE
        }
    }.stateIn(scope, SharingStarted.Eagerly, SubscriptionStatus.FREE)

    val hasCompletedOnboarding: StateFlow<Boolean> = authManager.hasCompletedOnboarding
    val appLanguage: StateFlow<AppLanguage> = authManager.appLanguage

    val dailyCount: StateFlow<Int> = authManager.currentUser.map { user ->
        user?.generationCount ?: 0
    }.stateIn(scope, SharingStarted.Eagerly, 0)

    val limitReachedAt: StateFlow<Long?> = authManager.currentUser.map { user ->
        user?.limitReachedAt
    }.stateIn(scope, SharingStarted.Eagerly, null)

    val userName: StateFlow<String> = authManager.currentUser.map { user ->
        user?.name ?: "Creator"
    }.stateIn(scope, SharingStarted.Eagerly, "Creator")

    val userEmail: StateFlow<String> = authManager.currentUser.map { user ->
        user?.email ?: "creator@tubemaster.ai"
    }.stateIn(scope, SharingStarted.Eagerly, "creator@tubemaster.ai")

    fun canGenerate(isToolPro: Boolean = false): Boolean {
        val user = authManager.currentUser.value ?: return false
        if (user.isSuspended) return false
        val isProUser = user.plan == "pro" || user.subscriptionStatus == SubscriptionStatus.PRO.name
        if (isToolPro && !isProUser) return false
        if (isProUser) return true

        // Free tier: if at limit, check if 24 hours elapsed
        if (user.generationCount >= FREE_DAILY_LIMIT) {
            val limitReached = user.limitReachedAt
            if (limitReached != null && limitReached > 0L) {
                val elapsed = System.currentTimeMillis() - limitReached
                if (elapsed >= ROLLING_WINDOW_MS) {
                    // Auto-reset triggered asynchronously
                    scope.launch(Dispatchers.IO) {
                        authManager.checkCooldownAndAutoReset(user)
                    }
                    return true
                }
            }
            return false
        }
        return true
    }

    fun getRemainingGenerations(): Int {
        val user = authManager.currentUser.value ?: return 0
        val isProUser = user.plan == "pro" || user.subscriptionStatus == SubscriptionStatus.PRO.name
        if (isProUser) return 999

        if (user.generationCount >= FREE_DAILY_LIMIT) {
            val limitReached = user.limitReachedAt
            if (limitReached != null && limitReached > 0L) {
                val elapsed = System.currentTimeMillis() - limitReached
                if (elapsed >= ROLLING_WINDOW_MS) {
                    return FREE_DAILY_LIMIT
                }
            }
            return 0
        }
        return (FREE_DAILY_LIMIT - user.generationCount).coerceAtLeast(0)
    }

    fun getCooldownRemainingMillis(): Long {
        val user = authManager.currentUser.value ?: return 0L
        if (user.plan == "pro" || user.subscriptionStatus == SubscriptionStatus.PRO.name) return 0L
        if (user.generationCount < FREE_DAILY_LIMIT) return 0L

        val limitReached = user.limitReachedAt ?: return 0L
        val elapsed = System.currentTimeMillis() - limitReached
        val remaining = ROLLING_WINDOW_MS - elapsed
        return if (remaining > 0L) remaining else 0L
    }

    fun recordGeneration() {
        scope.launch {
            authManager.recordGeneration()
        }
    }

    fun checkCooldownReset() {
        scope.launch {
            authManager.checkCooldownAndAutoReset()
        }
    }

    fun setProPlan(isPro: Boolean) {
        scope.launch {
            val status = if (isPro) SubscriptionStatus.PRO else SubscriptionStatus.FREE
            authManager.updateSubscriptionStatus(status)
        }
    }

    fun setSubscriptionStatus(status: SubscriptionStatus) {
        scope.launch {
            authManager.updateSubscriptionStatus(status)
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        authManager.setOnboardingCompleted(completed)
    }

    fun setAppLanguage(lang: AppLanguage) {
        authManager.setAppLanguage(lang)
    }
}
