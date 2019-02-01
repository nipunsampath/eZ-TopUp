package com.hashcode.eztop_up.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.hashcode.eztop_up.DataRepository.DataBaseHelper;
import com.hashcode.eztop_up.Entities.Carrier;
import com.hashcode.eztop_up.MainActivity;
import com.hashcode.eztop_up.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class CarrierDialog
{
    public static Carrier currentCarrier;
    private Context context;

    public CarrierDialog(Context context)
    {
        this.context = context;
    }

    public void Buld(final Activity activity)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.carrier_dialog, null);




//        carrierList.add(new Carrier("Dialog","*#123#",R.drawable.logo_small_dialog));
//        carrierList.add(new Carrier("Mobitel","*102*",R.drawable.logo_small_mobitel));
//
//        carrierList.add(new Carrier("Hutch","*355*",R.drawable.logo_small_hutch));
//        carrierList.add(new Carrier("Etisalat","#133*",R.drawable.logo_small_etisalat));
//        carrierList.add(new Carrier("Airtel","*567#",R.drawable.logo_small_airtel));

        DataBaseHelper helper = new DataBaseHelper(context);

        try {

            helper.createDataBase();
            helper.openDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }catch (SQLException e)
        {
            throw new Error("Unable to create database");
        }
        final ArrayList<Carrier> carrierList = helper.getAll();

        CarrierAdapter carrierAdapter = new CarrierAdapter(activity.getApplicationContext() ,carrierList);

        final ListView listView = mView.findViewById(R.id.list_item_view);
        listView.setAdapter(carrierAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                currentCarrier = (Carrier) parent.getItemAtPosition(position);

                Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);

                activity.startActivity(intent);
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }
}
