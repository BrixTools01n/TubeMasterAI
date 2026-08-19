package com.example.payment

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ProGold
import com.example.ui.theme.ProGoldSurface
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TubeMasterRed
import com.example.ui.theme.TubeMasterRedGlow
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodSheet(
    plan: Plan,
    userName: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onCopyUpiId: (String) -> Unit,
    onPaymentSubmitted: (String) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedMethod by remember { mutableStateOf(PaymentMethod.PHONEPE) }
    var transactionRefId by remember {
        mutableStateOf("TM-" + UUID.randomUUID().toString().take(8).uppercase())
    }
    var userUtrInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF09090C),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("payment_method_sheet"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF14080B))
                            .border(1.dp, TubeMasterRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payment,
                            contentDescription = null,
                            tint = TubeMasterRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Translations.get("pricing.upgrade_cta", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Instant UPI & Manual WhatsApp Activation",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            // Order Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ProGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = plan.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Reference ID: $transactionRefId",
                            fontSize = 11.sp,
                            color = ProGold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${plan.priceInr}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (plan.isAnnual) "(₹${plan.monthlyEffectiveInr}/mo)" else "Monthly",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // UPI ID Copy Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
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
                            text = "Direct Merchant UPI ID",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = PaymentConfig.DEFAULT_UPI_ID,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TubeMasterRedGlow
                        )
                    }

                    Button(
                        onClick = { onCopyUpiId(PaymentConfig.DEFAULT_UPI_ID) },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }

            // Select UPI App
            Text(
                text = "Choose Payment App",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedMethod == method
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF160A0D) else SurfaceCard)
                            .border(1.dp, if (isSelected) TubeMasterRed else SurfaceBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                selectedMethod = method
                                if (method.packageName != null) {
                                    try {
                                        val uri = Uri.parse(
                                            PaymentService.buildUpiIntentUri(
                                                plan = plan,
                                                referenceId = transactionRefId
                                            )
                                        )
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        intent.setPackage(method.packageName)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Fallback to generic UPI intent
                                        try {
                                            val uri = Uri.parse(
                                                PaymentService.buildUpiIntentUri(
                                                    plan = plan,
                                                    referenceId = transactionRefId
                                                )
                                            )
                                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                        } catch (ex: Exception) {
                                            // Handled
                                        }
                                    }
                                }
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = method.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                                Text(
                                    text = method.description,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TubeMasterRed)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("SELECTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // WhatsApp Verification Trigger Button
            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    val message = PaymentService.buildWhatsAppAdminMessage(
                        userName = userName,
                        plan = plan,
                        paymentMethod = selectedMethod,
                        referenceId = transactionRefId,
                        language = language
                    )
                    try {
                        val whatsappUrl = PaymentService.buildWhatsAppUrl(message)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                    onPaymentSubmitted(transactionRefId)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("send_whatsapp_verification_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Chat,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verify & Activate on WhatsApp",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Text(
                text = "🔒 100% Safe Manual Verification directly with TubeMaster AI Admin Support.",
                fontSize = 10.sp,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
