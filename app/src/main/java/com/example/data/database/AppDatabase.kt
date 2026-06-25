package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        User::class,
        Lead::class,
        Venda::class,
        Agendamento::class,
        Financeiro::class,
        InteractionLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun leadDao(): LeadDao
    abstract fun vendaDao(): VendaDao
    abstract fun agendamentoDao(): AgendamentoDao
    abstract fun financeiroDao(): FinanceiroDao
    abstract fun interactionLogDao(): InteractionLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crm_protecao_veicular_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val userDao = db.userDao()
            val leadDao = db.leadDao()
            val vendaDao = db.vendaDao()
            val agendamentoDao = db.agendamentoDao()
            val financeiroDao = db.financeiroDao()
            val logDao = db.interactionLogDao()

            // 1. Initial Users
            val adminId = userDao.insertUser(
                User(
                    name = "Carlos Souza (Diretor)",
                    email = "admin@crm.com",
                    level = "Administrador",
                    passwordHash = "123456"
                )
            )
            userDao.insertUser(
                User(
                    name = "Amanda Ribeiro (Gerente Comercial)",
                    email = "gerente@crm.com",
                    level = "Gerente",
                    passwordHash = "123456"
                )
            )
            userDao.insertUser(
                User(
                    name = "Lucas Mendes (Consultor)",
                    email = "lucas@crm.com",
                    level = "Consultor",
                    passwordHash = "123456"
                )
            )
            userDao.insertUser(
                User(
                    name = "Beatriz Lima (Financeiro)",
                    email = "financeiro@crm.com",
                    level = "Financeiro",
                    passwordHash = "123456"
                )
            )

            // 2. Initial Leads across Kanban stages
            val lead1 = leadDao.insertLead(
                Lead(
                    name = "Marcos Vinícius",
                    phone = "31988887777",
                    city = "Belo Horizonte",
                    vehicle = "Toyota Corolla 2021",
                    plate = "RFG5H22",
                    origin = "WhatsApp",
                    stage = "Leads",
                    notes = "Lead interessado em proteção integral contra roubo, furto e colisão."
                )
            )
            val lead2 = leadDao.insertLead(
                Lead(
                    name = "Mariana Alves",
                    phone = "11977776666",
                    city = "São Paulo",
                    vehicle = "Honda Biz 125 2023",
                    plate = "QWY9J11",
                    origin = "Redes Sociais",
                    stage = "Primeiro Contato",
                    notes = "Contatada ontem. Quer simulação para plano de moto com assistência 24h."
                )
            )
            val lead3 = leadDao.insertLead(
                Lead(
                    name = "Roberto Santos",
                    phone = "21966665555",
                    city = "Rio de Janeiro",
                    vehicle = "Chevrolet Onix 2019",
                    plate = "KLP8H88",
                    origin = "Indicação",
                    stage = "Interesse",
                    notes = "Indicado pelo cliente Paulo. Exigiu rastreador incluso no plano."
                )
            )
            val lead4 = leadDao.insertLead(
                Lead(
                    name = "Juliana Costa",
                    phone = "31955554444",
                    city = "Contagem",
                    vehicle = "Jeep Compass 2022",
                    plate = "OWX3G44",
                    origin = "Site",
                    stage = "Negociação",
                    notes = "Enviamos desconto de 15% na taxa de adesão. Aguardando retorno da proposta."
                )
            )
            val lead5 = leadDao.insertLead(
                Lead(
                    name = "Pedro Oliveira",
                    phone = "11944443333",
                    city = "Campinas",
                    vehicle = "Fiat Toro 2020",
                    plate = "GHA4D55",
                    origin = "WhatsApp",
                    stage = "Documentação",
                    notes = "Documentos recebidos (CNH, CRLV e comprovante). Pronto para vistoria digital."
                )
            )
            val lead6 = leadDao.insertLead(
                Lead(
                    name = "Ana Paula Vieira",
                    phone = "21933332222",
                    city = "Niterói",
                    vehicle = "Hyundai HB20 2022",
                    plate = "ABC1D23",
                    origin = "Redes Sociais",
                    stage = "Fechado",
                    notes = "Venda concluída com sucesso! Proteção ativa."
                )
            )

            // 3. Interaction Logs for Leads
            logDao.insertLog(InteractionLog(leadId = lead1, type = "WhatsApp", notes = "Mensagem automática de boas-vindas enviada."))
            logDao.insertLog(InteractionLog(leadId = lead2, type = "Chamada", notes = "Ligação efetuada, cliente solicitou retorno após as 18h."))
            logDao.insertLog(InteractionLog(leadId = lead3, type = "Proposta", notes = "Proposta de R$ 139,90/mês enviada via WhatsApp."))
            logDao.insertLog(InteractionLog(leadId = lead4, type = "WhatsApp", notes = "Discussão de valores de coparticipação realizada."))
            logDao.insertLog(InteractionLog(leadId = lead5, type = "Visita", notes = "Vistoria presencial agendada na casa do cliente."))

            // 4. Sales Records
            vendaDao.insertVenda(
                Venda(
                    clientName = "Ana Paula Vieira",
                    vehicle = "Hyundai HB20 2022",
                    category = "Passeio",
                    plate = "ABC1D23",
                    valueAdesao = 250.00,
                    auxilioMensal = 129.90,
                    saleDate = System.currentTimeMillis() - 4 * 24 * 3600 * 1000L,
                    consultantName = "Lucas Mendes",
                    status = "Adimplente"
                )
            )
            vendaDao.insertVenda(
                Venda(
                    clientName = "Ricardo Andrade",
                    vehicle = "Yamaha Fazer 250",
                    category = "Moto",
                    plate = "XYZ9Y99",
                    valueAdesao = 150.00,
                    auxilioMensal = 89.90,
                    saleDate = System.currentTimeMillis() - 10 * 24 * 3600 * 1000L,
                    consultantName = "Lucas Mendes",
                    status = "Adimplente"
                )
            )
            vendaDao.insertVenda(
                Venda(
                    clientName = "Cláudia Souza",
                    vehicle = "Scania R440",
                    category = "Pesado",
                    plate = "SCA8A88",
                    valueAdesao = 600.00,
                    auxilioMensal = 399.90,
                    saleDate = System.currentTimeMillis() - 25 * 24 * 3600 * 1000L,
                    consultantName = "Lucas Mendes",
                    status = "Inadimplente"
                )
            )
            vendaDao.insertVenda(
                Venda(
                    clientName = "Geraldo Magela",
                    vehicle = "Renault Master Baú",
                    category = "Utilitário",
                    plate = "BAU7B77",
                    valueAdesao = 400.00,
                    auxilioMensal = 249.90,
                    saleDate = System.currentTimeMillis() - 40 * 24 * 3600 * 1000L,
                    consultantName = "Beatriz Lima",
                    status = "Cancelado"
                )
            )

            // 5. Calendar Appointments
            val today = Calendar.getInstance()
            
            val calendar1 = today.clone() as Calendar
            calendar1.set(Calendar.HOUR_OF_DAY, 10)
            agendamentoDao.insertAgendamento(
                Agendamento(
                    title = "Vistoria Toyota Corolla",
                    description = "Vistoria técnica presencial obrigatória.",
                    dateTime = calendar1.timeInMillis,
                    status = "Agendado",
                    clientName = "Marcos Vinícius",
                    phone = "31988887777"
                )
            )

            val calendar2 = today.clone() as Calendar
            calendar2.add(Calendar.DAY_OF_YEAR, 1)
            calendar2.set(Calendar.HOUR_OF_DAY, 14)
            agendamentoDao.insertAgendamento(
                Agendamento(
                    title = "Assinatura Contrato Jeep Compass",
                    description = "Assinar adesão e ativar no sistema da associação.",
                    dateTime = calendar2.timeInMillis,
                    status = "Agendado",
                    clientName = "Juliana Costa",
                    phone = "31955554444"
                )
            )

            val calendar3 = today.clone() as Calendar
            calendar3.add(Calendar.DAY_OF_YEAR, -1)
            calendar3.set(Calendar.HOUR_OF_DAY, 11)
            agendamentoDao.insertAgendamento(
                Agendamento(
                    title = "Simulação Honda Biz",
                    description = "Apresentar plano básico e assistência de guincho.",
                    dateTime = calendar3.timeInMillis,
                    status = "Concluído",
                    clientName = "Mariana Alves",
                    phone = "11977776666"
                )
            )

            // 6. Financial transactions (receitas e despesas)
            // Revenues (Adesões, Mensalidades)
            financeiroDao.insertFinanceiro(
                Financeiro(
                    title = "Adesão Corolla - Marcos",
                    value = 250.00,
                    type = "Receita",
                    category = "Venda Adesão",
                    date = System.currentTimeMillis() - 2 * 3600 * 1000L,
                    isRecurring = false,
                    notes = "Pagamento via Pix efetuado na entrega do termo."
                )
            )
            financeiroDao.insertFinanceiro(
                Financeiro(
                    title = "Adesão HB20 - Ana Paula",
                    value = 250.00,
                    type = "Receita",
                    category = "Venda Adesão",
                    date = System.currentTimeMillis() - 4 * 24 * 3600 * 1000L,
                    isRecurring = false
                )
            )
            financeiroDao.insertFinanceiro(
                Financeiro(
                    title = "Mensalidades Ativas - Carteira Passeio",
                    value = 14850.00,
                    type = "Receita",
                    category = "Mensalidade",
                    date = System.currentTimeMillis() - 5 * 24 * 3600 * 1000L,
                    isRecurring = true,
                    notes = "Recebimento recorrente consolidado de boletos."
                )
            )
            
            // Expenses (Fixas e Variáveis)
            financeiroDao.insertFinanceiro(
                Financeiro(
                    title = "Aluguel da Sede",
                    value = 2200.00,
                    type = "Despesa",
                    category = "Fixa",
                    date = System.currentTimeMillis() - 6 * 24 * 3600 * 1000L,
                    isRecurring = true
                )
            )
            financeiroDao.insertFinanceiro(
                Financeiro(
                    title = "Servidores e AWS Cloud CRM",
                    value = 450.00,
                    type = "Despesa",
                    category = "Fixa",
                    date = System.currentTimeMillis() - 12 * 24 * 3600 * 1000L,
                    isRecurring = true
                )
            )
            financeiroDao.insertFinanceiro(
                Financeiro(
                    title = "Combustível p/ Vistorias",
                    value = 350.00,
                    type = "Despesa",
                    category = "Variável",
                    date = System.currentTimeMillis() - 1 * 24 * 3600 * 1000L,
                    isRecurring = false
                )
            )
            financeiroDao.insertFinanceiro(
                Financeiro(
                    title = "Comissão Vendedores (Junho)",
                    value = 1200.00,
                    type = "Despesa",
                    category = "Variável",
                    date = System.currentTimeMillis() - 3 * 24 * 3600 * 1000L,
                    isRecurring = false
                )
            )
        }
    }
}
