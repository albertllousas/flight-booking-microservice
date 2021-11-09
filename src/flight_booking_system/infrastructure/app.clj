(ns flight-booking-system.infrastructure.app
  (:require [io.pedestal.http :as http]
            [flight-booking-system.infrastructure.out-adapters.in-memory-flight-repository :as repository-impl]
            [flight-booking-system.infrastructure.out-adapters.in-memory-event-publisher :as event-publisher-impl]
            [flight-booking-system.application-service.flight-services :as application-flight-services]
            [flight-booking-system.infrastructure.in-adapters.http-routes :as routes])
  (:import (java.time LocalDateTime)
           (java.util UUID)))

(def flight-repository (repository-impl/create {}))

(def event-publisher (event-publisher-impl/->InMemoryEventPublisher (list event-publisher-impl/log-subscriber)))

(defn get-instant [] (LocalDateTime/now))

(defn generate-ref [] (UUID/randomUUID))

(def create-flight! (partial application-flight-services/create! flight-repository event-publisher get-instant))

(def book-flight! (partial application-flight-services/book! flight-repository event-publisher get-instant generate-ref))

(def get-flight (partial application-flight-services/find flight-repository))

(def port 8080)

(defn -main [& args]
  (println "\nStarting flight booking system in Clojure ...")
  (http/start (routes/create-server port create-flight! book-flight! get-flight)))
