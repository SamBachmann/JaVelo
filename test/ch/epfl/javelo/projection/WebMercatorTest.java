package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {
    private static final double DELTA = 1e-7;

    // Fonctionne.
    @Test
    public void xWorksOnKnownValues(){
        var actual = WebMercator.x(Math.PI / 2);
        var expected = 0.75;
        assertEquals(expected, actual,DELTA);
    }

    // Fonctionne (expected trouvé grâce à la calculette).
    @Test
    public void yWorksOnKnownValues(){
        var actual = WebMercator.y(3.0/4 * Math.PI);
        var expected = 0.6402749631;
        assertEquals(expected, actual,DELTA);
    }

    // Fonctionne.
    @Test
    public void lonWorksOnKnownValues(){
        var actual = WebMercator.lon(50);
        var expected = 99 * Math.PI;
        assertEquals(expected, actual, DELTA);
    }


    // Fonctionne (expected trouvé grâce à la calculette).
    @Test
    public void latWorksOnKnownValues(){
        var actual = WebMercator.lat(50);
        var expected = -1.57079633;
        assertEquals(expected, actual, DELTA);
    }
}
