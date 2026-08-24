package com.example.shreeganesh.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shreeganesh.domain.models.Product
import com.example.shreeganesh.ui.components.POSInputField
import com.example.shreeganesh.ui.components.POSPrimaryButton
import com.example.shreeganesh.ui.components.POSProductCard
import com.example.shreeganesh.ui.theme.LocalSpacing

@Composable
fun ProductManagementScreen(
    viewModel: ProductManagementViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    BoxWithConstraints {
        val isTablet = maxWidth > 800.dp

        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(if (isTablet && (showAddDialog || editingProduct != null)) 1.5f else 1f)) {
                ProductManagementScreenContent(
                    uiState = uiState,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onAddProduct = { 
                        if (isTablet) showAddDialog = true else showAddDialog = true 
                    },
                    onAddCategory = { showAddCategoryDialog = true },
                    onEditProduct = { product -> 
                        editingProduct = product
                    }
                )
            }

            if (isTablet && (showAddDialog || editingProduct != null)) {
                Surface(
                    modifier = Modifier.width(350.dp).fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            if (showAddDialog) "Add Product" else "Edit Product",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        ProductForm(
                            product = editingProduct,
                            categories = uiState.categories,
                            onDismiss = { 
                                showAddDialog = false
                                editingProduct = null
                            },
                            onConfirm = { name, price, catId, stock ->
                                if (showAddDialog) {
                                    viewModel.addProduct(name, price, catId, stock)
                                } else if (editingProduct != null) {
                                    viewModel.updateProduct(editingProduct!!.id, name, price, catId, stock, editingProduct!!.imageUrl)
                                }
                                showAddDialog = false
                                editingProduct = null
                            }
                        )
                    }
                }
            }
        }
    }

    // Mobile Dialogs
    BoxWithConstraints {
        val isTablet = maxWidth > 800.dp
        if (!isTablet && showAddDialog) {
            AddProductDialog(
                categories = uiState.categories,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, price, catId, stock ->
                    viewModel.addProduct(name, price, catId, stock)
                    showAddDialog = false
                }
            )
        }

        if (!isTablet && editingProduct != null) {
            EditProductDialog(
                product = editingProduct!!,
                categories = uiState.categories,
                onDismiss = { editingProduct = null },
                onConfirm = { name, price, catId, stock ->
                    viewModel.updateProduct(editingProduct!!.id, name, price, catId, stock, editingProduct!!.imageUrl)
                    editingProduct = null
                }
            )
        }
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name ->
                viewModel.addCategory(name)
                showAddCategoryDialog = false
            }
        )
    }
}

@Composable
fun ProductManagementScreenContent(
    uiState: ProductManagementUiState,
    onSearchQueryChange: (String) -> Unit,
    onAddProduct: () -> Unit,
    onAddCategory: () -> Unit,
    onEditProduct: (Product) -> Unit
) {
    val spacing = LocalSpacing.current
    
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            Text(
                text = "Product Management",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    POSInputField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        label = "Search products...",
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    )
                }
                
                POSPrimaryButton(
                    text = "Add Category",
                    onClick = onAddCategory,
                    modifier = Modifier.height(56.dp)
                )

                POSPrimaryButton(
                    text = "Add Product",
                    onClick = onAddProduct,
                    modifier = Modifier.height(56.dp)
                )
            }
        }

        // Products Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = uiState.allProducts,
                key = { it.id }
            ) { product ->
                POSManageProductCard(
                    product = product,
                    onEdit = { onEditProduct(product) }
                )
            }
        }
    }
}

@Composable
fun ProductForm(
    product: Product?,
    categories: List<com.example.shreeganesh.domain.models.Category>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String?, Int) -> Unit
) {
    var name by remember(product) { mutableStateOf(product?.name ?: "") }
    var price by remember(product) { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember(product) { mutableStateOf(product?.stockQuantity?.toString() ?: "0") }
    var selectedCatId by remember(product) { mutableStateOf<String?>(product?.categoryId) }
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
        
        @OptIn(ExperimentalMaterial3Api::class)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = categories.find { it.id == selectedCatId }?.name ?: "No Category",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("No Category") },
                    onClick = { selectedCatId = null; expanded = false }
                )
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = { selectedCatId = category.id; expanded = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
            Button(onClick = { onConfirm(name, price.toDoubleOrNull() ?: 0.0, selectedCatId, stock.toIntOrNull() ?: 0) }, modifier = Modifier.weight(1f)) {
                Text(if (product == null) "Add" else "Save")
            }
        }
    }
}

@Composable
fun POSManageProductCard(
    product: Product,
    onEdit: () -> Unit
) {
    val spacing = LocalSpacing.current
    val isLowStock = product.stockQuantity < 5
    val isOutOfStock = product.stockQuantity <= 0

    Box {
        POSProductCard(
            title = product.name,
            price = "₹%.2f".format(product.price),
            onClick = onEdit,
            imageContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!product.imageUrl.isNullOrBlank()) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(
                                id = when (product.imageUrl) {
                                    "burger" -> com.example.shreeganesh.R.drawable.burger
                                    "pizza" -> com.example.shreeganesh.R.drawable.pizza
                                    "coffee" -> com.example.shreeganesh.R.drawable.coffee
                                    "sandwich" -> com.example.shreeganesh.R.drawable.sandwich
                                    "salad" -> com.example.shreeganesh.R.drawable.salad
                                    else -> com.example.shreeganesh.R.drawable.ic_pos_logo
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        ) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.stockQuantity} in stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isOutOfStock -> MaterialTheme.colorScheme.error
                        isLowStock -> Color(0xFFFFA000) // Amber
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
        
        IconButton(
            onClick = onEdit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(spacing.small)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), shape = MaterialTheme.shapes.small)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AddProductDialog(
    categories: List<com.example.shreeganesh.domain.models.Category>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String?, Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product") },
        text = {
            ProductForm(product = null, categories = categories, onDismiss = onDismiss, onConfirm = onConfirm)
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun EditProductDialog(
    product: Product,
    categories: List<com.example.shreeganesh.domain.models.Category>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String?, Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Product") },
        text = {
            ProductForm(product = product, categories = categories, onDismiss = onDismiss, onConfirm = onConfirm)
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Category") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Category Name") }, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            Button(onClick = { onConfirm(name) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
