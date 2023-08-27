package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ActRepairs extends AppCompatActivity {

    SharedPreferences preference;
    SharedPreferences.Editor editor;
    ListView list;
    ImageView no_results;
    Map<String, Repairs> addictions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_repairs);

        AdapterRepairsList.delete = false;

        preference = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        editor = preference.edit();

        list = findViewById(R.id.list);
        no_results = findViewById(R.id.no_results);

        Glide.with(this)
                .load(R.drawable.loading_pink_list)
                .into(no_results);

        DatabaseReference reference = User.getMainReference();

        if(reference==null)
        {
            Glide.with(ActRepairs.this).load(R.drawable.noresultfound).into(no_results);
            return;
        }

        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        addictions =  snapshot.getValue(new GenericTypeIndicator<Map<String, Repairs>>() {
                            @NonNull
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        });

                        if(AdapterRepairsList.delete) list.setAdapter(new AdapterRepairsList(ActRepairs.this,addictions,true));
                        else list.setAdapter(new AdapterRepairsList(ActRepairs.this,addictions));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add:
                View view = View.inflate(this,R.layout.alert_dialog,null);

                AutoCompleteTextView addiction_edit = view.findViewById(R.id.effects_list);
                addiction_edit.setHint("Search or Enter");
                addiction_edit.setThreshold(0);

                addiction_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        addiction_edit.showDropDown();
                    }
                });

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("common_addictions")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                Map<String,Common> map = task.getResult().getValue(new GenericTypeIndicator<Map<String, Common>>() {
                                    @NonNull
                                    @Override
                                    public String toString() {
                                        return super.toString();
                                    }
                                });

                                if(map!=null)
                                {
                                    List<String> list = new ArrayList<>(map.keySet());

                                    ArrayAdapter<String> common_list = new ArrayAdapter<>(ActRepairs.this,androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,list);
                                    addiction_edit.setAdapter(common_list);
                                }
                            }
                        });

                new AlertDialog.Builder(this)
                        .setView(view)
                        .setNegativeButton("cancel",null)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Repairs addiction = new Repairs(LocalDateTime.now());
                                String addiction_ = addiction_edit.getText().toString().trim().toLowerCase();

                                if(!Data.isValidKey(addiction_))
                                {
                                    Toast.makeText(ActRepairs.this,"Invalid Repair",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Snackbar snack = Snackbar.make(list,"Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                snack.show();

                                DatabaseReference main_reference = User.getMainReference();

                                if(main_reference!=null)
                                {
                                    main_reference
                                            .child(addiction_)
                                            .setValue(addiction)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    User.setAddiction(ActRepairs.this,addiction_);
                                                    snack.dismiss();
                                                }
                                            });
                                }
                            }
                        })
                        .show();
                break;

            case R.id.common:
                Intent intent = new Intent(ActRepairs.this,ActCommon.class);
                intent.putExtra("common","common_addictions");
                startActivity(intent);
                break;

            case R.id.remove:
                list.setAdapter(new AdapterRepairsList(ActRepairs.this,addictions,true));
                break;

            case R.id.reset:
                reference = User.getMainReference();
                Snackbar snack = Snackbar.make(list,"Resetting",BaseTransientBottomBar.LENGTH_INDEFINITE);
                snack.show();
                if(reference!=null)
                {
                    reference.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            snack.dismiss();
                            Toast.makeText(getApplicationContext(),"Successfully resetted",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_database_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }
}