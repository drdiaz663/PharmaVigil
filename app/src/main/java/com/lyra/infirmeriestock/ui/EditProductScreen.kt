package com.lyra.infirmeriestock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.lyra.infirmeriestock.StockViewModel
import com.lyra.infirmeriestock.data.CATEGORIES
import com.lyra.infirmeriestock.data.Location
import com.lyra.infirmeriestock.data.Product
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    viewModel: StockViewModel,
    product: Product,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var category by remember { mutableStateOf(product.category) }
    var location by remember { mutableStateOf(product.location) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var minStock by remember { mutableStateOf(product.minStock.toString()) }
    var expiryDateText by remember { 
        mutableStateOf(
            product.expiryDate?.let { ts ->
                val date = ts.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            } ?: ""
        ) 
    }
    var lotNumber by remember { mutableStateOf(product.lotNumber) }
    var isStupefiant by remember { mutableStateOf(product.isStupefiant) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Modifier le produit", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BleuRoi)
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name, 
                onValueChange = { name = it }, 
                label = { Text("Nom du produit") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Menu déroulant catégorie
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Catégorie") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    CATEGORIES.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Emplacement
            Location.entries.forEach { loc ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = location == loc.name, onClick = { location = loc.name })
                    Text(loc.displayName)
                }
            }

            OutlinedTextField(
                value = quantity, 
                onValueChange = { quantity = it }, 
                label = { Text("Quantité") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = minStock, 
                onValueChange = { minStock = it }, 
                label = { Text("Stock minimum") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = expiryDateText,
                onValueChange = { expiryDateText = it },
                label = { Text("Date péremption (JJ-MM-AAAA)") },
                placeholder = { Text("ex: 31-12-2026") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lotNumber, 
                onValueChange = { lotNumber = it }, 
                label = { Text("N° lot") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isStupefiant, onCheckedChange = { isStupefiant = it })
                Text("Stupéfiant (coffre fort)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val expiryTimestamp = try {
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        val date = LocalDate.parse(expiryDateText, formatter)
                        Timestamp(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    } catch (e: Exception) {
                        null
                    }
                    viewModel.updateProduct(
                        product.copy(
                            name = name,
                            category = category,
                            location = location,
                            quantity = quantity.toIntOrNull() ?: 0,
                            minStock = minStock.toIntOrNull() ?: 0,
                            expiryDate = expiryTimestamp,
                            lotNumber = lotNumber,
                            isStupefiant = isStupefiant
                        )
                    )
                    onBack()
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VertEmeraude),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer les modifications", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = RougeVif),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Supprimer ce produit", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer ${product.name} ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        showDeleteDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RougeVif)
                ) {
                    Text("Supprimer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
