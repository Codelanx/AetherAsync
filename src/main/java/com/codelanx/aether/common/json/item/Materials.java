package com.codelanx.aether.common.json.item;

//common enum for items, should stick to basics here and keep skill-specific items in a different enum
//this is for a lower memory footprint, but this will likely be externalized in the future anyhow
public enum Materials implements Material {

    COINS(995, "Coin", "Coins"),
    KNIFE(946, "Knife"),
    ;

    private final int id;
    private final String name;
    private final String plural;

    private Materials(int id, String name) {
        this(id, name, name);
    }

    private Materials(int id, String name, String plural) {
        this.id = id;
        this.name = name;
        this.plural = plural;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPlural() {
        return this.plural;
    }

    @Override
    public boolean isStackable() {
        switch (this) {
            case COINS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "Materials#" + this.name() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
