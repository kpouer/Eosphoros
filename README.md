# Eosphoros

## Introduction

Eosphoros is a desktop application that change the colors of your Philips Hue lights according to the color of your computer screen.
I tried it on Windows but it should work on Linux and Mac too.
An important thing to know is that if you want to use it in games, it doesn't work for full screen games but works perfectly with Windowed no border.

## Requirement

Eosphoros is pure JavaFX application running in Java 8.

It uses the Philips Hue SDK (https://github.com/PhilipsHue/PhilipsHueSDK-Java-MultiPlatform-Android)

## Build & Launch

To build the application you will need to call "mvn package".
It will create a jar Eosphoros-version.jar

Then launch it with 
java -Xmx50M -cp huelocalsdk.jar;huesdkresources.jar;Eosphoros.jar com.kpouer.eosphoros.Eosphoros

## First start

At the first startup, the application will search for your Hue bridge in the local network. It can take a few seconds, be patient.
After selecting it it will show you the lights that support color.
For every light you decide it's position, left for the lights that will cover the left part of the screen, right, or center for lights that are in the middle and that should cover the entire screen.

## Running

After selecting your lights the process runs. The principle is extremely simple to understand.
At every cycle it takes a screenshot of your main screen, then compute the color for the left, middle and entire screen to update the lights. Then it waits for the next cycle.

## Performances

I run it on my old core i7 2600K with a screen in 1680x1050. The entire process takes about 40ms and the biggest time consumer is the screen capture that takes 30/35ms, the color computation takes only 3 to 5ms.
