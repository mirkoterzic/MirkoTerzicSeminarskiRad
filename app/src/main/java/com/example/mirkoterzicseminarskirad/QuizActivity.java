package com.example.mirkoterzicseminarskirad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    // Instance variables
    private RadioButton opt_a;
    private RadioButton opt_b;
    private RadioButton opt_c;
    private RadioButton opt_d;
    private TextView questionTextView;
    private TextView timer;
    private Button answer;
    private int currentQuestionIndex = 0;
    private int correctAnswers=0;
    private int secondsElapsed = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            secondsElapsed++;
            timer.setText(String.valueOf(secondsElapsed));
            timerHandler.postDelayed(this, 1000); // Schedule the Runnable to run again after 1 second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViews();
        setUpClickListener();
        displayNextQuestion();
        startTimer();

    }

    // Method to initialize views
    private void initializeViews() {
        setContentView(R.layout.activity_quiz);
        opt_a = findViewById(R.id.opt_a);
        opt_b = findViewById(R.id.opt_b);
        opt_c = findViewById(R.id.opt_c);
        opt_d = findViewById(R.id.opt_d);
        timer= findViewById(R.id.timer);
        questionTextView = findViewById(R.id.ques);
        answer = findViewById(R.id.answer);
    }

    // Method to set up click listener for answer button
    private void setUpClickListener() {
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerAndProceed();
            }
        });
    }

    // Method to check answer and proceed to the next question
    private void checkAnswerAndProceed() {
        QuizQuestion currentQuestion = getCurrentQuestion();
        String selectedOption = getSelectedOption();
        String correctAnswer = currentQuestion.getCorrectAnswer();
        if (selectedOption.equals(correctAnswer)) {
            showToast("Correct!");
            correctAnswers++;
        } else {
            showToast("Incorrect!");
        }
        moveToNextQuestion();
    }

    // Method to display the next question
    private void displayNextQuestion() {
        QuizQuestion[] questions = getQuestionsFromIntent();
        if ( currentQuestionIndex < questions.length) {
            QuizQuestion currentQuestion = questions[currentQuestionIndex];
            displayQuestion(currentQuestion);
        } else {
            stopTimer(); // Stop the timer when the activity is destroyed
            Intent result_intent = new Intent(this, ResultActivty.class);
            result_intent.putExtra("correctAnswers",correctAnswers);
            String playerName = getIntent().getStringExtra("playerName");
            result_intent.putExtra("playerName", playerName);
            result_intent.putExtra("time",secondsElapsed);

            startActivity(result_intent);
            // Finish the current activity to prevent the user from returning to it using the back button

            finish();
        }
    }

    // Method to display a toast message
    private void showToast(String message) {
        // Display the Toast
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();

        // Schedule a Runnable to cancel the Toast after a delay of 500 milliseconds
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel(); // Cancel the Toast
            }
        }, 500); // Delay in milliseconds
    }

    // Method to get the current question
    private QuizQuestion getCurrentQuestion() {
        QuizQuestion[] questions = getQuestionsFromIntent();
        if (questions != null && currentQuestionIndex < questions.length) {
            return questions[currentQuestionIndex];
        }
        return null;
    }

    // Method to get the selected option
    private String getSelectedOption() {
        if (opt_a.isChecked()) {
            return opt_a.getText().toString();
        } else if (opt_b.isChecked()) {
            return opt_b.getText().toString();
        } else if (opt_c.isChecked()) {
            return opt_c.getText().toString();
        } else if (opt_d.isChecked()) {
            return opt_d.getText().toString();
        }
        return "";
    }

    // Method to move to the next question
    private void moveToNextQuestion() {
        currentQuestionIndex++;
        displayNextQuestion();
    }

    // Method to display a question
    private void displayQuestion(QuizQuestion question) {
        questionTextView.setText(question.getQuestion());
        String[] options = question.getOptions();
        opt_a.setText(options[0]);
        opt_b.setText(options[1]);
        opt_c.setText(options[2]);
        opt_d.setText(options[3]);
    }

    // Method to retrieve questions from Intent
    private QuizQuestion[] getQuestionsFromIntent() {
        return (QuizQuestion[]) getIntent().getSerializableExtra("questions");
    }
    private void startTimer() {
        timerHandler.postDelayed(timerRunnable, 1000); // Start the timer by posting the Runnable
    }

    // Method to stop the timer
    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable); // Remove the scheduled Runnable to stop the timer
    }

}
