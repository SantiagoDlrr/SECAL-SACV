package com.secal.juraid.Views.Generals.BaseViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.HomeViewModel
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArticuloDetailView(navController: NavController, item: HomeViewModel.ContentItem?) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            item?.let { ArticuloDetailItem(it) }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatDate(): String {
    return try {
        val offsetDateTime = OffsetDateTime.parse(this)
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = offsetDateTime.atZoneSameInstant(zoneId)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy | h:mm a", Locale.ENGLISH)
        zonedDateTime.format(formatter).lowercase(Locale.ROOT)
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArticuloDetailItem(item: HomeViewModel.ContentItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header Card (Image, Title, Date)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    AsyncImage(
                        model = item.url_header,
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha de publicación")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.created_at.formatDate(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Card (Text)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodyMedium.copy(lineBreak = LineBreak.Paragraph),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}