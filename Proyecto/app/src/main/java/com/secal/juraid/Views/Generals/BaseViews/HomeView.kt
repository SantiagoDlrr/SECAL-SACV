package com.secal.juraid.Views.Generals.BaseViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.secal.juraid.BottomBar
import com.secal.juraid.HelpButton
import com.secal.juraid.TopBar
import com.secal.juraid.CategorySection
import com.secal.juraid.ViewModel.HomeViewModel
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import com.secal.juraid.CategoryItem

@Composable
fun HomeView(navController: NavController, viewModel: HomeViewModel) {
    val isLoading by viewModel.isLoading.collectAsState()
    val contentItems by viewModel.contentItems.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                LoadingScreen()
            } else {
                HomeContent(
                    contentItems = contentItems,
                    navController = navController,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it }
                )
            }
            HelpButton(modifier = Modifier.align(Alignment.BottomEnd), navController = navController)
        }
    }
}

@Composable
fun HomeContent(
    contentItems: List<HomeViewModel.ContentItemPreview>,
    navController: NavController,
    searchQuery: TextFieldValue,
    onSearchQueryChange: (TextFieldValue) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item { SearchBar(searchQuery, onSearchQueryChange) }

        if (searchQuery.text.isNotEmpty()) {
            val filteredItems = contentItems.filter {
                it.title.contains(searchQuery.text, ignoreCase = true)
            }
            item {
                CategorySectionGrid(
                    title = "Resultados de búsqueda",
                    items = filteredItems,
                    navController = navController
                )
            }
        } else {
            item { LargeCardCarousel(items = contentItems) }

            // Group items by category
            val groupedItems = contentItems.groupBy { it.category }

            groupedItems.forEach { (category, items) ->
                item {
                    CategorySection(
                        title = category?.name_category ?: "Sin categoría",
                        items = items,
                        navController = navController
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.padding(50.dp)) }
    }
}

@Composable
fun SearchBar(searchQuery: TextFieldValue, onSearchQueryChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(70.dp),
        placeholder = { Text("Buscar artículos...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
fun CategorySectionGrid(title: String, items: List<HomeViewModel.ContentItemPreview>, navController: NavController) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.height((items.size / 2 * 270).dp + 16.dp) // Adjust height based on number of items
        ) {
            items(items) { item ->
                CategoryItem(item = item, navController = navController)
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando contenido...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun LargeCardCarousel(items: List<HomeViewModel.ContentItemPreview>) {
    val listState = rememberLazyListState()
    val originalItemCount = items.size

    // Create an extended list that repeats elements to simulate infinite scrolling
    val extendedItems = items + items.take(1)

    LaunchedEffect(listState) {
        while (true) {
            delay(3000L)
            if (originalItemCount > 0) {  // Add this check to prevent division by zero
                val nextIndex = (listState.firstVisibleItemIndex + 1) % originalItemCount
                listState.animateScrollToItem(nextIndex)
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        userScrollEnabled = true
    ) {
        items(extendedItems) { item ->
            LargeCardItem(item = item)
        }
    }
}


@Composable
fun LargeCardItem(item: HomeViewModel.ContentItemPreview) {
    Card(
        onClick = { /* TODO: Action when clicking on the card */ },
        modifier = Modifier
            .width(350.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                AsyncImage(
                    model = item.url_header,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}




