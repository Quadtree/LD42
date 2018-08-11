package info.quadtree.ld42;

import com.badlogic.gdx.math.MathUtils;
import info.quadtree.ld42.unit.Mine;
import info.quadtree.ld42.unit.Unit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameState {
    Hex[] hexes;

    public static final int GRID_WIDTH = 22;
    public static final int GRID_HEIGHT = 44;

    public static final int CENTER_VARIANCE = 8;

    Unit.UnitType selectedUnitTypeToPlace = null;

    public Unit selectedUnit = null;

    public GameState(){
    }

    private void growFrom(Hex it){
        long hexCount = Arrays.stream(hexes).filter(Objects::nonNull).count();

        if (MathUtils.random(2) == 0 || hexCount < 200) {
            HexPos neighbor = MathUtils.randomBoolean() ? it.getNeighbors()[MathUtils.random(1) * 3 + 2] : it.getNeighbors()[MathUtils.random(5)];
            if (!(neighbor instanceof Hex)) {
                Hex nx = new Hex(neighbor.x, neighbor.y, 1);
                setHex(nx);
                if (MathUtils.random(6) == 0) growFrom(it);
            } else {
                ((Hex) neighbor).ttl++;
            }
        }
    }

    public void generate(){
        while(true) {
            hexes = new Hex[GRID_WIDTH * GRID_HEIGHT];

            int seeds = MathUtils.random(2, 5);

            for (int i=0;i<seeds;++i) setHex(new Hex(MathUtils.random(GRID_WIDTH / 2 - CENTER_VARIANCE, GRID_WIDTH / 2 + CENTER_VARIANCE*2), MathUtils.random(GRID_HEIGHT / 2 - CENTER_VARIANCE, GRID_HEIGHT / 2 + CENTER_VARIANCE*2), 1));


            for (int i = 0; i < 30; ++i) {
                final int fi = i;
                Arrays.stream(hexes).filter(Objects::nonNull).forEach(this::growFrom);
            }

            long badHexCount = Arrays.stream(hexes).filter(Objects::nonNull).filter(it -> it.x == 0 || it.y == 0 || it.x == GRID_WIDTH - 1 || it.y == GRID_HEIGHT - 1).count();
            long hexCount = Arrays.stream(hexes).filter(Objects::nonNull).count();
            System.out.println("BHC="+badHexCount + " HC=" + hexCount);

            if (hexCount >= 300) break;
        }

        Arrays.stream(hexes)
                .filter(Objects::nonNull).filter(it -> it.x <= 2 || it.y <= 2 || it.x >= GRID_WIDTH - 3 || it.y >= GRID_HEIGHT - 3)
                .collect(Collectors.toList())
                .forEach(it -> {
                    int distToSide = Math.min(Math.min(Math.min(it.x, it.y), GRID_WIDTH - 1 - it.x), GRID_HEIGHT - 1 - it.y);
                    if (MathUtils.random(distToSide) == 0) deleteHex(it.x, it.y);
                });

        new Mine().setTeam(Team.DigCorp).moveTo(Arrays.stream(hexes).filter(Objects::nonNull).findAny().get());

        recomputeOwnership();
    }

    public Hex getHex(int x, int y){
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) return null;
        return hexes[x * GRID_WIDTH + y];
    }

    public void setHex(Hex hex){
        int x = hex.x;
        int y = hex.y;
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) return;
        hexes[x * GRID_WIDTH + y] = hex;
    }

    public void deleteHex(int x, int y){
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) return;
        hexes[x * GRID_WIDTH + y] = null;
    }

    public void render(){
        Arrays.stream(hexes).filter(Objects::nonNull).sorted(Comparator.comparingInt(Hex::getY)).forEach(Hex::render);
    }

    public Optional<Hex> getHexAtScreenPos(int x, int y){
        return Arrays.stream(hexes).filter(Objects::nonNull).filter(it ->
                (Math.pow((it.getScreenX() + Hex.HEX_SIZE / 2.0) - x, 2) +
                Math.pow((it.getScreenY() + Hex.HEX_SIZE / 2.0) - y, 2)) < 16*16
        ).findAny();
    }

    public void recomputeOwnership(){
        Arrays.stream(hexes).filter(Objects::nonNull).forEach(Hex::recalcOwnership);
    }
}
