package com.hiojam.rpgcore.customcrafting.event.listener;

import com.hiojam.rpgcore.customcrafting.gui.CraftingTableGUI;
import dev.roxxion.api.Api;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CraftingTableListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(!(e.getInventory().getHolder() instanceof CraftingTableGUI)) return;
        if(!(e.getPlayer() instanceof Player p)) return;

        Inventory inv = e.getInventory();

        for (int i = 10; i < 13; i++)
            p.getInventory().addItem(inv.getItem(i));

        for (int i = 19; i < 22; i++)
            p.getInventory().addItem(inv.getItem(i));

        for (int i = 28; i < 31; i++)
            p.getInventory().addItem(inv.getItem(i));

        if(inv.getItem(24) == null) return;

        ItemStack item = inv.getItem(24);
        if(item.hasItemMeta() && item.getItemMeta().hasLocalizedName() && item.getItemMeta().getLocalizedName().equals("result")) return;

        p.getInventory().addItem(inv.getItem(24));
    }

    @EventHandler
    public void customCraftingTable(PlayerInteractEvent e){
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(!Objects.requireNonNull(e.getClickedBlock()).getType().equals(Material.CRAFTING_TABLE)) return;
        e.setCancelled(true);

        new CraftingTableGUI(Api.getPlayerMenuUtility(e.getPlayer())).open();
    }
}
