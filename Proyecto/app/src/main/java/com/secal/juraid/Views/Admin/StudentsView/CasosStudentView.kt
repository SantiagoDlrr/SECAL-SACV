package com.secal.juraid.Views.Admin.StudentsView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.Routes
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.Case
import com.secal.juraid.ViewModel.CasesViewModel
import com.secal.juraid.ViewModel.UserViewModel

@Composable
fun CasosStudentView(
    navController: NavController,
    userViewModel: UserViewModel,
    casesViewModel: CasesViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Casos")

    // Collect user ID from UserViewModel
    val id = userViewModel.userId.collectAsState().value

    // Collect assigned cases for the current student
    val assignedCases = casesViewModel.getAssignedCasesForStudent(id).collectAsState().value
    val isLoading = casesViewModel.isLoading.collectAsState().value

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(horizontal = 56.dp, vertical = 8.dp),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (assignedCases.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes casos asignados")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(assignedCases) { case ->
                        CaseCard(navController = navController, case = case)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseCard(navController: NavController,case: Case) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {navController.navigate("${Routes.detalleVw}/${case.id}")}
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "NUC: ${case.NUC}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Cliente: ${case.nombre_cliente}")
            Text(text = "Abogado: ${case.nombre_abogado}")
            Text(text = "Carpeta Judicial: ${case.carpeta_judicial}")
            Text(text = "Fiscal Titular: ${case.fiscal_titular}")

            if (!case.drive.isNullOrEmpty()) {
                Button(
                    onClick = { /* Implementar la navegación al Drive */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Ver documentos")
                }
            }
        }
    }
}