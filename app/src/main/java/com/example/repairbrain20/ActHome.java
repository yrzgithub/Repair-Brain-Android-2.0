package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.time.LocalDateTime;

public class ActHome extends AppCompatActivity {

    ImageButton free_button = null,hand_cuffed_button = null;
    CheckNetwork network_check;
    ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout main = findViewById(R.id.main);
       /* network_check =  new CheckNetwork(this,main);
        cm = (ConnectivityManager) getSystemService(ConnectivityManager.class); */

        free_button = findViewById(R.id.free_image);
        hand_cuffed_button = findViewById(R.id.hand_cuffed_image);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_anim);

        free_button.startAnimation(animation);
        hand_cuffed_button.startAnimation(animation);

        Intent intent = new Intent(ActHome.this, ActRecovery.class);

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

               // LocalDateTime date_time = LocalDateTime.now();

               /* DatabaseReference lastly_relapsed_data = reference.child("lastly_relapsed");

               lastly_relapsed_data.child("year").setValue(date_time.getYear());
               lastly_relapsed_data.child("month").setValue(date_time.getMonthValue());
               lastly_relapsed_data.child("day").setValue(date_time.getDayOfMonth());
               lastly_relapsed_data.child("hour").setValue(date_time.getHour());
               lastly_relapsed_data.child("minute").setValue(date_time.getMinute());
               lastly_relapsed_data.child("second").setValue(date_time.getSecond()); */

                User.getAddictionReference().child("lastly_relapsed").setValue(new Time(LocalDateTime.now()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ActHome.this,"Starting time rested",Toast.LENGTH_LONG).show();
                            }
                        });

                intent.putExtra("free",false);
                startActivity(intent);
            }
        });

      //  Toast.makeText(this,User.selected_addiction + " Selected",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        // cm.registerDefaultNetworkCallback(network_check);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // cm.unregisterNetworkCallback(network_check);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // getMenuInflater().inflate(R.menu.fragment_add_menu,menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}