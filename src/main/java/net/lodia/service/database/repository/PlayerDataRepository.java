package net.lodia.service.database.repository;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.lodia.service.LodiaService;
import net.lodia.service.database.DataRepository;
import net.lodia.service.database.data.PlayerData;
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
public class PlayerDataRepository implements DataRepository<PlayerData> {

    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private final LodiaService service;

    public PlayerDataRepository(LodiaService service) {
        this.service = service;
    }

    @Override
    public PlayerData load(Player player) {
        UUID uuid = player.getUniqueId();

        try (PreparedStatement stmt = service.databaseHandler().connection().prepareStatement(
                "SELECT * FROM player_data WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            PlayerData data;
            if (rs.next()) {
                data = new PlayerData();
                data.uuid(uuid);
                data.name = rs.getString("name");
                data.addMoney(rs.getInt("money"));
                data.addCredits(rs.getInt("credits"));
                data.addKills(rs.getInt("kills"));
                data.addDeaths(rs.getInt("deaths"));
                data.addPlaytime(rs.getInt("playtime"));
            } else {
                data = new PlayerData();
                data.uuid(uuid);
                data.name = player.getName();
                save(data);
            }

            cache.put(uuid, data);
            return data;

        } catch (SQLException e) {
            logError("Failed to load player data", e);
            return null;
        }
    }

    public PlayerData loadByName(String name) {
        try (PreparedStatement stmt = service.databaseHandler().connection().prepareStatement(
                "SELECT * FROM player_data WHERE name = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            PlayerData data = new PlayerData();
            UUID dataUuid = UUID.fromString(rs.getString("uuid"));
            data.uuid(dataUuid);
            data.name = rs.getString("name");
            data.addMoney(rs.getInt("money"));
            data.addCredits(rs.getInt("credits"));
            data.addKills(rs.getInt("kills"));
            data.addDeaths(rs.getInt("deaths"));
            data.addPlaytime(rs.getInt("playtime"));

            cache.put(dataUuid, data);
            return data;

        } catch (SQLException e) {
            logError("Failed to load player data by name", e);
            return null;
        }
    }

    @Override
    public Map<UUID, PlayerData> cache() {
        return cache;
    }

    @Override
    public void save(PlayerData data) {
        try (PreparedStatement stmt = service.databaseHandler().connection().prepareStatement(
                "REPLACE INTO player_data (uuid, name, money, credits, kills, deaths, playtime) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, data.uuid().toString());
            stmt.setString(2, data.name());
            stmt.setInt(3, data.money());
            stmt.setInt(4, data.credits());
            stmt.setInt(5, data.kills());
            stmt.setInt(6, data.deaths());
            stmt.setInt(7, data.playtime());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logError("Failed to save player data", e);
        }
    }

    private void logError(String message, Exception e) {
        service.databaseHandler().service().getLogger().log(Level.SEVERE, message + ": " + e.getMessage());
    }
}