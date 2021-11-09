(ns flight-booking-system.domain-model.events)

(defprotocol EventPublisher
  (publish! [this event]))

(defn flight-created [get-instant flight] {:event-type :flight-created :flight flight :occurred-on (get-instant)})

(defn flight-booked [get-instant flight] {:event-type :flight-booked :flight flight :occurred-on (get-instant)})
