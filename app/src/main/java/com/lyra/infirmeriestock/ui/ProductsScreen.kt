package com.lyra.infirmeriestock.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lyra.infirmeriestock.StockViewModel
import com.lyra.infirmeriestock.data.Location
import com.lyra.infirmeriestock.data.Product
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: StockViewModel,
    onAddProduct: () -> Unit,
    onMove: (Product) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val message by viewModel.message.collectAsState()

    var selectedLocation by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    message?.let {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            confirmButton = { TextButton(onClick = { viewModel.clearMessage() }) { Text("OK") } },
            title = { Text("Information") },
            text = { Text(it) }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("PharmaVigil") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedLocation == null,
                    onClick = { selectedLocation = null },
                    label = { Text("Tous") }
                )
                Location.entries.forEach { loc ->
                    FilterChip(
                        selected = selectedLocation == loc.name,
                        onClick = { selectedLocation = loc.name },
                        label = { Text(loc.displayName) }
                    )
                }
            }

            val filtered = if (selectedLocation == null) products
            else products.filter { it.location == selectedLocation }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(filtered, key = { it.id }) { product ->
                    ProductCard(product = product, onMove = { onMove(product) })
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onMove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onMove
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text(Location.valueOf(product.location).displayName + " • Qté: " + product.quantity)
            product.expiryDate?.let { ts ->
                val expiry = ts.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                val days = ChronoUnit.DAYS.between(LocalDate.now(), expiry)
                Text(
                    "Péremption: " + expiry + " (" + if (days >= 0) "J-" + days else "Expiré" + ")",
                    color = if (days <= 30) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            if (product.quantity <= product.minStock) {
                Text(
                    "⚠️ Stock bas (seuil: " + product.minStock + ")",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
