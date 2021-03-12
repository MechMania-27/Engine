package mech.mania.engine.model;

public class Achievements {
    private ArrayList<String> achievements;
    private int moneySpent;
    private Set<String> cropsSold;
    private int grapesStolen;
    private boolean stole;
    private boolean cropsPlant;
    private boolean fruitOnly;

    public Achievements() {
        achievements = ArrayList<String>();
        moneySpent = 0;
        cropsSold = Set<String>();
        cropsDestroyed = 0;
        grapesStolen = 0;
        stolen = false;
        cropsPlant = false;
        fruitOnly = true;
    }
    public void spendMoney(int money) {
        moneySpent += money;
    }
    public void sellCrop(String type) {
        cropsSold.add(type);
    }
    public void destroyCrops(int amount) {
        cropsDestroyed += amount;
    }
    public void stealGrapes(int amount) {
        grapesStolen += amount;
    }
    public void steal() {
        stole = true;
    }
    public void plant() {
        cropsPlant = true;
    }
    public void fruit() {
        fruitOnly = false;
    }
    public ArrayList<String> getAchievements() {
        if(moneySpent >= 1000) {
            achievements.add("My Favorite Customer");
        }
        if(cropsSold.size() == 6) {
            achievements.add("Omni-Agriculturalist");
        }
        if(grapesStolen >= 5) {
            achievements.add("Grapes of Mild Displeasure");
        }
        if(stole == false) {
            achievements.add("It Ain’t Much, but It’s Honest Work");
        }
        if(cropsPlant == false) {
            achievements.add("Botanical Burglary");
        }
        if(fruitOnly == true) {
            achievements.add("Fruits of our Labor");
        }
        return achievements;
    }
}