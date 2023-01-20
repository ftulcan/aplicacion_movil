package com.example.stoke;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Etapa2 extends AppCompatActivity {
    public Button btnIniciar;
    public Button btn;
    public String palabra;
    public String mn;
    ImageView imgQr;
    public Boolean hola;
    public Boolean ok;
    public TextView aviso;
    public String user;
    public String pass;
    public String publicKey;
    public String privKey;
    public String claveStokeCifrada="";
    private Cipher cipher;
    private final static String OPCION_RSA= "RSA/ECB/OAEPWithSHA256AndMGF1Padding";
    public PrivateKey llavepriv;
    public PublicKey llavepubli;
    private byte[] encrytedByte;
    private byte[] descryptedByte;
    private String descryptedString;
    public String texto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etapa2);
        cargarPreferencias();
        publicKey=lectura("PublicKeyUser.txt");
        privKey=lectura("PrivateKeyUser.txt");
        StringToKey(publicKey,privKey);

        btnIniciar=findViewById(R.id.inicio);
        btn=findViewById(R.id.siguiente);
        aviso=findViewById(R.id.textView2);
        //aviso.setText("usuario o contraseña incorrectos");

        hola=false;
        ok=false;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Etapa2.this,etapa3.class));
                finish();
            }
        });
        btnIniciar.setOnClickListener(v -> {
            scanCode1();
            Log.d("MainActivity", hola.toString()+"    hola fuera");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    if(hola){
                        hola=false;
                        scanCode1();
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //mn=lectura("mn.txt");
                            if(ok){
                                ok=false;
                                scanCode2();
                            }
                            }
                        },6000);
                    }
                    }
                },10000);
        });
    }

    private void scanCode1(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("volumen up to flash on");
        options.setBeepEnabled(false);
        options.setCameraId(1);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher1.launch(options);
    }
    private void scanCode2(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("volumen up to flash on");
        options.setBeepEnabled(true);
        options.setCameraId(1);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher1.launch(options);
    }

    public void escritura(String palabra, String nArchivo){
        try{
            OutputStreamWriter archivoStoke = new OutputStreamWriter(openFileOutput(nArchivo, Activity.MODE_PRIVATE));
            archivoStoke.write(palabra);
            archivoStoke.flush();
            archivoStoke.close();
        }catch (IOException e ){
        }
    }
    public String lectura(String nArchivo){
        String clavePublicaCompleta="";
        try{
            InputStreamReader archivoPublico = new InputStreamReader(openFileInput(nArchivo));
            BufferedReader br = new BufferedReader(archivoPublico);
            String lineaPublica = br.readLine();
            while(lineaPublica != null){
                clavePublicaCompleta=clavePublicaCompleta + lineaPublica;
                lineaPublica=br.readLine();
            }
            br.close();
            archivoPublico.close();
            Log.d("TAG1", "lectura -> " + clavePublicaCompleta);
            //variable= clavePublicaCompleta;
        }catch(IOException e){
        }return clavePublicaCompleta;
    }

    public ActivityResultLauncher<ScanOptions> barLauncher1 = registerForActivityResult(new ScanContract(),
            result -> {

                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(Etapa2.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(Etapa2.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mn=(String) result.getContents();
                    imgQr= findViewById(R.id.imgQr);
                    //**prueba**
                    if(mn.equals("hola")){
                        hola=true;
                        Log.d("MainActivity", hola.toString()+"    hola dentro");
                        imgQr= findViewById(R.id.imgQr);
                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(user+"-"+pass, BarcodeFormat.QR_CODE, 750, 750);
                            imgQr.setImageBitmap(bitmap);
                            aviso.setText("dijo hola");
                        }catch (WriterException e){
                            e.printStackTrace();
                        }
                        Toast.makeText(Etapa2.this, "dijo hola", Toast.LENGTH_LONG).show();

                    }else if(mn.equals("ok")){
                        imgQr= findViewById(R.id.imgQr);
                        ok=true;
                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(publicKey, BarcodeFormat.QR_CODE, 750, 750);
                            Toast.makeText(Etapa2.this, "ok recibido", Toast.LENGTH_LONG).show();
                            imgQr.setImageBitmap(bitmap);
                            aviso.setText("ok");

                        }catch (WriterException e){
                            e.printStackTrace();
                        }
                    }

                    else if(mn.equals("no")){
                        aviso.setText("usuario invalido");
                        //escritura(mn,"mn.txt");
                        aviso.setText("error");
                        ok=false;
                    }

                    else {
                        escritura(mn,"claveStoke.txt");
                        Toast.makeText(Etapa2.this, "clave stoke ricibida", Toast.LENGTH_LONG).show();
                        aviso.setText("clave stoke recibida");
                        claveStokeCifrada=lectura("claveStoke.txt");

                            
                        Log.d("TAG1", "texto -> " + texto);



                        //descifrar("claveStoke.txt");
                    }
                }
            });



//menu SALIR
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menuaccion,menu);
        return true;

    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id==R.id.salir){
            SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor= preferences.edit();
            editor.putString("llave1",null);
            editor.putBoolean("llave3",false);
            editor.commit();
            startActivity(new Intent(Etapa2.this,MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void cargarPreferencias(){
        SharedPreferences preferences =getSharedPreferences("sesion",Context.MODE_PRIVATE);
        user=preferences.getString("llave1","no hay informacion");
        pass=preferences.getString("llave2","no hay informacion");
        Log.d("TAG1", "user -> " + user);
        Log.d("TAG1", "pass -> " + pass);
    }

    public String descifrar(String archivo){
        String clave=lectura(archivo);
        String letra="";
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        final Python py = Python.getInstance();
        PyObject pyo = py.getModule("script");
        PyObject obj = pyo.callAttr("main3", clave,privKey);

        String str = obj.toString();

        return str;
    }

    private String descrypt(String result) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        cipher.init(Cipher.DECRYPT_MODE, llavepriv);
        descryptedByte = cipher.doFinal(Base64.decode(result, Base64.NO_WRAP));
        descryptedString = new String(descryptedByte);
        Log.d("TAG1", "cifrado -> " + result);
        Log.d("TAG1", "descifrado -> " + descryptedString);
        return descryptedString;
    }

    public void StringToKey(String kpublica, String kprivate) {
        kprivate = kprivate.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
        kpublica = kpublica.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        Log.d("TAG1", "private -> " + kprivate);
        Log.d("TAG1", "public -> " + kpublica);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(kprivate, Base64.DEFAULT));
        PrivateKey privKey = null;
        try {
            llavepriv = kf.generatePrivate(keySpecPKCS8);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decode(kpublica, Base64.DEFAULT));
        PublicKey pubKey = null;
        try {
            llavepubli =  kf.generatePublic(keySpecX509);
            Log.d("TAG1", "private -> " + llavepriv);
            Log.d("TAG1", "public -> " + llavepubli);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }


    }
}