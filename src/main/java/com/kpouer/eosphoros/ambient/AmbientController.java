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
import com.kpouer.eosphoros.config.Konfig;
import com.kpouer.eosphoros.message.ChangeFrequencyMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * @author Matthieu Casanova
 */
public class AmbientController
{
  public TextField frequency;
  public Button pauseButton;
  public CheckBox ignoreGrey;
  private ColorThread colorThread;
  private int currentFrequency;

  @FXML
  public void initialize()
  {
    colorThread = new ColorThread();
    colorThread.start();
    ignoreGrey.setSelected(colorThread.isIgnoreGrey());
    currentFrequency = Eosphoros.config.getFrequency();
    frequency.setText(Integer.toString(currentFrequency));
    frequency.focusedProperty().addListener((observable, oldValue, newValue) -> changeFrequency(null));
  }

  public void togglePause(ActionEvent actionEvent)
  {
    colorThread.togglePause();
    pauseButton.setText(colorThread.isPausing() ? "Unpause" : "Pause");
  }

  public void lightSelection(ActionEvent actionEvent)
  {
    colorThread.handleStopMessage(null);
    Eosphoros.instance.chooseLights();
  }

  public void off(ActionEvent actionEvent)
  {
    colorThread.pause();
    pauseButton.setText(colorThread.isPausing() ? "Unpause" : "Pause");
    colorThread.lightsOff();
  }

  public void changeFrequency(ActionEvent actionEvent)
  {
    int freq = Integer.parseInt(frequency.getText());
    if (freq < Konfig.MIN_FREQUENCY)
      freq = Konfig.MIN_FREQUENCY;
    if (currentFrequency != freq)
    {
      currentFrequency = freq;
      Eosphoros.eventBus.post(new ChangeFrequencyMessage(freq));
    }
  }

  public void toggleIgnoreGrey(ActionEvent actionEvent)
  {
    colorThread.setIgnoreGrey(ignoreGrey.isSelected());
  }
}
