package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MediaType
import com.example.data.repository.MediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AuraStream", appName)
    }

    @Test
    fun `media repository loads curated media`() {
        val repo = MediaRepository.instance
        val items = repo.mediaItems.value
        assertTrue("Media items should not be empty", items.isNotEmpty())

        val audioCount = items.count { it.type == MediaType.AUDIO }
        val videoCount = items.count { it.type == MediaType.VIDEO }
        assertTrue("Should contain both audio and video", audioCount > 0 && videoCount > 0)

        val user = repo.userProfile.value
        assertEquals("Klaus Robitics", user.name)
        assertEquals("klausrobitics@gmail.com", user.email)
    }
}
