package info.quadtree.ld42.tc;

public class EnvTurnController extends TurnController {
    @Override
    public void turnStart() {
        super.turnStart();

        /*
        hexStream().forEach(it -> it.ttl--);
            long falling = hexStream().filter(it -> it.ttl <= 0).count();
            if (falling > 0){
                waitForFallTime = 2f;
                return;
            }
            hexStream().filter(it -> it.ttl <= 0).collect(Collectors.toList()).forEach(it -> deleteHex(it.x, it.y));

            System.err.println("Turn num " + (++turnNum));
            recomputeOwnership();
         */
    }
}
