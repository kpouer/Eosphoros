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

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;

/**
 * @author Matthieu Casanova
 */
public class MultiThreadColorRectangle extends ColorRectangle
{
  public static final int THREAD_COUNT = 8;

  public MultiThreadColorRectangle(byte x, byte y)
  {
    super(x, y);
  }

  @Override
  public void computeColor(BufferedImage image) throws InterruptedException
  {
    colorResult.reset();
    int maxX = Math.min(image.getWidth(), rectangle.width + rectangle.x);
    int maxY = Math.min(image.getHeight(), rectangle.height + rectangle.y);
    int width = (maxX - rectangle.x) / THREAD_COUNT;
    CountDownLatch latch = new CountDownLatch(THREAD_COUNT -1);
    Sample[] samples = new Sample[THREAD_COUNT];
    for (int i = 0; i< THREAD_COUNT - 1; i++) {
      samples[i] = new Sample(latch, image, rectangle.x + i * width, width, maxY);
      Eosphoros.executor.execute(samples[i]);
    }
    samples[THREAD_COUNT -1] = new Sample(latch, image, rectangle.x + (THREAD_COUNT - 1) * width, maxX - (rectangle.x + (THREAD_COUNT - 1) * width), maxY);
    Eosphoros.executor.execute(samples[THREAD_COUNT -1]);
    latch.await();
    for (Sample sample : samples)
    {
      colorResult.add(sample.colorResult);
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
