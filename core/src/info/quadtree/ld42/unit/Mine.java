package info.quadtree.ld42.unit;

public class Mine extends Unit {
    @Override
    public String getMainGraphicName() {
        return "mine1";
    }

    @Override
    public boolean canBeSelected() {
        return false;
    }

    @Override
    public int getMaxHealth() {
        return 2;
    }
}
