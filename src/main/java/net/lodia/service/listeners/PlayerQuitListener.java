package net.lodia.service.listeners;

import net.lodia.service.LodiaService;
import net.lodia.service.enums.PipelineMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerQuitListener(LodiaService service) implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        var player = e.getPlayer();
        service.pipelineHandler(player, PipelineMode.SAVE);
    }

}