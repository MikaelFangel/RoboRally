package dk.dtu.compute.se.pisd.roborally.model;

public abstract class Card {
    private final String description;
    private final String effect;
    private final int cost;

    public Card(String description, String effect, int cost) {
        this.description = description;
        this.effect = effect;
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public int getCost() {
        return cost;
    }

    public String getEffect() {
        return effect;
    }
}
