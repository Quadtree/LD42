package info.quadtree.ld42.unit;

public class Turret extends Unit {
    @Override
    public String getMainGraphicName() {
        return "turret";
    }

    @Override
    public int getMaxHealth() {
        return 3;
    }

    @Override
    public int getAttack() {
        return 2;
    }

    @Override
    public int getCost() {
        return 5;
    }

    @Override
    public String getFlagGraphicName() {
        return "turret_flag";
    }
}
