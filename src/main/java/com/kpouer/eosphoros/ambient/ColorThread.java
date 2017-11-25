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

import com.google.common.eventbus.Subscribe;
import com.kpouer.eosphoros.Eosphoros;
import com.kpouer.eosphoros.ambient.capture.ImageCapture;
import com.kpouer.eosphoros.ambient.capture.RobotCapture;
import com.kpouer.eosphoros.light.LightDefinition;
import com.kpouer.eosphoros.message.ChangeFrequencyMessage;
import com.kpouer.eosphoros.message.ShutdownMessage;
import com.kpouer.eosphoros.rules.Rule;
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
public class ColorThread extends Thread
{
  private static final Logger logger = LoggerFactory.getLogger(ColorThread.class);
  private final Object lock = new Object();
  private int frequency;

  private boolean running = true;
  private boolean pausing;
  private final PHLightState lightState;
  private boolean ignoreGrey;

  private final Collection<Rule> rules;

  public ColorThread()
  {
    super("ColorThread");
    frequency = Eosphoros.config.getFrequency();
    setDaemon(true);
    ignoreGrey = true;
    lightState = new PHLightState();
    lightState.setOn(Boolean.TRUE);
    Eosphoros.eventBus.register(this);
    rules = new ArrayList<>();
    Rule ruleLeft = new Rule();
    Rule ruleRight = new Rule();
    Rule ruleCenter = new Rule();
    ruleLeft.addColorRectangle(ColorRectangleFactory.getColorRectangle((byte) 1, (byte) 1));
    ruleLeft.addColorRectangle(ColorRectangleFactory.getColorRectangle((byte) 1, (byte) 2));
    ruleRight.addColorRectangle(ColorRectangleFactory.getColorRectangle((byte) 2, (byte) 1));
    ruleRight.addColorRectangle(ColorRectangleFactory.getColorRectangle((byte) 2, (byte) 2));
    ruleCenter.addColorRectangle(ColorRectangleFactory.getColorRectangle((byte) 1, (byte) 1));
    ruleCenter.addColorRectangle(ColorRectangleFactory.getColorRectangle((byte) 1, (byte) 2));
    ruleCenter.addColorRectangle(ColorRectangleFactory.getColorRectangle((byte) 2, (byte) 1));
    ruleCenter.addColorRectangle(ColorRectangleFactory.getColorRectangle((byte) 2, (byte) 2));
    LightDefinition[] selectedAmbientLights = Eosphoros.config.getSelectedAmbientLights();
    for (LightDefinition selectedAmbientLight : selectedAmbientLights)
    {
      switch (selectedAmbientLight.getPosition())
      {
        case Disabled:
          break;
        case Left:
          logger.info("Left: {}", selectedAmbientLight.getLightIdentifier());
          ruleLeft.addLamp(selectedAmbientLight.getLightIdentifier());
          break;
        case Right:
          logger.info("Right: {}", selectedAmbientLight.getLightIdentifier());
          ruleRight.addLamp(selectedAmbientLight.getLightIdentifier());
          break;
        case Center:
          logger.info("All: {}", selectedAmbientLight.getLightIdentifier());
          ruleCenter.addLamp(selectedAmbientLight.getLightIdentifier());
          break;

      }
    }
    rules.add(ruleLeft);
    rules.add(ruleRight);
    rules.add(ruleCenter);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    updateRectangles(screenSize);
  }

  private void updateRectangles(Dimension screenSize)
  {
    for (Rule rule : rules)
      rule.computeRectangle(screenSize, (byte) 4, (byte) 4);
  }

  @Subscribe
  public void handleStopMessage(ShutdownMessage message)
  {
    running = false;
    unpause();
  }

  @Subscribe
  public void handleChangeFrequencyMessage(ChangeFrequencyMessage message)
  {
    logger.info("handleChangeFrequencyMessage: {}ms", message.frequency);
    frequency = message.frequency;
  }

  @Override
  public void run()
  {
    try
    {
      ImageCapture imageCapture = new RobotCapture();
      long lastLog = 0L;
      while (running)
      {
        while (pausing)
        {
          synchronized (lock)
          {
            lock.wait();
            if (!running)
              throw new InterruptedException("Done");
          }
        }
        long start = System.currentTimeMillis();
        BufferedImage image = imageCapture.createScreenCapture();
        long capture = System.currentTimeMillis();
        for (Rule rule : rules)
        {
          rule.applyColor(image);
        }

        long end = System.currentTimeMillis();
        if (end - lastLog > 1000L)
        {
          lastLog = end;
          logger.info("Capture: {}ms, Rules: {}ms, Total: {}ms", capture - start, end - capture, end - start);
        }
        Thread.sleep(frequency);
      }
    }
    catch (AWTException e)
    {
      logger.error(e.getMessage(), e);
    }
    catch (InterruptedException ignored)
    {
      Thread.currentThread().interrupt();
    }
    finally
    {
      Eosphoros.eventBus.unregister(this);
      logger.info("end");
    }
  }

  public void pause()
  {
    logger.info("pause");
    pausing = true;
  }

  public void unpause()
  {
    logger.info("unpause");
    pausing = false;
    synchronized (lock)
    {
      lock.notifyAll();
    }
  }

  public void togglePause()
  {
    logger.info("togglePause");
    if (pausing)
      unpause();
    else
      pause();
  }

  public boolean isPausing()
  {
    return pausing;
  }

  public void lightsOff()
  {
    pause();
    rules.forEach(Rule::lightsOff);
  }

  public boolean isIgnoreGrey()
  {
    return ignoreGrey;
  }

  public void setIgnoreGrey(boolean ignoreGrey)
  {
    this.ignoreGrey = ignoreGrey;
  }
}
