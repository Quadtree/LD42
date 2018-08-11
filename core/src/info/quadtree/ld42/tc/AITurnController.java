package info.quadtree.ld42.tc;

import info.quadtree.ld42.LD42;

public class AITurnController extends TurnController {
    @Override
    public void render() {
        super.render();

        LD42.s.gs.endTurn();
    }
}
