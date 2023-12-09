package com.hiojam.rpgcore.customcrafting.service;

import com.hiojam.rpgcore.customcrafting.utility.CustomRecipe;

import java.util.HashMap;
import java.util.List;

public class CraftingService {
    private final HashMap<Integer, CustomRecipe> recipes = new HashMap<>();

    public List<CustomRecipe> getRecipes() {
        return recipes.values().stream().toList();
    }

    public CustomRecipe getRecipe(int id){
        return this.recipes.getOrDefault(id, null);
    }

    public void addRecipe(CustomRecipe recipe){
        this.recipes.put(recipe.getId(), recipe);
    }

    public void removeRecipe(int id){
        this.recipes.remove(id);
    }

    public void clearRecipes(){
        this.recipes.clear();
    }

    public int getCraftingSlot(int slot) {
        return switch (slot) {
            case 0 -> 10;
            case 1 -> 11;
            case 2 -> 12;
            case 3 -> 19;
            case 4 -> 20;
            case 5 -> 21;
            case 6 -> 28;
            case 7 -> 29;
            case 8 -> 30;
            default -> 0;
        };
    }

    public void reloadRecipes(){
        this.recipes.clear();
        // TODO: 19/1/2022 ACTUALIZAR DESDE ARCHIVOS
    }
}
