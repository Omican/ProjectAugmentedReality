package com.par.projectaugmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.par.projectaugmentedreality.R;

import java.lang.reflect.Field;

public class TargetInformation extends Activity {
    TextView TargetInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_information);

        TargetInfoText = (TextView) findViewById(R.id.TargetInfoText);
        Intent intent = getIntent();
        String name = intent.getStringExtra("Dataset");

        int id = getId(name, R.string.class);

        TargetInfoText.setText(id);
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
