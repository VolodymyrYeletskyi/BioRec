package com.yeletskyiv.biorec

import android.app.Application
import com.yeletskyiv.biorec.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BioRecApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BioRecApp)
            modules(viewModelModule)
        }
    }
}