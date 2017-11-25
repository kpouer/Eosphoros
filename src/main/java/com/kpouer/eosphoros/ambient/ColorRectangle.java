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
package com.kpouer.eosphoros.ambient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Matthieu Casanova
 */
public abstract class ColorRectangle
{
  private static final Logger logger = LoggerFactory.getLogger(ColorRectangle.class);
  protected final byte x;
  protected final byte y;
  protected Rectangle rectangle;
  protected final ColorResult colorResult;
  boolean ignoreGrey = true;

  protected ColorRectangle(byte x, byte y)
  {
    this.x = x;
    this.y = y;
    colorResult = new ColorResult();
  }

  public void computeRectangle(Dimension screenSize, byte maxX, byte maxY)
  {
    rectangle = new Rectangle();
    rectangle.x = (x * screenSize.width) / maxX;
    rectangle.y = (y * screenSize.height) / maxY;
    rectangle.width = screenSize.width / maxX;
    rectangle.height = screenSize.height / maxY;
    logger.info("computeRectangle:x={},y={},{}", x, y, rectangle);
  }

  public abstract void computeColor(BufferedImage image) throws InterruptedException;

  protected static void computeColor(BufferedImage image, int minX, int maxX, int minY, int maxY, boolean ignoreGrey, ColorResult colorResult)
  {
    float[] hsbvals = new float[3];
    for (int x = minX; x < maxX; x++)
    {
      for (int y = minY; y < maxY; y++)
      {
        int rgb = image.getRGB(x, y);
        int tmpRed = rgb >> 16 & 0xff;
        int tmpGreen = rgb >> 8 & 0xff;
        int tmpBlue = rgb & 0xff;
        Color.RGBtoHSB(tmpRed, tmpGreen, tmpBlue, hsbvals);
        if (ignoreGrey && hsbvals[1] > 0.4)
        {
          colorResult.add(tmpRed, tmpGreen, tmpBlue, hsbvals[2]);
        }
      }
    }
  }

  public ColorResult getColorResult()
  {
    return colorResult;
  }
}
