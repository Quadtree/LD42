package info.quadtree.ld42;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import info.quadtree.ld42.unit.Unit;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public class Hex extends HexPos {
    public static final int HEX_SIZE = 32;

    int ttl;

    public Unit unit;

    public Hex(int x, int y, int ttl){
        super(x, y);
        this.ttl = ttl;
    }

    public void render(){
        int sx = getScreenX();
        int sy = getScreenY();

        float brightness = MathUtils.clamp(ttl / 30f, 0f, 1f);

        Sprite sp = LD42.s.getSprite("hex32");
        sp.setColor(brightness, brightness, brightness, 1f);
        sp.setBounds(sx, sy, HEX_SIZE, HEX_SIZE);
        sp.draw(LD42.s.batch);

        if (unit != null)
            unit.render();

        //LD42.s.batch.draw(sp, sx, sy, HEX_SIZE, HEX_SIZE);
    }

    public int getScreenY() {
        return getY() * (HEX_SIZE / 2 - 1);
    }

    public int getScreenX() {
        return getX() * (HEX_SIZE * 3 / 2 - 2) + ((getY() % 2) * (HEX_SIZE * 3 / 2 / 2 - 1));
    }

    public int getTtl() {
        return ttl;
    }

    /**
     * Gets all neighbors of a hex, starting with the one to the upper right
     * @return
     */
    public HexPos[] getNeighbors(){

        /*
        // ODD Y (5,5)
        gs.setHex(new Hex(5,4, 20));
        gs.setHex(new Hex(6,4, 20));
        gs.setHex(new Hex(5,3, 20));
        gs.setHex(new Hex(5,6, 20));
        gs.setHex(new Hex(6,6, 20));
        gs.setHex(new Hex(5,7, 20));

        // EVEN Y (5,12)
        gs.setHex(new Hex(4,11, 20));
        gs.setHex(new Hex(5,11, 20));
        gs.setHex(new Hex(5,10, 20));
        gs.setHex(new Hex(4,13, 20));
        gs.setHex(new Hex(5,13, 20));
        gs.setHex(new Hex(5,14, 20));*/

        BiFunction<Integer, Integer, HexPos> getAtPos = (x,y) -> {
            HexPos hp = LD42.s.gs.getHex(x,y);

            if (hp == null) hp = new HexPos(x,y);

            return hp;
        };

        if (y % 2 == 0){
            return new HexPos[]{
                    getAtPos.apply(x - 1, y - 1),
                    getAtPos.apply(x, y - 1),
                    getAtPos.apply(x, y - 2),
                    getAtPos.apply(x - 1, y + 1),
                    getAtPos.apply(x, y + 1),
                    getAtPos.apply(x, y + 2),
            };
        } else {
            return new HexPos[]{
                    getAtPos.apply(x, y - 1),
                    getAtPos.apply(x+1, y - 1),
                    getAtPos.apply(x, y - 2),
                    getAtPos.apply(x, y + 1),
                    getAtPos.apply(x + 1, y + 1),
                    getAtPos.apply(x, y + 2),
            };
        }
    }
}
