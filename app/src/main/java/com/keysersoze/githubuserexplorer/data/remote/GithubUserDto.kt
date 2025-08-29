package com.keysersoze.githubuserexplorer.data.remote

import com.google.gson.annotations.SerializedName

data class GithubUserDto(
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    val bio: String?,
    val followers: Int,
    @SerializedName("public_repos") val publicRepos: Int
)