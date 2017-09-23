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

import com.kpouer.eosphoros.Eosphoros;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;

/**
 * @author Matthieu Casanova
 */
public class MultiThreadColorRectangle extends ColorRectangle
{
  private static final Logger logger = LoggerFactory.getLogger(MultiThreadColorRectangle.class);

  public MultiThreadColorRectangle(byte x, byte y)
  {
    super(x, y);
  }

  @Override
  public void computeColor(BufferedImage image)
  {
    int maxX = Math.min(image.getWidth(), rectangle.width + rectangle.x);
    int maxY = Math.min(image.getHeight(), rectangle.height + rectangle.y);
    int NB = 8;
    int width = (maxX - rectangle.x) / NB;
    CountDownLatch latch = new CountDownLatch(NB-1);
    Sample[] samples = new Sample[NB];
    for (int i = 0; i< NB - 1; i++)
      Eosphoros.executor.execute(samples[i] = new Sample(latch, image,rectangle.x + i * width, width, maxY));

    Eosphoros.executor.execute(samples[NB -1] = new Sample(latch, image, rectangle.x + (NB - 1) * width, maxX - (rectangle.x + (NB - 1) * width), maxY));
    try
    {
      latch.await();
      for (Sample sample : samples)
      {
        colorResult.add(sample.colorResult);
      }
    }
    catch (InterruptedException e)
    {
      logger.error("Thread interrupted", e);
    }
  }

  private class Sample implements Runnable
  {
    private final CountDownLatch latch;
    private final BufferedImage image;
    private final int minX;
    private final int count;
    private final int maxY;
    final ColorResult colorResult;

    public Sample(CountDownLatch latch, BufferedImage image, int minX, int count, int maxY)
    {
      this.latch = latch;
      this.image = image;
      this.minX = minX;
      this.count = count;
      this.maxY = maxY;
      colorResult = new ColorResult();
    }

    @Override
    public void run()
    {
      try
      {
        computeColor(image, minX, minX + count, rectangle.y, maxY, ignoreGrey, colorResult);
      }
      finally
      {
        latch.countDown();
      }
    }
  }
}
