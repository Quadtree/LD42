package info.quadtree.ld42.unit;

public class Tank extends Unit {
    @Override
    public String getMainGraphicName() {
        return "tank1";
    }

    @Override
    public int getMaxMoves() {
        return 1;
    }
}
