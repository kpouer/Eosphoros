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
package com.kpouer.eosphoros.light;

import com.kpouer.eosphoros.Eosphoros;
import com.kpouer.eosphoros.message.*;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHLight;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class SelectLight
{
  private static final Logger logger = LoggerFactory.getLogger(SelectLight.class);
  @FXML
  private ListView<Light> lightList;

  @FXML
  public void initialize()
  {
    lightList.setCellFactory(lightDefinition -> new LightCell());
    lightList.setEditable(true);
    lightList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    refresh(null);
  }

  /**
   * Refresh the lights list
   * @param actionEvent
   */
  public void refresh(ActionEvent actionEvent)
  {
    logger.info("refresh");
    PHHueSDK phHueSDK = PHHueSDK.getInstance();
    List<PHLight> allLights = phHueSDK.getSelectedBridge().getResourceCache().getAllLights();
    lightList.getItems()
        .setAll(allLights.stream()
            .filter(PHLight::supportsColor)
            .map(Light::new)
            .toArray(Light[]::new));
  }

  @FXML
  public void select(ActionEvent actionEvent)
  {
    logger.info("select");
    lightList.getItems().filtered(Light::isSelected).toArray();
    LightDefinition[] selectedItems = lightList.getItems().stream()
        .filter(Light::isSelected)
        .map(Light::getDefinition)
        .toArray(LightDefinition[]::new);
    Eosphoros.eventBus.post(new SelectedAmbiantLight(selectedItems));
  }

  @FXML
  public void close(ActionEvent actionEvent)
  {
    logger.info("close");
    Eosphoros.eventBus.post(new StartAmbiant());
  }
}
