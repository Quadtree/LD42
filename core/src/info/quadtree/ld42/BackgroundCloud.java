package info.quadtree.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BackgroundCloud extends Actor {

    public static final float SPEED = 4f;

    Color col;

    public BackgroundCloud() {
        setPosition(MathUtils.random(-300, Gdx.graphics.getWidth()), MathUtils.random(-200, Gdx.graphics.getHeight()));

        col = new Color(1f, 1f, 1f, MathUtils.random(0.1f, 0.25f));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Sprite sp = LD42.s.getSprite("backgroundcloud");
        sp.setColor(col);
        sp.setPosition(getX(), getY());
        sp.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        setX(getX() + delta * SPEED);

        if (getX() > Gdx.graphics.getWidth()){
            wrap();
        }
    }

    public void wrap(){
        setPosition(-300, MathUtils.random(-200, Gdx.graphics.getHeight()));
    }
}
