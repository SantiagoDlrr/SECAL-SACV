//Detalle de un caso en específico

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secal.juraid.TitlesView
import com.secal.juraid.ViewModel.Case
import com.secal.juraid.ViewModel.unitInvestigation


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetalleView(navController: NavController, caseId: Int) {
    val context = LocalContext.current
    val viewModel: CaseDetailViewModel = viewModel()
    val caseDetail by viewModel.caseDetail.collectAsState()
    val hyperlinks by viewModel.hyperlinks.collectAsState()
    val unitInvestigation by viewModel.unitInvestigation.collectAsState()

    LaunchedEffect(caseId) {
        viewModel.loadCaseDetail(caseId)
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (caseDetail == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    TitlesView("Detalle de caso")
                    Spacer(modifier = Modifier.height(16.dp))
                    CaseDetailCard(caseDetail!!, unitInvestigation)
                    Spacer(modifier = Modifier.height(16.dp))
                    ActionsCard(caseDetail!!, unitInvestigation, context)
                    Spacer(modifier = Modifier.height(16.dp))
                    HyperlinksCard(hyperlinks, context)
                }
            }
        }
    }
}

@Composable
fun CaseDetailCard(case: Case, unitInvestigation: unitInvestigation?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = case.nombre_cliente,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "NUC: ${case.NUC}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                            DetailItem("Abogado", case.nombre_abogado)
                            DetailItem("Fiscal Titular", case.fiscal_titular)
                            DetailItem(
                                "Unidad de Investigación",
                                unitInvestigation?.nombre ?: "No especificada"
                            )
                        }
                        StatusChip(
                            isActive = case.status == 1,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailItemCopy("Carpeta Judicial", case.carpeta_judicial)
                        DetailItemCopy("Carpeta de Investigación", case.carpeta_investigacion)
                        DetailItemCopy("Acceso FV", case.acceso_fv)
                        DetailItemCopy("Pass FV", case.pass_fv)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(isActive: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor = if (isActive) Color(0xFF3B833E) else Color(0xFFE53935)
    val text = if (isActive) "Activo" else "Inactivo"

    Surface(
        modifier = modifier
            .wrapContentSize()
            .border(1.dp, backgroundColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = backgroundColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun DetailItemCopy(label: String, value: String) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        IconButton(
            onClick = {
                val clip = ClipData.newPlainText(label, value)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
            }
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy $label",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun ActionsCard(case: Case, unitInvestigation: unitInvestigation?, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Acciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            ActionButton("Abrir Drive", MaterialTheme.colorScheme.primary) { context.openUrl(case.drive) }
            ActionButton("Dirección de la Unidad de Investigación", MaterialTheme.colorScheme.primary, enabled = unitInvestigation?.direccion != null) {
                unitInvestigation?.direccion?.let { context.openUrl(it) }
            }
        }
    }
}

@Composable
fun ActionButton(text: String, buttonColor: Color, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun HyperlinksCard(hyperlinks: List<CaseDetailViewModel.Hiperlink>, context: Context) {
    if (hyperlinks.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Hipervínculos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                hyperlinks.forEach { hiperlink ->
                    ActionButton(hiperlink.texto, MaterialTheme.colorScheme.primary) { context.openUrl(hiperlink.link) }
                }
            }
        }
    }
}

fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}