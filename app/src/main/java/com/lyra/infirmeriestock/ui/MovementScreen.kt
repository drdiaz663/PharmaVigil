package com.lyra.infirmeriestock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lyra.infirmeriestock.StockViewModel
import com.lyra.infirmeriestock.data.MovementType
import com.lyra.infirmeriestock.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementScreen(viewModel: StockViewModel, product: Product, onBack: () -> Unit) {
    var type by remember { mutableStateOf(MovementType.ENTREE) }
    var quantity by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mouvement - ") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                RadioButton(selected = type == MovementType.ENTREE, onClick = { type = MovementType.ENTREE })
                Text("EntrÃ©e")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = type == MovementType.SORTIE, onClick = { type = MovementType.SORTIE })
                Text("Sortie")
            }
            OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("QuantitÃ©") })
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note (facultatif)") })

            Button(
                onClick = {
                    viewModel.registerMovement(
                        product = product,
                        type = type,
                        quantity = quantity.toIntOrNull() ?: 0,
                        note = note
                    )
                    onBack()
                },
                enabled = quantity.toIntOrNull() != null && quantity.toIntOrNull()!! > 0
            ) {
                Text("Enregistrer")
            }
        }
    }
}
