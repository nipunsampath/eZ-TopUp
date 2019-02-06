package com.hashcode.eztop_up.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hashcode.eztop_up.MainActivity;
import com.hashcode.eztop_up.R;

public class RechargeDialog
{
    private AlertDialog.Builder mBuilder;
    private View mView;

    private AlertDialog dialog;
    private Activity activity;
    private Button rechargeButton;
    private Button cancelButton;
    private ImageView copyContent;
    private TextView carrierName;
    private TextView rechargeCode;


    public void Build(final String rechargeCode,final Activity activity)
    {
        mBuilder = new AlertDialog.Builder(activity);
        mView = activity.getLayoutInflater().inflate(R.layout.recharge_prompt, null);
        mBuilder.setView(mView);

        this.activity = activity;
        rechargeButton = mView.findViewById(R.id.recharge);
        cancelButton = mView.findViewById(R.id.cancelButtonRecharge);
        copyContent = mView.findViewById(R.id.copyContent);
        carrierName = mView.findViewById(R.id.network_carrier);
        this.rechargeCode = mView.findViewById(R.id.rechargeCodeView);

        carrierName.setText(MainActivity.currentCarrier.getName());
        this.rechargeCode.setText(rechargeCode);
        mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                MainActivity.dialogCalled = false;
            }
        });

        dialog = mBuilder.create();


        rechargeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String ussdCode = Uri.encode(MainActivity.currentCarrier.getUssd()+ rechargeCode + "#");
                activity.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode)));
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
            }
        });

        copyContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("recharge code", rechargeCode);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(activity.getApplicationContext(),"Recharge PIN Copied To Clipboard ",Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }
}
