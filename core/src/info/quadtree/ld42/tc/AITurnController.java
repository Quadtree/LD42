package info.quadtree.ld42.tc;

import info.quadtree.ld42.LD42;
import info.quadtree.ld42.Team;
import info.quadtree.ld42.unit.Mine;

public class AITurnController extends TurnController {
    public AITurnController(Team team) {
        super(team);
    }

    @Override
    public void render() {
        super.render();

        long curMines = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Mine).count();

        if (curMines < 3){
            
        }

        //LD42.s.gs.endTurn();
    }
}
