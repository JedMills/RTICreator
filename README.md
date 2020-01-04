# Reflectance Transformation Imaging (RTI) Creator

Application used to create .ptm and .hsh RTI files, based on Cultural Heritage 
Imaging's (CHI) RTIBuilder:
http://culturalheritageimaging.org/What_We_Offer/Downloads/View/index.html.
RTICreator is designed to be used alongside RTIViewer. 

RTICreator is written in Java, and uses JavaFX for GUI components. RTICreator 
has been tested using Java SDK 13 and JavaFX SDK 11.0 (JavaFX now is completely 
standalone). The software was written using IntelliJ 
(https://www.jetbrains.com/idea/). 

## Dependencies 
- Java SDK 13.0 can be installed from: 
  https://www.oracle.com/technetwork/java/javase/downloads/index.html
- JavaFX 11 SDK has a very easy guide for installing here: 
  https://openjfx.io/openjfx-docs/. IntelliJ also required me to add the 
  following in the [Run > Edit Configurations... > VM Options] box: "-p 
  C:\Users\jm729\RTI\javafx-sdk-11.0.2\lib --add-modules javafx.controls" 
- RTICreator requires the propriety PTM fitter, and the HSH fitter.  

## Build and Run
The following VM options are needed for the new JavaFx when running:
"-p path\to\javafx\javafx-sdk-11.0.2\lib --add-modules javafx.controls 
-Dprism.order=sw"

