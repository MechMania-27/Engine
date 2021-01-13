package mech.mania.engine.model;

import com.google.gson.annotations.Expose;

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

    private Map<CropType, Integer> seedInventory = new HashMap<>();
    private List<Crop> harvestedInventory = new ArrayList<>();

    public Player(String name, Position position, ItemType item, UpgradeType upgrade, int money) {
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
        this.name = other.name;
        this.position = new Position(other.position);
        this.item = other.item;
        this.upgrade = other.upgrade;
        this.money = other.money;
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

    // stub for now
    public int getHarvestRadius() {
        return -1;
    }
}
