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
public class EditCarriers extends AppCompatActivity
{
    public static final String TAG = "Edit Carrier";

    private View addCarrier;
    private View editCarrier;
    private View deleteCarrier;
    private View scanMode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_menu_item);

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

        addCarrier = findViewById(R.id.addView);
        editCarrier = findViewById(R.id.editView);
        deleteCarrier = findViewById(R.id.deleteView);
        scanMode = findViewById(R.id.scanView);

        final CarrierDialog dialogBox = new CarrierDialog(EditCarriers.this);

        addCarrier.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogBox.Build(EditCarriers.this, CarrierDialog.CARRIER_ADDITION);
            }
        });

        editCarrier.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogBox.Build(EditCarriers.this, CarrierDialog.CARRIER_MODIFICATION);
            }
        });

        deleteCarrier.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogBox.Build(EditCarriers.this, CarrierDialog.CARRIER_DELETION);
            }
        });

        scanMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(EditCarriers.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}
