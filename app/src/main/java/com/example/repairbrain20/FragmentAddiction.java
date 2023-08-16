package com.example.repairbrain20;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.database.GenericTypeIndicator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentAddiction extends Fragment {

    SharedPreferences preference;
    SharedPreferences.Editor editor;
    ListView list;
    ImageView no_results;

    public FragmentAddiction() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        preference = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        editor = preference.edit();

        list = view.findViewById(R.id.list);
        no_results = view.findViewById(R.id.no_results);

        Glide.with(this)
                .load(R.drawable.loading_pink_list)
                .into(no_results);

        DatabaseReference reference = User.getMainReference();

        if(reference==null) return;

        reference
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Map<String,Addiction> addictions =  task.getResult().getValue(new GenericTypeIndicator<Map<String, Addiction>>() {
                            @NonNull
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        });

                        if(addictions==null || addictions.size()==0)
                        {
                            no_results.setImageResource(R.drawable.noresultfound);
                        }
                        else
                        {
                            list.setAdapter(new AdapterAddictionsList(getActivity(),addictions));
                            no_results.setVisibility(View.GONE);
                            list.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addiction,null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add:
                View view = View.inflate(getActivity(),R.layout.alert_dialog,null);

                AutoCompleteTextView addiction_edit = view.findViewById(R.id.effects_list);
                addiction_edit.setHint("Search or Enter");
                addiction_edit.setThreshold(0);

                addiction_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addiction_edit.showDropDown();
                    }
                });

                List<String> suggestion = new ArrayList<>();
                suggestion.add("uruttu");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,suggestion);
                addiction_edit.setAdapter(adapter);

                new AlertDialog.Builder(getActivity())
                        .setView(view)
                        .setNegativeButton("cancel",null)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                DatabaseReference reference =  User.getMainReference();
                                if(reference!=null)
                                {
                                    Addiction addiction = new Addiction(LocalDateTime.now());
                                    String addiction_ = addiction_edit.getText().toString();

                                    Snackbar snack = Snackbar.make(getView(),"Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                    snack.show();

                                    reference
                                            .child(addiction_)
                                            .setValue(addiction)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        User.setAddiction(getActivity(),addiction_);
                                                        snack.dismiss();
                                                        Toast.makeText(getActivity(),"Addiction Added",Toast.LENGTH_SHORT).show();

                                                        AdapterAddictionsList.addiction_copy.remove(addiction_);

                                                        list.setAdapter(new AdapterAddictionsList(getActivity(),false));
                                                    }
                                                }
                                            });
                                }
                            }
                        })
                        .show();
                break;

            case R.id.remove:
                list.setAdapter(new AdapterAddictionsList(getActivity(),true));
                break;

            case R.id.reset:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.habits_frament_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}