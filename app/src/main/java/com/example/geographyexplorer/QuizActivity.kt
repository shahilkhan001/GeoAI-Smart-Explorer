package com.example.geographyexplorer

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.geographyexplorer.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizBinding

    private var mCurrentPosition: Int = 1
    private lateinit var mQuestionsList: ArrayList<Question>
    private var mSelectedOptionPosition: Int = 0
    private var mCorrectAnswers: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mQuestionsList = Constants.getQuestions()

        setQuestion()

        // Set click listeners for the radio buttons and the submit button
        binding.rbOptionOne.setOnClickListener(this)
        binding.rbOptionTwo.setOnClickListener(this)
        binding.rbOptionThree.setOnClickListener(this)
        binding.rbOptionFour.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun setQuestion() {
        // Reset the UI for the new question
        defaultOptionsView()

        val question: Question = mQuestionsList[mCurrentPosition - 1]
        binding.progressBar.progress = mCurrentPosition
        binding.progressBar.max = mQuestionsList.size // Ensure max is correct
        binding.tvProgress.text = "$mCurrentPosition/${mQuestionsList.size}"
        binding.tvQuestion.text = question.questionText
        binding.rbOptionOne.text = question.optionOne
        binding.rbOptionTwo.text = question.optionTwo
        binding.rbOptionThree.text = question.optionThree
        binding.rbOptionFour.text = question.optionFour

        // Change button text based on position
        if (mCurrentPosition == mQuestionsList.size) {
            binding.btnSubmit.text = "FINISH"
        } else {
            binding.btnSubmit.text = "SUBMIT"
        }
    }

    // Resets the appearance of all radio buttons
    private fun defaultOptionsView() {
        val options = ArrayList<RadioButton>()
        options.add(0, binding.rbOptionOne)
        options.add(1, binding.rbOptionTwo)
        options.add(2, binding.rbOptionThree)
        options.add(3, binding.rbOptionFour)

        binding.radioGroup.clearCheck()
        for (option in options) {
            option.setTextColor(Color.parseColor("#000000"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, android.R.color.transparent)
            option.isEnabled = true // Re-enable buttons for the new question
        }
    }

    // Sets the background for correct/incorrect answers
    private fun answerView(answer: Int, drawableView: Int) {
        val radioButton = when (answer) {
            1 -> binding.rbOptionOne
            2 -> binding.rbOptionTwo
            3 -> binding.rbOptionThree
            4 -> binding.rbOptionFour
            else -> null
        }
        radioButton?.background = ContextCompat.getDrawable(this, drawableView)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.rbOptionOne -> mSelectedOptionPosition = 1
            R.id.rbOptionTwo -> mSelectedOptionPosition = 2
            R.id.rbOptionThree -> mSelectedOptionPosition = 3
            R.id.rbOptionFour -> mSelectedOptionPosition = 4

            R.id.btnSubmit -> {
                if (mSelectedOptionPosition == 0) {
                    // This means the user is trying to move to the next question
                    mCurrentPosition++
                    if (mCurrentPosition <= mQuestionsList.size) {
                        setQuestion()
                    } else {
                        // All questions are done, show the result
                        //Toast.makeText(this, "Quiz Finished! Your score is $mCorrectAnswers/${mQuestionsList.size}", Toast.LENGTH_LONG).show()
                        // We will navigate to a result screen later
                        val intent = Intent(this, ResultActivity::class.java)
                        intent.putExtra("CORRECT_ANSWERS", mCorrectAnswers)
                        intent.putExtra("TOTAL_QUESTIONS", mQuestionsList.size)
                        startActivity(intent)
                        finish() // Close the quiz activity
                    }
                } else {
                    // This means the user is submitting an answer
                    val question = mQuestionsList[mCurrentPosition - 1]

                    // If the selected answer is wrong
                    if (question.correctAnswer != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border)
                    } else {
                        // The answer is correct
                        mCorrectAnswers++
                    }
                    // Always show the correct answer in green
                    answerView(question.correctAnswer, R.drawable.correct_option_border)

                    // Disable all options after submission
                    binding.rbOptionOne.isEnabled = false
                    binding.rbOptionTwo.isEnabled = false
                    binding.rbOptionThree.isEnabled = false
                    binding.rbOptionFour.isEnabled = false

                    // Set button text for the next action
                    if (mCurrentPosition == mQuestionsList.size) {
                        binding.btnSubmit.text = "FINISH"
                    } else {
                        binding.btnSubmit.text = "GO TO NEXT QUESTION"
                    }
                    // Reset selected position for the next question
                    mSelectedOptionPosition = 0
                }
            }
        }
    }
}