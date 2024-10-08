package com.secal.juraid.Views.Sesion

import android.widget.Toast
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.secal.juraid.Routes
import com.secal.juraid.ViewModel.UserViewModel
import io.github.jan.supabase.auth.SessionStatus


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpView(navController: NavController, viewModel: UserViewModel) {
    val sessionState by viewModel.sessionState.collectAsState()
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val verificationMessage by viewModel.verificationMessage
    val accountExistsMessage by viewModel.accountExistsMessage  // Estado para cuenta ya registrada
    val emailNotConfirmed by viewModel.emailNotConfirmed.observeAsState(false)

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    SignCardView(navController, viewModel)

                    // Mostrar el mensaje de error si existe
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Mostrar el mensaje de verificación solo si no hay errores
                    if (verificationMessage.isNotEmpty() && errorMessage.isEmpty()) {
                        Text(
                            text = verificationMessage,
                            color = Color.Green,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // Manejar la cuenta ya existente con un AlertDialog
    if (accountExistsMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { /* Cerrar diálogo al hacer clic fuera */ },
            confirmButton = {
                Button(onClick = { navController.navigate(Routes.loginVw) }) {
                    Text("Continuar")
                }
            },
            title = { Text("Cuenta ya registrada") },
            text = { Text(accountExistsMessage) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }

    // Manejar email no confirmado con AlertDialog
    if (emailNotConfirmed) {
        AlertDialog(
            onDismissRequest = { /* Cerrar diálogo al hacer clic fuera */ },
            confirmButton = {
                Button(onClick = { navController.navigate(Routes.homeVw) }) {
                    Text("OK")
                }
            },
            title = { Text("Verifica tu correo") },
            text = { Text("Se te ha enviado un correo de confirmación para la creación de tu cuenta") },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }

    LaunchedEffect(sessionState) {
        when (sessionState) {
            is SessionStatus.Authenticated -> navController.navigate(Routes.homeVw)
            else -> {} // Manejar otros estados si es necesario
        }
    }
}

@Composable
fun SignCardView(navController: NavController, viewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var firstLastName by remember { mutableStateOf("") }
    var secondLastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) } // Estado para controlar el diálogo

    val allFieldsFilled = remember(email, password, name, firstLastName, secondLastName, phone) {
        email.isNotBlank() && password.isNotBlank() && name.isNotBlank() &&
                firstLastName.isNotBlank() && phone.isNotBlank()
    }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {

            Icon(
                imageVector = Icons.Sharp.AccountCircle,
                contentDescription = "User",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Registrarse", style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                placeholder = { Text("") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                placeholder = { Text("") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                placeholder = { Text("") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            Spacer(modifier = Modifier.height(8.dp))


            Row {
                OutlinedTextField(
                    value = firstLastName,
                    onValueChange = { firstLastName = it },
                    label = { Text("Apellido Paterno", fontSize = 15.sp) },
                    placeholder = { Text("") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = secondLastName,
                    onValueChange = { secondLastName = it },
                    label = { Text("Apellido Materno", fontSize = 15.sp) },
                    placeholder = { Text("") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    modifier = Modifier.weight(1f),
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { newValue ->
                    // Only allow digits and limit to 10 characters
                    if (newValue.length <= 10) {
                        phone = newValue.filter { it.isDigit() }
                    }
                },
                label = { Text("Teléfono") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                visualTransformation = PhoneVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de registro
            Button(
                onClick = {
                    //showDialog = true
                    viewModel.signUp(email, password, name, firstLastName, secondLastName, phone)
                          }, // Mostrar diálogo al hacer clic
                modifier = Modifier.fillMaxWidth(),
                enabled = allFieldsFilled // El botón se habilita solo cuando todos los campos están llenos
            ) {
                Text("Registro")
            }
        }
    }

    // Diálogo de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Cerrar diálogo al hacer clic fuera
            confirmButton = {
                Button(onClick = { navController.navigate(Routes.homeVw) }) {
                    Text("OK")
                }
            },
            title = { Text("Verifica tu correo") },
            text = { Text("Se te envió un correo de confirmación para la creación de tu cuenta") },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 10) text.text.substring(0..9) else text.text
        var output = ""
        for (i in trimmed.indices) {
            output += trimmed[i]
            if (trimmed.startsWith("81")) {
                if (i == 1 || i == 5) output += "-"
            } else {
                if (i == 2 || i == 5) output += "-"
            }
        }

        val phoneNumberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (trimmed.startsWith("81")) {
                    if (offset <= 1) return offset
                    if (offset <= 5) return offset + 1
                    if (offset <= 10) return offset + 2
                } else {
                    if (offset <= 2) return offset
                    if (offset <= 5) return offset + 1
                    if (offset <= 10) return offset + 2
                }
                return 12
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (trimmed.startsWith("81")) {
                    if (offset <= 2) return offset
                    if (offset <= 7) return offset - 1
                    if (offset <= 12) return offset - 2
                } else {
                    if (offset <= 3) return offset
                    if (offset <= 7) return offset - 1
                    if (offset <= 12) return offset - 2
                }
                return 10
            }
        }

        return TransformedText(AnnotatedString(output), phoneNumberOffsetTranslator)
    }
}
