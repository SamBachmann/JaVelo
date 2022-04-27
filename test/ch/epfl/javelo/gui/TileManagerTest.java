package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileManagerTest {

    @Test
    void graphLoadFromWorksOnLausanneData() throws IOException {
        var graph = Graph.loadFrom(Path.of("lausanne"));

        // Check that nodes.bin was properly loaded
        var actual1 = graph.nodeCount();
        var expected1 = 212679;
        assertEquals(expected1, actual1);

        var actual2 = graph.nodeOutEdgeId(2022, 0);
        var expected2 = 4095;
        assertEquals(expected2, actual2);

        // Check that edges.bin was properly loaded
        var actual3 = graph.edgeLength(2022);
        var expected3 = 17.875;
        assertEquals(expected3, actual3);

        // Check that profile_ids.bin and elevations.bin was properly loaded
        var actual4 = graph.edgeProfile(2022).applyAsDouble(0);
        var expected4 = 625.5625;
        assertEquals(expected4, actual4);

        // Check that attributes.bin and elevations.bin was properly loaded
        var actual5 = graph.edgeAttributes(2022).bits();
        var expected5 = 16;
        assertEquals(expected5, actual5);
    }
}
