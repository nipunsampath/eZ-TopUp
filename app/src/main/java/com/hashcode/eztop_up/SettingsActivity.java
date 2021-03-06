package com.hashcode.eztop_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hashcode.eztop_up.Utility.CarrierDialog;

/**
 * Activity that acts as a menu for Carrier Insertion, Modification and Deletion.
 */
public class SettingsActivity extends AppCompatActivity
{
    public static final String TAG = "Settings Activity";

    private View addCarrier;
    private View editCarrier;
    private View deleteCarrier;
    private View scanMode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        //handling the toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle(R.string.edit);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        addCarrier = findViewById(R.id.addView);
        editCarrier = findViewById(R.id.editView);
        deleteCarrier = findViewById(R.id.deleteView);
        scanMode = findViewById(R.id.scanView);

        final CarrierDialog dialogBox = new CarrierDialog(SettingsActivity.this);

        addCarrier.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                dialogBox.Build(SettingsActivity.this, CarrierDialog.CARRIER_ADDITION);
                Intent intent = new Intent(SettingsActivity.this,AddCarrier.class);
                startActivity(intent);
            }
        });

        editCarrier.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogBox.Build(SettingsActivity.this, CarrierDialog.CARRIER_MODIFICATION);
            }
        });

        deleteCarrier.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogBox.Build(SettingsActivity.this, CarrierDialog.CARRIER_DELETION);
            }
        });

        scanMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}
