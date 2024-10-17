import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.LocalUserViewModel
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.CasesViewModel
import com.secal.juraid.ViewModel.UserViewModel
import com.secal.juraid.ViewModel.unitInvestigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaseView(navController: NavController, viewModel: CasesViewModel) {
    val userViewModel = LocalUserViewModel.current
    var nombreCliente by remember { mutableStateOf("") }
    var nuc by remember { mutableStateOf("") }
    var carpetaJudicial by remember { mutableStateOf("") }
    var carpetaInvestigacion by remember { mutableStateOf("") }
    var acceso_fv by remember { mutableStateOf("") }
    var pass_fv by remember { mutableStateOf("") }
    var fiscalTitular by remember { mutableStateOf("") }
    var drive by remember { mutableStateOf("") }

    var selectedUnidadInvestigacion by remember { mutableStateOf<unitInvestigation?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val unitInvestigations by viewModel.unitInvestigations.collectAsState()
    val scope = rememberCoroutineScope()

    val nombreAbogado by userViewModel.userName.collectAsState()

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TitlesView(title = "Agregar Nuevo Caso")
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "Información General",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = nombreCliente,
                                onValueChange = { nombreCliente = it },
                                label = { Text("Nombre del Cliente") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = nuc,
                                onValueChange = { nuc = it },
                                label = { Text("NUC") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),

                                )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = fiscalTitular,
                                onValueChange = { fiscalTitular = it },
                                label = { Text("Fiscal Titular") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),

                                )

                            Spacer(modifier = Modifier.height(8.dp))

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedUnidadInvestigacion?.nombre ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Unidad de Investigación") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    unitInvestigations.forEach { unidad ->
                                        DropdownMenuItem(
                                            text = { Text(unidad.nombre) },
                                            onClick = {
                                                selectedUnidadInvestigacion = unidad
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Información Adicional",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = carpetaJudicial,
                                onValueChange = { carpetaJudicial = it },
                                label = { Text("Carpeta Judicial") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),

                                )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = carpetaInvestigacion,
                                onValueChange = { carpetaInvestigacion = it },
                                label = { Text("Carpeta de Investigación") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),

                                )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = acceso_fv,
                                onValueChange = { acceso_fv = it },
                                label = { Text("Acceso Fiscalía Virtual") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),

                                )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = pass_fv,
                                onValueChange = { pass_fv = it },
                                label = { Text("Contraseña Fiscalía Virtual") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),

                                )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = drive,
                                onValueChange = { drive = it },
                                label = { Text("Drive URL") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),

                                )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isSubmitting = true
                                try {
                                    viewModel.addCase(
                                        nombreAbogado = nombreAbogado,  // This field is not in the form, you may want to add it
                                        nombreCliente = nombreCliente,  // This field is not in the form, you may want to add it
                                        nuc = nuc,
                                        carpetaJudicial = carpetaJudicial,
                                        carpetaInvestigacion = carpetaInvestigacion,
                                        accesoFv = acceso_fv,
                                        passFv = pass_fv,
                                        fiscalTitular = fiscalTitular,
                                        idUnidadInvestigacion = selectedUnidadInvestigacion?.id,
                                        drive = drive
                                    )
                                    navController.navigate(Routes.casosVw)
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Agregar Caso")
                        }
                    }
                }
            }
        }
    }
}