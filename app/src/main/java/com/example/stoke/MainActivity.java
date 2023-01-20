package com.example.stoke;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPairGenerator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {

    Button ingresar;
    public KeyPair kp;
    public KeyPairGenerator kpg;
    public PublicKey publicKey;
    public PrivateKey privateKey;
    public PrivateKey llavePrivada;
    public PublicKey llavePublica;
    public final static String CRYPTO_METHOD="RSA";
    private final static int CRYPTO_BITS = 1024;
    private Cipher cipher;
    private final static String OPCION_RSA= "RSA/ECB/OAEPWithSHA256AndMGF1Padding";
    private byte[] encrytedByte;
    private byte[] descryptedByte;
    private String descryptedString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText correo = (EditText) findViewById(R.id.Email);
        EditText pass = (EditText)findViewById(R.id.Password);
        ingresar=findViewById(R.id.ingresar);
        SharedPreferences preferences=getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if(preferences.getBoolean("llave3",false)==true){
            startActivity(new Intent(MainActivity.this,Etapa2.class));
            finish();
        }
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                Boolean bandera = true;
                SharedPreferences preferences=getSharedPreferences("sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= preferences.edit();
                editor.putString("llave1",correo.getText().toString());
                editor.putString("llave2",pass.getText().toString());
                editor.putBoolean("llave3",bandera);
                editor.commit();
                generateKeyPair();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(MainActivity.this,Etapa2.class));
                finish();
            }
        });
    }
    //metodo para generar el par de claves
    private void generateKeyPair() throws Exception {
        kpg = KeyPairGenerator.getInstance(CRYPTO_METHOD);
        kpg.initialize(CRYPTO_BITS);
        kp = kpg.generateKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
        String codedPrivateKey = clavePriDeco(privateKey);
        String codedPublicKey = clavePubliDeco(publicKey);
        try {
            //guardar clave publica
            OutputStreamWriter archivoPub = new OutputStreamWriter(openFileOutput("PublicKeyUser.txt", Activity.MODE_PRIVATE));
            archivoPub.write(codedPublicKey);
            archivoPub.flush();
            archivoPub.close();
            //guardar clave privada
            OutputStreamWriter archivoPri = new OutputStreamWriter(openFileOutput("PrivateKeyUser.txt", Activity.MODE_PRIVATE));
            archivoPri.write(codedPrivateKey);
            archivoPri.flush();
            archivoPri.close();
            Log.d("TAG1", "publickey -> " + codedPublicKey);
            Log.d("TAG1", "privatekey -> " + codedPrivateKey);

        } catch (IOException e) {
        }

    }

    public String clavePubliDeco(PublicKey publi) {
        byte[] data = publi.getEncoded();
        String codedPublicKey = Base64.encodeToString(data, Base64.DEFAULT);
        Log.d("TAG1", "publickey -> " + publi);
        return codedPublicKey;
    }

    public String clavePriDeco(PrivateKey priv) {
        byte[] data = priv.getEncoded();
        String codedPublicKey = Base64.encodeToString(data, Base64.DEFAULT);
        Log.d("TAG1", "privatekey -> " + priv);
        return codedPublicKey;
    }

}