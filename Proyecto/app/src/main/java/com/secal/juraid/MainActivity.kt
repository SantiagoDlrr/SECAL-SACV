package com.secal.juraid

import AcercaDeView
import AddCaseView
import AddPostView
import AlumnosView
import ArticulosView
import CaseDetailViewModel
import CasosView
import DetalleView
import ProfileEditView
import ProfileViewModel
import ProfileViewModelFactory
import ScheduleViewModel
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.secal.juraid.Views.Generals.BaseViews.BienvenidaView
import com.secal.juraid.Views.Generals.Bookings.HelpView
import com.secal.juraid.Views.Generals.BaseViews.HomeView
import com.secal.juraid.Views.Sesion.LoginView
import com.secal.juraid.Views.Generals.BaseViews.ServiciosView
import com.secal.juraid.Views.Admin.StudentsView.StudentHomeView
import com.secal.juraid.Views.Sesion.SignUpView
import com.secal.juraid.Views.Admin.SuitViews.EspaciosView
import com.secal.juraid.Views.Admin.SuitViews.SuitHomeView
import com.secal.juraid.Views.Generals.BaseViews.UserView
import com.secal.juraid.Views.Generals.BaseViews.SettingsView
import com.secal.juraid.ui.theme.JurAidTheme
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.ViewModel.*
import com.secal.juraid.Views.*
import com.secal.juraid.Views.Admin.EditArticuloView
import com.secal.juraid.Views.Admin.EditDetalleView
import com.secal.juraid.Views.Admin.ProfileView
import com.secal.juraid.Views.Admin.StudentsView.CasosStudentView
import com.secal.juraid.Views.Admin.StudentsView.HorarioStudentView
import com.secal.juraid.Views.Admin.SuitViews.AlumnoDetailView
import com.secal.juraid.Views.Admin.SuitViews.CategoriasView
import com.secal.juraid.Views.Admin.SuitViews.InvUnitView
import com.secal.juraid.Views.Generals.BaseViews.ArticuloDetailView
import com.secal.juraid.Views.Generals.BaseViews.FAQView
import com.secal.juraid.Views.Generals.BaseViews.NuestrosServiciosView
import com.secal.juraid.Views.Generals.Bookings.BookingsView
import com.secal.juraid.Views.Generals.Users.UserHomeView
import com.secal.juraid.Views.Sesion.BiometricAuthView
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class MainActivity : FragmentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (isBiometricEnabled()) {
            startBiometricAuth {
                setContent {
                    JurAidTheme(
                        darkTheme = isSystemInDarkTheme(),
                        dynamicColor = false
                    ) {
                        UserScreen()
                    }
                }
            }
        } else {
            setContent {
                JurAidTheme(
                    darkTheme = isSystemInDarkTheme(),
                    dynamicColor = false
                ) {
                    UserScreen()
                }
            }
        }

        askNotificationPermission()

        intent?.data?.let { uri ->
            handleDeepLink(uri)
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {

            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {

                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token")
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
            })
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            handleDeepLink(uri)
        }
    }

    private fun handleDeepLink(uri: Uri) {
        val accessToken = uri.getQueryParameter("access_token")
        val type = uri.getQueryParameter("type")

        if (type == "signup" && accessToken != null) {
            Toast.makeText(this, "Correo confirmado, iniciando sesión...", Toast.LENGTH_LONG).show()
        }
    }

    private fun startBiometricAuth(onAuthSuccess: () -> Unit) {
        val biometricManager = BiometricManager.from(this)

        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        // Log if face authentication is available
        val canAuthenticateWithFace = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        Log.d("BiometricAuth", "Face Auth Available: ${canAuthenticateWithFace == BiometricManager.BIOMETRIC_SUCCESS}")

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val executor = ContextCompat.getMainExecutor(this)

            val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val authenticationType = when (result.authenticationType) {
                        BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> AuthenticationType.BIOMETRIC
                        BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> AuthenticationType.PIN
                        else -> AuthenticationType.UNKNOWN
                    }
                    runOnUiThread {
                        onAuthSuccess()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@MainActivity, "Error: $errString", Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Usa tu huella digital, rostro o PIN para autenticarte")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(this, "Este dispositivo no soporta autenticación biométrica o PIN", Toast.LENGTH_LONG).show()
            finish()
        }
    }


    private fun isBiometricEnabled(): Boolean {
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        return sharedPref.getBoolean("biometric_enabled", false)
    }

    private fun saveBiometricPreference(isEnabled: Boolean) {
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("biometric_enabled", isEnabled)
            apply()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class, ExperimentalSerializationApi::class)
