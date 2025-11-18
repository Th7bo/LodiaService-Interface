package net.lodia.service.database;

import net.lodia.service.LodiaService;
import org.bukkit.scheduler.BukkitRunnable;

public record DataSaveHandler(LodiaService service) {

    public DataSaveHandler(LodiaService service) {
        this.service = service;
        start();
    }

    private void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                service.databaseHandler().executor().submit(() -> {
                    service.playerDataRepository().cache().values().forEach(service.playerDataRepository()::save);
                    service.crateDataRepository().cache().values().forEach(service.crateDataRepository()::save);
                });
            }
        }.runTaskTimerAsynchronously(service, 0L, 200L);
    }
}