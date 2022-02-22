package ch.epfl.javelo;

public final class Preconditions {

    private Preconditions() {}

    public void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
