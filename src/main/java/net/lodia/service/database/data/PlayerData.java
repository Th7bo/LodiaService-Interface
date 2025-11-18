package net.lodia.service.database.data;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
public class PlayerData {

    public UUID uuid;
    public String name;
    private int money;
    private int credits;
    private int kills;
    private int deaths;
    private int playtime;

    public PlayerData() {
        this.money = 0;
        this.credits = 0;
        this.kills = 0;
        this.deaths = 0;
        this.playtime = 0;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }
    public void removeMoney(int amount) {
        this.money -= amount;
    }

    public void addCredits(int amount) {
        this.credits += amount;
    }
    public void removeCredits(int amount) {
        this.credits -= amount;
    }

    public void addKills(int amount) {
        this.kills += amount;
    }
    public void addDeaths(int amount) {
        this.deaths += amount;
    }
    public void addPlaytime(int amount) {
        this.playtime += amount;
    }
}