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
package com.kpouer.eosphoros.bridge;

import com.kpouer.eosphoros.Eosphoros;
import com.kpouer.eosphoros.message.SelectedAccessPointMessage;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class SelectBridge
{
  private static final Logger logger = LoggerFactory.getLogger(SelectBridge.class);
  @FXML
  private ListView<PHAccessPoint> bridgeList;

  @FXML
  public void initialize()
  {
    bridgeList.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<PHAccessPoint>()
    {
      @Override
      public String toString(PHAccessPoint object)
      {
        return object.getIpAddress();
      }

      @Override
      public PHAccessPoint fromString(String string)
      {
        return null;
      }
    }));
    PHHueSDK.getInstance().getNotificationManager().registerSDKListener(new PHSDKListener()
    {
      @Override
      public void onAccessPointsFound(List<PHAccessPoint> accessPointsList)
      {
        logger.info("onAccessPointsFound");
        Platform.runLater(() -> bridgeList.getItems().setAll(accessPointsList));
      }

      @Override
      public void onCacheUpdated(List<Integer> list, PHBridge phBridge)
      {
        // I don't care
      }

      @Override
      public void onBridgeConnected(PHBridge phBridge, String s)
      {
        // I don't care
      }

      @Override
      public void onAuthenticationRequired(PHAccessPoint phAccessPoint)
      {
        // I don't care
      }

      @Override
      public void onError(int i, String s)
      {
        // I don't care
      }

      @Override
      public void onConnectionResumed(PHBridge phBridge)
      {
        // I don't care
      }

      @Override
      public void onConnectionLost(PHAccessPoint phAccessPoint)
      {
        // I don't care
      }

      @Override
      public void onParsingErrors(List<PHHueParsingError> list)
      {
        // I don't care
      }
    });
    refresh(null);
  }

  @FXML
  public void refresh(ActionEvent actionEvent)
  {
    logger.info("refresh");
    PHHueSDK phHueSDK = PHHueSDK.getInstance();
    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
    sm.search(true, true);
  }

  @FXML
  public void select(ActionEvent actionEvent)
  {
    logger.info("select");
    PHAccessPoint selectedItem = bridgeList.getSelectionModel().getSelectedItem();
    if (selectedItem == null)
      return;
    Eosphoros.eventBus.post(new SelectedAccessPointMessage(selectedItem));
  }

  @FXML
  public void close(ActionEvent actionEvent)
  {
    logger.info("close");
    PHAccessPoint selectedBridge = Eosphoros.config.getSelectedAccessPoint();
    if (selectedBridge == null)
      return;
    Eosphoros.eventBus.post(new SelectedAccessPointMessage(selectedBridge));
  }
}
