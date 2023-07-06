package com.example.repairbrain20;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String firstname,lastname,username,password,email;
    Activity act;
    ProgressDialog progress;
    AlertDialog.Builder alert;
    final String title = "Repair Brain";

    User(Activity act,String firstname, String lastname, String username, String password, String email)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.email = email;

        this.act = act;

        this.progress = new ProgressDialog(act);
        progress.setTitle("Repair Brain");

        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        alert = new AlertDialog.Builder(act)
                .setTitle(title)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setPositiveButton("OK",null);
    }

    User(String email,String password)
    {
        this.email = email;
        this.password = password;

        this.progress = new ProgressDialog(act);
        progress.setTitle(title);
    }

    public void create_user()
    {
        progress.setMessage("Creating Account");

        Task<AuthResult> results =  auth.createUserWithEmailAndPassword(this.email,this.password);

        results.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                send_verification_email();
                progress.dismiss();
                alert.setMessage("Email verification link sent");
                alert.show();
            }
        });

        results.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("sanjay",e.toString());
            }
        });
    }

    public void login_with_email_and_password()
    {
        Task<AuthResult> result = auth.signInWithEmailAndPassword(this.email,this.password);

        result.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = authResult.getUser();
                if(user!=null)
                {
                    if(user.isEmailVerified())
                    {
                        Intent intent = new Intent(act, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        act.startActivity(intent);
                    }
                    else
                    {
                        alert.setMessage("Email not verified");
                        alert.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                send_verification_email();
                            }
                        });
                    }
                }
            }
        });

        result.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Sanjay",e.toString());
            }
        });
    }

    public  void show_progress(String message)
    {
        progress.setMessage(message);

        if(!progress.isShowing())
        {
            progress.show();
        }
    }

    public  void send_alert(String msg)
    {
        alert.setMessage(msg);
        alert.show();
    }

    public void send_verification_email()
    {
        show_progress("Sending Email verification link");

        FirebaseUser user = auth.getCurrentUser();

        if(user!=null)
        {
            Task<Void> task =  user.sendEmailVerification();

            task.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}
