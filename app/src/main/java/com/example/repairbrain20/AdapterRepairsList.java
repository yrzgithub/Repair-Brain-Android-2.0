package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterRepairsList extends BaseAdapter {

    Map<String, Repairs> addictions = new HashMap<>();
    List<String> keys = new ArrayList<>();
    Activity act;
    static boolean delete = false;
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
            delete = false;
        }

        snack = Snackbar.make(list,"Reload the list",BaseTransientBottomBar.LENGTH_INDEFINITE);
        snack.setAction("Reload", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.recreate();
            }
        });
    }

    AdapterRepairsList(Activity act,Map<String, Repairs> addictions, boolean delete)
    {
        this(act,addictions);
        AdapterRepairsList.delete = delete;
        if(delete && snack!=null && this.addictions.size()>0)
        {
            snack.show();
        }

        if(this.addictions.size()==0)
        {
            AdapterRepairsList.delete = false;
        }

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

        String key = keys.get(i).trim().toLowerCase();
        String title =  key.substring(0,1).toUpperCase() + key.substring(1);

        Repairs addiction = this.addictions.get(key);
        String date_added = addiction.getDate_added();

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(act, title +" Selected",Toast.LENGTH_SHORT).show();
                User.setAddiction(act, title);
                act.startActivity(new Intent(act, ActHome.class));
            }
        });

        DatabaseReference reference = User.getMainReference();

        if(reference!=null)
        {
            reference
                    .child(key)
                    .child("note")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                String link = task.getResult().getValue(String.class);
                                if(link==null)
                                {
                                    delete_or_go.setImageResource(R.drawable.note);
                                }
                            }
                        }
                    });
        }

        if(delete)
        {
            delete_or_go.setImageResource(R.drawable.delete_icon);
            main.setEnabled(false);
        }

        delete_or_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(delete)
                {
                    if(reference!=null)
                    {
                        reference
                                .child(key)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                }

                else
                {
                    if(reference!=null)
                    {
                        reference
                                .child(key)
                                .child("note")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                                        if(task.isSuccessful())
                                        {
                                            String link = task.getResult().getValue(String.class);

                                            if(link==null)
                                            {

                                                View view_ = act.getLayoutInflater().inflate(R.layout.alert_note,null);
                                                EditText link_ = view_.findViewById(R.id.effects_list);

                                                new AlertDialog.Builder(act)
                                                        .setView(view_)
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                String link = link_.getText().toString();

                                                                if(!FragmentSteps.isValidLink(link))
                                                                {
                                                                    Toast.makeText(act,"Invalid link",Toast.LENGTH_LONG).show();
                                                                    return;
                                                                }

                                                                reference.child(key).child("note").setValue(link);
                                                            }
                                                        })
                                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                            }
                                                        })
                                                        .create()
                                                        .show();
                                            }
                                            else
                                            {
                                                try
                                                {
                                                    if(!link.startsWith("http"))
                                                    {
                                                        link = "https://" + link;
                                                    }

                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(link));
                                                    act.startActivity(intent);
                                                }
                                                catch (Exception | Error e)
                                                {
                                                    Toast.makeText(act,"Invalid note link",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }

            }
        });

        addiction_name_view.setText(title);
        date_added_view.setText(date_added);

        return view;
    }
}
