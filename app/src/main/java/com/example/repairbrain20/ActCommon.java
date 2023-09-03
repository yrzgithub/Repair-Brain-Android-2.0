package com.example.repairbrain20;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class ActCommon extends AppCompatActivity {

    ListView list;
    ImageView loading;
    ConnectivityManager cm;
    CheckNetwork check;
    String type;
    boolean add;
    DatabaseReference common_reference = FirebaseDatabase.getInstance().getReference();
    AdapterCommonPosNegNext adapter;
    Map<String, Common> common;
    ArrayList<String> present;

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
        type = intent.getStringExtra("common");
        add = intent.getBooleanExtra("add",false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        loading.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);

                        common = task.getResult().getValue(new GenericTypeIndicator<Map<String, Common>>() {
                            @NonNull
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        });

                        if(add)
                        {
                            present = (ArrayList<String>) intent.getSerializableExtra("present");
                            adapter = new AdapterCommonPosNegNext(ActCommon.this,common,present,type, true);
                        }
                        else
                        {
                            adapter = new AdapterCommonPosNegNext(ActCommon.this, common);
                        }

                        ActCommon.this.list.setAdapter(adapter);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.common_effects_steps_menu,menu);

        MenuItem search = menu.findItem(R.id.search);

        androidx.appcompat.widget.SearchView complete = (androidx.appcompat.widget.SearchView) search.getActionView();
        complete.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(common!=null)
                {
                   Map<String,Common> map = common.entrySet().stream().filter(entry->entry.getKey().toLowerCase().contains(newText.toLowerCase())).collect(Collectors.toMap(x->x.getKey(),x->x.getValue()));
                   if(add) list.setAdapter(new AdapterCommonPosNegNext(ActCommon.this,map,present,type,true));
                   else list.setAdapter(new AdapterCommonPosNegNext(ActCommon.this,map));
                }
                return true;
            }
        });

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
                                    Toast.makeText(ActCommon.this,"Name cannot be empty",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(!isValid(source_))
                                {
                                    Toast.makeText(ActCommon.this,"Source cannot be empty",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(!isValid(link_))
                                {
                                    Toast.makeText(ActCommon.this,"Link cannot be empty",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Common common = new Common(source_,link_); // common act

                                if(common_reference!=null)
                                {
                                    Snackbar bar = Snackbar.make(list,"Saving", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                    bar.show();

                                    common_reference.child("suggestions/"+type)
                                            .child(name_)
                                            .setValue(common)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    bar.dismiss();
                                                    Toast.makeText(ActCommon.this,"Suggestion saved",Toast.LENGTH_SHORT).show();
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

            case R.id.request:
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("requests").child(User.selected_addiction).child(type).setValue(User.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ActCommon.this,"Request submitted",Toast.LENGTH_SHORT).show();
                    }
                });
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