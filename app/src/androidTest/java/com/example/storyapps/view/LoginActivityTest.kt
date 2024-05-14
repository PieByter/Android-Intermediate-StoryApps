package com.example.storyapps.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.storyapps.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest {

    private val dummyEmail = "pietertanoto01@gmail.com"
    private val dummyPassword = "valorant"
    private val wrongPassword = "wrong_password"

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginWithValidCredentials() {
        onView(withId(R.id.ed_login_email)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).perform(typeText(dummyPassword), closeSoftKeyboard())

        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).perform(click())
    }

    @Test
    fun loginWithInvalidPassword() {
        onView(withId(R.id.ed_login_email)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).perform(typeText(wrongPassword), closeSoftKeyboard())

        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).perform(click())
    }
}

