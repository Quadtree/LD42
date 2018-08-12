package info.quadtree.ld42;

public enum DifficultyLevel {
    Easy(4),
    Medium(10),
    Hard(20),
    Impossible(40)
    ;
    int aiResources;

    DifficultyLevel(int aiResources) {
        this.aiResources = aiResources;
    }
}
