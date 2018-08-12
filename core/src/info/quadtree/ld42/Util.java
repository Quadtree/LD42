package info.quadtree.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.List;
import java.util.Optional;

public class Util {
    public static <T> Optional<T> choice(List<T> options){
        if (options == null || options.size() == 0) return Optional.empty();
        return Optional.ofNullable(options.get(MathUtils.random(options.size() - 1)));
    }

    static Window.WindowStyle windowStyle;

    public static void showTutorialText(String text){
        if (LD42.s.titleScreenUp) return;

        Preferences prefs = Gdx.app.getPreferences("ld42.xml");

        if (prefs.contains(text)){
            return;
        }

        if (windowStyle == null){
            windowStyle = new Window.WindowStyle(LD42.s.defaultFont, Color.WHITE, new NinePatchDrawable(LD42.s.dialogNinePatch));
        }

        prefs.putBoolean(text, true);

        Dialog dialog = new Dialog("", windowStyle);
        Label ll = new Label(text, LD42.s.defaultLabelStyle);
        ll.setWrap(true);
        dialog.getContentTable().add(ll).width(500).pad(20).fill();
        dialog.pack();
        LD42.s.uiStage.addActor(dialog);
        dialog.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);

        dialog.addListener((evt) -> {
            if (evt instanceof InputEvent){
                if (((InputEvent) evt).getType() == InputEvent.Type.keyDown || ((InputEvent) evt).getType() == InputEvent.Type.touchDown){
                    dialog.remove();
                    return true;
                }
            }
            return false;
        });
    }
}
