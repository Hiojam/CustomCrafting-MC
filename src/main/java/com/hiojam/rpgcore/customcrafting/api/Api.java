package com.hiojam.rpgcore.customcrafting.api;

import com.hiojam.rpgcore.customcrafting.CustomCrafting;
import com.hiojam.rpgcore.customcrafting.service.CraftingService;
import com.hiojam.rpgcore.customcrafting.service.FileService;
import lombok.Getter;

@Getter
public final class Api {

    private final CraftingService craftingService;
    private final FileService fileService;

    public Api(CustomCrafting plugin){
        this.craftingService = new CraftingService();
        this.fileService = new FileService(plugin);
    }
}
