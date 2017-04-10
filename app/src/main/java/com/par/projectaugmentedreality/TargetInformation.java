package com.par.projectaugmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.par.projectaugmentedreality.R;

public class TargetInformation extends Activity {
    TextView TargetInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_information);

        TargetInfoText = (TextView) findViewById(R.id.TargetInfoText);
        Intent intent = getIntent();
        String name = intent.getStringExtra("Dataset");

        TargetInfoText.setText(name);

    }
}
