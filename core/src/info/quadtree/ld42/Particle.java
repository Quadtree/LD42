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
        this.growthRate = MathUtils.random(maxGrowthRate);

        this.lifespan = maxLifeSpan * MathUtils.random(0.9f, 1.1f);
        this.curLifespan = this.lifespan;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.size += this.growthRate * delta;

        this.curLifespan -= delta;
        if (this.curLifespan <= 0) remove();

        this.setPosition(getX() + velocity.x * delta, getY() + velocity.y * delta);

        this.velocity.sub(this.velocity.cpy().scl(drag).scl(delta));

        this.angle += this.spinRate * delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color col = color.cpy();
        col.a = MathUtils.clamp(curLifespan / lifespan, 0f, 1f);

        Sprite sp = LD42.s.getSprite("cloud");
        sp.setOrigin(size / 2, size / 2);
        sp.setBounds(getX() - size / 2, getY() - size / 2, size, size);
        sp.setRotation(this.angle * MathUtils.radiansToDegrees);
        sp.setColor(col);
        sp.draw(batch);
    }
}
