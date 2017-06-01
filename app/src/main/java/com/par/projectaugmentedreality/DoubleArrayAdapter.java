package com.par.projectaugmentedreality;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private LayoutInflater inflater;

    public DoubleArrayAdapter(Context context, ArrayList<String> text, ArrayList<String> result, ArrayList<String> question){
        Text = text;
        Result = result;
        Question = question;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentView = convertView;

        if(currentView == null){
            currentView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView textView = (TextView) currentView.findViewById(R.id.list_item_textview);
        textView.setText(Text.get(position));

        TextView resultView = (TextView) currentView.findViewById(R.id.list_item_textview_2);
        resultView.setText(Result.get(position));
        if(Result.get(position).equals("Goed")){
            resultView.setTextColor(Color.rgb(161, 207, 104));
        } else {
            resultView.setTextColor(Color.rgb(230,76,60));
        }

        TextView questionView = (TextView) currentView.findViewById(R.id.list_item_question);
        questionView.setText(Question.get(position));
        return currentView;
    }
}
