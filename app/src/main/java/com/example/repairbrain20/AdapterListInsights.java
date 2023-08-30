package com.example.repairbrain20;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdapterListInsights extends BaseAdapter {

    Map<String,Insight> insights = new HashMap<>();
    Activity activity;
    View view;
    List<String> keys = new ArrayList<>();
    StorageReference icons = FirebaseStorage.getInstance().getReference().child("icons/");
    Map<String,Uri> uris = new HashMap<>();

    AdapterListInsights(Activity act,View view_, Map<String,Insight> map)
    {
        this.activity = act;
        this.view = view_;

        ListView list = view.findViewById(R.id.list);
        ImageView no_results = view.findViewById(R.id.no_results);

        if(map!=null)
        {
            this.insights.putAll(map);
            this.keys.addAll(map.keySet());
        }

        if(map==null || insights.size()==0)
        {
            list.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
            Glide.with(view).load(R.drawable.noresultfound).into(no_results);
        }
        else
        {
            list.setVisibility(View.VISIBLE);
            no_results.setVisibility(View.GONE);
        }
    }

    @Override
    public int getCount() {
        return insights.size();
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = activity.getLayoutInflater().inflate(R.layout.custom_insights,null);

        ImageView source_image = view.findViewById(R.id.image);
        ImageView go = view.findViewById(R.id.go);

        TextView insight = view.findViewById(R.id.insight);
        TextView source = view.findViewById(R.id.source);

        String key_insight = keys.get(i);

        Insight insight_ = insights.get(key_insight);

        String source_name = insight_.getSource();
        String link = insight_.getLink();

        if(uris.containsKey(source_name))
        {
            Glide.with(view).load(uris.get(source_name)).into(source_image);
        }
        else
        {
            if(icons!=null)
            {
                icons.
                        child(source_name.toLowerCase()+".png")
                        .getDownloadUrl()
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful())
                                {
                                    Uri uri =  task.getResult();
                                    if(!uris.containsKey(source_name))
                                    {
                                        uris.put(source_name,uri);
                                        Glide.with(AdapterListInsights.this.view).load(uri).into(source_image);
                                    }
                                }
                            }
                        });
            }
        }

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FragmentSteps.isValidLink(link))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    activity.startActivity(intent);
                }
                else
                {
                    Toast.makeText(activity,"Invalid Link",Toast.LENGTH_SHORT).show();
                }
            }
        });

        insight.setText(key_insight);
        source.setText(source_name);

        return view;
    }
}
