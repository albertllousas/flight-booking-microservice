(ns flight-booking-system.infrastructure.out-adapters.in-memory-event-publisher
  (:require [flight-booking-system.domain-model.events :as events]
            [io.pedestal.log :as log]))


; IMPORTANT NOTE: In production code, usually domain event pub-sub model publish to streaming platforms like kafka using
; the transactional outbox pattern, logs, metrics... any side effect that manages domain events

(deftype InMemoryEventPublisher [subscribers]
  events/EventPublisher
  (events/publish! [_ domain-event] (apply list (map (fn [subscribe] (subscribe domain-event)) subscribers))))

(defn log-subscriber [domain-event] (log/info :event domain-event))


