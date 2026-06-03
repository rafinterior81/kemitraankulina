package com.example.ui

import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

import androidx.compose.ui.draw.alpha

data class PromoSlide(
    val title: String,
    val desc: String,
    val badge: String,
    val gradient: List<Color>,
    val imageResId: Int? = null
)

@Composable
fun HomeScreen(
    viewModel: KulinaViewModel
) {
    val profile by viewModel.profile.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    val assignedOutlet = userSession?.outletName ?: "Semua"
    val isMitra = userSession?.role == "MITRA"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        // TOPBAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KulinaPurple)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_kulina_logo_1780500709785),
                contentDescription = "Kulina Logo",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, KulinaYellow, CircleShape)
                    .background(Color.White),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Kuli",
                        color = KulinaYellow,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "na",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                }
                Text(
                    text = "Mitra App",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Notif badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    // Mini notification Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(KulinaYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurpleDark
                        )
                    }
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(KulinaYellow)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isMitra) "MT" else "AD",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = KulinaPurpleDark
                    )
                }

                // Symmetrical Logout Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { viewModel.logout() }
                        .testTag("logout_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Log Out",
                        tint = KulinaYellow,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // HERO GREETINGS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(KulinaPurple, KulinaPurpleLight)
                    )
                )
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Selamat datang,",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isMitra) "Mitra $assignedOutlet 👋" else "Admin Pusat 👋",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Omset Hari Ini
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isMitra) "Rp 1,4Jt" else "Rp 4,2Jt",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaYellow
                            )
                            Text(
                                text = "Omset Hari Ini",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Active outlets
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isMitra) "1 Outlet" else "3 Outlet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaYellow
                            )
                            Text(
                                text = "Aktif Beroperasi",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // SCROLLABLE BODY
        var searchQuery by remember { mutableStateOf("") }
        val context = androidx.compose.ui.platform.LocalContext.current

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // SLIDING HERO PROMOTIONAL BANNER CAROUSEL
            val promoSlides = listOf(
                PromoSlide(
                    title = "Kenyalnya Juara!",
                    desc = "Somay, Batagor, Ekado, Pempek 100% Ikan Pilihan asli Kulina.",
                    badge = "NEW BRAND",
                    gradient = emptyList(),
                    imageResId = com.example.R.drawable.img_kulina_banner_wide_1780500732294
                ),
                PromoSlide(
                    title = "Kulina Premium Partnership",
                    desc = "Pasokan Bahan Baku Segar & Terjamin 100% langsung dari gudang pusat.",
                    badge = "OFFICIAL",
                    gradient = listOf(KulinaOrange, KulinaYellow)
                ),
                PromoSlide(
                    title = "Promo Cashback Rp 50.000",
                    desc = "Dapatkan cashback langsung dan poin loyalty 2x lipat untuk setiap pemesanan somay ayam.",
                    badge = "MEI SALE",
                    gradient = listOf(KulinaPurpleDark, KulinaPurple)
                ),
                PromoSlide(
                    title = "Free Delivery Seluruh Outlet",
                    desc = "Gratis ongkos kirim ke seluruh area kemitraan Kulina Jabodetabek.",
                    badge = "FREE ONGKIR",
                    gradient = listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
                )
            )

            var currentSlideIndex by remember { mutableStateOf(0) }

            // Auto-advance sliding timer effect
            LaunchedEffect(Unit) {
                while (true) {
                    kotlinx.coroutines.delay(4000)
                    currentSlideIndex = (currentSlideIndex + 1) % promoSlides.size
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("promo_carousel")
            ) {
                val activeSlide = promoSlides[currentSlideIndex]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable { viewModel.navigateTo(Screen.Promo) },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, KulinaBorder)
                ) {
                    if (activeSlide.imageResId != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = androidx.compose.ui.res.painterResource(id = activeSlide.imageResId),
                                contentDescription = "Promo Banner Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            // Transparent float overlay badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = activeSlide.badge,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(activeSlide.gradient))
                        ) {
                            // Background pattern overlay
                            Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_kln_banner_1780392758252),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(0.12f),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )

                            // Visual decorative floating circles
                            Box(
                                modifier = Modifier
                                    .absoluteOffset(x = 240.dp, y = (-20).dp)
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                            )
                            Box(
                                modifier = Modifier
                                    .absoluteOffset(x = 290.dp, y = 40.dp)
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = activeSlide.badge,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    
                                    // Direct Navigation manual trigger
                                    Text(
                                        text = "Detail Promo ➜",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier
                                            .clickable { viewModel.navigateTo(Screen.Promo) }
                                            .padding(4.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = activeSlide.title,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = activeSlide.desc,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 12.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Indicator Dots at the bottom center of the Carousel card
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    promoSlides.forEachIndexed { idx, _ ->
                        val isSelected = currentSlideIndex == idx
                        Box(
                            modifier = Modifier
                                .size(width = if (isSelected) 14.dp else 6.dp, height = 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.4f))
                                .clickable { currentSlideIndex = idx }
                        )
                    }
                }
            }

            // SEARCH INPUT FIELD (HEADER BERANDA INPUT)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .testTag("home_search_input"),
                placeholder = {
                    Text(
                        text = "Cari produk, order, cctv, dll...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = KulinaPurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = KulinaPurpleLight
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KulinaPurple,
                    unfocusedBorderColor = KulinaBorder,
                    focusedContainerColor = KulinaCardBg,
                    unfocusedContainerColor = KulinaCardBg,
                    focusedTextColor = KulinaText,
                    unfocusedTextColor = KulinaText
                )
            )

            // Dynamic Search Shortcuts / Context matching based on user typing
            if (searchQuery.trim().isNotEmpty()) {
                val query = searchQuery.lowercase()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, KulinaOrange),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = KulinaOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pencarian Fitur Terkait:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurpleDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Suggest actions based on search input keywords
                        if (query.contains("som") || query.contains("siom") || query.contains("bahan") || query.contains("order") || query.contains("beli") || query.contains("batagor") || query.contains("pempek")) {
                            ListItemShortcut(
                                label = "Order Bahan Baku (Somay, Batagor, dll)",
                                onClick = { viewModel.navigateTo(Screen.Order) }
                            )
                        }
                        if (query.contains("cctv") || query.contains("kamera") || query.contains("pantau")) {
                            ListItemShortcut(
                                label = "Pantau Area CCTV Dapur Mitra",
                                onClick = { viewModel.navigateTo(Screen.Cctv) }
                            )
                        }
                        if (query.contains("duit") || query.contains("untung") || query.contains("resep") || query.contains("keuangan") || query.contains("lapor")) {
                            ListItemShortcut(
                                label = "Buka Laporan Keuangan",
                                onClick = { viewModel.navigateTo(Screen.Finance) }
                            )
                        }
                        if (query.contains("hadiah") || query.contains("poin") || query.contains("reward") || query.contains("tuker")) {
                            ListItemShortcut(
                                label = "Tukar Poin Loyalty Reward",
                                onClick = { viewModel.navigateTo(Screen.Reward) }
                            )
                        }
                        if (query.contains("admin") || query.contains("kelola") || query.contains("manage") || query.contains("password")) {
                            ListItemShortcut(
                                label = "Dashboard Admin - Kelola Bahan Baku",
                                onClick = { viewModel.navigateTo(Screen.AdminDashboard) }
                            )
                        }
                        
                        // Default general suggestion to order
                        if (query.isNotEmpty()) {
                            Text(
                                text = "Menampilkan hasil pintar untuk \"$searchQuery\". Klik menu relevan di bawah untuk navigasi cepat.",
                                fontSize = 11.sp,
                                color = KulinaTextMuted,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                            )
                        }
                    }
                }
            }

            // Main Menus
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = KulinaPurpleLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Menu Utama",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
            }

            // Grid of Menu Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MenuTile(
                        title = "Info Mitra",
                        icon = Icons.Default.Info,
                        colorType = "purple",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Info) }
                    )
                    MenuTile(
                        title = "Order Bahan",
                        icon = Icons.Default.ShoppingCart,
                        colorType = "orange",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Order) }
                    )
                    MenuTile(
                        title = "Outlet",
                        icon = Icons.Default.LocationOn,
                        colorType = "green",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Outlet) }
                    )
                    MenuTile(
                        title = "Keuangan",
                        icon = Icons.Default.Lock, // secure finance lock/ledger
                        colorType = "yellow",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Finance) }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MenuTile(
                        title = "CCTV Live",
                        icon = Icons.Default.PlayArrow, // play/stream video icon
                        colorType = "purple",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Cctv) }
                    )
                    MenuTile(
                        title = "Meeting",
                        icon = Icons.Default.Phone, // device placeholder
                        colorType = "orange",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Meeting) }
                    )
                    MenuTile(
                        title = "Reward",
                        icon = Icons.Default.Star, // star reward placeholder
                        colorType = "yellow",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Reward) }
                    )
                    MenuTile(
                        title = "Support",
                        icon = Icons.Default.Person, // user support/account helper icon
                        colorType = "green",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Support) }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MenuTile(
                        title = "Admin Panel 🔑",
                        icon = Icons.Default.Settings,
                        colorType = "black", // Modern styling for admin entry
                        modifier = Modifier.fillMaxWidth(),
                        isWide = true,
                        onClick = { viewModel.navigateTo(Screen.AdminDashboard) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SPECIAL PROMO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(KulinaOrange, KulinaYellow)
                        )
                    )
                    .clickable { viewModel.navigateTo(Screen.Promo) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications, // megaphone placeholder
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Promo Spesial Mei!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurpleDark
                        )
                        Text(
                            text = "Diskon 20% bahan baku ekado & pempek",
                            fontSize = 11.sp,
                            color = KulinaPurpleDark.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(KulinaPurple, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2 promo\naktif",
                            fontSize = 10.sp,
                            color = KulinaYellow,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // GROWTH CHART
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock, // stats chart placeholder
                    contentDescription = null,
                    tint = KulinaPurpleLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Grafik Pertumbuhan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, KulinaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Omset Mingguan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaText
                        )
                        Box(
                            modifier = Modifier
                                .background(KulinaBg, RoundedCornerShape(8.dp))
                                .border(1.dp, KulinaBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "7 Hari ▾",
                                fontSize = 11.sp,
                                color = KulinaPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SimpleBarChart(
                        data = listOf(3.2f, 4.1f, 3.8f, 4.5f, 3.9f, 4.8f, 4.2f),
                        labels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"),
                        highlightIndex = 6
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = KulinaBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rp 28,4Jt",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurple
                            )
                            Text(
                                text = "Total Minggu Ini",
                                fontSize = 10.sp,
                                color = KulinaTextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "▲ 12% vs minggu lalu",
                                fontSize = 10.sp,
                                color = KulinaGreen,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "247",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurple
                            )
                            Text(
                                text = "Total Transaksi",
                                fontSize = 10.sp,
                                color = KulinaTextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "▲ 8% vs minggu lalu",
                                fontSize = 10.sp,
                                color = KulinaGreen,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // NEAREST OUTLETS
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = KulinaPurpleLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Outlet Terdekat",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutletShortRow(
                name = "Kulina Kelapa Gading",
                loc = "Jakarta Utara",
                revenue = "Rp 1,8Jt",
                isGreen = true,
                onClick = { viewModel.navigateTo(Screen.Outlet) }
            )

            OutletShortRow(
                name = "Kulina Cibubur",
                loc = "Jakarta Timur",
                revenue = "Rp 1,4Jt",
                isGreen = true,
                onClick = { viewModel.navigateTo(Screen.Outlet) }
            )

            OutletShortRow(
                name = "Kulina Serpong",
                loc = "Tangerang Selatan",
                revenue = "Rp 980Rb",
                isGreen = false,
                onClick = { viewModel.navigateTo(Screen.Outlet) }
            )

            Spacer(modifier = Modifier.height(100.dp)) // padding for bottom menu offset
        }
    }
}

