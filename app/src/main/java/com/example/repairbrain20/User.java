package com.example.repairbrain20;

import static com.example.repairbrain20.CreateAccountAct.title;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

public class User implements OnCompleteListener<AuthResult> {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    String firstname,lastname,username,password,email,full_name;
    Activity act;
    ProgressDialog progress;
    AlertDialog.Builder alert;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ids_reference = database.getReference().child("ids");

    User(Activity act,String email,String password)
    {
        this.act = act;
        this.email = email;
        this.password = password;

        getEmail();

        this.progress = new ProgressDialog(act);

        alert = new AlertDialog.Builder(act)
                .setTitle(title)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setPositiveButton("OK",null)
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        act.finishAffinity();
                    }
                });
    }

    User(Activity act,String firstname, String lastname, String username, String password, String email)
    {

        this(act,email,password);

        this.firstname = firstname;
        this.lastname = lastname;
        this.full_name = firstname + " " + lastname;
        this.username = username;
    }

    public void getEmail()
    {
        if(email==null)
        {
            Task<DataSnapshot> snapshot =  ids_reference.child(this.username).get();

            show_progress("Connecting");

            snapshot.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    User.this.email = dataSnapshot.getValue(String.class);
                }
            });

            snapshot.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.getMessage();
                    send_alert(message);
                }
            });

            snapshot.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    progress.dismiss();
                }
            });
        }
    }

    public void create_user()
    {
        show_progress("Creating Account");

        Task<AuthResult> results =  auth.createUserWithEmailAndPassword(this.email,this.password);

        results.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                send_verification_email();

                FirebaseUser user = auth.getCurrentUser();

                if(user!=null)
                {
                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(User.this.full_name)
                            .build();

                    user.updateProfile(request);
                }
            }
        });

        results.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Log.e("sanjay",error);

                send_alert(error);
            }
        });

        results.addOnCompleteListener(this);
    }

    public void login_with_email_and_password()
    {
        Task<AuthResult> result = auth.signInWithEmailAndPassword(this.email,this.password);

        show_progress("Logging In");

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
                String message = e.getMessage();
                send_alert(message);
            }
        });

        result.addOnCompleteListener(this);

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
        progress.setMessage("Sending Email verification link");
        progress.setCanceledOnTouchOutside(false);

        FirebaseUser user = auth.getCurrentUser();

        if(user!=null)
        {
            Task<Void> task =  user.sendEmailVerification();

            task.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {
                        send_alert("Email verification link sent");
                    }
                    else {
                        Exception e = task.getException();
                        if(e!=null)
                        {
                            send_alert(e.getMessage());
                        }
                        else
                        {
                            send_alert("Something went wrong");
                        }
                    }
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    send_alert(e.getMessage());
                }
            });
        }
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        progress.dismiss();
    }
}
