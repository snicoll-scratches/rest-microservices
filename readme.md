# REST based micro-services sample

[![Build Status](https://travis-ci.org/olivergierke/rest-microservices.svg?branch=master)](https://travis-ci.org/olivergierke/rest-microservices)

## tl;dr

- Two Spring Boot based Maven projects that are standalone applications:
  - Stores (MongoDB, exposing a few Starbucks shops across north america, geo-spatial functionality)
  - Customers (JPA)

## Fundamentals

The core domain and focus of the example can be explored by simply starting both the customer and the
store service using `mvn spring-boot:run`. The store service exposes a resource to trigger geo-spatial
queries for Starbucks shops given a reference location and distance.

## The customer service

TODO

## The use of Hystrix

The customer service uses Hystrix to short-circuit the discovery calls trying to find the store system
if the link discovery or validation fails repeatedly. To see this working run the `hystrix-dashboard`
app (`mvn spring-boot:run`), browse to http://localhost:7979/hystrix and point the dashboard to the 
ustomer service's Hystrix stream (http://localhost:8080/hystrix.stream).

## Using service discovery

As an alternative to the static service reference the customer service uses by default, service discovery
via Eureka can be used. Make sure both the customer and store service are stopped. Start the `eureka-server`
application using `mvn spring-boot:run`. Browse `http://localhost:8761` to see the Eureka web interface.

Now start both the customer and the store service with the `cloud` profile enabled
(`mvn spring-boot:run -Dspring.profiles.active="cloud"`). Inspecting the Eureka web interface you should
see both instances being registered with the registry. The customer service now uses a `DiscoveryClient`
to obtain a `ServiceInstance` by name (see the `CustomerApplication.CloudConfig.dynamicServiceProvider(…)`
bean definition).
