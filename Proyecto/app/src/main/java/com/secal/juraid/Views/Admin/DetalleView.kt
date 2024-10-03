//Detalle de un caso en específico

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.R
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar


import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.ViewModel.UserViewModel
import com.secal.juraid.ViewModel.unitInvestigation
import com.secal.juraid.Views.Generals.BaseViews.formatDate
import com.secal.juraid.supabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetalleView(navController: NavController, caseId: Int) {
    val context = LocalContext.current
    val viewModel: CaseDetailViewModel = viewModel()
    val caseDetail by viewModel.caseDetail.collectAsState()
    val hyperlinks by viewModel.hyperlinks.collectAsState()
    val unitInvestigation by viewModel.unitInvestigation.collectAsState()

    val userRole by UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))).userRole.collectAsState()

    LaunchedEffect(caseId) {
        viewModel.loadCaseDetail(caseId)
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
        /*floatingActionButton = {
            if (userRole == 1) { // 1 = Abogado
                FloatingActionButton(
                    onClick = {
                        Log.d(TAG, "CASE ID $caseId")
                        navController.navigate("${Routes.editDetalleVw}/$caseId")
                    }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar caso")
                }
            }
        }*/

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TitlesView(title = "Información de Caso")

            Spacer(modifier = Modifier.height(16.dp))

            caseDetail?.let { case ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = "Placeholder",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(case.nombre_cliente, fontWeight = FontWeight.Bold)
                                Text("NUC: ${case.NUC}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fecha de creación: ${case.created_at.formatDate()}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Abogado: ${case.nombre_abogado}\n" +
                                    "Carpeta Judicial: ${case.carpeta_judicial}\n" +
                                    "Carpeta de Investigación: ${case.carpeta_investigacion}\n" +
                                    "Fiscal Titular: ${case.fiscal_titular}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "Acciones",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { context.openUrl(case.drive) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Abrir Drive")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { unitInvestigation?.direccion?.let { context.openUrl(it) } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = unitInvestigation?.direccion != null
                        ) {
                            Text("Dirección de la Unidad de Investigación")
                        }


                        if (hyperlinks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Hipervínculos",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(bottom = 8.dp),
                                fontSize = 20.sp
                            )
                            hyperlinks.forEach { hiperlink ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { context.openUrl(hiperlink.link) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text(hiperlink.texto)
                                }
                            }
                        }

                    }
                }
            } ?: run {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

fun Context.openUrl(url: String) {

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}