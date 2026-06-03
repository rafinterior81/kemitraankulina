package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface Screen {
    object Home : Screen
    object Info : Screen
    object Order : Screen
    object OrderHistory : Screen // Beautiful screen for order tracking and efficiency!
    object Outlet : Screen
    object Finance : Screen
    object Cctv : Screen
    object Meeting : Screen
    object Promo : Screen
    object Reward : Screen
    object Chat : Screen
    object Support : Screen
    object Feedback : Screen
    object AdminDashboard : Screen
    object Faq : Screen
}

enum class NotificationType {
    ORDER_PLACED,
    PAYMENT_CONFIRMED,
    INFO
}

data class InAppNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis()
)

data class CctvCamera(
    val id: Int,
    val name: String,
    val emoji: String,
    val status: String, // "ONLINE", "BUFFERING", "OFFLINE"
    val ipAddress: String,
    val lastSync: String = "Sinkron Berhasil",
    val activeFps: Int = 18,
    val resolution: String = "1080p FHD"
)

data class ChatMessage(
    val sender: String, // "Tim Kulina" or "Saya"
    val content: String,
    val time: String,
    val isUser: Boolean
)

data class PartnerMeeting(
    val id: String,
    val title: String,
    val day: String,
    val month: String,
    val timeInfo: String,
    val platform: String = "Google Meet",
    val meetUrl: String = "https://meet.google.com/abc-defg-hij",
    val currentParticipants: Int = 0,
    val isRegistered: Boolean = false,
    val isPast: Boolean = false,
    val durationText: String = ""
)

data class FaqItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val answer: String,
    val category: String, // "Umum", "Bahan Baku", "Kemitraan", "Pembayaran & Finance", "CCTV"
    val isExpanded: Boolean = false
)

data class UserSession(
    val username: String,
    val role: String, // "ADMIN" or "MITRA"
    val outletName: String // "Kelapa Gading", "Cibubur", "Serpong", or "Semua" for Admin
)

class KulinaViewModel(private val repository: MitraRepository) : ViewModel() {

    // Authentication Session State
    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession: StateFlow<UserSession?> = _userSession.asStateFlow()

    fun attemptLogin(usernameInput: String, passwordInput: String): Boolean {
        val user = usernameInput.trim().lowercase()
        val pass = passwordInput.trim()

        val session = when {
            user == "admin" && pass == "admin" -> {
                UserSession(username = "Admin Pusat", role = "ADMIN", outletName = "Semua")
            }
            user == "gading" && pass == "gading" -> {
                UserSession(username = "Mitra Kelapa Gading", role = "MITRA", outletName = "Kelapa Gading")
            }
            user == "cibubur" && pass == "cibubur" -> {
                UserSession(username = "Mitra Cibubur", role = "MITRA", outletName = "Cibubur")
            }
            user == "serpong" && pass == "serpong" -> {
                UserSession(username = "Mitra Serpong", role = "MITRA", outletName = "Serpong")
            }
            else -> null
        }

        if (session != null) {
            _userSession.value = session
            showNotification(
                title = "Akses Diberikan! 🔑",
                message = "Selamat datang, ${session.username}. Masuk sebagai ${session.role}.",
                type = NotificationType.INFO
            )
            return true
        } else {
            showNotification(
                title = "Gagal Masuk ⛔",
                message = "Username atau Password yang Anda masukkan salah.",
                type = NotificationType.INFO
            )
            return false
        }
    }

    fun logout() {
        _userSession.value = null
        _currentScreen.value = Screen.Home
        showNotification(
            title = "Logged Out 🔒",
            message = "Sesi Anda telah diakhiri dengan aman.",
            type = NotificationType.INFO
        )
    }

    // Seeding mock database tables on startup
    init {
        viewModelScope.launch {
            repository.seedMockData()
        }
    }

    // In-App Notification System Flow
    private val _notifications = MutableStateFlow<List<InAppNotification>>(emptyList())
    val notifications: StateFlow<List<InAppNotification>> = _notifications.asStateFlow()

