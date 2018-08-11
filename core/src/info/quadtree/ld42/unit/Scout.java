package info.quadtree.ld42.unit;

public class Scout extends Unit {
    @Override
    public String getMainGraphicName() {
        return "scout1";
    }

    @Override
    public int getMaxMoves() {
        return 3;
    }

    @Override
    public int getMaxHealth() {
        return 2;
    }

    @Override
    public int getAttack() {
        return 1;
    }
}
