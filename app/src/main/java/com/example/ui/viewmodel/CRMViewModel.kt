package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.CRMApplication
import com.example.data.entity.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class CRMViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    // Authentication State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Realtime Data Flows from Room
    val users: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leads: StateFlow<List<Lead>> = repository.allLeads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vendas: StateFlow<List<Venda>> = repository.allVendas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agendamentos: StateFlow<List<Agendamento>> = repository.allAgendamentos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financeiro: StateFlow<List<Financeiro>> = repository.allFinanceiro
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications state
    private val _notificationLogs = MutableStateFlow<List<String>>(emptyList())
    val notificationLogs: StateFlow<List<String>> = _notificationLogs.asStateFlow()

    init {
        // Initial Notification Mock Logs
        _notificationLogs.value = listOf(
            "Alerta: Lead 'Marcos Vinícius' está sem contato há mais de 48 horas.",
            "Financeiro: Recebimento mensalidade 'Cláudia Souza' vencendo amanhã.",
            "Meta Comercial: 68% da meta de adesão de Junho atingida!"
        )
    }

    // -----------------------------------------
    // AUTH LOGIC
    // -----------------------------------------
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginError.value = null
            val matchedUser = users.value.find { it.email.trim().equals(email.trim(), ignoreCase = true) }
            if (matchedUser != null) {
                if (matchedUser.passwordHash == password) {
                    _currentUser.value = matchedUser
                    addNotification("Bem-vindo de volta, ${matchedUser.name}!")
                    onSuccess()
                } else {
                    _loginError.value = "Senha incorreta."
                }
            } else {
                _loginError.value = "Usuário não cadastrado."
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginError.value = null
    }

    // -----------------------------------------
    // KANBAN / LEADS LOGIC
    // -----------------------------------------
    fun addLead(
        name: String,
        phone: String,
        city: String,
        vehicle: String,
        plate: String,
        origin: String,
        stage: String,
        notes: String
    ) {
        viewModelScope.launch {
            val newLead = Lead(
                name = name,
                phone = phone,
                city = city,
                vehicle = vehicle,
                plate = plate,
                origin = origin,
                stage = stage,
                notes = notes
            )
            val id = repository.insertLead(newLead)
            repository.insertLog(
                InteractionLog(
                    leadId = id,
                    type = "Sistema",
                    notes = "Lead cadastrado na etapa '$stage' via aplicativo mobile."
                )
            )
            addNotification("Novo Lead cadastrado: $name")
        }
    }

    fun updateLeadStage(leadId: Long, newStage: String) {
        viewModelScope.launch {
            val leadList = leads.value
            val lead = leadList.find { it.id == leadId }
            if (lead != null) {
                val oldStage = lead.stage
                val updated = lead.copy(stage = newStage, updatedAt = System.currentTimeMillis())
                repository.updateLead(updated)
                
                // Add timeline history
                repository.insertLog(
                    InteractionLog(
                        leadId = leadId,
                        type = "Movimentação",
                        notes = "Lead movido de '$oldStage' para '$newStage'."
                    )
                )

                // If stage changed to "Fechado", automatically create a Venda (Sale) to mimic professional CRM flow
                if (newStage == "Fechado" && oldStage != "Fechado") {
                    val defaultAdesao = 250.00
                    val defaultMensal = 129.90
                    repository.insertVenda(
                        Venda(
                            clientName = lead.name,
                            vehicle = lead.vehicle,
                            category = "Passeio",
                            plate = lead.plate,
                            valueAdesao = defaultAdesao,
                            auxilioMensal = defaultMensal,
                            saleDate = System.currentTimeMillis(),
                            consultantName = currentUser.value?.name ?: "Consultor Mobile",
                            status = "Adimplente"
                        )
                    )
                    // Save financial receipt for adesão
                    repository.insertFinanceiro(
                        Financeiro(
                            title = "Adesão Auto - ${lead.name}",
                            value = defaultAdesao,
                            type = "Receita",
                            category = "Venda Adesão",
                            date = System.currentTimeMillis()
                        )
                    )
                    addNotification("Venda gerada automaticamente para ${lead.name}!")
                } else {
                    addNotification("Estágio do Lead '${lead.name}' atualizado para '$newStage'")
                }
            }
        }
    }

    fun updateLead(lead: Lead) {
        viewModelScope.launch {
            repository.updateLead(lead.copy(updatedAt = System.currentTimeMillis()))
            addNotification("Lead '${lead.name}' editado com sucesso.")
        }
    }

    fun deleteLead(lead: Lead) {
        viewModelScope.launch {
            repository.deleteLead(lead)
            repository.deleteLogsForLead(lead.id)
            addNotification("Lead '${lead.name}' removido.")
        }
    }

    // -----------------------------------------
    // VENDAS (SALES) LOGIC
    // -----------------------------------------
    fun addVenda(
        clientName: String,
        vehicle: String,
        category: String,
        plate: String,
        valueAdesao: Double,
        auxilioMensal: Double,
        consultantName: String,
        status: String = "Adimplente"
    ) {
        viewModelScope.launch {
            val novaVenda = Venda(
                clientName = clientName,
                vehicle = vehicle,
                category = category,
                plate = plate,
                valueAdesao = valueAdesao,
                auxilioMensal = auxilioMensal,
                saleDate = System.currentTimeMillis(),
                consultantName = consultantName,
                status = status
            )
            repository.insertVenda(novaVenda)
            
            // Register finance entry for the adesão value
            if (valueAdesao > 0) {
                repository.insertFinanceiro(
                    Financeiro(
                        title = "Adesão - $clientName",
                        value = valueAdesao,
                        type = "Receita",
                        category = "Venda Adesão",
                        date = System.currentTimeMillis()
                    )
                )
            }
            addNotification("Venda de Adesão registrada para $clientName.")
        }
    }

    fun updateVendaStatus(venda: Venda, newStatus: String) {
        viewModelScope.launch {
            val updated = venda.copy(status = newStatus)
            repository.updateVenda(updated)
            addNotification("Status de venda de '${venda.clientName}' alterado para '$newStatus'.")
        }
    }

    fun deleteVenda(venda: Venda) {
        viewModelScope.launch {
            repository.deleteVenda(venda)
            addNotification("Registro de venda para '${venda.clientName}' deletado.")
        }
    }

    // -----------------------------------------
    // AGENDAMENTOS (CALENDAR) LOGIC
    // -----------------------------------------
    fun addAgendamento(
        title: String,
        description: String,
        dateTime: Long,
        clientName: String,
        phone: String
    ) {
        viewModelScope.launch {
            val novoAgendamento = Agendamento(
                title = title,
                description = description,
                dateTime = dateTime,
                status = "Agendado",
                clientName = clientName,
                phone = phone
            )
            repository.insertAgendamento(novoAgendamento)
            addNotification("Evento '$title' adicionado à agenda.")
        }
    }

    fun updateAgendamento(agendamento: Agendamento) {
        viewModelScope.launch {
            repository.updateAgendamento(agendamento)
            addNotification("Agendamento '${agendamento.title}' atualizado.")
        }
    }

    fun deleteAgendamento(agendamento: Agendamento) {
        viewModelScope.launch {
            repository.deleteAgendamento(agendamento)
            addNotification("Compromisso '${agendamento.title}' removido da agenda.")
        }
    }

    // -----------------------------------------
    // FINANCEIRO LOGIC
    // -----------------------------------------
    fun addFinanceiro(
        title: String,
        value: Double,
        type: String,
        category: String,
        date: Long,
        isRecurring: Boolean = false,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val novoFinanceiro = Financeiro(
                title = title,
                value = value,
                type = type,
                category = category,
                date = date,
                isRecurring = isRecurring,
                notes = notes
            )
            repository.insertFinanceiro(novoFinanceiro)
            addNotification("Lançamento financeiro registrado: $title.")
        }
    }

    fun deleteFinanceiro(financeiro: Financeiro) {
        viewModelScope.launch {
            repository.deleteFinanceiro(financeiro)
            addNotification("Lançamento financeiro '${financeiro.title}' excluído.")
        }
    }

    // -----------------------------------------
    // USERS MANAGEMENT LOGIC
    // -----------------------------------------
    fun addUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
            addNotification("Usuário '${user.name}' cadastrado.")
        }
    }

    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
            if (_currentUser.value?.id == user.id) {
                _currentUser.value = user
            }
            addNotification("Perfil de '${user.name}' atualizado.")
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            if (user.id == _currentUser.value?.id) {
                _loginError.value = "Não é possível excluir o próprio usuário logado."
                return@launch
            }
            repository.deleteUser(user)
            addNotification("Usuário '${user.name}' excluído.")
        }
    }

    // -----------------------------------------
    // TIMELINE/LOGS
    // -----------------------------------------
    fun getLogsForLead(leadId: Long): Flow<List<InteractionLog>> {
        return repository.getLogsByLead(leadId)
    }

    fun addInteractionLog(leadId: Long, type: String, notes: String) {
        viewModelScope.launch {
            repository.insertLog(
                InteractionLog(leadId = leadId, type = type, notes = notes)
            )
        }
    }

    // Helper notifications list management
    fun addNotification(message: String) {
        val currentList = _notificationLogs.value.toMutableList()
        currentList.add(0, "[${getFormattedTime()}] $message")
        if (currentList.size > 15) {
            currentList.removeAt(currentList.lastIndex)
        }
        _notificationLogs.value = currentList
    }

    fun clearNotifications() {
        _notificationLogs.value = emptyList()
    }

    private fun getFormattedTime(): String {
        val cal = Calendar.getInstance()
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }
}

class CRMViewModelFactory(
    private val application: Application,
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CRMViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CRMViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
