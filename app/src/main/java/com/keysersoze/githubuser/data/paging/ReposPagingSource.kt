package com.keysersoze.githubuser.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.keysersoze.githubuser.data.remote.GithubRepoDto
import com.keysersoze.githubuser.data.remote.GithubService
import retrofit2.HttpException
import java.io.IOException

class ReposPagingSource(
    private val service: GithubService,
    private val username: String,
    private val perPage: Int = 30
) : PagingSource<Int, GithubRepoDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubRepoDto> {
        val page = params.key ?: 1
        return try {
            val repos = service.getRepos(username, page, perPage)
            val nextKey = if (repos.isEmpty()) null else page + 1
            LoadResult.Page(
                data = repos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GithubRepoDto>): Int? {
        return state.anchorPosition?.let { anchorPos ->
            state.closestPageToPosition(anchorPos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPos)?.nextKey?.minus(1)
        }
    }
}