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
        border = androidx.compose.foundation.BorderStroke(2.dp, locColor)
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
            Text("Catégorie: ${product.category}")
            Text("Qté: ${product.quantity} (seuil: ${product.minStock})")
            Text("Péremption: ${formatDate(product.expiryDate)}")
            days?.let { d ->
                if (d >= 0 && d <= 30) {
                    Text("⚠️ Expire dans $d jour(s)", color = RougeVif, fontWeight = FontWeight.Bold)
                } else if (d < 0) {
                    Text("❌ EXPIRÉ", color = RougeVif, fontWeight = FontWeight.Bold)
                }
            }
            if (product.quantity <= product.minStock) {
                Text("⚠️ Stock bas !", color = OrangeVif, fontWeight = FontWeight.Bold)
            }
            // Bouton mouvement
            TextButton(onClick = onMove) {
                Text("📦 Entrée/Sortie", color = BleuRoi)
            }
        }
    }
}
