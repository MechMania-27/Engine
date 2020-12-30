package mech.mania.engine.model;

import com.google.gson.annotations.Expose;

public class Player {
    @Expose
    private String name;
    @Expose
    private Position position;
    @Expose
    private Item item;
    @Expose
    private Upgrade upgrade;
    @Expose
    private int money;

    public Player(String name, Position position, Item item, Upgrade upgrade, int money) {
        this.name = name;
        this.position = position;
        this.item = item;
        this.upgrade = upgrade;
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
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
