package com.par.projectaugmentedreality;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vuforia.DataSet;
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
    TextView answerText;
    RadioButton answerOne;
    RadioButton answerTwo;
    RadioButton answerThree;
    RadioButton answerFour;
    Button nextQuestion;
    RadioGroup quizRadiogroup;
    ArrayList<String> answerList;
    LinearLayout layout;
    String answers;
    int x = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);

        nextQuestion = (Button) findViewById(R.id.quiz_next_button);
        answerOne = (RadioButton) findViewById(R.id.answer_one);
        answerTwo = (RadioButton) findViewById(R.id.answer_two);
        answerThree = (RadioButton) findViewById(R.id.answer_three);
        answerFour = (RadioButton) findViewById(R.id.answer_four);
        question = (TextView) findViewById(R.id.quiz_question);
        quizRadiogroup = (RadioGroup) findViewById(R.id.quiz_radiogroup);
        answerText = (TextView) findViewById(R.id.answer_text);
        answerList = new ArrayList<String>();

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
                String quizAnswerOne = name + "_answer_one";
                String quizAnswerTwo = name + "_answer_two";
                String quizAnswerThree = name + "_answer_three";
                String quizAnswerFour = name + "_answer_four";
                String quizQuestion = name + "_question";

                int quizAnswerOneID = getId(quizAnswerOne, R.string.class);
                int quizAnswerTwoID = getId(quizAnswerTwo, R.string.class);
                int quizAnswerThreeID = getId(quizAnswerThree, R.string.class);
                int quizAnswerFourID = getId(quizAnswerFour, R.string.class);
                int quizQuestionID = getId(quizQuestion, R.string.class);

                answerOne.setText(quizAnswerOneID);
                answerTwo.setText(quizAnswerTwoID);
                answerThree.setText(quizAnswerThreeID);
                answerFour.setText(quizAnswerFourID);
                question.setText(quizQuestionID);
            }
        } else {
            hideText();
            answers = "";
            for(int i = 0; i < answerList.size(); i++){
                answers += answerList.get(i) + System.getProperty("line.separator");
            }
            answerText.setText(answers);
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

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }
}
