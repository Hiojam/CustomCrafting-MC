package com.hiojam.rpgcore.customcrafting;

import com.hiojam.rpgcore.customcrafting.api.Api;
import com.hiojam.rpgcore.customcrafting.command.CraftCommand;
import com.hiojam.rpgcore.customcrafting.event.listener.CraftingTableListener;
import com.hiojam.rpgcore.customcrafting.service.CraftingService;
import com.hiojam.rpgcore.customcrafting.service.FileService;
import com.hiojam.rpgcore.customcrafting.utility.CustomRecipe;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomCrafting extends JavaPlugin {
    @Getter
    private static Api api;

    @Override
    public void onEnable() {
        api = new Api(this);
        dev.roxxion.api.Api.getCommandService().registerCommands(new CraftCommand());
        api.getFileService().loadAllRecipes();
        getServer().getPluginManager().registerEvents(new CraftingTableListener(), this);
    }

    @Override
    public void onDisable() {
        CraftingService craftingS = api.getCraftingService();
        FileService fileS = api.getFileService();

        for (CustomRecipe recipe : craftingS.getRecipes()) {
            fileS.createRecipe(recipe);
        }
    }
}
