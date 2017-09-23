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
package com.kpouer.eosphoros.authentication;

import com.kpouer.eosphoros.Eosphoros;
import com.kpouer.eosphoros.message.SelectedAccessPointMessage;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matthieu Casanova
 */
public class Authentication
{
  private static final Logger logger = LoggerFactory.getLogger(Authentication.class);
  public ListView<PHAccessPoint> bridgeList;

  @FXML
  public void initialize()
  {

  }

  public void refresh(ActionEvent actionEvent)
  {
    logger.info("refresh");
    PHHueSDK phHueSDK = PHHueSDK.getInstance();
    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
    sm.search(true, true);
  }

  public void select(ActionEvent actionEvent)
  {
    logger.info("select");
    PHAccessPoint selectedItem = bridgeList.getSelectionModel().getSelectedItem();
    if (selectedItem == null)
      return;
    Eosphoros.eventBus.post(new SelectedAccessPointMessage(selectedItem));
  }

  public void close(ActionEvent actionEvent)
  {
    logger.info("close");
    PHAccessPoint selectedBridge = Eosphoros.config.getSelectedAccessPoint();
    if (selectedBridge == null)
      return;
    Eosphoros.eventBus.post(new SelectedAccessPointMessage(selectedBridge));
  }
}
