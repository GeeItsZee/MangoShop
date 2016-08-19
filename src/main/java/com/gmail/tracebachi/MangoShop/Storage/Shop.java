package com.gmail.tracebachi.MangoShop.Storage;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/20/16.
 */
public class Shop
{
    private int x;
    private int y;
    private int z;
    private String world;
    private ItemStack itemStack;
    private int amount;
    private Double buyPrice;
    private Double sellPrice;
    private String ownerName;
    private String ownerUUID;

    public Shop(Location location, ItemStack itemStack, int amount, Double buyPrice,
                Double sellPrice, String ownerName, String ownerUUID)
    {
        this(location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            location.getWorld().getName(),
            itemStack, amount, buyPrice,
            sellPrice, ownerName,
            ownerUUID);
    }

    public Shop(int x, int y, int z, String world, ItemStack itemStack, int amount,
                Double buyPrice, Double sellPrice, String ownerName, String ownerUUID)
    {
        Preconditions.checkArgument(
            buyPrice != null || sellPrice != null,
            "BuyPrice and SellPrice cannot both be null.");
        Preconditions.checkArgument(
            buyPrice == null || (buyPrice >= 0),
            "BuyPrice must be greater than or equal to 0.");
        Preconditions.checkArgument(
            sellPrice == null || (sellPrice >= 0),
            "SellPrice must be greater than or equal to 0.");
        Preconditions.checkArgument(
            amount > 0,
            "Amount must be greater than than 0.");

        this.x = x;
        this.y = y;
        this.z = z;
        this.world = Preconditions.checkNotNull(world, "World was null.");
        this.itemStack = Preconditions.checkNotNull(itemStack, "ItemStack was null.").clone();
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.amount = amount;
        this.ownerName = Preconditions.checkNotNull(ownerName, "OwnerName was null.");
        this.ownerUUID = Preconditions.checkNotNull(ownerUUID, "OwnerUUID was null.");

        short maxDurability = itemStack.getType().getMaxDurability();

        itemStack.setAmount(1);

        if(maxDurability != 0)
        {
            itemStack.setDurability(maxDurability);
        }
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public String getWorld()
    {
        return world;
    }

    public Location getLocation()
    {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public ItemStack getItemStack()
    {
        return itemStack.clone();
    }

    public int getAmount()
    {
        return amount;
    }

    public Double getBuyPrice()
    {
        return buyPrice;
    }

    public Double getSellPrice()
    {
        return sellPrice;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public String getOwnerUUID()
    {
        return ownerUUID;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("{");
        builder.append("x: ");
        builder.append(x);
        builder.append(", y: ");
        builder.append(y);
        builder.append(", z: ");
        builder.append(z);
        builder.append(", world: ");
        builder.append(world);
        builder.append(", itemStack: ");
        builder.append(itemStack.toString());
        builder.append(", amount: ");
        builder.append(amount);
        builder.append(", buyPrice: ");
        builder.append(buyPrice);
        builder.append(", sellPrice: ");
        builder.append(sellPrice);
        builder.append(", ownerName: ");
        builder.append(ownerName);
        builder.append(", ownerUUID: ");
        builder.append(ownerUUID);
        builder.append("}");
        return builder.toString();
    }
}
