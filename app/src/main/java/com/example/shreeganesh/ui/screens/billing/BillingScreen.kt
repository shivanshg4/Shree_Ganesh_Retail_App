package com.example.shreeganesh.ui.screens.billing

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shreeganesh.R
import com.example.shreeganesh.domain.models.Category
import com.example.shreeganesh.domain.models.Product
import com.example.shreeganesh.ui.screens.billing.components.CartSummary
import com.example.shreeganesh.ui.theme.LocalSpacing
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Entry point — wires ViewModel → content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BillingScreen(
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isCheckoutSuccess) {
        if (uiState.isCheckoutSuccess) {
            snackbarHostState.showSnackbar("Payment successful! ✓")
            viewModel.onCheckoutSuccessDismissed()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BillingScreenContent(
            uiState = uiState,
            onAddToCart = viewModel::addToCart,
            onUpdateQuantity = viewModel::updateQuantity,
            onRemoveItem = viewModel::removeItem,
            onCheckout = viewModel::onPayClicked,
            onClearCart = viewModel::clearCart,
            onCategorySelected = viewModel::onCategorySelected,
            onPrintReceipt = { /* handled separately */ }
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Content — responsive: tablet = side-by-side, mobile = bottom sheet
// ─────────────────────────────────────────────────────────────────────────────

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreenContent(
    uiState: BillingUiState,
    onAddToCart: (Product) -> Unit,
    onUpdateQuantity: (Product, Int) -> Unit,
    onRemoveItem: (Product) -> Unit,
    onCheckout: () -> Unit,
    onClearCart: () -> Unit,
    onCategorySelected: (String?) -> Unit,
    onPrintReceipt: () -> Unit
) {
    val spacing = LocalSpacing.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isTablet = maxWidth > 600.dp

        if (isTablet) {
            // ── Tablet layout: product grid left, cart panel right ────────────
            Column(modifier = Modifier.fillMaxSize()) {
                BillingTopAppBar()

                Row(modifier = Modifier.fillMaxSize()) {
                    // Products side (2/3)
                    Column(modifier = Modifier.weight(2f)) {
                        CategorySelector(
                            categories = uiState.categories,
                            selectedCategoryId = uiState.selectedCategoryId,
                            onCategorySelected = onCategorySelected
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                        ProductGrid(
                            products = uiState.availableProducts,
                            onAddToCart = onAddToCart,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Cart side (1/3)
                    Surface(
                        modifier = Modifier.weight(1f),
                        shadowElevation = 4.dp,
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CartSummary(
                            cartItems = uiState.cartItems,
                            subtotal = uiState.subtotal,
                            taxRatePercent = uiState.taxRatePercent,
                            tax = uiState.tax,
                            total = uiState.total,
                            onUpdateQuantity = onUpdateQuantity,
                            onRemoveItem = onRemoveItem,
                            onClearAll = onClearCart,
                            onCheckout = onCheckout,
                            onPrintReceipt = onPrintReceipt,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        } else {
            // ── Mobile layout: bottom sheet cart ─────────────────────────────
            val scaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    initialValue = SheetValue.PartiallyExpanded,
                    skipHiddenState = true
                )
            )
            val scope = rememberCoroutineScope()

            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 88.dp, // shows summary strip
                sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                sheetContainerColor = MaterialTheme.colorScheme.background,
                sheetShadowElevation = 8.dp,
                sheetContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(520.dp)
                    ) {
                        CartSummary(
                            cartItems = uiState.cartItems,
                            subtotal = uiState.subtotal,
                            taxRatePercent = uiState.taxRatePercent,
                            tax = uiState.tax,
                            total = uiState.total,
                            onUpdateQuantity = onUpdateQuantity,
                            onRemoveItem = onRemoveItem,
                            onClearAll = onClearCart,
                            onCheckout = {
                                onCheckout()
                                scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                            },
                            onPrintReceipt = onPrintReceipt,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                },
                topBar = { BillingTopAppBar() }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CategorySelector(
                        categories = uiState.categories,
                        selectedCategoryId = uiState.selectedCategoryId,
                        onCategorySelected = onCategorySelected
                    )

                    Spacer(modifier = Modifier.height(spacing.small))

                    ProductGrid(
                        products = uiState.availableProducts,
                        onAddToCart = { product ->
                            onAddToCart(product)
                            scope.launch { scaffoldState.bottomSheetState.expand() }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top App Bar — "QuickServe POS" with search + notification icons
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun BillingTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Shree Ganesh POS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = MaterialTheme.typography.titleLarge.fontSize * 1.2
            )
        },
        actions = {
            /*FilledIconButton(
                onClick = { },
                shape = RoundedCornerShape(50),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
//                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            }
            FilledIconButton(
                onClick = { },
                shape = RoundedCornerShape(50),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }*/
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Category tab selector
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit
) {
    val spacing = LocalSpacing.current
    val tabIndex = if (selectedCategoryId == null) 0
    else (categories.indexOfFirst { it.id == selectedCategoryId } + 1).coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = tabIndex,
        edgePadding = spacing.medium,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        Tab(
            selected = selectedCategoryId == null,
            onClick = { onCategorySelected(null) },
            text = { Text("All", style = MaterialTheme.typography.labelLarge) }
        )
        categories.forEach { category ->
            Tab(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) },
                text = { Text(category.name, style = MaterialTheme.typography.labelLarge) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Product grid — adaptive columns, matches Stitch card style
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ProductGrid(
    products: List<Product>,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 148.dp),
        contentPadding = PaddingValues(spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        modifier = modifier
    ) {
        items(
            items = products,
            key = { product -> product.id }
        ) { product ->
            POSProductCard(
                product = product,
                onClick = { if (product.stockQuantity > 0) onAddToCart(product) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Product card — matches Stitch: image top, name, price bold (₹)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun POSProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOutOfStock = product.stockQuantity <= 0

    Card(
        onClick = { if (!isOutOfStock) onClick() },
        modifier = modifier.fillMaxWidth(),
        enabled = !isOutOfStock,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                val painter = when (product.imageUrl) {
                    "burger"   -> painterResource(id = R.drawable.burger)
                    "pizza"    -> painterResource(id = R.drawable.pizza)
                    "coffee"   -> painterResource(id = R.drawable.coffee)
                    "sandwich" -> painterResource(id = R.drawable.sandwich)
                    "salad"    -> painterResource(id = R.drawable.salad)
                    else       -> null
                }
                if (painter != null) {
                    Image(
                        painter = painter,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = if (isOutOfStock) 0.4f else 1f
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }

                if (isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Out of Stock",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = if (isOutOfStock)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface
            )

            // Price (₹)
            Text(
                text = "₹%.2f".format(product.price),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isOutOfStock)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            )
        }
    }
}
