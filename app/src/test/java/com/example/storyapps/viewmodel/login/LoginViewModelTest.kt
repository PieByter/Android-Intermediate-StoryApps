package com.example.storyapps.viewmodel.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyapps.data.repository.Repository
import com.example.storyapps.response.LoginResponse
import com.example.storyapps.response.LoginResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(repository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `login successful`() = runTest {
        val dummyEmail = "pietertanoto01@gmail.com"
        val dummyPassword = "valorant"
        val dummyLoginResult = LoginResult(null, null, null)
        val dummyResponse = LoginResponse(dummyLoginResult, true, "Login successful")

        `when`(repository.login(dummyEmail, dummyPassword)).thenReturn(dummyResponse)

        viewModel.login(dummyEmail, dummyPassword)

        advanceTimeBy(500)

        assert(viewModel.isLoading.value == false)
        assert(viewModel.errorMessage.value == null)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `login failed with exception`() = runTest {
        val dummyEmail = "pietertanoto01@gmail.com"
        val dummyPassword = "valorant123"
        val loginException = RuntimeException("Invalid credentials")

        `when`(repository.login(dummyEmail, dummyPassword)).thenThrow(loginException)

        viewModel.login(dummyEmail, dummyPassword)

        assert(viewModel.isLoading.value == false)
        assert(viewModel.errorMessage.value != null)
}}

