package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Permission;
import java.security.Permissions;
import java.util.Scanner;

public class Effects extends AppCompatActivity {

    ListView pos,neg,next;
    File pos_file,neg_file,next_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        pos = findViewById(R.id.pos_effects);
        neg = findViewById(R.id.neg_effects);
        next = findViewById(R.id.next_step);

        pos_file = createFile("positive");
        neg_file = createFile("negative");
        next_file = createFile("next");

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference positive_reference = storage.getReferenceFromUrl("gs://repair-brain-20.appspot.com/negative effects.txt");
        StorageReference negative_reference = storage.getReferenceFromUrl("gs://repair-brain-20.appspot.com/positive effects.txt");
        StorageReference steps_reference = storage.getReferenceFromUrl("gs://repair-brain-20.appspot.com/steps.txt");

        FileDownloadTask pos_task =  positive_reference.getFile(pos_file);
        pos_task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String text_data = readTxt(pos_file);
                Log.e("sanjay_t",text_data);

                pos.setAdapter(new ListAdapter(Effects.this,text_data));
            }
        });
        pos_task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("sanjay_t","Error");
            }
        });


        FileDownloadTask neg_task = negative_reference.getFile(neg_file);
        neg_task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String text_data = readTxt(neg_file);
                Log.e("sanjay_t",text_data);

                pos.setAdapter(new ListAdapter(Effects.this,text_data));
            }
        });
        neg_task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("sanjay_t","Error");
            }
        });


        FileDownloadTask next_task = steps_reference.getFile(next_file);
        next_task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String text_data = readTxt(pos_file);
                Log.e("sanjay_t",text_data);

                pos.setAdapter(new ListAdapter(Effects.this,text_data));
            }
        });
        next_task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("sanjay_t","Error");
            }
        });


    }

    public String readTxt(File file) {
        String data = "";
        Scanner scanner;

        try
        {
            scanner = new Scanner(file);
        }
        catch (FileNotFoundException e)
        {
            return data;
        }

        while(scanner.hasNextLine())
        {
            data+=scanner.nextLine()+"\n";
        }
        return data;
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
}