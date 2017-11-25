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

import java.awt.image.BufferedImage;

/**
 * @author Matthieu Casanova
 */
public class SingleThreadColorRectangle extends ColorRectangle
{
  public SingleThreadColorRectangle(byte x, byte y)
  {
    super(x, y);
  }

  @Override
  public void computeColor(BufferedImage image)
  {
    colorResult.reset();
    int maxX = Math.min(image.getWidth(), rectangle.width + rectangle.x);
    int maxY = Math.min(image.getHeight(), rectangle.height + rectangle.y);
    computeColor(image, rectangle.x, maxX, rectangle.y, maxY, ignoreGrey, colorResult);
  }
}
