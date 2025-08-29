package com.keysersoze.githubuserexplorer.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.githubuserexplorer.data.GithubRepository
import com.keysersoze.githubuserexplorer.data.Resource
import com.keysersoze.githubuserexplorer.data.remote.GithubUserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private val _searchState = MutableStateFlow<Resource<List<GithubUserDto>>>(Resource.Success(emptyList()))
    val searchState: StateFlow<Resource<List<GithubUserDto>>> = _searchState.asStateFlow()

    fun updateQuery(new: String) {
        _query.value = new
    }

    fun searchUsers() {
        val trimmed = _query.value.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            _hasSearched.value = true
            _searchState.value = Resource.Loading
            _searchState.value = repository.searchUsers(trimmed)
        }
    }
}