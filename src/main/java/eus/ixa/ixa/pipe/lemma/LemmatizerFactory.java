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

import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.ext.ExtensionLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LemmatizerFactory extends BaseToolFactory {
  private static final Logger logger = LogManager.getLogger(LemmatizerFactory.class);
  /**
   * Creates a {@link LemmatizerFactory} that provides the default implementation
   * of the resources.
   */
  public LemmatizerFactory() {
  }

  public static LemmatizerFactory create(String subclassName)
      throws InvalidFormatException {
    if (subclassName == null) {
      // will create the default factory
      return new LemmatizerFactory();
    }
    try {
      LemmatizerFactory theFactory = ExtensionLoader.instantiateExtension(
          LemmatizerFactory.class, subclassName);
      return theFactory;
    } catch (Exception e) {
      String msg = "Could not instantiate the " + subclassName  + ". The initialization throw an exception.";

      logger.error(msg);
      e.printStackTrace();
      throw new InvalidFormatException(msg, e);
    }
  }

  @Override
  public void validateArtifactMap() throws InvalidFormatException {
    // no additional artifacts
  }

  public SequenceValidator<String> getSequenceValidator() {
    return new DefaultLemmatizerSequenceValidator();
  }

  public LemmatizerContextGenerator getContextGenerator() {
    return new DefaultLemmatizerContextGenerator();
  }
}
