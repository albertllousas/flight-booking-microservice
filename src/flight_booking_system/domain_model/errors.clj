(ns flight-booking-system.domain-model.errors)

(def concurrent-flight-modification-exception
  (ex-info "Already existent flight" {:type :concurrent-flight-modification}))

; add aggregate-version

(def non-existent-seat-exception
  (ex-info "Non existent seat" {:type :non-existent-seat}))

(def already-existent-flight-exception
  (ex-info "Already existent flight" {:type :already-existent-flight}))

(def invalid-number-of-seat-rows-exception
  (ex-info "Invalid number of seat rows" {:type :invalid-number-of-seat-rows}))

(def invalid-number-of-seats-per-row-exception
  (ex-info "Invalid number of seats per row" {:type :invalid-number-of-seats-per-row}))

(def already-existent-booking-exception
  (ex-info "Already existent booking" {:type :already-existent-booking}))

(def non-existent-flight-exception
  (ex-info "Non existent flight" {:type :non-existent-flight}))