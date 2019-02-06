package com.hashcode.eztop_up;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.hashcode.eztop_up.Camera.BoxDetector;
import com.hashcode.eztop_up.Camera.CameraSource;
import com.hashcode.eztop_up.DataRepository.DataBaseHelper;
import com.hashcode.eztop_up.Entities.Carrier;
import com.hashcode.eztop_up.Utility.CarrierDialog;
import com.hashcode.eztop_up.Utility.InputValidation;
import com.hashcode.eztop_up.Utility.RechargeDialog;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "AndroidCameraApi";
    private static final int REQUEST_CAMERA_PERMISSION = 200;


    public static Carrier placeholder;
    public static Carrier currentCarrier;
    public static ArrayList<Carrier> carrierList;
    private SurfaceView cameraView;
    private ImageView flash;


    private boolean mFlashSupported;

    private boolean isFlasherOn;

    private ImageView carrierLogo;
    private ImageView scanningIcon;
    private CameraSource cameraSource;
    private TextView scanningText;
    private DataBaseHelper helper;
    private Detector.Processor<TextBlock> textProcessor;
    private BoxDetector wrappedDetector;
    public static boolean dialogCalled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.surfaceView);
        cameraView.setZOrderOnTop(false);
        assert cameraView != null;
        scanningText = findViewById(R.id.scanningText);
        carrierList = getAllCarriers();

        dialogCalled = false;
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        //wrap text detector to recognize text in the middle
        wrappedDetector = new BoxDetector(textRecognizer, (int) Math.ceil(400 * logicalDensity), (int) Math.ceil(100 * logicalDensity));

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

            textProcessor = new Detector.Processor<TextBlock>()
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


                                String rechargeCode = "12";
                                String scannedString = stringBuilder.toString();
                                if (scannedString.length() >= 12 && !dialogCalled)
                                {

                                    rechargeCode = scannedString.substring(0, 12);
                                    dialogCalled = true;

                                    RechargeDialog dialog = new RechargeDialog();
                                    dialog.Build(rechargeCode, MainActivity.this);


                                }

                            }

                        });
                    }
                }
            };
            wrappedDetector.setProcessor(textProcessor);

            placeholder = new Carrier(0, "Placeholder", "placeholder", BitmapFactory.decodeResource(getResources(), R.drawable.no_logo));
            flash = findViewById(R.id.flashIcon);
            assert flash != null;
            flash.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    boolean hasFlash = MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                    if (hasFlash)
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
                    } else
                    {
                        Toast.makeText(MainActivity.this, "Device does not have a flasher", Toast.LENGTH_LONG).show();
                    }

                }
            });

            if (currentCarrier == null)
                currentCarrier = carrierList.get(0);
            carrierLogo = findViewById(R.id.carrierLogo);
            if (currentCarrier != null)
                carrierLogo.setImageBitmap(currentCarrier.getImage());
            assert carrierLogo != null;

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


    public ArrayList<Carrier> getAllCarriers()
    {
        helper = new DataBaseHelper(this);

        try
        {

            helper.createDataBase();
            helper.openDataBase();

        } catch (IOException ioe)
        {

            throw new Error("Unable to create database");

        } catch (SQLException e)
        {
            throw new Error("Unable to create database");
        }

        return helper.getAll();
    }

}
