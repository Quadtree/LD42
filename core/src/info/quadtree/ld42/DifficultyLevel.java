package info.quadtree.ld42;

public enum DifficultyLevel {
    Easy(10),
    Medium(15),
    Hard(20),
    Impossible(35)
    ;
    int aiResources;

    DifficultyLevel(int aiResources) {
        this.aiResources = aiResources;
    }
}
