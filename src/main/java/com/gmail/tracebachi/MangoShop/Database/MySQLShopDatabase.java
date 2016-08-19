package com.gmail.tracebachi.MangoShop.Database;

import com.gmail.tracebachi.MangoShop.MangoShop;
import com.gmail.tracebachi.MangoShop.Storage.Shop;
import org.bukkit.Location;

import java.util.HashMap;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/18/16.
 */
public class MySQLShopDatabase implements ShopDatabase
{
    private MangoShop plugin;
    private HashMap<Location, Shop> locationToShopChestMap = new HashMap<>(256);

    public MySQLShopDatabase(MangoShop plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void shutdown()
    {
        plugin = null;
    }

    @Override
    public Shop getShop(Location location)
    {
        Shop shop = locationToShopChestMap.get(location);

        if(shop != null) { return shop; }

        return null;
    }

    @Override
    public void createShop(Shop shop)
    {
        Location location = shop.getLocation();

        locationToShopChestMap.put(location, shop);
    }

    @Override
    public RemoveResult removeShop(Location location)
    {
        Shop shop = locationToShopChestMap.remove(location);

        if(shop == null) { return RemoveResult.SHOP_NOT_FOUND; }

        return RemoveResult.SUCCESS;
    }
}
