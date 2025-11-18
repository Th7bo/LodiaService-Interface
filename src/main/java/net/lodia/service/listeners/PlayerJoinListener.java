package net.lodia.service.listeners;

import net.lodia.service.LodiaService;
import net.lodia.service.enums.Locations;
import net.lodia.service.enums.PipelineMode;

import net.lodia.service.enums.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record PlayerJoinListener(LodiaService service) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        var player = event.getPlayer();
        var playerWorld = player.getWorld();

        var spawnLocation = Locations.SPAWN.location();
        var afkLocation = Locations.AFK.location();

        service.pipelineHandler(player, PipelineMode.LOAD);

        if (!player.hasPlayedBefore() || playerWorld.equals(spawnLocation.getWorld())) {
            player.teleport(spawnLocation);
            return;
        }

        if (playerWorld.equals(afkLocation.getWorld())) {
            player.teleport(afkLocation);
            return;
        }

        player.playSound(player, Sounds.SUCCESS.sound(), 1, 2);
    }
}