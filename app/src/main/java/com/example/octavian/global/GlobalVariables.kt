package com.example.octavian.global

import android.content.Context
import com.example.octavian.dataClass.CartItem
import java.io.File

object GlobalVariables {

    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
    val cacheDir: File
        get() = appContext.cacheDir

    lateinit var CARTLIST: MutableList<CartItem>
}