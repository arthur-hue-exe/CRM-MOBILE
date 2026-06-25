package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val level: String, // Administrador, Gerente, Consultor, Financeiro
    val passwordHash: String,
    val isActive: Boolean = true
) : Serializable

@Entity(tableName = "leads")
data class Lead(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val city: String,
    val vehicle: String,
    val plate: String,
    val origin: String, // WhatsApp, Indicação, Redes Sociais, Site, Outro
    val stage: String, // Leads, Primeiro Contato, Interesse, Negociação, Documentação, Fechado
    val notes: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "vendas")
data class Venda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientName: String,
    val vehicle: String,
    val category: String, // Passeio, Moto, Utilitário, Pesado
    val plate: String,
    val valueAdesao: Double,
    val auxilioMensal: Double,
    val saleDate: Long,
    val consultantName: String,
    val status: String // Adimplente, Inadimplente, Cancelado
) : Serializable

@Entity(tableName = "agendamentos")
data class Agendamento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val dateTime: Long,
    val status: String, // Agendado, Em andamento, Concluído, Cancelado
    val clientName: String,
    val phone: String,
    val isReminderSent: Boolean = false
) : Serializable

@Entity(tableName = "financeiro")
data class Financeiro(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val value: Double,
    val type: String, // Receita, Despesa
    val category: String, // Fixa, Variável, Venda Adesão, Mensalidade
    val date: Long,
    val isRecurring: Boolean = false,
    val notes: String = ""
) : Serializable

@Entity(tableName = "interaction_logs")
data class InteractionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val leadId: Long,
    val type: String, // WhatsApp, Chamada, Visita, Proposta
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
