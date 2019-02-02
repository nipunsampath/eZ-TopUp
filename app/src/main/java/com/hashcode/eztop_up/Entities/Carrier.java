package com.hashcode.eztop_up.Entities;


import android.graphics.Bitmap;

public class Carrier
{
    private int id;
    private String name;
    private String ussd;
    private Bitmap image;

    public Carrier(int id, String name, String ussd, Bitmap image)
    {
        this.id = id;
        this.name = name;
        this.ussd = ussd;
        this.image = image;
    }

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
}
