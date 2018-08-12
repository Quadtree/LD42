package info.quadtree.ld42;

public enum DifficultyLevel {
    Easy(5),
    Medium(12),
    Hard(20),
    Impossible(30)
    ;
    int aiResources;

    DifficultyLevel(int aiResources) {
        this.aiResources = aiResources;
    }
}
