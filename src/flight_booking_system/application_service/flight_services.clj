(ns flight-booking-system.application-service.flight-services
  (:require [flight-booking-system.domain-model.flight :as flight]
            [flight-booking-system.domain-model.flight-repository :as repository]
            [flight-booking-system.domain-model.events :as events]))

(defn create!

  "Use-case: c=Creation of a flight. Just side effects.
  Returns: nil
  Throws: ex-info with :type :invalid-number-of-seat-rows|:invalid-number-of-seats-per-row|:already-existent-flight
  Notes: repository should satisfy FlightRepository protocol and event-publisher should satisfy EventPublisher protocol"

  [repository event-publisher get-instant {flight-code :flight-code seat-rows :seat-rows seats-per-row :seats-per-row}]
  {:pre [(satisfies? repository/FlightRepository repository) (satisfies? events/EventPublisher event-publisher)]}
  (->> (flight/create flight-code seat-rows seats-per-row)
       (repository/save! repository)
       (events/flight-created get-instant)
       (events/publish! event-publisher)))

(defn book!

  "Use-case: Booking a seat of a flight.
  Returns: {:booking-ref \"booking-reference-code\"}
  Throws: ex-info with :type :already-existent-booking|:non-existent-flight|:non-existent-seat|:concurrent-flight-modification
  Notes: repository should satisfy FlightRepository protocol and event-publisher should satisfy EventPublisher protocol"

  [repository event-publisher get-instant generate-ref {flight-code :flight-code seat-row :seat-row seat-letter :seat-letter}]
  {:pre [(satisfies? repository/FlightRepository repository) (satisfies? events/EventPublisher event-publisher)]}
  (let [booking-ref (generate-ref)]
    (->> (repository/find repository flight-code)
         (flight/book booking-ref seat-row seat-letter)
         (repository/save! repository)
         (events/flight-booked get-instant)
         (events/publish! event-publisher))
    {:booking-ref booking-ref}))

(defn find

  "Use-case: Getting a flight.
  Returns: {:code \"code\",
            :rows (list {:row-number 1,
                         :seats (list {:seat \"a\", :booked false} {:seat \"b\", :booked true, :booking-ref \"ref\"}))}
  Throws: ex-info with :type :non-existent-flight
  Notes: repository should satisfy FlightRepository protocol"

  [repository flight-code]
  {:pre [(satisfies? repository/FlightRepository repository)]}
  (repository/find repository flight-code))
