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
        cannotAttack = new HashSet<>();
    }

    Phase phase;

    Set<Unit> cannotMove;
    Set<Unit> cannotAttack;

    enum Phase {
        DropMinePhase,
        AttackPhase,
        TurretPhase,
        EndTurnPhase
    }

    private int sizePath(List<Hex> path){
        if (path.size() > 0) return path.size();
        return Integer.MAX_VALUE;
    }

    @Override
    public void render() {
        super.render();

        int turretRatio = 0;
        switch (team){
            case InterstellarElectric: turretRatio = 0; break;
            case DigCorp: turretRatio = 0; break;
            case Underminers: turretRatio = 200; break;
        }

        int scoutRatio = 0;
        switch (team){
            case InterstellarElectric: scoutRatio = 0; break;
            case DigCorp: scoutRatio = 120; break;
            case Underminers: scoutRatio = 25; break;
        }

        int tankRatio = 0;
        switch (team){
            case InterstellarElectric: tankRatio = 0; break;
            case DigCorp: tankRatio = 25; break;
            case Underminers: tankRatio = 65; break;
        }

        if (phase == Phase.DropMinePhase) {
            long curMines = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Mine).count();
            long curTanks = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Tank).count();
            long curScouts = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Scout).count();
            long curTurrets = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it instanceof Turret).count();

            if (curScouts < curMines * scoutRatio / 100) {
                if (!spawnMilitary(Unit.UnitType.Scout)) nextPhase();
            } else if (curTanks < curMines * tankRatio / 100){
                if (!spawnMilitary(Unit.UnitType.Tank)) nextPhase();
            } else if (curTurrets < curMines * turretRatio / 100){
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
                    Optional<Unit> target = LD42.s.gs.unitStream().filter(it -> it.getTeam() != team && it.getAttack() < u.get().getAttack()).min(Comparator.comparingInt(it -> sizePath(u.get().pathTo(it.getHex()))));

                    cannotMove.add(u.get());

                    if (target.isPresent()){
                        u.get().setCurrentDestination(target.get().getHex());
                        u.get().executeMoves();
                    }
                } else {
                    nextPhase();
                }
            }
        }

        if (phase == Phase.TurretPhase){
            if (LD42.s.gs.unitStream().noneMatch(Unit::isAnimating)) {
                Optional<Unit> u = LD42.s.gs.unitStream().filter(it -> it.getTeam() == team && it.getAttacks() > 0 && !cannotAttack.contains(it)).findAny();

                if (u.isPresent()){
                    cannotAttack.add(u.get());

                    Optional<Hex> target = u.get().getHex().getNStream().filter(it -> it.unit != null && it.unit.getTeam() != team && it.unit.getAttack() < u.get().getAttack()).findAny();

                    if (target.isPresent()){
                        u.get().attack(target.get().unit);
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
                .filter(it -> it.owner == team &&
                        it.getNStream().anyMatch(it2 -> it2.unit != null && it2.unit instanceof Mine && it2.unit.getTeam() == team) &&
                        it.getNStream().noneMatch(it2 -> it2.unit != null && it2.unit instanceof Turret && it2.unit.getTeam() == team))
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
