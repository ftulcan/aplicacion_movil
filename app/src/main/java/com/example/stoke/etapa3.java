package com.example.stoke;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class etapa3 extends AppCompatActivity {

    private static final String TAG = "Camera2VideoImageActivi";
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    public int REQUEST_CODE=200;
    public long exposureDuration =1000000l /30; //1000000000l / 30
    private long seekSs = 6000000l /30;// 6000; 10000000l /30
    private int seekIso = 2500;//3200; 1000
    private TextureView mTextureView;
    private File mImageFolder;
    public Bitmap bm;
    public ByteArrayOutputStream baos;
    public byte[] b;
    int seg=0;
    //public String dato1;
    //public String dato2;
    //public String dato3;
    //public String dato4;
    //public String dato5;
    public String palabra;
    public List<String> lista;
    public String palabraFinal;


    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };


    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            //startPreview();
            if(mIsRecording) {
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mMediaRecorder.start();
            } else {
                startPreview();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };


    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private String mCameraId;
    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private int mTotalRotation;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private ImageButton mRecordImageButton;
    private ImageButton captura;
    private boolean mIsRecording = false;
    private File mVideoFolder;
    private String mVideoFileName;


    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 180);
        ORIENTATIONS.append(Surface.ROTATION_90, 270);
        ORIENTATIONS.append(Surface.ROTATION_180, 0);
        ORIENTATIONS.append(Surface.ROTATION_270, 90);
    }
    private static class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum( (long)(lhs.getWidth() * lhs.getHeight()) -
                    (long)(rhs.getWidth() * rhs.getHeight()));
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etapa3);
        llenarLista();
        createVideoFolder();
        createImageFolder();
        mMediaRecorder = new MediaRecorder();


        mTextureView = (TextureView) findViewById(R.id.textureView);
        //captura=findViewById(R.id.cameraImageButton2);
        mRecordImageButton = (ImageButton) findViewById(R.id.cameraImageButton2);
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        final Python py = Python.getInstance();
        //String p ="/storage/emulated/0/Movies/camera2VideoImage/VIDEO_011_input13.png";

        mRecordImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                palabra="";
                //if (mIsRecording) {
                //mIsRecording = false;
                //mRecordImageButton.setImageResource(R.mipmap.camgreen2);
                //mMediaRecorder.stop();
                //mMediaRecorder.reset();
                //startPreview();
                // Starting the preview prior to stopping recording which should hopefully
                // resolve issues being seen in Samsung devices.
                //} else {
                checkWriteStoragePermission();

                //iniciarHilo();
                Handler handler3 = new Handler();
                handler3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        verificarPermiso();
                        extract();
                    }
                }, 5000);


                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i =60;i>1;i--){
                            String p ="/storage/emulated/0/Movies/camera2VideoImage/VIDEO_011_input"+i+".png";
                            String letra=imageToString(p);
                            PyObject pyo = py.getModule("script");
                            try {
                                PyObject obj = pyo.callAttr("main", letra);

                                String str = obj.toString();

                                //Log.i(Config.TAG,letra+"    este es del movil");
                                //Log.i(Config.TAG,str+"    este es de python");

                                palabra = palabra + str;

                            }catch (Exception e){
                                Log.i(Config.TAG,"ERROR");
                            }
                        }

                        //dato1=palabra.replace(null,"");
                        Log.i(Config.TAG,palabra+"    este es de python");
                    }
                }, 6000);



                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //String pala="";
                        //String word="";
                        //int countE =0;
                        //int count1=0;
                        //int count2=0;

                        //for (int i = 0;i<palabra.length();i++ ){
                        //char a = palabra.charAt(i);
                        //String letra = String.valueOf(a);
                        //Log.i(Config.TAG,letra+" la letra");
                        //if((i+4)<palabra.length()){
                        //  if(palabra.substring(i,i+5).equals("enter") && countE<2){
                        //word=word+palabra.substring(i,i+5);
                        //    if(count1==0 && !palabra.substring(i,i+5).equals(palabra.substring(i+5,i+5+5))){

                        //      count1=i;
                        //    Log.i(Config.TAG,count1+" la letra");
                        //}
                        //else if(count2==0 && count1!=0){
                        //  count2=i;
                        //Log.i(Config.TAG,count2+" la letra");
                        //}

                        //Log.i(Config.TAG,word+" substring");
                                    /*if(countE==1){
                                        word=word+palabra.substring(count1,count2);
                                    }*/
                        //countE =countE + 1;


                        //}

                        //}
                        //}


                        PyObject pyo = py.getModule("script");
                        try {
                            PyObject obj2 = pyo.callAttr("main2", palabra);

                            String str2 = obj2.toString();

                            Log.i(Config.TAG,str2+"    este es del movil");
                            //Log.i(Config.TAG,str+"    este es de python");

                            //palabra = palabra + str;

                        }catch (Exception e){
                            Log.i(Config.TAG,"ERROR");
                        }

                    }
                }, 8000);
                //verificarPermiso();
                //extract();


                //}
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            connectCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Application will not run without camera services", Toast.LENGTH_SHORT).show();
            }
            /*if(grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Application will not have audio on record", Toast.LENGTH_SHORT).show();
            }*/
        }
        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // /*if(mIsRecording || mIsTimelapse) {
                mIsRecording = true;
                mRecordImageButton.setImageResource(R.mipmap.camred2);
                try{
                    createVideoFileName();
                }catch (IOException e){
                    e.printStackTrace();
                }
                //}*/
                Toast.makeText(this,
                        "Permission successfully granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "App needs to save video to run", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for(String cameraId : cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT){
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
                int rotatedWidth = width;
                int rotatedHeight = height;
                if(swapRotation) {
                    rotatedWidth = height;
                    rotatedHeight = width;
                }
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
                } else {
                    if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                        Toast.makeText(this,
                                "Video app required access to camera", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[] {android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                    }, REQUEST_CAMERA_PERMISSION_RESULT);
                }

            } else {
                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startRecord() {
        try{
            setupMediaRecorder();
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mMediaRecorder.getSurface();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);
            setupRequest(mCaptureRequestBuilder);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            //mRecordCaptureSession = session;
                            try {
                                session.setRepeatingRequest(
                                        mCaptureRequestBuilder.build(), null, null
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            Log.d(TAG, "onConfigureFailed: startRecord");
                        }
                    }, null);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void startPreview() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            setupRequest(mCaptureRequestBuilder);
            mCaptureRequestBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            Log.d(TAG, "onConfigured: startPreview");

                            //mPreviewCaptureSession = session;
                            try {
                                session.setRepeatingRequest(mCaptureRequestBuilder.build(),null,mBackgroundHandler);
                                //mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),
                                // null, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            Log.d(TAG, "onConfigureFailed: startPreview");

                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if(mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

    }
    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("Camera2VideoImage");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }



    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrienatation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrienatation + deviceOrientation + 360) % 360;
    }
    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option : choices) {
            if(option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if(bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }
    private void createVideoFolder() {
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        mVideoFolder = new File(movieFile, "Stoke");
        if(!mVideoFolder.exists()) {
            mVideoFolder.mkdirs();
        }
    }
    private File createVideoFileName() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "VIDEO_011";
        File videoFile = new File(mVideoFolder,prepend+".mp4");


        //File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);

        mVideoFileName = videoFile.getAbsolutePath();
        return videoFile;
    }



    private void checkWriteStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                mIsRecording = true;
                mRecordImageButton.setImageResource(R.mipmap.camred2);
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mMediaRecorder.start();

// codigo extra

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsRecording = false;mRecordImageButton.setImageResource(R.mipmap.camgreen2);
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        startPreview();
                        //verificarPermiso();
                        //extract();


                    }
                }, 3000);





                //delaySeg();
                //seg=0;
                //mIsRecording = false;
                //mRecordImageButton.setImageResource(R.mipmap.camgreen2);
                //mMediaRecorder.stop();
                //mMediaRecorder.reset();
                //startPreview();

            }else{
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        } else {
            mIsRecording = true;
            mRecordImageButton.setImageResource(R.mipmap.camred2);
            try {
                createVideoFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startRecord();
            mMediaRecorder.start();
        }
    }


    private void setupMediaRecorder() throws IOException {
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//MPEG_4
        mMediaRecorder.setOutputFile(mVideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(1000000);
        mMediaRecorder.setVideoFrameRate(25);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//H264
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOrientationHint(mTotalRotation);
        mMediaRecorder.prepare();

    }

    protected void setupRequest( CaptureRequest.Builder request) {
        request.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
        request.set(CaptureRequest.SENSOR_EXPOSURE_TIME, seekSs);
        //request.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);
        request.set(CaptureRequest.SENSOR_FRAME_DURATION, 1000000000l / 20);
        request.set(CaptureRequest.SENSOR_SENSITIVITY, seekIso);
    }

    public void extract(){
        //VideoCapture cap = new VideoCapture();
        String fileName = "/storage/emulated/0/Movies/camera2VideoImage/VIDEO_011.mp4";
        String mpegFileName = "/storage/emulated/0/Movies/camera2VideoImage/VIDEO_011_input%d.png";
        String aviFileName = "/storage/emulated/0/Movies/camera2VideoImage/VIDEO_011.avi";


        int rc = FFmpeg.execute("-y -i "+fileName+" "+mpegFileName);
        int rc2 = FFmpeg.execute("-i "+mpegFileName+" -vcodec "+ aviFileName);
        //check response from ffmpeg after execution

        if(rc2 == RETURN_CODE_SUCCESS){
            Log.i(Config.TAG,"command execution completed success");
        }else if(rc2==RETURN_CODE_CANCEL){
            Log.i(Config.TAG,"command execution cancelled by user");
        }else {
            Log.i(Config.TAG, String.format("command execution failed with rc=%d and the output bellow",rc));
            Config.printLastCommandOutput(Log.INFO);
        }
    }

    private void createImageFolder() {
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mImageFolder = new File(imageFile, "camera2VideoImage");
        if(!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }
    }

    public void verificarPermiso(){
        int permisoRead = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permisoRead==PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(this,"permisos read",Toast.LENGTH_SHORT).show();
        }else{
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
        }
    }

    public String imageToString (String st){

        bm = BitmapFactory.decodeFile(st);
        baos =new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG,100,baos);
        b = baos.toByteArray();
        return Base64.encodeToString(b,Base64.DEFAULT);
    }


    public void delaySeg(){
        Handler handler = new Handler();
        for(int i=0; seg<=3;i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    seg = seg + 1;
                    Log.i(Config.TAG,seg+" este es de python");
                }
            }, 1000);
        }
    }

    public void llenarLista(){
        lista=new ArrayList<>();
        lista.add("a");lista.add("s");lista.add("d");lista.add("f");lista.add("g");

    }


    public void iniciarHilo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                verificarPermiso();
                extract();
                try{
                    Thread.sleep(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();

                }

            }
        }).start();
    }
//menu salir
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menuaccion,menu);
        return true;

    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id==R.id.salir){
            /*SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor= preferences.edit();
            editor.putString("llave1",null);
            editor.putBoolean("llave3",false);
            editor.commit();*/
            startActivity(new Intent(etapa3.this,Etapa2.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


}