@Composable
fun MenuTile(
    title: String,
    icon: ImageVector,
    colorType: String,
    modifier: Modifier = Modifier,
    isWide: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundBrush = when (colorType) {
        "purple" -> Brush.verticalGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
        "orange" -> Brush.verticalGradient(listOf(Color(0xFFFF416C), Color(0xFFFF4B2B)))
        "green" -> Brush.verticalGradient(listOf(Color(0xFF11998E), Color(0xFF38EF7D)))
        "yellow" -> Brush.verticalGradient(listOf(Color(0xFFF1C40F), Color(0xFFF39C12)))
        "black" -> Brush.linearGradient(listOf(Color(0xFF141E30), Color(0xFF243B55)))
        else -> Brush.verticalGradient(listOf(KulinaPurple, KulinaPurpleLight))
    }

    Card(
        modifier = modifier
            .then(if (isWide) Modifier.fillMaxWidth().height(80.dp) else Modifier.aspectRatio(1.05f))
            .clickable(onClick = onClick)
            .testTag("menu_tile_${title.lowercase().replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, KulinaBorder)
    ) {
        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(backgroundBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaText
                    )
                    Text(
                        text = "Kelola data mitra, ubah produk, pantau transaksi real-time",
                        fontSize = 10.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = KulinaTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaText,
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun OutletShortRow(
    name: String,
    loc: String,
    revenue: String,
    isGreen: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, KulinaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored status dot indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isGreen) KulinaGreen else KulinaYellow)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaText
                )
                Text(
                    text = loc,
                    fontSize = 11.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = revenue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurple
                )
                Text(
                    text = "hari ini",
                    fontSize = 10.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ListItemShortcut(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = KulinaPurple,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = KulinaPurpleDark,
            modifier = Modifier.weight(1f)
        )
    }
}

