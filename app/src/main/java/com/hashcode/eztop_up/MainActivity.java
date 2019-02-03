package com.hashcode.eztop_up;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.hashcode.eztop_up.Utility.CarrierDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hashcode.eztop_up.Utility.CarrierDialog.currentCarrier;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "AndroidCameraApi";

    private TextureView textureView;

    private ImageView flash;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private CameraManager cameraManager;
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private boolean isFlasherOn;
    private boolean cameraOpen;
    private ImageView carrierLogo;
    private ImageView scanningIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.textureView);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);


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
        flash.setVisibility(View.GONE);
        carrierLogo = findViewById(R.id.carrierLogo);
        if (currentCarrier != null)
            carrierLogo.setImageBitmap(currentCarrier.getImage());
        assert carrierLogo != null;
        carrierLogo.setVisibility(View.GONE);
        carrierLogo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CarrierDialog dialogBox = new CarrierDialog(MainActivity.this);
                dialogBox.Buld(MainActivity.this);
            }
        });
        scanningIcon = findViewById(R.id.scaningIcon);
        scanningIcon.setVisibility(View.INVISIBLE);
        scan();
    }


    private void toggleFlash(int status)
    {


        try
        {

            if (status == 1)
            {
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null);

                isFlasherOn = true;
            } else
            {
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null);

                isFlasherOn = false;
            }


        } catch (CameraAccessException e)
        {
            e.printStackTrace();


        }


    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener()
    {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
        {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
        {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
        {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface)
        {

        }
    };

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback()
    {
        @Override
        public void onOpened(@NonNull CameraDevice camera)
        {
            Log.e(TAG, "onOpned");
            cameraDevice = camera;
            createCameraPreview();

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera)
        {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error)
        {
            if(cameraDevice != null)
            {
                cameraDevice.close();
                cameraDevice = null;
            }

        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback()
    {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result)
        {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(MainActivity.this, "Saved " + file, Toast.LENGTH_LONG).show();
            createCameraPreview();
        }
    };

    protected void startBackgroundThread()
    {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread()
    {
        mBackgroundThread.quitSafely();
        try
        {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    protected void takePicture()
    {
        if (cameraDevice == null)
        {
            Log.e(TAG, "Camera device is null1");
            return;
        }

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try
        {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;

            if (characteristics != null)
            {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                int width = 640;
                int height = 480;
                if (jpegSizes != null && 0 < jpegSizes.length)
                {
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }

                ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                List<Surface> outputSurfaces = new ArrayList<Surface>(2);
                outputSurfaces.add(reader.getSurface());
                outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
                final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                //orientation

                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");

                ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener()
                {
                    @Override
                    public void onImageAvailable(ImageReader reader)
                    {
                        Image image = null;
                        try
                        {
                            image = reader.acquireLatestImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.capacity()];
                            buffer.get(bytes);
                            save(bytes);
                        }
//                        catch (FileNotFoundException  e)
//                        {
//                            e.printStackTrace();
//                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        } finally
                        {
                            if (image != null)
                            {
                                image.close();
                            }
                        }
                    }

                    private void save(byte[] bytes) throws IOException
                    {
                        OutputStream output = null;
                        try
                        {
                            output = new FileOutputStream(file);
                            output.write(bytes);
                        } finally
                        {
                            if (null != output)
                            {
                                output.close();
                            }
                        }
                    }
                };

                reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
                final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback()
                {
                    @Override
                    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)
                    {
                        super.onCaptureCompleted(session, request, result);
                        Toast.makeText(MainActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                        createCameraPreview();
                    }
                };

                cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback()
                {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session)
                    {
                        try
                        {
                            session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                        } catch (CameraAccessException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session)
                    {

                    }
                }, mBackgroundHandler);
            }
        } catch (CameraAccessException | NullPointerException e)
        {
            e.printStackTrace();
        }


    }

    protected void createCameraPreview()
    {

        try
        {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE,CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback()
            {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession)
                {
                    //The camera is already closed
                    if (null == cameraDevice)
                    {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;

                    updatePreview();
                    flash.setVisibility(View.VISIBLE);
                    carrierLogo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession)
                {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);

        } catch (CameraAccessException e)
        {
            e.printStackTrace();
            closeCamera();

        }

    }

    private void openCamera()
    {
        if (cameraManager == null)
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        Log.e(TAG, "is camera open");
        try
        {
            cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            cameraManager.openCamera(cameraId, stateCallback, null);
            cameraOpen = true;
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview()
    {
        if (null == cameraDevice)
        {
            Log.e(TAG, "updatePreview error, return");
        }
//        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureRequestBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE,CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY);
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_EDOF);
        try
        {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void closeCamera()
    {
        if (null != cameraDevice)
        {
            cameraDevice.close();
            cameraDevice = null;
            cameraOpen = false;
        }
        if (null != imageReader)
        {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable())
        {
            openCamera();
        } else
        {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause()
    {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    public void scan()
    {
        final  Handler handler = new Handler();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                scanningIcon.setVisibility(View.VISIBLE);
                Animation animZoomInOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out);

                scanningIcon.startAnimation(animZoomInOut);
                takePicture();
                scanningIcon.setVisibility(View.INVISIBLE);

                handler.postDelayed(this,7000);
            }
        });
    }
}
