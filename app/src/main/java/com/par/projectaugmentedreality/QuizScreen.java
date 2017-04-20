package com.par.projectaugmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vuforia.DataSet;
import com.vuforia.Image;
import com.vuforia.ImageTarget;
import com.vuforia.ObjectTracker;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.StateUpdater;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;
import com.vuforia.TrackerManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
    ArrayList<String> correctAnswers;
    LinearLayout layout;
    String answers;
    String correctAnswerText;
    int x = 1;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

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

        setText();
    }

    public void setText(){
        StateUpdater tManager = TrackerManager.getInstance().getStateUpdater();
        State state =  tManager.getLatestState();

        int numTrackables = state.getNumTrackables();
        if(x <= numTrackables - 1){
            Trackable trackable = state.getTrackable(x);
            String name = trackable.getName();
            if(name.equals("quiz_icon")){
                x++;
            }
            else {
                StorageReference image = mStorageRef.child(name + ".jpg");
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(image)
                        .into(quizImage);
                mDatabase.child("Koude_Oorlog").child(name).child("quizAnswerA").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        answerOne.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mDatabase.child("Koude_Oorlog").child(name).child("quizAnswerB").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        answerTwo.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mDatabase.child("Koude_Oorlog").child(name).child("quizAnswerC").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        answerThree.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mDatabase.child("Koude_Oorlog").child(name).child("quizAnswerD").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        answerFour.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mDatabase.child("Koude_Oorlog").child(name).child("quizQuestion").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        question.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mDatabase.child("Koude_Oorlog").child(name).child("correctAnswer").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        correctAnswers.add(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } else {
                showAnswers();
        }
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
            RadioButton button = (RadioButton) findViewById(selected);
            answerList.add(button.getText().toString());
            x++;
            setText();
        }
    }

    public void showAnswers(){
        hideText();
        answers = "";
        correctAnswerText = "";
        for(int i = 0; i < answerList.size(); i++){
            answers += answerList.get(i);
            if(answerList.get(i).equals(correctAnswers.get(i))){
                correctAnswerText += correctAnswers.get(i);
            } else {
                correctAnswerText += correctAnswers.get(i);
            }
            answers += System.getProperty("line.separator") + System.getProperty("line.separator");
            correctAnswerText += System.getProperty("line.separator") + System.getProperty("line.separator");
        }
        Intent intent = new Intent(this, QuizAnswerScreen.class);
        intent.putExtra("answerList", answers);
        intent.putExtra("correctAnswers", correctAnswerText);
        startActivity(intent);
    }
}
