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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class Util {
    public static <T> Optional<T> choice(List<T> options){
        if (options == null || options.size() == 0) return Optional.empty();
        return Optional.ofNullable(options.get(MathUtils.random(options.size() - 1)));
    }

    static Window.WindowStyle windowStyle;

    static Dialog tutorialWindow = null;

    static List<Dialog> tutorialQueue = new ArrayList<>();

    static long lastTutorialTime = -100000;

    public static void closeTutorial(){
        if (System.currentTimeMillis() < lastTutorialTime + 500) return;

        if (tutorialWindow != null){
            tutorialWindow.remove();
            tutorialWindow = null;
        }

        showNextTutorial();
    }

    public static void showNextTutorial(){
        if (tutorialQueue.size() > 0 && tutorialWindow == null){
            Dialog dialog = tutorialQueue.get(0);
            tutorialQueue.remove(0);

            LD42.s.uiStage.addActor(dialog);
            dialog.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);
            tutorialWindow = dialog;

            lastTutorialTime = System.currentTimeMillis();
        }
    }

    public static void showTutorialText(String key, String text){
        if (LD42.s.titleScreenUp) return;

        Preferences prefs = Gdx.app.getPreferences("ld42.xml");

        if (prefs.contains(key)){
            return;
        }

        prefs.putBoolean(key, true);
        prefs.flush();

        Dialog dialog = new Dialog("", windowStyle);
        Label ll = new Label(text, LD42.s.defaultLabelStyle);
        ll.setWrap(true);
        dialog.getContentTable().add(ll).width(500).pad(20).fill();
        dialog.pack();

        dialog.addListener((evt) -> {
            if (evt instanceof InputEvent){
                if (((InputEvent) evt).getType() == InputEvent.Type.keyDown || ((InputEvent) evt).getType() == InputEvent.Type.touchDown){
                    closeTutorial();
                    return true;
                }
            }
            return false;
        });

        tutorialQueue.add(dialog);
        showNextTutorial();
    }

    public static void showTutorialText(String text){
        showTutorialText(text, text);
    }

    public static Runnable takeScreenshot = () -> {};
}
