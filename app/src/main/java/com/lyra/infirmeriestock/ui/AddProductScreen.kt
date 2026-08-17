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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: StockViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CATEGORIES[0]) }
    var location by remember { mutableStateOf(Location.ARMOIRE.name) }
    var quantity by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var expiryDateText by remember { mutableStateOf("") }
    var lotNumber by remember { mutableStateOf("") }
    var isStupefiant by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Ajouter un produit", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BleuRoi)
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom du produit") })
            
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

            // Sélecteur emplacement avec couleurs
            Location.entries.forEach { loc ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = location == loc.name, onClick = { location = loc.name })
                    Box(
                        modifier = Modifier
                            .background(getLocationColor(loc.name).copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(loc.displayName)
                    }
                }
            }

            OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantité") })
            OutlinedTextField(value = minStock, onValueChange = { minStock = it }, label = { Text("Stock minimum") })
            OutlinedTextField(
                value = expiryDateText,
                onValueChange = { expiryDateText = it },
                label = { Text("Date péremption (JJ-MM-AAAA)") },
                placeholder = { Text("ex: 31-12-2026") }
            )
            OutlinedTextField(value = lotNumber, onValueChange = { lotNumber = it }, label = { Text("N° lot") })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isStupefiant, onCheckedChange = { isStupefiant = it })
                Text("Stupéfiant (coffre fort)")
            }

            Button(
                onClick = {
                    val expiryTimestamp = try {
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        val date = LocalDate.parse(expiryDateText, formatter)
                        Timestamp(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    } catch (e: Exception) {
                        null
                    }
                    viewModel.addProduct(
                        name = name,
                        category = category,
                        location = location,
                        quantity = quantity.toIntOrNull() ?: 0,
                        minStock = minStock.toIntOrNull() ?: 0,
                        expiryDate = expiryTimestamp,
                        lotNumber = lotNumber,
                        isStupefiant = isStupefiant
                    )
                    onBack()
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VertEmeraude)
            ) {
                Text("Ajouter", fontWeight = FontWeight.Bold)
            }
        }
    }
}
