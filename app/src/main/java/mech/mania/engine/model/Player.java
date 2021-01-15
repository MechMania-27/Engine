package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.config.Config;

import java.util.*;

public class Player {
    @Expose
    private String name;
    @Expose
    private Position position;
    @Expose
    private ItemType item;
    @Expose
    private UpgradeType upgrade;
    @Expose
    private double money;
    @Expose
    private Map<CropType, Integer> seedInventory = new HashMap<>();
    @Expose
    private List<Crop> harvestedInventory = new ArrayList<>();

    private Config gameConfig;

    public Player(String name, Position position, ItemType item, UpgradeType upgrade, int money, Config gameConfig) {
        this.gameConfig = gameConfig;
        this.name = name;
        this.position = position;
        this.item = item;
        this.upgrade = upgrade;
        this.money = money;
        for (CropType type : CropType.values()) {
            seedInventory.put(type, 0);
        }
    }

    public Player(Player other) {
        this.gameConfig = other.gameConfig;
        this.name = other.name;
        this.position = new Position(other.position);
        this.item = other.item;
        this.upgrade = other.upgrade;
        this.money = other.money;
        seedInventory = new HashMap<>();
        seedInventory.putAll(other.seedInventory);
        this.seedInventory = new HashMap<>(other.seedInventory);
        this.harvestedInventory = new ArrayList<>(other.harvestedInventory);
    }

    public void sellInventory() {
        if (harvestedInventory.isEmpty()) {
            return;
        }
        Iterator<Crop> inventoryIter = harvestedInventory.iterator();
        while (inventoryIter.hasNext()) {
            Crop crop = inventoryIter.next();
            money += crop.getValue();
            inventoryIter.remove();
        }
    }

    public double getMoney() {
        return money;
    }

    public UpgradeType getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(UpgradeType upgradeType) {
        this.upgrade = upgradeType;
    }

    public ItemType getItem() {
        return item;
    }

    public void setItem(ItemType item) {
        this.item = item;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // TODO stub for now
    public int getHarvestRadius() {
        return gameConfig.HARVEST_RADIUS;
    }

    // TODO stub for now
    public int getPlantingRadius() {
        return gameConfig.PLANT_RADIUS;
    }

    // TODO stub for now
    public int getSpeed() {
        return gameConfig.MAX_MOVEMENT;
    }

    public void addSeeds(CropType type, int numSeeds) {
        seedInventory.put(type, seedInventory.get(type) + numSeeds);
    }

    public void removeSeeds(CropType type, int numSeeds) {
        seedInventory.put(type, seedInventory.get(type) - numSeeds);
    }

    public Map<CropType, Integer> getSeeds() {
        return this.seedInventory;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void harvest(Tile tile) {
        harvestedInventory.add(new Crop(tile.getCrop()));
        tile.clearCrop();
    }
}
