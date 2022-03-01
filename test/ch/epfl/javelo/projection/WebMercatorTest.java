package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {
    private static final double DELTA = 1e-7;

    @Test
    public void xWorksOnKnownValues(){
        var actual1 = WebMercator.x(Math.toRadians(0));
        var expected1 = 1/2;
        assertEquals(expected1, actual1,DELTA);
    }
}
