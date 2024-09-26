package com.secal.juraid.Views.Generals.BaseViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.secal.juraid.BottomBar
import com.secal.juraid.HelpButton
import com.secal.juraid.TopBar
import com.secal.juraid.CategorySection
import com.secal.juraid.ViewModel.ContentItem
import kotlinx.coroutines.delay

@Composable
fun HomeView(navController: NavController, contentItems: List<ContentItem>) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                SearchBar()
                LargeCardCarousel(items = contentItems)

                CategorySection(title = "Categoría 1", items = contentItems, navController = navController)
                CategorySection(title = "Categoría 2", items = contentItems, navController = navController)
                CategorySection(title = "Categoría 3", items = contentItems, navController = navController)

                Spacer(modifier = Modifier.padding(16.dp))
            }

            HelpButton(modifier = Modifier.align(Alignment.BottomEnd), navController = navController)
        }
    }
}




@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .height(70.dp)
            .background(color = MaterialTheme.colorScheme.secondaryContainer),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}



@Composable
fun LargeCardCarousel(items: List<ContentItem>) {
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
fun LargeCardItem(item: ContentItem) {
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




