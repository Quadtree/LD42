package info.quadtree.ld42.unit;

public class Tank extends Unit {
    @Override
    public String getMainGraphicName() {
        return "tank1";
    }

    @Override
    public int getMaxMoves() {
        return 2;
    }

    @Override
    public int getMaxHealth() {
        return 5;
    }

    @Override
    public int getAttack() {
        return 3;
    }

    @Override
    public int getCost() {
        return 20;
    }

    @Override
    public String getFlagGraphicName() {
        return "tank1_flag";
    }

    @Override
    public String getName() {
        return "Strider";
    }
}
