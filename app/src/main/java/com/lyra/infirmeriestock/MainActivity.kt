package com.lyra.infirmeriestock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lyra.infirmeriestock.ui.AddProductScreen
import com.lyra.infirmeriestock.ui.EditProductScreen
import com.lyra.infirmeriestock.ui.MovementScreen
import com.lyra.infirmeriestock.ui.ProductsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PharmaVigilApp()
        }
    }
}

@Composable
fun PharmaVigilApp(viewModel: StockViewModel = viewModel()) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "products") {
                composable("products") {
                    ProductsScreen(
                        viewModel = viewModel,
                        onAddProduct = { navController.navigate("add") },
                        onMove = { product ->
                            navController.navigate("movement/" + product.id)
                        },
                        onEdit = { product ->
                            navController.navigate("edit/" + product.id)
                        }
                    )
                }
                composable("add") {
                    AddProductScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("edit/{productId}") { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId")
                    val product = viewModel.products.value.find { it.id == productId }
                    if (product != null) {
                        EditProductScreen(
                            viewModel = viewModel,
                            product = product,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable("movement/{productId}") { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId")
                    val product = viewModel.products.value.find { it.id == productId }
                    if (product != null) {
                        MovementScreen(
                            viewModel = viewModel,
                            product = product,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
