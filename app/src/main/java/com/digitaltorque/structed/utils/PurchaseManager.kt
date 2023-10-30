package com.digitaltorque.structed.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

interface PurchaseManager {
    fun start(connected: (() -> Unit)?, disconnected: (() -> Unit)?)
    suspend fun queryProducts(): List<ProductDetails>
    fun beginPurchase(activity: Activity, productDetails: ProductDetails): BillingResult?
    fun hasPurchased(productId: String): Boolean
}

class DummyPurchaseManager() : PurchaseManager {
    override fun start(connected: (() -> Unit)?, disconnected: (() -> Unit)?) {
    }

    override suspend fun queryProducts(): List<ProductDetails> {
        return listOf()
    }

    override fun beginPurchase(activity: Activity, productDetails: ProductDetails): BillingResult? {
        return null
    }
    override fun hasPurchased(productId: String): Boolean {
        return false
    }
}

class PurchaseManagerImpl(appContext: Context) : PurchaseManager {
    private val purchases: MutableList<String> = mutableListOf()

    private var masterKey: MasterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private var sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
            if (billingResult.responseCode == BillingResponseCode.OK) {
                purchases?.let {
                    for (purchase in purchases) {
                        // log analytics
                        runBlocking {
                            launch { handlePurchase(purchase) }
                        }
                    }
                }
            } else {
                // log analytics
            }
        }

    private var billingClient = BillingClient.newBuilder(appContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    var started = false

    init {
        sharedPreferences.getStringSet(PURCHASES, setOf<String>())?.let { purchases.addAll(it) }
    }
    override fun start(connected: (() -> Unit)?, disconnected: (() -> Unit)?) {
        if (started) {
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    started = true
                    connected?.invoke()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                started = false
                disconnected?.invoke()
            }
        })
    }

    override suspend fun queryProducts(): List<ProductDetails> {
        if (!started) {
            return listOf()
        }
        val productList = ArrayList<QueryProductDetailsParams.Product>()
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUPPORT_DEVELOPER_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        // leverage queryProductDetails Kotlin extension function
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }

        // Process the result.
        return productDetailsResult.productDetailsList ?: listOf()
    }

    override fun beginPurchase(activity: Activity, productDetails: ProductDetails): BillingResult? {
        if (!started) {
            return null
        }
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Launch the billing flow
        return billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != PurchaseState.PURCHASED) {
            return
        }
        // Verify the purchase
        purchases.addAll(purchase.products)
        savePurchases()
        if (purchase.isAcknowledged) {
            return
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.PURCHASE) { }
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
        withContext(Dispatchers.IO) {
            billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
        }
    }

    suspend fun queryPurchaseHistory() {
        val params = QueryPurchaseHistoryParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
        val purchaseHistory = withContext(Dispatchers.IO) {
            billingClient.queryPurchaseHistory(params.build())
        }
        for (purchaseHistoryItem in purchaseHistory.purchaseHistoryRecordList ?: listOf()) {
            purchases.addAll(purchaseHistoryItem.products)
        }
        savePurchases()
    }

    private fun savePurchases() {
        val editor = sharedPreferences.edit()
        editor.putStringSet(PURCHASES, purchases.toSet())
        editor.apply()
    }

    override fun hasPurchased(productId: String): Boolean {
        return purchases.contains(productId)
    }

    companion object {
        const val SUPPORT_DEVELOPER_PRODUCT_ID = "support_developer_product_id"
        const val PURCHASES = "purchases"
    }
}