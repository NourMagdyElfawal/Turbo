package com.example.turbo.generatebarcode.components

interface StateListener {
     fun onStarted()
     fun onSuccess()
     fun onFailure(message:String)
}