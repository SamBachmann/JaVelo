package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevationProfilTest {
    float[] samples = {22f,23f,23f,22f, 19f, 20f, 20.5f};
    double length = 9.0;

    @Test
    void testElevationProfil(){
        ElevationProfile monProfil = new ElevationProfile(length, samples);

        assertEquals(23.0, monProfil.maxElevation());
        assertEquals(19.0, monProfil.minElevation());
        assertEquals(2.5, monProfil.totalAscent());
        assertEquals(4.0, monProfil.totalDescent());
    }

    @Test
    void testElevationAt(){
        ElevationProfile monProfil = new ElevationProfile(length, samples);
        assertEquals(22.0, monProfil.elevationAt(-0.23));
        assertEquals(20.5, monProfil.elevationAt(9.2));
        assertEquals(23.0, monProfil.elevationAt(2));
    }
}
