package com.example.repairbrain20;

import static com.example.repairbrain20.ActCreateAccount.title;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User implements OnCompleteListener<AuthResult> {

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static String email_regex = "^[a-zA-Z0-9+.-_]+@[a-zA-Z0-9.-]+$";
    static String uid;
    static String selected_addiction;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String firstname, lastname, username, password, email, full_name;
    Activity act;
    ProgressDialog progress;
    AlertDialog.Builder alert;
    DatabaseReference ids_reference = database.getReference().child("ids");
    boolean use_username;
    SharedPreferences.Editor editor;
    SharedPreferences preference;

    User() {

    }

    User(Activity act, String email_or_username, String password) {
        this.act = act;
        this.password = password;

        this.progress = new ProgressDialog(act);
        progress.setCanceledOnTouchOutside(false);
        progress.setOnCancelListener(null);
        progress.setIcon(R.drawable.icon_app);

        preference = act.getSharedPreferences("login_data", Context.MODE_PRIVATE);
        this.editor = preference.edit();

        selected_addiction = preference.getString("addiction", null);

        alert = new AlertDialog.Builder(act)
                .setTitle(title)
                .setIcon(R.drawable.icon_app)
                .setPositiveButton("OK", null)
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        act.finishAffinity();
                    }
                });

        if (email_or_username.matches(email_regex)) {
            this.email = email_or_username;
            this.use_username = false;
        } else {
            this.username = email_or_username;
            this.use_username = true;
        }
    }

    User(Activity act, FirebaseUser user) {
        this(act, user.getEmail(), null);
    }

    User(Activity act, String firstname, String lastname, String username, String password, String email) {

        this(act, email, password);

        this.firstname = firstname;
        this.lastname = lastname;
        this.full_name = firstname + " " + lastname;
        this.username = username;
    }

    public static String getEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) return user.getEmail();
        else return null;
    }

    public static String getUid() {
        return User.uid;
    }

    public static DatabaseReference getRepairReference() {
        if (User.uid != null && selected_addiction != null) {
            return database.getReference().child(User.uid).child("repairs").child(selected_addiction);
        }
        return null;
    }

    public static void setAddiction(Activity act, String addiction) {
        User.selected_addiction = addiction;
        act.getSharedPreferences("login_data", Context.MODE_PRIVATE).edit().putString("addiction", addiction).apply();
    }

    public static DatabaseReference getMainReference() {
        if (User.uid != null) {
            Log.e("userid", User.uid);
            return database.getReference().child(User.uid).child("repairs");
        }
        return null;
    }

    public static DatabaseReference getInsightsReference()
    {
        if (User.uid != null) {
            return database.getReference().child(User.uid).child("insights");
        }
        return null;
    }

    public static DatabaseReference getPlaylistReference()
    {
        if (User.uid != null) {
            return database.getReference().child(User.uid).child("playlist");
        }
        return null;
    }

    public boolean use_username() {
        return use_username;
    }

    public void login_with_credentials(AuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    }
                });
    }

    public void login_with_username() {

        String username = this.username;

        if (email == null) {
            show_progress("Connecting");
            Log.e("sanjay_username", username);

            if (!Data.isValidKey(username)) {
                Toast.makeText(act, "Invalid username", Toast.LENGTH_SHORT).show();
                return;
            }

            ids_reference
                    .child(username)
                    .get().
                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            send_alert("Connection failed");
                            progress.dismiss();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                Log.e("snapshot", snapshot.getValue().toString());
                                User.this.email = snapshot.getValue(String.class);
                                Log.e("sanjay_email", User.this.email);

                                login_with_email_and_password();
                            } else {
                                User.this.email = null;
                                progress.dismiss();
                                send_alert("Username not found");
                                editor.clear().commit();
                            }
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            send_alert("Connection failed");
                            progress.dismiss();
                        }
                    });
        } else {
            send_alert("Invalid Username or Email");
        }
    }

    public void create_user() {
        show_progress("Creating Account");

        Task<AuthResult> results = auth.createUserWithEmailAndPassword(this.email, this.password);

        results.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                User.this.send_verification_email();

                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {
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
                Log.e("sanjay", error);

                send_alert(error);
            }
        });

        results.addOnCompleteListener(this);
    }

    public void login_with_email_and_password() {

        String password = this.password;

        if (this.email == null) {
            Log.e("sanjay", "Cannot login");
            return;
        }

        Task<AuthResult> result = auth.signInWithEmailAndPassword(this.email, password);
        show_progress("Logging In");

        result.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = authResult.getUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        if (use_username) editor.putString("username", username);

                        if (!preference.contains("email") || !preference.contains("username")) {
                            Toast.makeText(act, "Login details saved", Toast.LENGTH_SHORT).show();
                        }

                        editor.putString("email", email).putString("password", password).commit();

                        User.uid = user.getUid();
                        Intent intent = new Intent(act, ActRepairsInsights.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        act.startActivity(intent);
                    } else {
                        alert.setPositiveButton("Ok", null);
                        alert.setNegativeButton("Verify", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                User.this.send_verification_email();
                            }
                        });
                        send_alert("Email not verified");
                    }
                }
            }
        });

        result.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Sanjay", e.toString());
                String message = e.getMessage();
                send_alert(message);
            }
        });

        result.addOnCompleteListener(this);
    }

    public void show_progress(String message) {
        progress.setMessage(message);

        if (!progress.isShowing()) {
            progress.show();
        }
    }

    public void send_alert(String msg) {
        if (progress.isShowing()) progress.dismiss();

        alert.setMessage(msg);
        alert.show();
    }

    public void send_verification_email() {
        if (!progress.isShowing()) {
            progress.show();
        }

        progress.setMessage("Sending Email verification link");

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            Task<Void> task = user.sendEmailVerification();

            task.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        ids_reference
                                .child(User.this.username)
                                .setValue(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        send_alert("Email verification link sent");
                                    }
                                });
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            send_alert(e.getMessage());
                        } else {
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
