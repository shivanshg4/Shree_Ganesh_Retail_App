package com.example.shreeganesh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.shreeganesh.data.repository.POSRepository
import com.example.shreeganesh.ui.navigation.AppNavigation
import com.example.shreeganesh.ui.theme.ShreeGaneshTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: POSRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repository.seedData()
        }

        enableEdgeToEdge()
        setContent {
            ShreeGaneshTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}