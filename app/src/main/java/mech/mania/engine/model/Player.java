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
}
