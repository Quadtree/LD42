package info.quadtree.ld42;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class GameState {
    Hex[] hexes;

    int gridSize = 20;

    public GameState(){
        hexes = new Hex[gridSize * gridSize];
    }

    public Hex getHex(int x, int y){
        if (x < 0 || y < 0 || x >= gridSize || y >= gridSize) return null;
        return hexes[x * gridSize + y];
    }

    public void setHex(Hex hex){
        int x = hex.x;
        int y = hex.y;
        if (x < 0 || y < 0 || x >= gridSize || y >= gridSize) return;
        hexes[x * gridSize + y] = hex;
    }

    public void render(){
        Arrays.stream(hexes).filter(Objects::nonNull).sorted(Comparator.comparingInt(Hex::getY)).forEach(Hex::render);
    }
}
