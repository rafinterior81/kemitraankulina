package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "bahan_baku_orders")
data class BahanBakuOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderDate: Long,
    val itemsJson: String, // format label summary: "Somay x2, Batagor x1"
    val totalPrice: Long,
    val status: String, // "Diproses", "Dikirim", "Selesai"
    val shippingFee: Long = 0L,
    val paymentMethod: String = "", // "Transfer Bank" / "QRIS"
    val isPaid: Boolean = false
)

@Entity(tableName = "finance_transactions")
data class FinanceTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timestamp: Long,
    val amount: Long,
    val type: String, // "in" / "out"
    val desc: String
)

@Entity(tableName = "mitra_feedbacks")
data class MitraFeedback(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rating: Int,
    val category: String,
    val message: String,
    val timestamp: Long
)

@Entity(tableName = "mitra_profile")
data class MitraProfileEntity(
    @PrimaryKey val id: String = "KLN-2024-0317",
    val name: String = "Budi Santoso",
    val points: Int = 2450,
    val level: String = "Gold Partner"
)

@Entity(tableName = "redeemed_rewards")
data class RedeemedReward(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val emoji: String,
    val pointsSpent: Int,
    val code: String, // e.g. KLN-REWARD-18239
    val redeemedAt: Long,
    val isUsed: Boolean = false
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val unit: String,
    val price: Long,
    val emoji: String = "🥟",
    val imageResType: String = "default", // "somay", "batagor", "pempek", "ingredients", "default"
    val stock: Int = 100,
    val threshold: Int = 15
)

