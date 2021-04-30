package eus.ixa.ixa.pipe.pos;

import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class StatisticalTaggerTest {
    private String posTaggerModelName = "models/en/en-test-pos-perceptron-autodict01-ud.bin";
    
    @Test
    public void testLoadWithPath() {
        MorphoFactory morphoFactory = new MorphoFactory();
        String posTaggerModelFullPathName = StatisticalTaggerTest.class.getClassLoader().getResource(posTaggerModelName).getPath();
        
        Properties prop = new Properties();
        prop.setProperty("language", "en");
        prop.setProperty("model", posTaggerModelFullPathName);
        prop.setProperty("useModelCache", "false");  //used to force reload
        StatisticalTagger posTagger = new StatisticalTagger(prop, morphoFactory);

        assertNotNull(posTagger);
    }

    @Test
    public void testLoadWithStream() {
        MorphoFactory morphoFactory = new MorphoFactory();
        InputStream taggerModeAsStream = StatisticalTaggerTest.class.getClassLoader().getResourceAsStream(posTaggerModelName);
        Properties prop = new Properties();
        prop.setProperty("language", "en");
        prop.setProperty("useModelCache", "false");  //used to force reload
        StatisticalTagger posTagger = new StatisticalTagger(taggerModeAsStream, prop, morphoFactory);

        assertNotNull(posTagger);
    }
}
