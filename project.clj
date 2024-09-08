(defproject org.clojars.ralii/def-data "0.1.1"
  :description "Def data"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [metosin/malli "0.16.0"]]
  :main ^:skip-aot def-data.core
  :target-path "target/%s"
    :repositories [["releases" {:url "https://repo.clojars.org"
                              :sign-releases false}]]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
