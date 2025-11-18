package net.lodia.service.api;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.lodia.service.LodiaService;
import net.lodia.service.utils.MessageConverter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

@Getter
@Accessors(fluent = true)
public class CombatAPI {

    private final Map<Player, BukkitTask> combat = new HashMap<>();
    private final LodiaService service;

    public CombatAPI(LodiaService service) {
        this.service = service;
    }

    public void putPlayerCombat(Player player) {
        if (combat.containsKey(player)) {
            combat.get(player).cancel();
            combat.remove(player);
        }

        var task = new BukkitRunnable() {
            int time = 20;

            @Override
            public void run() {
                if (!player.isOnline() || time <= 0) {
                    combat.remove(player);
                    cancel();
                    return;
                }

                player.sendActionBar(MessageConverter.convert("<red>In Combat " + time));
                time--;
            }
        }.runTaskTimer(service, 0, 20);

        combat.put(player, task);
    }
}