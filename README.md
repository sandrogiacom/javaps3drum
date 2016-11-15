# javaps3drum
Drum demo with Java and PS3 drum controller

This demo project is based on jinput project (https://github.com/jinput/jinput)

All of depedencies are configured in pom.xml file

To run main class, firt extract native librares from jinput-platform-2.0.6-natives into main folder. Ex:
To run in windows os, extract all dll files from jinput-platform-2.0.6-natives-windows.jar to \javaps3drum

These dll files are placed together with pom.xml

To run with maven (requires java 8):

mvn clean package exec:java

See this demo video on youtube:
https://www.youtube.com/watch?v=3kXpAfolqSs