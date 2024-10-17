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
import com.secal.juraid.ui.theme.JurAidTheme
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.ViewModel.*
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
import com.secal.juraid.Views.Generals.BaseViews.SettingsView
import com.secal.juraid.Views.Generals.Bookings.BookingsView
import com.secal.juraid.Views.Generals.Users.UserHomeView
import com.secal.juraid.Views.Sesion.BiometricAuthView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json

val LocalUserViewModel = staticCompositionLocalOf<UserViewModel> {
    error("No UserViewModel provided")
}

class MainActivity : FragmentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize biometric preference to false by default
        initializeBiometricPreference()

        // Check if biometric is enabled before starting authentication
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
            // If biometric is disabled, proceed directly to content
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

    private fun initializeBiometricPreference() {
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        // If the preference doesn't exist (first time), set it to false
        if (!sharedPref.contains("biometric_enabled")) {
            sharedPref.edit().putBoolean("biometric_enabled", false).apply()
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
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
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

        when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt(onAuthSuccess)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // No biometric features available on this device
                Toast.makeText(this, "Este dispositivo no tiene capacidades biométricas", Toast.LENGTH_LONG).show()
                onAuthSuccess() // Proceed anyway
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Device has biometric capabilities but no biometrics enrolled
                Toast.makeText(this, "No hay datos biométricos registrados en el dispositivo", Toast.LENGTH_LONG).show()
                onAuthSuccess() // Proceed anyway
            }
            else -> {
                // For any other error, proceed with normal app flow
                onAuthSuccess()
            }
        }
    }

    private fun showBiometricPrompt(onAuthSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                runOnUiThread {
                    onAuthSuccess()
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    // User canceled the authentication, close the app
                    finish()
                } else {
                    // For other errors, show message and proceed
                    Toast.makeText(this@MainActivity, "Error: $errString", Toast.LENGTH_LONG).show()
                    onAuthSuccess()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@MainActivity, "Autenticación fallida", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Usa tu huella digital, rostro o PIN para acceder")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }


    private fun isBiometricEnabled(): Boolean {
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        return sharedPref.getBoolean("biometric_enabled", false)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class, ExperimentalSerializationApi::class)
@Preview(showBackground = true)
@Composable
fun UserScreen(startDestination: String = Routes.homeVw) {
    val navController = rememberNavController()
    val biometricViewModel: BiometricViewModel = viewModel()

    // Assuming UserViewModel needs a UserRepository in its constructor
    val userRepository = UserRepository(supabase, CoroutineScope(Dispatchers.IO))
    val userViewModelFactory = UserViewModelFactory(userRepository)
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)

    userViewModel.checkSession()


    CompositionLocalProvider(LocalUserViewModel provides userViewModel) {
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
            HomeView(navController = navController, viewModel = viewModel, userViewModel = userViewModel)
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
            LoginView(navController = navController, userViewModel)
        }
        composable(Routes.signUpVw) {
            SignUpView(navController = navController, userViewModel)
        }
        composable(Routes.helpVw) {
            val context = LocalContext.current
            val bookingsViewModel: BookingsViewModel = viewModel(
                factory = BookingsViewModelFactory(
                    context.applicationContext as Application,
                    userViewModel
                )
            )
            val scheduleViewModel = remember { ScheduleViewModel() }

            HelpView(
                navController = navController,
                scheduleViewModel = scheduleViewModel,
                bookingsViewModel = bookingsViewModel,
                userViewModel = userViewModel
            )
        }

        composable(Routes.bookingsVw) {
            val context = LocalContext.current
            val bookingsViewModel: BookingsViewModel = viewModel(
                factory = BookingsViewModelFactory(
                    context.applicationContext as Application,
                    userViewModel
                )
            )

            BookingsView(
                navController = navController,
                bookingsViewModel = bookingsViewModel
            )
        }
        composable(Routes.suitVw) {
            SuitHomeView(navController = navController, userViewModel)
        }
        composable(Routes.casosVw) {
            val casesViewModel: CasesViewModel = viewModel()
            val citasViewModel: CitasViewModel = viewModel()
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
            StudentHomeView(navController = navController, userViewModel)
        }
        composable(Routes.userHomeVw) {
            UserHomeView(navController = navController, userViewModel)
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
                userViewModel = userViewModel
            )
        }
        composable(Routes.horarioStVw) {
            HorarioStudentView(
                navController = navController,
                userViewModel = userViewModel
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

            AddCaseView(navController = navController, casesViewModel)
        }
        composable(Routes.settingView) {
            SettingsView(navController = navController, userViewModel)
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
            ProfileView(navController = navController)
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
}

class BookingsViewModelFactory(
    private val application: Application,
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingsViewModel::class.java)) {
            return BookingsViewModel(application, userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}