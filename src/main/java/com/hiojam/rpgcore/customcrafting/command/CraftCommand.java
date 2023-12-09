package com.hiojam.rpgcore.customcrafting.command;

import com.hiojam.rpgcore.customcrafting.gui.CraftingCreatorGUI;
import dev.roxxion.api.Api;
import dev.roxxion.api.commands.Command;
import dev.roxxion.api.commands.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CraftCommand {

    @Command(name = "craft")
    public boolean craft(CommandContext context){
        CommandSender sender = context.getSender();
        if(!sender.hasPermission("craft.recipes")) return false;

        if (context.getArgs().length == 0) {
            sender.sendMessage("§cUso: /"+context.getCommand().getName()+" recipes");
            return true;
        }

        if (context.getArg(0).equalsIgnoreCase("recipes")) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage("§cSólo puedes acceder a esto mediante el juego.");
                return true;
            }
            new CraftingCreatorGUI(Api.getPlayerMenuUtility(p)).open();
            return true;
        }
        return false;
    }
}
