package com.par.projectaugmentedreality;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
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

import java.io.InputStream;
import java.lang.reflect.Field;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class QuizScreen extends Activity {
    TextView question;
    RadioButton answerOne;
    RadioButton answerTwo;
    RadioButton answerThree;
    RadioButton answerFour;
    Button nextQuestion;
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

        }
    }



   // }

    public void nextQuestion(View v){
        x++;
        setText();
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
