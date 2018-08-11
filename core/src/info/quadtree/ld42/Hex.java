package info.quadtree.ld42;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Hex {
    final int HEX_SIZE = 48; // shrink me

    int x, y;
    int ttl;

    public Hex(int x, int y, int ttl){
        this.x = x;
        this.y = y;
        this.ttl = ttl;
    }

    public void render(){
        int sx = x * HEX_SIZE * 3 / 2 + ((y % 2) * HEX_SIZE * 3 / 2 / 2);
        int sy = y * HEX_SIZE / 2;

        Sprite sp = LD42.s.getSprite("hex48");

        LD42.s.batch.draw(sp, sx, sy, HEX_SIZE, HEX_SIZE);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTtl() {
        return ttl;
    }
}
