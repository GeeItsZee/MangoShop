package com.gmail.tracebachi.MangoShop.Database;

import com.gmail.tracebachi.MangoShop.Interface.Shutdownable;
import com.gmail.tracebachi.MangoShop.Storage.Shop;
import org.bukkit.Location;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/17/16.
 */
public interface ShopDatabase extends Shutdownable
{
    Shop getShop(Location location);

    void createShop(Shop shop);

    RemoveResult removeShop(Location location);

    enum RemoveResult
    {
        SHOP_NOT_FOUND,
        SUCCESS
    }
}
