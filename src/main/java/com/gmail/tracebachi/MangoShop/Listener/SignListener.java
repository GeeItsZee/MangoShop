package com.gmail.tracebachi.MangoShop.Listener;

import com.gmail.tracebachi.MangoShop.Database.ShopDatabase;
import com.gmail.tracebachi.MangoShop.Interface.Registerable;
import com.gmail.tracebachi.MangoShop.Interface.Shutdownable;
import com.gmail.tracebachi.MangoShop.MangoShop;
import com.gmail.tracebachi.MangoShop.Storage.Shop;
import com.gmail.tracebachi.MangoShop.Storage.ShopBuilder;
import com.gmail.tracebachi.MangoShop.Utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/19/16.
 */
public class SignListener implements Listener, Registerable, Shutdownable
{
    private static final BlockFace[] NORTH_EAST_SOUTH_WEST_FACES = {
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST };

    private MangoShop plugin;

    public SignListener(MangoShop plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void register()
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void unregister()
    {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void shutdown()
    {
        unregister();
        plugin = null;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShopSignInteract(PlayerInteractEvent event)
    {
        Action action = event.getAction();

        if(action != Action.RIGHT_CLICK_BLOCK) { return; }

        Block clickedBlock = event.getClickedBlock();
        Material clickedBlockType = clickedBlock.getType();
        Player player = event.getPlayer();

        if(clickedBlockType != Material.WALL_SIGN) { return; }

        Sign signState = (Sign) clickedBlock.getState();
        String[] lines = trimLines(signState.getLines());
        ShopDatabase shopDatabase = plugin.getShopDatabase();

        if(lines[0].equals("[Admin Shop]") || lines[0].equals("[Shop]"))
        {
            Shop shop = shopDatabase.getShop(clickedBlock.getLocation());

            if(shop == null)
            {
                player.sendMessage("Invalid shop sign with itemstack");
                clickedBlock.breakNaturally();
                return;
            }

            openShopGui(shop, player);
        }
        else if(lines[0].equalsIgnoreCase("Shop"))
        {
            if(!player.hasPermission("MangoShop.Create"))
            {
                player.sendMessage("You do not have permission to build a shop.");
                return;
            }

            ShopBuilder shopBuilder = new ShopBuilder();

            if(!checkItemInHand(shopBuilder, player)) { return; }

            if(!checkAmountAndPrices(shopBuilder, lines, player)) { return; }

            if(!checkForNearbyShops(clickedBlock, signState, player)) { return; }

            Shop shop = shopBuilder
                .setLocation(clickedBlock.getLocation())
                .setOwnerName(player.getName())
                .setOwnerUUID(player.getUniqueId().toString())
                .build();

            convertSignToShop(shop, signState, false);

            shopDatabase.createShop(shop);
        }
        else if(lines[0].equalsIgnoreCase("Admin Shop"))
        {
            if(!player.hasPermission("MangoShop.AdminShop.Create"))
            {
                player.sendMessage("You do not have permission to build an admin shop.");
                return;
            }

            ShopBuilder shopBuilder = new ShopBuilder();

            if(!checkItemInHand(shopBuilder, player)) { return; }

            if(!checkAmountAndPrices(shopBuilder, lines, player)) { return; }

            Shop shop = shopBuilder
                .setLocation(clickedBlock.getLocation())
                .setOwnerName("AdminShop")
                .setOwnerUUID("AdminShop")
                .build();

            convertSignToShop(shop, signState, true);

            shopDatabase.createShop(shop);
        }
    }

    private boolean checkItemInHand(ShopBuilder builder, Player player)
    {
        ItemStack itemInPlayerHand = player.getInventory().getItemInMainHand();

        if(itemInPlayerHand == null || itemInPlayerHand.getType() == Material.AIR)
        {
            player.sendMessage("You need an actual item in your hand");
            return false;
        }

        builder.setItemStack(itemInPlayerHand);
        return true;
    }

    private boolean checkAmountAndPrices(ShopBuilder shopBuilder, String[] lines, Player player)
    {
        Integer amount = NumberUtils.parseInt(lines[1]);
        Double buyPrice = NumberUtils.parseDouble(lines[2]);
        Double sellPrice = NumberUtils.parseDouble(lines[3]);

        if(amount == null)
        {
            player.sendMessage("Amount must be set");
            return false;
        }

        if(amount < 0)
        {
            player.sendMessage("Amount must be bigger than 0");
            return false;
        }

        if(buyPrice == null && sellPrice == null)
        {
            player.sendMessage("A buy or sell must be set.");
            return false;
        }

        if(buyPrice != null && buyPrice < 0)
        {
            player.sendMessage("Buy price must be bigger than or equal to 0.");
            return false;
        }

        if(sellPrice != null && sellPrice < 0)
        {
            player.sendMessage("Sell price must be bigger than or equal to 0");
            return false;
        }

        shopBuilder
            .setAmount(amount)
            .setBuyPrice(buyPrice)
            .setSellPrice(sellPrice);
        return true;
    }

    private boolean checkForNearbyShops(Block clickedBlock, Sign signState, Player player)
    {
        org.bukkit.material.Sign signData = (org.bukkit.material.Sign) signState.getData();
        BlockFace attachedFace = signData.getAttachedFace();
        Block attachedBlock = clickedBlock.getRelative(attachedFace);
        Material attachedBlockType = attachedBlock.getType();

        if(attachedBlockType != Material.CHEST && attachedBlockType != Material.TRAPPED_CHEST)
        {
            player.sendMessage("Shops can only be on chests");
            return false;
        }

        Shop existingShop = getExistingShop(attachedBlock, attachedFace.getOppositeFace());

        if(existingShop != null)
        {
            String playerUUID = player.getUniqueId().toString();

            if(!playerUUID.equals(existingShop.getOwnerUUID()))
            {
                player.sendMessage("Cant make a shop on a chest owned by another person");
                return false;
            }
        }

        return true;
    }

    private void convertSignToShop(Shop shop, Sign signState, boolean isAdminShop)
    {
        if(isAdminShop)
        {
            signState.setLine(0, "[Admin Shop]");
        }
        else
        {
            signState.setLine(0, "[Shop]");
        }

        signState.setLine(1, String.valueOf(shop.getAmount()));

        Double buyPrice = shop.getBuyPrice();
        Double sellPrice = shop.getSellPrice();

        if(buyPrice != null && sellPrice != null)
        {
            signState.setLine(2,
                "B" + NumberUtils.stripTrailingDecimal(buyPrice) +
                " : " +
                "S" + NumberUtils.stripTrailingDecimal(sellPrice));
        }
        else if(buyPrice != null)
        {
            signState.setLine(2, "B" + NumberUtils.stripTrailingDecimal(buyPrice));
        }
        else
        {
            signState.setLine(2, "S" + NumberUtils.stripTrailingDecimal(sellPrice));
        }

        ItemStack itemStack = shop.getItemStack();
        Material type = itemStack.getType();
        StringBuilder itemDesc = new StringBuilder(type.toString());

        if(type.getMaxDurability() == 0)
        {
            itemDesc.append(':');
            itemDesc.append(itemStack.getDurability());
        }

        if(itemStack.hasItemMeta())
        {
            itemDesc.append(":#");
        }

        signState.setLine(3, itemDesc.toString());
        signState.update(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShopChestBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material blockType = block.getType();

        if(blockType != Material.WALL_SIGN) { return; }

        Sign signState = (Sign) block.getState();
        String firstLine = signState.getLine(0);

        if(!firstLine.equals("[Shop]") && !firstLine.equals("[Admin Shop]")) { return; }

        ShopDatabase shopDatabase = plugin.getShopDatabase();
        Shop shop = shopDatabase.getShop(block.getLocation());

        if(shop == null) { return; }

        String ownerUUID = shop.getOwnerUUID();

        if(ownerUUID.equals("AdminShop"))
        {
            if(!player.hasPermission("MangoShop.AdminShop.Create"))
            {
                player.sendMessage("You do not have permission to break admin shop signs.");
                event.setCancelled(true);
                return;
            }

            shopDatabase.removeShop(block.getLocation());
            return;
        }

        if(!ownerUUID.equals(player.getUniqueId().toString()))
        {
            if(!player.hasPermission("MangoShop.Admin.Break"))
            {
                player.sendMessage("You do not have permission to break admin shop signs.");
                event.setCancelled(true);
                return;
            }
        }

        shopDatabase.removeShop(block.getLocation());
    }

    private void openShopGui(Shop shop, Player player)
    {
        // TODO Need a custom inventory class that can protect items/fire events
        Inventory inventory = Bukkit.createInventory(null, 9, "MangoShop");

        inventory.addItem(shop.getItemStack());

        player.openInventory(inventory);
    }

    private Shop getExistingShop(Block chestBlock, BlockFace ignored)
    {
        ShopDatabase shopDatabase = plugin.getShopDatabase();

        for(BlockFace faceA : NORTH_EAST_SOUTH_WEST_FACES)
        {
            // Ignore the block that the search started from
            if(faceA == ignored) { continue; }

            Block blockA = chestBlock.getRelative(faceA);

            // If blockA is the same chest type as chestBlock
            if(blockA.getType() == chestBlock.getType())
            {
                for(BlockFace faceB : NORTH_EAST_SOUTH_WEST_FACES)
                {
                    // Ignore the block that the search started from
                    if(faceB == faceA.getOppositeFace()) { continue; }

                    Block blockB = blockA.getRelative(faceB);

                    if(blockB.getType() == Material.WALL_SIGN)
                    {
                        Shop found = shopDatabase.getShop(blockB.getLocation());

                        if(found != null) { return found; }
                    }
                }

            }
            else if(blockA.getType() == Material.WALL_SIGN)
            {
                Shop found = shopDatabase.getShop(blockA.getLocation());

                if(found != null) { return found; }
            }
        }

        return null;
    }

    private String[] trimLines(String[] lines)
    {
        for(int i = 0; i < lines.length; i++)
        {
            lines[i] = lines[i].trim();
        }

        return lines;
    }

    private boolean isShopSignWithItemStack(String[] lines)
    {
        if(!lines[0].equals("[Shop]")) { return false; }

        Integer amount = NumberUtils.parseInt(lines[1]);

        if(amount == null || amount <= 0) { return false; }

        if(lines[2].isEmpty()) { return false; }

        if(lines[3].isEmpty()) { return false; }

        return true;
    }
}
