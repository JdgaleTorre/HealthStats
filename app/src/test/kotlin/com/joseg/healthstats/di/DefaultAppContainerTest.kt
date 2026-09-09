package com.joseg.healthstats.di

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.test.core.app.ApplicationProvider
import com.joseg.healthstats.ui.onboarding.OnboardingViewModel
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultAppContainerTest {

    @Test
    fun `viewModelFactory creates a ViewModel through the container`() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val container: AppContainer = DefaultAppContainer(context)

        val owner = object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
        val viewModel = androidx.lifecycle.ViewModelProvider(
            owner,
            container.viewModelFactory.factory,
        )[OnboardingViewModel::class.java]

        assertNotNull(viewModel)
    }
}
