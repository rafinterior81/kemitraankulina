package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures

@Composable
fun InnerHeader(
    title: String,
    onBack: () -> Unit,
    trailingContent: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KulinaPurple)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(36.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                .testTag("header_back_button")
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (trailingContent != null) {
            trailingContent()
        }
    }
}

@Composable
fun SimpleBarChart(
    data: List<Float>,
    labels: List<String>,
    highlightIndex: Int = -1,
    modifier: Modifier = Modifier
) {
    val maxVal = if (data.isNotEmpty()) data.maxOrNull() ?: 1f else 1f
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { i, value ->
            val ratio = value / maxVal
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bar Card/Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(ratio.coerceAtLeast(0.08f))
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(
                            if (i == highlightIndex) KulinaYellow else KulinaPurple.copy(alpha = 0.6f)
                        )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = labels.getOrElse(i) { "" },
                    color = KulinaTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun OrderSummaryCard(
    order: BahanBakuOrder,
    viewModel: KulinaViewModel? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val dateStr = sdf.format(Date(order.orderDate))
    val grandTotal = order.totalPrice + order.shippingFee

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, KulinaBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID Order: #${order.id}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = KulinaPurple
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Paid Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (order.isPaid) KulinaGreen.copy(alpha = 0.15f)
                                else KulinaRed.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (order.isPaid) "Lunas ✅" else "Belum Bayar ❌",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (order.isPaid) { KulinaGreen } else { KulinaRed }
                        )
                    }

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (order.status) {
                                    "Selesai" -> KulinaGreen.copy(alpha = 0.15f)
                                    "Dikirim" -> KulinaOrange.copy(alpha = 0.15f)
                                    else -> KulinaPurple.copy(alpha = 0.1f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = order.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = when (order.status) {
                                "Selesai" -> KulinaGreen
                                "Dikirim" -> KulinaOrange
                                else -> KulinaPurple
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.itemsJson,
                fontSize = 12.sp,
                color = KulinaText,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Highlight shipping details & payment method
            if (order.paymentMethod.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Metode: ${order.paymentMethod}",
                        fontSize = 11.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ongkir: Rp " + String.format("%,d", order.shippingFee).replace(',', '.'),
                        fontSize = 11.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(color = KulinaBorder, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Total: Rp " + String.format("%,d", grandTotal).replace(',', '.'),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaText
                )
            }

            // Client payments confirmation trigger
            if (!order.isPaid && viewModel != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val updatedOrder = order.copy(isPaid = true)
                        viewModel.updateOrder(updatedOrder)
                        
                        // Show beautiful notification notice
                        android.widget.Toast.makeText(
                            context,
                            "Konfirmasi Pembayaran Rp " + String.format("%,d", grandTotal).replace(',', '.') + " Berhasil Dikirim! Admin akan segera memproses.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KulinaOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Konfirmasi Pembayaran Anda",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun InfoScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "Informasi Kemitraan", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Welcome Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(KulinaPurple, Color(0xFF9B59B6))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "KULINA",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaYellow
                    )

                    Text(
                        text = "Somay • Batagor • Ekado • Pempek",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaYellow.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"Kenyalnya Juara, Rasanya Bikin Nagih!\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Partner Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, KulinaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = KulinaPurpleLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Detail Mitra",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(label = "Nama Mitra", value = profile?.name ?: "Budi Santoso")
                    DetailRow(label = "ID Mitra", value = profile?.id ?: "KLN-2024-0317", highlight = true)
                    DetailRow(label = "Bergabung", value = "12 Maret 2024")
                    DetailRow(label = "Status", value = "● Aktif", valueColor = KulinaGreen)
                    DetailRow(label = "Paket", value = profile?.level ?: "Gold Partner", highlight = true)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Partnership Package Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, KulinaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = KulinaPurpleLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Paket Kemitraan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, KulinaPurple, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "🥇 Paket Gold",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurple
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                               text = "Rp 15Jt",
                               fontSize = 20.sp,
                               fontWeight = FontWeight.Black,
                               color = KulinaText
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                               text = "/ investasi awal",
                               fontSize = 12.sp,
                               color = KulinaTextMuted,
                               fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val features = listOf(
                            "3 varian produk (somay, batagor, ekado)",
                            "Gerobak & perlengkapan lengkap",
                            "Pelatihan 7 hari di pusat",
                            "Akses aplikasi mitra",
                            "Dukungan marketing 3 bulan"
                        )

                        features.forEach { feat ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = KulinaGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = feat,
                                    fontSize = 11.sp,
                                    color = KulinaTextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Office Contact
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, KulinaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = KulinaPurpleLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kontak Pusat",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(label = "Hotline", value = "0800-KULINA", highlight = true)
                    DetailRow(label = "WhatsApp", value = "+62 812-xxxx-xxxx", highlight = true)
                    DetailRow(label = "Email", value = "mitra@kulina.co.id")
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    highlight: Boolean = false,
    valueColor: Color = KulinaText
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = KulinaTextMuted,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = value,
            fontSize = 12.sp,
            color = if (highlight) KulinaPurple else valueColor,
            fontWeight = if (highlight) FontWeight.Black else FontWeight.Bold
        )
    }
    Divider(color = KulinaBorder)
}

@Composable
fun OrderScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val quantities by viewModel.quantities.collectAsState()
    val totalPrice by viewModel.totalOrderPrice.collectAsState()
    val totalCount by viewModel.selectedItemsCount.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val productList by viewModel.products.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Pesan, 1 = Riwayat
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var selectedShippingFee by remember { mutableStateOf(15000L) }
    var selectedPaymentMethod by remember { mutableStateOf("Transfer Bank") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(
            title = "Order Bahan Baku",
            onBack = onBack,
            trailingContent = {
                // Dynamic badge indicating orders count
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(KulinaYellow)
                        .clickable { activeTab = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Riwayat",
                        tint = KulinaPurpleDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        )

        // Custom tabs row for Purchase vs Order History (efficiency!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KulinaPurple)
                .padding(bottom = 8.dp)
        ) {
            TabButton(
                text = "Pesan Bahan",
                active = activeTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { activeTab = 0 }
            )
            TabButton(
                text = "Riwayat Order",
                active = activeTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { activeTab = 1 }
            )
        }

        if (activeTab == 0) {
            // Cart selection screen
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Pilih Produk / Bahan Baku",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp)
                ) {
                    items(productList.size) { index ->
                        val product = productList[index]
                        val qty = quantities[product.id] ?: 0

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .testTag("product_card_${product.id}"),
                            colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, KulinaBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Product image/graphic design background
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(KulinaBorder),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val imageResId = when (product.imageResType) {
                                        "somay" -> com.example.R.drawable.img_somay_1780392776236
                                        "batagor" -> com.example.R.drawable.img_batagor_1780392793000
                                        "pempek" -> com.example.R.drawable.img_pempek_1780392810244
                                        else -> com.example.R.drawable.img_ingredients_1780392830223
                                    }
                                    Image(
                                        painter = androidx.compose.ui.res.painterResource(id = imageResId),
                                        contentDescription = product.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaText
                                    )
                                    Text(
                                        text = product.unit,
                                        fontSize = 11.sp,
                                        color = KulinaTextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Rp " + String.format("%,d", product.price).replace(',', '.'),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaPurple
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.updateQuantity(product.id, -1) },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(KulinaBg, RoundedCornerShape(8.dp))
                                            .border(1.5.dp, KulinaBorder, RoundedCornerShape(8.dp))
                                            .testTag("qty_minus_${product.id}")
                                    ) {
                                        Text("−", fontSize = 16.sp, fontWeight = FontWeight.Black, color = KulinaPurple)
                                    }

                                    Text(
                                        text = qty.toString(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaText,
                                        modifier = Modifier
                                            .widthIn(min = 20.dp)
                                            .testTag("qty_label_${product.id}"),
                                        textAlign = TextAlign.Center
                                    )

                                    IconButton(
                                        onClick = { viewModel.updateQuantity(product.id, 1) },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(KulinaPurple, RoundedCornerShape(8.dp))
                                            .testTag("qty_plus_${product.id}")
                                    ) {
                                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Absolute Bottom Cart/Order action bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(BorderStroke(1.5.dp, KulinaBorder))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Pesanan",
                            fontSize = 11.sp,
                            color = KulinaTextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Rp " + String.format("%,d", totalPrice).replace(',', '.'),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurple,
                            modifier = Modifier.testTag("order_total_value")
                        )
                    }

                    Button(
                        onClick = {
                            if (totalCount > 0) {
                                showCheckoutDialog = true
                            } else {
                                android.widget.Toast.makeText(context, "Pilih item terlebih dahulu", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KulinaYellow),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("place_order_button")
                    ) {
                        Text(
                            text = "Pesan Sekarang →",
                            color = KulinaPurpleDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

        } else {
            // ORDER HISTORY (Efficiency tracking!)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = KulinaPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Kelola & Pantau Pesanan Bahan Baku",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada pesanan bahan baku.",
                            color = KulinaTextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(orders.size) { i ->
                            OrderSummaryCard(order = orders[i], viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Order & Pembayaran 💵",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Selesaikan pesanan bahan baku Anda dengan mudah & cepat.",
                        fontSize = 11.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    // Order Summary Breakdown
                    Card(
                        colors = CardDefaults.cardColors(containerColor = KulinaBg),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Ringkasan Belanja",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KulinaPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val itemsSelected = quantities.filter { it.value > 0 }
                            itemsSelected.forEach { (id, qty) ->
                                val prod = productList.find { it.id == id }
                                if (prod != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${prod.name} x$qty", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = KulinaText)
                                        Text("Rp ${String.format("%,d", prod.price * qty).replace(',', '.')}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KulinaText)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Divider(color = KulinaBorder)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Bahan Baku", fontSize = 11.sp, fontWeight = FontWeight.Black, color = KulinaText)
                                Text("Rp ${String.format("%,d", totalPrice).replace(',', '.')}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = KulinaText)
                            }
                        }
                    }

                    // 1. SHIPPING FEE SELECTOR (Ongkos Kirim)
                    Text(
                        text = "1. Pilih Metode Pengiriman 🚚",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurple
                    )
                    
                    val shippingOptions = listOf(
                        Triple(15000L, "Reguler (2-3 hari)", "Estimasi reguler hemat"),
                        Triple(25000L, "Ekspres (1 hari)", "Pengiriman cepat darurat"),
                        Triple(35000L, "Same-Day (6-8 jam)", "Langsung dikirim Instan")
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        shippingOptions.forEach { (fee, title, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedShippingFee == fee) KulinaPurple.copy(alpha = 0.08f) else Color.Transparent)
                                    .border(
                                        width = if (selectedShippingFee == fee) 1.5.dp else 1.dp,
                                        color = if (selectedShippingFee == fee) KulinaPurple else KulinaBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedShippingFee = fee }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedShippingFee == fee,
                                    onClick = { selectedShippingFee = fee },
                                    colors = RadioButtonDefaults.colors(selectedColor = KulinaPurple)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Black, color = KulinaText)
                                    Text(text = desc, fontSize = 9.sp, color = KulinaTextMuted, fontWeight = FontWeight.SemiBold)
                                }
                                Text(
                                    text = "Rp ${String.format("%,d", fee).replace(',', '.')}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KulinaPurpleDark
                                )
                            }
                        }
                    }

                    // 2. PAYMENT METHODS SELECTOR
                    Text(
                        text = "2. Cara Pembayaran 💳",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurple
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Transfer Bank", "QRIS").forEach { method ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedPaymentMethod == method) KulinaPurple.copy(alpha = 0.12f) else KulinaBg)
                                    .border(
                                        width = if (selectedPaymentMethod == method) 2.dp else 1.dp,
                                        color = if (selectedPaymentMethod == method) KulinaPurple else KulinaBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedPaymentMethod = method },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = method,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedPaymentMethod == method) KulinaPurpleDark else KulinaText
                                )
                            }
                        }
                    }

                    // Payment Instructions or QR display
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBFE)),
                        border = BorderStroke(1.dp, KulinaBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (selectedPaymentMethod == "Transfer Bank") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🏦", fontSize = 20.sp)
                                    Column {
                                        Text(text = "Nomor Rekening Kulina Mitra", fontSize = 11.sp, fontWeight = FontWeight.Black, color = KulinaPurpleDark)
                                        Text(text = "BCA: 864-0192-837", fontSize = 12.sp, fontWeight = FontWeight.Black, color = KulinaOrange)
                                        Text(text = "Mandiri: 102-0055-1234", fontSize = 12.sp, fontWeight = FontWeight.Black, color = KulinaOrange)
                                        Text(text = "a.n. PT Kulina Mitra Nusantara", fontSize = 9.sp, color = KulinaTextMuted, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            } else {
                                // Dynamic beautiful styled QR Code layout! (Gives QRIS style)
                                Text(text = "QRIS INSTANT PAY 📲", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // Draw a beautiful visual mockup of a QR code using Canvas/Grid
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color.White)
                                        .border(2.dp, Color.Black)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Custom 2D pattern representation
                                    Canvas(modifier = Modifier.size(80.dp)) {
                                        val sizeBox = 80f / 5f
                                        // Top-left finder pattern
                                        drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(3 * sizeBox, 3 * sizeBox))
                                        drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(sizeBox, sizeBox), size = androidx.compose.ui.geometry.Size(sizeBox, sizeBox))
                                        // Top-right finder pattern
                                        drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(4 * sizeBox, 0f), size = androidx.compose.ui.geometry.Size(2 * sizeBox, 2 * sizeBox))
                                        // Bottom-left finder pattern
                                        drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(0f, 4 * sizeBox), size = androidx.compose.ui.geometry.Size(2 * sizeBox, 2 * sizeBox))
                                        // Some random QR noise blocks
                                        drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(2 * sizeBox, 3 * sizeBox), size = androidx.compose.ui.geometry.Size(sizeBox, sizeBox))
                                        drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(3 * sizeBox, 2 * sizeBox), size = androidx.compose.ui.geometry.Size(sizeBox, sizeBox))
                                        drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(4 * sizeBox, 4 * sizeBox), size = androidx.compose.ui.geometry.Size(sizeBox, sizeBox))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Silakan screenshot & scan QR di atas di aplikasi e-wallet Anda.",
                                    fontSize = 8.sp,
                                    color = KulinaTextMuted,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Grand Total Billing
                    val grandTotalPrice = totalPrice + selectedShippingFee
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KulinaPurple.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Tagihan", fontSize = 10.sp, color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                "Rp ${String.format("%,d", grandTotalPrice).replace(',', '.')}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurple
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(KulinaOrange)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Belum Lunas", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitOrder(selectedShippingFee, selectedPaymentMethod) {
                            showCheckoutDialog = false
                            android.widget.Toast.makeText(context, "Order dibuat! Konfirmasi pembayaran Anda jika sudah bayar.", android.widget.Toast.LENGTH_LONG).show()
                            activeTab = 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Konfirmasi & Buat Pesanan", fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutDialog = false }) {
                    Text("Batal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KulinaPurple)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun TabButton(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) KulinaYellow else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (active) KulinaPurpleDark else Color.White,
            fontWeight = if (active) FontWeight.Black else FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun AdminTabButton(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) KulinaPurple.copy(alpha = 0.15f) else Color.Transparent)
            .border(
                width = if (active) 1.5.dp else 1.dp,
                color = if (active) KulinaPurple else KulinaBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (active) KulinaPurpleDark else KulinaText,
            fontWeight = if (active) FontWeight.Black else FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun OutletScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "Monitoring Outlet", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Comparisons Chart Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                            text = "Perbandingan Omset Outlet",
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
                                text = "Bulan Ini",
                                fontSize = 11.sp,
                                color = KulinaPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SimpleBarChart(
                        data = listOf(1.8f, 1.4f, 0.98f),
                        labels = listOf("Kel.Gading", "Cibubur", "Serpong"),
                        highlightIndex = 0
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Outlets detail List
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = KulinaPurpleLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Daftar Outlet (3 Aktif)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card list
            val outlets = listOf(
                Triple("Kulina Kelapa Gading", "Jl. Boulevard Kelapa Gading, Jakarta Utara", "BUKA" to "Rp 1,8Jt"),
                Triple("Kulina Cibubur", "Jl. Alternatif Cibubur, Jakarta Timur", "BUKA" to "Rp 1,4Jt"),
                Triple("Kulina Serpong", "BSD City, Tangerang Selatan", "SEPI" to "Rp 980Rb")
            )

            outlets.forEach { outlet ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
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
                                text = outlet.first,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaText
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (outlet.third.first == "BUKA") Color(0xFFE8F8F0) else Color(0xFFFFFBEA)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = outlet.third.first,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (outlet.third.first == "BUKA") KulinaGreen else KulinaOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = outlet.second,
                            fontSize = 11.sp,
                            color = KulinaTextMuted,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(KulinaBg, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = outlet.third.second,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaPurple
                                    )
                                    Text(
                                        text = "Omset Hari Ini",
                                        fontSize = 10.sp,
                                        color = KulinaTextMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(KulinaBg, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = if (outlet.first.contains("Gading")) "89" else if (outlet.first.contains("Cibubur")) "72" else "51",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaPurple
                                    )
                                    Text(
                                        text = "Transaksi",
                                        fontSize = 10.sp,
                                        color = KulinaTextMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinanceScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val txns by viewModel.transactions.collectAsState()
    val userSession by viewModel.userSession.collectAsState()

    val posApiKey by viewModel.posApiKey.collectAsState()
    val webhookUrl by viewModel.webhookUrl.collectAsState()
    val webhookRegistered by viewModel.webhookRegistered.collectAsState()
    val isWebhookRegistering by viewModel.isWebhookRegistering.collectAsState()
    val posSyncLogs by viewModel.posSyncLogs.collectAsState()
    val cloudPosOmzet by viewModel.cloudPosOmzet.collectAsState()
    val isPosSyncing by viewModel.isPosSyncing.collectAsState()

    val assignedOutlet = userSession?.outletName ?: "Semua"
    val isMitra = userSession?.role == "MITRA"

    // Limit transactions based on roles:
    // Partners strictly see transactions from their own outlet + general expenses, but NO other outlet's transactions!
    val filteredTxns = if (!isMitra || assignedOutlet == "Semua") {
        txns
    } else {
        txns.filter { txn ->
            val otherOutlets = listOf("Kelapa Gading", "Cibubur", "Serpong").filter { it != assignedOutlet }
            val containsOtherOutlet = otherOutlets.any { separator -> txn.title.contains(separator, ignoreCase = true) }
            !containsOtherOutlet
        }
    }

    // Dynamic totals calculation
    val earnedToday = filteredTxns.filter { it.type == "in" }.sumOf { it.amount }
    val spentToday = filteredTxns.filter { it.type == "out" }.sumOf { it.amount }
    val netProfit = (earnedToday - spentToday / 2).coerceAtLeast(0)

    val daysOfWeek = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val chartData = when (assignedOutlet) {
        "Kelapa Gading" -> listOf(12.4f, 15.2f, 14.0f, 18.5f, 22.0f, 25.4f, 24.5f)
        "Cibubur" -> listOf(10.5f, 11.2f, 13.0f, 12.5f, 16.0f, 19.8f, 18.2f)
        "Serpong" -> listOf(9.0f, 10.1f, 9.8f, 12.0f, 14.5f, 17.0f, 16.5f)
        else -> listOf(31.9f, 36.5f, 36.8f, 43.0f, 52.5f, 62.2f, 59.2f) // All combined
    }
    val maxVal = (chartData.maxOrNull() ?: 10f).coerceAtLeast(1f)

    var showPdfProgress by remember { mutableStateOf(false) }
    var pdfProgressValue by remember { mutableStateOf(0f) }
    var pdfStepMessage by remember { mutableStateOf("") }
    var showPdfDialog by remember { mutableStateOf(false) }
    var hoveredChartIndex by remember { mutableStateOf(-1) }
    
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "Laporan Keuangan Realtime", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Main Card Summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(KulinaPurple, KulinaPurpleLight))
                    )
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMitra) "Total Omset Hari Ini (Outlet $assignedOutlet)" else "Total Omset Hari Ini (Seluruh Outlet)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (cloudPosOmzet > 0) {
                            Box(
                                modifier = Modifier
                                    .background(KulinaYellow.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+ Cloud POS Live",
                                    color = KulinaYellow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "Rp " + String.format("%,d", ((if (isMitra) 1450000 else 4180000) + earnedToday + cloudPosOmzet.toLong())).replace(',', '.'),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaYellow,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FinanceS_Item(
                            label = "Bulan Ini", 
                            value = when (assignedOutlet) {
                                "Kelapa Gading" -> "Rp 12,4Jt"
                                "Cibubur" -> "Rp 9,8Jt"
                                "Serpong" -> "Rp 8,1Jt"
                                else -> "Rp 30,3Jt"
                            }, 
                            modifier = Modifier.weight(1f)
                        )
                        FinanceS_Item(
                            label = "Overall Total", 
                            value = when (assignedOutlet) {
                                "Kelapa Gading" -> "Rp 148Jt"
                                "Cibubur" -> "Rp 112Jt"
                                "Serpong" -> "Rp 96Jt"
                                else -> "Rp 356Jt"
                            }, 
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FinanceS_Item(
                            label = "Keuangan Bersih Hari Ini",
                            value = "Rp " + String.format("%,d", (if (isMitra) 950000 else 2100000) + netProfit).replace(',', '.'),
                            modifier = Modifier.weight(1f)
                        )
                        FinanceS_Item(
                            label = "Today Transaksi",
                            value = "${filteredTxns.size} Trx",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PDF Download Trigger Section
            Button(
                onClick = {
                    scope.launch {
                        showPdfProgress = true
                        pdfProgressValue = 0.1f
                        pdfStepMessage = "Mengumpulkan transaksi outlet..."
                        delay(600)
                        
                        pdfProgressValue = 0.4f
                        pdfStepMessage = "Melakukan rekonsiliasi data keuangan..."
                        delay(600)
                        
                        pdfProgressValue = 0.7f
                        pdfStepMessage = "Merender grafik statistik omset..."
                        delay(600)
                        
                        pdfProgressValue = 0.9f
                        pdfStepMessage = "Mengekspor berkas ke PDF standard Kulina..."
                        delay(600)
                        
                        pdfProgressValue = 1.0f
                        pdfStepMessage = "Finalisasi berkas dan penandatanganan..."
                        delay(400)
                        
                        showPdfProgress = false
                        showPdfDialog = true
                        
                        viewModel.showNotification(
                            title = "Unduh Sukses 📄",
                            message = "laporan_keuangan_kulina_${assignedOutlet.lowercase().replace(" ", "_")}.pdf berhasil diunduh!",
                            type = NotificationType.INFO
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KulinaOrange, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("download_pdf_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share, // representing export/sheet action
                    contentDescription = "Unduh PDF",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ekspor & Unduh Laporan PDF",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // STATISTICAL CHART CARD (GRAFIK STATISTIK OMSET)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, KulinaBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Statistik Omset Mingguan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurpleDark
                            )
                            Text(
                                text = "Tren harian dalam satuan Juta Rupiah (Rp Jt)",
                                fontSize = 10.sp,
                                color = KulinaTextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(KulinaPurple.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = assignedOutlet,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurple
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Chart Graph drawing using Jetpack Compose Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(chartData) {
                                    detectTapGestures(
                                        onTap = { offset ->
                                            val stepX = size.width.toFloat() / (chartData.size - 1)
                                            hoveredChartIndex = (offset.x / stepX).toInt().coerceIn(0, chartData.size - 1)
                                        }
                                    )
                                }
                        ) {
                            val W = size.width
                            val H = size.height
                            val numPoints = chartData.size
                            val stepX = W / (numPoints - 1)

                            // 1. Draw horizontal grid lines
                            for (gridIdx in 0..3) {
                                val gridY = (H / 3) * gridIdx
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.3f),
                                    start = androidx.compose.ui.geometry.Offset(0f, gridY),
                                    end = androidx.compose.ui.geometry.Offset(W, gridY),
                                    strokeWidth = 2f
                                )
                            }

                            // 2. Compute path points
                            val trendPath = androidx.compose.ui.graphics.Path().apply {
                                val startY = H - (chartData[0] / maxVal) * H
                                moveTo(0f, startY)
                                for (i in 1 until numPoints) {
                                    val curX = i * stepX
                                    val curY = H - (chartData[i] / maxVal) * H
                                    lineTo(curX, curY)
                                }
                            }

                            // 3. Fill area under graph with elegant translucent gradient
                            val fillPath = androidx.compose.ui.graphics.Path().apply {
                                addPath(trendPath)
                                lineTo((numPoints - 1) * stepX, H)
                                lineTo(0f, H)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(KulinaPurple.copy(alpha = 0.35f), Color.Transparent),
                                    startY = 0f,
                                    endY = H
                                )
                            )

                            // 4. Draw curve line outline
                            drawPath(
                                path = trendPath,
                                color = KulinaPurple,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 6f,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            )

                            // 5. Draw data point node circles
                            for (i in 0 until numPoints) {
                                val curX = i * stepX
                                val curY = H - (chartData[i] / maxVal) * H
                                drawCircle(
                                    color = Color.White,
                                    radius = 6f,
                                    center = androidx.compose.ui.geometry.Offset(curX, curY)
                                )
                                drawCircle(
                                    color = KulinaPurple,
                                    radius = 6f,
                                    center = androidx.compose.ui.geometry.Offset(curX, curY),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                                )
                            }

                            // 6. Focus hover indicator guide line & tooltips
                            if (hoveredChartIndex in 0 until numPoints) {
                                val hX = hoveredChartIndex * stepX
                                val hY = H - (chartData[hoveredChartIndex] / maxVal) * H

                                drawLine(
                                    color = KulinaOrange.copy(alpha = 0.5f),
                                    start = androidx.compose.ui.geometry.Offset(hX, 0f),
                                    end = androidx.compose.ui.geometry.Offset(hX, H),
                                    strokeWidth = 3f
                                )

                                drawCircle(
                                    color = Color.White,
                                    radius = 12f,
                                    center = androidx.compose.ui.geometry.Offset(hX, hY)
                                )
                                drawCircle(
                                    color = KulinaOrange,
                                    radius = 12f,
                                    center = androidx.compose.ui.geometry.Offset(hX, hY),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // X-Axis day labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEachIndexed { idx, day ->
                            val isSelected = hoveredChartIndex == idx
                            Text(
                                text = day,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) KulinaOrange else KulinaTextMuted,
                                modifier = Modifier
                                    .clickable { hoveredChartIndex = idx }
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }

                    if (hoveredChartIndex in 0 until chartData.size) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(KulinaBg, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Hari ${daysOfWeek[hoveredChartIndex]} • Estimasi Pendapatan: Rp ${chartData[hoveredChartIndex]} Juta",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = KulinaPurpleDark
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 Tip: Klik hari atau titik di atas grafik untuk melihat rincian omset spesifik.",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KulinaTextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // CLOUD POS INTEGRATION CARD & REAL-TIME DEVELOPER PLAYGROUND
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .testTag("pos_cloud_integration_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, KulinaBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(KulinaPurple.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = KulinaPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Integrasi Cloud POS",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KulinaPurpleDark
                                )
                                Text(
                                    text = "Sinkronisasi Webhook & Keamanan API",
                                    fontSize = 10.sp,
                                    color = KulinaTextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Connected badge
                        Box(
                            modifier = Modifier
                                .background(
                                    if (webhookRegistered) Color(0xFFE8F8F0) else Color(0xFFFFF0F0),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            if (webhookRegistered) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                                            CircleShape
                                        )
                                )
                                Text(
                                    text = if (webhookRegistered) "TERKONEKSI" else "OFFLINE",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (webhookRegistered) KulinaGreen else KulinaRed
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // API Key Form
                    Text(
                        text = "Mitra API Secret Key (Token Keamanan)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = posApiKey,
                        onValueChange = { viewModel.updatePosApiKey(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pos_api_key_input"),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = KulinaPurple, modifier = Modifier.size(16.dp))
                        },
                        placeholder = { Text("Masukkan API Secret Key Mitra...", fontSize = 11.sp) },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KulinaPurple,
                            unfocusedBorderColor = KulinaBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Webhook URL Form
                    Text(
                        text = "Endpoint Webhook POS (Penyedia POS Cloud)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = webhookUrl,
                            onValueChange = { viewModel.updateWebhookUrl(it) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("webhook_url_input"),
                            leadingIcon = {
                                Icon(Icons.Default.Build, contentDescription = null, tint = KulinaPurple, modifier = Modifier.size(16.dp))
                            },
                            placeholder = { Text("https://api.kulina.com/webhook/pos-update", fontSize = 11.sp) },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = KulinaPurple,
                                unfocusedBorderColor = KulinaBorder
                            )
                        )

                        Button(
                            onClick = {
                                viewModel.registerWebhook(
                                    url = webhookUrl,
                                    description = "Webhook update omzet Mitra untuk outlet $assignedOutlet",
                                    secretKey = posApiKey
                                )
                            },
                            enabled = webhookUrl.isNotEmpty() && !isWebhookRegistering,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (webhookRegistered) KulinaGreen else KulinaPurple,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("register_webhook_button")
                        ) {
                            if (isWebhookRegistering) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(
                                    imageVector = if (webhookRegistered) Icons.Default.Check else Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (webhookRegistered) "Registered" else "Daftarkan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sync & Handshake trigger
                    Button(
                        onClick = {
                            viewModel.syncCloudPosOmzet(
                                outletId = if (assignedOutlet == "Semua") "Kelapa Gading" else assignedOutlet,
                                secretKey = posApiKey
                            )
                        },
                        enabled = !isPosSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = KulinaOrange, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sync_pos_button")
                    ) {
                        if (isPosSyncing) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tarik & Sync Omset POS Realtime",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Webhook Console Output Terminal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Developer Handshake Console",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurpleDark
                        )
                        Text(
                            text = "Clear Logs",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaOrange,
                            modifier = Modifier
                                .clickable { viewModel.clearPosLogs() }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1E1E))
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(posSyncLogs.size) { index ->
                                Text(
                                    text = posSyncLogs[index],
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF00FF66),
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Transactions List
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = KulinaPurple,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Daftar Transaksi Realtime",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredTxns.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, KulinaBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📄", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Belum Ada Transaksi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KulinaTextMuted
                        )
                    }
                }
            } else {
                filteredTxns.forEach { txn ->
                    val sdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
                    val timeStr = sdf.format(Date(txn.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, KulinaBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (txn.type == "in") Color(0xFFE8F8F0) else Color(0xFFFFF0F0)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (txn.type == "in") Icons.Default.ArrowBack else Icons.Default.Share,
                                    contentDescription = null,
                                    tint = if (txn.type == "in") KulinaGreen else KulinaRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = txn.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KulinaText
                                )
                                Text(
                                    text = "$timeStr • " + txn.desc,
                                    fontSize = 11.sp,
                                    color = KulinaTextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Text(
                                text = (if (txn.type == "in") "+" else "−") + "Rp " + String.format("%,d", txn.amount).replace(',', '.'),
                                fontSize = 13.sp,
                                color = if (txn.type == "in") KulinaGreen else KulinaRed,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }

    // Modern Progress Sim Loading Dialog for PDF Generics
    if (showPdfProgress) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Spacer(modifier = Modifier.width(1.dp))
            },
            title = {
                Text(
                    text = "Menyiapkan Laporan PDF",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = KulinaPurple,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = pdfStepMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(pdfProgressValue * 100).toInt()}% Selesai",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaOrange
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // Mock PDF Document Preview Reader Dialog
    if (showPdfDialog) {
        AlertDialog(
            onDismissRequest = { showPdfDialog = false },
            confirmButton = {
                Button(
                    onClick = { showPdfDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple)
                ) {
                    Text("Tutup Laporan", fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.showNotification(
                            title = "Ekspor Sukses 💾",
                            message = "PDF Laporan Berhasil Dikonversi ke Cetak Fisik!",
                            type = NotificationType.INFO
                        )
                    }
                ) {
                    Text("Cetak Laporan", fontSize = 12.sp, color = KulinaOrange, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📄 ", fontSize = 20.sp)
                    Text(
                        text = "Pratinjau PDF Laporan Keuangan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE5E5E5), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    // Actual Sheet of Paper Mockup
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = "KULINA MITRA MANDIRI Ltd.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurpleDark,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Digital Coordination Receipt Document",
                                fontSize = 7.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Divider(color = Color.Black, modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "LAPORAN KINERJA OUTLET",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "Outlet Kemitraan: $assignedOutlet",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "ID Pencetak: ${userSession?.username ?: "Sistem Kulina"}",
                                fontSize = 8.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Tanggal Dokumen: 03 Juni 2026",
                                fontSize = 8.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Finance breakdown table
                            Row(
                                modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0)).padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Deskripsi Aliran Dana", fontSize = 8.sp, fontWeight = FontWeight.Black)
                                Text("Jumlah Jurnal", fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Pendapatan Terjurnal", fontSize = 8.sp, fontWeight = FontWeight.Medium)
                                Text("Rp " + String.format("%,d", earnedToday).replace(',', '.'), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = KulinaGreen)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Pengeluaran Terjurnal", fontSize = 8.sp, fontWeight = FontWeight.Medium)
                                Text("Rp " + String.format("%,d", spentToday).replace(',', '.'), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = KulinaRed)
                            }

                            Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Surplus Bersih Rekonsiliasi", fontSize = 8.sp, fontWeight = FontWeight.Black)
                                Text("Rp " + String.format("%,d", netProfit).replace(',', '.'), fontSize = 8.sp, fontWeight = FontWeight.Black, color = KulinaPurple)
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Signature layout
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Sistem Verifikasi Kulina", fontSize = 6.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("DITERIMA SECARA SAH ✓", fontSize = 7.sp, fontWeight = FontWeight.Black, color = KulinaGreen)
                                    Text("Sandi Keamanan: SHA256", fontSize = 5.sp, color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun FinanceS_Item(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = value,
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CctvScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    var timerVal by remember { mutableStateOf("14:38:22") }

    // Start a lightweight clock updates for CCTV realism (efficiency!)
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            timerVal = sdf.format(Date())
            delay(1000)
        }
    }

    val cams by viewModel.cctvCameras.collectAsState()
    val userSession by viewModel.userSession.collectAsState()

    val assignedOutlet = userSession?.outletName ?: "Semua"
    val isMitra = userSession?.role == "MITRA"

    val displayCams = if (!isMitra || assignedOutlet == "Semua") {
        cams
    } else {
        cams.filter { cam ->
            cam.name.contains(assignedOutlet, ignoreCase = true)
        }
    }

    val baseIp by viewModel.cctvBaseIpState.collectAsState()
    val isSyncing by viewModel.isCctvSyncing.collectAsState()
    val syncProgress by viewModel.cctvSyncProgress.collectAsState()
    val syncMessage by viewModel.cctvSyncMessage.collectAsState()

    var selectedCamId by remember { mutableStateOf(1) }
    val activeCam = displayCams.find { it.id == selectedCamId } ?: displayCams.firstOrNull() ?: CctvCamera(1, "Cam", "🏪", "OFFLINE", "192.168.1.101")

    var fpsOffset by remember { mutableStateOf(0) }
    LaunchedEffect(selectedCamId) {
        while (true) {
            delay(1800)
            fpsOffset = (-2..2).random()
        }
    }

    val displayFps = if (activeCam.status == "ONLINE") {
        (activeCam.activeFps + fpsOffset).coerceAtLeast(1)
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "CCTV Monitoring & Sync", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Main Live CAM Player Screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF140B26))
                    .border(2.dp, KulinaPurple.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                // CAM top status header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = when (activeCam.status) {
                                    "ONLINE" -> KulinaGreen
                                    "BUFFERING" -> KulinaOrange
                                    else -> KulinaRed
                                },
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VIEW - ${activeCam.name}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = KulinaYellow,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = activeCam.ipAddress,
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Video body or simulated terminal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.77f) // 16:9 widescreen
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF090412))
                        .drawBehind {
                            val lineGap = 4.dp.toPx()
                            var y = 0f
                            while (y < size.height) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.025f),
                                    start = androidx.compose.ui.geometry.Offset(0f, y),
                                    end = androidx.compose.ui.geometry.Offset(size.width, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                                y += lineGap
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (activeCam.status) {
                        "ONLINE" -> {
                            // Online camera display
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = activeCam.emoji,
                                    fontSize = 54.sp,
                                    modifier = Modifier.drawBehind {
                                        drawCircle(
                                            color = KulinaPurple.copy(alpha = 0.15f),
                                            radius = size.minDimension * 0.8f
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(KulinaGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIVE FEED TRANSMISSION ACTIVE",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Dynamic bandwidth badge (bottom right)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "$displayFps FPS • ${(1.1f + (displayFps / 40f)).toString().take(4)} Mbps",
                                    color = KulinaYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Dynamic camera type (bottom left)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "${activeCam.resolution} • H.265",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Blinking REC sign (top left)
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(vertical = 2.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(KulinaRed, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "REC",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        "BUFFERING" -> {
                            // Buffering state view
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = KulinaYellow,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Menyambungkan Kembali...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "RTSP handshakes retrying on port 554",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        else -> {
                            // Offline state view
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = KulinaRed,
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "KAMERA DISCONNECTED / ERROR",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tidak ada transmisi video aktif di sub-node IP ${activeCam.ipAddress}.",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Realtime IP Synchronization Dashboard Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = KulinaPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Konfigurasi Subnet & Sinkron IP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Sinkronkan IP untuk memindai port RTSP di seluruh outlet secara realtime.",
                        fontSize = 11.sp,
                        color = KulinaTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = baseIp,
                            onValueChange = { viewModel.updateCctvBaseIp(it) },
                            placeholder = { Text("cth. 192.168.1.100") },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("cctv_ip_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KulinaText
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = KulinaPurple,
                                unfocusedBorderColor = KulinaBorder
                            )
                        )

                        Button(
                            onClick = { viewModel.syncCctvWithIp(baseIp) },
                            enabled = !isSyncing && baseIp.isNotBlank(),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("cctv_sync_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KulinaPurple,
                                contentColor = Color.White,
                                disabledContainerColor = KulinaPurple.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync IP", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    // Progress overlay when syncing
                    AnimatedVisibility(
                        visible = isSyncing || syncProgress > 0f,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mengintegrasikan Jaringan...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KulinaText
                                )
                                Text(
                                    text = "${(syncProgress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KulinaPurple
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = syncProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = KulinaPurple,
                                trackColor = KulinaBorder
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Terminal logging box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0D0620))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "> $syncMessage",
                                    fontSize = 10.sp,
                                    color = KulinaYellow,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (!isSyncing && displayCams.all { it.status == "ONLINE" }) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(KulinaGreen.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .border(1.dp, KulinaGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = KulinaGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Koneksi Realtime Aktif: CCTV Sinkron",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KulinaGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Grid cameras section header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = KulinaPurple,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Daftar Kamera Terhubung (${displayCams.size} Feed)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
            }

            // Grid layout display
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                displayCams.chunked(2).forEach { rowCams ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowCams.forEach { cam ->
                            val isSelected = cam.id == selectedCamId
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) KulinaPurple else KulinaBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedCamId = cam.id },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    // Simulated small video stream box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1.6f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF140D24)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = cam.emoji, fontSize = 28.sp)

                                        // Status node indicator overlay
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .background(
                                                    color = when (cam.status) {
                                                        "ONLINE" -> KulinaGreen
                                                        "BUFFERING" -> KulinaOrange
                                                        else -> KulinaRed
                                                    },
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(vertical = 2.dp, horizontal = 5.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(Color.White, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = cam.status,
                                                    color = Color.White,
                                                    fontSize = 7.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }

                                        // IP overlay on camera feed
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(6.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                                .padding(vertical = 2.dp, horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = cam.ipAddress.substringAfterLast("."),
                                                color = Color.White,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = cam.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "ID #${cam.id}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = KulinaTextMuted
                                        )
                                        if (cam.status == "ONLINE") {
                                            Text(
                                                text = "${cam.activeFps} FPS",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = KulinaGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (rowCams.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RewardScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val points = profile?.points ?: 2450
    val redeemedRewards by viewModel.redeemedRewards.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Tukar Reward, 1: Voucher Saya

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "Reward Mitra", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Main loyalty Points Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(KulinaOrange, KulinaYellow))
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Poin Kamu",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaPurpleDark.copy(alpha = 0.7f)
                    )

                    Text(
                        text = String.format("%,d", points).replace(',', '.'),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark
                    )

                    Text(
                        text = "Poin Aktif",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaPurpleDark.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar to platinum level
                    val progressRatio = (points.toFloat() / 3500f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(KulinaPurpleDark.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressRatio)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(KulinaPurple)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val remainingPoints = (3500 - points).coerceAtLeast(0)
                    Text(
                        text = if (remainingPoints > 0) "$remainingPoints poin lagi menuju level Platinum ⭐" else "Anda sudah mencapai level Platinum! 🏆",
                        fontSize = 11.sp,
                        color = KulinaPurpleDark.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TAB CHANNELS FOR REDEEM & INVENTORY
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(KulinaBorder)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Tukar Poin 🪙", "Kupon Saya 🎟️").forEachIndexed { index, title ->
                    val isSelected = activeTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { activeTab = index }
                            .padding(vertical = 10.dp)
                            .testTag("reward_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) KulinaPurpleDark else KulinaTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (activeTab == 0) {
                // REDEEM TAB VIEW
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = KulinaPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tukarkan Reward Menarik",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                data class RewardItem(val title: String, val emoji: String, val pointsNeeded: Int, val desc: String)
                val rewards = listOf(
                    RewardItem("Voucher Kopi Gratis", "☕", 500, "Voucher Janji Jiwa Kopi Susu Premium"),
                    RewardItem("Diskon Bahan Baku 10%", "🎁", 1000, "Diskon langsung order bahan selanjutnya"),
                    RewardItem("Cashback Rp 50.000", "🧾", 1500, "Cashback saldo ditambahkan ke transaksi"),
                    RewardItem("Free Ongkir Sekali Pakai", "🚚", 300, "Bebas ongkos kirim ke outlet kulinamu"),
                    RewardItem("Apron Eksklusif Kulina", "🧑‍🍳", 1200, "Apron kanvas tebal dengan logo bordir"),
                    RewardItem("Upgrade Paket Platinum", "🏆", 5000, "Upgrade status mitra prioritas gratis ongkir")
                )

                rewards.forEach { reward ->
                    val canRedeem = points >= reward.pointsNeeded

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .testTag("reward_item_card_${reward.pointsNeeded}"),
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
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(KulinaBg, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = reward.emoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reward.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KulinaText
                                )
                                Text(
                                    text = reward.desc,
                                    fontSize = 10.sp,
                                    color = KulinaTextMuted,
                                    lineHeight = 13.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Text(
                                    text = "${reward.pointsNeeded} poin",
                                    fontSize = 11.sp,
                                    color = if (canRedeem) KulinaPurple else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.redeemReward(reward.title, reward.emoji, reward.pointsNeeded) {
                                        android.widget.Toast.makeText(context, "Kupon ${reward.title} diproses!", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                },
                                enabled = canRedeem,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canRedeem) KulinaYellow else KulinaBg,
                                    contentColor = if (canRedeem) KulinaPurpleDark else KulinaTextMuted
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
                                modifier = Modifier.testTag("btn_redeem_${reward.pointsNeeded}")
                            ) {
                                Text(
                                    text = if (canRedeem) "Tukar" else "Terkunci",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // PERSONAL VOUCHER MY COUPONS TAB VIEW
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = KulinaGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Voucher Saya (${redeemedRewards.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (redeemedRewards.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, KulinaBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎫", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Belum Ada Voucher Terbuka",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KulinaText
                            )
                            Text(
                                text = "Kumpulkan poin dari pembelian bahan baku dan tukarkan di tab sebelah.",
                                fontSize = 11.sp,
                                color = KulinaTextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    redeemedRewards.forEach { voucher ->
                        val dateFormatted = SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(voucher.redeemedAt))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .testTag("my_voucher_card_${voucher.id}"),
                            colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, KulinaBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(KulinaBorder, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = voucher.emoji, fontSize = 20.sp)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = voucher.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = KulinaText
                                        )
                                        Text(
                                            text = "Ditukar pada: $dateFormatted",
                                            fontSize = 9.sp,
                                            color = KulinaTextMuted
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(KulinaPurple.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "-${voucher.pointsSpent} Pts",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KulinaPurple
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = KulinaBorder, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(KulinaBg, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "KODE VOUCHER",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KulinaTextMuted
                                        )
                                        Text(
                                            text = voucher.code,
                                            fontSize = 12.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            color = KulinaPurpleDark
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("Kulina Voucher", voucher.code)
                                            clipboard.setPrimaryClip(clip)
                                            android.widget.Toast.makeText(context, "Kode voucher disalin!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                                        modifier = Modifier.height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Salin", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val meetings by viewModel.meetings.collectAsState()
    
    // Form Inputs State
    var showAddForm by remember { mutableStateOf(false) }
    var judulInput by remember { mutableStateOf("") }
    var tanggalInput by remember { mutableStateOf("") }
    var waktuInput by remember { mutableStateOf("") }
    
    // Tab State: 0 = "Semua Jadwal", 1 = "Terdaftar Saya"
    var selectedTab by remember { mutableStateOf(0) }
    
    // Active Web-conference state
    var activeMeetForConf by remember { mutableStateOf<PartnerMeeting?>(null) }
    
    // If inside simulated Google Meet Conference
    activeMeetForConf?.let { meet ->
        SimulatedGoogleMeetDialog(
            meeting = meet,
            onDismiss = { activeMeetForConf = null }
        )
    } ?: run {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KulinaBg)
        ) {
            InnerHeader(title = "Meeting dengan Mitra", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Highlighting header banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KulinaYellow, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = KulinaPurpleDark,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        val activeCount = meetings.filter { !it.isPast && it.isRegistered }.size
                        Text(
                            text = "Meeting Bulan Ini: ${meetings.filter { !it.isPast }.size} Sesi ($activeCount Diikuti)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurpleDark
                        )
                        Text(
                            text = "Buat koordinasi, daftar, atau gabung lewat Google Meet langsung.",
                            fontSize = 11.sp,
                            color = KulinaPurpleDark.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Bar: Add Schedule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manajemen Koordinasi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark
                    )
                    
                    Button(
                        onClick = { showAddForm = !showAddForm },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showAddForm) KulinaRed else KulinaPurple,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp).testTag("toggle_add_meeting_button")
                    ) {
                        Icon(
                            imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showAddForm) "Batal" else "Jadwalkan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Expandable Schedule Creation Form ("Buatkan halaman/form daftar")
                AnimatedVisibility(
                    visible = showAddForm,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .shadow(2.dp, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, KulinaBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Jadwalkan Koordinasi Baru (Google Meet)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurple
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = judulInput,
                                onValueChange = { judulInput = it },
                                label = { Text("Judul Koordinasi") },
                                placeholder = { Text("cth. Koordinasi Stok Outlet Kelapa Gading") },
                                modifier = Modifier.fillMaxWidth().testTag("meeting_title_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = KulinaPurple,
                                    unfocusedBorderColor = KulinaBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = tanggalInput,
                                    onValueChange = { tanggalInput = it },
                                    label = { Text("Tanggal (cth: 18 Jun)") },
                                    placeholder = { Text("18 Jun") },
                                    modifier = Modifier.weight(1f).testTag("meeting_date_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = KulinaPurple,
                                        unfocusedBorderColor = KulinaBorder
                                    )
                                )

                                OutlinedTextField(
                                    value = waktuInput,
                                    onValueChange = { waktuInput = it },
                                    label = { Text("Waktu (cth: 10:00 WIB)") },
                                    placeholder = { Text("10:00 WIB") },
                                    modifier = Modifier.weight(1f).testTag("meeting_time_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = KulinaPurple,
                                        unfocusedBorderColor = KulinaBorder
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    viewModel.createMeetingAndSyncMeet(judulInput, tanggalInput, waktuInput)
                                    judulInput = ""
                                    tanggalInput = ""
                                    waktuInput = ""
                                    showAddForm = false
                                },
                                enabled = judulInput.isNotBlank(),
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("meeting_schedule_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Google Meet & Jadwalkan", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                // Interactive Navigation Tab ("halaman daftar" representation)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = KulinaPurple,
                    divider = { Divider(color = KulinaBorder) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Semua Jadwal", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Terdaftar Saya (${meetings.filter { !it.isPast && it.isRegistered }.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                val filteredMeetings = if (selectedTab == 1) {
                    meetings.filter { !it.isPast && it.isRegistered }
                } else {
                    meetings.filter { !it.isPast }
                }

                // Schedules Listing (Upcoming)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null,
                        tint = KulinaPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Jadwal Pertemuan Mendatang",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredMeetings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = KulinaTextMuted, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tidak ada jadwal koordinasi terpilih.",
                                fontSize = 11.sp,
                                color = KulinaTextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    filteredMeetings.forEach { meet ->
                        MeetingCard(
                            day = meet.day,
                            mon = meet.month,
                            title = meet.title,
                            timeInfo = if (meet.isPast) meet.timeInfo else "${meet.timeInfo}",
                            participants = "${meet.currentParticipants} mitra terdaftar",
                            btnText = if (meet.isRegistered) "Gabung Meet" else "Daftar",
                            labelBg = if (meet.isRegistered) KulinaPurple else KulinaOrange,
                            onClick = {
                                if (meet.isRegistered) {
                                    activeMeetForConf = meet
                                } else {
                                    viewModel.registerForMeeting(meet.id)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Previous Records (always shown if on tab 0)
                if (selectedTab == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = KulinaPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rekaman Sebelumnya & Dokumentasi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val pastMeetings = meetings.filter { it.isPast }
                    pastMeetings.forEach { meet ->
                        MeetingCard(
                            day = meet.day,
                            mon = meet.month,
                            title = meet.title,
                            timeInfo = "Rekaman tersedia • ${meet.durationText}",
                            participants = "Dihadiri ${meet.currentParticipants} mitra",
                            btnText = "Tonton",
                            labelBg = Color(0xFFB0B0B0),
                            outlineBtn = true,
                            onClick = {
                                viewModel.showNotification(
                                    title = "Memutar Dokumentasi Rekaman 📺",
                                    message = "Memutar ulang rekaman '${meet.title}' berdurasi ${meet.durationText}.",
                                    type = NotificationType.INFO
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatedGoogleMeetDialog(
    meeting: PartnerMeeting,
    onDismiss: () -> Unit
) {
    var timerTenths by remember { mutableStateOf(0) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isCamOff by remember { mutableStateOf(false) }
    
    // Ticking conference timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            timerTenths += 1
        }
    }

    val minutes = timerTenths / 60
    val seconds = timerTenths % 60
    val durationText = String.format("%02d:%02d", minutes, seconds)

    // A dark full-screen dialog mimicking actual Google Meet
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141517))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Header: Google Meet codes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(KulinaGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GOOGLE MEET CONFERENCE LIVE",
                            fontSize = 11.sp,
                            color = KulinaGreen,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = meeting.title,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = KulinaYellow,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = durationText,
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Main Active Presenter Screen (Mock Presentation Screen-share)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF202124))
                    .border(2.dp, KulinaPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background scanline detail
                Column(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📊",
                        fontSize = 54.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Presentase Kinerja Operasional Kulina",
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sedang dipresentasikan oleh Kulina Admin Penyelenggara",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Small simulated chart lines
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(24.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Box(modifier = Modifier.width(6.dp).fillMaxHeight(0.4f).background(KulinaPurple))
                        Box(modifier = Modifier.width(6.dp).fillMaxHeight(0.6f).background(KulinaPurple))
                        Box(modifier = Modifier.width(6.dp).fillMaxHeight(0.5f).background(KulinaPurple))
                        Box(modifier = Modifier.width(6.dp).fillMaxHeight(0.9f).background(KulinaYellow))
                    }
                }
                
                // Overlay active presenting tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(vertical = 3.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = "Kulina Presenter • Layar Utama",
                        color = KulinaYellow,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Other Attendees Grid (Shows Anda, other simulated partners)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Partner Grid Box: Anda
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2D2E30))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCamOff) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(KulinaPurple, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("B", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Kamera Anda Mati", fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👤", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Budi Santoso (Anda)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Bottom tag
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(vertical = 2.dp, horizontal = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Anda",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isMicMuted) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Close, contentDescription = null, tint = KulinaRed, modifier = Modifier.size(8.dp))
                            }
                        }
                    }
                }

                // Partner Grid Box: Mitra 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2D2E30))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏪", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Mitra Kelapa Gading", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(vertical = 2.dp, horizontal = 6.dp)
                    ) {
                        Text(
                            text = "Mitra KLG",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Google Meet controller action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF202124), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Mic Control
                IconButton(
                    onClick = { isMicMuted = !isMicMuted },
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (isMicMuted) KulinaRed else Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isMicMuted) Icons.Default.Close else Icons.Default.Phone,
                        contentDescription = "Mute Microphone",
                        tint = if (isMicMuted) Color.White else Color.White
                    )
                }

                // Camera Control
                IconButton(
                    onClick = { isCamOff = !isCamOff },
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (isCamOff) KulinaRed else Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isCamOff) Icons.Default.Lock else Icons.Default.Star,
                        contentDescription = "Toggle Video",
                        tint = Color.White
                    )
                }

                // Share link Info
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Info & Tautan",
                        tint = Color.White
                    )
                }

                // End meeting hangup button (Standard Google Meet Red Pill)
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = KulinaRed),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .widthIn(min = 100.dp)
                ) {
                    Text(
                        text = "Tinggalkan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Meet URL link display
            Text(
                text = "Tautan Meet aktif: ${meeting.meetUrl}",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MeetingCard(
    day: String,
    mon: String,
    title: String,
    timeInfo: String,
    participants: String,
    btnText: String,
    labelBg: Color,
    outlineBtn: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, KulinaBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calendar visual label
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(labelBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = mon,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaText
                    )
                    Text(
                        text = timeInfo,
                        fontSize = 11.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = KulinaBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = participants,
                    fontSize = 11.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (outlineBtn) KulinaBg else KulinaPurple,
                        contentColor = if (outlineBtn) KulinaPurple else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = if (outlineBtn) BorderStroke(1.5.dp, KulinaBorder) else null,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = btnText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PromoScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "Info Promo", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Main decorative promo hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(KulinaOrange, KulinaYellow))
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🎉", fontSize = 36.sp)
                    Text(
                        text = "Promo Ramadan Special",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark,
                        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                    )
                    Text(
                        text = "Berlaku 25 Mei – 30 Juni 2025",
                        fontSize = 13.sp,
                        color = KulinaPurpleDark.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Promo Card 1
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.5.dp, KulinaBorder, RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KulinaPurple)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text(
                            text = "PROMO AKTIF #1",
                            color = KulinaYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Diskon 20% Bahan Baku Ekado & Pempek",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Dapatkan diskon 20% untuk setiap pemesanan bahan baku ekado udang premium dan pempek palembang selama periode promo berlangsung.",
                        fontSize = 12.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PromoMetricItem(title = "20%", subtitle = "Diskon", modifier = Modifier.weight(1f))
                        PromoMetricItem(title = "Min 5", subtitle = "Pack", modifier = Modifier.weight(1f))
                        PromoMetricItem(title = "37", subtitle = "Hari Lagi", modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Promo Card 2
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.5.dp, KulinaBorder, RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KulinaOrange)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text(
                            text = "PROMO AKTIF #2",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Gratis Ongkir Order di Atas Rp 500Rb",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Gratis biaya pengiriman untuk setiap pemesanan bahan baku dengan total minimal Rp 500.000 ke seluruh wilayah Jabodetabek.",
                        fontSize = 12.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KulinaBg, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Min. Order: Rp 500.000",
                                color = KulinaPurple,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Berlaku untuk area Jabodetabek",
                                color = KulinaTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PromoMetricItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(KulinaBg, RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = KulinaPurple,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtitle,
                color = KulinaTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ChatScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.chatMessages.collectAsState()
    val typedText by viewModel.typedMessage.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto scroll down upon messaging
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KulinaPurple)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Obrolan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Tim Support Kulina • Online",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 1.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(KulinaYellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "KT",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = KulinaPurpleDark
                )
            }
        }

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(14.dp)
        ) {
            items(messages.size) { index ->
                val msg = messages[index]

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.dp),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!msg.isUser) {
                        // Support avatar
                        Box(
                            modifier = Modifier
                                .align(Alignment.Top)
                                .size(30.dp)
                                .background(KulinaPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "KT",
                                color = KulinaYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(
                        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = if (msg.isUser) "Saya" else msg.sender,
                            fontSize = 11.sp,
                            color = KulinaTextMuted,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (msg.isUser) 14.dp else 4.dp,
                                        bottomEnd = if (msg.isUser) 4.dp else 14.dp
                                    )
                                )
                                .background(if (msg.isUser) KulinaPurple else Color.White)
                                .border(
                                    width = if (msg.isUser) 0.dp else 1.5.dp,
                                    color = if (msg.isUser) Color.Transparent else KulinaBorder,
                                    shape = RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (msg.isUser) 14.dp else 4.dp,
                                        bottomEnd = if (msg.isUser) 4.dp else 14.dp
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = msg.content,
                                color = if (msg.isUser) Color.White else KulinaText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 20.sp
                            )
                        }

                        Text(
                            text = msg.time,
                            fontSize = 10.sp,
                            color = KulinaTextMuted,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (msg.isUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .align(Alignment.Top)
                                .size(30.dp)
                                .background(KulinaYellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BS",
                                color = KulinaPurpleDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // Custom Keyboard layout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(BorderStroke(1.5.dp, KulinaBorder))
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = typedText,
                    onValueChange = { viewModel.updateTypedMessage(it) },
                    placeholder = {
                        Text(
                            text = "Ketik pesan...",
                            color = KulinaTextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(max = 120.dp)
                        .testTag("chat_input_text_field"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = KulinaBg,
                        unfocusedContainerColor = KulinaBg,
                        focusedBorderColor = KulinaBorder,
                        unfocusedBorderColor = KulinaBorder
                    ),
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KulinaText
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            viewModel.sendChatMessage()
                            keyboardController?.hide()
                        }
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = {
                        viewModel.sendChatMessage()
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(KulinaPurple, CircleShape)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Kirim",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SupportScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "Bantuan & Support", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Live Directory Help options
            SupportOptionItem(
                title = "Live Chat Support",
                subtitle = "Obrolan langsung dengan tim kami",
                icon = Icons.Default.Email,
                themeColor = "purple",
                onClick = { viewModel.navigateTo(Screen.Chat) }
            )

            SupportOptionItem(
                title = "Telepon Support",
                subtitle = "0800-KULINA • Gratis 24 jam",
                icon = Icons.Default.Phone,
                themeColor = "orange",
                onClick = { }
            )

            SupportOptionItem(
                title = "Saran & Kritik",
                subtitle = "Bantu kami menjadi lebih baik",
                icon = Icons.Default.Star,
                themeColor = "green",
                onClick = { viewModel.navigateTo(Screen.Feedback) }
            )

            SupportOptionItem(
                title = "FAQ & Panduan",
                subtitle = "Temukan jawaban pertanyaan umum",
                icon = Icons.Default.Info,
                themeColor = "yellow",
                onClick = { viewModel.navigateTo(Screen.Faq) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Partner Profile visual box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, KulinaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Profil Akun",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurple,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    DetailRow_Support(key = "Nama", value = profile?.name ?: "Budi Santoso")
                    DetailRow_Support(key = "ID Partner", value = profile?.id ?: "KLN-2024-0317", highlight = true)
                    DetailRow_Support(key = "Paket", value = profile?.level ?: "Gold Partner")
                    DetailRow_Support(key = "Bergabung", value = "12 Mar 2024")

                    Spacer(modifier = Modifier.height(14.dp))

                    // Logout Simulated Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF0F0), RoundedCornerShape(10.dp))
                            .clickable {
                                android.widget.Toast
                                    .makeText(context, "Log Out disimulasikan!", android.widget.Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            tint = KulinaRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Keluar dari Akun",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow_Support(key: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = key, fontSize = 12.sp, color = KulinaTextMuted, fontWeight = FontWeight.SemiBold)
        Text(
            text = value,
            fontSize = 12.sp,
            color = if (highlight) KulinaPurple else KulinaText,
            fontWeight = if (highlight) FontWeight.Black else FontWeight.Bold
        )
    }
    Divider(color = KulinaBorder)
}

@Composable
fun SupportOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    themeColor: String,
    onClick: () -> Unit
) {
    val bgColors = when (themeColor) {
        "purple" -> Color(0xFFF0E8FF)
        "orange" -> Color(0xFFFFF3E0)
        "green" -> Color(0xFFE8F5E9)
        else -> Color(0xFFFFF8E1)
    }
    val iconColors = when (themeColor) {
        "purple" -> KulinaPurple
        "orange" -> KulinaOrange
        "green" -> KulinaGreen
        else -> Color(0xFFE6A800)
    }

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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(bgColors, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColors,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaText
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                imageVector = Icons.Default.Share, // arrow placeholder
                contentDescription = null,
                tint = KulinaTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun FeedbackScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val rating by viewModel.rating.collectAsState()
    val categories by viewModel.selectedCategories.collectAsState()
    val text by viewModel.feedbackText.collectAsState()

    val availableTags = listOf(
        "Kualitas Produk", "Pengiriman", "Aplikasi", "Support", "Harga"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "Saran & Kritik", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Friendly Intro
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "💬", fontSize = 40.sp)
                Text(
                    text = "Pendapat Kamu Sangat Berarti!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaText,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Bantu Kulina berkembang lebih baik",
                    fontSize = 12.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Input form
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, KulinaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Penilaian Keseluruhan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Stars dynamic selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star Bintang",
                                tint = if (star <= rating) KulinaOrange else KulinaBg,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { viewModel.updateRating(star) }
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Kategori",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaTextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Flow dynamic tags
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTags.forEach { tag ->
                            val selected = categories.contains(tag)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) KulinaPurple else KulinaBg)
                                    .border(
                                        1.5.dp,
                                        if (selected) KulinaPurple else KulinaBorder,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { viewModel.toggleCategory(tag) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = if (selected) Color.White else KulinaTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "Pesan Kamu",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaTextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Textarea inputs
                    OutlinedTextField(
                        value = text,
                        onValueChange = { viewModel.updateFeedbackText(it) },
                        placeholder = {
                            Text(
                                "Tuliskan saran atau kritikmu di sini...",
                                fontSize = 13.sp,
                                color = KulinaTextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("feedback_text_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = KulinaBg,
                            unfocusedContainerColor = KulinaBg,
                            focusedBorderColor = KulinaBorder,
                            unfocusedBorderColor = KulinaBorder
                        ),
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KulinaText
                        )
                    )

                    Button(
                        onClick = {
                            viewModel.submitFeedback {
                                android.widget.Toast.makeText(
                                    context,
                                    "Terima kasih! Feedback kamu telah terkirim 🙏",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                                onBack() // return safely
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(48.dp)
                            .testTag("submit_feedback_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Kirim Feedback",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Admin CRUD state for Product (Tab 0)
    var showForm by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var nameInput by remember { mutableStateOf("") }
    var unitInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var stockInput by remember { mutableStateOf("100") }
    var thresholdInput by remember { mutableStateOf("15") }
    var imageTypeSelect by remember { mutableStateOf("somay") }

    // Admin CRUD state for Meeting (Tab 2)
    var showMeetingForm by remember { mutableStateOf(false) }
    var editingMeeting by remember { mutableStateOf<PartnerMeeting?>(null) }
    var meetingTitleInput by remember { mutableStateOf("") }
    var meetingDayInput by remember { mutableStateOf("") }
    var meetingMonthInput by remember { mutableStateOf("") }
    var meetingTimeInput by remember { mutableStateOf("") }
    var meetingPlatformInput by remember { mutableStateOf("Google Meet") }
    var meetingUrlInput by remember { mutableStateOf("") }
    var meetingParticipantsInput by remember { mutableStateOf("0") }
    var meetingIsRegisteredInput by remember { mutableStateOf(false) }
    var meetingIsPastInput by remember { mutableStateOf(false) }

    // Admin CRUD state for FAQ (Tab 3)
    var showFaqForm by remember { mutableStateOf(false) }
    var editingFaq by remember { mutableStateOf<FaqItem?>(null) }
    var faqQuestionInput by remember { mutableStateOf("") }
    var faqAnswerInput by remember { mutableStateOf("") }
    var faqCategorySelect by remember { mutableStateOf("Umum") }

    // Admin CRUD state for CCTV (Tab 4)
    var showCctvForm by remember { mutableStateOf(false) }
    var editingCctv by remember { mutableStateOf<CctvCamera?>(null) }
    var cctvNameInput by remember { mutableStateOf("") }
    var cctvEmojiInput by remember { mutableStateOf("🏪") }
    var cctvStatusSelect by remember { mutableStateOf("ONLINE") }
    var cctvIpInput by remember { mutableStateOf("192.168.1.100") }
    var cctvResolutionInput by remember { mutableStateOf("1080p FHD") }
    var cctvFpsInput by remember { mutableStateOf("24") }

    // Flow collections
    val products by viewModel.products.collectAsState()
    val incomingOrders by viewModel.orders.collectAsState()
    val meetings by viewModel.meetings.collectAsState()
    val faqs by viewModel.faqs.collectAsState()
    val cctvCameras by viewModel.cctvCameras.collectAsState()

    var adminTab by remember { mutableStateOf(0) } // 0 = Katalog & Keuangan, 1 = Pesanan Masuk, 2 = Pertemuan, 3 = FAQ, 4 = CCTV

    // Screen shell
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(
            title = "Admin Control Center",
            onBack = onBack,
            trailingContent = {
                if (isAuthenticated) {
                    IconButton(
                        onClick = { isAuthenticated = false; passwordInput = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Keluar",
                            tint = Color.White
                        )
                    }
                }
            }
        )

        if (!isAuthenticated) {
            // SECURE PASSWORD ENTRANCE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(KulinaPurple.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Gate",
                        tint = KulinaPurple,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Konfirmasi Pemilik",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )

                Text(
                    text = "Masukkan sandi rahasia admin Kulina Mitra untuk mengelola katalog bahan baku.",
                    fontSize = 12.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        passwordError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_field"),
                    label = { Text("Sandi Rahasia Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    placeholder = { Text("Ketik sandi di sini...") },
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                text = if (passwordVisible) "🙈" else "👁️",
                                fontSize = 18.sp
                            )
                        }
                    },
                    isError = passwordError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KulinaPurple,
                        unfocusedBorderColor = KulinaBorder
                    )
                )

                if (passwordError) {
                    Text(
                        text = "Sandi salah! Gunakan sandi 'admin123' atau 'kulina2026'",
                        fontSize = 11.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (passwordInput == "admin123" || passwordInput == "kulina" || passwordInput == "kulina2026") {
                            isAuthenticated = true
                            passwordError = false
                            android.widget.Toast.makeText(context, "Akses Admin Diberikan!", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            passwordError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit"),
                    colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Masuk Panel Admin 🔑", fontSize = 13.sp, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(30.dp))
                
                Text(
                    text = "*Catatan demonstrasi: Sandi rahasia default adalah admin123",
                    fontSize = 11.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // ADMIN MANAGEMENT SCREEN (AUTHENTICATED)
            Column(modifier = Modifier.fillMaxSize()) {
                
                // Beautiful Tab Toggles (Scrollable row)
                val pendingCount = incomingOrders.filter { !it.isPaid || it.status != "Selesai" }.size
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 4.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        AdminTabButton(
                            text = "Katalog & Keuangan 📋",
                            active = adminTab == 0,
                            onClick = { adminTab = 0 }
                        )
                    }
                    item {
                        AdminTabButton(
                            text = "Pesanan Masuk ($pendingCount) 📥",
                            active = adminTab == 1,
                            onClick = { adminTab = 1 }
                        )
                    }
                    item {
                        AdminTabButton(
                            text = "Pertemuan 🎥",
                            active = adminTab == 2,
                            onClick = { adminTab = 2 }
                        )
                    }
                    item {
                        AdminTabButton(
                            text = "Kelola FAQs ❔",
                            active = adminTab == 3,
                            onClick = { adminTab = 3 }
                        )
                    }
                    item {
                        AdminTabButton(
                            text = "IoT CCTV 📡",
                            active = adminTab == 4,
                            onClick = { adminTab = 4 }
                        )
                    }
                }

                if (adminTab == 0) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Form Slide/Draw section (Add/Edit Product)
                        if (showForm || editingProduct != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, KulinaPurpleLight),
                        colors = CardDefaults.cardColors(containerColor = KulinaCardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (editingProduct == null) "Tambah Produk Baru 🥟" else "Ubah Data Produk ✏️",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurpleDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("input_product_name"),
                                label = { Text("Nama Produk", fontSize = 11.sp) },
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = unitInput,
                                onValueChange = { unitInput = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("input_product_unit"),
                                label = { Text("Satuan (e.g. per pack)", fontSize = 11.sp) },
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = priceInput,
                                onValueChange = { priceInput = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("input_product_price"),
                                label = { Text("Harga (Rp)", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                )
                            )

                            // Raw material stock & low stock threshold configuration
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = stockInput,
                                    onValueChange = { stockInput = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_product_stock"),
                                    label = { Text("Stok Gudang Pusat", fontSize = 11.sp) },
                                    singleLine = true,
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                    )
                                )
                                OutlinedTextField(
                                    value = thresholdInput,
                                    onValueChange = { thresholdInput = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_product_threshold"),
                                    label = { Text("Threshold Alert (Min)", fontSize = 11.sp) },
                                    singleLine = true,
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Pilih Ilustrasi Gambar Produk:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaText
                            )

                            // Image Category selection row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val types = listOf(
                                    Triple("somay", "Somay", com.example.R.drawable.img_somay_1780392776236),
                                    Triple("batagor", "Batagor", com.example.R.drawable.img_batagor_1780392793000),
                                    Triple("pempek", "Pempek", com.example.R.drawable.img_pempek_1780392810244),
                                    Triple("ingredients", "Bahan", com.example.R.drawable.img_ingredients_1780392830223)
                                )
                                types.forEach { (typeKey, labelStr, img) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (imageTypeSelect == typeKey) KulinaPurple.copy(alpha = 0.15f) else KulinaBg)
                                            .border(
                                                width = if (imageTypeSelect == typeKey) 2.dp else 1.dp,
                                                color = if (imageTypeSelect == typeKey) KulinaPurple else KulinaBorder,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { imageTypeSelect = typeKey },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = labelStr, fontSize = 9.sp, fontWeight = FontWeight.Black, color = if (imageTypeSelect == typeKey) KulinaPurpleDark else KulinaText)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        // Reset
                                        showForm = false
                                        editingProduct = null
                                        nameInput = ""
                                        unitInput = ""
                                        priceInput = ""
                                        stockInput = "100"
                                        thresholdInput = "15"
                                        imageTypeSelect = "somay"
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Batal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KulinaPurple)
                                }

                                Button(
                                    onClick = {
                                        val priceVal = priceInput.toLongOrNull() ?: 0L
                                        val nameVal = nameInput.trim()
                                        val unitVal = unitInput.trim()
                                        val stockVal = stockInput.toIntOrNull() ?: 100
                                        val thresholdVal = thresholdInput.toIntOrNull() ?: 15
                                        if (nameVal.isNotEmpty() && unitVal.isNotEmpty() && priceVal > 0L) {
                                            val emojiVal = when (imageTypeSelect) {
                                                "somay" -> "🥟"
                                                "batagor" -> "🍤"
                                                "pempek" -> "🐠"
                                                else -> "🫙"
                                            }
                                            val savedProd = editingProduct?.copy(
                                                name = nameVal,
                                                unit = unitVal,
                                                price = priceVal,
                                                emoji = emojiVal,
                                                imageResType = imageTypeSelect,
                                                stock = stockVal,
                                                threshold = thresholdVal
                                            ) ?: ProductEntity(
                                                name = nameVal,
                                                unit = unitVal,
                                                price = priceVal,
                                                emoji = emojiVal,
                                                imageResType = imageTypeSelect,
                                                stock = stockVal,
                                                threshold = thresholdVal
                                            )
                                            
                                            viewModel.addOrUpdateProduct(savedProd)
                                            android.widget.Toast.makeText(context, "Produk berhasil disimpan!", android.widget.Toast.LENGTH_SHORT).show()

                                            // Reset
                                            showForm = false
                                            editingProduct = null
                                            nameInput = ""
                                            unitInput = ""
                                            priceInput = ""
                                            stockInput = "100"
                                            thresholdInput = "15"
                                            imageTypeSelect = "somay"
                                        } else {
                                            android.widget.Toast.makeText(context, "Mohon lengkapi data dengan benar!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1.5f),
                                    colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Simpan", fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // Header Control Row (Add Button)
                if (!showForm && editingProduct == null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Katalog Bahan Baku Kulina (${products.size} Item)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = KulinaPurpleDark
                        )

                        Button(
                            onClick = {
                                showForm = true
                                editingProduct = null
                                nameInput = ""
                                unitInput = ""
                                priceInput = ""
                                stockInput = "100"
                                thresholdInput = "15"
                                imageTypeSelect = "somay"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KulinaOrange),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                // Scrollable Products Catalog
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 30.dp, start = 16.dp, end = 16.dp)
                ) {
                    item {
                        RechartsFinancialOverview(viewModel = viewModel)
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Low-Stock Alarm System Banner
                    val lowStockItems = products.filter { it.stock < it.threshold }
                    if (lowStockItems.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("low_stock_warning_card"),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFF3CD)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.5.dp, Color(0xFFFFD54F))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFFC107)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("⚠️", fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Peringatan Batas Stok Minimum",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF856404)
                                            )
                                            Text(
                                                text = "${lowStockItems.size} bahan baku kritis butuh re-stock segera.",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF856404).copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = Color(0xFFFFEBAA), thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    lowStockItems.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 5.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1.5f)
                                            ) {
                                                Text(item.emoji, fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = item.name,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color(0xFF533F03)
                                                    )
                                                    Text(
                                                        text = "Tersisa: ${item.stock} / ${item.threshold} ${item.unit}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFFC62828)
                                                    )
                                                }
                                            }

                                            // Quick Restock Button Actions
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Button(
                                                    onClick = { viewModel.restockProduct(item.id, 50) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF856404)),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.height(28.dp).testTag("restock_50_${item.id}")
                                                ) {
                                                    Text("+50", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                }
                                                Button(
                                                    onClick = { viewModel.restockProduct(item.id, 100) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF533F03)),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.height(28.dp).testTag("restock_100_${item.id}")
                                                ) {
                                                    Text("+100", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    items(products.size) { index ->
                        val item = products[index]
                        val isStockWarning = item.stock < item.threshold
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .testTag("product_admin_card_${item.id}"),
                            colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, if (isStockWarning) Color(0xFFEF5350) else KulinaBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Product Image indicator
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(KulinaBorder),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val imageResId = when (item.imageResType) {
                                        "somay" -> com.example.R.drawable.img_somay_1780392776236
                                        "batagor" -> com.example.R.drawable.img_batagor_1780392793000
                                        "pempek" -> com.example.R.drawable.img_pempek_1780392810244
                                        else -> com.example.R.drawable.img_ingredients_1780392830223
                                    }
                                    Image(
                                        painter = androidx.compose.ui.res.painterResource(id = imageResId),
                                        contentDescription = item.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = KulinaText
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = item.emoji,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Text(
                                        text = "${item.unit} | Rp " + String.format("%,d", item.price).replace(',', '.'),
                                        fontSize = 11.sp,
                                        color = KulinaPurple,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    // Stock & Threshold level metadata
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isStockWarning) Color(0xFFFFEBEE) else KulinaBorder,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = if (isStockWarning) Icons.Default.Warning else Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = if (isStockWarning) Color.Red else KulinaGreen,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "Stok: ${item.stock}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isStockWarning) Color(0xFFC62828) else KulinaPurpleDark
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Threshold: ${item.threshold}",
                                            fontSize = 10.sp,
                                            color = KulinaTextMuted,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Edit & Delete operational triggers
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Edit action
                                    IconButton(
                                        onClick = {
                                            editingProduct = item
                                            showForm = false
                                            nameInput = item.name
                                            unitInput = item.unit
                                            priceInput = item.price.toString()
                                            stockInput = item.stock.toString()
                                            thresholdInput = item.threshold.toString()
                                            imageTypeSelect = item.imageResType
                                        },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(KulinaBg, RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = KulinaText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Delete action
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteProduct(item)
                                            android.widget.Toast.makeText(context, "Produk '${item.name}' dihapus!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = Color.Red,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (adminTab == 1) {
            // TAB 1: Pesanan Masuk (Incoming Orders)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Daftar Permintaan Order Masuk 📥",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
                Text(
                    text = "Konfirmasi pembayaran QRIS/Bank & kelola armada pengiriman bahan baku.",
                    fontSize = 11.sp,
                    color = KulinaTextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                
                if (incomingOrders.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada pesanan masuk dari outlet.",
                            color = KulinaTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(incomingOrders.size) { i ->
                            val ord = incomingOrders[i]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, KulinaBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Order ID: #${ord.id}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = KulinaPurple
                                        )
                                        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
                                        Text(
                                            text = sdf.format(Date(ord.orderDate)),
                                            fontSize = 11.sp,
                                            color = KulinaTextMuted,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ord.itemsJson,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = KulinaText
                                    )
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Biaya Ongkir:", fontSize = 11.sp, color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                                        Text("Rp ${String.format("%,d", ord.shippingFee).replace(',', '.')}", fontSize = 11.sp, color = KulinaText, fontWeight = FontWeight.Black)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Total Transaksi:", fontSize = 11.sp, color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                                        Text("Rp ${String.format("%,d", ord.totalPrice + ord.shippingFee).replace(',', '.')}", fontSize = 11.sp, color = KulinaPurpleDark, fontWeight = FontWeight.Black)
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = KulinaBorder)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Badges and Operations Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Payment Badge Toggle Clickable
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (ord.isPaid) KulinaGreen.copy(alpha = 0.15f) else KulinaRed.copy(alpha = 0.15f))
                                                .clickable {
                                                    viewModel.updateOrder(ord.copy(isPaid = !ord.isPaid))
                                                    android.widget.Toast.makeText(context, "Status pembayaran ID #${ord.id} diubah!", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (ord.isPaid) "Lunas ✅" else "Belum Lunas ❌",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (ord.isPaid) KulinaGreen else KulinaRed
                                            )
                                        }
                                        
                                        // Status Indicator Badge
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when (ord.status) {
                                                        "Selesai" -> KulinaGreen.copy(alpha = 0.15f)
                                                        "Dikirim" -> KulinaOrange.copy(alpha = 0.15f)
                                                        else -> KulinaPurple.copy(alpha = 0.1f)
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = ord.status,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = when (ord.status) {
                                                    "Selesai" -> KulinaGreen
                                                    "Dikirim" -> KulinaOrange
                                                    else -> KulinaPurple
                                                }
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.weight(1f))
                                        
                                        // Admin actions on payment
                                        if (!ord.isPaid) {
                                            Button(
                                                onClick = {
                                                    viewModel.updateOrder(ord.copy(isPaid = true))
                                                    android.widget.Toast.makeText(context, "Order #${ord.id} berhasil ditandai LUNAS! ✅", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = KulinaGreen),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.height(28.dp),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("Konfirmasi Bayar", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    // Admin action on status pipeline
                                    Text("Perbarui Status Pengiriman:", fontSize = 9.sp, color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val statuses = listOf("Diproses", "Dikirim", "Selesai")
                                        statuses.forEach { st ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (ord.status == st) KulinaPurple.copy(alpha = 0.15f) else KulinaBg)
                                                    .border(
                                                        width = if (ord.status == st) 1.5.dp else 1.dp,
                                                        color = if (ord.status == st) KulinaPurple else KulinaBorder,
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .clickable {
                                                        viewModel.updateOrder(ord.copy(status = st))
                                                        android.widget.Toast.makeText(context, "Order #${ord.id} berstatus '$st'!", android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = st,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (ord.status == st) KulinaPurpleDark else KulinaText
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (adminTab == 2) {
            // TAB 2: PARTNER MEETINGS (CONFERENCES)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (showMeetingForm || editingMeeting != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, KulinaPurpleLight),
                        colors = CardDefaults.cardColors(containerColor = KulinaCardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (editingMeeting == null) "Jadwalkan Pertemuan Baru 🎥" else "Ubah Jadwal Pertemuan ✏️",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = KulinaPurpleDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = meetingTitleInput,
                                onValueChange = { meetingTitleInput = it },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                label = { Text("Judul Pertemuan", fontSize = 11.sp) },
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = meetingDayInput,
                                    onValueChange = { meetingDayInput = it },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Tanggal Hari (e.g. 05)", fontSize = 11.sp) },
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = meetingMonthInput,
                                    onValueChange = { meetingMonthInput = it },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Bulan (e.g. JUN)", fontSize = 11.sp) },
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = meetingTimeInput,
                                onValueChange = { meetingTimeInput = it },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                label = { Text("Waktu (e.g. 10:00 WIB • Google Meet)", fontSize = 11.sp) },
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = meetingUrlInput,
                                onValueChange = { meetingUrlInput = it },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                label = { Text("Tautan Video Conference", fontSize = 11.sp) },
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Apakah Pertemuan Masa Lalu?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KulinaText)
                                Spacer(modifier = Modifier.weight(1f))
                                Switch(
                                    checked = meetingIsPastInput,
                                    onCheckedChange = { meetingIsPastInput = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = KulinaPurple)
                                )
                            }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                showMeetingForm = false
                                                editingMeeting = null
                                                meetingTitleInput = ""
                                                meetingDayInput = ""
                                                meetingMonthInput = ""
                                                meetingTimeInput = ""
                                                meetingUrlInput = ""
                                                meetingIsPastInput = false
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Batal", fontSize = 12.sp, color = KulinaPurple, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                if (meetingTitleInput.trim().isNotEmpty()) {
                                                    val savedMeeting = editingMeeting?.copy(
                                                        title = meetingTitleInput.trim(),
                                                        day = meetingDayInput.trim(),
                                                        month = meetingMonthInput.trim().uppercase(),
                                                        timeInfo = meetingTimeInput.trim(),
                                                        meetUrl = meetingUrlInput.trim().ifEmpty { "https://meet.google.com/abc-def-ghi" },
                                                        isPast = meetingIsPastInput
                                                    ) ?: PartnerMeeting(
                                                        id = "meet_" + System.currentTimeMillis(),
                                                        title = meetingTitleInput.trim(),
                                                        day = meetingDayInput.trim().ifEmpty { "12" },
                                                        month = meetingMonthInput.trim().uppercase().ifEmpty { "JUN" },
                                                        timeInfo = meetingTimeInput.trim().ifEmpty { "09:00 WIB • Google Meet" },
                                                        meetUrl = meetingUrlInput.trim().ifEmpty { "https://meet.google.com/abc-def-ghi" },
                                                        isRegistered = false,
                                                        isPast = meetingIsPastInput
                                                    )
                                                    
                                                    viewModel.addOrUpdateMeeting(savedMeeting)
                                                    android.widget.Toast.makeText(context, "Pertemuan disimpan!", android.widget.Toast.LENGTH_SHORT).show()

                                                    showMeetingForm = false
                                                    editingMeeting = null
                                                    meetingTitleInput = ""
                                                    meetingDayInput = ""
                                                    meetingMonthInput = ""
                                                    meetingTimeInput = ""
                                                    meetingUrlInput = ""
                                                    meetingIsPastInput = false
                                                } else {
                                                    android.widget.Toast.makeText(context, "Judul wajib diisi!", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1.5f),
                                            colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(text = "Simpan", fontSize = 12.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Jadwal Briefing & Evaluasi 🎥",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KulinaPurpleDark
                                )
                                Text(
                                    text = "Konfigurasi link seminar / webinar koordinasi operasional.",
                                    fontSize = 11.sp,
                                    color = KulinaTextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!showMeetingForm && editingMeeting == null) {
                                Button(
                                    onClick = {
                                        showMeetingForm = true
                                        editingMeeting = null
                                        meetingTitleInput = ""
                                        meetingDayInput = ""
                                        meetingMonthInput = ""
                                        meetingTimeInput = ""
                                        meetingUrlInput = ""
                                        meetingIsPastInput = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = KulinaOrange),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Tambah", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        if (meetings.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("Tidak ada pertemuan tersedia.", color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(meetings.size) { index ->
                                    val meet = meetings[index]
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.dp, KulinaBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(KulinaPurple.copy(alpha = 0.12f)),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(text = meet.day, fontSize = 16.sp, fontWeight = FontWeight.Black, color = KulinaPurpleDark)
                                                Text(text = meet.month, fontSize = 9.sp, fontWeight = FontWeight.Black, color = KulinaPurple)
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = meet.title, fontSize = 13.sp, fontWeight = FontWeight.Black, color = KulinaText)
                                                Text(text = meet.timeInfo, fontSize = 10.sp, color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                                                
                                                if (meet.isPast) {
                                                    Text(text = "Pertemuan Berakhir", fontSize = 9.sp, color = KulinaRed, fontWeight = FontWeight.Black)
                                                } else {
                                                    Text(text = "Aktif • " + meet.platform, fontSize = 9.sp, color = KulinaGreen, fontWeight = FontWeight.Black)
                                                }
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        editingMeeting = meet
                                                        showMeetingForm = false
                                                        meetingTitleInput = meet.title
                                                        meetingDayInput = meet.day
                                                        meetingMonthInput = meet.month
                                                        meetingTimeInput = meet.timeInfo
                                                        meetingUrlInput = meet.meetUrl
                                                        meetingIsPastInput = meet.isPast
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = KulinaText)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteMeeting(meet.id)
                                                        android.widget.Toast.makeText(context, "Pertemuan dihapus!", android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = KulinaRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (adminTab == 3) {
                    // TAB 3: FAQ KEMITRAAN
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (showFaqForm || editingFaq != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.5.dp, KulinaPurpleLight),
                                colors = CardDefaults.cardColors(containerColor = KulinaCardBg)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = if (editingFaq == null) "Tambah FAQ Baru ❔" else "Ubah FAQ ✏️",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaPurpleDark
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = faqQuestionInput,
                                        onValueChange = { faqQuestionInput = it },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        label = { Text("Pertanyaan (Question)", fontSize = 11.sp) },
                                        singleLine = false
                                    )

                                    OutlinedTextField(
                                        value = faqAnswerInput,
                                        onValueChange = { faqAnswerInput = it },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        label = { Text("Jawaban (Answer)", fontSize = 11.sp) },
                                        singleLine = false
                                    )

                                    Text(
                                        text = "Kategori Bantuan:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        val categories = listOf("Umum", "Bahan Baku", "Kemitraan", "CCTV")
                                        categories.forEach { cat ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (faqCategorySelect == cat) KulinaPurple.copy(alpha = 0.15f) else KulinaBg)
                                                    .border(
                                                        width = if (faqCategorySelect == cat) 1.5.dp else 1.dp,
                                                        color = if (faqCategorySelect == cat) KulinaPurple else KulinaBorder,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { faqCategorySelect = cat }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = cat, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (faqCategorySelect == cat) KulinaPurpleDark else KulinaText)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                showFaqForm = false
                                                editingFaq = null
                                                faqQuestionInput = ""
                                                faqAnswerInput = ""
                                                faqCategorySelect = "Umum"
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Batal", fontSize = 12.sp, color = KulinaPurple, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                if (faqQuestionInput.trim().isNotEmpty() && faqAnswerInput.trim().isNotEmpty()) {
                                                    val savedFaq = editingFaq?.copy(
                                                        question = faqQuestionInput.trim(),
                                                        answer = faqAnswerInput.trim(),
                                                        category = faqCategorySelect
                                                    ) ?: FaqItem(
                                                        id = java.util.UUID.randomUUID().toString(),
                                                        question = faqQuestionInput.trim(),
                                                        answer = faqAnswerInput.trim(),
                                                        category = faqCategorySelect
                                                    )
                                                    
                                                    viewModel.addOrUpdateFaq(savedFaq)
                                                    android.widget.Toast.makeText(context, "FAQ berhasil disimpan!", android.widget.Toast.LENGTH_SHORT).show()

                                                    showFaqForm = false
                                                    editingFaq = null
                                                    faqQuestionInput = ""
                                                    faqAnswerInput = ""
                                                    faqCategorySelect = "Umum"
                                                } else {
                                                    android.widget.Toast.makeText(context, "Pertanyaan & jawaban wajib diisi!", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1.5f),
                                            colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(text = "Simpan", fontSize = 12.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Manajemen Panduan FAQ Bantuan ❔",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KulinaPurpleDark
                                )
                                Text(
                                    text = "Ubah panduan atau naskah solusi bantuan mandiri mitra.",
                                    fontSize = 11.sp,
                                    color = KulinaTextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!showFaqForm && editingFaq == null) {
                                Button(
                                    onClick = {
                                        showFaqForm = true
                                        editingFaq = null
                                        faqQuestionInput = ""
                                        faqAnswerInput = ""
                                        faqCategorySelect = "Umum"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = KulinaOrange),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Tambah", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        if (faqs.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("Panduan FAQ kosong.", color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(faqs.size) { index ->
                                    val faq = faqs[index]
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.5.dp, KulinaBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(KulinaPurple.copy(alpha = 0.12f))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(text = faq.category, fontSize = 9.sp, fontWeight = FontWeight.Black, color = KulinaPurpleDark)
                                                }

                                                Spacer(modifier = Modifier.weight(1f))

                                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    IconButton(
                                                        onClick = {
                                                            editingFaq = faq
                                                            showFaqForm = false
                                                            faqQuestionInput = faq.question
                                                            faqAnswerInput = faq.answer
                                                            faqCategorySelect = faq.category
                                                        },
                                                        modifier = Modifier.size(30.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = KulinaText, modifier = Modifier.size(16.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.deleteFaq(faq.id)
                                                            android.widget.Toast.makeText(context, "FAQ dihapus!", android.widget.Toast.LENGTH_SHORT).show()
                                                        },
                                                        modifier = Modifier.size(30.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = KulinaRed, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = "Q: " + faq.question, fontSize = 12.sp, fontWeight = FontWeight.Black, color = KulinaText)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = "A: " + faq.answer, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KulinaTextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (adminTab == 4) {
                    // TAB 4: IoT CCTV CAMERA MANAGERS
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (showCctvForm || editingCctv != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.5.dp, KulinaPurpleLight),
                                colors = CardDefaults.cardColors(containerColor = KulinaCardBg)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = if (editingCctv == null) "Daftarkan NVR CCTV Baru 📡" else "Ubah Kamera CCTV ✏️",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaPurpleDark
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = cctvNameInput,
                                        onValueChange = { cctvNameInput = it },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        label = { Text("Nama Kamera / Ruang", fontSize = 11.sp) },
                                        singleLine = true
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = cctvIpInput,
                                            onValueChange = { cctvIpInput = it },
                                            modifier = Modifier.weight(1.5f),
                                            label = { Text("IP Address Kamera RTSP", fontSize = 11.sp) },
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = cctvEmojiInput,
                                            onValueChange = { cctvEmojiInput = it },
                                            modifier = Modifier.weight(1f),
                                            label = { Text("Emoji Icon", fontSize = 11.sp) },
                                            singleLine = true
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = cctvResolutionInput,
                                            onValueChange = { cctvResolutionInput = it },
                                            modifier = Modifier.weight(1f),
                                            label = { Text("Resolusi (e.g. 1080p FHD)", fontSize = 11.sp) },
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = cctvFpsInput,
                                            onValueChange = { cctvFpsInput = it },
                                            modifier = Modifier.weight(1f),
                                            label = { Text("Frame Rate (FPS)", fontSize = 11.sp) },
                                            singleLine = true
                                        )
                                    }

                                    Text(
                                        text = "Status Aliran Video:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        listOf("ONLINE", "BUFFERING", "OFFLINE").forEach { st ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (cctvStatusSelect == st) KulinaPurple.copy(alpha = 0.15f) else KulinaBg)
                                                    .border(
                                                        width = if (cctvStatusSelect == st) 1.5.dp else 1.dp,
                                                        color = if (cctvStatusSelect == st) KulinaPurple else KulinaBorder,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { cctvStatusSelect = st }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = st, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (cctvStatusSelect == st) KulinaPurpleDark else KulinaText)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                showCctvForm = false
                                                editingCctv = null
                                                cctvNameInput = ""
                                                cctvEmojiInput = "🏪"
                                                cctvStatusSelect = "ONLINE"
                                                cctvIpInput = "192.168.1.100"
                                                cctvResolutionInput = "1080p FHD"
                                                cctvFpsInput = "24"
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Batal", fontSize = 12.sp, color = KulinaPurple, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                if (cctvNameInput.trim().isNotEmpty()) {
                                                    val savedCctv = editingCctv?.copy(
                                                        name = cctvNameInput.trim(),
                                                        emoji = cctvEmojiInput.trim(),
                                                        status = cctvStatusSelect,
                                                        ipAddress = cctvIpInput.trim(),
                                                        resolution = cctvResolutionInput.trim(),
                                                        activeFps = cctvFpsInput.toIntOrNull() ?: 24
                                                    ) ?: CctvCamera(
                                                        id = (cctvCameras.maxOfOrNull { it.id } ?: 0) + 1,
                                                        name = cctvNameInput.trim(),
                                                        emoji = cctvEmojiInput.trim().ifEmpty { "🏪" },
                                                        status = cctvStatusSelect,
                                                        ipAddress = cctvIpInput.trim().ifEmpty { "192.168.1.100" },
                                                        resolution = cctvResolutionInput.trim().ifEmpty { "1080p FHD" },
                                                        activeFps = cctvFpsInput.toIntOrNull() ?: 24
                                                    )
                                                    
                                                    viewModel.addOrUpdateCctv(savedCctv)
                                                    android.widget.Toast.makeText(context, "Kamera CCTV dikonfigurasi!", android.widget.Toast.LENGTH_SHORT).show()

                                                    showCctvForm = false
                                                    editingCctv = null
                                                    cctvNameInput = ""
                                                    cctvEmojiInput = "🏪"
                                                    cctvStatusSelect = "ONLINE"
                                                    cctvIpInput = "192.168.1.100"
                                                    cctvResolutionInput = "1080p FHD"
                                                    cctvFpsInput = "24"
                                                } else {
                                                    android.widget.Toast.makeText(context, "Nama kamera wajib diisi!", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1.5f),
                                            colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(text = "Simpan", fontSize = 12.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Kelola Endpoint IP CCTV IoT 📡",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KulinaPurpleDark
                                )
                                Text(
                                    text = "Tambah atau sesuaikan feed video pengawasan langsung outlet mitra.",
                                    fontSize = 11.sp,
                                    color = KulinaTextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!showCctvForm && editingCctv == null) {
                                Button(
                                    onClick = {
                                        showCctvForm = true
                                        editingCctv = null
                                        cctvNameInput = ""
                                        cctvEmojiInput = "🏪"
                                        cctvStatusSelect = "ONLINE"
                                        cctvIpInput = "192.168.1.100"
                                        cctvResolutionInput = "1080p FHD"
                                        cctvFpsInput = "24"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = KulinaOrange),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Tambah", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        if (cctvCameras.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("Feed CCTV Kosong.", color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(cctvCameras.size) { index ->
                                    val cam = cctvCameras[index]
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = KulinaCardBg),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.5.dp, KulinaBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(KulinaBorder),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = cam.emoji, fontSize = 20.sp)
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = cam.name, fontSize = 12.sp, fontWeight = FontWeight.Black, color = KulinaText)
                                                Text(text = cam.ipAddress, fontSize = 10.sp, color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                                                
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.padding(top = 2.dp)
                                                ) {
                                                    val badgeBg = when (cam.status) {
                                                        "ONLINE" -> KulinaGreen.copy(alpha = 0.15f)
                                                        "BUFFERING" -> KulinaOrange.copy(alpha = 0.15f)
                                                        else -> KulinaRed.copy(alpha = 0.15f)
                                                    }
                                                    val badgeText = when (cam.status) {
                                                        "ONLINE" -> KulinaGreen
                                                        "BUFFERING" -> KulinaOrange
                                                        else -> KulinaRed
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(badgeBg)
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(text = cam.status, fontSize = 8.sp, fontWeight = FontWeight.Black, color = badgeText)
                                                    }

                                                    Text(text = "${cam.resolution} | FPS: ${cam.activeFps}", fontSize = 9.sp, color = KulinaTextMuted, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        editingCctv = cam
                                                        showCctvForm = false
                                                        cctvNameInput = cam.name
                                                        cctvEmojiInput = cam.emoji
                                                        cctvStatusSelect = cam.status
                                                        cctvIpInput = cam.ipAddress
                                                        cctvResolutionInput = cam.resolution
                                                        cctvFpsInput = cam.activeFps.toString()
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = KulinaText)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteCctv(cam.id)
                                                        android.widget.Toast.makeText(context, "Kamera feed dihapus!", android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = KulinaRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
    }
}
    }
}

data class RechartsDataPoint(
    val label: String,
    val value: Float,
    val formattedValue: String
)

@Composable
fun RechartsFinancialOverview(viewModel: KulinaViewModel) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Daily, 1 = Monthly
    var hoveredIndex by remember { mutableStateOf(-1) }
    
    val txns by viewModel.transactions.collectAsState()
    val extraInflow = txns.filter { it.type == "in" }.sumOf { it.amount }.toFloat()
    
    // Calculate daily dataset
    val dailyPoints = listOf(
        RechartsDataPoint("Sen", 1200000f, "Rp 1.200.000"),
        RechartsDataPoint("Sel", 2050000f, "Rp 2.050.000"),
        RechartsDataPoint("Rab", 1600000f, "Rp 1.600.000"),
        RechartsDataPoint("Kam", 2400000f, "Rp 2.400.000"),
        RechartsDataPoint("Jum", 3100000f, "Rp 3.100.000"),
        RechartsDataPoint("Sab", 3850000f, "Rp 3.850.000"),
        RechartsDataPoint("Min", 4500000f + extraInflow, "Rp " + String.format("%,d", (4500000f + extraInflow).toLong()).replace(',', '.'))
    )
    
    // Calculate monthly dataset
    val monthlyPoints = listOf(
        RechartsDataPoint("Jan", 32500000f, "Rp 32.5Jt"),
        RechartsDataPoint("Feb", 38200000f, "Rp 38.2Jt"),
        RechartsDataPoint("Mar", 41800000f, "Rp 41.8Jt"),
        RechartsDataPoint("Apr", 48900000f, "Rp 48.9Jt"),
        RechartsDataPoint("Mei", 54400000f, "Rp 54.4Jt"),
        RechartsDataPoint("Jun", 61200000f + extraInflow, "Rp " + String.format("Rp %,d", (61200000f + extraInflow).toLong()).replace(',', '.'))
    )
    
    val points = if (selectedTab == 0) dailyPoints else monthlyPoints
    val maxVal = if (selectedTab == 0) 5000000f else 70000000f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("financial_overview_recharts"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, KulinaBorder),
        colors = CardDefaults.cardColors(containerColor = KulinaCardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Recharts Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kulina Recharts Engine 📊",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurpleDark
                    )
                    Text(
                        text = "Grafik analitik tren pendapatan real-time",
                        fontSize = 11.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Live Indicator Dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF2E7D32), CircleShape)
                    )
                    Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Tab Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KulinaBg, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Harian (Temukan Pola)", "Bulanan (Ringkasan)").forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == index) KulinaPurple else Color.Transparent)
                            .clickable {
                                selectedTab = index
                                hoveredIndex = -1 // Reset index on tab change
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (selectedTab == index) Color.White else KulinaText
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Highlight Metric Header
            val totalInPeriod = points.sumOf { it.value.toLong() }
            val avgInPeriod = totalInPeriod / points.size
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = if (selectedTab == 0) "Estimasi Omset 7 Hari" else "Estimasi Omset Semester",
                        fontSize = 10.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rp " + String.format("%,d", totalInPeriod).replace(',', '.'),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaOrange
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Rata-rata Pendapatan",
                        fontSize = 10.sp,
                        color = KulinaTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rp " + String.format("%,d", avgInPeriod).replace(',', '.'),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = KulinaPurpleDark
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Outer Frame for Cartesian Graphics
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFCFBFE), RoundedCornerShape(12.dp))
                    .border(1.dp, KulinaBorder, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                // Interactive Chart Zone
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    // Y-Axis labels
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(42.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        val tickLabels = if (selectedTab == 0) {
                            listOf("5.0Jt", "3.75Jt", "2.5Jt", "1.25Jt", "0")
                        } else {
                            listOf("70Jt", "52.5Jt", "35Jt", "17.5Jt", "0")
                        }
                        tickLabels.forEach {
                            Text(it, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = KulinaTextMuted)
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Main Drawing Block
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(points) {
                                    detectTapGestures(
                                        onTap = { offset ->
                                            val stepX = size.width.toFloat() / (points.size - 1)
                                            hoveredIndex = (offset.x / stepX).toInt().coerceIn(0, points.size - 1)
                                        }
                                    )
                                }
                        ) {
                            val W = size.width
                            val H = size.height
                            val numPoints = points.size
                            val stepX = W / (numPoints - 1)
                            
                            // 1. Draw Cartesian Grid Lines (Subtle horizontal dotted grids)
                            for (gridIdx in 0..4) {
                                val gridY = (H / 4) * gridIdx
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.35f),
                                    start = androidx.compose.ui.geometry.Offset(0f, gridY),
                                    end = androidx.compose.ui.geometry.Offset(W, gridY),
                                    strokeWidth = 2f
                                )
                            }
                            
                            // 2. Draw Vector Graphic Trends (AreaChart for Daily / BarChart for Monthly)
                            if (selectedTab == 0) {
                                // Area Curve Path (Teal/Purple Recharts Style!)
                                val trendPath = androidx.compose.ui.graphics.Path().apply {
                                    val startY = H - (points[0].value / maxVal) * H
                                    moveTo(0f, startY)
                                    for (i in 1 until numPoints) {
                                        val curX = i * stepX
                                        val curY = H - (points[i].value / maxVal) * H
                                        lineTo(curX, curY)
                                    }
                                }
                                
                                // Draw Area fill translucent gradient
                                val fillPath = androidx.compose.ui.graphics.Path().apply {
                                    addPath(trendPath)
                                    lineTo((numPoints - 1) * stepX, H)
                                    lineTo(0f, H)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(KulinaPurple.copy(alpha = 0.3f), Color.Transparent),
                                        startY = 0f,
                                        endY = H
                                    )
                                )
                                
                                // Draw main bezier outline line
                                drawPath(
                                    path = trendPath,
                                    color = KulinaPurple,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = 6f,
                                        join = androidx.compose.ui.graphics.StrokeJoin.Round,
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    )
                                )
                                
                                // Draw regular mini node dots
                                for (i in 0 until numPoints) {
                                    val curX = i * stepX
                                    val curY = H - (points[i].value / maxVal) * H
                                    drawCircle(
                                        color = Color.White,
                                        radius = 6f,
                                        center = androidx.compose.ui.geometry.Offset(curX, curY)
                                    )
                                    drawCircle(
                                        color = KulinaPurple,
                                        radius = 6f,
                                        center = androidx.compose.ui.geometry.Offset(curX, curY),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                                    )
                                }
                                
                                // Render selected interactive highlight overlay
                                if (hoveredIndex != -1 && hoveredIndex < numPoints) {
                                    val hoverX = hoveredIndex * stepX
                                    val hoverY = H - (points[hoveredIndex].value / maxVal) * H
                                    
                                    // Vertical dashed guide line
                                    drawLine(
                                        color = KulinaPurpleLight.copy(alpha = 0.7f),
                                        start = androidx.compose.ui.geometry.Offset(hoverX, 0f),
                                        end = androidx.compose.ui.geometry.Offset(hoverX, H),
                                        strokeWidth = 3f
                                    )
                                    
                                    // Highlight node core
                                    drawCircle(
                                        color = Color.White,
                                        radius = 12f,
                                        center = androidx.compose.ui.geometry.Offset(hoverX, hoverY)
                                    )
                                    drawCircle(
                                        color = KulinaOrange,
                                        radius = 12f,
                                        center = androidx.compose.ui.geometry.Offset(hoverX, hoverY),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f)
                                    )
                                }
                            } else {
                                // Monthly Bar Chart Style with modern rounded pillars
                                val barWidth = W / (numPoints * 2f)
                                for (i in 0 until numPoints) {
                                    val barH = (points[i].value / maxVal) * H
                                    val barX = i * (W / numPoints) + (W / numPoints - barWidth) / 2f
                                    val barY = H - barH
                                    
                                    val barColor = if (hoveredIndex == i) KulinaOrange else KulinaPurple
                                    
                                    // Draw rounded column bar pillar
                                    drawRoundRect(
                                        color = barColor,
                                        topLeft = androidx.compose.ui.geometry.Offset(barX, barY),
                                        size = androidx.compose.ui.geometry.Size(barWidth, barH),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                                    )
                                    
                                    // Add a sleek border outline for active hovered pillar
                                    if (hoveredIndex == i) {
                                        drawRoundRect(
                                            color = KulinaPurpleDark,
                                            topLeft = androidx.compose.ui.geometry.Offset(barX, barY),
                                            size = androidx.compose.ui.geometry.Size(barWidth, barH),
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Floating Interactive Tooltip Card overlay (Recharts feature!)
                        if (hoveredIndex != -1 && hoveredIndex < points.size) {
                            val activePt = points[hoveredIndex]
                            Card(
                                modifier = Modifier
                                    .align(if (hoveredIndex < points.size / 2) Alignment.TopEnd else Alignment.TopStart)
                                    .padding(8.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, KulinaOrange),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Periode: ${activePt.label}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KulinaPurpleDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = activePt.formattedValue,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KulinaOrange
                                    )
                                    Text(
                                        text = "Status: Target Terlampaui ✓",
                                        fontSize = 10.sp,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // X-Axis labels row under the graphics Canvas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    points.forEachIndexed { index, pt ->
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .clickable { hoveredIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pt.label,
                                fontSize = 10.sp,
                                fontWeight = if (hoveredIndex == index) FontWeight.Black else FontWeight.Bold,
                                color = if (hoveredIndex == index) KulinaOrange else KulinaTextMuted
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Recharts Interactive Legend Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(KulinaPurple, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Realisasi Omset Mitra (Real-time)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KulinaText)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(KulinaOrange, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Puncak Target", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KulinaText)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Touch Tip Explanation
            Text(
                text = "💡 Tip Admin: Sentuh/klik area grafik atau label hari di atas untuk membuka rekap rincian data omset secara interaktif.",
                fontSize = 10.sp,
                color = KulinaPurpleDark,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KulinaBg, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun FaqScreen(
    viewModel: KulinaViewModel,
    onBack: () -> Unit
) {
    val faqs by viewModel.faqs.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    
    val categories = listOf("Semua", "Bahan Baku", "Kemitraan", "Pembayaran & Finance", "CCTV")
    
    // Filtering FAQs based on search and selected category
    val filteredFaqs = faqs.filter { faq ->
        val matchesCategory = selectedCategory == "Semua" || faq.category == selectedCategory
        val matchesSearch = faq.question.contains(searchQuery, ignoreCase = true) || 
                            faq.answer.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
    ) {
        InnerHeader(title = "FAQ & Panduan Mitra", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Search Input Block
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari solusi atau pertanyaan umum...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = KulinaPurple
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = KulinaRed)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("faq_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KulinaPurple,
                    unfocusedBorderColor = KulinaBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontally Scrollable Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) KulinaPurple else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else KulinaBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("faq_chip_$cat"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else KulinaPurpleDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FAQ Count and Category Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pertanyaan Terkait: ${filteredFaqs.size} Topik",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaPurpleDark
                )
                
                if (selectedCategory != "Semua") {
                    Box(
                        modifier = Modifier
                            .background(KulinaPurple.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = selectedCategory,
                            fontSize = 10.sp,
                            color = KulinaPurple,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // FAQ Collapsible Item List (Accordion Cards)
            if (filteredFaqs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, KulinaBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Topik Tidak Ditemukan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = KulinaText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Silakan ganti kata kunci pencarian Anda atau kembalikan filter kategori ke 'Semua'.",
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            color = KulinaTextMuted
                        )
                    }
                }
            } else {
                filteredFaqs.forEach { faq ->
                    // FAQ Accordion Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .shadow(if (faq.isExpanded) 1.5.dp else 0.dp, RoundedCornerShape(12.dp))
                            .testTag("faq_item_${faq.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, if (faq.isExpanded) KulinaPurple.copy(alpha = 0.6f) else KulinaBorder)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Expandable Header Action area (touch target min 48dp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleFaqExpand(faq.id) }
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "Q:",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = KulinaPurple
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = faq.question,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = KulinaText,
                                        lineHeight = 16.sp
                                    )
                                }

                                Icon(
                                    imageVector = if (faq.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand FAQ",
                                    tint = KulinaPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Dynamic expanded content
                            AnimatedVisibility(
                                visible = faq.isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(KulinaBg.copy(alpha = 0.6f))
                                        .padding(14.dp)
                                ) {
                                    Divider(color = KulinaBorder)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text(
                                            text = "A:",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = KulinaOrange
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = faq.answer,
                                            fontSize = 12.sp,
                                            color = KulinaText.copy(alpha = 0.9f),
                                            lineHeight = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = KulinaBorder.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Interactivity within card: Helpful thumbs feedback!
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Apakah jawaban ini membantu?",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KulinaTextMuted
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            var answerFeedbackGiven by remember { mutableStateOf(false) }
                                            
                                            Button(
                                                onClick = {
                                                    answerFeedbackGiven = true
                                                    viewModel.showNotification(
                                                        title = "Terima Kasih! 👍",
                                                        message = "Saran Anda membantu kami memperbaiki materi panduan mitra.",
                                                        type = NotificationType.INFO
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (answerFeedbackGiven) KulinaPurple.copy(alpha = 0.1f) else Color.White,
                                                    contentColor = KulinaPurple
                                                ),
                                                border = BorderStroke(1.dp, KulinaBorder),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                modifier = Modifier.height(24.dp)
                                            ) {
                                                Text("Ya", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    answerFeedbackGiven = true
                                                    viewModel.showNotification(
                                                        title = "Masukan Dicatat! 🙏",
                                                        message = "Kami segera menyempurnakan rincian jawaban.",
                                                        type = NotificationType.INFO
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color.White,
                                                    contentColor = KulinaTextMuted
                                                ),
                                                border = BorderStroke(1.dp, KulinaBorder),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                modifier = Modifier.height(24.dp)
                                            ) {
                                                Text("Tidak", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Lower Call to Action Block: Direct routing to Live Chat
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = KulinaPurpleDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Belum menemukan solusi?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Admin Support kami siap berdiskusi langsung dan menyelesaikan keluhan Anda H24.",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.navigateTo(Screen.Chat) },
                        colors = ButtonDefaults.buttonColors(containerColor = KulinaYellow, contentColor = KulinaPurpleDark),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("faq_chat_support_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Chat Support",
                            tint = KulinaPurpleDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hubungi Live Chat Support",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: KulinaViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KulinaBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo / Branding Header
            Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_kulina_logo_1780500709785),
                contentDescription = "Kulina Logo",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, KulinaYellow, CircleShape)
                    .background(Color.White),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kulina Mitra Portal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = KulinaPurpleDark,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Sistem Koordinasi Mitra & Admin Pusat",
                fontSize = 12.sp,
                color = KulinaTextMuted,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, KulinaBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk Portal Berpasword",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = KulinaPurple,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        placeholder = { Text("cth. gading / admin") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = KulinaPurple)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_username_field"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KulinaPurple,
                            unfocusedBorderColor = KulinaBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Kata Sandi") },
                        placeholder = { Text("Masukkan sandi") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = KulinaPurple)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Info else Icons.Default.Lock,
                                    contentDescription = "Toggle Password",
                                    tint = KulinaTextMuted
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_field"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KulinaPurple,
                            unfocusedBorderColor = KulinaBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                android.widget.Toast.makeText(context, "Username dan Kata Sandi wajib diisi!", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.attemptLogin(username, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = KulinaPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Masuk",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Login Ke Portal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick access shortcut cards/pills (extremely helpful for testers)
            Text(
                text = "PINTASAN LOGIN CEPAT (KLIK UNTUK ISI)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = KulinaTextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickLoginPill(
                        title = "Admin Pusat",
                        subtitle = "Sandi: admin",
                        icon = "🔑",
                        color = KulinaYellow,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            username = "admin"
                            password = "admin"
                            viewModel.attemptLogin("admin", "admin")
                        }
                    )
                    QuickLoginPill(
                        title = "Kelapa Gading",
                        subtitle = "Sandi: gading",
                        icon = "🏪",
                        color = Color(0xFFE8F2FF),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            username = "gading"
                            password = "gading"
                            viewModel.attemptLogin("gading", "gading")
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickLoginPill(
                        title = "Mitra Cibubur",
                        subtitle = "Sandi: cibubur",
                        icon = "🍽️",
                        color = Color(0xFFFFF0F0),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            username = "cibubur"
                            password = "cibubur"
                            viewModel.attemptLogin("cibubur", "cibubur")
                        }
                    )
                    QuickLoginPill(
                        title = "Mitra Serpong",
                        subtitle = "Sandi: serpong",
                        icon = "🛒",
                        color = Color(0xFFEAF9EE),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            username = "serpong"
                            password = "serpong"
                            viewModel.attemptLogin("serpong", "serpong")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickLoginPill(
    title: String,
    subtitle: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, KulinaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = KulinaText
                )
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = KulinaTextMuted
                )
            }
        }
    }
}



