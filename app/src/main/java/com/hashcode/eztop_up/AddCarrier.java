package com.hashcode.eztop_up;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hashcode.eztop_up.DataRepository.DataBaseHelper;
import com.hashcode.eztop_up.Utility.CropDialog;
import com.hashcode.eztop_up.Utility.InputValidation;

import java.io.File;
import java.io.IOException;


/**
 * Activity that deals with insertion of carriers into the database
 */
public class AddCarrier extends AppCompatActivity
{
    public static final String TAG = "AddCarrier";
    //Request Code
    public static final int OPEN_GALLERY_CODE = 22;

    /**
     * Handling the Carrier Logo Croping
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == OPEN_GALLERY_CODE)
        {
            assert data != null;
            Uri imageURI = data.getData();
            CropDialog cropDialog = new CropDialog();
            cropDialog.Build(AddCarrier.this, imageURI);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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

        final ImageView carrierLogo = findViewById(R.id.carrierLogo_edit_carrier);
        final EditText carrierName = findViewById(R.id.carrierName);
        final EditText USSD = findViewById(R.id.ussdInput);

        //setting image to predefined no_logo image
        carrierLogo.setImageDrawable(getResources().getDrawable(R.drawable.no_logo));

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

                imagePicker.setDataAndType(data, "image/*");
                startActivityForResult(imagePicker, OPEN_GALLERY_CODE);
            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = carrierName.getText().toString();
                String ussd = USSD.getText().toString();
                Drawable drawable = carrierLogo.getDrawable();
                if(!name.equals("") && !ussd.equals("") && InputValidation.validateUSSD(ussd))
                {
                    DataBaseHelper helper = new DataBaseHelper(AddCarrier.this);

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



                    helper.insertCarrier(name, ussd, ((BitmapDrawable) drawable).getBitmap());
                    helper.close();

                    Toast.makeText(AddCarrier.this, "NetWork Career Added", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AddCarrier.this, EditCarriers.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(AddCarrier.this,"Please Check Your Inputs Again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
