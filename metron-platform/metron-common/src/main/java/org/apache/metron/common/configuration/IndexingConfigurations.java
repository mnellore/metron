/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.metron.common.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.metron.stellar.common.utils.ConversionUtils;
import org.apache.metron.common.utils.JSONUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class IndexingConfigurations extends Configurations {
  public static final String BATCH_SIZE_CONF = "batchSize";
  public static final String ENABLED_CONF = "enabled";
  public static final String INDEX_CONF = "index";
  public static final String OUTPUT_PATH_FUNCTION_CONF = "outputPathFunction";

  public Map<String, Object> getSensorIndexingConfig(String sensorType, String writerName) {
    Map<String, Object> ret = (Map<String, Object>) configurations.get(getKey(sensorType));
    if(ret == null) {
      return new HashMap();
    }
    else {
      Map<String, Object> writerConfig = (Map<String, Object>)ret.get(writerName);
      return writerConfig != null?writerConfig:new HashMap<>();
    }
  }

  public void updateSensorIndexingConfig(String sensorType, byte[] data) throws IOException {
    updateSensorIndexingConfig(sensorType, new ByteArrayInputStream(data));
  }

  public void updateSensorIndexingConfig(String sensorType, InputStream io) throws IOException {
    Map<String, Object> sensorIndexingConfig = JSONUtils.INSTANCE.load(io, new TypeReference<Map<String, Object>>() {
    });
    updateSensorIndexingConfig(sensorType, sensorIndexingConfig);
  }

  public void updateSensorIndexingConfig(String sensorType, Map<String, Object> sensorIndexingConfig) {
    configurations.put(getKey(sensorType), sensorIndexingConfig);
  }

  private String getKey(String sensorType) {
    return ConfigurationType.INDEXING.getName() + "." + sensorType;
  }

  public boolean isDefault(String sensorName, String writerName) {
    Map<String, Object> ret = (Map<String, Object>) configurations.get(getKey(sensorName));
    if(ret == null) {
      return true;
    }
    else {
      Map<String, Object> writerConfig = (Map<String, Object>)ret.get(writerName);
      return writerConfig != null?false:true;
    }
  }

  public int getBatchSize(String sensorName, String writerName ) {
     return getBatchSize(getSensorIndexingConfig(sensorName, writerName));
  }

  public String getIndex(String sensorName, String writerName) {
    return getIndex(getSensorIndexingConfig(sensorName, writerName), sensorName);
  }

  public boolean isEnabled(String sensorName, String writerName) {
    return isEnabled(getSensorIndexingConfig(sensorName, writerName));
  }

  public String getOutputPathFunction(String sensorName, String writerName) {
    return getOutputPathFunction(getSensorIndexingConfig(sensorName, writerName), sensorName);
  }

  public static boolean isEnabled(Map<String, Object> conf) {
    return getAs( ENABLED_CONF
                 ,conf
                , true
                , Boolean.class
                );
  }

  public static int getBatchSize(Map<String, Object> conf) {
    return getAs( BATCH_SIZE_CONF
                 ,conf
                , 1
                , Integer.class
                );
  }

  public static String getIndex(Map<String, Object> conf, String sensorName) {
    return getAs( INDEX_CONF
                 ,conf
                , sensorName
                , String.class
                );
  }

  public static String getOutputPathFunction(Map<String, Object> conf, String sensorName) {
    return getAs(OUTPUT_PATH_FUNCTION_CONF
            ,conf
            , ""
            , String.class
    );
  }

  public static Map<String, Object> setEnabled(Map<String, Object> conf, boolean enabled) {
    Map<String, Object> ret = conf == null?new HashMap<>():conf;
    ret.put(ENABLED_CONF, enabled);
    return ret;
  }
  public static Map<String, Object> setBatchSize(Map<String, Object> conf, int batchSize) {
    Map<String, Object> ret = conf == null?new HashMap<>():conf;
    ret.put(BATCH_SIZE_CONF, batchSize);
    return ret;
  }

  public static Map<String, Object> setIndex(Map<String, Object> conf, String index) {
    Map<String, Object> ret = conf == null?new HashMap<>():conf;
    ret.put(INDEX_CONF, index);
    return ret;
  }

  public static <T> T getAs(String key, Map<String, Object> map, T defaultValue, Class<T> clazz) {
    return map == null?defaultValue: ConversionUtils.convert(map.getOrDefault(key, defaultValue), clazz);
  }
}
