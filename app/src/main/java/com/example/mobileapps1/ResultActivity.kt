package com.example.mobileapps1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        val discardButton = findViewById<Button>(R.id.discardButton)
        discardButton.setOnClickListener {
            finish()
        }


    }
}