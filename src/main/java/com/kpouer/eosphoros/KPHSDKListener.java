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
package com.kpouer.eosphoros;

import com.kpouer.eosphoros.light.LightDefinition;
import com.kpouer.eosphoros.message.*;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
class KPHSDKListener implements PHSDKListener
{
  private static final Logger logger = LoggerFactory.getLogger(KPHSDKListener.class);
  private final PHHueSDK phHueSDK;
  private final Stage primaryStage;

  public KPHSDKListener(PHHueSDK phHueSDK, Stage primaryStage)
  {
    this.phHueSDK = phHueSDK;
    this.primaryStage = primaryStage;
  }

  @Override
  public void onCacheUpdated(List<Integer> list, PHBridge phBridge)
  {
//      logger.info("onCacheUpdated");
  }

  @Override
  public void onBridgeConnected(PHBridge bridge, String username)
  {
    logger.info("onBridgeConnected");
    phHueSDK.setSelectedBridge(bridge);
    Eosphoros.config.setBridgeUsername(username);
//      phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
    LightDefinition[] selectedAmbiantLights = Eosphoros.config.getSelectedAmbientLights();
    if (selectedAmbiantLights == null || selectedAmbiantLights.length == 0)
    {
      Eosphoros.instance.chooseLights();
    } else
    {
      Eosphoros.eventBus.post(new StartAmbient());
    }
  }

  @Override
  public void onAuthenticationRequired(PHAccessPoint accessPoint)
  {
    logger.info("onAuthenticationRequired");

    Platform.runLater(() ->
    {
      try
      {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/Authentication.fxml")), 516, 500);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        phHueSDK.startPushlinkAuthentication(accessPoint);
      }
      catch (IOException e)
      {
        logger.error("Error", e);
      }
    });
  }

  @Override
  public void onAccessPointsFound(List<PHAccessPoint> list)
  {
    logger.info("onAccessPointsFound");
  }

  @Override
  public void onError(int i, String s)
  {
    logger.info("onError {}", s);
  }

  @Override
  public void onConnectionResumed(PHBridge phBridge)
  {
    logger.info("onConnectionResumed");
  }

  @Override
  public void onConnectionLost(PHAccessPoint phAccessPoint)
  {
    logger.info("onConnectionLost");
  }

  @Override
  public void onParsingErrors(List<PHHueParsingError> list)
  {
    logger.info("onParsingErrors");
  }
}
