package com.example.repairbrain20;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.StatusBarManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

public class CreateAccountAct extends AppCompatActivity {

    EditText firstname_view,lastname_view,email_view,password_view,verify_password_view,username_view;
    Button sign_up_button,login_with_google_button;
    String first_name,last_name,email,password,verify_password,username;
    ProgressDialog progress;
    static final String title = "Repair Brain";
    LinearLayout main;
    CheckNetwork network_check;
    ConnectivityManager cm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        main = findViewById(R.id.main);
        Glide.with(this)
                .asGif()
                .load(R.drawable.signup)
                .into(new SimpleTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(GifDrawable resource, Transition<? super GifDrawable> transition) {
                        main.setBackground(resource);
                        resource.start();
                    }
                });

        TextView title_view = findViewById(R.id.sign_in_to_repair_brain);
        title_view.setSelected(true);

        firstname_view = findViewById(R.id.first_name);
        lastname_view = findViewById(R.id.last_name);
        username_view = findViewById(R.id.username);
        email_view = findViewById(R.id.email);
        password_view = findViewById(R.id.password);
        verify_password_view = findViewById(R.id.verify_password);

        sign_up_button = findViewById(R.id.sign_up);
        login_with_google_button = findViewById(R.id.login_to_repair_brain);

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first_name = firstname_view.getText().toString().trim();
                last_name = lastname_view.getText().toString().trim();
                username = username_view.getText().toString().trim();
                email = email_view.getText().toString().trim();
                password = password_view.getText().toString().trim();
                verify_password = verify_password_view.getText().toString().trim();

                if(!isValidString(first_name))
                {
                    Toast.makeText(CreateAccountAct.this,"Invalid First Name",Toast.LENGTH_LONG).show();
                }

                else if(!isValidString(last_name))
                {
                    Toast.makeText(CreateAccountAct.this,"Invalid Last Name",Toast.LENGTH_LONG).show();
                }

                else if(!isValidString(username))
                {
                    Toast.makeText(CreateAccountAct.this,"Invalid Username",Toast.LENGTH_LONG).show();
                }

                else if(!isValidString(email))
                {
                    Toast.makeText(CreateAccountAct.this,"Invalid E-Mail",Toast.LENGTH_LONG).show();
                }
                else if(!isValidString(password))
                {
                    Toast.makeText(CreateAccountAct.this,"Invalid Password",Toast.LENGTH_LONG).show();
                }
                else if(password.length()<6)
                {
                    Toast.makeText(CreateAccountAct.this,"Password is too short",Toast.LENGTH_LONG).show();
                }
                else if(!isValidString(verify_password))
                {
                    Toast.makeText(CreateAccountAct.this,"Invalid verification password",Toast.LENGTH_LONG).show();
                }
                else  if(!password.equals(verify_password))
                {
                    Toast.makeText(CreateAccountAct.this,"Passwords doesn't match",Toast.LENGTH_LONG).show();
                }
                else if(!email.matches(User.email_regex))
                {
                    Toast.makeText(CreateAccountAct.this,"Invalid Email",Toast.LENGTH_LONG).show();
                }
                else {
                   // User user = new User(CreateAccountAct.this,first_name,last_name,email,password);

                    User user = new User(CreateAccountAct.this,first_name,last_name,username,password,email);
                    user.create_user();
                }
            }
        });

        login_with_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccountAct.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static boolean isValidString(String string)
    {
        return !string.equals("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}