package de.thb.fbi.msr.maus.einkaufsliste;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import de.thb.fbi.msr.maus.einkaufsliste.R;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.room.RoomDataItemListAccessor;

public class LoginWidgetActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private TextView errorTextView;
    private boolean EmailValid = false;
    private boolean PasswordValid = false;
    private TextView passwordErrorTextView;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        errorTextView = findViewById(R.id.error);
        passwordErrorTextView = findViewById(R.id.passwordError);
        login.setEnabled(false);
        progressBar = findViewById(R.id.progressBar);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onEmailChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onPasswordChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        login.setOnClickListener(v -> {
            checkResults();
        });
    }

    // Other Methods
    void checkIfBothValid(){
        if(EmailValid && PasswordValid){
            login.setEnabled(true);
        } else {
            login.setEnabled(false);
        }
    }

    private boolean checkWrongPassword() {
        return "123456".equals(password.getText().toString()) && "test@test.de".equals(email.getText().toString());
    }

    private boolean isPasswordValid() {
        return password.getText() != null && password.getText().length() == 6;
    }
    private boolean isEmailValid() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches();
    }

    void onEmailChanged(){
        if (isEmailValid()){
            errorTextView.setVisibility(View.GONE);
            EmailValid = true;
        } else {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("Ungültige E-Mail-Adresse");
            EmailValid = false;
        }
        checkIfBothValid();
    }

    void onPasswordChanged(){
        if (isPasswordValid()) {
            PasswordValid = true;
        }
        else{
            PasswordValid = false;
        }
        checkIfBothValid();
    }

    void checkResults() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);

            if (checkWrongPassword()){
                passwordErrorTextView.setVisibility(View.VISIBLE);
                passwordErrorTextView.setText(R.string.wrong_password);
            }else{
                Intent intent = new Intent(LoginWidgetActivity.this, ItemListActivity.class);
                intent.putExtra(DataAccessActivity.ARG_ACCESSOR_CLASS, RoomDataItemListAccessor.class.getName());
                startActivity(intent);
            }
        }, 3000);
    }
}
