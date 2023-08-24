package com.example.repairbrain20;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class FragmentSteps extends Fragment {

    ListView list;
    ImageView no_results;
    View view;
    DatabaseReference reference;
    Map<String,Step> map;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        StepsAdapter.delete = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.view = view;

        list = view.findViewById(R.id.list);
        no_results = view.findViewById(R.id.no_results);

        Glide.with(no_results).load(R.drawable.noresultfound).into(no_results);

        reference = User.getRepairReference();

        if(reference!=null)
        {
            reference.child("next_steps").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    map = snapshot.getValue(new GenericTypeIndicator<Map<String, Step>>() {
                        @NonNull
                        @Override
                        public String toString() {
                            return super.toString();
                        }
                    });

                    if(StepsAdapter.delete) list.setAdapter(new StepsAdapter(getActivity(),FragmentSteps.this.view,map,true));
                    else list.setAdapter(new StepsAdapter(getActivity(),FragmentSteps.this.view,map));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            });
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_effects, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.add:
                View view_ = getLayoutInflater().inflate(R.layout.custom_adapter_steps,null);

                EditText step_name = view_.findViewById(R.id.name);
                EditText link = view_.findViewById(R.id.link);
                EditText source_name = view_.findViewById(R.id.source_name);

                DatabaseReference main_reference = User.getMainReference();

                if(main_reference!=null)
                {
                    main_reference
                            .child("common_next_steps")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    Map<String,Step> map = task.getResult().getValue(new GenericTypeIndicator<Map<String, Step>>() {
                                        @NonNull
                                        @Override
                                        public String toString() {
                                            return super.toString();
                                        }
                                    });

                                }
                            });
                }

                DatabaseReference reference = User.getRepairReference();

                new AlertDialog.Builder(getActivity())
                        .setView(view_)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String step = step_name.getText().toString().trim();
                                String link_  = link.getText().toString().trim();
                                String source_name_ = source_name.getText().toString().trim();

                                if(step.equals(""))
                                {
                                    Toast.makeText(getActivity(),"Step cannot be empty",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(!source_name_.equals("") && link_.equals(""))
                                {
                                    Toast.makeText(getActivity(),"Please paste the link",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Snackbar snack = Snackbar.make(FragmentSteps.this.view,"Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);

                                if(reference!=null)
                                {
                                    snack.show();

                                    reference
                                            .child("next_steps")
                                            .child(step)
                                            .setValue(new Step(source_name_,link_))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    snack.dismiss();
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("cancel",null)
                        .create().show();

                break;

            case R.id.add_common:

                ArrayList<String> present;
                if(map==null) present = new ArrayList<>();
                else present = new ArrayList<>(map.keySet());

                intent = new Intent(getActivity(), ActCommon.class);
                intent.putExtra("common","common_next_steps");
                intent.putExtra("add",true);
                intent.putExtra("present",present);

                startActivity(intent);
                break;

            case R.id.common:
                intent = new Intent(getActivity(), ActCommon.class);
                intent.putExtra("common","common_next_steps");
                intent.putExtra("add",false);
                startActivity(intent);
                break;

            case R.id.remove:

                if(map==null || map.size()==0)
                {
                    Toast.makeText(getActivity(),"Steps list is empty",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    list.setAdapter(new StepsAdapter(getActivity(),FragmentSteps.this.view,map,true));
                }
                break;

            case R.id.reset:

                break;
        }
        return true;
    }

    public static boolean isValidLink(String link)
    {
        return Patterns.WEB_URL.matcher(link).matches();
    }

}