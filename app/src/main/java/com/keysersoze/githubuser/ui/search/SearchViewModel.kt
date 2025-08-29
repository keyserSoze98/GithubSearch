package com.keysersoze.githubuser.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.githubuser.data.GithubRepository
import com.keysersoze.githubuser.data.Resource
import com.keysersoze.githubuser.data.remote.GithubUserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<GithubUserDto>>>(Resource.Success(emptyList()))
    val searchState: StateFlow<Resource<List<GithubUserDto>>> = _searchState

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _searchState.value = Resource.Loading
            val result = repository.searchUsers(query)
            _searchState.value = result
        }
    }
}