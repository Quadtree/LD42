package info.quadtree.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import info.quadtree.ld42.unit.Mine;
import info.quadtree.ld42.unit.Unit;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hex extends HexPos {
    public static final int HEX_SIZE = 32;

    public static final int MAX_TTL = 30;

    public int ttl;

    public Unit unit;

    public boolean isOnCurrentPath;
    public boolean isOnFuturePath;

    public Team owner = Team.Nobody;

    float fallenDistance = 0;
    float fallSpeed = 0;
    float fallSpeedModifier = MathUtils.random(0.8f, 1.2f);

    public Unit.UnitType shadowToRender;

    int wiggleX, wiggleY;

    public Hex(int x, int y, int ttl){
        super(x, y);
        this.ttl = ttl;
    }

    public void render(){
        if (ttl <= 0){
            fallSpeed += 1200f * Gdx.graphics.getDeltaTime();
            fallenDistance += fallSpeed * fallSpeedModifier * Gdx.graphics.getDeltaTime();
        }

        int sx = getScreenX();
        int sy = getScreenY() + getScreenOffsetY();

        float brightness = 1f;

        Sprite sp4 = LD42.s.getSprite("hex32");
        sp4.setColor(0.5f * owner.color.r, 0.5f * owner.color.g, 0.5f * owner.color.b, 1f);
        sp4.setBounds(sx, sy, HEX_SIZE, HEX_SIZE);
        sp4.draw(LD42.s.batch);

        int hexReinLevel = MathUtils.clamp(ttl / 5, 0, 3);

        if (hexReinLevel > 0){
            Sprite sp6 = LD42.s.getSprite("hexr2ein" + hexReinLevel);
            sp6.setColor(Color.WHITE);
            sp6.setBounds(sx, sy, HEX_SIZE, HEX_SIZE);
            sp6.draw(LD42.s.batch);
        }

        if (isOnCurrentPath){
            Sprite sp2 = LD42.s.getSprite("selected_hex");
            sp2.setBounds(this.getScreenX(), this.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
            sp2.setColor(Color.GREEN);
            sp2.draw(LD42.s.batch);
        }

        if (isOnFuturePath){
            Sprite sp2 = LD42.s.getSprite("selected_hex");
            sp2.setBounds(this.getScreenX(), this.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
            sp2.setColor(Color.RED);
            sp2.draw(LD42.s.batch);
        }

        if (ttl <= 1){
            if (MathUtils.randomBoolean(Gdx.graphics.getDeltaTime() * 16)){
                //wiggleX = MathUtils.random(-1, 1);
                wiggleY = MathUtils.random(-1, 1);
            }
        }

        if (shadowToRender != null){
            Unit u = Unit.factory(shadowToRender);

            Sprite sp7 = LD42.s.getSprite(u.getMainGraphicName());
            sp7.setBounds(this.getScreenX(), this.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
            sp7.setColor(new Color(1f, 1f, 1f, 0.5f));
            sp7.draw(LD42.s.batch);

            sp7 = LD42.s.getSprite(u.getFlagGraphicName());
            sp7.setBounds(this.getScreenX(), this.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
            sp7.setColor(new Color(Team.Overminers.color.r, Team.Overminers.color.g, Team.Overminers.color.b, 0.5f));
            sp7.draw(LD42.s.batch);
        }

        //LD42.s.batch.draw(sp, sx, sy, HEX_SIZE, HEX_SIZE);
    }

    public void render2(){
        if (unit != null)
            unit.render();
    }

    public void render3(){
        if (unit != null) unit.render3();

        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)){
            GlyphLayout gl = new GlyphLayout();
            gl.setText(LD42.s.defaultFont, "" + ttl);

            LD42.s.defaultFont.draw(LD42.s.batch, gl, getScreenX() + HEX_SIZE / 2 - gl.width / 2, getScreenY() + HEX_SIZE / 2 + gl.height / 2);
        }
    }

    public int getScreenOffsetY(){
        return -(int)fallenDistance + (wiggleY);
    }

    public int getScreenY() {
        return getY() * (HEX_SIZE / 2 - 1);
    }

    public int getScreenX() {
        return getX() * (HEX_SIZE * 3 / 2 - 2) + ((getY() % 2) * (HEX_SIZE * 3 / 2 / 2 - 1)) + (wiggleX);
    }

    public int getTtl() {
        return ttl;
    }

    public void recalcOwnership(){
        owner = Team.Nobody;

        List<Hex> allNeighbors = getExistingTwoLevelNeighbors();

        allNeighbors.forEach(it -> {
            if (it.unit instanceof Mine && !it.unit.isAnimating()){
                if (owner == Team.Nobody){
                    owner = it.unit.getTeam();
                } else if (it.unit.getTeam() != owner) {
                    owner = Team.Contested;
                }
            }
        });
    }

    public List<Hex> getExistingTwoLevelNeighbors(){
        Set<Hex> allNeighbors = new HashSet<>();

        allNeighbors.add(this);

        allNeighbors.addAll(Arrays.stream(getNeighbors()).filter(it -> it instanceof Hex).map(it -> (Hex)it).collect(Collectors.toList()));

        allNeighbors.addAll(allNeighbors.stream()
                .flatMap(it -> Arrays.stream(((Hex)it).getNeighbors()))
                .filter(it -> it instanceof Hex)
                .map(it -> (Hex)it)
                .collect(Collectors.toList()
                ));

        return new ArrayList<>(allNeighbors);
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

    public Stream<Hex> getNStream(){
        return Arrays.stream(getNeighbors()).filter(it -> it instanceof Hex).map(it -> (Hex)it);
    }

    public Set<Unit> getZonesOfControl(){
        return getNStream().filter(it -> it.unit != null && it.unit.getAttack() > 0).map(it -> it.unit).collect(Collectors.toSet());
    }
}
