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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CraftingCreatorGUI extends Menu {
    private final CraftingService craftingS = CustomCrafting.getApi().getCraftingService();

    public CraftingCreatorGUI(IPlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
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
        return "§9Creador de crafteos";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(Objects.requireNonNull(e.getClickedInventory()).getHolder() instanceof CraftingCreatorGUI){
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
                case 24:
                    break;
                default: e.setCancelled(true); break;
            }

            if(e.getSlot() == 23){
                boolean a = createCraft();
                if(!a){
                    inventory.setItem(23, new ItemBuilder(XMaterial.BARRIER).setName("§cEl item resultante no puede ser nulo.").build());
                }else{
                    inventory.setItem(23, new ItemBuilder(XMaterial.LIME_DYE).setName("§aCreado exitosamente!").build());

                    for (int i = 0; i < 9; i++) inventory.setItem(craftingS.getCraftingSlot(i), null);

                    inventory.setItem(24, null);
                }
                Api.getTaskService().runTaskLaterAsynchronously(()->{
                    if(inventory != null) inventory.setItem(23, new ItemBuilder(XMaterial.LIME_DYE).setName("§aAñadir crafteo.").build());
                }, 40L);
            }
        }
    }

    @Override
    public void setMenuItems() {
        ItemStack blackPane = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName(" ").build();
        for(int i=0;i<10;i++) inventory.setItem(i, blackPane);

        for(int i=13;i<19;i++) inventory.setItem(i, blackPane);

        inventory.setItem(22, blackPane);

        for(int i=25;i<28;i++) inventory.setItem(i, blackPane);

        for(int i=31;i<45;i++) inventory.setItem(i, blackPane);

        ItemStack confirm = new ItemBuilder(XMaterial.LIME_DYE).setName("§aAñadir crafteo.").build();
        inventory.setItem(23, confirm);
    }

    private boolean createCraft(){
        ItemStack result = inventory.getItem(24);
        if(result == null) return false;

        int id = 0;

        for(CustomRecipe recipe : CustomCrafting.getApi().getCraftingService().getRecipes()){
            if(recipe.getId() > id) id = recipe.getId();
        }

        CustomRecipe recipe = new CustomRecipe(id+1, result);

        for (int i = 0; i < 9; i++) recipe.setItem(i, inventory.getItem(craftingS.getCraftingSlot(i)));

        CustomCrafting.getApi().getCraftingService().addRecipe(recipe);
        playerMenuUtility.getOwner().playSound(playerMenuUtility.getOwner().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3, 2);
        return true;
    }
}
