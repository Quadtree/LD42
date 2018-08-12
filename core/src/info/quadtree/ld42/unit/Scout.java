package info.quadtree.ld42.unit;

public class Scout extends Unit {
    @Override
    public String getMainGraphicName() {
        return "scout1";
    }

    @Override
    public int getMaxMoves() {
        return 4;
    }

    @Override
    public int getMaxHealth() {
        return 2;
    }

    @Override
    public int getAttack() {
        return 1;
    }

    @Override
    public int getCost() {
        return 25;
    }

    @Override
    public String getFlagGraphicName() {
        return "scout1_flag";
    }
}
