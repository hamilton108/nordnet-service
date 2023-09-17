(defproject nordnet-service "1.0.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/core.match "1.0.0"]
                 [org.jsoup/jsoup "1.11.3"]

                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 ;;  [io.pedestal/pedestal.service "0.5.11-beta-1"]
                 ;;  [io.pedestal/pedestal.jetty "0.5.11-beta-1"]
                 ;; [io.pedestal/pedestal.immutant "0.5.11-beta-1"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.11-beta-1"]

                ;------------------ Db  ------------------ 
                 [org.mybatis/mybatis "3.5.9"]
                 [org.postgresql/postgresql "42.3.3"]
                 [redis.clients/jedis "3.3.0" :exclusions [org.slf4j/slf4j-api]]

                ;------------------ Logging  ------------------ 
                 [ch.qos.logback/logback-classic "1.2.10" :exclusions [org.slf4j/slf4j-api]]
                 ;[org.slf4j/jul-to-slf4j "1.7.35"]
                 ;[org.slf4j/jcl-over-slf4j "1.7.35"]
                 ;[org.slf4j/log4j-over-slf4j "1.7.35"]


                ;------------------ Jackson ------------------ 
                 ;[com.fasterxml.jackson.core/jackson-core "2.10.2"]
                 ;[com.fasterxml.jackson.core/jackson-annotations "2.10.2"]
                 [com.fasterxml.jackson.core/jackson-databind "2.10.2"]

                ;------------------ Web  ------------------ 
                 [net.sourceforge.htmlunit/htmlunit "2.44.0"
                  :exclusions [org.eclipse.jetty/jetty-http org.eclipse.jetty/jetty-io]]

                ;------------------ Local libs ------------------ 
                 [rcstadheim/oahu "${oahu}"]
                 [rcstadheim/vega "${vega}"]

                ;------------------ Diverse  ------------------ 
                 [colt/colt "1.2.0"]
                 ;[org.clojure/core.cache "1.0.207"]
                 [com.github.ben-manes.caffeine/caffeine "3.1.2"]

                 [clj-http "3.12.3"]]
  :min-lein-version "2.0.0"
  :repositories {"project" "file:/home/rcs/opt/java/mavenlocalrepo"}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :resource-paths ["config", "resources"]
  :test-paths ["test"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "nordnet-service.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.11-beta-1"]]}
             :uberjar {:aot [nordnetservice.server]}}
  :main ^{:skip-aot true} nordnetservice.server)
