package com.example.octavian.activity

import com.example.octavian.dataClass.CartItem
import com.example.octavian.global.GlobalVariables

class ClickListenerInit {
    val app = GlobalVariables

    fun onCartClick(itemCart: CartItem) {
        if(!app.CARTLIST.contains(itemCart)) {
            app.CARTLIST.add(itemCart)
        }
    }

}