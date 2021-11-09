(ns flight_booking_system.fixtures
  (:import (clojure.lang ExceptionInfo)))

(defmacro catch-ex-info [fn]
  `(try
     ~fn
     (catch ExceptionInfo e#
       {:msg (ex-message e#) :data (ex-data e#)})))
