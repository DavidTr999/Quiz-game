package com.techmania.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.techmania.quizgame.databinding.ActivitySingupBinding

class SingupActivity : AppCompatActivity() {

    lateinit var singupBinding: ActivitySingupBinding

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        singupBinding = ActivitySingupBinding.inflate(layoutInflater)
        val view = singupBinding.root
        setContentView(view)

        singupBinding.buttonSignUp.setOnClickListener {

            val email = singupBinding.editTextSignupEmail.text.toString()
            val password = singupBinding.editTextSingUpPassword.text.toString()

            singUpWithFirebase(email, password)

        }
    }
    fun singUpWithFirebase(email : String, password : String){

        singupBinding.progressBarSingUp.visibility = View.VISIBLE
        singupBinding.buttonSignUp.isClickable = false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if (task.isSuccessful){
                Toast.makeText(applicationContext,"Your account has bean created",Toast.LENGTH_SHORT).show()
                finish()
                singupBinding.progressBarSingUp.visibility = View.INVISIBLE
                singupBinding.buttonSignUp.isClickable = true

            }else {
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }

    }
}

