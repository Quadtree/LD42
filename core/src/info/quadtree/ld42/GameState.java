package info.quadtree.ld42;

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

    public void setHex(int x, int y, Hex hex){
        if (x < 0 || y < 0 || x >= gridSize || y >= gridSize) return;
        hexes[x * gridSize + y] = hex;
    }

    public void render(){

    }
}
