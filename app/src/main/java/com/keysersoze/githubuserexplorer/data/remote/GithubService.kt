package com.keysersoze.githubuserexplorer.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): GithubSearchResponse


    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): Response<GithubUserDto>

    @GET("users/{username}/repos")
    suspend fun getRepos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "updated"
    ): List<GithubRepoDto>
}