package ai.testzombie.demo.support;

public enum MutationLevel {
    OFF(0), LIGHT(1), REALISTIC(2), HARD(3), EXTREME(4);

    private final int value;

    MutationLevel(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
