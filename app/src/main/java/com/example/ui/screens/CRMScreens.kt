package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CRMViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Helper formatting functions
fun formatMoney(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(value)
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return sdf.format(Date(timestamp))
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
    return sdf.format(Date(timestamp))
}

// -------------------------------------------------------------
// LOGIN SCREEN
// -------------------------------------------------------------
@Composable
fun LoginScreen(viewModel: CRMViewModel, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("admin@crm.com") }
    var password by remember { mutableStateOf("123456") }
    val error by viewModel.loginError.collectAsState()
    val users by viewModel.users.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Protective Shield Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CRM Proteção",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "SaaS Gestão Comercial & Risco",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail corporativo") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha de acesso") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                error?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.login(email, password, onLoginSuccess)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "ACESSAR SISTEMA",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Profiles quick selection for easy demo/testing
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Acesso Rápido (Perfis de Teste):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        InputChip(
                            selected = email == "admin@crm.com",
                            onClick = {
                                email = "admin@crm.com"
                                password = "123456"
                            },
                            label = { Text("Admin") }
                        )
                    }
                    item {
                        InputChip(
                            selected = email == "gerente@crm.com",
                            onClick = {
                                email = "gerente@crm.com"
                                password = "123456"
                            },
                            label = { Text("Gerente") }
                        )
                    }
                    item {
                        InputChip(
                            selected = email == "lucas@crm.com",
                            onClick = {
                                email = "lucas@crm.com"
                                password = "123456"
                            },
                            label = { Text("Consultor") }
                        )
                    }
                    item {
                        InputChip(
                            selected = email == "financeiro@crm.com",
                            onClick = {
                                email = "financeiro@crm.com"
                                password = "123456"
                            },
                            label = { Text("Financeiro") }
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MAIN DASHBOARD SCREEN
// -------------------------------------------------------------
@Composable
fun DashboardScreen(viewModel: CRMViewModel) {
    val leads by viewModel.leads.collectAsState()
    val vendas by viewModel.vendas.collectAsState()
    val financeiro by viewModel.financeiro.collectAsState()

    // Calculations based on actual local Room Database
    val totalLeads = leads.size
    val closedLeads = leads.count { it.stage == "Fechado" }
    val conversionRate = if (totalLeads > 0) (closedLeads.toDouble() / totalLeads) * 100 else 0.0

    val leadsAtivos = leads.count { it.stage != "Fechado" }
    val clientesAtivos = vendas.count { it.status == "Adimplente" }
    val totalVendasValue = vendas.sumOf { it.valueAdesao }
    val receitaRecorrente = vendas.filter { it.status == "Adimplente" }.sumOf { it.auxilioMensal }

    // Receita do mês (Adesões no mês + Recorrentes ativas + Lançamentos de receita em financeiro)
    val totalFinanceiroReceita = financeiro.filter { it.type == "Receita" }.sumOf { it.value }
    val receitaMes = totalFinanceiroReceita // Includes sales adesões automatically and subscription records

    val metaMensal = 25000.0
    val metaPercent = (receitaMes / metaMensal).coerceIn(0.0, 1.0)

    // Collapsible states for graphs
    var isReceitaChartOpen by remember { mutableStateOf(true) }
    var isLeadOriginChartOpen by remember { mutableStateOf(true) }
    var isFunnelChartOpen by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcoming card
        Text(
            text = "Indicadores Estratégicos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Meta Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Meta Comercial de Junho",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "${formatMoney(receitaMes)} / ${formatMoney(metaMensal)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                                CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = String.format("%.0f%%", metaPercent * 100),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = metaPercent.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                )
            }
        }

        // Metrics Grid (2 columns)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(
                title = "Clientes Ativos",
                value = "$clientesAtivos",
                subText = "Proteção ativa",
                icon = Icons.Default.DirectionsCar,
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Leads Em Aberto",
                value = "$leadsAtivos",
                subText = "No funil Kanban",
                icon = Icons.Default.FilterList,
                color = WarningOrange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(
                title = "Mensalidades (MRR)",
                value = formatMoney(receitaRecorrente),
                subText = "Receita Recorrente",
                icon = Icons.Default.Autorenew,
                color = NeutralBlue,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Conversão CRM",
                value = String.format("%.1f%%", conversionRate),
                subText = "$closedLeads de $totalLeads fechados",
                icon = Icons.Default.TrendingUp,
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Collapsible Section: Revenue Bar Chart
        CollapsibleSection(
            title = "Gráfico: Evolução de Receitas",
            isOpen = isReceitaChartOpen,
            onToggle = { isReceitaChartOpen = !isReceitaChartOpen }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Adesões e taxas arrecadadas nas últimas semanas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                // Draw elegant custom Canvas bar chart
                val chartData = listOf(4200.0, 6800.0, 9500.0, receitaMes)
                val labels = listOf("Semana 1", "Semana 2", "Semana 3", "Semana atual")
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val maxVal = chartData.maxOrNull() ?: 10000.0
                    val widthPerBar = size.width / (chartData.size * 2)
                    val spacing = size.width / (chartData.size * 2)

                    chartData.forEachIndexed { idx, valItem ->
                        val barHeight = ((valItem / maxVal) * (size.height - 40f)).toFloat()
                        val leftX = idx * (widthPerBar + spacing) + (spacing / 2f)
                        val topY = size.height - barHeight - 30f

                        // Rounded bar
                        drawRoundRect(
                            color = if (idx == chartData.size - 1) NeutralBlue else NeutralBlue.copy(alpha = 0.5f),
                            topLeft = Offset(leftX, topY),
                            size = Size(widthPerBar, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                        )

                        // Value Text inside canvas context (or overlay, here we draw custom lines)
                        drawLine(
                            color = OutlineDark,
                            start = Offset(0f, size.height - 25f),
                            end = Offset(size.width, size.height - 25f),
                            strokeWidth = 2f
                        )
                    }
                }
                
                // Labels Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    labels.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Collapsible Section: Lead Origins
        CollapsibleSection(
            title = "Gráfico: Origem dos Leads",
            isOpen = isLeadOriginChartOpen,
            onToggle = { isLeadOriginChartOpen = !isLeadOriginChartOpen }
        ) {
            val origins = listOf("WhatsApp", "Redes Sociais", "Indicação", "Site", "Outro")
            val counts = origins.map { orig -> leads.count { it.origin == orig } }
            val total = counts.sum().coerceAtLeast(1)

            Column(modifier = Modifier.padding(16.dp)) {
                origins.forEachIndexed { idx, name ->
                    val count = counts[idx]
                    val pct = (count.toFloat() / total)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(110.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp)
                                .background(MaterialTheme.colorScheme.outline, CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct)
                                    .fillMaxHeight()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        ),
                                        CircleShape
                                    )
                            )
                        }
                        Text(
                            text = "$count (${colorPercent(pct)})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .width(60.dp)
                                .padding(start = 8.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Collapsible Section: Funnel Stage Conversion
        CollapsibleSection(
            title = "Conversão por Etapa do Funil",
            isOpen = isFunnelChartOpen,
            onToggle = { isFunnelChartOpen = !isFunnelChartOpen }
        ) {
            val stages = listOf("Leads", "Primeiro Contato", "Interesse", "Negociação", "Documentação", "Fechado")
            val counts = stages.map { stg -> leads.count { it.stage == stg } }
            val maxCount = counts.maxOrNull()?.coerceAtLeast(1) ?: 1

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                stages.forEachIndexed { index, stage ->
                    val count = counts[index]
                    val ratio = count.toFloat() / maxCount
                    val widthPercent = 0.3f + (ratio * 0.7f) // Make sure even 0 has minor visual placeholder width

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}. $stage",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.primary.copy(alpha = ratio.coerceAtLeast(0.1f))
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$count leads",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun colorPercent(value: Float): String {
    return String.format("%.0f%%", value * 100)
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun CollapsibleSection(
    title: String,
    isOpen: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isOpen) "Fechar" else "Abrir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(
                visible = isOpen,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Divider(color = MaterialTheme.colorScheme.outline)
                    content()
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CRM KANBAN SCREEN
// -------------------------------------------------------------
@Composable
fun KanbanScreen(viewModel: CRMViewModel) {
    val leads by viewModel.leads.collectAsState()
    val context = LocalContext.current

    val stages = listOf("Leads", "Primeiro Contato", "Interesse", "Negociação", "Documentação", "Fechado")
    var selectedStageIndex by remember { mutableIntStateOf(0) }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedOriginFilter by remember { mutableStateOf("Todos") }
    val origins = listOf("Todos", "WhatsApp", "Redes Sociais", "Indicação", "Site", "Outro")

    var isAddingLead by remember { mutableStateOf(false) }
    var selectedLeadForDetails by remember { mutableStateOf<Lead?>(null) }

    // Filter leads
    val filteredLeads = leads.filter { lead ->
        (lead.name.contains(searchQuery, ignoreCase = true) || 
         lead.vehicle.contains(searchQuery, ignoreCase = true) ||
         lead.plate.contains(searchQuery, ignoreCase = true)) &&
        (selectedOriginFilter == "Todos" || lead.origin == selectedOriginFilter)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Add Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar lead, veículo, placa...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
            
            FloatingActionButton(
                onClick = { isAddingLead = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Lead")
            }
        }

        // Horizontal origins filter chip row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(origins) { origin ->
                FilterChip(
                    selected = selectedOriginFilter == origin,
                    onClick = { selectedOriginFilter = origin },
                    label = { Text(origin) }
                )
            }
        }

        // Kanban Column Tabs for ergonomic mobile interaction, with badge count
        ScrollableTabRow(
            selectedTabIndex = selectedStageIndex,
            edgePadding = 16.dp,
            divider = {}
        ) {
            stages.forEachIndexed { idx, stage ->
                val stageCount = filteredLeads.count { it.stage == stage }
                Tab(
                    selected = selectedStageIndex == idx,
                    onClick = { selectedStageIndex = idx },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stage, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(
                                containerColor = if (selectedStageIndex == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (selectedStageIndex == idx) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                Text(text = "$stageCount")
                            }
                        }
                    }
                )
            }
        }

        // Active Stage Column List
        val activeStageName = stages[selectedStageIndex]
        val activeLeads = filteredLeads.filter { it.stage == activeStageName }

        if (activeLeads.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterNone,
                        contentDescription = "Vazio",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sem leads nesta etapa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(activeLeads) { lead ->
                    LeadCard(
                        lead = lead,
                        onClick = { selectedLeadForDetails = lead },
                        onMoveForward = if (selectedStageIndex < stages.size - 1) {
                            { viewModel.updateLeadStage(lead.id, stages[selectedStageIndex + 1]) }
                        } else null,
                        onMoveBackward = if (selectedStageIndex > 0) {
                            { viewModel.updateLeadStage(lead.id, stages[selectedStageIndex - 1]) }
                        } else null
                    )
                }
            }
        }
    }

    // Modal: Create Lead Dialog
    if (isAddingLead) {
        AddLeadDialog(
            onDismiss = { isAddingLead = false },
            onSave = { name, phone, city, vehicle, plate, origin, stage, notes ->
                viewModel.addLead(name, phone, city, vehicle, plate, origin, stage, notes)
                isAddingLead = false
            }
        )
    }

    // Modal: Lead Details and Timeline Logs
    if (selectedLeadForDetails != null) {
        LeadDetailsDialog(
            lead = selectedLeadForDetails!!,
            viewModel = viewModel,
            onDismiss = { selectedLeadForDetails = null }
        )
    }
}

@Composable
fun LeadCard(
    lead: Lead,
    onClick: () -> Unit,
    onMoveForward: (() -> Unit)?,
    onMoveBackward: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lead.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Origin chip
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = lead.origin,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${lead.vehicle} • Placa: ${lead.plate.uppercase()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (lead.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lead.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: Move columns or simple info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Atualizado: ${formatDate(lead.updatedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (onMoveBackward != null) {
                        IconButton(
                            onClick = onMoveBackward,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Recuar etapa",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (onMoveForward != null) {
                        IconButton(
                            onClick = onMoveForward,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Avançar etapa",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Dialogue: Add Lead
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLeadDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var origin by remember { mutableStateOf("WhatsApp") }
    var stage by remember { mutableStateOf("Leads") }
    var notes by remember { mutableStateOf("") }

    val origins = listOf("WhatsApp", "Redes Sociais", "Indicação", "Site", "Outro")
    val stages = listOf("Leads", "Primeiro Contato", "Interesse", "Negociação", "Documentação", "Fechado")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Adicionar Novo Lead",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefone / WhatsApp") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Cidade") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it },
                        label = { Text("Placa") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = vehicle,
                    onValueChange = { vehicle = it },
                    label = { Text("Modelo do Veículo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Dropdowns for stage and origin
                Text("Origem do Lead:", style = MaterialTheme.typography.bodySmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    origins.forEach { orig ->
                        InputChip(
                            selected = origin == orig,
                            onClick = { origin = orig },
                            label = { Text(orig, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Etapa do Funil:", style = MaterialTheme.typography.bodySmall)
                ScrollableTabRow(
                    selectedTabIndex = stages.indexOf(stage),
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    stages.forEach { stg ->
                        Tab(
                            selected = stage == stg,
                            onClick = { stage = stg },
                            text = { Text(stg, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCELAR")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && phone.isNotEmpty()) {
                                onSave(name, phone, city, vehicle, plate, origin, stage, notes)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("SALVAR")
                    }
                }
            }
        }
    }
}

// Dialog: Lead Details, WhatsApp Trigger, Interaction Timeline Logs
@Composable
fun LeadDetailsDialog(
    lead: Lead,
    viewModel: CRMViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val logs by viewModel.getLogsForLead(lead.id).collectAsState(initial = emptyList())
    var interactionText by remember { mutableStateOf("") }
    var interactionType by remember { mutableStateOf("WhatsApp") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Header details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Visualizar Lead",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(lead.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Celular/Wpp: ${lead.phone}", style = MaterialTheme.typography.bodyMedium)
                                Text("Cidade: ${lead.city}", style = MaterialTheme.typography.bodyMedium)
                                Text("Veículo: ${lead.vehicle}", style = MaterialTheme.typography.bodyMedium)
                                Text("Placa: ${lead.plate.uppercase()}", style = MaterialTheme.typography.bodyMedium)
                                Text("Estágio atual: ${lead.stage}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // WhatsApp Direct Launch Integration
                    item {
                        Button(
                            onClick = {
                                val urlText = "Olá ${lead.name}, sou consultor da CRM Proteção Veicular! Estava analisando sua simulação para o seu ${lead.vehicle}."
                                val wppIntent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?phone=55${lead.phone}&text=${Uri.encode(urlText)}")
                                }
                                try {
                                    context.startActivity(wppIntent)
                                    // Log this interaction automatically
                                    viewModel.addInteractionLog(lead.id, "WhatsApp", "Contato comercial disparado para WhatsApp.")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp não instalado.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("ABRIR WHATSAPP AUTOMATICAMENTE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    // Add Custom Interaction Log
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Registrar Nova Interação:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("WhatsApp", "Chamada", "Visita", "Proposta").forEach { item ->
                                FilterChip(
                                    selected = interactionType == item,
                                    onClick = { interactionType = item },
                                    label = { Text(item, fontSize = 10.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = interactionText,
                            onValueChange = { interactionText = it },
                            placeholder = { Text("Descreva o que foi falado com o cliente...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                if (interactionText.isNotEmpty()) {
                                    viewModel.addInteractionLog(lead.id, interactionType, interactionText)
                                    interactionText = ""
                                    Toast.makeText(context, "Interação registrada!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Salvar Nota")
                        }
                    }

                    // Logs/Timeline items
                    item {
                        Divider()
                        Text("Histórico & Timeline do Cliente:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
                    }

                    if (logs.isEmpty()) {
                        item {
                            Text("Sem interações salvas.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        items(logs) { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (log.type) {
                                                "WhatsApp" -> SuccessGreen.copy(alpha = 0.15f)
                                                "Movimentação" -> NeutralBlue.copy(alpha = 0.15f)
                                                "Chamada" -> WarningOrange.copy(alpha = 0.15f)
                                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            },
                                            CircleShape
                                        )
                                        .padding(6.dp)
                                ) {
                                    Icon(
                                        imageVector = when (log.type) {
                                            "WhatsApp" -> Icons.Default.ChatBubble
                                            "Movimentação" -> Icons.Default.SyncAlt
                                            "Chamada" -> Icons.Default.Call
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = null,
                                        tint = when (log.type) {
                                            "WhatsApp" -> SuccessGreen
                                            "Movimentação" -> NeutralBlue
                                            "Chamada" -> WarningOrange
                                            else -> MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(log.type, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                        Text(formatDate(log.timestamp) + " " + formatTime(log.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(log.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// VENDAS (SALES MANAGEMENT) SCREEN
// -------------------------------------------------------------
@Composable
fun VendasScreen(viewModel: CRMViewModel) {
    val vendas by viewModel.vendas.collectAsState()
    var isAddingVenda by remember { mutableStateOf(false) }

    var filterStatus by remember { mutableStateOf("Todos") }
    val statuses = listOf("Todos", "Adimplente", "Inadimplente", "Cancelado")

    val filteredVendas = vendas.filter { venda ->
        filterStatus == "Todos" || venda.status == filterStatus
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sales top summary statistics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Vendas Efetuadas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${vendas.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.width(1.dp).height(40.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mensalidade Ativa", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatMoney(vendas.filter { it.status == "Adimplente" }.sumOf { it.auxilioMensal }), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SuccessGreen)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gestão Comercial", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(
                onClick = { isAddingVenda = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Venda Manual")
            }
        }

        // Status Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(statuses) { status ->
                FilterChip(
                    selected = filterStatus == status,
                    onClick = { filterStatus = status },
                    label = { Text(status) }
                )
            }
        }

        if (filteredVendas.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Nenhuma venda correspondente.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredVendas) { venda ->
                    VendaRowCard(venda = venda, onStatusChange = { newStatus ->
                        viewModel.updateVendaStatus(venda, newStatus)
                    })
                }
            }
        }
    }

    if (isAddingVenda) {
        AddVendaDialog(
            onDismiss = { isAddingVenda = false },
            onSave = { client, vehicle, cat, plate, adesao, auxilio, consultant ->
                viewModel.addVenda(client, vehicle, cat, plate, adesao, auxilio, consultant)
                isAddingVenda = false
            }
        )
    }
}

@Composable
fun VendaRowCard(venda: Venda, onStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(venda.clientName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Veículo: ${venda.vehicle} • ${venda.plate.uppercase()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Dropdown or Switch selector for financial status
                Box(
                    modifier = Modifier
                        .background(
                            when (venda.status) {
                                "Adimplente" -> SuccessGreen.copy(alpha = 0.2f)
                                "Inadimplente" -> WarningOrange.copy(alpha = 0.2f)
                                else -> DangerRed.copy(alpha = 0.2f)
                            },
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = venda.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (venda.status) {
                            "Adimplente" -> SuccessGreen
                            "Inadimplente" -> WarningOrange
                            else -> DangerRed
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Adesão paga", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatMoney(venda.valueAdesao), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Mensalidade", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatMoney(venda.auxilioMensal), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text("Consultor", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(venda.consultantName, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fast status alter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Alterar status:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 8.dp))
                listOf("Adimplente", "Inadimplente", "Cancelado").forEach { st ->
                    if (st != venda.status) {
                        TextButton(
                            onClick = { onStatusChange(st) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(st, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddVendaDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Double, Double, String) -> Unit
) {
    var client by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Passeio") }
    var plate by remember { mutableStateOf("") }
    var adesao by remember { mutableStateOf("") }
    var auxilio by remember { mutableStateOf("") }
    var consultant by remember { mutableStateOf("Lucas Mendes") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("Registrar Venda Manual", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = client, onValueChange = { client = it }, label = { Text("Nome do Cliente") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = vehicle, onValueChange = { vehicle = it }, label = { Text("Veículo") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = plate, onValueChange = { plate = it }, label = { Text("Placa") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = consultant, onValueChange = { consultant = it }, label = { Text("Consultor") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = adesao,
                        onValueChange = { adesao = it },
                        label = { Text("Taxa Adesão") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = auxilio,
                        onValueChange = { auxilio = it },
                        label = { Text("Recorrência Mensal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("CANCELAR") }
                    Button(onClick = {
                        val adVal = adesao.toDoubleOrNull() ?: 0.0
                        val axVal = auxilio.toDoubleOrNull() ?: 0.0
                        if (client.isNotEmpty()) {
                            onSave(client, vehicle, category, plate, adVal, axVal, consultant)
                        }
                    }) {
                        Text("REGISTRAR")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// AGENDAMENTOS (CALENDAR & SCHEDULER) SCREEN
// -------------------------------------------------------------
@Composable
fun AgendamentosScreen(viewModel: CRMViewModel) {
    val agendamentos by viewModel.agendamentos.collectAsState()
    var isAddingAgendamento by remember { mutableStateOf(false) }

    // Interactive custom calendar picker (Days of June 2026)
    var selectedDay by remember { mutableIntStateOf(25) } // matches local metadata date

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Agenda Comercial e Vistorias",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        )

        // Month selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Junho 2026", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            IconButton(onClick = { isAddingAgendamento = true }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Agendar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
        }

        // Horizontal Row of Days
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items((15..30).toList()) { day ->
                val isSelected = selectedDay == day
                Card(
                    modifier = Modifier
                        .width(55.dp)
                        .height(70.dp)
                        .clickable { selectedDay = day },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (day % 7) {
                                1 -> "Seg"
                                2 -> "Ter"
                                3 -> "Qua"
                                4 -> "Qui"
                                5 -> "Sex"
                                6 -> "Sáb"
                                else -> "Dom"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$day",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Divider()

        // List of Events for the chosen Day (we simulate events matching date)
        val selectedDayMillis = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.JUNE)
            set(Calendar.DAY_OF_MONTH, selectedDay)
        }.timeInMillis

        // Simple mock matching by day of month
        val dayEvents = agendamentos.filter { event ->
            val calEvent = Calendar.getInstance().apply { timeInMillis = event.dateTime }
            val calSelected = Calendar.getInstance().apply { timeInMillis = selectedDayMillis }
            calEvent.get(Calendar.DAY_OF_MONTH) == calSelected.get(Calendar.DAY_OF_MONTH) &&
            calEvent.get(Calendar.MONTH) == calSelected.get(Calendar.MONTH)
        }

        if (dayEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = OutlineDark, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sem compromissos marcados para este dia.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(dayEvents) { event ->
                    AgendamentoCard(
                        event = event,
                        onStatusChange = { newStatus ->
                            viewModel.updateAgendamento(event.copy(status = newStatus))
                        },
                        onDelete = {
                            viewModel.deleteAgendamento(event)
                        }
                    )
                }
            }
        }
    }

    if (isAddingAgendamento) {
        AddAgendamentoDialog(
            selectedDay = selectedDay,
            onDismiss = { isAddingAgendamento = false },
            onSave = { title, desc, hour, client, phone ->
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, 2026)
                    set(Calendar.MONTH, Calendar.JUNE)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, 0)
                }
                viewModel.addAgendamento(title, desc, cal.timeInMillis, client, phone)
                isAddingAgendamento = false
            }
        )
    }
}

@Composable
fun AgendamentoCard(
    event: Agendamento,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatTime(event.dateTime)}h - ${event.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Box(
                    modifier = Modifier
                        .background(
                            when (event.status) {
                                "Agendado" -> WarningOrange.copy(alpha = 0.2f)
                                "Em andamento" -> NeutralBlue.copy(alpha = 0.2f)
                                "Concluído" -> SuccessGreen.copy(alpha = 0.2f)
                                else -> DangerRed.copy(alpha = 0.2f)
                            },
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = event.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (event.status) {
                            "Agendado" -> WarningOrange
                            "Em andamento" -> NeutralBlue
                            "Concluído" -> SuccessGreen
                            else -> DangerRed
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(event.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Cliente: ${event.clientName} • ${event.phone}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = DangerRed, modifier = Modifier.size(18.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Agendado", "Em andamento", "Concluído", "Cancelado").forEach { st ->
                        if (st != event.status) {
                            TextButton(
                                onClick = { onStatusChange(st) },
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Text(st, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddAgendamentoDialog(
    selectedDay: Int,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var hour by remember { mutableIntStateOf(10) }
    var client by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Novo Compromisso ($selectedDay/06)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título da Visita / Reunião") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descrição detalhada") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = client, onValueChange = { client = it }, label = { Text("Nome do Cliente") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Celular/Wpp") }, modifier = Modifier.weight(1.5f))
                    OutlinedTextField(
                        value = "$hour",
                        onValueChange = { hour = it.toIntOrNull() ?: 10 },
                        label = { Text("Hora (0-23)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("CANCELAR") }
                    Button(onClick = {
                        if (title.isNotEmpty()) {
                            onSave(title, desc, hour, client, phone)
                        }
                    }) {
                        Text("AGENDAR")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// FINANCEIRO SCREEN (FINANCE CONTROL & Split Revenue)
// -------------------------------------------------------------
@Composable
fun FinanceiroScreen(viewModel: CRMViewModel) {
    val financeiro by viewModel.financeiro.collectAsState()
    var isAddingFin by remember { mutableStateOf(false) }

    val receitas = financeiro.filter { it.type == "Receita" }.sumOf { it.value }
    val despesas = financeiro.filter { it.type == "Despesa" }.sumOf { it.value }
    val saldo = receitas - despesas

    Column(modifier = Modifier.fillMaxSize()) {
        // Cash Flow Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Fluxo de Caixa Consolidado (Mês)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatMoney(saldo), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = if (saldo >= 0) SuccessGreen else DangerRed)
                
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Receitas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(formatMoney(receitas), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = SuccessGreen)
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(DangerRed, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Despesas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(formatMoney(despesas), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = DangerRed)
                    }
                }
            }
        }

        // Automatic Split Revenues (Divisão Automática das Receitas de Proteção)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Divisão de Receitas Auto (Regras Fundo)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Divisão automática regulamentar para associação de proteção veicular:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(8.dp))

                // Split metrics: 60% Sinistros, 20% Comercial, 20% Administrativo
                val reserveSinistros = receitas * 0.60
                val reserveComercial = receitas * 0.20
                val reserveAdmin = receitas * 0.20

                Row(modifier = Modifier.fillMaxWidth().height(14.dp).clip(CircleShape)) {
                    Box(modifier = Modifier.weight(0.6f).fillMaxHeight().background(SuccessGreen))
                    Box(modifier = Modifier.weight(0.2f).fillMaxHeight().background(NeutralBlue))
                    Box(modifier = Modifier.weight(0.2f).fillMaxHeight().background(WarningOrange))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Sinistros (60%)", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        Text(formatMoney(reserveSinistros), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Comercial (20%)", style = MaterialTheme.typography.labelSmall, color = NeutralBlue, fontWeight = FontWeight.Bold)
                        Text(formatMoney(reserveComercial), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Admin (20%)", style = MaterialTheme.typography.labelSmall, color = WarningOrange, fontWeight = FontWeight.Bold)
                        Text(formatMoney(reserveAdmin), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Lançamentos de Caixa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { isAddingFin = true }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Adicionar Lançamento", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
        }

        // List of transactions
        if (financeiro.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Sem lançamentos de caixa.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(financeiro) { fin ->
                    FinanceiroRow(fin = fin, onDelete = {
                        viewModel.deleteFinanceiro(fin)
                    })
                }
            }
        }
    }

    if (isAddingFin) {
        AddFinanceDialog(
            onDismiss = { isAddingFin = false },
            onSave = { title, valItem, type, category, isRec ->
                viewModel.addFinanceiro(title, valItem, type, category, System.currentTimeMillis(), isRec)
                isAddingFin = false
            }
        )
    }
}

@Composable
fun FinanceiroRow(fin: Financeiro, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .background(
                            if (fin.type == "Receita") SuccessGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f),
                            CircleShape
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (fin.type == "Receita") Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (fin.type == "Receita") SuccessGreen else DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(fin.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("Categoria: ${fin.category} • ${formatDate(fin.date)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = (if (fin.type == "Receita") "+" else "-") + formatMoney(fin.value),
                    fontWeight = FontWeight.Bold,
                    color = if (fin.type == "Receita") SuccessGreen else DangerRed,
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Deletar", tint = OutlineDark, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun AddFinanceDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Receita") }
    var category by remember { mutableStateOf("Fixa") }
    var isRec by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Registrar Transação de Caixa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Descrição do lançamento") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Valor (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Tipo de Lançamento:", style = MaterialTheme.typography.bodySmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { type = "Receita" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (type == "Receita") SuccessGreen else MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("RECEITA", color = if (type == "Receita") Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                    Button(
                        onClick = { type = "Despesa" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (type == "Despesa") DangerRed else MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("DESPESA", color = if (type == "Despesa") Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Categoria:", style = MaterialTheme.typography.bodySmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Fixa", "Variável", "Imposto", "Marketing", "Outro").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isRec, onCheckedChange = { isRec = it })
                    Text("Lançamento Recorrente (Mensal)", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("CANCELAR") }
                    Button(onClick = {
                        val valDouble = value.toDoubleOrNull() ?: 0.0
                        if (title.isNotEmpty() && valDouble > 0) {
                            onSave(title, valDouble, type, category, isRec)
                        }
                    }) {
                        Text("SALVAR")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// NOTIFICATION HUB SCREEN
// -------------------------------------------------------------
@Composable
fun NotificationHubScreen(viewModel: CRMViewModel) {
    val notificationLogs by viewModel.notificationLogs.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Centro de Notificações", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            TextButton(onClick = { viewModel.clearNotifications() }) {
                Text("Limpar", color = DangerRed)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (notificationLogs.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Nenhum alerta comercial recente.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notificationLogs) { log ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = WarningOrange)
                            Text(log, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// USERS / PROFILE SCREEN
// -------------------------------------------------------------
@Composable
fun UsersScreen(viewModel: CRMViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val users by viewModel.users.collectAsState()
    var isAddingUser by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Active User Profile Panel
        currentUser?.let { user ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(user.level.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Action: Register Users
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Membros da Equipe", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (currentUser?.level == "Administrador" || currentUser?.level == "Gerente") {
                Button(onClick = { isAddingUser = true }) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar")
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(users) { u ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(u.name, fontWeight = FontWeight.Bold)
                            Text(u.email, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Cargo: ${u.level}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                        
                        // Admin capabilities
                        if ((currentUser?.level == "Administrador" || currentUser?.level == "Gerente") && u.id != currentUser?.id) {
                            IconButton(onClick = { viewModel.deleteUser(u) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = DangerRed)
                            }
                        }
                    }
                }
            }
        }
    }

    if (isAddingUser) {
        AddUserDialog(
            onDismiss = { isAddingUser = false },
            onSave = { name, email, level, pass ->
                viewModel.addUser(User(name = name, email = email, level = level, passwordHash = pass))
                isAddingUser = false
            }
        )
    }
}

@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("Consultor") }
    var password by remember { mutableStateOf("") }

    val levels = listOf("Administrador", "Gerente", "Consultor", "Financeiro")

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("Cadastrar Novo Membro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome Completo") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail corporativo") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha de Acesso") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Nível de Acesso (Perfil comercial):", style = MaterialTheme.typography.bodySmall)
                ScrollableTabRow(
                    selectedTabIndex = levels.indexOf(level),
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    levels.forEach { lv ->
                        Tab(
                            selected = level == lv,
                            onClick = { level = lv },
                            text = { Text(lv, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("CANCELAR") }
                    Button(onClick = {
                        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                            onSave(name, email, level, password)
                        }
                    }) {
                        Text("CADASTRAR")
                    }
                }
            }
        }
    }
}
