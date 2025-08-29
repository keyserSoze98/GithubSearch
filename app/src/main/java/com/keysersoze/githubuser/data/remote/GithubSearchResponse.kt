package com.keysersoze.githubuser.data.remote

data class GithubSearchResponse(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<GithubUserDto>
)