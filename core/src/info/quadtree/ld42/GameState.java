package info.quadtree.ld42;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class GameState {
    Hex[] hexes;

    public static final int GRID_WIDTH = 22;
    public static final int GRID_HEIGHT = 44;

    public GameState(){



    }

    public void generate(){
        while(true) {
            hexes = new Hex[GRID_WIDTH * GRID_HEIGHT];
            setHex(new Hex(MathUtils.random(GRID_WIDTH / 2 - 8, GRID_WIDTH / 2 + 8), MathUtils.random(GRID_HEIGHT / 2 - 8, GRID_HEIGHT / 2 + 8), 1));
            setHex(new Hex(MathUtils.random(GRID_WIDTH / 2 - 8, GRID_WIDTH / 2 + 8), MathUtils.random(GRID_HEIGHT / 2 - 8, GRID_HEIGHT / 2 + 8), 1));
            setHex(new Hex(MathUtils.random(GRID_WIDTH / 2 - 8, GRID_WIDTH / 2 + 8), MathUtils.random(GRID_HEIGHT / 2 - 8, GRID_HEIGHT / 2 + 8), 1));

            for (int i = 0; i < 30; ++i) {
                long hexCount = Arrays.stream(hexes).filter(Objects::nonNull).count();

                final int fi = i;
                Arrays.stream(hexes).filter(Objects::nonNull).forEach(it -> {
                    if (MathUtils.random(3) != 0) it.ttl++;
                    if (MathUtils.random(2) == 0 || hexCount < 200) {
                        HexPos neighbor = it.getNeighbors()[MathUtils.random(5)];
                        if (!(neighbor instanceof Hex)) {
                            setHex(new Hex(neighbor.x, neighbor.y, 1));
                        }
                    }
                });
            }

            long badHexCount = Arrays.stream(hexes).filter(Objects::nonNull).filter(it -> it.x == 0 || it.y == 0 || it.x == GRID_WIDTH - 1 || it.y == GRID_HEIGHT - 1).count();
            long hexCount = Arrays.stream(hexes).filter(Objects::nonNull).count();
            System.out.println("BHC="+badHexCount + " HC=" + hexCount);

            if (badHexCount == 0 && hexCount >= 300) break;
        }
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

    public void render(){
        Arrays.stream(hexes).filter(Objects::nonNull).sorted(Comparator.comparingInt(Hex::getY)).forEach(Hex::render);
    }
}
