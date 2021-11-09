(ns flight-booking-system.application-service.flight-services-test
  (:require [clojure.test :refer :all])
  (:require [flight-booking-system.application-service.flight-services :refer [create! book! find]])
  (:require [flight-booking-system.domain-model.flight-repository :as repository]
            [flight-booking-system.domain-model.events :as events]
            [spy.core :as spy]
            [spy.protocol :as protocol]
            [spy.assert :as assert])
  (:import (java.time LocalDateTime)))

(def now (LocalDateTime/now))

(defn get-instant [] now)

(defn generate-ref [] "ref")

(def flight {:code "code" :rows (list {:row-number 1 :seats (list {:seat "a" :booked false})})})

(def flight-repository-mock (protocol/mock repository/FlightRepository
                                           (repository/find [this flight-code] flight)
                                           (repository/save! [this flight] flight)))

(def event-publisher-mock (protocol/mock events/EventPublisher
                                         (events/publish! [this event] nil)))

(def create-fn-with-dependencies! (partial create! flight-repository-mock event-publisher-mock get-instant))

(def book-fn-with-dependencies! (partial book! flight-repository-mock event-publisher-mock get-instant generate-ref))

(def get-fn-with-dependencies (partial find flight-repository-mock))

(deftest create!-test
  (testing "should orchestrate the creation of a flight"
    (let [expected-flight {:code "code", :rows (list {:row-number 1, :seats (list {:seat "a", :booked false})})}]
      (do
        (is (nil? (create-fn-with-dependencies! {:flight-code "code" :seat-rows 1 :seats-per-row 1})))
        (assert/called-with? (:save! (protocol/spies flight-repository-mock)) flight-repository-mock
                             expected-flight)
        (assert/called-with? (:publish! (protocol/spies event-publisher-mock)) event-publisher-mock
                             {:event-type :flight-created :flight expected-flight :occurred-on now})
        )))

  (testing "should fail when repository does not satisfy FlightRepository rules"
    (is (thrown?
          AssertionError
          (create! "invalid-repository" event-publisher-mock get-instant {:flight-code "code" :seat-rows 1 :seats-per-row 1}))))

  (testing "should fail when event publisher does not satisfy EventPublisher rules"
    (is (thrown?
          AssertionError
          (create! flight-repository-mock "invalid-event-publisher" get-instant {:flight-code "code" :seat-rows 1 :seats-per-row 1})))))

(deftest book!-test
  (testing "should orchestrate the booking of a seat in a flight"
    (let [expected-flight {:code "code" :rows (list {:row-number 1 :seats (list {:seat "a" :booked true :booking-ref "ref"})})}]
      (do
        (is (=
              (book-fn-with-dependencies! {:flight-code "code" :seat-row 1 :seat-letter "a"})
              {:booking-ref "ref"}))
        (assert/called-with? (:save! (protocol/spies flight-repository-mock)) flight-repository-mock
                             expected-flight)
        (assert/called-with? (:publish! (protocol/spies event-publisher-mock)) event-publisher-mock
                             {:event-type :flight-booked :flight expected-flight :occurred-on now})
        ))))

(deftest get-test
  (testing "should get a flight"
    (is (= (get-fn-with-dependencies "code") flight))))


