package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Test {
    @Test
    void TestResultAsDoubleQ28_4(){
        int Q28_4Base = 0b0000_0000_0000_0000_0000_0000_1001_1100;
        double expected = 9.75;
        double actual = Q28_4.asDouble(Q28_4Base);
        assertEquals(expected, actual);
    }
    @Test
    void TestResultAsFloatQ28_4(){
        int Q28_4Base = 0b1001_1100;
        double expected = 9.75f;
        double actual = Q28_4.asFloat(Q28_4Base);
        assertEquals(expected, actual);
    }

    @Test
    void TestOfInt1(){
        int inQ28_4 = Q28_4.ofInt(127);
        assertEquals(127.0, Q28_4.asDouble(inQ28_4));
    }


}

