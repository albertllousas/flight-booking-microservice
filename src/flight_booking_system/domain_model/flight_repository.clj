(ns flight-booking-system.domain-model.flight-repository)

(defprotocol FlightRepository
  (find [this flight-code])
  (save! [this flight]))