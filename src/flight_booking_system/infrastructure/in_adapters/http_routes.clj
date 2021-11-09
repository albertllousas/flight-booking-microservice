(ns flight-booking-system.infrastructure.in-adapters.http-routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor.error :as error-int]
            [flight-booking-system.domain-model.errors :as domain-errors]
            [clojure.spec.alpha :as spec]))

(spec/def ::flight-code string?)
(spec/def ::seat-rows int?)
(spec/def ::seats-per-row int?)
(spec/def ::create-flight-request (spec/keys :req-un [::flight-code ::seat-rows ::seats-per-row]))

(defn create-flight-route [create-flight! request]
  {:pre [(spec/valid? ::create-flight-request (:json-params request))]}
  (create-flight! (:json-params request))
  {:status 201})

(spec/def ::seat-row int?)
(spec/def ::seat-letter string?)
(spec/def ::book-flight-request (spec/keys :req-un [::seat-row ::seat-letter]))

(defn book-flight-route [book-flight! request]
  {:pre [(spec/valid? ::book-flight-request (:json-params request))]}
  (let [booking (book-flight! (assoc (:json-params request) :flight-code (:code (:path-params request))))]
    {:status 201 :body booking}))

(defn get-flight-route [get-flight! request]
  (let [flight (get-flight! (:code (:path-params request)))]
    {:status 200 :body flight}))

(def service-error-handler
  (error-int/error-dispatch
    [ctx ex]
    [{:exception-type :java.lang.AssertionError}] (assoc ctx :response {:status 400})
    [{:type :already-existent-flight}] (assoc ctx :response {:status 409})
    [{:type :already-existent-booking}] (assoc ctx :response {:status 409})
    [{:type :non-existent-flight}] (assoc ctx :response {:status 404})
    :else (assoc ctx :io.pedestal.interceptor.chain/error ex)))

(def common-interceptors [(body-params/body-params) http/json-body service-error-handler])

(defn create-server [port create-flight! book-flight! get-flight]
  (let [routes
        #{["/flights" :post (conj common-interceptors (partial create-flight-route create-flight!)) :route-name :create-flight]
          ["/flights/:code/bookings" :post (conj common-interceptors (partial book-flight-route book-flight!)) :route-name :book-flight]
          ["/flights/:code" :get (conj common-interceptors (partial get-flight-route get-flight)) :route-name :get-flight]}]
    (http/create-server
      {::http/routes routes
       ::http/type   :jetty
       ::http/port   port})))

