package com.hiojam.rpgcore.customcrafting.utility;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomRecipe {
    @Getter
    private final int id;
    private final ItemStack output;
    @Getter
    private final List<ItemStack> recipe = new ArrayList<>(Collections.nCopies(9, null));

    public CustomRecipe(int id, ItemStack resultItem){
        this.id = id;
        this.output = resultItem;
    }

    public ItemStack getResult() {
        return this.output.clone();
    }

    public void setItem(int slot, ItemStack item){
        this.recipe.set(slot, item);
    }

    public ItemStack getItem(int slot){
        return this.recipe.get(slot);
    }

    public boolean craft(List<ItemStack> items){
        boolean isSame = false;
        for(int i=0;i<items.size();i++){
            if((items.get(i) == null) && (recipe.get(i) == null)) continue;
            if(items.get(i) == null){
                isSame = false;
                break;
            }
            if(recipe.get(i) == null){
                isSame = false;
                break;
            }

            ItemStack item = items.get(i).clone();
            ItemStack needed = recipe.get(i).clone();

            if(item.getAmount() >= needed.getAmount()){
                item.setAmount(needed.getAmount());

                NBTItem nbt1 = new NBTItem(item);
                if(nbt1.hasNBTData()){
                    nbt1.clearCustomNBT();
                    nbt1.applyNBT(item);
                }

                NBTItem nbt2 = new NBTItem(needed);
                if(nbt2.hasNBTData()){
                    nbt2.clearCustomNBT();
                    nbt2.applyNBT(needed);
                }

                if(item.equals(needed)){
                    isSame = true;
                    continue;
                }
            }
            isSame = false;
            break;
        }
        return isSame;
    }

    public List<ItemStack> getItemsNeeded(){
        HashMap<ItemStack, Integer> itemsInRecipe = new HashMap<>();

        for(ItemStack item : getRecipe()){
            if(item == null) continue;
            ItemStack newI = item.clone();
            NBTItem nbt = new NBTItem(newI);
            if(nbt.hasNBTData()){
                nbt.clearCustomNBT();
                nbt.applyNBT(newI);
            }

            newI.setAmount(1);
            if(itemsInRecipe.containsKey(newI)){
                itemsInRecipe.replace(newI, itemsInRecipe.get(newI)+item.getAmount());
            }else{
                itemsInRecipe.put(newI, item.getAmount());
            }
        }

        List<ItemStack> items = new ArrayList<>(itemsInRecipe.keySet());

        for(int i=0;i<items.size();i++){
            ItemStack item = items.get(i);
            int amount = itemsInRecipe.get(item);
            item.setAmount(amount);
            items.set(i, item);
        }
        return items;
    }
}
