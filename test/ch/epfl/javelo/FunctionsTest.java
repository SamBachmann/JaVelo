package ch.epfl.javelo;


import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionsTest {
    @Test
    void ConstantTest(){
         DoubleUnaryOperator maConstante = Functions.constant(Double.MAX_VALUE);
        assertEquals(maConstante.applyAsDouble(34), Double.MAX_VALUE);
    }

    @Test
    void InterpolateTest(){
        float tab[] = {2,3,3,3,5,4,2};
        DoubleUnaryOperator FonctionInterpolation = Functions.sampled(tab,3);
        assertEquals(2.5, FonctionInterpolation.applyAsDouble(0.25));
    }

}
