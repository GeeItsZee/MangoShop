package com.gmail.tracebachi.MangoShop.Storage;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/20/16.
 */
public class ShopBuilder
{
    private int x;
    private int y;
    private int z;
    private String world;
    private ItemStack itemStack;
    private Double buyPrice;
    private Double sellPrice;
    private int amount;
    private String ownerName;
    private String ownerUUID;

    public ShopBuilder setX(int x)
    {
        this.x = x;
        return this;
    }

    public ShopBuilder setY(int y)
    {
        this.y = y;
        return this;
    }

    public ShopBuilder setZ(int z)
    {
        this.z = z;
        return this;
    }

    public ShopBuilder setWorld(String world)
    {
        this.world = world;
        return this;
    }

    public ShopBuilder setLocation(Location location)
    {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getName();
        return this;
    }

    public ShopBuilder setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;
        return this;
    }

    public ShopBuilder setBuyPrice(Double buyPrice)
    {
        this.buyPrice = buyPrice;
        return this;
    }

    public ShopBuilder setSellPrice(Double sellPrice)
    {
        this.sellPrice = sellPrice;
        return this;
    }

    public ShopBuilder setAmount(int amount)
    {
        this.amount = amount;
        return this;
    }

    public ShopBuilder setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
        return this;
    }

    public ShopBuilder setOwnerUUID(String ownerUUID)
    {
        this.ownerUUID = ownerUUID;
        return this;
    }

    public Shop build()
    {
        return new Shop(
            x,
            y,
            z,
            world,
            itemStack, amount, buyPrice,
            sellPrice, ownerName,
            ownerUUID);
    }
}
