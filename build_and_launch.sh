#!/usr/bin/env sh
./gradlew bootJar -Dorg.gradle.java.home=C:/Java/jdk-15.0.1
C:/Java/jdk-15.0.1/bin/java.exe --enable-preview -jar build/libs/api-gateway-0.0.1-SNAPSHOT.jar