package info.quadtree.ld42.tc;

import info.quadtree.ld42.Hex;
import info.quadtree.ld42.LD42;
import info.quadtree.ld42.Team;
import info.quadtree.ld42.Util;
import info.quadtree.ld42.unit.*;

import java.util.*;
import java.util.stream.Collectors;

public class AITurnController extends TurnController {
    public AITurnController(Team team) {
        super(team);
    }

    @Override
    public void turnStart() {
        super.turnStart();

        phase = Phase.DropMinePhase;
        cannotMove = new HashSet<>();
    }

    Phase phase;

    Set<Unit> cannotMove;

    enum Phase {
        DropMinePhase,
        AttackPhase,
        EndTurnPhase
    }

    @Override
    public void render() {
        super.render();

        if (phase == Phase.DropMinePhase) {
            long curMines = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Mine).count();
            long curTanks = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Tank).count();
            long curScouts = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Scout).count();
            long curTurrets = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Turret).count();

            if (false && curTanks < curMines / 2){
                if (!spawnMilitary(Unit.UnitType.Tank)) nextPhase();
            } else if (curScouts < curMines / 1){
                if (!spawnMilitary(Unit.UnitType.Scout)) nextPhase();
            } else if (curTurrets < curMines / 2){
                if (!spawnTurret()) nextPhase();
            } else {
                LD42.s.gs.hexStream().max(Comparator.comparingInt(this::getHexValue)).ifPresent(bestHex -> {
                    if (!team.dropUnit(bestHex, Unit.UnitType.Mine)) nextPhase();
                });
            }
        }

        if (phase == Phase.AttackPhase){
            if (LD42.s.gs.unitStream().noneMatch(Unit::isAnimating)) {
                Optional<Unit> u = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it.getMoves() > 0 && !cannotMove.contains(it)).findAny();

                if (u.isPresent()){
                    Optional<Unit> target = LD42.s.gs.unitStream().filter(it -> it.getTeam() != team).min(Comparator.comparingInt(it -> u.get().pathTo(it.getHex()).size()));

                    if (target.isPresent()){
                        u.get().setCurrentDestination(target.get().getHex());
                        u.get().executeMoves();
                    } else {
                        cannotMove.add(u.get());
                    }
                } else {
                    nextPhase();
                }
            }
        }

        if (phase == Phase.EndTurnPhase) {
            LD42.s.gs.endTurn();
        }
    }

    private boolean spawnMilitary(Unit.UnitType type){
        Optional<Hex> hx = Util.choice(LD42.s.gs.hexStream().filter(it -> it.unit == null && it.owner == team).collect(Collectors.toList()));

        if (hx.isPresent()){
            return team.dropUnit(hx.get(), type);
        }

        return false;
    }

    private boolean spawnTurret(){
        List<Hex> tSpawn = LD42.s.gs.hexStream()
                .filter(it -> it.owner == team || it.owner == Team.Nobody && it.getNStream()
                        .anyMatch(it2 -> it2.unit != null && it2.unit instanceof Mine && it2.unit.getTeam() == team))
                .collect(Collectors.toList());

        Optional<Hex> hx = Util.choice(tSpawn);

        if (hx.isPresent()) {
            return team.dropUnit(hx.get(), Unit.UnitType.Turret);
        }

        return false;
    }

    private int getHexValue(Hex hex){
        return hex.getExistingTwoLevelNeighbors().stream().filter(it -> it.owner == Team.Nobody).mapToInt(it -> it.ttl).sum();
    }

    private void nextPhase(){
        phase = Phase.values()[phase.ordinal() + 1];
        System.out.println("Now on " + phase + " phase");
    }
}
