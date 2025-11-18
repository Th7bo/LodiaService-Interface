package net.lodia.service.database;

import net.lodia.service.LodiaService;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

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
                    // Save all data from all repositories and should be batched in some way
                    for (var repository : service.dataRepositories()) {
                        var cacheValues = new ArrayList<>(repository.cache().values());
                        for (var data : cacheValues) {
                            saveDataUnchecked(repository, data);
                        }
                    }
                });
            }
            
            @SuppressWarnings("unchecked")
            private void saveDataUnchecked(
                    DataRepository<?> repository,
                    IPlayerDataObject data) {
                ((DataRepository<IPlayerDataObject>) repository).save(data);
            }
        }.runTaskTimerAsynchronously(service, 0L, 200L);
    }
}