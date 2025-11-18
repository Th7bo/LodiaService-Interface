package net.lodia.service.api;

import net.lodia.service.LodiaService;
import net.lodia.service.enums.Sounds;
import net.lodia.service.utils.MessageConverter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class TeleportAPI {

    private final Map<UUID, BukkitRunnable> teleportTasks = new HashMap<>();
    private final LodiaService service;

    public TeleportAPI(LodiaService service) {
        this.service = service;
    }

    public void teleport(Player player, Location location) {
        teleport(player, () -> location);
    }

    public void teleport(Player player, Player target) {
        teleport(player, target::getLocation);
    }

    public void teleport(Player player, Supplier<Location> targetSupplier) {
        var uuid = player.getUniqueId();

        if (service.combatAPI().combat().containsKey(player)) {
            player.sendRichMessage(service.prefix() + "<red>You can't teleport while in combat.");
            player.playSound(player, Sounds.ERROR.sound(), 1, 1);
            return;
        }

        if (teleportTasks.containsKey(uuid)) {
            player.sendRichMessage(service.prefix() + "<red>You are already teleporting.");
            player.playSound(player, Sounds.ERROR.sound(), 1, 1);
            return;
        }

        var startLocation = player.getLocation();
        int countdownSeconds = 5;

        BukkitRunnable task = new BukkitRunnable() {
            private int time = countdownSeconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTeleport(uuid);
                    return;
                }

                if (service.combatAPI().combat().containsKey(player)) {
                    cancelTeleport(uuid);
                    player.playSound(player, Sounds.ERROR.sound(), 1, 1);
                    return;
                }

                if (!player.getLocation().getWorld().equals(startLocation.getWorld())
                        || player.getLocation().distanceSquared(startLocation) > 1) {
                    cancelTeleport(uuid);
                    player.sendActionBar(MessageConverter.convert("<red>Teleport Canceled"));
                    player.playSound(player, Sounds.ERROR.sound(), 1, 1);
                    return;
                }

                if (time <= 0) {
                    cancelTeleport(uuid);
                    player.teleportAsync(targetSupplier.get());
                    player.sendActionBar(MessageConverter.convert("<green>Successfully Teleported"));
                    service.getServer().getScheduler().runTaskLater(service,
                            () -> player.playSound(player, Sounds.SUCCESS.sound(), 1, 1), 1);
                    return;
                }

                player.sendActionBar(MessageConverter.convert("<gray>Teleporting in " + service.mainColor() + time));
                player.playSound(player, Sounds.COUNTDOWN.sound(), 1, 1);
                time--;
            }

            private void cancelTeleport(UUID uuid) {
                teleportTasks.remove(uuid);
                this.cancel();
            }
        };

        task.runTaskTimer(service, 0, 20);
        teleportTasks.put(uuid, task);
    }
}