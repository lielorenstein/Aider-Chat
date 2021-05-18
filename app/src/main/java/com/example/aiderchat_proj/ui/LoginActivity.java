package com.example.aiderchat_proj.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.aiderchat_proj.R;
import com.example.aiderchat_proj.databinding.ActivityLoginBinding;
import com.example.aiderchat_proj.ui.RegisterActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ActivityLoginBinding mBinding;
    private SharedPreferences sharedPreferences;
    final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mBinding.setLoginViewModel(loginViewModel);
        Animation moveDown =  AnimationUtils.loadAnimation(LoginActivity.this, R.anim.move_down);
        Animation moveLeft =  AnimationUtils.loadAnimation(LoginActivity.this, R.anim.move_left);
        Animation moveRight =  AnimationUtils.loadAnimation(LoginActivity.this, R.anim.move_right);
        mBinding.backgroundLogin.startAnimation(moveDown);
        mBinding.cloud1.startAnimation(moveRight);
        mBinding.cloud2.startAnimation(moveLeft);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        loadPreferences();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void firebaseAuthWithEmailAndPassword() {
        mAuth.signInWithEmailAndPassword(loginViewModel.form.getEmail(), loginViewModel.form.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "signIn: Success!");
                            // update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (!user.isEmailVerified()) {
                                Snackbar.make(mBinding.backgroundLogin, "Email validation is missing.", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                            savePreferences();
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            intent.putExtra("userEmail", user.getEmail());
//                            errorText.setText("");
//                            startActivity(intent);
                        } else {
                            Log.e(TAG, "signIn: Failed!", task.getException());
                            showSnackBar("Email or password are incorrect.");
                            //Snackbar.make(mBinding.backgroundLogin, "Email or password are incorrect.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void forgotPasswordOnClick(View view) {
        Intent intent = new Intent(this, ChannelActivity.class);
        startActivity(intent);
    }

    public void signUpOnClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                mBinding.IdClientEmail.setText(bundle.getString("EML"));
                mBinding.IdPassword.setText(bundle.getString("PSS"));
                savePreferences();
                showSnackBar("We sent you a verification via your email");
            }

        }
    }


    public void logInOnClick(View view) {
        firebaseAuthWithEmailAndPassword();
    }



    private void showSnackBar(String messageText) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), messageText, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity =  Gravity.CENTER_HORIZONTAL | Gravity.TOP;

    // calculate actionbar height
        TypedValue tv = new TypedValue();
        int actionBarHeight=0;
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        mBinding.btnEmailSignUp.getY();
    // set margin
        params.setMargins(0, actionBarHeight*3, 0, 0);

        view.setLayoutParams(params);
        snack.show();
    }



    private void savePreferences() {
        String emailData = loginViewModel.form.getEmail().trim();
        String passwordData = loginViewModel.form.getPassword().trim();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", emailData);
        editor.putString("Pss", passwordData);
        editor.apply();
    }

    private void loadPreferences() {
        loginViewModel.form.setEmail(sharedPreferences.getString("Email", ""));
        loginViewModel.form.setPassword(sharedPreferences.getString("Pss", ""));
        mBinding.notifyChange();
    }
}
