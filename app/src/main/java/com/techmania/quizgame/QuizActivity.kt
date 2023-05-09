package com.techmania.quizgame

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techmania.quizgame.databinding.ActivityQuizBinding
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding: ActivityQuizBinding

    lateinit var timer : CountDownTimer
    private val totalTime = 25000L
    var timeContinue = false
    var timeLeft = totalTime

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("question")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount = 0
    var questionNumber = 0
    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreRef = database.reference

    val questions = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        do {
            val number = Random.nextInt(1,11)
            questions.add(number)
        }while (questions.size < 5)

        gameLogic()

        quizBinding.buttonFinish.setOnClickListener {

            sendScore()

        }
        quizBinding.buttonNext.setOnClickListener {

            timeReset()
            gameLogic()

        }
        quizBinding.textViewA.setOnClickListener {

            pauseTimer()

            userAnswer = "a"
            if (userAnswer == correctAnswer){

                quizBinding.textViewA.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()

            }else{

                quizBinding.textViewA.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()

        }
        quizBinding.textViewB.setOnClickListener {

            pauseTimer()
            userAnswer = "b"
            if (correctAnswer == userAnswer){

                quizBinding.textViewB.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()

            }else{

                quizBinding.textViewB.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()


        }
        quizBinding.textViewC.setOnClickListener {

            pauseTimer()
            userAnswer = "c"
            if (correctAnswer == userAnswer){

                quizBinding.textViewC.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()

            }else{

                quizBinding.textViewC.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()
        }
        quizBinding.textViewD.setOnClickListener {

            pauseTimer()
            userAnswer = "d"
            if (correctAnswer == userAnswer){

                quizBinding.textViewD.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()

            }else{

                quizBinding.textViewD.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }

            disableClickableOfOptions()
        }

    }

    private fun gameLogic(){

        restoreOptions()

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                questionCount = snapshot.childrenCount.toInt()

                if (questionNumber < questions.size){

                    question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    answerA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    answerB = snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    answerC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    answerD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    correctAnswer = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewA.text = answerA
                    quizBinding.textViewB.text = answerB
                    quizBinding.textViewC.text = answerC
                    quizBinding.textViewD.text = answerD

                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestion.visibility = View.VISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE

                    startTimer()

                }else{

                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz game")
                    dialogMessage.setMessage("Congratulations!!!\n You answered all the questions. Do you wont to see the result?")
                    dialogMessage.setCancelable(false)
                    dialogMessage.setPositiveButton("See Result"){dialogWindow,postion ->
                        sendScore()
                    }
                    dialogMessage.setNegativeButton("Play Again"){dialogWindow, postion ->
                        val intent = Intent(this@QuizActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialogMessage.create().show()


                }

                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun findAnswer(){
        when(correctAnswer){

            "a" -> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d" -> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }

    fun disableClickableOfOptions(){

        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false
    }

    fun restoreOptions(){

        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true
    }

    private fun startTimer(){

        timer = object : CountDownTimer(timeLeft,1000){
            override fun onTick(p0: Long) {
                timeLeft = p0
                updateCountDownText()
            }

            override fun onFinish() {

                userWrong++
                disableClickableOfOptions()
                findAnswer()
                timeReset()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Sorry, Time is up!"
                timeContinue = false


            }

        }.start()

        timeContinue = true
    }

    fun updateCountDownText(){

        val timeRemaining : Int = (timeLeft/1000).toInt()

        quizBinding.textViewTime.text = timeRemaining.toString()

    }
    fun pauseTimer(){

        timer.cancel()
        timeContinue = false

    }

    fun timeReset(){

        pauseTimer()
        timeLeft = totalTime
        updateCountDownText()

    }

    fun sendScore(){

        user?.let {
            val userUID = user.uid
            scoreRef.child("score").child(userUID).child("correct").setValue(userCorrect)
            scoreRef.child("score").child(userUID).child("wrong").setValue(userWrong)

            val intent = Intent(this@QuizActivity, ResultActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}