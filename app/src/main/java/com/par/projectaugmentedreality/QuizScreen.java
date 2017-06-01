package com.par.projectaugmentedreality;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class QuizScreen extends Activity {
    TextView question;
    ImageView quizImage;
    RadioButton answerOne;
    RadioButton answerTwo;
    RadioButton answerThree;
    RadioButton answerFour;
    Button nextQuestion;
    RadioGroup quizRadiogroup;
    ArrayList<String> answerList;
    ArrayList<String> isQuizTargetArray;
    ArrayList<String> correctAnswers;
    ArrayList<String> correctAnswersResult;
    ArrayList<String> quizQuestions;
    String answers;
    String correctAnswerText;
    int size = 0;
    int x = 0;
    int answerListIndex = -1;
    int amountOfQuizTargets;
    int correctAnswerCount = 0;
    ArrayList<String> childNames;
    ArrayList<String> imageTargetNames;
    RadioButton button;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String isQuizTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        nextQuestion = (Button) findViewById(R.id.quiz_next_button);
        answerOne = (RadioButton) findViewById(R.id.answer_one);
        answerTwo = (RadioButton) findViewById(R.id.answer_two);
        answerThree = (RadioButton) findViewById(R.id.answer_three);
        answerFour = (RadioButton) findViewById(R.id.answer_four);
        question = (TextView) findViewById(R.id.quiz_question);
        quizRadiogroup = (RadioGroup) findViewById(R.id.quiz_radiogroup);
        quizImage = (ImageView) findViewById(R.id.quiz_image);
        answerList = new ArrayList<String>();
        correctAnswers = new ArrayList<String>();
        childNames = new ArrayList<String>();
        quizQuestions = new ArrayList<>();
        correctAnswersResult = new ArrayList<>();
        isQuizTargetArray = new ArrayList<String>();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Intent intent = getIntent();
        imageTargetNames = intent.getStringArrayListExtra("ImageTargets");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String name = postSnapshot.getKey();
                    size++;
                    childNames.add(name);
                    isQuizTargetArray.add(postSnapshot.child("isQuizTarget").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setText();
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("FirebaseAUTH", "Signed in anonymously");
                }
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setText(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(x <= size - 1){
                    String name = childNames.get(x);
                    isQuizTarget = isQuizTargetArray.get(x);

                    if(isQuizTarget.equals("false")){
                        StorageReference image = mStorageRef.child(name);
                        Glide.with(getApplication().getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(image)
                                .into(quizImage);
                        mDatabase.child(name).child("quizAnswerA").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                answerOne.setText(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child(name).child("quizAnswerB").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                answerTwo.setText(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child(name).child("quizAnswerC").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                answerThree.setText(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child(name).child("quizAnswerD").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                answerFour.setText(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child(name).child("quizQuestion").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                question.setText(dataSnapshot.getValue().toString());
                                quizQuestions.add(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child(name).child("correctAnswer").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                correctAnswers.add(dataSnapshot.getValue().toString());
                                answerListIndex++;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        x++;
                        setText();
                    }
                }
                else {
                    showAnswers();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void hideText(){
        answerOne.setVisibility(View.INVISIBLE);
        answerTwo.setVisibility(View.INVISIBLE);
        answerThree.setVisibility(View.INVISIBLE);
        answerFour.setVisibility(View.INVISIBLE);
        question.setVisibility(View.INVISIBLE);
        nextQuestion.setVisibility(View.INVISIBLE);
    }

    public void nextQuestion(View v){
        if(quizRadiogroup.getCheckedRadioButtonId() == -1){
            return;
        }else {
            int selected = quizRadiogroup.getCheckedRadioButtonId();
            button = (RadioButton) findViewById(selected);
            answerList.add(button.getText().toString());
            if(answerList.get(answerListIndex).equals(correctAnswers.get(answerListIndex))){
                button.setTextColor(Color.rgb(161, 207, 104));
            }else {
                button.setTextColor(Color.rgb(230,76,60));
            }
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.setTextColor(Color.WHITE);
                    quizRadiogroup.clearCheck();
                    x++;
                    setText();
                }
            }, 1000);


        }
    }

    public void showAnswers(){
        hideText();
        answers = "";
        correctAnswerText = "";
        for(int j = 0; j < isQuizTargetArray.size(); j++){
            if(isQuizTargetArray.get(j).equals("true")){
                amountOfQuizTargets++;
            }
        }
        for(int i = 0; i < childNames.size() - amountOfQuizTargets; i++){
            answers += answerList.get(i);
            if(answerList.get(i).equals(correctAnswers.get(i))){
                correctAnswerCount++;
                correctAnswerText += correctAnswers.get(i);
                correctAnswersResult.add("Goed");
            } else {
                correctAnswerText += correctAnswers.get(i);
                correctAnswersResult.add("Fout");
            }
            answers += System.getProperty("line.separator") + System.getProperty("line.separator");
            correctAnswerText += System.getProperty("line.separator") + System.getProperty("line.separator");
        }
        Intent intent = new Intent(this, QuizAnswerScreen.class);
        intent.putExtra("correctAnswerCount", correctAnswerCount);
        intent.putExtra("answerList", answerList);
        intent.putExtra("quizAnswersLength", childNames.size() - amountOfQuizTargets);
        intent.putExtra("correctAnswers", correctAnswerText);
        intent.putExtra("correctAnswersResult", correctAnswersResult);
        intent.putExtra("quizQuestions", quizQuestions);
        startActivity(intent);
    }
}
