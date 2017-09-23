# Eosphoros

## Introduction

Eosphoros is a desktop application that change the colors of your Philips Hue lights according to the color of your computer screen.

## Requirement

Eosphoros is pure JavaFX application running in Java 8.

It uses the Philips Hue SDK (https://github.com/PhilipsHue/PhilipsHueSDK-Java-MultiPlatform-Android)

## Build & Launch

To build the application you will need to call "mvn package".
It will create a jar Eosphoros-version.jar

Then launch it with 
java -Xmx50M -cp huelocalsdk.jar;huesdkresources.jar;Eosphoros.jar com.kpouer.eosphoros.Eosphoros

