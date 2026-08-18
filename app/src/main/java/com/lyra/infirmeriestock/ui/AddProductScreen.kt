package com.lyra.infirmeriestock.ui

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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: StockViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var expiryDateText by remember { mutableStateOf("") }
    var lotNumber by remember { mutableStateOf("") }
    var isStupefiant by remember { mutableStateOf(false) }

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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom du produit *") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Catégorie (ex: Médicament, Pansement...)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Emplacement (ex: Armoire, Frigidaire...) *") },
                modifier = Modifier.fillMaxWidth()
            )

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
                Text("Stupéfiant")
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
                enabled = name.isNotBlank() && location.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VertEmeraude),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajouter", fontWeight = FontWeight.Bold)
            }
        }
    }
}
