package com.par.projectaugmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.par.projectaugmentedreality.R;

import java.lang.reflect.Field;

public class TargetInformation extends Activity {
    TextView TargetInfoText;
    ImageView TargetInfoImage;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_information);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        TargetInfoText = (TextView) findViewById(R.id.TargetInfoText);
        TargetInfoImage = (ImageView) findViewById(R.id.TargetInfoImage);
        Intent intent = getIntent();
        String name = intent.getStringExtra("Dataset");

        StorageReference image = mStorageRef.child(name);

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(image)
                .into(TargetInfoImage);

                mDatabase.child(name).child("text").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String text = dataSnapshot.getValue().toString();
                        TargetInfoText.setText(text);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mAuth.signInAnonymously();
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
