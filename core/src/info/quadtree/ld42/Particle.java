package info.quadtree.ld42;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Particle extends Actor {
    float lifespan;
    float curLifespan;

    float drag;

    Vector2 velocity;

    float angle;
    float spinRate;

    Color color;

    float size;
    float growthRate;

    public Particle(Vector2 startPos, float maxSpeed, float maxSpinRate, float drag, float initialSize, float maxGrowthRate, float maxLifeSpan, Color color){
        this.setPosition(startPos.x, startPos.y);

        float moveAngle = MathUtils.random(0f, MathUtils.PI2);
        float speed = MathUtils.random(maxSpeed);

        velocity = new Vector2(MathUtils.cos(moveAngle), MathUtils.sin(moveAngle)).scl(speed);

        this.drag = drag;
        this.color = color;

        angle = MathUtils.random(0f, MathUtils.PI2);
        this.spinRate = MathUtils.random(maxSpinRate);

        this.size = initialSize;
        this.growthRate = MathUtils.random(1f, maxGrowthRate);

        this.lifespan = MathUtils.random(maxLifeSpan);
        this.curLifespan = this.lifespan;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.size += this.size * (this.growthRate - 1f) * delta;

        this.lifespan -= delta;
        if (this.lifespan <= 0) remove();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Sprite sp = LD42.s.getSprite("cloud");
        sp.setOrigin(sp.getWidth() / 2, sp.getHeight() / 2);
        sp.setBounds(getX() - size / 2, getY() - size / 2, size, size);
        sp.setColor(color);
        sp.draw(batch);
    }
}
