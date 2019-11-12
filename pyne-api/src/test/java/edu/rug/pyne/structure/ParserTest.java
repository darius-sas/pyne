package edu.rug.pyne.structure;

import edu.rug.pyne.api.parser.Parser;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

public class ParserTest {

    @Test
    void testFindSourceDirs(){
        var rootDir = "";
        var parser = new Parser((Graph)null);
        parser.setRootDirectory(new File("/home/fenn/git/data/repos/mina"));
        var propsFile = new File("/home/fenn/git/data/repos/mina/sources.properties");
        var set = parser.getFromPropertiesFile(propsFile);
        assertTrue(set.contains(new File("/home/fenn/git/data/repos/mina/mina-core/src/main/java")));
        var set2 = parser.findSourceDirectories();
        assertEquals(set, set2);

        parser = new Parser((Graph)null);
        parser.setRootDirectory(new File("/home/fenn/git/data/repos/ant-ivy"));
        set = parser.findSourceDirectories();
        assertFalse(set.isEmpty());
    }
}
