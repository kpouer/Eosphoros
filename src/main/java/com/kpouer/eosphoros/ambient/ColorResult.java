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

/**
 * @author Matthieu Casanova
 */
public class ColorResult
{
  private int red;
  private int green;
  private int blue;
  private int pixels;
  private float brightness;

  public void reset()
  {
    red = 0;
    green = 0;
    blue = 0;
    pixels = 0;
    brightness = 0;
  }

  public void add(ColorResult colorResult)
  {
    red += colorResult.red;
    green += colorResult.green;
    blue += colorResult.blue;
    pixels += colorResult.pixels;
    brightness += colorResult.brightness;
  }

  public void add(int tmpRed, int tmpGreen, int tmpBlue, float tmpBrightness)
  {
    pixels++;
    red += tmpRed;
    green += tmpGreen;
    blue += tmpBlue;
    brightness += tmpBrightness;
  }

  public void compute()
  {
    if (pixels == 0)
      return;
    red /= pixels;
    green /= pixels;
    blue /= pixels;
    brightness /= pixels;
  }

  public int getRed() {
    return red;
  }

  public int getGreen() {
    return green;
  }

  public int getBlue() {
    return blue;
  }

  public float getBrightness() {
    return brightness;
  }
}
