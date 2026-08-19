package com.example.payment

import com.example.i18n.AppLanguage
import java.net.URLEncoder

object PaymentConfig {
    // Admin WhatsApp Contact (Centralized configuration - sole source of truth)
    const val ADMIN_WHATSAPP_PHONE = "919211791952"
    const val ADMIN_WHATSAPP_DISPLAY = "+91 9211791952"

    // UPI Merchant Configuration
    const val DEFAULT_UPI_ID = "tubemaster-rehankhan@fam"
    const val MERCHANT_NAME = "TubeMaster AI"
    const val CURRENCY_INR = "INR"
    const val CURRENCY_SYMBOL = "₹"

    // Plans
    val PRO_MONTHLY = Plan(
        id = "pro_monthly",
        name = "TubeMaster Pro Monthly",
        priceInr = 590,
        monthlyEffectiveInr = 590,
        priceUsd = 19,
        billingPeriod = "month",
        isAnnual = false
    )

    val PRO_ANNUAL = Plan(
        id = "pro_annual",
        name = "TubeMaster Pro Annual (35% OFF)",
        priceInr = 4680, // ₹390 / month
        monthlyEffectiveInr = 390,
        priceUsd = 149,
        billingPeriod = "year",
        isAnnual = true
    )

    fun getWhatsAppDirectUrl(message: String = ""): String {
        val encoded = if (message.isNotBlank()) URLEncoder.encode(message, "UTF-8") else ""
        return if (encoded.isNotBlank()) {
            "https://api.whatsapp.com/send?phone=$ADMIN_WHATSAPP_PHONE&text=$encoded"
        } else {
            "https://api.whatsapp.com/send?phone=$ADMIN_WHATSAPP_PHONE"
        }
    }

    fun buildSupportMessage(userName: String, isPro: Boolean): String {
        val safeName = userName.ifBlank { "Creator" }
        val statusText = if (isPro) "PRO Member" else "Free Starter Plan"
        return """
            Hello TubeMaster AI Admin,

            I have a question / query regarding TubeMaster AI.

            Name: $safeName
            Plan Status: $statusText

            Please assist me.
        """.trimIndent()
    }
}

data class Plan(
    val id: String,
    val name: String,
    val priceInr: Int,
    val monthlyEffectiveInr: Int = priceInr,
    val priceUsd: Int,
    val billingPeriod: String,
    val isAnnual: Boolean
)

enum class PaymentMethod(
    val id: String,
    val displayName: String,
    val packageName: String?,
    val description: String
) {
    PHONEPE("phonepe", "PhonePe", "com.phonepe.app", "Fast & secure instant UPI payment"),
    GPAY("gpay", "Google Pay", "com.google.android.apps.nbu.paisa.user", "Google Pay UPI direct checkout"),
    PAYTM("paytm", "Paytm", "net.one97.paytm", "Paytm Wallet, UPI or Netbanking"),
    FAMPAY("fampay", "FamPay", "com.fampay.in", "Gen-Z & student UPI payments"),
    UPI_QR("upi_qr", "UPI App / ID", null, "Scan QR or enter UPI ID: tubemaster-rehankhan@fam")
}

enum class SubscriptionStatus {
    FREE,
    PAYMENT_INITIATED,
    PENDING_VERIFICATION,
    PRO
}

object PaymentService {
    fun buildUpiIntentUri(
        plan: Plan,
        upiId: String = PaymentConfig.DEFAULT_UPI_ID,
        merchantName: String = PaymentConfig.MERCHANT_NAME,
        referenceId: String
    ): String {
        val note = "TubeMaster AI ${plan.name} ($referenceId)"
        val encodedNote = URLEncoder.encode(note, "UTF-8")
        val encodedMerchant = URLEncoder.encode(merchantName, "UTF-8")
        return "upi://pay?pa=$upiId&pn=$encodedMerchant&am=${plan.priceInr}.00&cu=INR&tn=$encodedNote&tr=$referenceId"
    }

    fun buildWhatsAppAdminMessage(
        userName: String,
        plan: Plan,
        paymentMethod: PaymentMethod,
        referenceId: String,
        language: AppLanguage
    ): String {
        val safeName = userName.ifBlank { "Creator" }
        val safeRef = referenceId.ifBlank { "Pending Verification" }
        return when (language) {
            AppLanguage.HINDI -> """
                नमस्ते TubeMaster AI Admin,

                मैं अपने TubeMaster AI भुगतान का सत्यापन कराना चाहता हूँ।

                Name: $safeName
                Plan: ${plan.name}
                Amount: ₹${plan.priceInr}
                Currency: INR
                Payment Method: ${paymentMethod.displayName}
                Reference: $safeRef

                कृपया मेरा पेमेंट वेरीफाई करें और मेरा Pro अकाउंट एक्टिवेट करें।
            """.trimIndent()

            AppLanguage.HINGLISH -> """
                Hello TubeMaster AI Admin,

                I want to verify my TubeMaster AI payment.

                Name: $safeName
                Plan: ${plan.name}
                Amount: ₹${plan.priceInr}
                Currency: INR
                Payment Method: ${paymentMethod.displayName}
                Reference: $safeRef

                Please mera payment verify karke Pro plan activate karein.
            """.trimIndent()

            AppLanguage.ENGLISH -> """
                Hello TubeMaster AI Admin,

                I want to verify my TubeMaster AI payment.

                Name: $safeName
                Plan: ${plan.name}
                Amount: ₹${plan.priceInr}
                Currency: INR
                Payment Method: ${paymentMethod.displayName}
                Reference: $safeRef

                Please verify my payment and activate my plan.
            """.trimIndent()
        }
    }

    fun buildWhatsAppUrl(message: String): String {
        val encoded = URLEncoder.encode(message, "UTF-8")
        return "https://api.whatsapp.com/send?phone=${PaymentConfig.ADMIN_WHATSAPP_PHONE}&text=$encoded"
    }
}
