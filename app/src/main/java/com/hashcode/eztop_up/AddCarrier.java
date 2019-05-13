package com.hashcode.eztop_up;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.hashcode.eztop_up.Utility.InputValidation;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;


/**
 * Activity that deals with insertion of carriers into the database
 */
public class AddCarrier extends AppCompatActivity
{
    public static final String TAG = "AddCarrier";

    private ImageView carrierLogo;

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
        final EditText codeLength = findViewById(R.id.codeLengthInput);

        //setting image to predefined no_logo image
        carrierLogo.setImageDrawable(getResources().getDrawable(R.drawable.no_logo));

        Button save = findViewById(R.id.save_button);


        carrierLogo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //opening gallery for image selection
//                Intent imagePicker = new Intent(Intent.ACTION_PICK);
                Crop.pickImage(AddCarrier.this);
//                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                String pictureDirPath = pictureDirectory.getParent();
//                Uri data = Uri.parse(pictureDirPath);

//                imagePicker.setDataAndType(data, "image/*");
//                startActivityForResult(imagePicker, OPEN_GALLERY_CODE);
            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = carrierName.getText().toString();
                String ussd = USSD.getText().toString();
                int codeLengthValue = Integer.parseInt(codeLength.getText().toString());
                Drawable drawable = carrierLogo.getDrawable();
                if (!name.equals("") && !ussd.equals("") && InputValidation.validateUSSD(ussd))
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


                    helper.insertCarrier(name, ussd, ((BitmapDrawable) drawable).getBitmap(),codeLengthValue);
                    helper.close();

                    Toast.makeText(AddCarrier.this, "NetWork Career Added", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AddCarrier.this, SettingsActivity.class);
                    startActivity(intent);
                } else
                {
                    Toast.makeText(AddCarrier.this, "Please Check Your Inputs Again", Toast.LENGTH_LONG).show();
                }
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
