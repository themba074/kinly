package com.kinly.app.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackendHealthParserTest {
    @Test
    fun acceptsSuccessfulUpResponse() {
        assertTrue(BackendHealthParser.isReady(200, """{"status":"UP"}"""))
    }

    @Test
    fun rejectsNonSuccessfulResponse() {
        assertFalse(BackendHealthParser.isReady(503, """{"status":"UP"}"""))
    }

    @Test
    fun rejectsUnexpectedOrDetailedPayload() {
        assertFalse(BackendHealthParser.isReady(200, """{"status":"DOWN"}"""))
        assertFalse(BackendHealthParser.isReady(200, """{"database":"secret"}"""))
    }
}
