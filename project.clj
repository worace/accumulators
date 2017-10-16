(defproject accumulators "0.1.0-SNAPSHOT"
  :description "Sample flambo spark project demonstrating accumulator usage."
  :url "http://github.com/worace/accumulators-example"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [yieldbot/flambo "0.8.2"]
                 [cheshire "5.7.1"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [environ "1.1.0"]
                 [midje "1.8.3"]
                 [me.raynes/fs "1.4.6"]]
  :main accumulators.core
  :plugins [[lein-midje "3.2.1"]
            [lein-environ "1.1.0"]]
  :profiles {:provided {:dependencies
                        [[org.apache.spark/spark-core_2.11 "2.1.0"]
                         [org.apache.spark/spark-sql_2.11 "2.1.0"]]}
             :dev {:aot :all}
             :uberjar {:aot :all}})
