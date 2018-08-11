package info.quadtree.ld42.tc;

import com.badlogic.gdx.Gdx;
import info.quadtree.ld42.LD42;

import java.util.stream.Collectors;

public class EnvTurnController extends TurnController {
    float waitForFallTime = 0f;

    @Override
    public void turnStart() {
        super.turnStart();

        LD42.s.gs.hexStream().forEach(it -> it.ttl--);
        long falling = LD42.s.gs.hexStream().filter(it -> it.ttl <= 0).count();
        if (falling > 0){
            waitForFallTime = 2f;
        }
        LD42.s.gs.hexStream().filter(it -> it.ttl <= 0).collect(Collectors.toList()).forEach(it -> LD42.s.gs.deleteHex(it.getX(), it.getY()));

        System.err.println("Turn num " + (++LD42.s.gs.turnNum));
        LD42.s.gs.recomputeOwnership();
    }

    @Override
    public void render() {
        super.render();

        waitForFallTime -= Gdx.graphics.getDeltaTime();

        if (waitForFallTime <= 0){
            LD42.s.gs.endTurn();
        }
    }
}
