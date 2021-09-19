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
    private ArrayList<Crop> harvestedInventory = new ArrayList<>();
    private Achievements achievements;

    @Expose
    private double discount;
    @Expose
    private int amountSpent;
    @Expose
    private int protectionRadius;
    @Expose
    private int harvestRadius;
    @Expose
    private int plantRadius;
    @Expose
    private int carryingCapacity;
    @Expose
    private int maxMovement;
    @Expose
    private double doubleDropChance;

    @Expose
    private boolean usedItem = false;
    @Expose
    private boolean hasDeliveryDrone = false;
    @Expose
    private boolean hasCoffeeThermos = false;
    @Expose
    private boolean itemTimeExpired = false;

    private int playerID;

    private Config gameConfig;

    public Player(String name, int playerID, Position position, ItemType item, UpgradeType upgrade, int money, Config gameConfig) {
        this.gameConfig = gameConfig;
        this.name = name;
        this.playerID = playerID;
        this.position = position;
        this.item = item;
        this.upgrade = upgrade;
        this.money = money;
        this.achievements = new Achievements();

        for (CropType type : CropType.values()) {
            seedInventory.put(type, 0);
        }

        if (upgrade != UpgradeType.BACKPACK) {
            this.carryingCapacity = gameConfig.CARRYING_CAPACITY;
        } else {
            this.carryingCapacity = gameConfig.BACKPACK_CARRYING_CAPACITY;
        }

        if (upgrade != UpgradeType.LONGER_SCYTHE) {
            setHarvestRadius(gameConfig.HARVEST_RADIUS);
        } else {
            setHarvestRadius(gameConfig.LONGER_SCYTHE_HARVEST_RADIUS);
        }

        // don't need to do anything for loyalty card yet

        if (upgrade != UpgradeType.LONGER_LEGS) {
            setMaxMovement(gameConfig.MAX_MOVEMENT);
        } else {
            setMaxMovement(gameConfig.LONGER_LEGS_MAX_MOVEMENT);
        }

        if (upgrade != UpgradeType.RABBITS_FOOT) {
            setDoubleDropChance(0);
        } else {
            setDoubleDropChance(gameConfig.RABBITS_FOOT_DOUBLE_DROP_CHANCE);
        }

        if (upgrade != UpgradeType.SEED_A_PULT) {
            setPlantRadius(gameConfig.PLANT_RADIUS);
        } else {
            setPlantRadius(gameConfig.SEED_A_PULT_PLANT_RADIUS);
        }

        if (upgrade != UpgradeType.SPYGLASS) {
            setProtectionRadius(gameConfig.PROTECTION_RADIUS);
        } else {
            setProtectionRadius(gameConfig.SPYGLASS_PROTECTION_RADIUS);
        }

        if (upgrade != UpgradeType.BACKPACK) {
            setCarryingCapacity(gameConfig.CARRYING_CAPACITY);
        } else {
            setCarryingCapacity(gameConfig.BACKPACK_CARRYING_CAPACITY);
        }
    }

    public Player(Player other) {
        this.playerID = other.playerID;
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

        this.discount = other.discount;
        this.protectionRadius = other.protectionRadius;
        this.plantRadius = other.plantRadius;
        this.doubleDropChance = other.doubleDropChance;
        this.harvestRadius = other.harvestRadius;
        this.maxMovement = other.maxMovement;
        this.carryingCapacity = other.carryingCapacity;

        this.usedItem = other.usedItem;
        this.hasDeliveryDrone = other.hasDeliveryDrone;
        this.hasCoffeeThermos = other.hasCoffeeThermos;
        this.itemTimeExpired = other.itemTimeExpired;
        this.achievements = other.achievements;
    }

    public void sellInventory() {
        if (harvestedInventory.isEmpty()) {
            return;
        }
        if(achievements.hasStolen()) {
            achievements.addAchievement("Seedy Business");
        }
        if(achievements.hasStolen5Grapes()) {
            achievements.addAchievement("Grapes of Mild Displeasure");
        }
        Iterator<Crop> inventoryIter = harvestedInventory.iterator();
        while (inventoryIter.hasNext()) {
            Crop crop = inventoryIter.next();

            //store the sold CropType to achievements
            if(crop.getType() != CropType.GOLDENCORN && crop.getType() != CropType.PEANUTS) {
                achievements.addCropType(crop.getType());
            }
            if(crop.getType() == CropType.GOLDENCORN) {
                achievements.addAchievement("Stalks and Bonds");
            }
            money += crop.getValue();
            inventoryIter.remove();
        }
    }

    public double getMoney() {
        return money;
    }
    public void setMoney(int money){
            this.money = money;
    }
    public void changeBalance(double delta) {
        this.money += delta;
    }
    public UpgradeType getUpgrade() {
        return upgrade;
    }

    // player is initialized with UpgradeType + can't change item anyways
//    public void setUpgrade(UpgradeType upgradeType) {
//        this.upgrade = upgradeType;
//    }
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
        if (this.upgrade == UpgradeType.LOYALTY_CARD && achievements.getMoneySpent() >= gameConfig.GREEN_GROCER_LOYALTY_CARD_MINIMUM) {
            setDiscount(gameConfig.GREEN_GROCER_LOYALTY_CARD_DISCOUNT);
        }
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
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

    public void setPlantRadius(int plantRadius) {
        this.plantRadius = plantRadius;
    }

    public boolean getUsedItem() {
        return usedItem;
    }

    public void setUsedItem() {
        this.usedItem = true;
    }

    public boolean getHasDeliveryDrone() {
        return hasDeliveryDrone;
    }

    public void setDeliveryDrone(boolean hasDeliveryDrone) {
        this.hasDeliveryDrone = hasDeliveryDrone;
    }

    public boolean getHasCoffeeThermos() {
        return hasCoffeeThermos;
    }

    public void setHasCoffeeThermos(boolean hasCoffeeThermos) {
        this.hasCoffeeThermos = hasCoffeeThermos;
    }

    public int getPlantingRadius() {
        return this.plantRadius;
    }

    public int getSpeed() {
        if (this.getHasCoffeeThermos()) {
            return this.maxMovement * 3;
        }
        return this.maxMovement;
    }

    public int getCarryingCapacity() {
        return this.carryingCapacity;
    }

    public ArrayList<Crop> getHarvestedCrops() {
        return harvestedInventory;
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

        if (this.doubleDropChance > 0 && harvestedInventory.size() < this.carryingCapacity) {
            Random r = new Random();
            if (r.nextDouble() < this.doubleDropChance) {
                harvestedInventory.add(new Crop(tile.getCrop()));
            }
        }

        tile.clearCrop();
    }

    public Config getConfig() {
        return gameConfig;
    }


    public void addToHarvestInventory(Crop crop) {
        harvestedInventory.add(crop);
    }

    public Achievements getAchievements() {
        return achievements;
    }
}
