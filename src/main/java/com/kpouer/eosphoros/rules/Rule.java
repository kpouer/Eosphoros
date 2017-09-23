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
package com.kpouer.eosphoros.rules;

import com.kpouer.eosphoros.ambient.ColorRectangle;
import com.kpouer.eosphoros.ambient.ColorResult;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLightState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Matthieu Casanova
 */
public class Rule
{
  private static final Logger logger = LoggerFactory.getLogger(Rule.class);
  private final Collection<String> lamps;
  private final Collection<ColorRectangle> colorRectangles;
  private final PHLightState lightState;
  private int oldRed;
  private int oldGreen;
  private int oldBlue;

  public Rule()
  {
    lamps = new ArrayList<>();
    colorRectangles = new ArrayList<>();
    lightState = new PHLightState();
    lightState.setOn(Boolean.TRUE);
  }

  public void applyColor(BufferedImage image)
  {
    ColorResult colorResult = new ColorResult();
    for (ColorRectangle colorRectangle : colorRectangles)
    {
      colorRectangle.computeColor(image);
      colorResult.add(colorRectangle.getColorResult());
    }

    if (colorResult.pixels == 0)
      colorResult.pixels = 1;
    colorResult.red /= colorResult.pixels;
    colorResult.green /= colorResult.pixels;
    colorResult.blue /= colorResult.pixels;
    colorResult.brightness /= colorResult.pixels;
    setColor(colorResult.red, colorResult.green, colorResult.blue, colorResult.brightness);
  }

  private void setColor(int red, int green, int blue, float brightness)
  {
    float[] xy = PHUtilities.calculateXYFromRGB(red, green, blue, "LCT001");
    if (oldRed == red && oldGreen == green && oldBlue == blue)
      return;

    lightState.setBrightness((int) (brightness * 100));
    lightState.setX(xy[0]);
    lightState.setY(xy[1]);

    PHBridge selectedBridge = PHHueSDK.getInstance().getSelectedBridge();
    if (selectedBridge != null)
    {
      oldRed = red;
      oldGreen = green;
      oldBlue = blue;
      lamps.forEach(lamp -> selectedBridge.updateLightState(lamp, lightState, null));
    }
  }

  public void addLamp(String lamp)
  {
    lamps.add(lamp);
  }

  public void addColorRectangle(ColorRectangle rectangle)
  {
    colorRectangles.add(rectangle);
  }

  public void computeRectangle(Dimension screenSize, byte maxX,  byte maxY)
  {
    colorRectangles.forEach(colorRectangle -> colorRectangle.computeRectangle(screenSize, maxX, maxY));
  }

  public void lightsOff()
  {
    logger.info("lightsOff");
    PHBridge selectedBridge = PHHueSDK.getInstance().getSelectedBridge();
    if (selectedBridge != null)
    {
      PHLightState lightState = new PHLightState();
      lightState.setOn(Boolean.FALSE);
      lamps.forEach(lamp -> selectedBridge.updateLightState(lamp, lightState, null));
    }
  }
}
