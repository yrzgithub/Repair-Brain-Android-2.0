package com.example.repairbrain20;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Notes {

    StorageReference reference = FirebaseStorage.getInstance().getReference();
    String file_name,repair_name;

    Notes(String repair_name)
    {
        file_name = "repair_name"+".txt";
        this.repair_name = repair_name;
    }
}
