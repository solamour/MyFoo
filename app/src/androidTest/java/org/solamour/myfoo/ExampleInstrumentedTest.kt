package org.solamour.myfoo

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.solamour.myfoo.ui.theme.MyFooTheme

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
/*
./gradlew :app:connectedDebugAndroidTest
./gradlew :app:connectedDebugAndroidTest -P android.testInstrumentationRunnerArguments.class=org.solamour.myfoo.ExampleInstrumentedTest
./gradlew :app:connectedDebugAndroidTest -P android.testInstrumentationRunnerArguments.class=org.solamour.myfoo.ExampleInstrumentedTest#composeTest
*/
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>() // Set content manually.
//    val composeTestRule = createAndroidComposeRule<MyFooActivity>() // Let Activity set content.
//    val composeTestRule = createComposeRule()   // When accessing Activity is not necessary.

    private val viewModel by lazy {
        MyFooViewModel(composeTestRule.activity.application)
    }

    companion object {
        private const val COUNT = 4
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("org.solamour.myfoo", appContext.packageName)
    }

    @Test
    fun composeTest() {
        composeTestRule.setContent {
            MyFooTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyFoo(
                        logList = viewModel.logList,
                        onAction = viewModel::onAction,
                    )
                }
            }
        }

        composeTestRule.onRoot(/*useUnmergedTree = true*/).printToLog("MyFoo")

        addListItems()
        clearListItems()
    }

    private fun addListItems() {
        composeTestRule.onNodeWithContentDescription("play").apply {
            assertIsDisplayed()

            repeat(COUNT) {
                performClick()
            }
        }
        assertEquals(viewModel.logList.size, COUNT)
    }

    private fun clearListItems() {
        composeTestRule.onNodeWithContentDescription("more").performClick()
        composeTestRule.onNodeWithText("Clear log").performClick()
        assertEquals(viewModel.logList.size, 0)
    }
}
