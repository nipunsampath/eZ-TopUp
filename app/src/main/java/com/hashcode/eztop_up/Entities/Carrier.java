package com.hashcode.eztop_up.Entities;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Carrier implements Parcelable
{
    private int id;
    private String name;
    private String ussd;
    private Bitmap image;
    private int ussd_length;

    public Carrier(int id, String name, String ussd, Bitmap image,int length)
    {
        this.id = id;
        this.name = name;
        this.ussd = ussd;
        this.image = image;
        this.ussd_length = length;
    }



    private Carrier(Parcel in)
    {
        this.id = in.readInt();
        this.name = in.readString();
        this.ussd = in.readString();
        this.image = in.readParcelable(Bitmap.class.getClassLoader());


    }

    public static final Creator<Carrier> CREATOR = new Parcelable.Creator<Carrier>()
    {
        @Override
        public Carrier createFromParcel(Parcel in)
        {
            return new Carrier(in);
        }

        @Override
        public Carrier[] newArray(int size)
        {
            return new Carrier[size];
        }
    };

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUssd()
    {
        return ussd;
    }

    public int getUssd_length()
    {
        return ussd_length;
    }

    public void setUssd_length(int ussd_length)
    {
        this.ussd_length = ussd_length;
    }

    public void setUssd(String ussd)
    {
        this.ussd = ussd;
    }

    public Bitmap getImage()
    {
        return image;
    }

    public void setImage(Bitmap image)
    {
        this.image = image;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(ussd);
        dest.writeParcelable(image, flags);
    }
}
