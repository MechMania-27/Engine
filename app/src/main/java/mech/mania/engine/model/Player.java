package mech.mania.engine.model;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
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
    private int money;
    @Expose
    private HashMap<CropType, Integer> seeds;
    @Expose
    private ArrayList<Crop> harvestedCrops;

    private Map<CropType, Integer> seedInventory = new HashMap<>();
    private List<Crop> harvestedInventory = new ArrayList<>();

    public Player(String name, Position position, ItemType item, UpgradeType upgrade, int money) {
        this.name = name;
        this.position = position;
        this.item = item;
        this.upgrade = upgrade;
        this.money = money;

        seeds = new HashMap<>();
        // TODO need a better way to initialize this
        seeds.put(CropType.CORN, 0);
        seeds.put(CropType.GRAPE, 0);
        seeds.put(CropType.POTATO, 0);

        harvestedCrops = new ArrayList<>();


        for (CropType type : CropType.values()) {
            seedInventory.put(type, 0);
        }
    }

    public Player(Player other) {
        this.name = other.name;
        this.position = new Position(other.position);
        this.item = other.item;
        this.upgrade = other.upgrade;
        this.money = other.money;
        seeds = new HashMap<>();
        seeds.putAll(other.seeds);
        this.seedInventory = new HashMap<>(seedInventory);
        this.harvestedInventory = new ArrayList<>(harvestedInventory);
    }

    public void sellInventory() {
        Iterator<Crop> inventoryIter = harvestedInventory.iterator();
        while (inventoryIter.hasNext()) {
            Crop crop = inventoryIter.next();
            money += crop.getValue();
            inventoryIter.remove();
        }
    }

    public void buySeed(CropType seed) throws InvalidBalanceException {
        if (money < seed.getSeedBuyPrice()) {
            throw new InvalidBalanceException(money, seed.getSeedBuyPrice());
        } else {
            money -= seed.getSeedBuyPrice();
            seedInventory.put(seed, seedInventory.get(seed) + 1);
        }
    }

    public int getMoney() {
        return money;
    }

    public void changeBalance(double delta) {
        this.money += delta;
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
        return -1;
    }

    // TODO stub for now
    public int getPlantingRadius() {
        return -1;
    }

    // TODO stub for now
    public int getSpeed() {
        return -1;
    }

    // TODO stub for now
    public int getCarryingCapacity() {
        return -1;
    }

    public ArrayList<Crop> getHarvestedCrops() {
        return harvestedCrops;
    }

    public void addSeeds(CropType type, int numSeeds) {
        seeds.put(type, seeds.get(type) + numSeeds);
    }

    public void removeSeeds(CropType type, int numSeeds) {
        seeds.put(type, seeds.get(type) - numSeeds);
    }

    public HashMap<CropType, Integer> getSeeds() {
        return this.seeds;
    }
}
