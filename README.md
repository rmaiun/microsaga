![Screenshot](logo.png)

Saga management library for jvm services.
# Description
Saga pattern is just one tool in our belt for distributed or long-running transaction management.
Nevertheless, saga is a powerful mechanism and easy readable api can help to integrate this pattern into different projects.
Microsaga library provides simple and readable api for saga actions and their compensations, giving possibility to declare sagas in composable way.
Inspired by [cats-saga](https://github.com/VladKopanev/cats-saga).
Contains one and only dependency to [failsafe](https://github.com/failsafe-lib/failsafe) which allows to use retry behavior in a flexible way.

# Usage
Add dependency to your project  with `gradle`  
`implementation group: 'io.github.rmaiun', name: 'microsaga', version: '0.3.0'`  
or `maven`  
````xml
<dependency>
  <groupId>io.github.rmaiun</groupId>
  <artifactId>microsaga</artifactId>
  <version>0.3.0</version>`
</dependency>
````

# Api description
