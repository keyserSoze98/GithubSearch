package com.keysersoze.githubuser.ui.search

import app.cash.turbine.test
import com.keysersoze.githubuser.data.GithubRepository
import com.keysersoze.githubuser.data.Resource
import com.keysersoze.githubuser.data.remote.GithubUserDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope
    private lateinit var repository: GithubRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        repository = mock()
        testScope = TestScope(testDispatcher)
        viewModel = SearchViewModel(repository)
    }

    @Test
    fun `searchUsers emits Loading then Success`() = testScope.runTest {
        val fakeUsers = listOf(
            GithubUserDto(
                login = "kishan",
                avatarUrl = "url",
                bio = "Test bio",
                followers = 10,
                publicRepos = 5
            )
        )
        whenever(repository.searchUsers("kishan")).thenReturn(Resource.Success(fakeUsers))

        viewModel.searchState.test {
            viewModel.updateQuery("kishan")
            viewModel.searchUsers()


            assert(awaitItem() is Resource.Success) // initial state
            assert(awaitItem() is Resource.Loading) // loading
            val result = awaitItem()
            assert(result is Resource.Success && result.data == fakeUsers)
        }
    }

    @Test
    fun `searchUsers emits Loading then Error`() = testScope.runTest {
        whenever(repository.searchUsers("error")).thenReturn(Resource.Error("Network error"))

        viewModel.searchState.test {
            viewModel.updateQuery("error")
            viewModel.searchUsers()


            assert(awaitItem() is Resource.Success)
            assert(awaitItem() is Resource.Loading)
            val result = awaitItem()
            assert(result is Resource.Error && result.message == "Network error")
        }
    }
}