@Dao
interface AppDao {
    // Products
    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    // Orders
    @Query("SELECT * FROM bahan_baku_orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<BahanBakuOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: BahanBakuOrder)

    @Query("DELETE FROM bahan_baku_orders")
    suspend fun clearOrders()

    // Transactions
    @Query("SELECT * FROM finance_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<FinanceTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinanceTransaction)

    @Query("DELETE FROM finance_transactions")
    suspend fun clearTransactions()

    // Feedbacks
    @Query("SELECT * FROM mitra_feedbacks ORDER BY timestamp DESC")
    fun getAllFeedbacks(): Flow<List<MitraFeedback>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: MitraFeedback)

    // Profile
    @Query("SELECT * FROM mitra_profile WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String = "KLN-2024-0317"): MitraProfileEntity?

    @Query("SELECT * FROM mitra_profile WHERE id = :id LIMIT 1")
    fun getProfileFlow(id: String = "KLN-2024-0317"): Flow<MitraProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: MitraProfileEntity)

    // Redeemed Rewards
    @Query("SELECT * FROM redeemed_rewards ORDER BY redeemedAt DESC")
    fun getAllRedeemedRewards(): Flow<List<RedeemedReward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedeemedReward(reward: RedeemedReward)

    @Query("DELETE FROM redeemed_rewards")
    suspend fun clearRedeemedRewards()
}

@Database(
    entities = [
        BahanBakuOrder::class,
        FinanceTransaction::class,
        MitraFeedback::class,
        MitraProfileEntity::class,
        ProductEntity::class,
        RedeemedReward::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kulina_mitra_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class MitraRepository(private val db: AppDatabase) {
    private val dao = db.dao()

    val products: Flow<List<ProductEntity>> = dao.getAllProducts()
    val orders: Flow<List<BahanBakuOrder>> = dao.getAllOrders()
    val transactions: Flow<List<FinanceTransaction>> = dao.getAllTransactions()
    val feedbacks: Flow<List<MitraFeedback>> = dao.getAllFeedbacks()
    val profile: Flow<MitraProfileEntity?> = dao.getProfileFlow()
    val redeemedRewards: Flow<List<RedeemedReward>> = dao.getAllRedeemedRewards()

    suspend fun insertProduct(product: ProductEntity) {
        dao.insertProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        dao.deleteProduct(product)
    }

    suspend fun insertOrder(order: BahanBakuOrder) {
        dao.insertOrder(order)
    }

    suspend fun insertTransaction(transaction: FinanceTransaction) {
        dao.insertTransaction(transaction)
    }

    suspend fun insertFeedback(feedback: MitraFeedback) {
        dao.insertFeedback(feedback)
    }

    suspend fun updateProfile(profile: MitraProfileEntity) {
        dao.insertProfile(profile)
    }

    suspend fun insertRedeemedReward(reward: RedeemedReward) {
        dao.insertRedeemedReward(reward)
    }

    suspend fun clearRedeemedRewards() {
        dao.clearRedeemedRewards()
    }

    suspend fun seedMockData() {
        // Seed default products if empty
        if (dao.getProductCount() == 0) {
            val defaultProducts = listOf(
                ProductEntity(name = "Somay Ayam Udang", unit = "per pack (50 pcs)", price = 85000L, emoji = "🥟", imageResType = "somay", stock = 120, threshold = 25),
                ProductEntity(name = "Batagor Tahu Ikan", unit = "per pack (40 pcs)", price = 75000L, emoji = "🍤", imageResType = "batagor", stock = 12, threshold = 20), // LOW STOCK ALERT!
                ProductEntity(name = "Ekado Udang Premium", unit = "per pack (30 pcs)", price = 95000L, emoji = "🫙", imageResType = "ingredients", stock = 80, threshold = 15),
                ProductEntity(name = "Pempek Palembang", unit = "per pack (20 pcs)", price = 110000L, emoji = "🐠", imageResType = "pempek", stock = 8, threshold = 15), // LOW STOCK ALERT!
                ProductEntity(name = "Saus Kacang Kulina", unit = "per botol 1L", price = 35000L, emoji = "🥜", imageResType = "ingredients", stock = 150, threshold = 30),
                ProductEntity(name = "Kuah Cuko Pempek", unit = "per botol 500ml", price = 25000L, emoji = "🍶", imageResType = "ingredients", stock = 4, threshold = 10) // LOW STOCK ALERT!
            )
            for (p in defaultProducts) {
                dao.insertProduct(p)
            }
        }

        // Seed database if profile is empty
        val existingProfile = dao.getProfile()
        if (existingProfile == null) {
            // Seed profile
            dao.insertProfile(MitraProfileEntity())

            // Seed initial transactions
            val initialTxns = listOf(
                FinanceTransaction(
                    title = "Penjualan - Kelapa Gading",
                    timestamp = System.currentTimeMillis() - 3600000 * 2, // 2 hours ago
                    amount = 45000,
                    type = "in",
                    desc = "Somay, Batagor × 3"
                ),
                FinanceTransaction(
                    title = "Penjualan - Cibubur",
                    timestamp = System.currentTimeMillis() - 3600000 * 3, // 3 hours ago
                    amount = 55000,
                    type = "in",
                    desc = "Pempek Palembang × 2"
                ),
                FinanceTransaction(
                    title = "Pembelian Bahan Baku",
                    timestamp = System.currentTimeMillis() - 3600000 * 24, // 24 hours ago
                    amount = 850000,
                    type = "out",
                    desc = "Stok Mingguan"
                ),
                FinanceTransaction(
                    title = "Penjualan - Serpong",
                    timestamp = System.currentTimeMillis() - 3600000 * 25, // 25 hours ago
                    amount = 72000,
                    type = "in",
                    desc = "Ekado Premium × 4"
                ),
                FinanceTransaction(
                    title = "Biaya Operasional",
                    timestamp = System.currentTimeMillis() - 3600000 * 30, // 30 hours ago
                    amount = 120000,
                    type = "out",
                    desc = "Gas, listrik, dll"
                )
            )
            for (t in initialTxns) {
                dao.insertTransaction(t)
            }

            // Seed some default orders
            val initialOrders = listOf(
                BahanBakuOrder(
                    orderDate = System.currentTimeMillis() - 86400000 * 2,
                    itemsJson = "Somay Ayam Udang × 5 pack, Saus Kacang Kulina × 2 botol",
                    totalPrice = 495000,
                    status = "Selesai",
                    shippingFee = 15000L,
                    paymentMethod = "Transfer Bank",
                    isPaid = true
                ),
                BahanBakuOrder(
                    orderDate = System.currentTimeMillis() - 86400000,
                    itemsJson = "Pempek Palembang × 2 pack, Kuah Cuko Pempek × 2 botol",
                    totalPrice = 270000,
                    status = "Dikirim",
                    shippingFee = 15000L,
                    paymentMethod = "QRIS",
                    isPaid = false
                )
            )
            for (o in initialOrders) {
                dao.insertOrder(o)
            }
        }
    }
}
