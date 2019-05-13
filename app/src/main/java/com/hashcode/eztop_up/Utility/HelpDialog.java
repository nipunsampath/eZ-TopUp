package com.hashcode.eztop_up.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.hashcode.eztop_up.R;

public class HelpDialog
{
    private final Activity activity;
    private AlertDialog.Builder mBuilder;
    private View mView;
    private AlertDialog dialog;

    public HelpDialog(Activity activity)
    {
        this.activity = activity;
    }

    public void build()
    {
        mBuilder = new AlertDialog.Builder(activity.getApplicationContext());
        mView = activity.getLayoutInflater().inflate(R.layout.carrier_dialog, null);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
    }
}
