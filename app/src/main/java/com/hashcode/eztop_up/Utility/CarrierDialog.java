package com.hashcode.eztop_up.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.hashcode.eztop_up.AddCarrier;
import com.hashcode.eztop_up.DataRepository.DataBaseHelper;
import com.hashcode.eztop_up.EditCarriers;
import com.hashcode.eztop_up.Entities.Carrier;
import com.hashcode.eztop_up.MainActivity;
import com.hashcode.eztop_up.ModifyCarrier;
import com.hashcode.eztop_up.R;

import java.io.IOException;

public class CarrierDialog
{
    public static final String TAG = "Carrier Dialog";

    private Context context;

    private View mView;
    private AlertDialog.Builder mBuilder;
    private CarrierAdapter carrierAdapter;
    private ListView listView;
    private AlertDialog dialog;

    //constants
    public static final int CARRIER_SELECTION = 1;
    public static final int CARRIER_ADDITION = 2;
    public static final int CARRIER_MODIFICATION = 3;
    public static final int CARRIER_DELETION = 4;
    private DataBaseHelper helper;

    public CarrierDialog(Context context)
    {
        this.context = context;
    }

    public void Build(final Activity activity, int mode)
    {

        mBuilder = new AlertDialog.Builder(activity);
        MainActivity.dialogCalled = true;

        if (mode == CARRIER_SELECTION)
        {
            mView = activity.getLayoutInflater().inflate(R.layout.carrier_dialog, null);
            FloatingActionButton editButton = mView.findViewById(R.id.editFlotingActionButton);

            mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    MainActivity.dialogCalled = false;
                }
            });
            getDB(activity);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    MainActivity.currentCarrier = (Carrier) parent.getItemAtPosition(position);

                    dialog.cancel();
                    ImageView logo = activity.findViewById(R.id.carrierLogo);
                    logo.setImageBitmap(MainActivity.currentCarrier.getImage());

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
                        MainActivity.currentCarrier = (Carrier) parent.getItemAtPosition(position);
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
                        MainActivity.currentCarrier = (Carrier) parent.getItemAtPosition(position);
                        Intent intent = new Intent(activity, ModifyCarrier.class);
//                        intent.putExtra("Carrier",currentCarrier);
                        activity.startActivity(intent);


                    }
                });
            } else if (mode == CARRIER_DELETION)
            {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        MainActivity.currentCarrier = (Carrier) parent.getItemAtPosition(position);
                        dialog.cancel();
                        int status = helper.deleteCarrier(MainActivity.currentCarrier);
                        if (status != 0) // status = 0 if no deletion ocured
                        {
                            Toast.makeText(activity, "NetWork Career Deleted", Toast.LENGTH_LONG).show();
                            MainActivity.currentCarrier = MainActivity.placeholder;
                        } else
                            Toast.makeText(activity, "NetWork Career Could not be Deleted", Toast.LENGTH_LONG).show();
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

        MainActivity.carrierList = helper.getAll();
        carrierAdapter = new CarrierAdapter(activity.getApplicationContext(), MainActivity.carrierList);

        listView = mView.findViewById(R.id.list_item_view);
        listView.setAdapter(carrierAdapter);

        mBuilder.setView(mView);

        dialog = mBuilder.create();

    }
}
