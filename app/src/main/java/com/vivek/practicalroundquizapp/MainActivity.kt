package com.vivek.practicalroundquizapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.vivek.practicalroundquizapp.databinding.ActivityMainBinding
import com.vivek.practicalroundquizapp.model.ApiData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var apiInterface: ApiInterface
    lateinit var appDB: AppDB
    private lateinit var binding: ActivityMainBinding
    private var questionIndex = 0
    private var userAnswer = " "
    private var userAnswerList: ArrayList<String> = arrayListOf()
    private var quizData: List<QuizEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiInterface = Utils.retrofitBuilder().create(ApiInterface::class.java)
        appDB = Utils.dataBaseBuilder(this)

        if (Utils.checkForInternet(this)) {
            getApiData()
            Toast.makeText(this, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Internet Disconnected", Toast.LENGTH_SHORT).show()
            quizData = appDB.quizDao.getAllQuizData()
            setData()
        }

        binding.op1.setOnClickListener {
            userAnswer = binding.op1.text.toString()
            binding.uAnswer.text = userAnswer
        }
        binding.op2.setOnClickListener {
            userAnswer = binding.op2.text.toString()
            binding.uAnswer.text = userAnswer
        }
        binding.op3.setOnClickListener {
            userAnswer = binding.op3.text.toString()
            binding.uAnswer.text = userAnswer
        }
        binding.op4.setOnClickListener {
            userAnswer = binding.op4.text.toString()
            binding.uAnswer.text = userAnswer
        }

        binding.next.setOnClickListener {
            userAnswerList.add(userAnswer)
            binding.uAnswer.text = " "
            userAnswer = " "
            questionIndex++
            if (questionIndex < quizData.size) {
                setData()
            } else {
                var score = 0
                for (i in quizData.indices) {
                    if (quizData[i].correct_answer == userAnswerList[i]) {
                        score++
                    }
                }
                Toast.makeText(
                    this, " You Scored  $score OutOf ${quizData.size}", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getApiData() {
        val call = apiInterface.getApiData()
        call.enqueue(object : Callback<ApiData?> {
            override fun onResponse(p0: Call<ApiData?>, response: Response<ApiData?>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!.results
                    result.let {
                        appDB.quizDao.deleteAllQuizData()
                        result.forEach {
                            val option = it.incorrect_answers as ArrayList
                            val randomIndex = Random.nextInt(0, 3)
                            option.add(randomIndex, it.correct_answer)

                            appDB.quizDao.insertQuizData(
                                QuizEntity(
                                    type = it.type,
                                    difficulty = it.difficulty,
                                    category = it.category,
                                    question = it.question,
                                    correct_answer = it.correct_answer,
                                    incorrect_answers = option
                                )
                            )
                        }
                    }
                    quizData = appDB.quizDao.getAllQuizData()
                    setData()
                }
            }

            override fun onFailure(p0: Call<ApiData?>, p1: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch Data", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setData() {
        val data = quizData[questionIndex]
        binding.question.text = data.question
        binding.op1.text = data.incorrect_answers[0]
        binding.op2.text = data.incorrect_answers[1]
        binding.op3.text = data.incorrect_answers[2]
        binding.op4.text = data.incorrect_answers[3]

        if (questionIndex == quizData.size - 1) {
            binding.next.text = "Submit"
        }
    }
}