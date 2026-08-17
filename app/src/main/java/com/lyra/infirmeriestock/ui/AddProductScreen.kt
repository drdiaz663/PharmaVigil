package com.lyra.infirmeriestock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.lyra.infirmeriestock.StockViewModel
import com.lyra.infirmeriestock.data.Location
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: StockViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("MÃ©dicament") }
    var location by remember { mutableStateOf(Location.ARMOIRE.name) }
    var quantity by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var expiryDateText by remember { mutableStateOf("") }
    var lotNumber by remember { mutableStateOf("") }
    var isStupefiant by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajouter un produit") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom") })
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("CatÃ©gorie") })

            // SÃ©lecteur emplacement
            Location.entries.forEach { loc ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = location == loc.name, onClick = { location = loc.name })
                    Text(loc.displayName)
                }
            }

            OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("QuantitÃ©") })
            OutlinedTextField(value = minStock, onValueChange = { minStock = it }, label = { Text("Stock minimum") })
            OutlinedTextField(
                value = expiryDateText,
                onValueChange = { expiryDateText = it },
                label = { Text("Date pÃ©remption (AAAA-MM-JJ)") }
            )
            OutlinedTextField(value = lotNumber, onValueChange = { lotNumber = it }, label = { Text("NÂ° lot") })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isStupefiant, onCheckedChange = { isStupefiant = it })
                Text("StupÃ©fiant (coffre fort)")
            }

            Button(
                onClick = {
                    val expiryTimestamp = try {
                        val date = LocalDate.parse(expiryDateText, DateTimeFormatter.ISO_LOCAL_DATE)
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
                enabled = name.isNotBlank()
            ) {
                Text("Ajouter")
            }
        }
    }
}
