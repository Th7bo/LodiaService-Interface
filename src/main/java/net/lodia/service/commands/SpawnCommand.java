package net.lodia.service.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.lodia.service.LodiaService;
import net.lodia.service.database.data.CratesData;
import net.lodia.service.enums.Locations;
import org.bukkit.entity.Player;

public class SpawnCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

//        CratesData data = LodiaService.service().crateDataRepository().getOrLoad(player);
//        data.addAllCreate(5);
//        System.out.println("Set crate to " + data.allCreate());
//        used to test the database

        if (Locations.isProtectedWorld(player.getWorld().getName())) {
            player.teleport(Locations.SPAWN.location());
            return;
        }
    }
}