package com.digitaltorque.structed.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import firebase.ActivateCallback

class AppConfig : firebase.RemoteConfig {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }

    init {
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    override fun fetchAndActivate(activated: ActivateCallback?) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { activated?.onActivate(it.isSuccessful) }
    }

    override fun getBool(key: String?): Boolean {
        return key?.let {
            remoteConfig.getBoolean(it)
        } ?: false
    }

    override fun getFloat64(key: String?): Double {
        return key?.let {
            remoteConfig.getDouble(it)
        } ?: 0.0
    }

    override fun getInt(key: String?): Long {
        return key?.let {
            remoteConfig.getLong(it)
        } ?: 0
    }

    override fun getJson(key: String?): String {
        return key?.let {
            remoteConfig.getString(it)
        } ?: ""
    }

    override fun getStr(key: String?): String {
        return key?.let {
            remoteConfig.getString(it)
        } ?: ""
    }

    companion object {
        const val ASK_SUPPORT_WAIT = "askSupportWait"
    }
}