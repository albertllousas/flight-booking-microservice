(ns flight-booking-system.domain-model.flight
  (:require [flight-booking-system.domain-model.errors :refer :all]))

(def limits {:seat-rows-min 0 :seat-rows-max 56 :seats-per-row-min 0 :seats-per-row-max 12})

(def first-alphabet-lowercase-letter-ascii 97)

(defn- create-seats-in-a-row [seats-per-row]
  (->> (map char (range first-alphabet-lowercase-letter-ascii (+ seats-per-row first-alphabet-lowercase-letter-ascii)))
       (map str)
       (map (fn [seat] {:seat seat :booked false}))))

(defn create

  "Creates a flight given a flight code, seat rows and seats per row.
  Example: => (create \"code\" 1 2)
             {:code \"code\",
              :rows (list
                     {:row-number 1, :seats (list {:seat \"a\", :booked false} {:seat \"b\", :booked false})}
                     {:row-number 2, :seats (list {:seat \"a\", :booked false} {:seat \"b\", :booked false})}
                     )
              }
  Throws: ex-info with :type :invalid-number-of-seat-rows or :type :invalid-number-of-seats-per-row"

  [flight-code seat-rows seats-per-row]
  (cond
    (not (< (:seat-rows-min limits) seat-rows (:seat-rows-max limits))) (throw invalid-number-of-seat-rows-exception)
    (not (< (:seats-per-row-min limits) seats-per-row (:seats-per-row-max limits))) (throw invalid-number-of-seats-per-row-exception)
    :else {:code flight-code
           :rows (->> (range 0 seat-rows)
                      (map inc)
                      (map #(hash-map :seats (create-seats-in-a-row seats-per-row) :row-number %)))
           }))

(defn- find-seat [seat-row seat-letter flight]
  (->> (:rows flight)
       (filter (fn [row] (= (:row-number row) seat-row)))
       (first)
       (:seats)
       (filter (fn [seat] (= (:seat seat) seat-letter)))
       (first)))

(defn- book-seat [booking-ref seat-row seat flight]
  (let [booked-seat (assoc seat :booked true :booking-ref booking-ref)]
    (->> (:rows flight)
         (map (fn [row]
                (if (and (.contains (:seats row) seat) (= (:row-number row) seat-row))
                  (assoc row :seats (replace {seat booked-seat} (:seats row)))
                  row)))
         (assoc flight :rows))))

(defn book [booking-ref seat-row seat-letter flight]
  (let [seat (find-seat seat-row seat-letter flight)]
    (cond
      (nil? seat) (throw non-existent-seat-exception)
      (contains? seat :booking-ref) (throw already-existent-booking-exception)
      :else (book-seat booking-ref seat-row seat flight))))




