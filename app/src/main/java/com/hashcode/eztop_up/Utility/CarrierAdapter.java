package com.hashcode.eztop_up.Utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hashcode.eztop_up.Entities.Carrier;
import com.hashcode.eztop_up.R;

import java.util.ArrayList;

public class CarrierAdapter extends ArrayAdapter<Carrier>
{
    public CarrierAdapter(Context context, ArrayList<Carrier> types)
    {
        //resource id is zero because we use custom resource layout
        super(context, 0, types);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {

        View listItemView = convertView;
        if (listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Carrier nowCarrier = getItem(position);

        TextView nameTextView = listItemView.findViewById(R.id.AccountTypeName);
        ImageView imageView = listItemView.findViewById(R.id.AccountTypeImage);
//        imageView.setVisibility(View.GONE);
        nameTextView.setTextSize(20);
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.setMargins(20, 16, 16, 16);
        nameTextView.setLayoutParams(textViewLayoutParams);

        try
        {
            assert nowCarrier != null;
            nameTextView.setText(nowCarrier.getName());
            imageView.setImageResource(nowCarrier.getImage());

        } catch (NullPointerException e)
        {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return listItemView;
    }
}
