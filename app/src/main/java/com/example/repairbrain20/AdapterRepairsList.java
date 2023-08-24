package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterRepairsList extends BaseAdapter {

    Map<String, Repairs> addictions = new HashMap<>();
    List<String> keys = new ArrayList<>();
    Activity act;
    static Map<String, Repairs> addiction_copy = new HashMap<>();
    boolean delete = false;
    ListView list;
    ImageView no_results;
    Snackbar snack;

    AdapterRepairsList(Activity act, Map<String, Repairs> addictions)
    {
        this.act = act;

        this.list = act.findViewById(R.id.list);
        no_results = act.findViewById(R.id.no_results);

        if(addictions!=null && addictions.size()>0)
        {
            list.setVisibility(View.VISIBLE);
            no_results.setVisibility(View.GONE);
            this.addictions.putAll(addictions);
            this.keys.addAll(addictions.keySet());
        }
        else
        {
            list.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
            no_results.setImageResource(R.drawable.noresultfound);
        }

        snack = Snackbar.make(list,"Reload the list",BaseTransientBottomBar.LENGTH_INDEFINITE);
        snack.setAction("Reload", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.recreate();
            }
        });

        AdapterRepairsList.addiction_copy = this.addictions;
    }

    AdapterRepairsList(Activity act, boolean delete)
    {
        this(act,addiction_copy);
        this.delete = delete;
        if(delete && snack!=null && addiction_copy.size()>0) snack.show();

    }

    @Override
    public int getCount() {
        return addictions!=null?addictions.size():0;
    }

    @Override
    public Object getItem(int i) {
        return addictions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = act.getLayoutInflater().inflate(R.layout.custom_repair_list_view,null);
        view.setBackgroundResource(R.drawable.round_layout);

        RelativeLayout main = view.findViewById(R.id.main);

        TextView addiction_name_view = view.findViewById(R.id.addiction_name);
        TextView date_added_view = view.findViewById(R.id.date_added);

        ImageView delete_or_go = view.findViewById(R.id.delete_or_go);

        String key = keys.get(i);
        String title =  key.substring(0,1).toUpperCase() + key.substring(1).trim();

        Repairs addiction = this.addictions.get(title);
        String date_added = addiction.getDate_added().trim();

        if(this.delete)
        {
            delete_or_go.setImageResource(R.drawable.delete_icon);

            delete_or_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference reference = User.getMainReference();

                    Snackbar snack = Snackbar.make(list,"Removing", BaseTransientBottomBar.LENGTH_INDEFINITE);
                    snack.show();

                    if(reference!=null)
                    {
                        reference.child(title)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        snack.dismiss();

                                        AdapterRepairsList.addiction_copy.remove(title);
                                        list.setAdapter(new AdapterRepairsList(act,delete));

                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(act,"Repair deleted",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(act,"Something went wrong",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
            });
        }
        else
        {
            delete_or_go.setImageResource(R.drawable.go);
            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User.setAddiction(act, title);
                    Toast.makeText(act, title +" Selected",Toast.LENGTH_SHORT).show();
                    User.setAddiction(act, title);
                    act.startActivity(new Intent(act, ActHome.class));
                }
            });
        }

        addiction_name_view.setText(title);
        date_added_view.setText(date_added);

        return view;
    }
}
