package com.gmail.tracebachi.MangoShop;

import com.gmail.tracebachi.MangoShop.Database.MySQLShopDatabase;
import com.gmail.tracebachi.MangoShop.Database.ShopDatabase;
import com.gmail.tracebachi.MangoShop.Listener.SignListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/16/16.
 */
public class MangoShop extends JavaPlugin
{
    private ShopDatabase shopDatabase;
    private SignListener signListener;

    @Override
    public void onLoad()
    {

    }

    @Override
    public void onEnable()
    {
        shopDatabase = new MySQLShopDatabase(this);

        signListener = new SignListener(this);
        signListener.register();
    }

    @Override
    public void onDisable()
    {
        signListener.shutdown();
        signListener = null;

        shopDatabase.shutdown();
        shopDatabase = null;
    }

    public ShopDatabase getShopDatabase()
    {
        return shopDatabase;
    }
}
