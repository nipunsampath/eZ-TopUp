package com.hashcode.eztop_up;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hashcode.eztop_up.DataRepository.DataBaseHelper;
import com.hashcode.eztop_up.Entities.Carrier;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

public class ModifyCarrier extends AppCompatActivity
{

    public static final String TAG = "Modify Carrier";

    public static final int OPEN_GALLERY_CODE = 22;
    private ImageView carrierLogo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK)
        {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP)
        {
            handleCrop(resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        Carrier Carrier = (Carrier) getIntent().getSerializableExtra("Carrier");

        setContentView(R.layout.content_edit_carrier);
        //handling the toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        carrierLogo = findViewById(R.id.carrierLogo_edit_carrier);
        final EditText carrierName = findViewById(R.id.carrierName);
        final EditText USSD = findViewById(R.id.ussdInput);

        carrierLogo.setImageBitmap(MainActivity.currentCarrier.getImage());
        carrierName.setText(MainActivity.currentCarrier.getName());
        USSD.setText(MainActivity.currentCarrier.getUssd());

        Button save = findViewById(R.id.save_button);


        carrierLogo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //opening gallery for image selection
                Crop.pickImage(ModifyCarrier.this);
//                Intent imagePicker = new Intent(Intent.ACTION_PICK);
//
//                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                String pictureDirPath = pictureDirectory.getParent();
//                Uri data = Uri.parse(pictureDirPath);
//
//                imagePicker.setDataAndType(data,"image/*");
//                startActivityForResult(imagePicker, OPEN_GALLERY_CODE);
            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Carrier modifiedCarrier = new Carrier(MainActivity.currentCarrier.getId(), carrierName.getText().toString(), USSD.getText().toString(), ((BitmapDrawable) carrierLogo.getDrawable()).getBitmap());

                if (!MainActivity.currentCarrier.equals(modifiedCarrier))
                {
                    DataBaseHelper helper = new DataBaseHelper(ModifyCarrier.this);

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
                    helper.updateCarrier(modifiedCarrier);
                    helper.close();
                }

                Toast.makeText(ModifyCarrier.this, "NetWork Career Modified", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ModifyCarrier.this, EditCarriers.class);
                startActivity(intent);
            }
        });


    }

    private void beginCrop(Uri source)
    {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }


    private void handleCrop(int resultCode, Intent result)
    {
        if (resultCode == RESULT_OK)
        {
            carrierLogo.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR)
        {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
