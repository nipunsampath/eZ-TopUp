package com.hashcode.eztop_up.Entities;


public class Carrier
{
    private String name;
    private String ussd;
    private int image;

    public Carrier(String name, String ussd, int image)
    {
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

    public String getUssd()
    {
        return ussd;
    }

    public void setUssd(String ussd)
    {
        this.ussd = ussd;
    }

    public int getImage()
    {
        return image;
    }

    public void setImage(int image)
    {
        this.image = image;
    }
}
