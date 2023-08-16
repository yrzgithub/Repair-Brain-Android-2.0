package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Map;

public class ActCommonPosNegNext extends AppCompatActivity {

    ListView list;
    ImageView loading;
    ConnectivityManager cm;
    CheckNetwork check;
    String type;
    DatabaseReference common_reference = FirebaseDatabase.getInstance().getReference();

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
        type = intent.getStringExtra("effect");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("common_" + type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        loading.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);

                        Map<String, Common> common = task.getResult().getValue(new GenericTypeIndicator<Map<String, Common>>() {
                            @NonNull
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        });

                        AdapterCommonPosNegNext adapter = new AdapterCommonPosNegNext(ActCommonPosNegNext.this, common);
                        ActCommonPosNegNext.this.list.setAdapter(adapter);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.common_effects_steps_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        View view = LayoutInflater.from(this).inflate(R.layout.suggest_alert_dialog,null);

        EditText name = view.findViewById(R.id.name);
        EditText source = view.findViewById(R.id.source);
        EditText link = view.findViewById(R.id.link);

        switch (item.getItemId())
        {
            case R.id.suggest:
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name_ = name.getText().toString();
                                String source_ = source.getText().toString();
                                String link_ = link.getText().toString();

                                if(!isValid(name_))
                                {
                                    Toast.makeText(ActCommonPosNegNext.this,"Name cannot be empty",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(!isValid(source_))
                                {
                                    Toast.makeText(ActCommonPosNegNext.this,"Source cannot be empty",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(!isValid(link_))
                                {
                                    Toast.makeText(ActCommonPosNegNext.this,"Link cannot be empty",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Common common = new Common(source_,link_);

                                if(common_reference!=null)
                                {
                                    Snackbar bar = Snackbar.make(list,"Saving", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                    bar.show();

                                    common_reference.child(type+"_suggestions")
                                            .child(name_)
                                            .setValue(common)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    bar.dismiss();
                                                    Toast.makeText(ActCommonPosNegNext.this,"Suggestion saved",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    public boolean isValid(String str)
    {
        return !str.trim().equals("");
    }
}