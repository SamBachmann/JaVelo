package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointWebMercatorTest {
    private static final double DELTA = 1e-7;

    // Problème, car les coordonnées zoomées sont hors de l'intervalle [0;1] donc
    // ne passent pas les préconditions.
    @Test
    public void ofWorksOnKnownValues(){
        var actual = PointWebMercator.of(19,
                69561722, 47468099);
        var expectedX19 = 0.518275214444;
        var expectedY19 = 0.353664894749;
        var expectedPointWebMercator = new PointWebMercator(expectedX19, expectedY19);
        assertEquals(expectedX19, actual.x(), DELTA);
        assertEquals(expectedY19, actual.y(), DELTA);
        //assertEquals(expectedPointWebMercator, actual);
    }

    // La méthode ofPointCh est constituée de méthodes déjà testées.
    // La méthode toPointCh est constituée de méthodes déjà testées.

    @Test
    public void TESTofWorksOnKnownValues(){
        var actual = PointWebMercator.of(19,
                69561722, 47468099);
        System.out.println(new PointWebMercator(69561722 * Math.pow(2, -(8 + 19)),
                47468099 * Math.pow(2, -(8 + 19))));

    }
}
