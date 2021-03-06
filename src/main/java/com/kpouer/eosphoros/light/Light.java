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
import com.philips.lighting.model.PHLight;

/**
 * @author Matthieu Casanova
 */
public class Light
{
  private final PHLight phLight;
  private final LightDefinition definition;

  public Light(PHLight light)
  {
    phLight = light;
    LightDefinition lightDefinition = Eosphoros.config.getDefinition(light.getIdentifier());
    if (lightDefinition == null)
      lightDefinition = new LightDefinition(light.getIdentifier(), Position.Disabled);
    definition = lightDefinition;
  }

  public boolean isSelected()
  {
    return definition.isSelected();
  }

  public PHLight getPhLight() {
    return phLight;
  }

  public LightDefinition getDefinition() {
    return definition;
  }
}
