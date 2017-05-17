package com.par.projectaugmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class QuizAnswerScreen extends Activity {
    TextView answerText;
    TextView correctAnswerHeader;
    TextView givenAnswerHeader;
    TextView correctAnswer;
    TextView resultScore;
    ProgressBar resultProgressbar;
    int correctAnswerCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_answer_screen);

        answerText = (TextView) findViewById(R.id.answer_text);
        correctAnswer = (TextView) findViewById(R.id.correct_answer);
        givenAnswerHeader = (TextView) findViewById(R.id.given_answer_header);
        correctAnswerHeader = (TextView) findViewById(R.id.correct_answer_header);
        resultScore = (TextView) findViewById(R.id.resultText);
        resultProgressbar = (ProgressBar) findViewById(R.id.resultSpinner);

        correctAnswerHeader.setText(getString(R.string.correct_answer_header));
        givenAnswerHeader.setText(getString(R.string.given_answer_header));

        Intent intent = getIntent();

        String answers = intent.getStringExtra("answerList");
        String correctAnswers = intent.getStringExtra("correctAnswers");
        correctAnswerCounter = intent.getIntExtra("correctAnswerCount", 0);
        int answersSize = intent.getIntExtra("quizAnswersLength", 0);

        correctAnswer.setText(correctAnswers);
        answerText.setText(answers);

        String correctAnswerCountString = correctAnswerCounter + "/" + answersSize + " vragen goed";

        resultScore.setText(correctAnswerCountString);
        resultProgressbar.setMax(answersSize);
        resultProgressbar.setProgress(correctAnswerCounter);
    }
}
