package com.example.repairbrain20;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.PreferenceChangeEvent;

public class Notes {

    StorageReference reference = FirebaseStorage.getInstance().getReference();
    String file_name,repair_name;

    Notes(String repair_name)
    {
        file_name = "repair_name"+".txt";
        this.repair_name = repair_name;
    }
}
