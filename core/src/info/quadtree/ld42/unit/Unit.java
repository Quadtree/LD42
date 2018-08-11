package info.quadtree.ld42.unit;

import com.badlogic.gdx.graphics.g2d.Sprite;
import info.quadtree.ld42.Hex;
import info.quadtree.ld42.LD42;

public abstract class Unit {
    int health;

    Hex hex;

    public void turn(){

    }

    public void render(){
        Sprite sp = LD42.s.getSprite(getMainGraphicName());

        sp.setBounds(hex.getScreenX(), hex.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
        sp.draw(LD42.s.batch);
    }

    public void moveTo(Hex hex){
        if (this.hex != null) this.hex.unit = null;

        this.hex = hex;

        if (this.hex != null) {
            this.hex.unit = this;
        }
    }

    public abstract String getMainGraphicName();
}
