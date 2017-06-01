package com.par.projectaugmentedreality;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class QuizAnswerScreen extends ListActivity {
    TextView answerText;
    TextView correctAnswerHeader;
    TextView givenAnswerHeader;
    TextView correctAnswer;
    TextView resultScore;
    ProgressBar resultProgressbar;
    ListView answerListView;
    int correctAnswerCounter;
    DoubleArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_answer_screen);

        Intent intent = getIntent();

        ArrayList<String> answers = intent.getStringArrayListExtra("answerList");
        ArrayList<String> correctAnswersResult = intent.getStringArrayListExtra("correctAnswersResult");
        ArrayList<String> quizQuestions = intent.getStringArrayListExtra("quizQuestions");
        String correctAnswers = intent.getStringExtra("correctAnswers");
        correctAnswerCounter = intent.getIntExtra("correctAnswerCount", 0);
        int answersSize = intent.getIntExtra("quizAnswersLength", 0);

        //answerText = (TextView) findViewById(R.id.answer_text);
        //correctAnswer = (TextView) findViewById(R.id.correct_answer);
        //givenAnswerHeader = (TextView) findViewById(R.id.given_answer_header);
        answerListView = (ListView) findViewById(android.R.id.list);
       // correctAnswerHeader = (TextView) findViewById(R.id.correct_answer_header);
        resultScore = (TextView) findViewById(R.id.resultText);
        arrayAdapter = new DoubleArrayAdapter(this, answers, correctAnswersResult, quizQuestions);
        setListAdapter(arrayAdapter);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //correctAnswerHeader.setText(getString(R.string.correct_answer_header));
        //givenAnswerHeader.setText(getString(R.string.given_answer_header));



        //correctAnswer.setText(correctAnswers);
        //answerText.setText(answers);

        String correctAnswerCountString = correctAnswerCounter + "/" + answersSize + " vragen goed";

        resultScore.setText(correctAnswerCountString);
        //resultProgressbar.setMax(answersSize);
       // resultProgressbar.setProgress(correctAnswerCounter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void backButton(View v){
        Intent intent = new Intent(this, CloudReco.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, CloudReco.class);
        startActivity(intent);
    }

}
