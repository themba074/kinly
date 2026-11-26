package com.kinly.app.auth

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class KeystoreSessionStoreTest {
    @Test
    fun encryptsSessionAndClearsItOnLogout() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = KeystoreSessionStore(context)
        store.clear()
        val session = StoredSession("synthetic-access-marker", System.currentTimeMillis() + 60_000)
        store.save(session)
        val stored = context.getSharedPreferences("kinly_auth", 0)
            .getString("session", null).orEmpty()
        assertFalse(stored.contains(session.accessToken))
        assertEquals(session, store.load())
        store.clear()
        assertNull(store.load())
    }
}
