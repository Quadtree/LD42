package info.quadtree.ld42;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class OverlayTextureRegion extends TextureRegionDrawable {
    Sprite tx2;

    public OverlayTextureRegion(TextureRegionDrawable tx1, Sprite tx2){
        super(tx1);

        this.tx2 = tx2;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        super.draw(batch, x, y, width, height);

        tx2.setColor(Color.YELLOW);
        tx2.setBounds(x,y,width,height);
        tx2.draw(batch);
    }
}
