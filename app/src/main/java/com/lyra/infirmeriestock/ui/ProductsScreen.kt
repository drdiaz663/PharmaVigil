package com.lyra.infirmeriestock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lyra.infirmeriestock.StockViewModel
import com.lyra.infirmeriestock.data.Location
import com.lyra.infirmeriestock.data.Product
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val BleuRoi = Color(0xFF1A237E)
val VertEmeraude = Color(0xFF00C853)
val OrangeVif = Color(0xFFFF6D00)
val RougeVif = Color(0xFFD50000)

fun getLocationColor(location: String): Color {
    return when (location) {
        Location.ARMOIRE.name -> Color(0xFF37474F)
        Location.BOITE_SECOURS_1.name -> Color(0xFF2E7D32)
        Location.BOITE_SECOURS_2.name -> Color(0xFF1565C0)
        Location.BOITE_SECOURS_3.name -> Color(0xFFE65100)
        Location.COFFRE_FORT.name -> Color(0xFFC62828)
        else -> Color(0xFF616161)
    }
}

fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return "N/A"
    val date = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return date.format(formatter)
}

fun daysUntil(timestamp: com.google.firebase.Timestamp?): Long? {
    if (timestamp == null) return null
    val date = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    return ChronoUnit.DAYS.between(LocalDate.now(), date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: StockViewModel,
    onAddProduct: () -> Unit,
    onMove: (Product) -> Unit,
    onEdit: (Product) -> Unit
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
        topBar = {
            TopAppBar(
                title = {
                    Text("PharmaVigil", color = Color.White, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BleuRoi
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = VertEmeraude,
                contentColor = Color.White
            ) {
                Text("+", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
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
                        label = { Text(loc.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = getLocationColor(loc.name).copy(alpha = 0.3f)
                        )
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
                    ProductCard(
                        product = product,
                        onMove = { onMove(product) },
                        onEdit = { onEdit(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onMove: () -> Unit, onEdit: () -> Unit) {
    val locColor = getLocationColor(product.location)
    val days = daysUntil(product.expiryDate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit,
        colors = CardDefaults.cardColors(
            containerColor = locColor.copy(alpha = 0.15f)
        ),
        border = BorderStroke(2.dp, locColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BleuRoi
                )
                Box(
                    modifier = Modifier
                        .background(locColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        Location.valueOf(product.location).displayName,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Catégorie: " + product.category)
            Text("Qté: " + product.quantity + " (seuil: " + product.minStock + ")")
            Text("Péremption: " + formatDate(product.expiryDate))
            days?.let { d ->
                if (d >= 0 && d <= 30) {
                    Text(
                        "⚠️ Expire dans " + d + " jour(s)",
                        color = RougeVif,
                        fontWeight = FontWeight.Bold
                    )
                } else if (d < 0) {
                    Text(
                        "❌ EXPIRÉ",
                        color = RougeVif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (product.quantity <= product.minStock) {
                Text(
                    "⚠️ Stock bas !",
                    color = OrangeVif,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = onMove) {
                Text("📦 Entrée/Sortie", color = BleuRoi)
            }
        }
    }
}
