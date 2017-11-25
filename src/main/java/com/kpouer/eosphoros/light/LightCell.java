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

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

/**
 * @author Matthieu Casanova
 */
public class LightCell extends ListCell<Light>
{
  private final HBox hbox;
  private final Label label;
  private final ComboBox<Position> positionCombo;
  private LightDefinition definition;

  public LightCell()
  {
    hbox = new HBox();
    label = new Label();
    hbox.getChildren().addAll(label);
    positionCombo = new ComboBox<>(FXCollections.observableArrayList(Position.values()));
    hbox.getChildren().addAll(positionCombo);
    positionCombo.setOnAction(event -> setPosition(positionCombo.getValue()));
  }

  private void setPosition(Position position)
  {
    definition.setPosition(position);
  }

  @Override
  protected void updateItem(Light light, boolean empty)
  {
    if (empty)
      setGraphic(null);
    else
    {
      definition = light.getDefinition();
      positionCombo.setValue(definition.getPosition());
      label.setText(light.getPhLight().getName() + " (" + light.getPhLight().getIdentifier()+ ')');
      setGraphic(hbox);
    }
  }
}
