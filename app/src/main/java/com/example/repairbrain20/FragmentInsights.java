package com.example.repairbrain20;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class FragmentInsights extends Fragment {

    ImageView no_results;
    ListView list;
    DatabaseReference common_reference = FirebaseDatabase.getInstance().getReference();
    Map<String, Insight> insights;
    View view;
    Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        this.activity = getActivity();
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        AdapterListInsights.remove = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        no_results = view.findViewById(R.id.no_results);
        list = view.findViewById(R.id.list);

        Glide.with(this).load(R.drawable.noresultfound).into(no_results);

        DatabaseReference reference = User.getMainReference();
        if (reference != null) {
            reference
                    .child("insights")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            insights = snapshot.getValue(new GenericTypeIndicator<Map<String, Insight>>() {
                                @NonNull
                                @Override
                                public String toString() {
                                    return super.toString();
                                }
                            });

                            AdapterListInsights adapter;

                            if (AdapterListInsights.remove)
                                adapter = new AdapterListInsights(FragmentInsights.this.activity, FragmentInsights.this.view, insights, true);
                            else
                                adapter = new AdapterListInsights(FragmentInsights.this.activity, FragmentInsights.this.view, insights);

                            list.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        DatabaseReference reference = User.getMainReference();

        switch (item.getItemId()) {
            case R.id.add:

                View view = FragmentInsights.this.activity.getLayoutInflater().inflate(R.layout.custom_add_insights, null);

                EditText name = view.findViewById(R.id.name);
                AutoCompleteTextView source = view.findViewById(R.id.source_name);
                EditText link = view.findViewById(R.id.link);

                source.setThreshold(0);

                source.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        source.showDropDown();
                    }
                });

                if (common_reference != null) {
                    common_reference
                            .child("common_steps_sources")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<String> keys = task.getResult().getValue(new GenericTypeIndicator<List<String>>() {
                                            @NonNull
                                            @Override
                                            public String toString() {
                                                return super.toString();
                                            }
                                        });

                                        if (keys != null) {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(FragmentInsights.this.activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, keys);
                                            source.setAdapter(adapter);
                                        }
                                    }
                                }
                            });
                }

                new AlertDialog.Builder(FragmentInsights.this.activity)
                        .setView(view)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String name_ = name.getText().toString();
                                String source_ = source.getText().toString();
                                String link_ = link.getText().toString();

                                if (name_.equals("")) {
                                    Toast.makeText(FragmentInsights.this.activity, "Insight cannot be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (!source_.equals("") && link_.equals("")) {
                                    Toast.makeText(FragmentInsights.this.activity, "Please paste the link", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (!link_.equals("") && !FragmentSteps.isValidLink(link_)) {
                                    Toast.makeText(FragmentInsights.this.activity, "Invalid source link", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (!Data.isValidKey(name_)) {
                                    Toast.makeText(FragmentInsights.this.activity, "Invalid Insight name", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (reference != null) {

                                    Snackbar snack = Snackbar.make(FragmentInsights.this.view, "Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                    snack.show();

                                    reference
                                            .child("insights")
                                            .child(name_)
                                            .setValue(new Insight(source_, link_))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    snack.dismiss();
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
                break;

            case R.id.delete:
                if (insights == null || insights.isEmpty()) {
                    Toast.makeText(FragmentInsights.this.activity, "Insights list is empty", Toast.LENGTH_SHORT).show();
                } else {
                    list.setAdapter(new AdapterListInsights(FragmentInsights.this.activity, FragmentInsights.this.view, insights, true));
                }
                break;

            case R.id.reset:
                if (reference != null) {
                    reference.child("insights")
                            .removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(FragmentInsights.this.activity, "Successfully resetted", Toast.LENGTH_LONG).show();
                                }
                            });
                }
                break;

            case R.id.settings:
                startActivity(new Intent(FragmentInsights.this.activity, ActSettings.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_insights, menu);
    }
}




































