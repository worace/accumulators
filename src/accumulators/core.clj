(ns accumulators.core
  (:require [clojure.tools.cli :as cli]
            [flambo.api :as f]
            [clojure.string :refer [lower-case]]
            [clojure.tools.logging :as log]
            [flambo.conf :as conf])
  (:gen-class))

(def local-spark-conf (-> (conf/spark-conf)
                          (conf/master "local")
                          (conf/app-name "tile_builder")))

(def prod-spark-conf  (-> (conf/spark-conf)
                          (conf/app-name "tile_builder")))

(defn make-counter [sc name]
  (.longAccumulator (.sc sc) name))

(def alphabet (map (comp str char) (range 65 91)))

(defn timestamp [] (str (int (/ (System/currentTimeMillis) 1000.0))))
(defn out-dir [] (str "/tmp/accumulators_" (timestamp)))

(defn -main [& args]
  (log/warn "*** Running Main ***")
  (let [sc (f/spark-context local-spark-conf)
        out-dir (out-dir)
        counter (make-counter sc "num-lines")
        counter-action (make-counter sc "num-lines-action")
        counter-executor (make-counter sc "executor-counter")
        upper (f/parallelize sc alphabet)
        _ (log/warn "*** Input RDD: " upper)
        lower (f/map upper (f/fn [char]
                             (log/warn "*** Map Processing Char: " char " ***")
                             (log/warn "Counter object: " counter)
                             (log/warn "Counter value: " (.value counter))
                             (.add counter (long 1))
                             (lower-case char)))
        action (f/foreach upper (f/fn [char]
                                  (log/warn "*** ForEach Processing Char: " char " ***")
                                  (log/warn "Counter object: " counter-action)
                                  (log/warn "Counter value: " (.value counter-action))
                                  (.add counter-action (long 1))
                                  ))]
    (log/warn "Increment Counter Executor")
    (.add counter-executor (long 1))
    (log/warn "*** Counter Executor Value: " (.value counter-executor)) ;; Works
    (log/warn "*** Will Save RDD to file ***")
    (f/save-as-text-file lower (str out-dir "/output"))
    (log/warn "*** Counter object after saving rdd: " counter)
    (log/warn "*** Counter value after saving rdd: " (.value counter)) ;; Doesn't Work
    (log/warn "******* Counter from Action ********")
    (log/warn "*** Counter object after saving rdd: " counter-action)
    (log/warn "*** Counter value after saving rdd: " (.value counter-action)) ;; Doesn't work
    (log/warn "Results to: " out-dir)
    (f/save-as-text-file (f/parallelize sc [(.value counter) (.value counter-action) (.value counter-executor)]) (str out-dir "/counters"))))
