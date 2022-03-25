package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ElevationProfileComputerTest {
    @Test
    void ElevationProfilComputerWorksOnEmptyTable(){
        var tableauNan = new float[]{Float.NaN, Float.NaN,Float.NaN, Float.NaN};

        var expected = new float[]{0f,0f,0f,0f};
        //var actual = ElevationProfileComputer.elevationProfile()
        //assertEquals();
    }
}
