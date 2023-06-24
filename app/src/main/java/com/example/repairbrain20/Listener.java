package com.example.repairbrain20;

import android.app.Activity;
import android.telephony.mbms.StreamingServiceInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Listener implements OnSuccessListener<FileDownloadTask.TaskSnapshot> ,OnFailureListener, OnProgressListener<FileDownloadTask.TaskSnapshot> {

    FileDownloadTask task;
    File file;
    String type;
    String text;
    ListView list;
    TextView topic;
    ImageView img;
    Activity act;

    Listener(Activity act, StorageReference reference, String type)
    {
        this.type = type;
        this.act = act;

        topic = act.findViewById(R.id.topic);
        list = act.findViewById(R.id.list);
        img = act.findViewById(R.id.no_results);

        topic.setText(type);

        file = createFile(type);
        task = reference.getFile(file);

        task.addOnSuccessListener(this);
        task.addOnFailureListener(this);
        task.addOnProgressListener(this);
    }

    @Override
    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
        text = readTxt(file);
        Log.e("sanjay_t",text);

        if(text==null)
        {
            show_image_view(R.drawable.noresultfound);
        }
        else
        {
            delete_image_view();
            effect(text);
        }
    }

    @Override
    public void onFailure(Exception e) {
        show_image_view(R.drawable.noresultfound);
    }

    @Override
    public void onProgress(FileDownloadTask.TaskSnapshot snapshot) {
        show_image_view(R.drawable.loading_pink_list);
    }

    public String readTxt(File file) {
        Scanner scanner;

        try
        {
            scanner = new Scanner(file);
        }
        catch (FileNotFoundException e)
        {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        while(scanner.hasNextLine())
        {
            builder.append(scanner.nextLine()).append("\n");
        }
        return builder.toString();
    }

    public FileReader getReader(File file)
    {
        try
        {
            return new FileReader(file);
        }
        catch (Exception e)
        {
            Log.e("sanjay_t",e.toString());
            return null;
        }
    }

    public File createFile(String name)
    {
        File file = null;

        try
        {
            file = File.createTempFile(name,"txt");
        }
        catch (IOException e)
        {
            Log.e("sanjay_t",e.toString());
        }

        return file;
    }

    public void effect(String text)
    {
        if(text!=null)
        {
            list.setAdapter(new ListAdapter(act,text));
        }
    }

    public void show_image_view(int id)
    {
        img.setVisibility(View.VISIBLE);
        img.setForegroundGravity(Gravity.CENTER);
        Glide.with(act).load(id).into(img);
        list.setVisibility(View.GONE);
    }

    public void delete_image_view()
    {
        img.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }
}
