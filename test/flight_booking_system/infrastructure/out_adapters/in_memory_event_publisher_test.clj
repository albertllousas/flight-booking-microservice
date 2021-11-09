(ns flight-booking-system.infrastructure.out-adapters.in-memory-event-publisher-test
  (:require [clojure.test :refer :all])
  (:require [flight-booking-system.infrastructure.out-adapters.in-memory-event-publisher :as events-impl]
            [flight-booking-system.domain-model.events :as events]))


(def test-subscriber (fn [domain-event] domain-event))

(def in-memory-event-publisher (events-impl/->InMemoryEventPublisher (list test-subscriber)))

(deftest InMemoryEventPublisher-test

  (testing "should publish an event"
      (is (= (events/publish! in-memory-event-publisher "my-event") '("my-event")))))