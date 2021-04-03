package mech.mania.engine.model;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
public class Achievements {
    private Set<String> achievements;
    private double moneySpent;
    private Set<CropType> cropsSold;
    private int grapesStolen;
    private boolean stole;
    private boolean cropsPlant;
    private boolean fruitOnly;
    private int cropsDestroyed;

    public Achievements() {
        achievements = new HashSet<>();
        moneySpent = 0;
        cropsSold = new HashSet<>();
        cropsDestroyed = 0;
        grapesStolen = 0;
        stole = false;
        cropsPlant = false;
        fruitOnly = true;
        cropsDestroyed = 0;
    }
    public void spendMoney(int money) {
        moneySpent += money;
    }
    public void addCropType(CropType type) {
        cropsSold.add(type);
    }
    public void destroyCrops(int amount) {
        cropsDestroyed += amount;
    }
    public int getCropsDestroyed() {return cropsDestroyed;}
    public void stealGrapes(int amount) {
        grapesStolen += amount;
    }
    public boolean hasStolen5Grapes() {
        return grapesStolen >= 5;
    }
    public void steal() {
        stole = true;
    }
    public boolean hasStolen() {
        return stole;
    }
    public void plant() {
        cropsPlant = true;
    }
    public void fruit() {
        fruitOnly = false;
    }
    public void destroyCrop() {
        cropsDestroyed += 1;
    }
    public void addAchievement(String achievement) {
        achievements.add(achievement);
    }
    public List<String> getFinalAchievements(boolean win, double starting_money, double money) {
        if(cropsDestroyed >= 10) {
            achievements.add("Dust Bowl");
        }
        if(moneySpent >= 1000) {
            achievements.add("My Favorite Customer");
        }
        if(money >= 2500) {
            achievements.add("Richer than Phineas Himself");
        }
        if(money < starting_money) {
            achievements.add("Not Worth the Dirt He Sows");
        }
        if(cropsSold.size() == 6) {
            achievements.add("Omni-Agriculturalist");
        }
        if(win == true) {
            achievements.add("A Worthy Heir");

            if (stole == false) {
                achievements.add("It Ain't Much, but It's Honest Work");
            }
            if (cropsPlant == false) {
                achievements.add("Botanical Burglary");
            }
            if (fruitOnly == true) {
                achievements.add("Fruits of our Labor");
            }
        }
        List<String> ret = new ArrayList<>();
        for(String e : achievements) {
            ret.add(e);
        }
        return ret;
    }
}