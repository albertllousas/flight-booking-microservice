(ns flight-booking-system.infrastructure.out-adapters.in-memory-flight-repository
  (:require [flight-booking-system.domain-model.flight-repository :as protocol]))

;add valid flight specs

(deftype InMemoryFlightRepository [initial-data-set]
  protocol/FlightRepository
  (protocol/find [_ flight-code] (get @initial-data-set (keyword flight-code)))
  (protocol/save! [_ flight]  (swap! initial-data-set assoc (keyword (:code flight)) flight)))

(defn create [initial-data]
  (->InMemoryFlightRepository (atom initial-data)))
