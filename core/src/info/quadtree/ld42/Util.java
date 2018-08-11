package info.quadtree.ld42;

import com.badlogic.gdx.math.MathUtils;

import java.util.List;
import java.util.Optional;

public class Util {
    public static <T> Optional<T> choice(List<T> options){
        if (options == null || options.size() == 0) return Optional.empty();
        return Optional.ofNullable(options.get(MathUtils.random(options.size() - 1)));
    }
}
