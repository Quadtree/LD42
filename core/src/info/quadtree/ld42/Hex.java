package info.quadtree.ld42;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

import java.util.stream.Stream;

public class Hex extends HexPos {
    final int HEX_SIZE = 32;

    int ttl;

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

        //LD42.s.batch.draw(sp, sx, sy, HEX_SIZE, HEX_SIZE);
    }

    private int getScreenY() {
        return getY() * (HEX_SIZE / 2 - 1);
    }

    private int getScreenX() {
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
        return null;
    }
}
