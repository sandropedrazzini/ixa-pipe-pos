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

import eus.ixa.ixa.pipe.pos.StringUtils;
import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Reads data for training and testing. The format consists of:
 * word\tabpostag\tablemma.
 * @author ragerri
 * @version 2016-02-16
 */
public class LemmaSampleStream extends FilterObjectStream<String, LemmaSample> {
  private static final Logger logger = LogManager.getLogger(LemmaSampleStream.class);
  public LemmaSampleStream(ObjectStream<String> samples) {
    super(samples);
  }

  public LemmaSample read() throws IOException {

    List<String> toks = new ArrayList<String>();
    List<String> tags = new ArrayList<String>();
    List<String> preds = new ArrayList<String>();

    for (String line = samples.read(); line != null && !line.equals(""); line = samples.read()) {
      String[] parts = line.split("\t");
      if (parts.length != 3) {
        logger.error("Skipping corrupt line: " + line);
      }
      else {
        toks.add(parts[0]);
        tags.add(parts[1]);
        String ses = StringUtils.getShortestEditScript(parts[0], parts[2]);
        preds.add(ses);
      }
    }
    if (toks.size() > 0) {
      LemmaSample lemmaSample = new LemmaSample(toks.toArray(new String[toks.size()]), tags.toArray(new String[tags.size()]), preds.toArray(new String[preds.size()]));
      return lemmaSample;
    }
    else {
      return null;
    }
  }
}