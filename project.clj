(defproject mthomure/native-runtime "0.1.0-SNAPSHOT"
  :description "Clojure wrapper for Java Native Runtime."
  :url "https://github.com/mthomure/clojure-native-runtime"
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.github.jnr/jnr-ffi "2.0.9"]]
  :profiles {:dev
             {:dependencies [[midje "1.6.3"]
                             [org.clojure/tools.namespace "0.2.11"]]
              :source-paths ["test"]
              :plugins [[lein-midje "3.1.3"]]}})
