package com.example.qritqin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.util.regex.*;


public class Scanner extends AppCompatActivity {

    SurfaceView CameraView;
    TextView CodeValue;
    String intentData = "";
    ConstraintLayout back;
    private BarcodeDetector Detector;
    private Barcode Barcode;
    private CameraSource cameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        initviews();
    }
    private void initviews()
    {
        CodeValue = findViewById(R.id.CodeValue);
        back = findViewById(R.id.back);
        CameraView = findViewById(R.id.CameraView);
    }
    private void initcomponents()
    {
        Toast.makeText(getApplicationContext(),"Escaner iniciado",Toast.LENGTH_SHORT).show();
        Detector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this,Detector)
                .setRequestedPreviewSize(1920,1080).setAutoFocusEnabled(true).build();
        CameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if(ActivityCompat.checkSelfPermission(Scanner.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        cameraSource.start(CameraView.getHolder());
                    }
                    else {
                        ActivityCompat.requestPermissions(Scanner.this,
                                new String[]{Manifest.permission.CAMERA}, 201);
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        Detector.setProcessor(new Detector.Processor<com.google.android.gms.vision.barcode.Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "Escaner cerrado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(com.google.android.gms.vision.Detector.Detections<com.google.android.gms.vision.barcode.Barcode> detections) {
                final SparseArray<Barcode> codigos = detections.getDetectedItems();
                if (codigos.size() != 0)
                {
                    CodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            intentData = codigos.valueAt(0).displayValue;
                            if(Base64.isBase64(intentData))
                            {
                                byte[] qrdata = Base64.decodeBase64(intentData.getBytes());
                                String decodedString = new String(qrdata);
                                String re1="(\\d)";
                                String re2="(\\d)";
                                String re3="(\\d)";
                                String re4="(\\d)";
                                String re5="(\\d)";
                                String re6="(\\d)";
                                String re7="(\\d)";
                                String re8="(\\d)";
                                String re9="(;)";
                                String re10="(t)";
                                String re11="(r)";
                                String re12="(u)";
                                String re13="(e)";
                                Pattern patron = Pattern.compile(re1+re2+re3+re4+re5
                                                +re6+re7+re8+re9+re10+re11+re12+re13,
                                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                                Matcher m = patron.matcher(decodedString);
                                if(m.find()) {
                                    CodeValue.setText("Acceso permitido");
                                    back.setBackgroundColor(Color.parseColor("#60ad5e"));
                                }
                                else{
                                    CodeValue.setText("Acceso no permitido");
                                    back.setBackgroundColor(Color.parseColor("#b71c1c"));
                                }
                            }else{
                                CodeValue.setText("Acceso no permitido");
                                back.setBackgroundColor(Color.parseColor("#b71c1c"));
                            }
                        }
                    });
                }else
                    {
                    }
            }
        });
    }
    protected void onPause()
    {
     super.onPause();
     cameraSource.release();
    }
    protected void onResume()
    {
        super.onResume();
        initcomponents();
    }
}
