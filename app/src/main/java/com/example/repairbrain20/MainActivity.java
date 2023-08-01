package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    ImageButton free_button = null,hand_cuffed_button = null;
    FirebaseDatabase database = null;
    DatabaseReference reference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        free_button = findViewById(R.id.free_image);
        hand_cuffed_button = findViewById(R.id.hand_cuffed_image);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_anim);

        free_button.startAnimation(animation);
        hand_cuffed_button.startAnimation(animation);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("data");

        Intent intent = new Intent(MainActivity.this,HabitsAndAccuracy.class);

        free_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("free",true);
                startActivity(intent);
            }
        });

        hand_cuffed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDateTime date_time = LocalDateTime.now();

               DatabaseReference lastly_relapsed_data = reference.child("lastly_relapsed");

               lastly_relapsed_data.child("year").setValue(date_time.getYear());
               lastly_relapsed_data.child("month").setValue(date_time.getMonthValue());
               lastly_relapsed_data.child("day").setValue(date_time.getDayOfMonth());
               lastly_relapsed_data.child("hour").setValue(date_time.getHour());
               lastly_relapsed_data.child("minute").setValue(date_time.getMinute());
               lastly_relapsed_data.child("second").setValue(date_time.getSecond());

                intent.putExtra("free",false);
                startActivity(intent);
            }
        });
    }
}