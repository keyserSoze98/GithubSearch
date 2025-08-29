package com.keysersoze.githubuser.data.remote

import com.google.gson.annotations.SerializedName

data class GithubRepoDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
    @SerializedName("html_url") val htmlUrl: String
)