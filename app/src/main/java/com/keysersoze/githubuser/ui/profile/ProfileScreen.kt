package com.keysersoze.githubuser.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.keysersoze.githubuser.data.Resource
import com.keysersoze.githubuser.data.remote.GithubRepoDto
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    val repos = viewModel.reposFlow.collectAsLazyPagingItems()

    var refreshing by remember { mutableStateOf(false) }
    val swipeState = rememberSwipeRefreshState(isRefreshing = refreshing)

    LaunchedEffect(userState, repos.loadState) {
        refreshing = userState is Resource.Loading ||
                repos.loadState.refresh is androidx.paging.LoadState.Loading
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        SwipeRefresh(
            state = swipeState,
            onRefresh = {
                refreshing = true
                viewModel.reload()
                repos.refresh()
            },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (userState) {
                is Resource.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text((userState as Resource.Error).message)
                    }
                }
                is Resource.Success -> {
                    val user = (userState as Resource.Success<*>).data
                            as com.keysersoze.githubuser.data.remote.GithubUserDto

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = user.avatarUrl,
                                        contentDescription = "avatar",
                                        modifier = Modifier.size(72.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = user.login,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "${user.followers} followers • ${user.publicRepos} repos",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = user.bio ?: "No bio",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Repositories",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        items(repos.itemCount) { index ->
                            val repo = repos[index]
                            if (repo != null) {
                                RepoItem(repo = repo)
                            }
                        }

                        repos.apply {
                            when {
                                loadState.refresh is androidx.paging.LoadState.Loading -> item {
                                    LoadingItem()
                                }
                                loadState.refresh is androidx.paging.LoadState.Error -> item {
                                    val e = loadState.refresh as androidx.paging.LoadState.Error
                                    Text(
                                        "Error loading repos: ${e.error.localizedMessage}",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepoItem(repo: GithubRepoDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = repo.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = repo.description ?: "No description",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(text = "★ ${repo.stars}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(12.dp))
                Text(text = "🍴 ${repo.forks}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}