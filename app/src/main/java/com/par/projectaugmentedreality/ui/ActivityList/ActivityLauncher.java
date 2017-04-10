package com.par.projectaugmentedreality.ui.ActivityList;

/**
 * Created by Maick on 4/4/2017.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.par.projectaugmentedreality.R;


// This activity starts activities which demonstrate the Vuforia features
public class ActivityLauncher extends ListActivity
{

    private String mActivities[] = { "Image Targets", "VideoPlayback"};


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activities_list_text_view, mActivities);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activities_list);
        setListAdapter(adapter);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {

        Intent intent = new Intent(this, AboutScreen.class);
        intent.putExtra("ABOUT_TEXT_TITLE", mActivities[position]);

        switch (position)
        {
            case 0:
                intent.putExtra("ACTIVITY_TO_LAUNCH",
                        "ImageTargets");
                intent.putExtra("ABOUT_TEXT", "ImageTargets/IT_about.html");
                break;
            case 1:
                intent.putExtra("ACTIVITY_TO_LAUNCH",
                        "VideoPlayback.VideoPlayback");
                intent.putExtra("ABOUT_TEXT_TITLE", "Video Playback");
                intent.putExtra("ABOUT_TEXT", "VideoPlayback/VP_about.html");
                break;
        }

        startActivity(intent);

    }
}
