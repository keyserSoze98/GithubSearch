package com.keysersoze.githubuser.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.keysersoze.githubuser.data.GithubRepository
import com.keysersoze.githubuser.data.Resource
import com.keysersoze.githubuser.data.remote.GithubRepoDto
import com.keysersoze.githubuser.data.remote.GithubUserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GithubRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val username: String = checkNotNull(savedStateHandle["username"])

    private val _userState = MutableStateFlow<Resource<GithubUserDto>>(Resource.Loading)
    val userState: StateFlow<Resource<GithubUserDto>> = _userState

    val reposFlow: Flow<PagingData<GithubRepoDto>> =
        repository.getReposPager(username).cachedIn(viewModelScope)

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _userState.value = Resource.Loading
            val result = repository.getUser(username)
            _userState.value = result
        }
    }

    fun reload() {
        loadUser()
    }
}