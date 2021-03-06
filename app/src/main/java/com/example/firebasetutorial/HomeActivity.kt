package com.example.firebasetutorial

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType {
    BASIC
}

class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        setup(email ?: "", provider ?: "")

        //Guardado de datos
        val prefs: SharedPreferences.Editor =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"
        emailTextView.text = email
        providerTextView.text = provider

        logOutButton.setOnClickListener {
            //Borrar datos
            val prefs: SharedPreferences.Editor =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        saveButton.setOnClickListener {
            db.collection("users").document(email).set(
                hashMapOf("provider" to provider, "address" to addressTextView.text.toString(), "phone" to phoneTextView.text.toString())
            ).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this, "Se guardaron los datos correctamente", Toast.LENGTH_SHORT).show()
                    addressTextView.setText("")
                    phoneTextView.setText("")
                }
            }
        }

        getButton.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                addressTextView.setText(it.get("address")as String?)
                phoneTextView.setText(it.get("phone")as String?)
            }
        }

        deleteButton.setOnClickListener {
            db.collection("users").document(email).delete()
        }

    }


}
