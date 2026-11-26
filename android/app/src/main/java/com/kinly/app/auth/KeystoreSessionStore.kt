package com.kinly.app.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal data class StoredSession(val accessToken: String, val expiresAtMillis: Long)

internal class KeystoreSessionStore(context: Context) {
    private val preferences = context.getSharedPreferences("kinly_auth", Context.MODE_PRIVATE)
    private val keyAlias = "kinly_access_token"

    fun save(session: StoredSession) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val plain = (session.expiresAtMillis.toString() + "\n" + session.accessToken)
            .toByteArray(Charsets.UTF_8)
        val encrypted = cipher.doFinal(plain)
        val encoded = Base64.encodeToString(cipher.iv + encrypted, Base64.NO_WRAP)
        check(preferences.edit().putString("session", encoded).commit())
    }

    fun load(): StoredSession? {
        val encoded = preferences.getString("session", null) ?: return null
        return try {
            val stored = Base64.decode(encoded, Base64.NO_WRAP)
            require(stored.size > 12)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, stored.copyOfRange(0, 12)))
            val plain = cipher.doFinal(stored.copyOfRange(12, stored.size))
                .toString(Charsets.UTF_8)
            val separator = plain.indexOf('\n')
            require(separator > 0)
            StoredSession(plain.substring(separator + 1), plain.substring(0, separator).toLong())
        } catch (_: Exception) {
            clear()
            null
        }
    }

    fun clear() {
        preferences.edit().remove("session").commit()
    }

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(keyAlias, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build(),
        )
        return generator.generateKey()
    }
}
