![Screenshot](logo.png)

Saga management library for jvm services.

[![Support Ukraine](https://img.shields.io/static/v1?label=United24&message=Support%20Ukraine&color=lightgrey&link=https%3A%2F%2Fu24.gov.ua&logo=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAASwAAADICAYAAABS39xVAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAANKSURBVHhe7dZBThRhFEbRnx1IgvtFiIoxbgemOHLAhAoJ1QyaBahroKxqE%2BMS6iZncPKSbwE3b4yr6W58en4Z148zwC5tjbqabrdgvZ59PS5nn2eAfVobtbbquAXrcBquJ4B9Wht1%2BrQEC9g9wQIyBAvIECwgQ7CADMECMgQLyBAsIEOwgAzBAjIEC8gQLCBDsIAMwQIyBAvIECwgQ7CADMECMgQLyBAsIEOwgAzBAjIEC8gQLCBDsIAMwQIyBAvIECwgQ7CADMECMgQLyBAsIEOwgAzBAjIEC8gQLCBDsIAMwQIyBAvIECwgQ7CADMECMgQLyBAsIEOwgAzBAjIEC8gQLCBDsIAMwQIyBAvIECwgQ7CADMECMgQLyBAsIEOwgAzBAjIEC8gQLCBDsIAMwQIyBAvIECwgQ7CADMECMgQLyBAsIEOwgAzBAjIEC8gQLCBDsIAMwQIyBAvIECwgQ7CADMECMgQLyBAsIEOwgAzBAjIEC8gQLCBDsIAMwQIyBAvIECwgQ7CADMECMgQLyBAsIEOwgAzBAjIEC8j4F6zL%2BTA%2BHpfxYR0A9mhr1OXzPC5u7g%2Fvv%2F1YLr58B9ilU6Nu7ufx6%2BH88Hs6X9YLsEtbo34%2BvJvH29M4LC9jWZ4Admpt1NqqNVjTGqz5bFkmgJ1aG%2FX2KFhAgWABGYIFZAgWkCFYQIZgARmCBWQIFpAhWECGYAEZggVkCBaQIVhAhmABGYIFZAgWkCFYQIZgARmCBWQIFpAhWECGYAEZggVkCBaQIVhAhmABGYIFZAgWkCFYQIZgARmCBWQIFpAhWECGYAEZggVkCBaQIVhAhmABGYIFZAgWkCFYQIZgARmCBWQIFpAhWECGYAEZggVkCBaQIVhAhmABGYIFZAgWkCFYQIZgARmCBWQIFpAhWECGYAEZggVkCBaQIVhAhmABGYIFZAgWkCFYQIZgARmCBWQIFpAhWECGYAEZggVkCBaQIVhAhmABGYIFZAgWkCFYQIZgARmCBWQIFpAhWECGYAEZggVkCBaQIVhAhmABGYIFZAgWkCFYQIZgARmCBWQIFpAhWECGYAEZggVk%2FBes1%2BX4dwDYpbVRa6uOW7Du3p7Hy1YvgF3aGjWN2z9qCgwkg1n6XwAAAABJRU5ErkJggg%3D%3D)](https://u24.gov.ua)

| CI build                               | Test coverage                                     | Release |
|----------------------------------------|---------------------------------------------------| --- |
| [![Build Status][Badge-GHA]][Link-GHA] | [![Coverage Status][Badge-Codecov]][Link-Codecov] | [![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases] |

# Summary

Saga pattern is just one tool in our belt for distributed or long-running transaction management.
Nevertheless, saga is a powerful mechanism and easy readable api can help to integrate this pattern into different projects.
Microsaga library provides simple and readable api for saga actions and their compensations, giving possibility to declare sagas in composable way.
Inspired by [cats-saga](https://github.com/VladKopanev/cats-saga).
Contains one and only dependency to [failsafe](https://github.com/failsafe-lib/failsafe) which allows to use retry behavior in a flexible way.

# Usage

Add dependency to your project with `gradle`:  
`implementation group: 'io.github.rmaiun', name: 'microsaga', version: '1.0.0'`  
or you can use another build tools.  
Actual version also could be checked at [mvnrepository.com](https://mvnrepository.com/artifact/io.github.rmaiun/microsaga)

# Api description

## Core Components

To declare `Saga` we need to have some saga steps. To create saga step, we need to prepare 2 main parts:

- action, which is mandatory
- compensation, which is optional

Action represents the logical block, which is a part of long-running transaction.  
It consists of name and `Callable<A>` action, which are mandatory attributes and optionally allows to describe `RetryPolicy<A>` retryPolicy.
`saga`. `Sagas` class contains a lot of useful methods to cooperate with sagas and their parts.
To create saga action we can easily call:

```java
    Saga<User> createUserAction = Sagas.action("createUser",() -> myService.createUser(user));
    // or using retry policy
    Saga<User> createUserAction= Sagas.retryableAction("createUser", () -> myService.createUser(user), new RetryPolicy<>().withMaxRetries(3));
```  

Action can have a compensation, which can be also created using `Sagas` class:

```java
    SagaCompensation removeUserCompensation = Sagas.compensation("removeUserFromDb",
    () -> userService.deleteUserByCriteria());
    // or using retry policy
    SagaCompensation removeUserCompensation = Sagas.retryableCompensation("removeUserFromDb",
    () -> userService.deleteUserByCriteria(),
    new RetryPolicy<>().withDelay(Duration.ofSeconds(2)));
```  

The main difference here is that action is `Callable<A>` because next action can be dependent on result of previous one.
Compensation is `Runnable` because it hasn't any dependency to other ones.
While we have both action and compensation, we can combine them to some saga step:

```java
    Saga<User> saga = createUserAction.compensate(removeUserCompensation);
    // or we can declare full saga in a one place
    Saga<User> saga = Sagas.action("createUser",() -> myService.createUser(user))
    .retryableCompensation("removeUserFromDb",
    () -> userService.deleteUserByCriteria(), new RetryPolicy<>().withDelay(Duration.ofSeconds(2)));
```  

## Available Operators

There different combination operators available in microsaga library:

* *then()* sequentially runs 2 saga steps where second step doesn't require output of first step

```java
    sagaStep1.then(sagaStep2); 
```

* *flatmap()* gives possibility for second step to consume output dto of first step as input param

```java
    sagaStep1.flatMap(step1DtoOut -> runSagaStep2(step1DtoOut));
```

* *zipWith* uses the same principle as flatMap but is extended with transformer function, which can change output dto based on input and output of particular saga step

```java
    sagaStep1.zipWith(step1DtoOut -> runSagaStep2(step1DtoOut), (step2Input,step2Output) -> new Step2ResultModified());
    // or if you don't need step1DtoOut
    sagaStep1.zipWith(runSagaStep2(), step2Output -> new Step2ResultModified());

```

## Evaluation

Saga supports lazy evaluation, so it will not be run until we ask for it.
To launch it, we should create instance of `SagaManager` or call `SagaManager.use(saga)` static method. This class is responsible for saga transactions
so lets run our saga:

```java
    // returns EvaluationResult (1)
    EvaluationResult<User> result = SagaManager.use(saga).transact();
    // returns value or throws RuntimeException (2)    
    User user = SagaManager.use(saga).transactOrThrow();
```

where  
1 - EvaluationResult contains value or exception with evaluation history, which included steps with calculation time and saga name, which can be customized using `SagaManager`  
2 - there are 2 types of exceptions `SagaActionFailedException` and `SagaCompensationFailedException` related to particular failed saga part. However, user can define exception transformer.

As it was mentioned above, saga steps are composable, so it is possible to write quite complex sagas, like:

```java
    AtomicInteger x = new AtomicInteger();
    Saga<Integer> saga = Sagas.action("initX", x::incrementAndGet).compensate("intoToZero", () -> x.set(0))
    .then(Sagas.action("multiplyBy2", () -> x.get() * 2).compensate("divideBy2", () -> x.set(x.get() / 2)))
    .flatmap(a -> Sagas.action("intoToString", a::toString).withoutCompensation())
    .flatmap(str -> Sagas.retryableAction("changeString", () -> "prefix=" + str, new RetryPolicy<String>().withMaxRetries(2)).withoutCompensation())
    .map(res -> res.split("=").length);
```  

## Saga identification
By default saga will use `UUID.randomUUID()` as sagaId. User can customize saga identification using
`sagaManager.saga(someSaga).withName("cusomSagaId")` approach.  
However, there is possibility to pass sagaId implicitly to any action or compensation. To do this, need to use different api.  
For example:
```java
    // action with sagaId
    Sagas.action("testAction", sagaId -> doSomething(sagaId, dtoIn));
    // compensation with sagaId
    Sagas.compensation("compensation#1", sagaId -> deleteAllBySagaId(sagaId));
```
Saga runner will use predefined sagaId and propagate it to all actions and compensations which need it.

[Link-Codecov]: https://codecov.io/gh/rmaiun/microsaga?branch=main "Codecov"

[Link-GHA]: https://github.com/rmaiun/microsaga/actions "github actions"

[Link-SonatypeReleases]: https://repo1.maven.org/maven2/io/github/rmaiun/microsaga/ "Sonatype Release"

[Badge-Codecov]: https://codecov.io/gh/rmaiun/microsaga/branch/main/graph/badge.svg "Codecov"

[Badge-GHA]: https://github.com/rmaiun/microsaga/actions/workflows/microsaga.yml/badge.svg "Github actions"

[Badge-SonatypeReleases]: https://img.shields.io/badge/release-1.0.0-blueviolet "Sonatype Releases"