package info.quadtree.ld42.tc;

import info.quadtree.ld42.Hex;
import info.quadtree.ld42.LD42;
import info.quadtree.ld42.Team;
import info.quadtree.ld42.unit.Mine;
import info.quadtree.ld42.unit.Unit;

import java.util.Comparator;
import java.util.List;

public class AITurnController extends TurnController {
    public AITurnController(Team team) {
        super(team);
    }

    @Override
    public void turnStart() {
        super.turnStart();

        phase = Phase.DropMinePhase;
    }

    Phase phase;

    enum Phase {
        DropMinePhase,
        EndTurnPhase
    }

    @Override
    public void render() {
        super.render();

        if (phase == Phase.DropMinePhase) {
            long curMines = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Mine).count();

            if (curMines < 3) {
                LD42.s.gs.hexStream().max(Comparator.comparingInt(this::getHexValue)).ifPresent(bestHex -> {
                    if (!team.dropUnit(bestHex, Unit.UnitType.Mine)) nextPhase();
                });
            } else {
                nextPhase();
            }
        }

        if (phase == Phase.EndTurnPhase) {
            LD42.s.gs.endTurn();
        }
    }

    private int getHexValue(Hex hex){
        return hex.getExistingTwoLevelNeighbors().stream().filter(it -> it.owner == Team.Nobody).mapToInt(it -> it.ttl).sum();
    }

    private void nextPhase(){
        phase = Phase.values()[phase.ordinal() + 1];
        System.out.println("Now on " + phase + " phase");
    }
}
