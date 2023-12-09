package com.hiojam.rpgcore.customcrafting.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hiojam.rpgcore.customcrafting.CustomCrafting;
import com.hiojam.rpgcore.customcrafting.service.files.JsonRecipe;
import com.hiojam.rpgcore.customcrafting.utility.CustomRecipe;

import java.io.*;
import java.nio.file.Files;

public class FileService {
    private final CustomCrafting plugin;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public FileService(CustomCrafting plugin){
        this.plugin = plugin;
    }

    public void createRecipe(CustomRecipe recipe){
        String path = plugin.getDataFolder().getAbsolutePath()+"/recipes";
        File file = new File(path, recipe.getId()+".json");

        File filePath = new File(path);

        if (!file.exists()){

            if (!filePath.exists()){
                filePath.mkdirs();
            }

            //If file isn't a resource, create from scratch
            try {
                file.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        setDefaultOptionsFromClass(recipe, file);
    }

    public CustomRecipe getRecipe(File file){
        CustomRecipe d = null;
        try {
            FileReader reader = new FileReader(file);
            d = gson.fromJson(reader, CustomRecipe.class);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return d;
    }

    public void loadAllRecipes(){
        // creates a file object
        String path = plugin.getDataFolder().getAbsolutePath()+"/recipes";
        File filePath = new File(path);

        if (!filePath.exists()) return;

        File[] fileList = filePath.listFiles();
        if (fileList == null) return;
        if (fileList.length == 0) return;

        for (File file : fileList){

            JsonRecipe d = null;
            try {
                FileReader reader = new FileReader(file);
                d = gson.fromJson(reader, JsonRecipe.class);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }

            CustomRecipe recipe = null;
            try {
                assert d != null;
                recipe = d.toRecipe();
            } catch (IOException e) {
                e.printStackTrace();
            }

            CustomCrafting.getApi().getCraftingService().addRecipe(recipe);
        }
    }

    private void setDefaultOptionsFromClass(CustomRecipe recipe, File file){
        try {
            FileWriter f = new FileWriter(file);
            JsonRecipe jsonRecipe = new JsonRecipe(recipe);
            String json = gson.toJson(jsonRecipe);

            f.write(json);
            f.flush();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(final InputStream input, final File file) throws IOException {
        final OutputStream output = Files.newOutputStream(file.toPath());
        final byte[] buffer = new byte[8 * 1024];
        int length = input.read(buffer);
        while (length > 0) {
            output.write(buffer, 0, length);
            length = input.read(buffer);
        }
        input.close();
        output.close();
    }
}
