package com.hiojam.rpgcore.customcrafting.gui;

import com.cryptomorin.xseries.XMaterial;
import com.hiojam.rpgcore.customcrafting.CustomCrafting;
import com.hiojam.rpgcore.customcrafting.service.CraftingService;
import com.hiojam.rpgcore.customcrafting.utility.CustomRecipe;
import dev.roxxion.api.Api;
import dev.roxxion.api.inventories.IPlayerMenuUtility;
import dev.roxxion.api.inventories.Menu;
import dev.roxxion.api.item.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CraftingTableGUI extends Menu {

    private final CraftingService craftingS = CustomCrafting.getApi().getCraftingService();
    private List<ItemStack> items;

    public CraftingTableGUI(IPlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public CraftingTableGUI(IPlayerMenuUtility playerMenuUtility, List<ItemStack> items) {
        this(playerMenuUtility);
        if(items != null) this.items = items;
    }

    @Override
    public boolean cancelInteractions(){
        return false;
    }

    @Override
    public boolean overridePlayerInv(){
        return false;
    }

    @Override
    public boolean interactPlayerInv() {
        return true;
    }

    @Override
    public String getMenuName() {
        return "§9Mesa de trabajo";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(Objects.requireNonNull(e.getClickedInventory()).getHolder() instanceof CraftingTableGUI){
            Player p = playerMenuUtility.getOwner();
            switch (e.getSlot()){
                case 10:
                case 11:
                case 12:
                case 19:
                case 20:
                case 21:
                case 28:
                case 29:
                case 30:
                    break;
                case 24:
                    e.setCancelled(true);
                    if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasLocalizedName() && e.getCurrentItem().getItemMeta().getLocalizedName().equals("result")){
                        break;
                    }
                    if((e.getCurrentItem() != null)){
                        p.getInventory().addItem(inventory.getItem(24));
                        ItemStack result = new ItemBuilder(XMaterial.BARRIER).setName("§cResultado").setLocalizedName("result").build();
                        inventory.setItem(24, result);
                    }
                    break;
                default: e.setCancelled(true); break;
            }

            if(e.getSlot() == 23){
                confirmCraft(); return;
            }
            if(e.getSlot() == 25){
                p.closeInventory();
                new CraftingListGUI(Api.getPlayerMenuUtility(p)).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        ItemStack blackPane = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName(" ").build();
        for(int i=0;i<10;i++) inventory.setItem(i, blackPane);

        for(int i=13;i<19;i++) inventory.setItem(i, blackPane);

        inventory.setItem(22, blackPane);

        for(int i=26;i<28;i++) inventory.setItem(i, blackPane);

        for(int i=31;i<45;i++) inventory.setItem(i, blackPane);

        ItemStack confirm = new ItemBuilder(XMaterial.LIME_DYE).setName("§aCraftear!").build();
        inventory.setItem(23, confirm);

        ItemStack result = new ItemBuilder(XMaterial.BARRIER).setName("§cResultado").setLocalizedName("result").build();
        inventory.setItem(24, result);

        ItemStack crafts = new ItemBuilder(XMaterial.FISHING_ROD).setName("§aLista de crafteos.").addFlags(ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build();
        inventory.setItem(25, crafts);

        if(this.items == null) return;

        for(int i = 0;i<9;i++) inventory.setItem(craftingS.getCraftingSlot(i), items.get(i));

    }

    private void confirmCraft(){

        List<ItemStack> items = new ArrayList<>();
        items.add(inventory.getItem(10)); items.add(inventory.getItem(11)); items.add(inventory.getItem(12));
        items.add(inventory.getItem(19)); items.add(inventory.getItem(20)); items.add(inventory.getItem(21));
        items.add(inventory.getItem(28)); items.add(inventory.getItem(29)); items.add(inventory.getItem(30));

        for(CustomRecipe recipe : CustomCrafting.getApi().getCraftingService().getRecipes()){
            boolean craft = recipe.craft(items);
            if(craft){
                Player p = playerMenuUtility.getOwner();

                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 3, 2);
                //CLEAN FIRST
                for(int i = 0;i<items.size();i++){
                    ItemStack item = items.get(i);

                    if(item == null) continue;
                    int amount = items.get(i).getAmount()-recipe.getItem(i).getAmount();
                    if(amount == 0){
                        inventory.setItem(craftingS.getCraftingSlot(i), null);
                        continue;
                    }

                    ItemStack last = new ItemBuilder(item).setAmount(amount).build();
                    inventory.setItem(craftingS.getCraftingSlot(i), last);
                }

                if(inventory.getItem(24) != null){
                    ItemStack item = inventory.getItem(24);
                    if(!(item.hasItemMeta() && item.getItemMeta().getLocalizedName().equals("result"))){
                        p.getInventory().addItem(inventory.getItem(24));
                    }
                }

                inventory.setItem(24, recipe.getResult());
                break;
            }
        }
    }
}
