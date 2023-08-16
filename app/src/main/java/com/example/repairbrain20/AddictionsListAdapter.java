package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class AddictionsListAdapter extends BaseAdapter {

    Map<String,Addiction> addictions = new HashMap<>();
    List<String> keys = new ArrayList<>();
    Activity act;
    static Map<String,Addiction> addiction_copy = new HashMap<>();
    boolean delete = false;
    ListView list;

    AddictionsListAdapter(Activity act, Map<String,Addiction> addictions)
    {
        this.addictions.putAll(addictions);
        this.keys.addAll(addictions.keySet());
        this.act = act;

        this.list = act.findViewById(R.id.list);

        AddictionsListAdapter.addiction_copy = this.addictions;

    }

    AddictionsListAdapter(Activity act,boolean delete)
    {
        this(act,addiction_copy);

        this.addictions = addiction_copy;
        this.delete = delete;
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

        view = act.getLayoutInflater().inflate(R.layout.custom_addiction_list_view,null);
        view.setBackgroundResource(R.drawable.round_layout);

        RelativeLayout main = view.findViewById(R.id.main);

        TextView addiction_name_view = view.findViewById(R.id.addiction_name);
        TextView date_added_view = view.findViewById(R.id.date_added);

        ImageView delete_or_go = view.findViewById(R.id.delete_or_go);

        String addiction_name = keys.get(i);

        Addiction addiction = this.addictions.get(addiction_name);
        String date_added = addiction.getDate_added();

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
                        reference.child(addiction_name)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            snack.dismiss();
                                            Toast.makeText(act,"Addiction deleted",Toast.LENGTH_SHORT).show();
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
                    User.setAddiction(act,addiction_name);
                    Toast.makeText(act,addiction_name+" Selected",Toast.LENGTH_SHORT).show();
                    User.setAddiction(act,addiction_name);
                    act.startActivity(new Intent(act, TimeAndAccuracyAct.class));
                }
            });
        }

        addiction_name_view.setText(addiction_name);
        date_added_view.setText(date_added);

        return view;
    }
}
