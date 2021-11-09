(ns flight-booking-system.infrastructure.out-adapters.in-memory-flight-repository-test
  (:require [clojure.test :refer :all])
  (:require [flight-booking-system.infrastructure.out-adapters.in-memory-flight-repository :as repository-impl]
            [flight-booking-system.domain-model.flight-repository :as repository]))

(def in-memory-flight-repository (repository-impl/create
                                   {:BA2490 {:code "BA2490",
                                             :rows (list {:row-number 1, :seats (list {:seat "a", :booked false} {:seat "b", :booked false})}
                                                         {:row-number 2, :seats (list {:seat "a", :booked false} {:seat "b", :booked false})})}
                                    }))

(deftest InMemoryFlightRepository-test

  (testing "should find an existent flight"
    (is (=
          (repository/find in-memory-flight-repository "BA2490")
          {:code "BA2490",
           :rows (list {:row-number 1, :seats (list {:seat "a", :booked false} {:seat "b", :booked false})}
                       {:row-number 2, :seats (list {:seat "a", :booked false} {:seat "b", :booked false})})})))

  (testing "should not find a non existent flight"
    (is (nil? (repository/find in-memory-flight-repository "non-existent"))))

  (testing "should save a new flight"
    (do
      (repository/save! in-memory-flight-repository
                        {:code "BA2491",
                         :rows (list {:row-number 1, :seats (list {:seat "a", :booked false})})})
      (is (=
            (repository/find in-memory-flight-repository "BA2491")
            {:code "BA2491",
             :rows (list {:row-number 1, :seats (list {:seat "a", :booked false})})})))))
