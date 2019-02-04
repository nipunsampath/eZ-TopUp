package com.hashcode.eztop_up.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hashcode.eztop_up.R;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class CropDialog
{


    private AlertDialog.Builder mBuilder;
    private View mView;
    private CropImageView mCropView;
    private Uri saveUri;
    private AlertDialog dialog;
    private Activity activity;
    private View mCropButton;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;

    public void Build(final Activity activity, final Uri imageUri)
    {
        mBuilder = new AlertDialog.Builder(activity);
        mView = activity.getLayoutInflater().inflate(R.layout.crop_dialog, null);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        this.activity = activity;
        mCropView = mView.findViewById(R.id.cropImageView);
        mCropButton = mView.findViewById(R.id.cropButton);
        mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
        mCropView.setInitialFrameScale(1.0f);
        mCropView.setOutputWidth(100);
        mCropView.setOutputHeight(100);
        mCropView.setDebug(true);

        mCropView.load(imageUri).execute(mLoadCallback);
        mCropButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCropView.crop(imageUri).execute(new CropCallback()
                {
                    @Override
                    public void onSuccess(Bitmap cropped)
                    {
                        mCropView.crop(imageUri).execute(mCropCallback);
                        mCropView.save(cropped)
                                .execute(saveUri, mSaveCallback);
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                    }
                });
            }
        });

        dialog.show();
    }



    public Uri createSaveUri() {
        return createNewUri(activity.getApplicationContext(), mCompressFormat);
    }


    public static Uri createNewUri(Context context, Bitmap.CompressFormat format)
    {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "scv" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists())
        {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Logger.i("SaveUri = " + uri);
        return uri;
    }

    public static String getDirPath()
    {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite())
        {
            imageDir = new File(extStorageDir.getPath() + "/simplecropview");
        }
        if (imageDir != null)
        {
            if (!imageDir.exists())
            {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite())
            {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    public static String getMimeType(Bitmap.CompressFormat format) {
        Logger.i("getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }

    /**
     * Call Backs
     */
    private final LoadCallback mLoadCallback = new LoadCallback()
    {
        @Override
        public void onSuccess()
        {
        }

        @Override
        public void onError(Throwable e)
        {
        }
    };

    private final CropCallback mCropCallback = new CropCallback()
    {
        @Override
        public void onSuccess(Bitmap cropped)
        {
            mCropView.save(cropped)
                    .compressFormat(mCompressFormat)
                    .execute(createSaveUri(), mSaveCallback);
        }

        @Override
        public void onError(Throwable e)
        {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback()
    {
        @Override
        public void onSuccess(Uri outputUri)
        {
            dialog.cancel();
            ImageView carrierLogo = activity.findViewById(R.id.carrierLogo_edit_carrier);
            try
            {
                InputStream inputStream = activity.getContentResolver().openInputStream(outputUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                carrierLogo.setImageBitmap(bitmap);

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "Unable to display Image", Toast.LENGTH_LONG);
            }

        }

        @Override
        public void onError(Throwable e)
        {
            dialog.cancel();
        }
    };


}
