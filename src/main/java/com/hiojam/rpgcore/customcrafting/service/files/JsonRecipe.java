package com.hiojam.rpgcore.customcrafting.service.files;

import com.hiojam.rpgcore.customcrafting.utility.CustomRecipe;
import dev.roxxion.api.utils.Serializer;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class JsonRecipe {

    private final int id;
    @Getter
    private final String resultItem, recipe;

    public JsonRecipe(CustomRecipe recipe){
        this.id = recipe.getId();

        Serializer s = new Serializer();
        this.resultItem = s.itemStackArrayToBase64(new ItemStack[]{recipe.getResult()});
        this.recipe = s.itemStackArrayToBase64(recipe.getRecipe().toArray(new ItemStack[0]));
    }

    public CustomRecipe toRecipe() throws IOException {
        Serializer s = new Serializer();
        ItemStack result = s.itemStackArrayFromBase64(resultItem)[0];

        CustomRecipe customRecipe = new CustomRecipe(id, result);
        ItemStack[] items = s.itemStackArrayFromBase64(recipe);

        for(int i = 0; i<9; i++){
            customRecipe.setItem(i, items[i]);
        }

        return customRecipe;
    }
}
