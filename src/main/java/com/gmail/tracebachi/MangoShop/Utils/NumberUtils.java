package com.gmail.tracebachi.MangoShop.Utils;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/21/16.
 */
public class NumberUtils
{
    public static Integer parseInt(String input)
    {
        try
        {
            return Integer.parseInt(input);
        }
        catch(NumberFormatException ex)
        {
            return null;
        }
    }

    public static Double parseDouble(String input)
    {
        try
        {
            Double number = Double.parseDouble(input);
            return Math.round(number * 100.0) / 100.0;
        }
        catch(NumberFormatException ex)
        {
            return null;
        }
    }

    public static String stripTrailingDecimal(double val)
    {
        return (long) val == val ? String.valueOf((long) val) : String.valueOf(val);
    }
}
