package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.payment.PaymentConfig
import com.example.payment.PaymentMethod
import com.example.payment.PaymentService
import com.example.payment.Plan
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodSheet(
    plan: Plan,
    userName: String,
    language: AppLanguage,
    onUserNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onPaymentInitiated: (PaymentMethod, String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val referenceId = remember { "TM-${System.currentTimeMillis() % 1000000}" }
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var isPaymentInitiated by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PitchBlack,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderSubtle)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .testTag("payment_modal_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isPaymentInitiated) Translations.get("payment.did_you_complete", language)
                        else Translations.get("payment.choose_method", language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${plan.name} • ₹${plan.priceInr} INR",
                        fontSize = 12.sp,
                        color = PrimaryRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isPaymentInitiated) {
                // Payment Methods List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PaymentMethod.entries.forEach { method ->
                        PaymentMethodItem(
                            method = method,
                            isSelected = selectedMethod == method,
                            onClick = {
                                selectedMethod = method
                                launchPayment(
                                    context = context,
                                    method = method,
                                    plan = plan,
                                    referenceId = referenceId
                                )
                                isPaymentInitiated = true
                                onPaymentInitiated(method, referenceId)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // UPI Details Fallback Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Direct UPI ID",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = PaymentConfig.DEFAULT_UPI_ID,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(PaymentConfig.DEFAULT_UPI_ID))
                                Toast.makeText(context, Translations.get("payment.upi_copied", language), Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceLighter),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.border(1.dp, PrimaryRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = PrimaryRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy UPI", fontSize = 11.sp, color = PrimaryRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Post-Intent Return State
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFFD700), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = Translations.get("payment.verification_pending", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = Translations.get("payment.initiated_notice", language),
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // "I've Paid — Verify via WhatsApp" Button
                    Button(
                        onClick = {
                            val msg = PaymentService.buildWhatsAppAdminMessage(
                                userName = userName,
                                plan = plan,
                                paymentMethod = selectedMethod ?: PaymentMethod.UPI_QR,
                                referenceId = referenceId,
                                language = language
                            )
                            val waUrl = PaymentService.buildWhatsAppUrl(msg)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                clipboardManager.setText(AnnotatedString(msg))
                                Toast.makeText(context, "WhatsApp not installed. Reference details copied.", Toast.LENGTH_LONG).show()
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("payment_ive_paid_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Translations.get("payment.ive_paid", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // "Not Yet / Try Other Method" Button
                    TextButton(
                        onClick = { isPaymentInitiated = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = Translations.get("payment.not_yet", language),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, if (isSelected) PrimaryRed else BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = method.description,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryRed.copy(alpha = 0.15f))
                    .border(1.dp, PrimaryRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Pay",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryRed
                )
            }
        }
    }
}

fun launchPayment(
    context: Context,
    method: PaymentMethod,
    plan: Plan,
    referenceId: String
) {
    val upiUri = PaymentService.buildUpiIntentUri(
        plan = plan,
        referenceId = referenceId
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(upiUri)
        if (method.packageName != null) {
            setPackage(method.packageName)
        }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val generalIntent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUri)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(generalIntent)
        } catch (e2: Exception) {
            // Handled via copy fallback
        }
    }
}
