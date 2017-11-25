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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.kpouer.eosphoros.config.Konfig;
import com.kpouer.eosphoros.message.*;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Matthieu Casanova
 */
public class Eosphoros extends Application
{
  public static final EventBus eventBus = new EventBus();
  private static final Logger logger = LoggerFactory.getLogger(Eosphoros.class);
  public static Konfig config;
  private Stage primaryStage;
  public static Eosphoros instance;
  public static Executor executor;

  public static void main(String[] args)
  {
    executor = Executors.newCachedThreadPool();
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws ConfigurationException, IOException
  {
    primaryStage.setTitle("Eosphoros");
    primaryStage.setOnCloseRequest(event -> Platform.exit());
    this.primaryStage = primaryStage;
    eventBus.register(this);
    logger.info("start");
    config = new Konfig();
    PHHueSDK phHueSDK = PHHueSDK.getInstance();
    phHueSDK.getNotificationManager().registerSDKListener(new KPHSDKListener(phHueSDK, primaryStage));
    PHAccessPoint selectedBridge = config.getSelectedAccessPoint();
    if (selectedBridge == null)
    {
      Parent selectBridge = FXMLLoader.load(getClass().getResource("/SelectBridge.fxml"));
      Scene scene = new Scene(selectBridge, 800, 600);
      primaryStage.setScene(scene);
      primaryStage.show();
    }
    else
    {
      primaryStage.show();
      handleSelectedAccessPoint(new SelectedAccessPointMessage(selectedBridge));
    }
  }

  @Override
  public void init() throws Exception
  {
    instance = this;
    System.setProperty("org.slf4j.simpleLogger.dateTimeFormat","yyyy-MM-dd HH:mm:ss:SSS");
    System.setProperty("org.slf4j.simpleLogger.showDateTime","true");
    logger.info("init");
  }

  @Override
  public void stop() throws Exception
  {
    logger.info("stop");
    PHHeartbeatManager.removeTimer();
    eventBus.post(new ShutdownMessage());
  }

  @Subscribe
  public void handleSelectedAccessPoint(SelectedAccessPointMessage selectedAccessPointMessage)
  {
    logger.info("handleSelectAccessPoint");
    PHHueSDK phHueSDK = PHHueSDK.getInstance();
    phHueSDK.connect(selectedAccessPointMessage.getAccessPoint());
    Scene scene = new Scene(new Label("Connecting to bridge:" + selectedAccessPointMessage.getAccessPoint().getIpAddress()), 800, 600);
    primaryStage.setScene(scene);
  }

  @Subscribe
  public void handleStartAmbiant(StartAmbient message)
  {
    logger.info("handleStartAmbiant");
    Platform.runLater(() ->
    {
      try
      {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Ambiant.fxml"))));
      }
      catch (IOException e)
      {
        logger.error("Unable to load LightSelector", e);
      }
    });
  }

  public void chooseLights()
  {
    Platform.runLater(() ->
    {
      try
      {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/LightSelector.fxml")), 516, 500);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
      }
      catch (IOException e)
      {
        logger.error("Cannot load Light Selector", e);
      }
    });
  }

}
