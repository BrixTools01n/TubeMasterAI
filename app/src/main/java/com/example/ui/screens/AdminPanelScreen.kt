package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.PaymentEntity
import com.example.data.local.UserEntity
import com.example.data.registry.ToolRegistry
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AdminTab {
    USERS,
    EARNINGS,
    TOOLS,
    NOTIFICATIONS,
    AUDIT
}

@Composable
fun AdminPanelScreen(viewModel: MainViewModel) {
    var activeTab by remember { mutableStateOf(AdminTab.USERS) }

    val users by viewModel.allAdminUsers.collectAsState()
    val payments by viewModel.allPayments.collectAsState()
    val toolOverrides by viewModel.toolOverrides.collectAsState()
    val auditLogs by viewModel.adminAuditLogs.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Header
            AdminHeader(
                onExit = { viewModel.exitAdminPanel() }
            )

            // Horizontal Tab Bar
            ScrollableTabRow(
                selectedTabIndex = activeTab.ordinal,
                containerColor = SurfaceDark,
                contentColor = Color.White,
                edgePadding = 12.dp,
                divider = { HorizontalDivider(color = BorderSubtle) }
            ) {
                Tab(
                    selected = activeTab == AdminTab.USERS,
                    onClick = { activeTab = AdminTab.USERS },
                    text = {
                        Text(
                            text = "Users (${users.size})",
                            fontWeight = if (activeTab == AdminTab.USERS) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == AdminTab.USERS) PrimaryRed else TextMuted
                        )
                    }
                )
                Tab(
                    selected = activeTab == AdminTab.EARNINGS,
                    onClick = { activeTab = AdminTab.EARNINGS },
                    text = {
                        val verifiedCount = payments.count { it.status == "verified" }
                        Text(
                            text = "Earnings ($verifiedCount)",
                            fontWeight = if (activeTab == AdminTab.EARNINGS) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == AdminTab.EARNINGS) PrimaryRed else TextMuted
                        )
                    }
                )
                Tab(
                    selected = activeTab == AdminTab.TOOLS,
                    onClick = { activeTab = AdminTab.TOOLS },
                    text = {
                        Text(
                            text = "Tools (100)",
                            fontWeight = if (activeTab == AdminTab.TOOLS) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == AdminTab.TOOLS) PrimaryRed else TextMuted
                        )
                    }
                )
                Tab(
                    selected = activeTab == AdminTab.NOTIFICATIONS,
                    onClick = { activeTab = AdminTab.NOTIFICATIONS },
                    text = {
                        Text(
                            text = "Push (${notifications.size})",
                            fontWeight = if (activeTab == AdminTab.NOTIFICATIONS) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == AdminTab.NOTIFICATIONS) PrimaryRed else TextMuted
                        )
                    }
                )
                Tab(
                    selected = activeTab == AdminTab.AUDIT,
                    onClick = { activeTab = AdminTab.AUDIT },
                    text = {
                        Text(
                            text = "Audit Log",
                            fontWeight = if (activeTab == AdminTab.AUDIT) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == AdminTab.AUDIT) PrimaryRed else TextMuted
                        )
                    }
                )
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                when (activeTab) {
                    AdminTab.USERS -> AdminUsersTab(
                        users = users,
                        onGivePro = { viewModel.adminGivePro(it) },
                        onRemovePro = { viewModel.adminRemovePro(it) },
                        onToggleSuspend = { viewModel.adminToggleSuspend(it) },
                        onResetUsage = { viewModel.adminResetUsage(it) },
                        onDeleteUser = { userToDelete = it }
                    )
                    AdminTab.EARNINGS -> AdminEarningsTab(
                        payments = payments,
                        onVerifyPayment = { viewModel.adminMarkPaymentVerified(it) },
                        onRejectPayment = { viewModel.adminMarkPaymentFailed(it) }
                    )
                    AdminTab.TOOLS -> AdminToolsTab(
                        toolOverrides = toolOverrides,
                        onSetPro = { id, isPro -> viewModel.adminSetToolPro(id, isPro) },
                        onSetDisabled = { id, disabled -> viewModel.adminSetToolDisabled(id, disabled) }
                    )
                    AdminTab.NOTIFICATIONS -> AdminNotificationsTab(
                        notifications = notifications,
                        onSend = { title, message, audience ->
                            viewModel.adminSendPushNotification(title, message, audience)
                        },
                        onDelete = { viewModel.clearAllNotifications() }
                    )
                    AdminTab.AUDIT -> AdminAuditTab(auditLogs = auditLogs)
                }
            }
        }

        // Delete User Confirmation Dialog
        userToDelete?.let { user ->
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                containerColor = SurfaceDark,
                titleContentColor = Color.White,
                textContentColor = TextMuted,
                title = { Text("Delete User Permanently?", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to permanently delete '${user.name}' (${user.email})? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.adminDeleteUser(user)
                            userToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                    ) {
                        Text("Delete User", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }
    }
}

@Composable
fun AdminHeader(onExit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = PrimaryRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TubeMaster Admin",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ONLINE",
                            color = Color(0xFF22C55E),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Full-Stack Security & Revenue Control",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        IconButton(
            onClick = onExit,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SurfaceLighter)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Exit Admin",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// USERS TAB
// -------------------------------------------------------------
@Composable
fun AdminUsersTab(
    users: List<UserEntity>,
    onGivePro: (UserEntity) -> Unit,
    onRemovePro: (UserEntity) -> Unit,
    onToggleSuspend: (UserEntity) -> Unit,
    onResetUsage: (UserEntity) -> Unit,
    onDeleteUser: (UserEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") } // All, Free, Pro, Suspended

    val filteredUsers = remember(users, searchQuery, filterType) {
        users.filter { user ->
            val matchQuery = searchQuery.isBlank() ||
                    user.name.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true)
            val matchFilter = when (filterType) {
                "Free" -> user.plan == "free" && !user.isSuspended
                "Pro" -> user.plan == "pro" && !user.isSuspended
                "Suspended" -> user.isSuspended
                else -> true
            }
            matchQuery && matchFilter
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filter
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search users by name or email...", color = TextDisabled, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryRed,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Free", "Pro", "Suspended").forEach { type ->
                FilterChip(
                    selected = filterType == type,
                    onClick = { filterType = type },
                    label = { Text(type, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryRed,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceDark,
                        labelColor = TextMuted
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = BorderSubtle,
                        selectedBorderColor = PrimaryRed,
                        enabled = true,
                        selected = filterType == type
                    )
                )
            }
        }

        if (filteredUsers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No users found matching query.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers, key = { it.id }) { user ->
                    UserCardItem(
                        user = user,
                        onGivePro = { onGivePro(user) },
                        onRemovePro = { onRemovePro(user) },
                        onToggleSuspend = { onToggleSuspend(user) },
                        onResetUsage = { onResetUsage(user) },
                        onDelete = { onDeleteUser(user) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserCardItem(
    user: UserEntity,
    onGivePro: () -> Unit,
    onRemovePro: () -> Unit,
    onToggleSuspend: () -> Unit,
    onResetUsage: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val isPro = user.plan == "pro" || user.subscriptionStatus == "PRO"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (user.isSuspended) PrimaryRed.copy(alpha = 0.5f) else if (isPro) Color(0xFFFFD700).copy(alpha = 0.3f) else BorderSubtle
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isPro) Color(0xFFFFD700).copy(alpha = 0.15f) else SurfaceLighter),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isPro) Color(0xFFFFD700) else Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (user.isSuspended) {
                            BadgeBox("SUSPENDED", PrimaryRed)
                        } else if (isPro) {
                            BadgeBox("PRO", Color(0xFFFFD700))
                        } else {
                            BadgeBox("FREE (12/24h)", TextMuted)
                        }
                    }

                    Text(
                        text = user.email,
                        fontSize = 12.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Generations: ${user.generationCount}/12 • Joined ${formatDate(user.createdAt)}",
                        fontSize = 10.sp,
                        color = TextDisabled
                    )
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Actions",
                        tint = TextMuted
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SurfaceLighter)
                ) {
                    if (isPro) {
                        DropdownMenuItem(
                            text = { Text("Revoke Pro Plan", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = PrimaryRed) },
                            onClick = {
                                showMenu = false
                                onRemovePro()
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Grant TubeMaster Pro", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700)) },
                            onClick = {
                                showMenu = false
                                onGivePro()
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = { Text("Reset 24h Limits", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White) },
                        onClick = {
                            showMenu = false
                            onResetUsage()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(if (user.isSuspended) "Unsuspend User" else "Suspend User", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = PrimaryRed) },
                        onClick = {
                            showMenu = false
                            onToggleSuspend()
                        }
                    )

                    HorizontalDivider(color = BorderSubtle)

                    DropdownMenuItem(
                        text = { Text("Delete User Permanently", color = PrimaryRed, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = PrimaryRed) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// EARNINGS TAB
// -------------------------------------------------------------
@Composable
fun AdminEarningsTab(
    payments: List<PaymentEntity>,
    onVerifyPayment: (PaymentEntity) -> Unit,
    onRejectPayment: (PaymentEntity) -> Unit
) {
    val verifiedPayments = payments.filter { it.status == "verified" }
    val totalRevenue = verifiedPayments.sumOf { it.amount.toLong() }
    val verifiedCount = verifiedPayments.size

    val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
    val thisMonthRevenue = verifiedPayments.filter { it.createdAt >= thirtyDaysAgo }.sumOf { it.amount.toLong() }

    val todayStart = System.currentTimeMillis() - 24L * 60 * 60 * 1000
    val todayRevenue = verifiedPayments.filter { it.createdAt >= todayStart }.sumOf { it.amount.toLong() }

    Column(modifier = Modifier.fillMaxSize()) {
        // Summary KPI Metrics
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Total Revenue",
                value = "₹$totalRevenue",
                color = Color(0xFF22C55E),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "This Month",
                value = "₹$thisMonthRevenue",
                color = Color(0xFFFFD700),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Today",
                value = "₹$todayRevenue",
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Verified Orders",
                value = "$verifiedCount",
                color = Color(0xFF38BDF8),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Pending Verification",
                value = "${payments.count { it.status == "pending" }}",
                color = PrimaryRed,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Payment Transactions & WhatsApp Verifications",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        if (payments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No payment records in database yet.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(payments, key = { it.id }) { payment ->
                    PaymentTransactionItem(
                        payment = payment,
                        onVerify = { onVerifyPayment(payment) },
                        onReject = { onRejectPayment(payment) }
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, color = color, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun PaymentTransactionItem(
    payment: PaymentEntity,
    onVerify: () -> Unit,
    onReject: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when (payment.status) {
                "verified" -> Color(0xFF22C55E).copy(alpha = 0.4f)
                "pending" -> Color(0xFFFFD700).copy(alpha = 0.4f)
                else -> PrimaryRed.copy(alpha = 0.4f)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = payment.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = payment.userEmail,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${payment.amount}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextMuted)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(SurfaceLighter)
                        ) {
                            if (payment.status != "verified") {
                                DropdownMenuItem(
                                    text = { Text("Mark Payment Verified & Activate Pro", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold) },
                                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E)) },
                                    onClick = {
                                        showMenu = false
                                        onVerify()
                                    }
                                )
                            }
                            if (payment.status != "failed") {
                                DropdownMenuItem(
                                    text = { Text("Mark Payment Failed", color = PrimaryRed) },
                                    leadingIcon = { Icon(Icons.Default.Cancel, contentDescription = null, tint = PrimaryRed) },
                                    onClick = {
                                        showMenu = false
                                        onReject()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref: ${payment.reference} • ${payment.method}",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                when (payment.status) {
                    "verified" -> BadgeBox("VERIFIED ✓", Color(0xFF22C55E))
                    "pending" -> BadgeBox("PENDING VERIFICATION", Color(0xFFFFD700))
                    else -> BadgeBox("FAILED", PrimaryRed)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TOOLS TAB (100)
// -------------------------------------------------------------
@Composable
fun AdminToolsTab(
    toolOverrides: Map<String, com.example.data.local.ToolOverrideEntity>,
    onSetPro: (String, Boolean) -> Unit,
    onSetDisabled: (String, Boolean) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf<Platform?>(null) }

    val allTools = remember { ToolRegistry.allTools }

    val filteredTools = remember(allTools, toolOverrides, searchQuery, selectedPlatform) {
        allTools.filter { tool ->
            val matchPlatform = selectedPlatform == null || tool.platform == selectedPlatform
            val matchSearch = searchQuery.isBlank() ||
                    tool.name.contains(searchQuery, ignoreCase = true) ||
                    tool.category.contains(searchQuery, ignoreCase = true) ||
                    tool.id.contains(searchQuery, ignoreCase = true)
            matchPlatform && matchSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search across all 100 tools...", color = TextDisabled, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryRed,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedPlatform == null,
                onClick = { selectedPlatform = null },
                label = { Text("All (100)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryRed,
                    selectedLabelColor = Color.White,
                    containerColor = SurfaceDark,
                    labelColor = TextMuted
                )
            )
            Platform.entries.forEach { p ->
                FilterChip(
                    selected = selectedPlatform == p,
                    onClick = { selectedPlatform = p },
                    label = { Text(p.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryRed,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceDark,
                        labelColor = TextMuted
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTools, key = { it.id }) { tool ->
                val override = toolOverrides[tool.id]
                val isPro = override?.isProOverride ?: tool.isPro
                val isDisabled = override?.isDisabled ?: false

                AdminToolItem(
                    tool = tool,
                    isPro = isPro,
                    isDisabled = isDisabled,
                    onTogglePro = { onSetPro(tool.id, !isPro) },
                    onToggleDisabled = { onSetDisabled(tool.id, !isDisabled) }
                )
            }
        }
    }
}

@Composable
fun AdminToolItem(
    tool: ToolConfig,
    isPro: Boolean,
    isDisabled: Boolean,
    onTogglePro: () -> Unit,
    onToggleDisabled: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDisabled) PrimaryRed.copy(alpha = 0.4f) else if (isPro) Color(0xFFFFD700).copy(alpha = 0.3f) else BorderSubtle
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlatformBadge(platform = tool.platform)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tool.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isDisabled) TextDisabled else Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${tool.category} • ID: ${tool.id}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (isDisabled) {
                        BadgeBox("DISABLED", PrimaryRed)
                    } else if (isPro) {
                        BadgeBox("PRO ONLY", Color(0xFFFFD700))
                    } else {
                        BadgeBox("FREE (12/24h)", Color(0xFF22C55E))
                    }
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextMuted)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SurfaceLighter)
                ) {
                    DropdownMenuItem(
                        text = { Text(if (isPro) "Make Free Tool" else "Make Pro Tool", color = Color.White) },
                        leadingIcon = { Icon(if (isPro) Icons.Default.LockOpen else Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD700)) },
                        onClick = {
                            showMenu = false
                            onTogglePro()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isDisabled) "Enable Tool" else "Disable Tool", color = Color.White) },
                        leadingIcon = { Icon(if (isDisabled) Icons.Default.CheckCircle else Icons.Default.Block, contentDescription = null, tint = PrimaryRed) },
                        onClick = {
                            showMenu = false
                            onToggleDisabled()
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PUSH NOTIFICATIONS TAB
// -------------------------------------------------------------
@Composable
fun AdminNotificationsTab(
    notifications: List<com.example.data.local.NotificationEntity>,
    onSend: (String, String, String) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("all") } // all, free, pro

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Broadcast Push Notification", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Notification Title (e.g., New AI Tool Released!)", color = TextDisabled, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = SurfaceLighter,
                        unfocusedContainerColor = SurfaceLighter
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Notification message body...", color = TextDisabled, fontSize = 12.sp) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = SurfaceLighter,
                        unfocusedContainerColor = SurfaceLighter
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("all" to "All Users", "free" to "Free Users", "pro" to "Pro Users").forEach { (aud, label) ->
                        FilterChip(
                            selected = audience == aud,
                            onClick = { audience = aud },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryRed,
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceLighter,
                                labelColor = TextMuted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            onSend(title, message, audience)
                            title = ""
                            message = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Broadcast Push Notification", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Broadcast History", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
            if (notifications.isNotEmpty()) {
                TextButton(onClick = onDelete) {
                    Text("Clear All", color = PrimaryRed, fontSize = 11.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notifications, key = { it.id }) { notif ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            BadgeBox(notif.audience.uppercase(), PrimaryRed)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notif.message, fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(formatDate(notif.createdAt), fontSize = 10.sp, color = TextDisabled)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// AUDIT LOG TAB
// -------------------------------------------------------------
@Composable
fun AdminAuditTab(auditLogs: List<AdminAuditLogEntity>) {
    if (auditLogs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No admin audit records yet.", color = TextMuted, fontSize = 13.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(auditLogs, key = { it.id }) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(log.action, fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFFFFD700))
                            Text(formatDate(log.timestamp), fontSize = 10.sp, color = TextDisabled)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(log.details, fontSize = 12.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Target: ${log.target} • Admin: ${log.adminEmail}", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeBox(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(timestamp))
}
