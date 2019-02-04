package com.hashcode.eztop_up.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.hashcode.eztop_up.AddCarrier;
import com.hashcode.eztop_up.DataRepository.DataBaseHelper;
import com.hashcode.eztop_up.EditCarriers;
import com.hashcode.eztop_up.Entities.Carrier;
import com.hashcode.eztop_up.ModifyCarrier;
import com.hashcode.eztop_up.R;

import java.io.IOException;
import java.util.ArrayList;

public class CarrierDialog
{
    public static final String TAG = "Carrier Dialog";

    public static Carrier currentCarrier;
    private Context context;
    private DataBaseHelper helper;
    private View mView;
    private AlertDialog.Builder mBuilder;
    private ArrayList<Carrier> carrierList;
    private CarrierAdapter carrierAdapter;
    private ListView listView;
    private AlertDialog dialog;

    //constants
    public static final int CARRIER_SELECTION = 1;
    public static final int CARRIER_ADDITION = 2;
    public static final int CARRIER_MODIFICATION = 3;
    public static final int CARRIER_DELETION = 4;

    public CarrierDialog(Context context)
    {
        this.context = context;
    }

    public void Build(final Activity activity, int mode)
    {

        mBuilder = new AlertDialog.Builder(activity);

        if (mode == CARRIER_SELECTION)
        {
            mView = activity.getLayoutInflater().inflate(R.layout.carrier_dialog, null);
            FloatingActionButton editButton = mView.findViewById(R.id.editFlotingActionButton);

            getDB(activity);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    currentCarrier = (Carrier) parent.getItemAtPosition(position);

                    dialog.cancel();
                    ImageView logo = activity.findViewById(R.id.carrierLogo);
                    logo.setImageBitmap(currentCarrier.getImage());

                }
            });

            editButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(activity.getApplicationContext(), EditCarriers.class);

                    activity.startActivity(intent);
                }
            });

        } else
        {

            mView = activity.getLayoutInflater().inflate(R.layout.edit_carriers_layout, null);
            getDB(activity);

            if (mode == CARRIER_ADDITION)
            {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        currentCarrier = (Carrier) parent.getItemAtPosition(position);
                        Intent intent = new Intent(activity.getApplicationContext(), AddCarrier.class);
                        activity.startActivity(intent);


                    }
                });
            } else if (mode == CARRIER_MODIFICATION)
            {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        currentCarrier = (Carrier) parent.getItemAtPosition(position);
                        Intent intent = new Intent(activity, ModifyCarrier.class);
//                        intent.putExtra("Carrier",currentCarrier);
                        activity.startActivity(intent);


                    }
                });
            }
            else if(mode == CARRIER_DELETION)
            {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        currentCarrier = (Carrier) parent.getItemAtPosition(position);
                        dialog.cancel();
                        helper.deleteCarrier(currentCarrier);
                        helper.close();


                    }
                });
            }
        }
        dialog.show();
    }

    private void getDB(Activity activity)
    {
        helper = new DataBaseHelper(context);

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

        carrierList = helper.getAll();

        carrierAdapter = new CarrierAdapter(activity.getApplicationContext(), carrierList);

        listView = mView.findViewById(R.id.list_item_view);
        listView.setAdapter(carrierAdapter);

        mBuilder.setView(mView);

        dialog = mBuilder.create();

    }
}
