package com.keysersoze.githubuserexplorer.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.keysersoze.githubuserexplorer.data.Resource
import com.keysersoze.githubuserexplorer.data.remote.GithubUserDto
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToProfile: (String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val hasSearched by viewModel.hasSearched.collectAsState()
    val searchState by viewModel.searchState.collectAsState()

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🔍 Search GitHub User",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter username") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.searchUsers() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (searchState) {
            is Resource.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (searchState as Resource.Error).message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is Resource.Success -> {
                val users = (searchState as Resource.Success<List<GithubUserDto>>).data
                if (hasSearched) {
                    if (users.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No users found 😕",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize(),
                            state = listState
                        ) {
                            itemsIndexed(users) { _, user ->
                                UserItem(user) {
                                    onNavigateToProfile(user.login)
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
fun UserItem(user: GithubUserDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "avatar",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(50))
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = user.login,
                    style = MaterialTheme.typography.titleMedium
                )
                user.bio?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}