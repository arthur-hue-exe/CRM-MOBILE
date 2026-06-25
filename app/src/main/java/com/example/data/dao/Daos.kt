package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY updatedAt DESC")
    fun getAllLeads(): Flow<List<Lead>>

    @Query("SELECT * FROM leads WHERE stage = :stage ORDER BY updatedAt DESC")
    fun getLeadsByStage(stage: String): Flow<List<Lead>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: Lead): Long

    @Update
    suspend fun updateLead(lead: Lead)

    @Delete
    suspend fun deleteLead(lead: Lead)
}

@Dao
interface VendaDao {
    @Query("SELECT * FROM vendas ORDER BY saleDate DESC")
    fun getAllVendas(): Flow<List<Venda>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenda(venda: Venda): Long

    @Update
    suspend fun updateVenda(venda: Venda)

    @Delete
    suspend fun deleteVenda(venda: Venda)
}

@Dao
interface AgendamentoDao {
    @Query("SELECT * FROM agendamentos ORDER BY dateTime ASC")
    fun getAllAgendamentos(): Flow<List<Agendamento>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgendamento(agendamento: Agendamento): Long

    @Update
    suspend fun updateAgendamento(agendamento: Agendamento)

    @Delete
    suspend fun deleteAgendamento(agendamento: Agendamento)
}

@Dao
interface FinanceiroDao {
    @Query("SELECT * FROM financeiro ORDER BY date DESC")
    fun getAllFinanceiro(): Flow<List<Financeiro>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinanceiro(financeiro: Financeiro): Long

    @Update
    suspend fun updateFinanceiro(financeiro: Financeiro)

    @Delete
    suspend fun deleteFinanceiro(financeiro: Financeiro)
}

@Dao
interface InteractionLogDao {
    @Query("SELECT * FROM interaction_logs WHERE leadId = :leadId ORDER BY timestamp DESC")
    fun getLogsByLead(leadId: Long): Flow<List<InteractionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: InteractionLog): Long

    @Query("DELETE FROM interaction_logs WHERE leadId = :leadId")
    suspend fun deleteLogsForLead(leadId: Long)
}
