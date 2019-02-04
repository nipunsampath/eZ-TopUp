package com.hashcode.eztop_up;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hashcode.eztop_up.DataRepository.DataBaseHelper;
import com.hashcode.eztop_up.Entities.Carrier;
import com.hashcode.eztop_up.Utility.CarrierDialog;
import com.hashcode.eztop_up.Utility.CropDialog;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.BitSet;

public class ModifyCarrier extends AppCompatActivity
{

    public static final int OPEN_GALLERY_CODE = 22;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == OPEN_GALLERY_CODE)
        {
            assert data != null;
            Uri imageURI = data.getData();
            CropDialog cropDialog = new CropDialog();
            cropDialog.Build(ModifyCarrier.this,imageURI);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        Carrier Carrier = (Carrier) getIntent().getSerializableExtra("Carrier");

        setContentView(R.layout.content_edit_carrier);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageView carrierLogo = findViewById(R.id.carrierLogo_edit_carrier);
        final EditText carrierName = findViewById(R.id.carrierName);
        final EditText USSD = findViewById(R.id.ussdInput);

        carrierLogo.setImageBitmap(CarrierDialog.currentCarrier.getImage());
        carrierName.setText(CarrierDialog.currentCarrier.getName());
        USSD.setText(CarrierDialog.currentCarrier.getUssd());

        Button save = findViewById(R.id.save_button);


        carrierLogo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //opening gallery for image selection
                Intent imagePicker = new Intent(Intent.ACTION_PICK);

                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirPath = pictureDirectory.getParent();
                Uri data = Uri.parse(pictureDirPath);

                imagePicker.setDataAndType(data,"image/*");
                startActivityForResult(imagePicker, OPEN_GALLERY_CODE);
            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Carrier modifiedCarrier = new Carrier(CarrierDialog.currentCarrier.getId(),carrierName.getText().toString(),USSD.getText().toString(), ((BitmapDrawable)carrierLogo.getDrawable()).getBitmap());

                if(!CarrierDialog.currentCarrier.equals(modifiedCarrier))
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

                Intent intent = new Intent(ModifyCarrier.this,EditCarriers.class);
                startActivity(intent);
            }
        });



    }

}