    fun showNotification(title: String, message: String, type: NotificationType) {
        val notif = InAppNotification(title = title, message = message, type = type)
        _notifications.value = _notifications.value + notif
        
        // Auto-dismiss after 6 seconds
        viewModelScope.launch {
            delay(6000)
            dismissNotification(notif.id)
        }
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // CCTV state properties and live sync
    private val _cctvCameras = MutableStateFlow<List<CctvCamera>>(
        listOf(
            CctvCamera(1, "Kelapa Gading Cam 1 (Depan)", "🏪", "ONLINE", "192.168.1.101", "Sinkron Berhasil, Realtime", 24, "1080p FHD"),
            CctvCamera(2, "Cibubur Cam 1 (Kasir)", "🍽️", "ONLINE", "192.168.1.102", "Sinkron Berhasil, Realtime", 20, "1080p FHD"),
            CctvCamera(3, "Cibubur Cam 2 (Dapur)", "👥", "BUFFERING", "192.168.1.103", "Sinkron Berhasil, Realtime", 0, "720p HD"),
            CctvCamera(4, "Serpong Cam 1 (Outdoor)", "🛒", "ONLINE", "192.168.1.104", "Sinkron Berhasil, Realtime", 15, "1080p FHD"),
            CctvCamera(5, "Kelapa Gading Cam 2 (Gudang)", "📦", "OFFLINE", "192.168.1.105", "Gagal Terkoneksi", 0, "N/A")
        )
    )
    val cctvCameras: StateFlow<List<CctvCamera>> = _cctvCameras.asStateFlow()

    private val _cctvBaseIpState = MutableStateFlow("192.168.1.100")
    val cctvBaseIpState: StateFlow<String> = _cctvBaseIpState.asStateFlow()

    private val _isCctvSyncing = MutableStateFlow(false)
    val isCctvSyncing: StateFlow<Boolean> = _isCctvSyncing.asStateFlow()

    private val _cctvSyncProgress = MutableStateFlow(0f)
    val cctvSyncProgress: StateFlow<Float> = _cctvSyncProgress.asStateFlow()

    private val _cctvSyncMessage = MutableStateFlow("")
    val cctvSyncMessage: StateFlow<String> = _cctvSyncMessage.asStateFlow()

    fun updateCctvBaseIp(newIp: String) {
        _cctvBaseIpState.value = newIp
    }

    fun syncCctvWithIp(baseIp: String) {
        viewModelScope.launch {
            _isCctvSyncing.value = true
            _cctvSyncProgress.value = 0f
            _cctvSyncMessage.value = "Menginisialisasi pencarian di subnet IP '$baseIp'..."
            delay(1000)
            
            _cctvSyncProgress.value = 0.25f
            _cctvSyncMessage.value = "Memindai port RTSP 554/8554 kelayakan streaming..."
            delay(1200)

            _cctvSyncProgress.value = 0.55f
            _cctvSyncMessage.value = "Berhasil menemukan 5 unit CCTV outlet. Memverifikasi SSL & Token Handshake..."
            delay(1100)

            _cctvSyncProgress.value = 0.85f
            _cctvSyncMessage.value = "Mengonfigurasi pengiriman paket real-time ke $baseIp..."
            delay(900)

            _cctvSyncProgress.value = 1.0f
            _cctvSyncMessage.value = "Sinkronisasi Berhasil! Seluruh link CCTV kini terhubung penuh."
            delay(800)

            val cleanedIpHost = if (baseIp.contains(".")) baseIp.substringBeforeLast(".") else "192.168.1"
            val updatedCams = _cctvCameras.value.map { cam ->
                val lastOctet = 100 + cam.id
                val newCamIp = "$cleanedIpHost.$lastOctet"
                cam.copy(
                    status = "ONLINE",
                    ipAddress = newCamIp,
                    lastSync = "Sertifikat SSL Cocok • Realtime",
                    activeFps = (20..30).random(),
                    resolution = "1080p FHD"
                )
            }
            _cctvCameras.value = updatedCams
            _isCctvSyncing.value = false

            showNotification(
                title = "CCTV IP Terhubung Real-Time! 🎥",
                message = "Berhasil mensinkronkan 5 kamera pada base IP '$baseIp'. Transmisi video langsung aktif.",
                type = NotificationType.PAYMENT_CONFIRMED
            )
        }
    }

    // Room Database Flow collections to reactive UI states
    val orders: StateFlow<List<BahanBakuOrder>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<FinanceTransaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile: StateFlow<MitraProfileEntity?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val redeemedRewards: StateFlow<List<RedeemedReward>> = repository.redeemedRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun redeemReward(title: String, emoji: String, pointsSpent: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val profVal = profile.value ?: MitraProfileEntity()
            if (profVal.points >= pointsSpent) {
                // Deduct points
                val updatedProf = profVal.copy(points = profVal.points - pointsSpent)
                repository.updateProfile(updatedProf)

                // Generate voucher code
                val codeNum = (10000 + (Math.random() * 90000).toInt())
                val voucherCode = "KLN-REWARD-$codeNum"

                // Save RedeemedReward inside Room DB
                val newReward = RedeemedReward(
                    title = title,
                    emoji = emoji,
                    pointsSpent = pointsSpent,
                    code = voucherCode,
                    redeemedAt = System.currentTimeMillis()
                )
                repository.insertRedeemedReward(newReward)

                // Trigger nice notification
                showNotification(
                    title = "Reward Berhasil Ditukar! 🎉",
                    message = "Voucher '$title' berhasil didapatkan! Gunakan kode: $voucherCode",
                    type = NotificationType.PAYMENT_CONFIRMED
                )
                
                onSuccess()
            } else {
                showNotification(
                    title = "Gagal Menukar Reward ❌",
                    message = "Poin loyalty Anda tidak mencukupi untuk ditukar dengan reward '$title'.",
                    type = NotificationType.INFO
                )
            }
        }
    }

