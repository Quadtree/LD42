package info.quadtree.ld42;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import info.quadtree.ld42.tc.AITurnController;
import info.quadtree.ld42.tc.EnvTurnController;
import info.quadtree.ld42.tc.TurnController;
import info.quadtree.ld42.unit.Mine;
import info.quadtree.ld42.unit.Unit;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameState implements IndexedGraph<Hex> {
    Hex[] hexes;

    public static final int GRID_WIDTH = 22;
    public static final int GRID_HEIGHT = 44;

    public static final int CENTER_VARIANCE = 8;

    Unit.UnitType selectedUnitTypeToPlace = null;

    public Unit selectedUnit = null;

    public IndexedAStarPathFinder<Hex> pathFinder;

    public List<Team> turnOrder = new ArrayList<>();
    Team currentTurnTeam;

    float waitForFallTime = 0f;

    public Map<Team, Integer> money = new EnumMap<>(Team.class);
    public Map<Team, Integer> points = new EnumMap<>(Team.class);
    public Map<Team, TurnController> controllerMap = new EnumMap<>(Team.class);

    public int turnNum = 0;

    boolean endTurnInProgress = false;

    public GameState(){

    }

    public void endTurn(){
        currentTurnTeam.endTurn();
        endTurnInProgress = true;
    }

    private void growFrom(Hex it){
        long hexCount = Arrays.stream(hexes).filter(Objects::nonNull).count();

        if (MathUtils.random(2) == 0 || hexCount < 200) {
            HexPos neighbor = MathUtils.randomBoolean() ? it.getNeighbors()[MathUtils.random(1) * 3 + 2] : it.getNeighbors()[MathUtils.random(5)];
            if (!(neighbor instanceof Hex)) {
                Hex nx = new Hex(neighbor.x, neighbor.y, 1);
                setHex(nx);
                if (MathUtils.random(6) == 0) growFrom(it);
            } else {
                ((Hex) neighbor).ttl += 2;
            }
        }
    }

    public void generate(){
        pathFinder = new IndexedAStarPathFinder<>(this);

        while(true) {
            hexes = new Hex[GRID_WIDTH * GRID_HEIGHT];

            int seeds = MathUtils.random(2, 5);

            for (int i=0;i<seeds;++i) setHex(new Hex(MathUtils.random(GRID_WIDTH / 2 - CENTER_VARIANCE, GRID_WIDTH / 2 + CENTER_VARIANCE*2), MathUtils.random(GRID_HEIGHT / 2 - CENTER_VARIANCE, GRID_HEIGHT / 2 + CENTER_VARIANCE*2), 1));


            for (int i = 0; i < 30; ++i) {
                final int fi = i;
                Arrays.stream(hexes).filter(Objects::nonNull).forEach(this::growFrom);
            }

            long badHexCount = Arrays.stream(hexes).filter(Objects::nonNull).filter(it -> it.x == 0 || it.y == 0 || it.x == GRID_WIDTH - 1 || it.y == GRID_HEIGHT - 1).count();
            long hexCount = Arrays.stream(hexes).filter(Objects::nonNull).count();
            System.out.println("BHC="+badHexCount + " HC=" + hexCount);

            if (hexCount >= 300) break;
        }

        hexStream().forEach(it -> it.ttl += MathUtils.random(1));

        Arrays.stream(hexes)
                .filter(Objects::nonNull).filter(it -> it.x <= 2 || it.y <= 2 || it.x >= GRID_WIDTH - 3 || it.y >= GRID_HEIGHT - 3)
                .collect(Collectors.toList())
                .forEach(it -> {
                    int distToSide = Math.min(Math.min(Math.min(it.x, it.y), GRID_WIDTH - 1 - it.x), GRID_HEIGHT - 1 - it.y);
                    if (MathUtils.random(distToSide) == 0) deleteHex(it.x, it.y);
                });

        Unit mine = new Mine();
        mine.setTeam(Team.DigCorp).moveTo(Arrays.stream(hexes).filter(Objects::nonNull).findAny().get());
        mine.startFall();

        recomputeOwnership();

        turnOrder.add(Team.Overminers);
        turnOrder.add(Team.DigCorp);
        turnOrder.add(Team.Underminers);
        turnOrder.add(Team.InterstellarElectric);

        for(Team t : turnOrder){
            money.put(t, 20);
            points.put(t, 0);
            controllerMap.put(t, new AITurnController());
        }

        controllerMap.put(Team.Overminers, new TurnController());
        money.put(Team.Nobody, 0);
        points.put(Team.Nobody, 0);
        controllerMap.put(Team.Nobody, new EnvTurnController());

        Collections.shuffle(turnOrder);

        turnOrder.add(Team.Nobody);

        currentTurnTeam = turnOrder.get(0);
        currentTurnTeam.beginTurn();
    }

    public void beginTurn(){
        currentTurnTeam.beginTurn();
        controllerMap.get(currentTurnTeam).turnStart();
    }

    public Hex getHex(int x, int y){
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) return null;

        int idx = x * GRID_HEIGHT + y;

        Hex ret = hexes[idx];

        if (ret != null) {
            if (ret.x != x || ret.y != y){
                System.err.println("!!!! MISMATCH " + ret.x + " != " + x + " || " + ret.y + " != " + y);
            }
        }

        return ret;
    }

    public void setHex(Hex hex){
        int x = hex.x;
        int y = hex.y;
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) return;
        hexes[x * GRID_HEIGHT + y] = hex;

        if (hex != null) {
            assert (hex.x == x);
            assert (hex.y == y);
        }
    }

    public void checkHexInvariants(){
        for (int i=0;i<hexes.length;++i){
            if (hexes[i] != null){
                int expectedI = hexes[i].x * GRID_HEIGHT + hexes[i].y;
                if (i != expectedI){
                    System.err.println("MISMATCH " + i + " != " + expectedI);
                }
            }
        }
    }

    public void deleteHex(int x, int y){
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) return;
        hexes[x * GRID_HEIGHT + y] = null;
    }

    public void render(){
        if (!endTurnInProgress) {
            controllerMap.get(currentTurnTeam).render();
        } else {
            if (hexStream().noneMatch(it -> it.unit != null && it.unit.isAnimating())) {
                currentTurnTeam = turnOrder.get((turnOrder.indexOf(currentTurnTeam) + 1) % turnOrder.size());
                beginTurn();
                endTurnInProgress = false;
            }
        }

        Arrays.stream(hexes).filter(Objects::nonNull).sorted(Comparator.comparingInt(Hex::getY).reversed()).forEach(Hex::render);
        Arrays.stream(hexes).filter(Objects::nonNull).sorted(Comparator.comparingInt(Hex::getY).reversed()).forEach(Hex::render2);
    }

    public Optional<Hex> getHexAtScreenPos(int x, int y){
        return Arrays.stream(hexes).filter(Objects::nonNull).filter(it ->
                (Math.pow((it.getScreenX() + Hex.HEX_SIZE / 2.0) - x, 2) +
                Math.pow((it.getScreenY() + Hex.HEX_SIZE / 2.0) - y, 2)) < 16*16
        ).findAny();
    }

    public void recomputeOwnership(){
        Arrays.stream(hexes).filter(Objects::nonNull).forEach(Hex::recalcOwnership);
    }

    @Override
    public int getIndex(Hex node) {
        return node.x * GRID_HEIGHT + node.y;
    }

    @Override
    public int getNodeCount() {
        return GRID_WIDTH * GRID_HEIGHT;
    }

    @Override
    public Array<Connection<Hex>> getConnections(Hex fromNode) {
        Array<Connection<Hex>> ret = new Array<>();

        Arrays.stream(fromNode.getNeighbors()).forEach(it ->{
            if (it instanceof Hex) ret.add(new DefaultConnection<>(fromNode, (Hex)it));
        });

        return ret;
    }

    public Stream<Hex> hexStream(){
        return Arrays.stream(hexes).filter(Objects::nonNull);
    }

    public Heuristic<Hex> defaultHeuristic = (node, endNode) -> Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y);
}
