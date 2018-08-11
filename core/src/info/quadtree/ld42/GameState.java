package info.quadtree.ld42;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class GameState {
    Hex[] hexes;

    public static final int GRID_WIDTH = 20;
    public static final int GRID_HEIGHT = 40;

    public GameState(){
        hexes = new Hex[GRID_WIDTH * GRID_HEIGHT];
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