    // Products / Ingredients Lists loaded dynamically from Room Database
    val products: StateFlow<List<ProductEntity>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin helper methods
    fun addOrUpdateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
            // Check if added/modified product stock is below its threshold
            if (product.stock < product.threshold) {
                showNotification(
                    title = "Stok Kritis Terdeteksi! ⚠️",
                    message = "Bahan '${product.name}' berada di bawah threshold: ${product.stock} < ${product.threshold}",
                    type = NotificationType.INFO
                )
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun restockProduct(productId: Int, amount: Int) {
        viewModelScope.launch {
            val p = products.value.find { it.id == productId }
            if (p != null) {
                val updatedP = p.copy(stock = p.stock + amount)
                repository.insertProduct(updatedP)
                showNotification(
                    title = "Stok Ditambah! 📦",
                    message = "Berhasil menambah +$amount stok untuk ${p.name}. Total sekarang: ${updatedP.stock}",
                    type = NotificationType.PAYMENT_CONFIRMED
                )
            }
        }
    }

    fun updateThreshold(productId: Int, newThreshold: Int) {
        viewModelScope.launch {
            val p = products.value.find { it.id == productId }
            if (p != null) {
                val updatedP = p.copy(threshold = newThreshold)
                repository.insertProduct(updatedP)
                showNotification(
                    title = "Threshold Diperbarui ⚙️",
                    message = "Threshold minimum '${p.name}' diubah menjadi $newThreshold ${p.unit}.",
                    type = NotificationType.INFO
                )
            }
        }
    }

    private val _quantities = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quantities: StateFlow<Map<Int, Int>> = _quantities.asStateFlow()

    fun updateQuantity(productId: Int, change: Int) {
        val currentMap = _quantities.value.toMutableMap()
        val currentQty = currentMap[productId] ?: 0
        val newQty = (currentQty + change).coerceAtLeast(0)
        currentMap[productId] = newQty
        _quantities.value = currentMap
    }

    val totalOrderPrice: StateFlow<Long> = _quantities.map { qtyMap ->
        qtyMap.entries.sumOf { (id, qty) ->
            (products.value.find { it.id == id }?.price ?: 0L) * qty
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val selectedItemsCount: StateFlow<Int> = _quantities.map { qtyMap ->
        qtyMap.values.sum()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun resetCart() {
        _quantities.value = emptyMap()
    }

    fun submitOrder(shippingFee: Long, paymentMethod: String, onSuccess: () -> Unit) {
        val qtyMap = _quantities.value
        if (qtyMap.values.sum() == 0) return

        viewModelScope.launch {
            // Format labels summary
            val itemsSummary = qtyMap.filter { it.value > 0 }.entries.joinToString(", ") { (id, qty) ->
                val p = products.value.find { it.id == id }
                "${p?.name ?: ""} × $qty pack"
            }
            val totalPriceVal = qtyMap.entries.sumOf { (id, qty) ->
                (products.value.find { it.id == id }?.price ?: 0L) * qty
            }

            // Insert dynamic database entry for materials order
            val newOrder = BahanBakuOrder(
                orderDate = System.currentTimeMillis(),
                itemsJson = itemsSummary,
                totalPrice = totalPriceVal,
                status = "Diproses",
                shippingFee = shippingFee,
                paymentMethod = paymentMethod,
                isPaid = false
            )
            repository.insertOrder(newOrder)

            // Deduct stock levels for materials ordered & check low stock alerting
            qtyMap.filter { it.value > 0 }.forEach { (id, qty) ->
                val prod = products.value.find { it.id == id }
                if (prod != null) {
                    val newStock = (prod.stock - qty).coerceAtLeast(0)
                    val updatedProd = prod.copy(stock = newStock)
                    repository.insertProduct(updatedProd)

                    // Critical alert triggered for the admin when specific raw materials fall below a defined threshold
                    if (newStock < prod.threshold) {
                        showNotification(
                            title = "Peringatan Stok Rendah! ⚠️",
                            message = "Stok bahan '${prod.name}' menipis tinggal $newStock ${prod.unit} (Batas minimum: ${prod.threshold})",
                            type = NotificationType.INFO
                        )
                    }
                }
            }

            // Log corresponding financial transaction
            val newTxn = FinanceTransaction(
                title = "Pembelian Bahan Baku",
                timestamp = System.currentTimeMillis(),
                amount = totalPriceVal + shippingFee,
                type = "out",
                desc = "Bahan: $itemsSummary (Ongkir: Rp $shippingFee via $paymentMethod)"
            )
            repository.insertTransaction(newTxn)

            // Update partner's loyalty points balance dynamically! (e.g. 1 point per 500 IDR)
            val p = profile.value ?: MitraProfileEntity()
            val pointsEarned = ((totalPriceVal + shippingFee) / 500).toInt()
            val updatedProfile = p.copy(
                points = p.points + pointsEarned
            )
            repository.updateProfile(updatedProfile)

            // Reset cart
            resetCart()

            showNotification(
                title = "Pesanan Bahan Baku Dibuat! 📦",
                message = "Pesanan untuk $itemsSummary berhasil didaftarkan.",
                type = NotificationType.ORDER_PLACED
            )
            
            onSuccess()
        }
    }

    fun updateOrder(order: BahanBakuOrder) {
        viewModelScope.launch {
            val oldOrder = orders.value.find { it.id == order.id }
            val wasPaidBefore = oldOrder?.isPaid ?: false

            repository.insertOrder(order)

            if (order.isPaid && !wasPaidBefore) {
                val totalAmount = order.totalPrice + order.shippingFee
                showNotification(
                    title = "Pembayaran Dikonfirmasi Lunas 💵",
                    message = "Pembayaran untuk Order #${order.id} sebesar Rp ${String.format("%,d", totalAmount).replace(',', '.')} telah terkonfirmasi lunas.",
                    type = NotificationType.PAYMENT_CONFIRMED
                )
            }
        }
    }

    // Chat list state management
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("Tim Kulina", "Halo Pak Budi! Selamat datang di Kulina Mitra. Ada yang bisa kami bantu? 😊", "09:00", false),
            ChatMessage("Saya", "Halo, mau tanya soal jadwal pengiriman bahan baku minggu ini", "09:05", true),
            ChatMessage("Tim Kulina", "Pengiriman jadwal Selasa & Jumat ya Pak. Untuk minggu ini Selasa 27 Mei pukul 08.00–12.00. Mau konfirmasi sekarang?", "09:07", false)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _typedMessage = MutableStateFlow("")
    val typedMessage: StateFlow<String> = _typedMessage.asStateFlow()

    fun updateTypedMessage(text: String) {
        _typedMessage.value = text
    }

    fun sendChatMessage() {
        val text = _typedMessage.value.trim()
        if (text.isEmpty()) return

        val calendar = java.util.Calendar.getInstance()
        val timeStr = String.format("%02d:%02d", calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE))

        val userMsg = ChatMessage("Saya", text, timeStr, true)
        val list = _chatMessages.value.toMutableList()
        list.add(userMsg)
        _chatMessages.value = list
        _typedMessage.value = ""

        viewModelScope.launch {
            delay(1000)
            val autoReplies = listOf(
                "Terima kasih pesannya! Tim kami akan segera membalas dalam beberapa menit. 🙏",
                "Baik Pak Budi, keluhan / kendala Anda telah diteruskan ke tim pengiriman Kulina. Mohon ditunggu ya.",
                "Pesanan bahan baku Anda sudah tercatat di sistem kami dan sedang diproses dengan prioritas tinggi! ⚡",
                "Poin Reward Anda sudah kami bantu sinkronisasi. Selamat mengumpulkan poin lebih banyak!"
            )
            val replyText = autoReplies.random()
            val replyMsg = ChatMessage("Tim Kulina", replyText, timeStr, false)
            val currentList = _chatMessages.value.toMutableList()
            currentList.add(replyMsg)
            _chatMessages.value = currentList
        }
    }

    // Feedback inputs State
    private val _rating = MutableStateFlow(5)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    fun updateRating(stars: Int) {
        _rating.value = stars
    }

    private val _selectedCategories = MutableStateFlow<Set<String>>(setOf("Kualitas Produk"))
    val selectedCategories: StateFlow<Set<String>> = _selectedCategories.asStateFlow()

    fun toggleCategory(cat: String) {
        val currentSet = _selectedCategories.value.toMutableSet()
        if (currentSet.contains(cat)) {
            currentSet.remove(cat)
        } else {
            currentSet.add(cat)
        }
        _selectedCategories.value = currentSet
    }

    private val _feedbackText = MutableStateFlow("")
    val feedbackText: StateFlow<String> = _feedbackText.asStateFlow()

    fun updateFeedbackText(text: String) {
        _feedbackText.value = text
    }

    fun submitFeedback(onSuccess: () -> Unit) {
        val msg = _feedbackText.value.trim()
        viewModelScope.launch {
            val categorSummary = _selectedCategories.value.joinToString(", ")
            val feedback = MitraFeedback(
                rating = _rating.value,
                category = categorSummary,
                message = msg,
                timestamp = System.currentTimeMillis()
            )
            repository.insertFeedback(feedback)

            // Reward partner 50 points for loyal feedback contribution
            val p = profile.value ?: MitraProfileEntity()
            val updatedProfile = p.copy(points = p.points + 50)
            repository.updateProfile(updatedProfile)

            // Reset state
            _feedbackText.value = ""
            _rating.value = 5
            _selectedCategories.value = setOf("Kualitas Produk")

            onSuccess()
        }
    }

    // Meeting states and realtime Google Meet synchronization
    private val _meetings = MutableStateFlow<List<PartnerMeeting>>(
        listOf(
            PartnerMeeting(
                id = "meet_1",
                title = "Briefing Promo Juni 2026",
                day = "28",
                month = "MEI",
                timeInfo = "14:00 WIB • Google Meet",
                platform = "Google Meet",
                meetUrl = "https://meet.google.com/qkn-ysdw-qpx",
                currentParticipants = 47,
                isRegistered = true,
                isPast = false
            ),
            PartnerMeeting(
                id = "meet_2",
                title = "Evaluasi Penjualan Q2",
                day = "05",
                month = "JUN",
                timeInfo = "09:30 WIB • Google Meet",
                platform = "Google Meet",
                meetUrl = "https://meet.google.com/asj-pmnb-zwt",
                currentParticipants = 32,
                isRegistered = false,
                isPast = false
            ),
            PartnerMeeting(
                id = "meet_3",
                title = "Perkenalan Produk Ekado Baru",
                day = "14",
                month = "MEI",
                timeInfo = "Rekaman tersedia • 1j 23m",
                platform = "Google Meet",
                meetUrl = "https://meet.google.com/kln-prod-ekd",
                currentParticipants = 61,
                isRegistered = false,
                isPast = true,
                durationText = "1j 23m"
            )
        )
    )
    val meetings: StateFlow<List<PartnerMeeting>> = _meetings.asStateFlow()

    fun registerForMeeting(meetingId: String) {
        _meetings.value = _meetings.value.map { meeting ->
            if (meeting.id == meetingId) {
                val updatedStatus = meeting.copy(
                    isRegistered = true,
                    currentParticipants = meeting.currentParticipants + 1
                )
                showNotification(
                    title = "Terdaftar di Meeting! 🎥",
                    message = "Berhasil mendaftar untuk '${meeting.title}'. Link Google Meet aktif.",
                    type = NotificationType.ORDER_PLACED
                )
                updatedStatus
            } else {
                meeting
            }
        }
    }

    fun createMeetingAndSyncMeet(title: String, dateInput: String, timeInput: String) {
        val cleanedDate = dateInput.trim().ifEmpty { "18 Juni" }
        val parts = cleanedDate.split(" ")
        val dayVal = parts.getOrNull(0) ?: "18"
        val monVal = (parts.getOrNull(1) ?: "JUN").uppercase()
        val clinTime = timeInput.trim().ifEmpty { "10:00 WIB" }

        val letters = ('a'..'z')
        val code1 = (1..3).map { letters.random() }.joinToString("")
        val code2 = (1..4).map { letters.random() }.joinToString("")
        val code3 = (1..3).map { letters.random() }.joinToString("")
        val sampleMeetUrl = "https://meet.google.com/$code1-$code2-$code3"

        val newId = "meet_" + System.currentTimeMillis()
        val newMeeting = PartnerMeeting(
            id = newId,
            title = title.ifEmpty { "Koordinasi Mitra Kulina" },
            day = dayVal,
            month = monVal,
            timeInfo = "$clinTime • Google Meet",
            platform = "Google Meet",
            meetUrl = sampleMeetUrl,
            currentParticipants = 1,
            isRegistered = true,
            isPast = false
        )

        _meetings.value = listOf(newMeeting) + _meetings.value

        showNotification(
            title = "Meet Dijadwalkan! 🎥",
            message = "Tautan Google Meet baru berhasil di-push secara real-time.",
            type = NotificationType.PAYMENT_CONFIRMED
        )
    }

    // FAQ list state management
    private val _faqs = MutableStateFlow<List<FaqItem>>(
        listOf(
            FaqItem(
                question = "Bagaimana cara melakukan pemesanan bahan baku?",
                answer = "Buka menu 'Bahan Baku' di halaman Home, pilih item yang Anda inginkan, masukkan jumlah item, dan tekan 'Buat Pesanan'. Setelah itu, lakukan transfer dana ke rekening Kulina dan konfirmasi pembayaran Anda di sistem agar pesanan segera dikirim.",
                category = "Bahan Baku"
            ),
            FaqItem(
                question = "Berapa lama proses verifikasi pembayaran?",
                answer = "Verifikasi pembayaran disimulasikan selesai instan saat Anda menekan tombol 'Confirm Pembayaran / Bayar'. Dalam aplikasi riil, konfirmasi manual membutuhkan waktu 10-30 menit di jam operasional.",
                category = "Pembayaran & Finance"
            ),
            FaqItem(
                question = "Bagaimana cara mengoneksikan kamera CCTV outlet?",
                answer = "Anda dapat membuka menu 'IoT CCTV' di menu Home. Masukkan IP Address router / NVR lokal Anda di form Sinkronisasi IP, lalu klik 'Sync IP'. Sistem secara otomatis memindai port streaming RTSP dan memunculkan grid live stream kamera.",
                category = "CCTV"
            ),
            FaqItem(
                question = "Apa fungsi poin reward mitra?",
                answer = "Setiap transaksi pemesanan bahan baku atau pemberian feedback bernilai poin. Poin ini dapat ditukarkan dengan diskon belanja bahan baku, merchandise eksklusif, hingga voucher gratis di menu 'Rewards'.",
                category = "Kemitraan"
            ),
            FaqItem(
                question = "Bagaimana jika pesanan bahan baku terlambat datang?",
                answer = "Kulina menjamin kedatangan pasokan tepat waktu (Maks. H+1 sejak verifikasi pembayaran). Jika terdapat keterlambatan, hubungi CS kami melalui fitur Live Chat di aplikasi untuk mendapatkan kompensasi koin.",
                category = "Bahan Baku"
            ),
            FaqItem(
                question = "Bagaimana cara menaikkan level kemitraan?",
                answer = "Level kemitraan didasarkan atas total pemesanan bahan baku dalam 3 bulan terakhir. Tingkat Gold & Platinum mendapatkan manfaat berupa diskon langsung bahan baku, prioritas pengiriman, dan biaya penanganan yang lebih murah.",
                category = "Kemitraan"
            )
        )
    )
    val faqs: StateFlow<List<FaqItem>> = _faqs.asStateFlow()

    fun toggleFaqExpand(faqId: String) {
        _faqs.value = _faqs.value.map { faq ->
            if (faq.id == faqId) {
                faq.copy(isExpanded = !faq.isExpanded)
            } else {
                faq
            }
        }
    }

    fun addOrUpdateFaq(faq: FaqItem) {
        val current = _faqs.value.toMutableList()
        val index = current.indexOfFirst { it.id == faq.id }
        if (index >= 0) {
            current[index] = faq
        } else {
            current.add(0, faq)
        }
        _faqs.value = current
    }

    fun deleteFaq(faqId: String) {
        _faqs.value = _faqs.value.filter { it.id != faqId }
    }

    fun addOrUpdateMeeting(meeting: PartnerMeeting) {
        val current = _meetings.value.toMutableList()
        val index = current.indexOfFirst { it.id == meeting.id }
        if (index >= 0) {
            current[index] = meeting
        } else {
            current.add(0, meeting)
        }
        _meetings.value = current
    }

    fun deleteMeeting(meetingId: String) {
        _meetings.value = _meetings.value.filter { it.id != meetingId }
    }

    fun addOrUpdateCctv(cam: CctvCamera) {
        val current = _cctvCameras.value.toMutableList()
        val index = current.indexOfFirst { it.id == cam.id }
        if (index >= 0) {
            current[index] = cam
        } else {
            current.add(0, cam)
        }
        _cctvCameras.value = current
    }

    fun deleteCctv(camId: Int) {
        _cctvCameras.value = _cctvCameras.value.filter { it.id != camId }
    }
}

class KulinaViewModelFactory(private val repository: MitraRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KulinaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KulinaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
