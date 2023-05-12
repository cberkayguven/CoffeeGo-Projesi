package com.berkayguven.coffegooo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class GirisActivity extends AppCompatActivity {

    private EditText editEmail,editSifre,editGuncelIsim;
    private String txtEmail,txtSifre,txtGuncelIsim;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private HashMap<String,Object>mData;
    private Button hesapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        editEmail=(EditText) findViewById(R.id.giris_yap_editEmail);
        editSifre=(EditText) findViewById(R.id.giris_yap_editSifre);
        editGuncelIsim=(EditText)findViewById(R.id.giris_yap_editGuncelIsim);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        // mUser = mAuth.getCurrentUser();

        hesapBtn = findViewById(R.id.yeniHesap);
        hesapBtn.setOnClickListener(view -> startActivity(new Intent(this,MainActivity.class)));

    }

    public void GirisYap(View view){
        txtEmail =editEmail.getText().toString();
        txtSifre=editSifre.getText().toString();

        if (!TextUtils.isEmpty(txtEmail)&&!TextUtils.isEmpty(txtSifre)){
            mAuth.signInWithEmailAndPassword(txtEmail,txtSifre)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            mUser=mAuth.getCurrentUser();

                            assert mUser!=null;
                            verileriGetir(mUser.getUid());

                            Intent intent = new Intent(GirisActivity.this,MapsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GirisActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }else
            Toast.makeText(this,"Email ve Şifre Boş Olamaz.",Toast.LENGTH_SHORT).show();
    }

    private void verileriGetir(String uid){
        mReference= FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(uid);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snp: snapshot.getChildren()){
                    System.out.println(snp.getKey() + " = " +snp.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GirisActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void veriyiGüncelle(HashMap<String, Object>hashMap,final String uid){
        mReference= FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(uid);
        mReference.updateChildren(hashMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(GirisActivity.this,"Veri Başarıyla Güncellendi.",Toast.LENGTH_SHORT).show();
                            System.out.println("---- Güncellenen Veriler ----");
                            verileriGetir(uid);
                        }


                    }
                });


    }
    public void isimGuncelle(View v){
        txtGuncelIsim = editGuncelIsim.getText().toString();

        if(!TextUtils.isEmpty(txtGuncelIsim)){
            mData=new HashMap<>();
            mData.put("kullanıcıAdı",txtGuncelIsim);
            assert mUser!=null;
            veriyiGüncelle(mData,mUser.getUid());

        }else
            Toast.makeText(this,"Güncellenecek Değer Boş Olamaz.",Toast.LENGTH_SHORT).show();
    }

    public void datayiSil(View v){
        mReference = FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(mUser.getUid());
        mReference.removeValue()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(GirisActivity.this,"Data Başarıyla Silindi.",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(GirisActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
}