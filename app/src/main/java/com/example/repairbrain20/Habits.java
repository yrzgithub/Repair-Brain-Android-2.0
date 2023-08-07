package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class Habits extends AppCompatActivity {

    TextView percent;
    ListView list_view;
    ImageView img;
    static int last_accuracy_percent = 0;
    CheckNetwork network_check;
    ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits);

        LinearLayout main = findViewById(R.id.main);

        percent = findViewById(R.id.percent);
        list_view = findViewById(R.id.list);

        network_check =  new CheckNetwork(this,main);
        cm = (ConnectivityManager) getSystemService(ConnectivityManager.class);

     //   img.setImageResource(R.drawable.noresultfound);

        User.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData data = snapshot.getValue(UserData.class);
                Habits.last_accuracy_percent = data.getLast_accuracy_percent();
                percent.setText(Habits.last_accuracy_percent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onResume() {
        cm.registerDefaultNetworkCallback(network_check);
        super.onResume();
    }

    @Override
    protected void onPause() {
        cm.unregisterNetworkCallback(network_check);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static int lastly_accuracy()
    {
        return last_accuracy_percent;
    }
}