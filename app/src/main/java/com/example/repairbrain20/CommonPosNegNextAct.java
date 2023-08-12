package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.List;
import java.util.Map;

public class CommonPosNegNextAct extends AppCompatActivity {

    ListView list;
    ImageView loading;
    ConnectivityManager cm;
    CheckNetwork check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_pos_neg_next);

        ConstraintLayout main = findViewById(R.id.main);
        list = findViewById(R.id.effects);
        loading = findViewById(R.id.loading);

        cm = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        check = new CheckNetwork(this,main);

        Glide.with(loading)
                .load(R.drawable.loading_pink_list)
                .into(loading);

        Intent intent = getIntent();
        String type = intent.getStringExtra("effect");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("common_"+type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        loading.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);

                        Map<String,Common> common = task.getResult().getValue(new GenericTypeIndicator<Map<String, Common>>() {
                            @NonNull
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        });

                        CommonPosNegNextAdapter adapter = new CommonPosNegNextAdapter(CommonPosNegNextAct.this,common);
                        CommonPosNegNextAct.this.list.setAdapter(adapter);
                    }
                });
    }

    @Override
    protected void onResume() {
        cm.registerDefaultNetworkCallback(check);
        super.onResume();
    }

    @Override
    protected void onPause() {
        cm.unregisterNetworkCallback(check);
        super.onPause();
    }
}