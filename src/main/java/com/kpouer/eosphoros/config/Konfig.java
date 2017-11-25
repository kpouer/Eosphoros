/*
 * Copyright 2017 Matthieu Casanova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kpouer.eosphoros.config;

import com.google.common.eventbus.Subscribe;
import com.kpouer.eosphoros.Eosphoros;
import com.kpouer.eosphoros.message.ChangeFrequencyMessage;
import com.kpouer.eosphoros.light.LightDefinition;
import com.kpouer.eosphoros.light.Position;
import com.kpouer.eosphoros.message.*;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Matthieu Casanova
 */
public class Konfig
{
  public static final int MIN_FREQUENCY = 20;
  private static final Logger logger = LoggerFactory.getLogger(Konfig.class);
  public static final String BRIDGE_IP = "bridgeIp";
  public static final String BRIDGE_USERNAME = "bridge_username";
  public static final String BRIDGE_MAC = "bridge_mac";
  public static final String AMBIANT_LIGHTS = "ambiant_lights";
  public static final String FREQUENCY = "frequency";
  private final Configuration config;
  private final FileBasedConfigurationBuilder<XMLConfiguration> builder;

  public Konfig() throws ConfigurationException, IOException
  {
    Eosphoros.eventBus.register(this);
    File configFile = new File("config.properties");
    Parameters parameters = new Parameters();
    builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class, new FastHashMap(), true)
        .configure(parameters.fileBased().setFile(configFile));
    config = builder.getConfiguration();
  }

  @Nullable
  public PHAccessPoint getSelectedAccessPoint()
  {
    String ip = config.getString(BRIDGE_IP);
    String username = config.getString(BRIDGE_USERNAME);
    String mac = config.getString(BRIDGE_MAC);
    if (ip == null || mac == null)
      return null;
    if ("null".equals(username))
      username = null;
    return new PHAccessPoint(ip, username, mac);
  }

  public void setBridgeUsername(String username)
  {
    config.setProperty(BRIDGE_USERNAME, username);
  }

  @Subscribe
  public void handleSelectedAccessPoint(SelectedAccessPointMessage selectedAccessPoint)
  {
    PHAccessPoint accessPoint = selectedAccessPoint.getAccessPoint();
    config.setProperty(BRIDGE_IP, accessPoint.getIpAddress());
    config.setProperty(BRIDGE_USERNAME, accessPoint.getUsername());
    config.setProperty(BRIDGE_MAC, accessPoint.getMacAddress());
    save();
  }

  private void save()
  {
    try
    {
      builder.save();
    }
    catch (ConfigurationException e)
    {
      logger.error("Unable to save config", e);
    }
  }

  @Subscribe
  public void handleSelectedAmbiantLight(SelectedAmbientLight selectedAmbientLight)
  {
    LightDefinition[] selectedItems = selectedAmbientLight.getSelectedItems();
    String[] lights = Arrays.stream(selectedItems)
        .map(lightDefinition -> lightDefinition.getLightIdentifier() + ':' + lightDefinition.getPosition())
        .toArray(String[]::new);
    config.setProperty(AMBIANT_LIGHTS, lights);
    save();
    Eosphoros.eventBus.post(new StartAmbient());
  }

  @Subscribe
  public void handleChangeFrequencyMessage(ChangeFrequencyMessage message)
  {
    logger.info("handleChangeFrequencyMessage: {}ms", message.frequency);
    config.setProperty(FREQUENCY, message.frequency);
    save();
  }

  public LightDefinition[] getSelectedAmbientLights()
  {
    String[] stringArray = config.getStringArray(AMBIANT_LIGHTS);
    return Arrays.stream(stringArray)
        .map(s -> s.split(":"))
        .filter(strings -> strings.length == 2)
        .map(strings -> new LightDefinition(strings[0], Position.fromString(strings[1])))
        .toArray(LightDefinition[]::new);
  }

  public LightDefinition getDefinition(String identifier)
  {
    LightDefinition[] selectedAmbiantLights = getSelectedAmbientLights();
    for (LightDefinition selectedAmbiantLight : selectedAmbiantLights)
    {
      if (identifier.equals(selectedAmbiantLight.getLightIdentifier()))
        return selectedAmbiantLight;
    }
    return null;
  }

  public int getFrequency()
  {
    return config.getInt(FREQUENCY, MIN_FREQUENCY);
  }
}
