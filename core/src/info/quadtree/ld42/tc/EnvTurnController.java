package info.quadtree.ld42.tc;

import com.badlogic.gdx.Gdx;
import info.quadtree.ld42.LD42;
import info.quadtree.ld42.Team;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvTurnController extends TurnController {
    float waitForFallTime = 0f;

    public EnvTurnController(Team team) {
        super(team);
    }

    @Override
    public void turnStart() {
        super.turnStart();

        LD42.s.gs.hexStream().forEach(it -> it.ttl--);
        long falling = LD42.s.gs.hexStream().filter(it -> it.ttl <= 0).count();
        if (falling > 0){
            waitForFallTime = 1.25f;
        }
    }

    @Override
    public void render() {
        super.render();

        waitForFallTime -= Gdx.graphics.getDeltaTime();

        if (waitForFallTime <= 0){
            System.err.println("Turn num " + (++LD42.s.gs.turnNum));
            LD42.s.gs.hexStream().filter(it -> it.ttl <= 0).collect(Collectors.toList()).forEach(it -> LD42.s.gs.deleteHex(it.getX(), it.getY()));

            long numLeft = LD42.s.gs.hexStream().count();
            if (numLeft == 0){
                LD42.s.gs.points.entrySet().stream().filter(it -> it.getKey() != Team.Nobody).max(Comparator.comparingInt(Map.Entry::getValue)).ifPresent(it -> {
                    LD42.s.gs.winner = it.getKey();
                });
            }

            LD42.s.gs.recomputeOwnership();
            LD42.s.gs.endTurn();
        }
    }
}
