package com.par.projectaugmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class QuizAnswerScreen extends Activity {
    TextView answerText;
    TextView correctAnswerHeader;
    TextView givenAnswerHeader;
    TextView correctAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_answer_screen);

        answerText = (TextView) findViewById(R.id.answer_text);
        correctAnswer = (TextView) findViewById(R.id.correct_answer);
        givenAnswerHeader = (TextView) findViewById(R.id.given_answer_header);
        correctAnswerHeader = (TextView) findViewById(R.id.correct_answer_header);

        correctAnswerHeader.setText(getString(R.string.correct_answer_header));
        givenAnswerHeader.setText(getString(R.string.given_answer_header));

        Intent intent = getIntent();

        String answers = intent.getStringExtra("answerList");
        String correctAnswers = intent.getStringExtra("correctAnswers");

        correctAnswer.setText(correctAnswers);
        answerText.setText(answers);
    }
}