@Preview(showBackground = true)
@Composable
fun UserScreen(startDestination: String = Routes.homeVw) {
    val navController = rememberNavController()
    val biometricViewModel: BiometricViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(Routes.bienvenidaVw) {
            BienvenidaView(navController = navController)
        }
        composable(Routes.homeVw) {
            val viewModel = remember { HomeViewModel() }
            HomeView(navController = navController, viewModel = viewModel)
        }
        composable(Routes.serviciosVw) {
            ServiciosView(navController = navController)
        }
        composable(Routes.faqVw) {
            FAQView(navController = navController)
        }
        composable(Routes.acercaDeVw) {
            AcercaDeView(navController = navController)
        }
        composable(Routes.nuestrosServiciosVw) {
            NuestrosServiciosView(navController = navController)
        }
        composable(Routes.userVw) {
            UserView(navController = navController)
        }
        composable(Routes.loginVw) {
            LoginView(navController = navController, UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
        }
        composable(Routes.signUpVw) {
            SignUpView(navController = navController, UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
        }
        composable(Routes.helpVw) {
            val uvm = remember {UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))) }
            val scheduleViewModel = remember { ScheduleViewModel() }
            val bookingsViewModel = remember { BookingsViewModel(uvm) }
            val userViewModel = remember { UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO)))}

            HelpView(navController = navController, scheduleViewModel = scheduleViewModel, bookingsViewModel = bookingsViewModel, userViewModel = userViewModel)
        }
        composable(Routes.bookingsVw) {
            val uvm = remember {UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))) }
            val bookingsViewModel = remember { BookingsViewModel(uvm) }
            BookingsView(
                navController = navController, bookingsViewModel = bookingsViewModel
            )
        }
        composable(Routes.suitVw) {
            SuitHomeView(navController = navController, UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
        }
        composable(Routes.casosVw) {
            val casesViewModel: CasesViewModel = viewModel()
            val citasViewModel: CitasViewModel = viewModel()
            val userRepository = UserRepository(supabase, CoroutineScope(Dispatchers.IO))
            val userViewModelFactory = UserViewModelFactory(userRepository)
            val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
            CasosView(
                navController = navController,
                viewModel = casesViewModel,
                citasViewModel = citasViewModel
            )
        }
        composable(Routes.invUnitVw) {
            val casesViewModelFactory = CasesViewModelFactory(LocalContext.current.applicationContext as Application)
            InvUnitView(navController = navController, viewModelFactory = casesViewModelFactory)
        }
        composable(Routes.espaciosVw) {
            EspaciosView(navController = navController)
        }
        composable(Routes.studentHomeVw) {
            StudentHomeView(navController = navController, UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
        }
        composable(Routes.userHomeVw) {
            UserHomeView(navController = navController, UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
        }
        composable(
            "${Routes.detalleVw}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getInt("caseId") ?: -1
            DetalleView(navController = navController, caseId = caseId)
        }
        composable(Routes.alumnosVw) {
            AlumnosView(navController = navController)
        }
        composable(Routes.casosStVw) {
            CasosStudentView(
                navController = navController,
                userViewModel = UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO)))
            )
        }
        composable(Routes.horarioStVw) {
            HorarioStudentView(
                navController = navController,
                userViewModel = UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO)))
            )
        }
        composable(
            route = "${Routes.alumnoDetailVw}/{studentId}",
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            AlumnoDetailView(navController = navController, studentId = studentId)
        }
        composable(Routes.articulosVw) {
            val homeViewModel = viewModel<HomeViewModel>()
            ArticulosView(navController = navController, homeViewModel)
        }
        composable(Routes.categoriasVw) {
            val homeViewModel = viewModel<HomeViewModel>()
            CategoriasView(navController = navController, homeViewModel)
        }
        composable(
            "articulo_detail_view/{itemJson}",
            arguments = listOf(navArgument("itemJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemJson = backStackEntry.arguments?.getString("itemJson")
            itemJson?.let {
                val item = try {
                    Json.decodeFromString(HomeViewModel.ContentItem.serializer(), it)
                } catch (e: MissingFieldException) {
                    Log.e("Error", "Faltan campos en el JSON: ${e.message}")
                    null
                }

                item?.let {
                    val viewModel = remember { HomeViewModel() }
                    ArticuloDetailView(navController = navController, viewModel = viewModel, postId = item.ID_Post)
                }
            }
        }

        composable(
            "${Routes.editArticuloVw}/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: -1
            val homeViewModel = viewModel<HomeViewModel>()
            EditArticuloView(
                navController = navController,
                viewModel = homeViewModel,
                postId = postId
            )
        }
        composable(Routes.addPostVw) {
            val homeViewModel = viewModel<HomeViewModel>()
            AddPostView(navController = navController, homeViewModel)
        }
        composable(Routes.addCaseVw) {
            val casesViewModel = viewModel<CasesViewModel>()
            val userRepository = UserRepository(supabase, CoroutineScope(Dispatchers.IO))
            val userViewModelFactory = UserViewModelFactory(userRepository)
            val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)

            AddCaseView(navController = navController, casesViewModel, userViewModel)
        }
        composable(Routes.settingView) {
            SettingsView(navController = navController, UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
        }
        composable(
            "${Routes.editDetalleVw}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getInt("caseId") ?: -1
            val caseViewModel = viewModel<CaseDetailViewModel>()
            EditDetalleView(
                navController = navController,
                viewModel = caseViewModel,
                caseId = caseId
            )
        }
        composable(Routes.biometricAuthVw) {
            val context = LocalContext.current

            BiometricAuthView(
                biometricViewModel = biometricViewModel,
                onAuthSuccess = {
                    Toast.makeText(context, "Autenticación biométrica exitosa", Toast.LENGTH_LONG).show()
                },
                onAuthError = { errorMessage ->
                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                }
            )
        }
        composable(Routes.profileView) {
            val context = LocalContext.current
            val application = context.applicationContext as Application
            val userRepository = UserRepository(supabase, CoroutineScope(Dispatchers.IO))
            val viewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(application, userRepository)
            )
            ProfileView(navController = navController, viewModel = viewModel)
        }
        composable(Routes.editProfileView) {
            val context = LocalContext.current
            val application = context.applicationContext as Application
            val userRepository = UserRepository(supabase, CoroutineScope(Dispatchers.IO))
            val viewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(application, userRepository)
            )
            ProfileEditView(navController = navController, viewModel = viewModel)
        }
    }
}