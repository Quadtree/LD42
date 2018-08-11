package info.quadtree.ld42.unit;

public class Block extends Unit {
    @Override
    public String getMainGraphicName() {
        return "block1";
    }

    @Override
    public int getHealth() {
        return 3;
    }

    @Override
    public int getCost() {
        return 4;
    }
}
