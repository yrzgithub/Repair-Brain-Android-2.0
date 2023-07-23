package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button signup_btn,login_btn;
    TextView topic,forget_password_text,create_account;
    EditText id_or_email_edit_txt,password_edit_txt;
    LinearLayout google_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().hide();
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        topic = findViewById(R.id.topic);
        forget_password_text = findViewById(R.id.forget_password);
        create_account = findViewById(R.id.create_account);

        id_or_email_edit_txt = findViewById(R.id.id_or_email);
        password_edit_txt = findViewById(R.id.password);

        signup_btn = findViewById(R.id.sign_up);
        login_btn = findViewById(R.id.login);
        google_btn = findViewById(R.id.login_with_google);

        topic.setSelected(true);

        topic.setOnClickListener(this);
        forget_password_text.setOnClickListener(this);
        create_account.setOnClickListener(this);

        id_or_email_edit_txt.setOnClickListener(this);
        password_edit_txt.setOnClickListener(this);

        signup_btn.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        google_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.sign_up:
                Intent signup_intent = new Intent(LoginActivity.this,CreateAccountAct.class);
                startActivity(signup_intent);
                break;

            case R.id.topic:
                break;

            case R.id.forget_password:

                break;

            case R.id.create_account:
                startActivity(new Intent(this,CreateAccountAct.class));
                break;

            case R.id.login:
                String username_or_email = id_or_email_edit_txt.getText().toString();
                String password = password_edit_txt.getText().toString();

                if(!CreateAccountAct.isValidString(username_or_email))
                {
                    Toast.makeText(this,"Invalid Username",Toast.LENGTH_LONG).show();
                    break;
                }

                if(!CreateAccountAct.isValidString(password))
                {
                    Toast.makeText(this,"Invalid Password",Toast.LENGTH_LONG).show();
                    break;
                }

                User user = new User(this,username_or_email,password);
                user.login_with_email_and_password(password);
                break;

            case R.id.login_with_google:
                break;
        }
    }
}