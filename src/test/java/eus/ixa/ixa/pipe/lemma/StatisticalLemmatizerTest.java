package eus.ixa.ixa.pipe.lemma;

import eus.ixa.ixa.pipe.pos.MorphoFactory;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class StatisticalLemmatizerTest {
    private String posLemmatizerModelName = "models/en/en-test-lemma-perceptron-ud.bin";

    @Test
    public void testLoadWithPath() {
        MorphoFactory morphoFactory = new MorphoFactory();
        String lemmatizerModelFullPathName = StatisticalLemmatizerTest.class.getClassLoader().getResource(posLemmatizerModelName).getPath();

        Properties prop = new Properties();
        prop.setProperty("language", "en");
        prop.setProperty("lemmatizerModel", lemmatizerModelFullPathName);
        prop.setProperty("useModelCache", "false");  //used to force reload
        StatisticalLemmatizer lemmatizer = new StatisticalLemmatizer(prop, morphoFactory);

        assertNotNull(lemmatizer);
    }

    @Test
    public void testLoadWithStream() {
        MorphoFactory morphoFactory = new MorphoFactory();
        InputStream lemmatizerModelAsStream = StatisticalLemmatizerTest.class.getClassLoader().getResourceAsStream(posLemmatizerModelName);

        Properties prop = new Properties();
        prop.setProperty("language", "en");
        prop.setProperty("useModelCache", "false");  //used to force reload
        StatisticalLemmatizer lemmatizer = new StatisticalLemmatizer(lemmatizerModelAsStream, prop, morphoFactory);

        assertNotNull(lemmatizer);

        //used just to provide some more logs
        lemmatizer.lemmatize(new String[]{"the", "cat"}, new String[]{"DET", "NOUN"});
    }
}
