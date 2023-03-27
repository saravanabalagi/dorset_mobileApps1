package com.example.mobileapps1

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val rootView = window.decorView.rootView
        val rootView = findViewById<View>(R.id.root_layout)

        val helloText = findViewById<TextView>(R.id.welcomeText)
        helloText.text = getString(R.string.hello_dorset)

        val welcomeText = findViewById<TextView>(R.id.welcomeNameText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val nameText = nameEditText.text

        Handler(Looper.getMainLooper()).postDelayed({
            Snackbar.make(rootView, "This should appear after 5 seconds", Snackbar.LENGTH_LONG).show()
        }, 5000)

        saveButton.setOnClickListener {
            val welcomeTextString = "Welcome, $nameText!"
            welcomeText.text = welcomeTextString
            it.hideKeyboard()
            Toast.makeText(this, "Save button clicked", Toast.LENGTH_LONG).show()
            val sb = Snackbar.make(rootView, "Save button clicked", Snackbar.LENGTH_INDEFINITE)
            sb.setAction("dismiss") {
                sb.dismiss()
            }
            sb.show()

            Log.i("MainAct", "Text entered $nameText")
            if (nameText.toString() == "Hide") {
                Log.i("MainAct", "Inside Hide if")
                saveButton.visibility = View.GONE
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://github.com/saravanabalagi/dorset_mobileApps1")
            }
            startActivity(intent)
        }

        val nextPageButton = findViewById<Button>(R.id.nextPageButton)
        nextPageButton.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        val exampleViewPager = findViewById<ViewPager2>(R.id.exampleViewPager)
        exampleViewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount() = 3
            override fun createFragment(position: Int): Fragment = SampleFragment().apply {
                arguments = Bundle().apply {
                    putString("username", "This is page ${position + 1}")
                    putString("imageUrl", "https://cdn.pixabay.com/photo/2023/01/31/05/59/zebra-7757193_1280.jpg")
                }
            }
        }
        val exampleTabLayout = findViewById<TabLayout>(R.id.exampleTabLayout)
        TabLayoutMediator(exampleTabLayout, exampleViewPager) { tab, position ->
            tab.text = "Page ${position + 1}"
            val rId = if (position == 0) R.drawable.baseline_link_24 else R.drawable.baseline_person_24
            tab.icon = AppCompatResources.getDrawable(this, rId)
        }.attach()
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


}