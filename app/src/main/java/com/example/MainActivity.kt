package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CRMViewModel
import com.example.ui.viewmodel.CRMViewModelFactory

class MainActivity : ComponentActivity() {

    // Initialize our ViewModel with constructor injection via repository
    private val viewModel: CRMViewModel by viewModels {
        CRMViewModelFactory(application, (application as CRMApplication).repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val currentUser by viewModel.currentUser.collectAsState()
                val notificationLogs by viewModel.notificationLogs.collectAsState()

                if (currentUser == null) {
                    // Show login flow if not authenticated
                    LoginScreen(viewModel = viewModel, onLoginSuccess = {
                        // Success handled reactively via state
                    })
                } else {
                    // Authenticated Shell Layout
                    var currentTab by remember { mutableStateOf("dashboard") }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            text = "CRM Proteção",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Olá, ${currentUser?.name?.split(" ")?.get(0) ?: "Usuário"}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                navigationIcon = {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 12.dp, end = 8.dp)
                                            .size(36.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                actions = {
                                    // Notifications Alert Icon Badge
                                    IconButton(
                                        onClick = { currentTab = "avisos" },
                                        modifier = Modifier.testTag("notification_button")
                                    ) {
                                        Box {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = "Alertas"
                                            )
                                            if (notificationLogs.isNotEmpty()) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(Color.Red, CircleShape)
                                                        .align(Alignment.TopEnd)
                                                )
                                            }
                                        }
                                    }

                                    // Logout button
                                    IconButton(
                                        onClick = { viewModel.logout() },
                                        modifier = Modifier.testTag("logout_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ExitToApp,
                                            contentDescription = "Sair",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == "dashboard",
                                    onClick = { currentTab = "dashboard" },
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                    label = { Text("Painel", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("tab_dashboard")
                                )
                                NavigationBarItem(
                                    selected = currentTab == "kanban",
                                    onClick = { currentTab = "kanban" },
                                    icon = { Icon(Icons.Default.ViewWeek, contentDescription = "Kanban") },
                                    label = { Text("CRM", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("tab_kanban")
                                )
                                NavigationBarItem(
                                    selected = currentTab == "vendas",
                                    onClick = { currentTab = "vendas" },
                                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "Vendas") },
                                    label = { Text("Vendas", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("tab_vendas")
                                )
                                NavigationBarItem(
                                    selected = currentTab == "agenda",
                                    onClick = { currentTab = "agenda" },
                                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Agenda") },
                                    label = { Text("Agenda", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("tab_agenda")
                                )
                                NavigationBarItem(
                                    selected = currentTab == "financeiro",
                                    onClick = { currentTab = "financeiro" },
                                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Finanças") },
                                    label = { Text("Finanças", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("tab_financeiro")
                                )
                                NavigationBarItem(
                                    selected = currentTab == "membros",
                                    onClick = { currentTab = "membros" },
                                    icon = { Icon(Icons.Default.People, contentDescription = "Equipe") },
                                    label = { Text("Equipe", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("tab_team")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            // Seamless transitions between dashboards, modules and details
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "ScreenTransition"
                            ) { tab ->
                                when (tab) {
                                    "dashboard" -> DashboardScreen(viewModel = viewModel)
                                    "kanban" -> KanbanScreen(viewModel = viewModel)
                                    "vendas" -> VendasScreen(viewModel = viewModel)
                                    "agenda" -> AgendamentosScreen(viewModel = viewModel)
                                    "financeiro" -> FinanceiroScreen(viewModel = viewModel)
                                    "membros" -> UsersScreen(viewModel = viewModel)
                                    "avisos" -> NotificationHubScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
