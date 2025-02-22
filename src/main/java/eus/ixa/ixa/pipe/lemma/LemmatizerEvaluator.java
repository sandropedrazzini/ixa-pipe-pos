/*
 * Copyright 2016 Rodrigo Agerri

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
package eus.ixa.ixa.pipe.lemma;

import opennlp.tools.util.eval.Evaluator;
import opennlp.tools.util.eval.Mean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The {@link LemmatizerEvaluator} measures the performance of
 * the given {@link Lemmatizer} with the provided reference
 * {@link LemmaSample}s.
 */
public class LemmatizerEvaluator extends Evaluator<LemmaSample> {
  private static final Logger logger = LogManager.getLogger(LemmatizerEvaluator.class);

  private Lemmatizer lemmatizer;

  private Mean wordAccuracy = new Mean();

  /**
   * Initializes the current instance.
   *
   * @param aLemmatizer a lemmatizer
   * @param listeners an array of evaluation listeners
   */
  public LemmatizerEvaluator(Lemmatizer aLemmatizer, LemmatizerEvaluationMonitor ... listeners) {
    super(listeners);
    this.lemmatizer = aLemmatizer;
  }

  /**
   * Evaluates the given reference {@link LemmaSample} object.
   *
   * This is done by tagging the sentence from the reference
   * {@link LemmaSample} with the {@link Lemmatizer}. The
   * tags are then used to update the word accuracy score.
   *
   * @param reference the reference {@link LemmaSample}.
   *
   * @return the predicted {@link LemmaSample}.
   */
  @Override
  protected LemmaSample processSample(LemmaSample reference) {

    String[] predictedLemmas = lemmatizer.lemmatize(reference.getTokens(), reference.getTags());
    String[] referenceLemmas = reference.getLemmas();
    
    for (int i = 0; i < referenceLemmas.length; i++) {
      logger.debug("-> Reference: " + referenceLemmas[i]);
      
      if (referenceLemmas[i].equals(predictedLemmas[i])) {
        wordAccuracy.add(1);
      }
      else {
        wordAccuracy.add(0);
      }
    }
    return new LemmaSample(reference.getTokens(), reference.getTags(), predictedLemmas);
  }

  /**
   * Retrieves the word accuracy.
   *
   * This is defined as:
   * word accuracy = correctly detected tags / total words
   *
   * @return the word accuracy
   */
  public double getWordAccuracy() {
    return wordAccuracy.mean();
  }

  /**
   * Retrieves the total number of words considered
   * in the evaluation.
   *
   * @return the word count
   */
  public long getWordCount() {
    return wordAccuracy.count();
  }

  /**
   * Represents this objects as human readable {@link String}.
   */
  @Override
  public String toString() {
    return "Accuracy:" + wordAccuracy.mean() +
        " Number of Samples: " + wordAccuracy.count();
  }

}