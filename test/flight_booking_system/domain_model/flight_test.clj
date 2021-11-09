(ns flight-booking-system.domain-model.flight-test
  (:require [clojure.test :refer :all])
  (:require [flight_booking_system.fixtures :refer [catch-ex-info]])
  (:require [flight-booking-system.domain-model.flight :as flight]))

(deftest create-test

  (testing "should create a flight"
    (is (=
          (flight/create "code" 2 2)
          {:code "code",
           :rows (list {:row-number 1, :seats (list {:seat "a", :booked false} {:seat "b", :booked false})}
                       {:row-number 2, :seats (list {:seat "a", :booked false} {:seat "b", :booked false})})})))

  (testing "should fail creating a flight with a negative num of rows"
    (is (=
          (catch-ex-info (flight/create "code" -2 2))
          {:msg "Invalid number of seat rows", :data {:type :invalid-number-of-seat-rows}})))

  (testing "should fail creating a flight with a too much num of rows"
    (is (=
          (catch-ex-info (flight/create "code" 100 10))
          {:msg "Invalid number of seat rows", :data {:type :invalid-number-of-seat-rows}})))

  (testing "should fail creating a flight with a negative number of seats in a row"
    (is (=
          (catch-ex-info (flight/create "code" 2 -2))
          {:msg "Invalid number of seats per row", :data {:type :invalid-number-of-seats-per-row}})))

  (testing "should fail creating a flight with a too much number of seats in a row"
    (is (=
          (catch-ex-info (flight/create "code" 2 100))
          {:msg "Invalid number of seats per row", :data {:type :invalid-number-of-seats-per-row}}))))



(deftest book-test
  (let [flight {:code "code",
                :rows (list {:row-number 1 :seats (list {:seat "a" :booked false} {:seat "b" :booked false})}
                            {:row-number 2 :seats (list {:seat "a" :booked false} {:seat "b" :booked false})})}]

    (testing "should book a seat"
      (is (=
            (flight/book "ref" 1 "a" flight)
            {:code "code",
             :rows (list {:row-number 1 :seats (list {:seat "a" :booked true :booking-ref "ref"} {:seat "b" :booked false})}
                         {:row-number 2 :seats (list {:seat "a" :booked false} {:seat "b" :booked false})})})))

    (testing "should fail booking a a non existent seat on a flight"
      (is (=
            (catch-ex-info (flight/book "ref" 2 "c" flight))
            {:msg "Non existent seat" :data {:type :non-existent-seat}})))

    (testing "should fail booking an already booked seat in a flight"
      (let [flight {:code "code" :rows (list {:row-number 1 :seats (list {:seat "a" :booked true :booking-ref "ref"})})}]
      (is (=
            (catch-ex-info (flight/book "ref" 1 "a" flight))
            {:msg "Already existent booking" :data {:type :already-existent-booking}}))))))
