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
        int sy = getScreenY();

        float brightness = 1f;

        Sprite sp = LD42.s.getSprite("hexborder");
        sp.setColor(brightness * owner.color.r, brightness * owner.color.g, brightness * owner.color.b, 1f);
        sp.setBounds(sx, sy, HEX_SIZE, HEX_SIZE);
        sp.draw(LD42.s.batch);

        Sprite sp4 = LD42.s.getSprite("hex32");
        sp4.setColor(brightness * owner.color.r, brightness * owner.color.g, brightness * owner.color.b, 0.5f);
        sp4.setBounds(sx, sy, HEX_SIZE, HEX_SIZE);
        sp4.draw(LD42.s.batch);

        int breakAmt = MathUtils.clamp((ttl) / (MAX_TTL / 7) + 1, 1, 6);

        if (breakAmt > 0) {
            Sprite sp3 = LD42.s.getSprite("hb4lvl" + breakAmt);
            sp3.setColor(owner.color);
            sp3.setBounds(sx, sy, HEX_SIZE, HEX_SIZE);
            sp3.draw(LD42.s.batch);
        }

        if (isOnCurrentPath){
            Sprite sp2 = LD42.s.getSprite("selected_hex");
            sp2.setBounds(this.getScreenX(), this.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
            sp2.draw(LD42.s.batch);
        }

        if (ttl <= 1){
            if (MathUtils.randomBoolean(Gdx.graphics.getDeltaTime() * 16)){
                //wiggleX = MathUtils.random(-1, 1);
                wiggleY = MathUtils.random(-1, 1);
            }
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

    public int getScreenY() {
        return getY() * (HEX_SIZE / 2 - 1) - (int)fallenDistance + (wiggleY);
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

    public Set<Team> getZonesOfControl(){
        return getNStream().filter(it -> it.unit != null && it.unit.getAttack() > 0).map(it -> it.unit.getTeam()).collect(Collectors.toSet());
    }
}
