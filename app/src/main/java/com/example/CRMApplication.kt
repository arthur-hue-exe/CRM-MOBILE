package com.example

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CRMApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    
    val repository by lazy {
        AppRepository(
            userDao = database.userDao(),
            leadDao = database.leadDao(),
            vendaDao = database.vendaDao(),
            agendamentoDao = database.agendamentoDao(),
            financeiroDao = database.financeiroDao(),
            logDao = database.interactionLogDao()
        )
    }
}
