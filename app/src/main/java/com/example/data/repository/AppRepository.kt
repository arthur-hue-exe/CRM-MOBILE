package com.example.data.repository

import com.example.data.dao.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val leadDao: LeadDao,
    private val vendaDao: VendaDao,
    private val agendamentoDao: AgendamentoDao,
    private val financeiroDao: FinanceiroDao,
    private val logDao: InteractionLogDao
) {
    // Users
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    // Leads
    val allLeads: Flow<List<Lead>> = leadDao.getAllLeads()
    fun getLeadsByStage(stage: String): Flow<List<Lead>> = leadDao.getLeadsByStage(stage)
    suspend fun insertLead(lead: Lead): Long = leadDao.insertLead(lead)
    suspend fun updateLead(lead: Lead) = leadDao.updateLead(lead)
    suspend fun deleteLead(lead: Lead) = leadDao.deleteLead(lead)

    // Vendas (Sales)
    val allVendas: Flow<List<Venda>> = vendaDao.getAllVendas()
    suspend fun insertVenda(venda: Venda): Long = vendaDao.insertVenda(venda)
    suspend fun updateVenda(venda: Venda) = vendaDao.updateVenda(venda)
    suspend fun deleteVenda(venda: Venda) = vendaDao.deleteVenda(venda)

    // Agendamentos (Appointments)
    val allAgendamentos: Flow<List<Agendamento>> = agendamentoDao.getAllAgendamentos()
    suspend fun insertAgendamento(agendamento: Agendamento): Long = agendamentoDao.insertAgendamento(agendamento)
    suspend fun updateAgendamento(agendamento: Agendamento) = agendamentoDao.updateAgendamento(agendamento)
    suspend fun deleteAgendamento(agendamento: Agendamento) = agendamentoDao.deleteAgendamento(agendamento)

    // Financeiro (Financial)
    val allFinanceiro: Flow<List<Financeiro>> = financeiroDao.getAllFinanceiro()
    suspend fun insertFinanceiro(financeiro: Financeiro): Long = financeiroDao.insertFinanceiro(financeiro)
    suspend fun updateFinanceiro(financeiro: Financeiro) = financeiroDao.updateFinanceiro(financeiro)
    suspend fun deleteFinanceiro(financeiro: Financeiro) = financeiroDao.deleteFinanceiro(financeiro)

    // Interaction Logs
    fun getLogsByLead(leadId: Long): Flow<List<InteractionLog>> = logDao.getLogsByLead(leadId)
    suspend fun insertLog(log: InteractionLog): Long = logDao.insertLog(log)
    suspend fun deleteLogsForLead(leadId: Long) = logDao.deleteLogsForLead(leadId)
}
