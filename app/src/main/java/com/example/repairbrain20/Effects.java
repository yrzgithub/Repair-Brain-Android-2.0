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