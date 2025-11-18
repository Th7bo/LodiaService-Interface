package net.lodia.service.database.repository;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.lodia.service.LodiaService;
import net.lodia.service.database.data.CratesData;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Getter
@Accessors(fluent = true)
public class CrateDataRepository {

    private final Map<UUID, CratesData> cache = new ConcurrentHashMap<>();
    private final LodiaService service;

    public CrateDataRepository(LodiaService service) {
        this.service = service;
    }

    public CratesData load(Player player) {
        UUID uuid = player.getUniqueId();

        try (PreparedStatement stmt = service.databaseHandler().connection().prepareStatement(
                "SELECT * FROM crates_data WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            CratesData data;
            if (rs.next()) {
                data = new CratesData();
                data.uuid = uuid;
                data.addAllCreate(rs.getInt("allCrate"));
                data.addBasicCreate(rs.getInt("basicCrate"));
                data.addRareCreate(rs.getInt("rareCrate"));
                data.addUltraCreate(rs.getInt("ultraCrate"));
            } else {
                data = new CratesData();
                data.uuid = uuid;
                save(data);
            }

            cache.put(uuid, data);
            return data;

        } catch (SQLException e) {
            logError("Failed to load crates data", e);
            return null;
        }
    }

    public void save(CratesData data) {
        try (PreparedStatement stmt = service.databaseHandler().connection().prepareStatement(
                "REPLACE INTO crates_data (uuid, allCrate, basicCrate, rareCrate, ultraCrate) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, data.uuid().toString());
            stmt.setInt(2, data.allCreate());
            stmt.setInt(3, data.basicCreate());
            stmt.setInt(4, data.rareCrate());
            stmt.setInt(5, data.ultraCrate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logError("Failed to save crates data", e);
        }
    }

    private void logError(String message, Exception e) {
        service.databaseHandler().service().getLogger().log(Level.SEVERE, message + ": " + e.getMessage());
    }
}