# flight-booking-system

## Description

Flight booking system is a try to apply hexagonal architecture and domain-driven design patterns to the clojure world.

Keywords: `microservice`, `clojure`, `Hexagonal-Architecture`, `SOLID`, `dependecy-inversion`, `Domain-Driven Design`, 
`functional-programming`, `Testing`, `Domain-Events`, `Pedestal`

## Why

Learning clojure I was wondering if an architecture pattern like hexagonal architecture that mainly relies on the 
dependency inversion principle could make sense in a dynamic language where types are weak.

Here you have the result, even though I am not an expert on clojure I think this pattern could be applied and can bring 
its main benefit, the business/domain isolation.

## Test
```shell
lein test
```
Output:
```shell
lein test flight-booking-system.application-service.flight-services-test
lein test flight-booking-system.domain-model.flight-test
lein test flight-booking-system.infrastructure.in-adapters.http-routes-test
lein test flight-booking-system.infrastructure.out-adapters.in-memory-event-publisher-test
lein test flight-booking-system.infrastructure.out-adapters.in-memory-flight-repository-test

Ran 10 tests containing 34 assertions.
0 failures, 0 errors.
```

## Run

```shell
lein uberjar
```

### Usage

```shell
lein run
```

```shell
curl --request POST http://localhost:8080/flights \                
  --header 'Content-Type: application/json' \
  --data '{"flight-code":"LH1617", "seat-rows": 24, "seats-per-row": 6}' \
  --include
```

```shell
curl --request POST http://localhost:8080/flights/LH1617/bookings \
  --header 'Content-Type: application/json' \
  --data '{"seat-letter": "a", "seat-row": 1}' \
  --include
```

```shell
curl --request GET http://localhost:8080/flights/LH1617
```

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
