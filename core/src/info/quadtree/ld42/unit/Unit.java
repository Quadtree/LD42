package info.quadtree.ld42.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import info.quadtree.ld42.Hex;
import info.quadtree.ld42.LD42;
import info.quadtree.ld42.Team;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit {
    public enum UnitType {
        Mine,
        Tank,
        Scout,
        Turret,
        Block
    }

    int health = getMaxHealth();

    int moves = 0;

    int attacks = 0;

    Hex hex;

    Hex currentDestination;

    Team team = Team.Contested;

    Vector2 currentScreenPos;
    boolean isAccelerating;
    float animationSpeed;

    List<Hex> currentPath = null;

    public Unit(){
    }

    public void turnStart(){
        moves = getMaxMoves();
        if (getAttack() > 0) attacks = 1;
    }

    public void turn(){

    }

    public void render(){
        if (LD42.s.gs.selectedUnit == this){
            Sprite sp = LD42.s.getSprite("selected_hex");

            sp.setBounds(currentScreenPos.x, currentScreenPos.y, Hex.HEX_SIZE, Hex.HEX_SIZE);
            sp.setColor(team.getColor());
            sp.draw(LD42.s.batch);
        }

        Sprite sp = LD42.s.getSprite(getMainGraphicName());
        sp.setBounds(currentScreenPos.x, currentScreenPos.y, Hex.HEX_SIZE, Hex.HEX_SIZE);
        sp.setColor(team.getColor());
        sp.draw(LD42.s.batch);

        if (isAccelerating && isAnimating()){
            Sprite sp2 = LD42.s.getSprite("landing_target");
            sp2.setBounds(hex.getScreenX(), hex.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
            sp2.setColor(team.getColor());
            sp2.draw(LD42.s.batch);
        }

        if (currentPath != null && isAtDestination()){
            animationSpeed = 200f;
            isAccelerating = false;

            boolean didSomething = false;

            if (currentPath.get(0).unit == null && moves > 0) {
                moveTo(currentPath.get(0));
                currentPath.remove(0);
                --moves;
                didSomething = true;
            } else if (currentPath.get(0).unit != null && currentPath.get(0).unit.getTeam() != this.getTeam()) {
                if (attacks > 0) {
                    attack(currentPath.get(0).unit);
                    --attacks;
                    currentDestination = null;
                }
            }

            if (currentPath.size() == 0 || !didSomething) currentPath = null;
        }

        if (isAnimating()){
            float moveDist = animationSpeed * Gdx.graphics.getDeltaTime();
            if (Vector2.dst2(hex.getScreenX(), hex.getScreenY(), currentScreenPos.x, currentScreenPos.y) > moveDist*moveDist) {
                Vector2 move = new Vector2(hex.getScreenX() - currentScreenPos.x, hex.getScreenY() - currentScreenPos.y);
                move.nor();
                move.scl(moveDist);
                currentScreenPos.add(move);
            } else {
                currentScreenPos.set(hex.getScreenX(), hex.getScreenY());
            }

            if (isAccelerating){
                animationSpeed += 400f * Gdx.graphics.getDeltaTime();
            }

            if (!isAnimating() && this instanceof Mine){
                LD42.s.gs.recomputeOwnership();
            }
        }
    }

    public void moveTo(Hex hex){
        if (this.hex != null) this.hex.unit = null;

        if (hex != null){
            if (hex.unit != null) return;
        }

        this.hex = hex;

        if (this.hex != null) {
            this.hex.unit = this;
        }
    }

    public abstract String getMainGraphicName();

    public static Unit factory(UnitType type){
        switch (type){
            case Mine: return new Mine();
            case Tank: return new Tank();
            case Scout: return new Scout();
            case Turret: return new Turret();
            case Block: return new Block();
        }

        return null;
    }

    public Team getTeam() {
        return team;
    }

    public Unit setTeam(Team team) {
        this.team = team;
        return this;
    }

    public int getHealth() {
        return health;
    }

    public Hex getHex() {
        return hex;
    }

    public boolean canBeSelected(){
        return true;
    }

    public List<Hex> pathTo(Hex destHex){
        GraphPath<Hex> hexPath = new DefaultGraphPath<>();
        LD42.s.gs.pathFinder.searchNodePath(this.getHex(), destHex, LD42.s.gs.defaultHeuristic, hexPath);

        List<Hex> ret = new ArrayList<>();

        //System.err.println("START");
        for (int i=1;i<hexPath.getCount();++i){
            //hexPath.get(i).isOnCurrentPath = true;
            ret.add(hexPath.get(i));
        }

        return ret;
    }

    public void executeMoves(){
        if (currentDestination != null && currentDestination != hex){
            currentPath = pathTo(currentDestination);
        }
    }

    public void attack(Unit other){
        other.health -= getAttack();

        if (other.health <= 0){
            other.hex.unit = null;
            LD42.s.gs.recomputeOwnership();
        }
    }

    public int getMaxMoves(){
        return 0;
    }

    public Hex getCurrentDestination() {
        return currentDestination;
    }

    public Unit setCurrentDestination(Hex currentDestination) {
        this.currentDestination = currentDestination;
        return this;
    }

    public int getMaxHealth(){
        return 1;
    }

    public int getAttack(){
        return 0;
    }

    public int getCost(){
        return 0;
    }

    public void startFall(){
        currentScreenPos = new Vector2(hex.getScreenX(), hex.getScreenY() + 900);
        isAccelerating = true;
        animationSpeed = 1200f;
    }

    public boolean isAnimating(){
        return !isAtDestination() || currentPath != null;
    }

    public boolean isAtDestination(){
        return Vector2.dst2(hex.getScreenX(), hex.getScreenY(), currentScreenPos.x, currentScreenPos.y) <= 1;
    }
}
