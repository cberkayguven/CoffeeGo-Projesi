package com.berkayguven.coffegooo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText editEmail,editSifre,editIsim;
    private String txtEmail,txtSifre,txtIsim;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private HashMap<String,Object>mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editEmail=(EditText) findViewById(R.id.kayit_ol_editEmail);
        editSifre=(EditText) findViewById(R.id.kayit_ol_editSifre);
        editIsim=(EditText)findViewById(R.id.kayit_ol_editIsim);

        mAuth=FirebaseAuth.getInstance();
        mReference= FirebaseDatabase.getInstance().getReference();
    }

    public void kayitOl(View v){
        txtIsim=editIsim.getText().toString();
        txtEmail=editEmail.getText().toString();
        txtSifre=editSifre.getText().toString();

        if (!TextUtils.isEmpty(txtIsim)&&!TextUtils.isEmpty(txtEmail)&& !TextUtils.isEmpty(txtSifre)){
            mAuth.createUserWithEmailAndPassword(txtEmail,txtSifre)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mUser=mAuth.getCurrentUser();
                            mData=new HashMap<>();
                            mData.put("kullaniciAdı",txtIsim);
                            mData.put("kullaniciEmail",txtEmail);
                            mData.put("kullaniciSifre",txtSifre);
                            mData.put("kullaniciId",mUser.getUid());

                            mReference.child("Kullanıcılar").child(mUser.getUid())
                                    .setValue(mData)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                            Toast.makeText(MainActivity.this,"Kayıt İşlemi Başarılı",Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            }
                            else
                                Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });



        }else
            Toast.makeText(this,"Email ve Şifre Boş Olamaz.",Toast.LENGTH_SHORT).show();

    }
}
