package com.example.otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
     Button GetOTP;
     EditText Number;
          FirebaseAuth auth;
     String verificationCode;
     Button signin;
     EditText otp;
    Context context;
     PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GetOTP  = (Button) findViewById(R.id.verify);
        signin  = (Button) findViewById(R.id.signin);
        context =this;
        Number = (EditText) findViewById(R.id.Number);
        auth = FirebaseAuth.getInstance();
        otp  =(EditText) findViewById(R.id.otp);
        ImplementFirebaseLogin();
        GetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckNumber();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpp = otp.getText().toString();
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode,otpp);
                checkOtp(phoneAuthCredential);
            }
        });
    }

    private void checkOtp(PhoneAuthCredential phoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful()){
                                  Toast.makeText(context,"Verified",Toast.LENGTH_SHORT).show();
                              }
                              else{
                                  Toast.makeText(context,"OTP VERIFICATION FAILED",Toast.LENGTH_SHORT).show();
                              }
                    }
                });
    }

    private void ImplementFirebaseLogin() {

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
          Toast.makeText(context,"Verification Completed",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(context,"Verification Failed",Toast.LENGTH_LONG).show();
                Log.e("Ver",e.toString());

            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(context,"Code sent",Toast.LENGTH_SHORT).show();
                otp.setVisibility(View.VISIBLE);
            }
        };
    }

    public void CheckNumber(){
        String text =Number.getText().toString();
        if(text.length()!=10){
            Toast.makeText(this,"Invalid Phone ",Toast.LENGTH_LONG).show();
        }
        else{
            PhoneAuthOptions details= PhoneAuthOptions.newBuilder(auth)
                                       .setPhoneNumber("+91"+text)
                                        .setTimeout(45L, TimeUnit.SECONDS)
                                       .setActivity(this)
                                       .setCallbacks(callbacks).build();
            PhoneAuthProvider.verifyPhoneNumber(details);
        }
    }
}