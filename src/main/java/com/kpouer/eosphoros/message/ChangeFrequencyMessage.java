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
package com.kpouer.eosphoros.message;

import com.kpouer.eosphoros.config.Konfig;

/**
 * @author Matthieu Casanova
 */
public class ChangeFrequencyMessage implements Message
{
  public final int frequency;

  public ChangeFrequencyMessage(int freq)
  {
    if (freq < Konfig.MIN_FREQUENCY)
      this.frequency = Konfig.MIN_FREQUENCY;
    else
      this.frequency = freq;
  }
}
