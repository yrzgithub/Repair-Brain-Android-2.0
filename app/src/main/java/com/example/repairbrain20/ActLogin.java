package com.example.repairbrain20;

import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Calendar;

public class ActLogin extends AppCompatActivity implements View.OnClickListener {

    Button login_btn,google_btn;
    TextView topic,forget_password_text,create_account;
    EditText id_or_email_edit_txt,password_edit_txt;
    ProgressDialog progress;
    SharedPreferences preference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GoogleSignInOptions gso;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppSettings settings = new AppSettings(this);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().hide();
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        LinearLayout main = findViewById(R.id.main);
        Glide.with(this)
                .asGif()
                .load(R.drawable.signup)
                .into(new SimpleTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                        main.setBackground(resource);
                        resource.start();
                    }
                });

        topic = findViewById(R.id.topic);
        forget_password_text = findViewById(R.id.forget_password);
        create_account = findViewById(R.id.create_account);

        id_or_email_edit_txt = findViewById(R.id.id_or_email);
        password_edit_txt = findViewById(R.id.password);

        login_btn = findViewById(R.id.login);
        google_btn = findViewById(R.id.login_with_google);

        topic.setSelected(true);

        topic.setOnClickListener(this);
        forget_password_text.setOnClickListener(this);
        create_account.setOnClickListener(this);

        id_or_email_edit_txt.setOnClickListener(this);
        password_edit_txt.setOnClickListener(this);

        login_btn.setOnClickListener(this);
        google_btn.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,settings.getHour());
        calendar.set(Calendar.MINUTE,settings.getMinute());
        calendar.set(Calendar.SECOND,0);

        AlarmManager manager =  (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent alarm_intent = new Intent(this,AlarmReceiver.class);
        PendingIntent alarm_pending = PendingIntent.getBroadcast(this,100,alarm_intent,PendingIntent.FLAG_MUTABLE);

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES,alarm_pending);

        preference = getSharedPreferences("login_data",MODE_PRIVATE);

        String username = preference.getString("username",null);
        String email = preference.getString("email",null);
        String password = preference.getString("password",null);

        User user;

        if(username!=null)
        {
            id_or_email_edit_txt.setText(username);

            user = new User(this,username,password);
            if(settings.isAuto_login()) user.login_with_username();
        }

        else if(email!=null)
        {
            id_or_email_edit_txt.setText(email);

            user = new User(this,email,password);
            if(settings.isAuto_login()) user.login_with_email_and_password();
        }

        if(password!=null) password_edit_txt.setText(password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.sign_up:
                Intent signup_intent = new Intent(ActLogin.this, ActCreateAccount.class);
                startActivity(signup_intent);
                break;

            case R.id.topic:
                break;

            case R.id.forget_password:
                FirebaseAuth auth = FirebaseAuth.getInstance();

                View view_ = getLayoutInflater().inflate(R.layout.alert_dialog,null);

                EditText edit = view_.findViewById(R.id.effects_list);
                edit.setHint("Enter your E-mail");

                new AlertDialog.Builder(this)
                        .setView(view_)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String email_ = edit.getText().toString();
                                if(!email_.matches(User.email_regex))
                                {
                                    Toast.makeText(ActLogin.this,"Invalid email",Toast.LENGTH_LONG).show();
                                    return;
                                }

                                ProgressDialog progress = new ProgressDialog(ActLogin.this);
                                progress.setMessage("Sending Password Reset Link");
                                progress.setCanceledOnTouchOutside(false);
                                progress.setOnCancelListener(null);
                                progress.show();

                                auth.
                                    sendPasswordResetEmail(email_)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            progress.dismiss();

                                            if(task.isSuccessful())
                                            {
                                                progress.dismiss();
                                                Toast.makeText(ActLogin.this,"Reset Link Sent",Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(ActLogin.this,error,Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            }
                        })
                        .setNegativeButton("cancel",null)
                        .create()
                        .show();

                break;

            case R.id.create_account:
                startActivity(new Intent(this, ActCreateAccount.class));
                break;

            case R.id.login:
                String username_or_email = id_or_email_edit_txt.getText().toString();
                String password = password_edit_txt.getText().toString();

                if(!ActCreateAccount.isValidString(username_or_email))
                {
                    Toast.makeText(this,"Invalid Username",Toast.LENGTH_LONG).show();
                    break;
                }

                if(!ActCreateAccount.isValidString(password))
                {
                    Toast.makeText(this,"Invalid Password",Toast.LENGTH_LONG).show();
                    break;
                }

                User user = new User(this,username_or_email,password);
                if(user.use_username())
                {
                    user.login_with_username();
                }
                else
                {
                    user.login_with_email_and_password();
                }
                break;

            case R.id.login_with_google:
                gso = new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient client = GoogleSignIn.getClient(this,gso);
                client.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent google_sign_in_intent =  client.getSignInIntent();
                        startActivityForResult(google_sign_in_intent,100);
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==100)
        {
            progress = new ProgressDialog(this);
            progress.setCanceledOnTouchOutside(false);
            progress.setOnCancelListener(null);
            progress.setIcon(R.drawable.icon_app);
            progress.setMessage("Logging In");

            progress.show();

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(!task.isSuccessful())
            {
                login_failed();
                return;
            }

            try
            {
                GoogleSignInAccount account =  task.getResult(ApiException.class);
                String email =  account.getEmail();
                String idToken = account.getIdToken();

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = auth.getCurrentUser();
                                progress.dismiss();
                                if(user!=null)
                                {
                                    User.uid = user.getUid();
                                    startActivity(new Intent(ActLogin.this, ActRepairsInsights.class));
                                }
                                else
                                {
                                    login_failed();
                                }
                            }
                        });
            }
            catch (ApiException e)
            {
                login_failed();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void login_failed()
    {
        progress.dismiss();
        Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        // cm.registerDefaultNetworkCallback(network_check);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // cm.unregisterNetworkCallback(network_check);
        super.onPause();
    }
}