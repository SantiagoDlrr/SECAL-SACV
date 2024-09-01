package com.secal.juraid.Views

import android.widget.EditText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.HelpButton
import com.secal.juraid.TopBar
import kotlinx.coroutines.delay

@Composable
fun HomeView(navController: NavController) {
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
                    .verticalScroll(rememberScrollState()) // Habilitar scroll vertical
            ) {
                SearchBar()
                LargeCardCarousel(items = listOf("Card 1", "Card 2", "Card 3", "Card 4"))
                CategorySection(title = "Categoría 1", items = listOf("Post 1", "Post 2", "Post 3", "Post 4"))
                CategorySection(title = "Categoría 2", items = listOf("Post 1", "Post 2", "Post 3", "Post 4"))
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
fun CategorySection(title: String, items: List<String>) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        Text(text = title, modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 20.sp)

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(items.size) { index ->
                CategoryItem(item = items[index])
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}



@Composable
fun CategoryItem(item: String) {
    Card(
        onClick = { /*TODO*/ },
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {

        }
        Text(
            text = item,
            modifier = Modifier.padding(10.dp)
            )
    }

}

@Composable
fun LargeCardCarousel(items: List<String>) {
    val listState = rememberLazyListState()
    val originalItemCount = items.size

    // Crear una lista extendida que repita los elementos para simular desplazamiento infinito
    val extendedItems = items + items.take(1) // Agregar un elemento extra al final

    LaunchedEffect(listState) {
        while (true) {
            delay(3000L)

            val nextIndex = (listState.firstVisibleItemIndex + 1)

            // Si estamos en el último elemento de la lista original, saltar al principio
            // para simular el desplazamiento infinito
            if (nextIndex == originalItemCount) {
                listState.scrollToItem(0) // Saltar al principio sin animación para que sea suave
            } else {
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
        items(extendedItems.size) { index ->
            LargeCardItem(item = extendedItems[index])
        }
    }
}

@Composable
fun LargeCardItem(item: String) {
    Card(
        onClick = { /* TODO: Acción al hacer clic en la tarjeta */ },
        modifier = Modifier
            .width(350.dp)  // Anchura de la tarjeta
            .height(200.dp)  // Altura de la tarjeta
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = item)
        }
    }
}



