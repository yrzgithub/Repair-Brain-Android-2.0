package com.example.repairbrain20;

import static com.example.repairbrain20.R.*;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Map;

public class FragmentTriggers extends Fragment {

    ListView list;
    ImageView loading;

    public FragmentTriggers() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(layout.fragment_triggers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        list = view.findViewById(id.list);
        loading = view.findViewById(id.loading);

        Glide.with(getActivity()).load(drawable.loading_pink_list).into(loading);

        DatabaseReference reference = User.getAddictionReference();

        if(reference!=null)
        {
            reference.child("triggers")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Map<String,Trigger> map =  task.getResult().getValue(new GenericTypeIndicator<Map<String, Trigger>>() {
                                @NonNull
                                @Override
                                public String toString() {
                                    return super.toString();
                                }
                            });

                            if(map==null || map.size()==0)
                            {
                                no_results();
                            }
                            else
                            {
                                list.setAdapter(new AdapterTriggers(getActivity(),map));
                            }
                        }
                    });
        }
        else
        {
            no_results();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    public void no_results()
    {

       // Toast.makeText(getActivity(),"No results",Toast.LENGTH_SHORT).show();

        //list.setVisibility(View.GONE);
        //loading.setVisibility(View.VISIBLE);
        //loading.setBackgroundResource(R.drawable.noresultfound);

        Glide.with(getActivity()).load(R.drawable.noresultfound).into(loading);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}