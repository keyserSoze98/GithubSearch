package com.keysersoze.githubuser.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.keysersoze.githubuser.data.paging.ReposPagingSource
import com.keysersoze.githubuser.data.remote.GithubRepoDto
import com.keysersoze.githubuser.data.remote.GithubService
import com.keysersoze.githubuser.data.remote.GithubUserDto
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import java.io.IOException

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}

@Singleton
class GithubRepository @Inject constructor(
    private val service: GithubService
) {

    private fun mapError(e: Exception): String {
        return when (e) {
            is IOException -> "Please check your internet connection 🌐"
            is HttpException -> {
                when (e.code()) {
                    404 -> "Not found. Please try again."
                    500 -> "Server error. Please try later."
                    else -> "Server error (${e.code()})"
                }
            }
            else -> "Something went wrong. Please try again."
        }
    }

    suspend fun searchUsers(query: String): Resource<List<GithubUserDto>> {
        return try {
            val response = service.searchUsers(query)
            Resource.Success(response.items)
        } catch (e: Exception) {
            Resource.Error(mapError(e))
        }
    }

    suspend fun getUser(username: String): Resource<GithubUserDto> {
        return try {
            val response = service.getUser(username)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) } ?: Resource.Error("Empty response")
            } else {
                if (response.code() == 404) Resource.Error("User not found 🙁")
                else Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(mapError(e))
        }
    }

    fun getReposPager(username: String, pageSize: Int = 30): Flow<PagingData<GithubRepoDto>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { ReposPagingSource(service, username, pageSize) }
        ).flow
    }
}