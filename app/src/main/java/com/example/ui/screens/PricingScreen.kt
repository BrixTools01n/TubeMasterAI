package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.payment.PaymentConfig
import com.example.payment.Plan
import com.example.payment.SubscriptionStatus
import com.example.ui.theme.*

@Composable
fun PricingScreen(
    isPro: Boolean,
    subscriptionStatus: SubscriptionStatus,
    language: AppLanguage,
    onOpenPaymentSheet: (Plan) -> Unit
) {
    var isAnnual by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .testTag("pricing_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pending verification banner
        if (subscriptionStatus == SubscriptionStatus.PENDING_VERIFICATION) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1705)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Translations.get("pricing.verification_pending", language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                            Text(
                                text = "Your payment transaction was submitted for admin verification. Your Pro account will be unlocked as soon as verified.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⭐ PRO CREATOR ACCESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = Translations.get("pricing.title", language),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = Translations.get("pricing.subtitle", language),
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Billing Toggle (Monthly vs Annual)
                BillingToggle(
                    isAnnual = isAnnual,
                    onToggle = { isAnnual = it }
                )
            }
        }

        // Pro Plan (Featured)
        item {
            val selectedPlan = if (isAnnual) PaymentConfig.PRO_ANNUAL else PaymentConfig.PRO_MONTHLY
            ProPlanCard(
                isAnnual = isAnnual,
                isCurrentPlan = isPro,
                language = language,
                onUpgrade = { onOpenPaymentSheet(selectedPlan) }
            )
        }

        // Free Plan Card
        item {
            FreePlanCard(
                isCurrentPlan = !isPro,
                language = language
            )
        }

        // Guarantee Badge
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "7-Day Creator Guarantee",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "If TubeMaster AI does not 10x your content workflow, reach admin support anytime for complete assistance.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // FAQ Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )

                FaqAccordionItem(
                    question = "What AI models power TubeMaster AI?",
                    answer = "TubeMaster AI utilizes Gemini 1.5 and multi-tier LLM integrations via OpenRouter with custom viral prompt engineering."
                )
                FaqAccordionItem(
                    question = "How many tools are included in Pro?",
                    answer = "Pro includes all 100 tools (40 YouTube, 30 Instagram, and 30 Facebook) without any feature locking or restrictive limits."
                )
                FaqAccordionItem(
                    question = "How does UPI and WhatsApp verification work?",
                    answer = "When you pay via UPI apps (PhonePe, GPay, Paytm, FamPay), our system records your payment and opens WhatsApp to verify your reference ID for fast admin activation."
                )
            }
        }
    }
}

@Composable
fun BillingToggle(isAnnual: Boolean, onToggle: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!isAnnual) PrimaryRed else Color.Transparent)
                    .clickable { onToggle(false) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Monthly",
                    fontSize = 12.sp,
                    fontWeight = if (!isAnnual) FontWeight.Bold else FontWeight.Normal,
                    color = if (!isAnnual) Color.White else TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isAnnual) PrimaryRed else Color.Transparent)
                    .clickable { onToggle(true) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Annual",
                        fontSize = 12.sp,
                        fontWeight = if (isAnnual) FontWeight.Bold else FontWeight.Normal,
                        color = if (isAnnual) Color.White else TextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFD700))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "SAVE 50%",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProPlanCard(
    isAnnual: Boolean,
    isCurrentPlan: Boolean,
    language: AppLanguage,
    onUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, Color(0xFFFFD700), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TubeMaster PRO",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "For serious creators & social teams",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFD700))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "BEST VALUE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (isAnnual) "₹590" else "₹390",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = if (isAnnual) " / year (One-time)" else " / month",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (isAnnual) {
                Text(
                    text = "Special creator launch rate • Full 12-month unlimited pass",
                    fontSize = 11.sp,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PlanFeatureRow(included = true, text = "Access to ALL 100 YouTube, IG & FB Tools")
                PlanFeatureRow(included = true, text = "Unlimited AI Generations (No 24h Quota)")
                PlanFeatureRow(included = true, text = "Advanced Gemini & OpenRouter AI Engine")
                PlanFeatureRow(included = true, text = "Unlimited Saved Collections & History")
                PlanFeatureRow(included = true, text = "Full Production Scripts & 30-Day Calendars")
                PlanFeatureRow(included = true, text = "English, Hindi & Hinglish Content Support")
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (!isCurrentPlan) {
                        onUpgrade()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("pricing_pro_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentPlan) SurfaceLighter else PrimaryRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isCurrentPlan) "Active Plan: TubeMaster PRO" else Translations.get("pricing.upgrade_cta", language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FreePlanCard(
    isCurrentPlan: Boolean,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Free Creator Plan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "For starting out & testing tools",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Text(
                    text = "₹0",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PlanFeatureRow(included = true, text = "12 AI Generations per 24-hour window")
                PlanFeatureRow(included = true, text = "Access to Standard Free Tools")
                PlanFeatureRow(included = true, text = "Save up to 50 items locally")
                PlanFeatureRow(included = false, text = "Advanced PRO Tools (Locked)")
                PlanFeatureRow(included = false, text = "High-tier LLM Models")
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isCurrentPlan) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceLighter)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Current Plan", fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun PlanFeatureRow(included: Boolean, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (included) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (included) Color(0xFFFFD700) else TextDisabled,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (included) Color.White else TextDisabled
        )
    }
}

@Composable
fun FaqAccordionItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = answer,
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
