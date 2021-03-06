package mech.mania.engine.model;

import com.google.gson.annotations.Expose;

public class Player {
    @Expose
    private String name;
    @Expose
    private Position position;
    @Expose
    private ItemType item;
    @Expose
    private UpgradeType upgradeType;
    @Expose
    private int money;

    private double discount;
    private int amountSpent;
    private int protectionRadius;
    private int harvestRadius;
    private int plantRadius;
    private int carryingCapacity;
    private int maxMovement;
    private double doubleDropChance;

    public Player(String name, Position position, ItemType item, UpgradeType upgradeType, int money) {
        this.name = name;
        this.position = position;
        this.item = item;
        this.upgradeType = upgradeType;
        this.money = money;
    }

    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public UpgradeType getUpgrade() {
        return upgradeType;
    }
    public void setUpgrade(UpgradeType upgradeType) {
        this.upgradeType = upgradeType;
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
    public double getDiscount() {
        return discount;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    public void setAmountSpent(int amountSpent) {
        this.amountSpent = amountSpent;
    }
    public int getAmountSpent() {
        return amountSpent;
    }
    public void setProtectionRadius(int protectionRadius) {
        this.protectionRadius = protectionRadius;
    }

    public int getProtectionRadius() {
        return protectionRadius;
    }

    public int getHarvestRadius() {
        return harvestRadius;
    }

    public void setHarvestRadius(int harvestRadius) {
        this.harvestRadius = harvestRadius;
    }

    public int getCarryingCapacity() {
        return carryingCapacity;
    }

    public void setCarryingCapacity(int carryingCapacity) {
        this.carryingCapacity = carryingCapacity;
    }

    public int getMaxMovement() {
        return maxMovement;
    }

    public void setMaxMovement(int maxMovement) {
        this.maxMovement = maxMovement;
    }

    public double getDoubleDropChance() {
        return doubleDropChance;
    }

    public void setDoubleDropChance(double doubleDropChance) {
        this.doubleDropChance = doubleDropChance;
    }

    public int getPlantRadius() {
        return plantRadius;
    }

    public void setPlantRadius(int plantRadius) {
        this.plantRadius = plantRadius;
    }
}
