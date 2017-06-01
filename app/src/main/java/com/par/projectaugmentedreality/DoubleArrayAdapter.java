package com.par.projectaugmentedreality;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Maickel on 6/1/2017.
 */

public class DoubleArrayAdapter extends BaseAdapter {
    private ArrayList<String> Text;
    private ArrayList<String> Result;
    private ArrayList<String> Question;
    private  ArrayList<String> CorrectAnswers;
    private LayoutInflater inflater;
    private Context Context;

    public DoubleArrayAdapter(Context context, ArrayList<String> text, ArrayList<String> result, ArrayList<String> question, ArrayList<String> correctAnswers){
        Text = text;
        Result = result;
        Context = context;
        Question = question;
        CorrectAnswers = correctAnswers;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return Text.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View currentView = convertView;

        if(currentView == null){
            currentView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView textView = (TextView) currentView.findViewById(R.id.list_item_textview);
        textView.setText(Text.get(position));


        TextView questionView = (TextView) currentView.findViewById(R.id.list_item_question);
        questionView.setText(Question.get(position));

        TextView resultView = (TextView) currentView.findViewById(R.id.list_item_textview_2);
        resultView.setText(Result.get(position));
        if(Result.get(position).equals("Goed")){
            resultView.setTextColor(Color.rgb(161, 207, 104));
        } else {
            resultView.setTextColor(Color.rgb(230,76,60));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(Context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.correct_answer_popup);
                    TextView popupText = (TextView) dialog.findViewById(R.id.answer_popup_text);
                    popupText.setText(CorrectAnswers.get(position));
                    dialog.show();
                }
            });

            questionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(Context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.correct_answer_popup);
                    TextView popupText = (TextView) dialog.findViewById(R.id.answer_popup_text);
                    popupText.setText(CorrectAnswers.get(position));
                    dialog.show();
                }
            });
        }

        return currentView;
    }
}
