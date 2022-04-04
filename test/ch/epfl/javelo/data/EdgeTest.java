package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {
    private static final double DELTA = 1e-7;

    @Test
    void positionClosestToTest() {
        double aX = 5;
        double aY = 5;
        double bX = 7;
        double bY = 7;
        double pX = 6;
        double pY = 6;
        assertEquals(Math.sqrt(2), Math2.projectionLength(aX, aY,
                bX, bY, pX, pY), DELTA);
    }

    @Test
    void pointAtTest() {

    }
}
