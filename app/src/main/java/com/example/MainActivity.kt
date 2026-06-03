package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: KulinaViewModel by viewModels {
        val app = application as KulinaMitraApplication
        KulinaViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val userSession by viewModel.userSession.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                    if (userSession == null) {
                        LoginScreen(viewModel = viewModel)
                    } else {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("main_scaffold"),
                            bottomBar = {
                                // Show bottom navigation on primary screens
                                if (currentScreen == Screen.Home ||
                                    currentScreen == Screen.Outlet ||
                                    currentScreen == Screen.Order ||
                                    currentScreen == Screen.Chat ||
                                    currentScreen == Screen.Support
                                ) {
                                    KulinaBottomNavigation(
                                        currentScreen = currentScreen,
                                        onSelectScreen = { viewModel.navigateTo(it) }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                AnimatedContent(
                                    targetState = currentScreen,
                                    transitionSpec = {
                                        fadeIn() togetherWith fadeOut()
                                    },
                                    label = "screen_transition"
                                ) { screen ->
                                    when (screen) {
                                        Screen.Home -> HomeScreen(viewModel = viewModel)
                                        Screen.Info -> InfoScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Order -> OrderScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.OrderHistory -> OrderScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Outlet -> OutletScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Finance -> FinanceScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Cctv -> CctvScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Meeting -> MeetingScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Promo -> PromoScreen(onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Reward -> RewardScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Chat -> ChatScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Support -> SupportScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Feedback -> FeedbackScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Support) })
                                        Screen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Home) })
                                        Screen.Faq -> FaqScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(Screen.Support) })
                                    }
                                }
                            }
                        }
                    }

                    // Floating In-App Toast Overlays (visible everywhere)
                    InAppNotificationOverlay(
                        viewModel = viewModel,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun KulinaBottomNavigation(
    currentScreen: Screen,
    onSelectScreen: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White),
        color = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // MANDATORY constraint to avoid gestural notch conflict!
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Beranda / Home Tab
            BottomNavItem(
                label = "Beranda",
                icon = Icons.Default.Home,
                active = currentScreen == Screen.Home,
                onClick = { onSelectScreen(Screen.Home) },
                modifier = Modifier.weight(1f).testTag("nav_home")
            )

            // Outlet Tab
            BottomNavItem(
                label = "Outlet",
                icon = Icons.Default.LocationOn,
                active = currentScreen == Screen.Outlet,
                onClick = { onSelectScreen(Screen.Outlet) },
                modifier = Modifier.weight(1f).testTag("nav_outlet")
            )

            // Center custom Floating Button shape Order
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectScreen(Screen.Order) }
                    .offset(y = (-10).dp)
                    .testTag("nav_order_fab"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(KulinaPurple)
                        .border(3.dp, Color.White, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Order",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Order",
                    fontSize = 9.sp,
                    color = if (currentScreen == Screen.Order) KulinaPurple else KulinaTextMuted,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Chat Tab
            BottomNavItem(
                label = "Chat",
                icon = Icons.Default.Email, // chat symbol
                active = currentScreen == Screen.Chat,
                onClick = { onSelectScreen(Screen.Chat) },
                modifier = Modifier.weight(1f).testTag("nav_chat")
            )

            // Akun / Support Tab
            BottomNavItem(
                label = "Akun",
                icon = Icons.Default.Person,
                active = currentScreen == Screen.Support,
                onClick = { onSelectScreen(Screen.Support) },
                modifier = Modifier.weight(1f).testTag("nav_support")
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) KulinaPurple else KulinaTextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (active) KulinaPurple else KulinaTextMuted,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InAppNotificationOverlay(
    viewModel: KulinaViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            notifications.forEach { notification ->
                key(notification.id) {
                    InAppNotificationItem(
                        notification = notification,
                        onDismiss = { viewModel.dismissNotification(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun InAppNotificationItem(
    notification: InAppNotification,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .border(
                    width = 1.5.dp,
                    color = when (notification.type) {
                        NotificationType.ORDER_PLACED -> KulinaPurple.copy(alpha = 0.4f)
                        NotificationType.PAYMENT_CONFIRMED -> KulinaGreen.copy(alpha = 0.4f)
                        NotificationType.INFO -> KulinaBlue.copy(alpha = 0.4f)
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    visible = false
                    onDismiss()
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            when (notification.type) {
                                NotificationType.ORDER_PLACED -> KulinaPurple.copy(alpha = 0.12f)
                                NotificationType.PAYMENT_CONFIRMED -> KulinaGreen.copy(alpha = 0.12f)
                                NotificationType.INFO -> KulinaBlue.copy(alpha = 0.12f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (notification.type) {
                            NotificationType.ORDER_PLACED -> Icons.Default.ShoppingCart
                            NotificationType.PAYMENT_CONFIRMED -> Icons.Default.CheckCircle
                            NotificationType.INFO -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when (notification.type) {
                            NotificationType.ORDER_PLACED -> KulinaPurple
                            NotificationType.PAYMENT_CONFIRMED -> KulinaGreen
                            NotificationType.INFO -> KulinaBlue
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = KulinaText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = notification.message,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        color = KulinaTextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        visible = false
                        onDismiss()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = KulinaTextMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
