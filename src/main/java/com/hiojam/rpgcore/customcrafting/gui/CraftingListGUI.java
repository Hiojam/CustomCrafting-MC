package com.hiojam.rpgcore.customcrafting.gui;

import com.cryptomorin.xseries.XMaterial;
import com.hiojam.rpgcore.customcrafting.CustomCrafting;
import com.hiojam.rpgcore.customcrafting.service.CraftingService;
import com.hiojam.rpgcore.customcrafting.utility.CustomRecipe;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.roxxion.api.Api;
import dev.roxxion.api.inventories.IPlayerMenuUtility;
import dev.roxxion.api.inventories.PaginatedMenu;
import dev.roxxion.api.item.ItemBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CraftingListGUI extends PaginatedMenu {

    private final CraftingService craftingS;

    public CraftingListGUI(IPlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        craftingS = CustomCrafting.getApi().getCraftingService();
    }

    @Override
    public String getMenuName() {
        return "§5Lista de crafteos";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e){
        Player p = playerMenuUtility.getOwner();
        List<CustomRecipe> recipes = craftingS.getRecipes();

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;

        if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasLocalizedName()){

            if(e.getCurrentItem().getItemMeta().getLocalizedName().equals("error")) return;

            int id = Integer.parseInt(e.getCurrentItem().getItemMeta().getLocalizedName());
            CustomRecipe recipe = craftingS.getRecipe(id);

            List<ItemStack> items = recipe.getItemsNeeded();

            boolean contains = false;
            for (ItemStack item : items) {

                if (containsAtLeast(item, item.getAmount())) {
                    contains = true;
                    continue;
                }

                contains = false;
                break;
            }

            if(contains){

                List<ItemStack> itemList = new ArrayList<>(this.items.keySet());
                if(!itemList.isEmpty()){
                    for (ItemStack item : itemList) {
                        if(!this.items.containsKey(item)) continue;

                        if (this.items.get(item) == 0) {
                            p.getInventory().remove(item);
                        } else {
                            item.setAmount(this.items.get(item));
                        }
                    }
                }

                if(e.getClick().isRightClick()){
                    p.closeInventory();
                    new CraftingTableGUI(Api.getPlayerMenuUtility(p), recipe.getRecipe()).open();
                    return;
                }

                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 3, 2);
                p.getInventory().addItem(recipe.getResult());
                return;
            }

            if(!(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasLocalizedName() && e.getCurrentItem().getItemMeta().getLocalizedName().equals("error"))){
                ItemStack pre = e.getCurrentItem();

                ItemStack error = new ItemBuilder(XMaterial.BARRIER).setName("§cMateriales insuficientes!").setLocalizedName("error").build();
                inventory.setItem(e.getSlot(), error);

                Api.getTaskService().runTaskLaterAsynchronously(() -> {
                    inventory.setItem(e.getSlot(), pre);
                }, 35);
            }
            return;
        }

        if(e.getSlot() == 45){
            p.closeInventory();
            new CraftingTableGUI(Api.getPlayerMenuUtility(p)).open();
        }

        addDefaultButtonsListener(recipes, e);
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        List<CustomRecipe> recipes = craftingS.getRecipes();

        ItemStack volver = createCustomSkull( "§a§lVolver","5f133e91919db0acefdc272d67fd87b4be88dc44a958958824474e21e06d53e6");
        inventory.setItem(45 , volver);

        addPagedItems(recipes, () -> {
            CustomRecipe recipe = recipes.get(index);
            List<ItemStack> items = recipe.getItemsNeeded();

            List<String> lore = new ArrayList<>();
            lore.add("§b§m                                   ");
            for(ItemStack item : items){
                lore.add(" §7x"+item.getAmount()+" §f"+(item.hasItemMeta()
                        ? Objects.requireNonNull(item.getItemMeta()).getDisplayName()
                        : StringUtils.capitalize(XMaterial.matchXMaterial(item).toString())));
            }
            lore.add("");
            lore.add(" §aLC §7Crafteo rápido.");
            lore.add(" §cRC §7Abrir mesa de trabajo.");
            lore.add("§b§m                                   ");

            ItemStack item = new ItemBuilder(recipe.getResult()).setName("§b§lReceta §a"+recipe.getId()).setLore(lore)
                    .setLocalizedName(recipe.getId()+"")
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS).build();

            inventory.addItem(item);
        });

        //PARA QUE NO SE VEA LA FLECHA SIGUIENTE
        if ((index + 1) >= recipes.size()) {
            inventory.setItem( 50 , super.FILLER_GLASS );
        }
    }

    private final HashMap<ItemStack, Integer> items = new HashMap<>();

    private boolean containsAtLeast(ItemStack item, int amount) {
        int originalA = amount;

        if (item == null) {
            return false;
        } else if (amount <= 0) {
            return true;
        } else {
            ItemStack[] items;
            int length = (items = playerMenuUtility.getOwner().getInventory().getContents()).length;

            for(int a = 0; a < length; ++a) {
                ItemStack i = items[a];
                if(i == null) continue;
                ItemStack i2 = i.clone();

                NBTItem nbt = new NBTItem(i2);
                if(nbt.hasNBTData()){
                    nbt.clearCustomNBT();
                    nbt.applyNBT(i2);
                }
                if (item.isSimilar(i2) && (amount -= i2.getAmount()) <= 0) {
                    if(i.getAmount()-amount <= 0){
                        this.items.put(i, 0);
                        //p.getInventory().remove(i);
                    }else{
                        this.items.put(i, i.getAmount()-originalA);
                        //i.setAmount(i.getAmount()-originalA);
                    }
                    return true;
                }
            }

            return false;
        }
    }
}
