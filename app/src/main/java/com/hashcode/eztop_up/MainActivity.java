package com.hashcode.eztop_up;

import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.hashcode.eztop_up.Camera.BoxDetector;
import com.hashcode.eztop_up.Camera.CameraSource;
import com.hashcode.eztop_up.Entities.Carrier;
import com.hashcode.eztop_up.Utility.CarrierDialog;
import com.hashcode.eztop_up.Utility.InputValidation;

import java.io.IOException;

import static com.hashcode.eztop_up.Utility.CarrierDialog.currentCarrier;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "AndroidCameraApi";
    private static final int REQUEST_CAMERA_PERMISSION = 200;


    public static Carrier placeholder;
    private SurfaceView cameraView;
    private ImageView flash;


    private boolean mFlashSupported;

    private boolean isFlasherOn;

    private ImageView carrierLogo;
    private ImageView scanningIcon;
    private CameraSource cameraSource;
    private TextView scanningText;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.surfaceView);
        cameraView.setZOrderOnTop(false);
        assert cameraView != null;
        scanningText = findViewById(R.id.scanningText);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        //wrap text detector to recognize text in the middle
        BoxDetector wrappedDetector = new BoxDetector(textRecognizer, (int) Math.ceil(400 * logicalDensity), (int) Math.ceil(100 * logicalDensity));

        if (!textRecognizer.isOperational())
        {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else
        {
            cameraSource = new CameraSource.Builder(getApplicationContext(), wrappedDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback()
            {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder)
                {

                    try
                    {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
                {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder)
                {
                    cameraSource.stop();
                }
            });

            wrappedDetector.setProcessor(new Detector.Processor<TextBlock>()
            {
                @Override
                public void release()
                {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections)
                {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0)
                    {
                        scanningText.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                StringBuilder stringBuilder = new StringBuilder();

                                    TextBlock item = items.valueAt(0);

                                    String s = InputValidation.getNumbers(item.getValue());
                                    stringBuilder.append(s);
                                    stringBuilder.append("\n");


                                scanningText.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });

            placeholder = new Carrier(0, "Placeholder", "placeholder", BitmapFactory.decodeResource(getResources(), R.drawable.no_logo));
            flash = findViewById(R.id.flashIcon);
            assert flash != null;
            flash.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (isFlasherOn)
                    {
                        toggleFlash(0);
                        flash.setImageResource(R.drawable.flash_off);
                    } else
                    {
                        toggleFlash(1);
                        flash.setImageResource(R.drawable.flash_on);
                    }

                }
            });
//            flash.setVisibility(View.GONE);
            carrierLogo = findViewById(R.id.carrierLogo);
            if (currentCarrier != null)
                carrierLogo.setImageBitmap(currentCarrier.getImage());
            assert carrierLogo != null;
//            carrierLogo.setVisibility(View.GONE);
            carrierLogo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    CarrierDialog dialogBox = new CarrierDialog(MainActivity.this);
                    dialogBox.Build(MainActivity.this, CarrierDialog.CARRIER_SELECTION);
                }
            });
            scanningIcon = findViewById(R.id.scaningIcon);
            scanningIcon.setVisibility(View.INVISIBLE);
//        scan();
        }
    }


    private void toggleFlash(int status)
    {


        if (status == 1)
        {
            cameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            isFlasherOn = true;
        } else
        {
            cameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            isFlasherOn = false;
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CAMERA_PERMISSION:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                    try
                    {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }


}
