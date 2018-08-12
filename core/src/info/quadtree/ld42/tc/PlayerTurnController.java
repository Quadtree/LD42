package info.quadtree.ld42.tc;

import info.quadtree.ld42.Team;
import info.quadtree.ld42.Util;

public class PlayerTurnController extends TurnController {
    public PlayerTurnController(Team team) {
        super(team);
    }

    @Override
    public void turnStart() {
        super.turnStart();

        Util.showTutorialText("Welcome to the Overminers Inc. team! These floating platforms contain valuable ore. Drop a mining base on them from our orbiting fleet by clicking on the mine button or pressing 1.");
    }
}
