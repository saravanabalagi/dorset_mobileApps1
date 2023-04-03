package com.example.mobileapps1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "sampleNotifChannel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE) ?: return
        val userID = sharedPref.getInt("userID", -1)
        Toast.makeText(this, "userID: $userID", Toast.LENGTH_LONG).show()

//        write sharedPreference
//        with (sharedPref.edit()) {
//            putInt("userID", 11)
//            apply()
//        }

//        val rootView = window.decorView.rootView
        val rootView = findViewById<View>(R.id.root_layout)

        val url_simple_text = "https://gist.githubusercontent.com/saravanabalagi/3bcc93edc84d700ae62433edd45f3a07/raw/69ca44bbb610541b1898c4e6199c77f62442b89c/sample.txt"
        val url_json_array = "https://jsonplaceholder.typicode.com/users"
//        makeGetTextRequest(url_simple_text)
        makeGetTextRequest(url_json_array)

        val helloText = findViewById<TextView>(R.id.welcomeText)
        helloText.text = getString(R.string.hello_dorset)

        val welcomeText = findViewById<TextView>(R.id.welcomeNameText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val nameText = nameEditText.text

        Handler(Looper.getMainLooper()).postDelayed({
            Snackbar.make(rootView, "This should appear after 5 seconds", Snackbar.LENGTH_LONG).show()
        }, 5000)

        val notificationIntent = Intent(this, MainActivity2::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_link_24)
            .setContentTitle("Sample Notification")
            .setContentText("This is an awesome notification text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationId = 100
        createNotificationChannel()
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
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

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this, "Result received successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Result action cancelled", Toast.LENGTH_LONG).show()
            }
        }
        val resultIntent = Intent(this, ResultActivity::class.java)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = Uri.parse("https://github.com/saravanabalagi/dorset_mobileApps1")
//            }
//            startActivity(intent)
            launcher.launch(resultIntent)
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

    private fun makeGetTextRequest(url: String) {
        val client = OkHttpClient()

        val sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE) ?: return
        val token = sharedPref.getString("token", "N/A")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainAct", "GET request failed $e")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val resBody = response.body?.string()
                    val gson = Gson()
                    val contacts: Array<Contact> = gson.fromJson(resBody, Array<Contact>::class.java)
                    Log.i("MainAct", "Response: $resBody")
                    contacts.forEach {
                        Log.i("MainAct", "Contacts $it")
                    }
//                    Handler(Looper.getMainLooper()).post {
//                        Toast.makeText(this@MainActivity, resBody, Toast.LENGTH_LONG).show()
//                    }
                }
            }
        })
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sample Channel Category"
            val descriptionText = "Sample Channel Category Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


}