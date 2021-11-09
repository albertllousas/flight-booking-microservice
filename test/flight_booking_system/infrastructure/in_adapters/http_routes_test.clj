(ns flight-booking-system.infrastructure.in-adapters.http-routes-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as http]
            [spy.core :as spy]
            [spy.assert :as assert]
            [cheshire.core :as json]
            [flight-booking-system.domain-model.errors :refer :all]
            [flight-booking-system.infrastructure.in-adapters.http-routes :as http-routes]))

(def flight {:code "code" :rows (list {:row-number 1 :seats (list {:seat "a" :booked true :booking-ref "ref"})})})

(def get-flight (spy/spy (fn [request] flight)))

(def create-flight! (spy/spy (fn [request])))

(def book-flight! (spy/spy (fn [request] {:booking-ref "f68f0b5f-4700-421c-a59f-05f72b675f31"})))

(defn create-flight-fails! [exception] (spy/spy (fn [request] (throw exception))))

(defn book-flight-fails! [exception] (spy/spy (fn [request] (throw exception))))

(defn get-flight-fails! [exception] (spy/spy (fn [request] (throw exception))))

(defn clear-fixture [f] (do (spy/reset-spy! create-flight!) (spy/reset-spy! book-flight!)) (f))

(use-fixtures :each clear-fixture)

(def service
  (::http/service-fn ((partial http-routes/create-server 8080 create-flight! book-flight! get-flight))))

(defn create-failing-service-with [exception]
  (::http/service-fn
    ((partial http-routes/create-server
              8080
              (create-flight-fails! exception)
              (book-flight-fails! exception)
              (get-flight-fails! exception)))))

(deftest create-flight-test

  (testing "should create a flight successfully"
    (do
      (is (= (:status (response-for service
                                    :post "/flights"
                                    :headers {"Content-Type" "application/json"}
                                    :body (json/encode {:flight-code "LH1617", :seat-rows 24, :seats-per-row 6})))
             201))
      (is (spy/called-with? create-flight! {:flight-code "LH1617", :seat-rows 24, :seats-per-row 6}))))

  (testing "should fail creating a flight when request body is wrong"
    (is (= (:status (response-for service
                                  :post "/flights"
                                  :headers {"Content-Type" "application/json"}
                                  :body (json/encode {:code "LH1617"})))
           400)))

  (testing "should fail creating a flight when it already exists in the system"
    (is (= (:status (response-for (create-failing-service-with already-existent-flight-exception)
                                  :post "/flights"
                                  :headers {"Content-Type" "application/json"}
                                  :body (json/encode {:flight-code "LH1617", :seat-rows 24, :seats-per-row 6})))
           409))))

(deftest book-flight-test

  (testing "should book a flight successfully"
    (let [response (response-for service
                                 :post "/flights/LH1617/bookings"
                                 :headers {"Content-Type" "application/json"}
                                 :body (json/encode {:seat-row 2 :seat-letter "f"}))]
      (do
        (is (= (:status response) 201))
        (is (= (:body response) (json/encode {:booking-ref "f68f0b5f-4700-421c-a59f-05f72b675f31"})))
        (is (spy/called-with? book-flight! {:flight-code "LH1617" :seat-row 2 :seat-letter "f"})))))

  (testing "should fail booking a flight when request body is wrong"
    (is (= (:status (response-for service
                                  :post "/flights/LH1617/bookings"
                                  :headers {"Content-Type" "application/json"}
                                  :body (json/encode {:seat-row "2" :seat-letter "f"})))
           400)))

  (testing "should fail booking a flight when it does not exists in the system"
    (is (= (:status (response-for (create-failing-service-with non-existent-flight-exception)
                                  :post "/flights/LH1617/bookings"
                                  :headers {"Content-Type" "application/json"}
                                  :body (json/encode {:seat-row 2 :seat-letter "f"})))
           404)))

  (testing "should fail booking a flight when seat is already booked"
    (is (= (:status (response-for (create-failing-service-with already-existent-booking-exception)
                                  :post "/flights/LH1617/bookings"
                                  :headers {"Content-Type" "application/json"}
                                  :body (json/encode {:seat-row 2 :seat-letter "f"})))
           409))))

(deftest get-flight-test

  (testing "should get a flight successfully"
    (let [response (response-for service
                                 :get "/flights/LH1617"
                                 :headers {"Content-Type" "application/json"})]
      (do
        (is (= (:status response) 200))
        (is (= (:body response) (json/encode flight))))))

  (testing "should fail getting a flight when it does not exists in the system"
    (is (= (:status (response-for (create-failing-service-with non-existent-flight-exception)
                                  :get "/flights/LH1617/bookings"
                                  :headers {"Content-Type" "application/json"}))
           404))))
