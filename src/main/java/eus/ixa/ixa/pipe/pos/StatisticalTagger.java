/*Copyright 2014 Rodrigo Agerri

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package eus.ixa.ixa.pipe.pos;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POS tagging module based on Apache OpenNLP machine learning API.
 * 
 * @author ragerri
 * @version 2014-04-24
 */

public class StatisticalTagger {
  private static final Logger logger = LogManager.getLogger(StatisticalTagger.class);
  /**
   * The morpho tagger.
   */
  private final POSTaggerME posTagger;
  /**
   * The models to use for every language. The keys of the hashmap are the language
   * codes, the values the models.
   */
  private final static ConcurrentHashMap<String, POSModel> posModels = new ConcurrentHashMap<String, POSModel>();
  /**
   * The morpho factory.
   */
  private MorphoFactory morphoFactory;

  /**
   * Construct a morphotagger.
   * 
   * @param props
   *          the properties object
   */
  public StatisticalTagger(final Properties props) {
    this(props, null);
  }

  /**
   * Construct a morphotagger with {@code MorphoFactory}.
   * 
   * @param props
   *          the properties object
   * @param aMorphoFactory
   *          the morpho factory
   */
  public StatisticalTagger(final Properties props, final MorphoFactory aMorphoFactory) {
    final String lang = props.getProperty("language");
    final String model = props.getProperty("model");
    final Boolean useModelCache = Boolean.valueOf(props.getProperty("useModelCache", "true"));
    final POSModel posModel = loadModel(lang, model, useModelCache);
    this.posTagger = new POSTaggerME(posModel);
    this.morphoFactory = aMorphoFactory;
  }

  /**
   * Construct a morphotagger with {@code MorphoFactory}.
   *
   * @param modelAsStream
   *          passing the model as stream allows a more flexible classpath management
   * @param props
   *          the properties object
   * @param aMorphoFactory
   *          the morpho factory
   */
  public StatisticalTagger(final InputStream modelAsStream, final Properties props, final MorphoFactory aMorphoFactory) {
    final String lang = props.getProperty("language");
    final Boolean useModelCache = Boolean.valueOf(props.getProperty("useModelCache", "true"));
    final POSModel posModel = loadModelAsStream(lang, modelAsStream, useModelCache);
    this.posTagger = new POSTaggerME(posModel);
    this.morphoFactory = aMorphoFactory;
  }

  /**
   * Get morphological analysis from a tokenized sentence.
   * 
   * @param tokens
   *          the tokenized sentence
   * @return a list of {@code Morpheme} objects containing morphological info
   */
  public final List<Morpheme> getMorphemes(final String[] tokens) {
    final List<String> origPosTags = posAnnotate(tokens);
    final List<Morpheme> morphemes = getMorphemesFromStrings(origPosTags,
        tokens);
    return morphemes;
  }

  /**
   * Produce postags from a tokenized sentence.
   * 
   * @param tokens
   *          the sentence
   * @return a list containing the postags
   */
  public final List<String> posAnnotate(final String[] tokens) {
    final String[] annotatedText = this.posTagger.tag(tokens);
    final List<String> posTags = new ArrayList<String>(
        Arrays.asList(annotatedText));
    return posTags;
  }
  
  /**
   * Produces a multidimensional array containing all the tagging
   * possible for a given sentence.
   * @param tokens the tokens
   * @return the array containing for each row the tags
   */
  public final String[][] getAllPosTags(final String[] tokens) {
    final String[][] allPosTags = this.posTagger.tag(13, tokens);
    return allPosTags;
  }

  /**
   * Create {@code Morpheme} objects from the output of posAnnotate.
   * 
   * @param posTags
   *          the postags
   * @param tokens
   *          the tokens
   * @return a list of morpheme objects
   */
  public final List<Morpheme> getMorphemesFromStrings(
      final List<String> posTags, final String[] tokens) {
    final List<Morpheme> morphemes = new ArrayList<Morpheme>();
    for (int i = 0; i < posTags.size(); i++) {
      final String word = tokens[i];
      final String tag = posTags.get(i);
      final Morpheme morpheme = this.morphoFactory.createMorpheme(word, tag);
      morphemes.add(morpheme);
    }
    return morphemes;
  }

  /**
   * Loads statically the probabilistic model. Every instance of this finder
   * will share the same model.
   * 
   * @param lang
   *          the language
   * @param modelName
   *          the model to be loaded
   * @param useModelCache
   *          whether to cache the model in memory
   * @return the model as a {@link POSModel} object
   */
  private POSModel loadModel(final String lang, final String modelName, final Boolean useModelCache) {
    FileInputStream fileInputStream;
    try {
      fileInputStream = new FileInputStream(modelName);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    return loadModelAsStream(lang,fileInputStream, useModelCache);
  }

  /**
   * Loads statically the probabilistic model. Every instance of this finder
   * will share the same model.
   * The second argument is an already set InputStream. This allow a better classpath management.
   *
   * @param lang
   *          the language
   * @param modelAsStream
   *          the model to be loaded
   * @param useModelCache
   *          whether to cache the model in memory
   * @return the model as a {@link POSModel} object
   */
  private POSModel loadModelAsStream(final String lang, InputStream modelAsStream, final Boolean useModelCache) {
    final long lStartTime = new Date().getTime();
    POSModel model = null;
    try {
      if (useModelCache) {
        synchronized (posModels) {
          model = posModels.get(lang);
          if (model == null) {
            model = new POSModel(modelAsStream);
            posModels.put(lang, model);
          } 
        }
      } else {
        model = new POSModel(modelAsStream);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
    final long lEndTime = new Date().getTime();
    final long difference = lEndTime - lStartTime;
    logger.info("ixa-pipe-pos model loaded in: " + difference  + " miliseconds ... [DONE]");
    return model;
  }
}
