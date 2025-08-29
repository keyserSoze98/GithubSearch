package com.keysersoze.githubuser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import com.keysersoze.githubuser.ui.navigation.NavGraph
import com.keysersoze.githubuser.ui.theme.GithubUserTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubUserTheme {
                Surface(modifier = androidx.compose.ui.Modifier.systemBarsPadding()) {
                    NavGraph()
                }
            }
        }
    }
}