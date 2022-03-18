package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class GraphTest {

    @Test
    void testLoadForm() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph javelo = Graph.loadFrom(basePath);

        System.out.println(javelo.nodeCount());



    }
}
