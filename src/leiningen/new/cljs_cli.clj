(ns leiningen.new.cljs-cli
  (:require [leiningen.new.templates :refer [->files
                                             date
                                             multi-segment
                                             name-to-path
                                             project-name
                                             renderer
                                             sanitize-ns
                                             year]]
            [leiningen.core.main :as main]))

(def render (renderer "cljs-cli"))

(def files {:common          [[".gitignore"                            "gitignore"]
                              [".hgignore"                             "hgignore"]
                              ["CHANGELOG.md"                          "CHANGELOG.md"]
                              ["LICENSE"                               "LICENSE"]
                              ["README.md"                             "README.md"]
                              ["docs/intro.md"                         "docs/intro.md"]
                              ["package.json"                          "package.json"]
                              ["src/{{nested-dirs}}/core.cljs"         "src/core.cljs"]
                              ["src/{{nested-dirs}}/events.cljs"       "src/events.cljs"]
                              ["src/{{nested-dirs}}/keys.cljs"         "src/keys.cljs"]
                              ["src/{{nested-dirs}}/subs.cljs"         "src/subs.cljs"]
                              ["src/{{nested-dirs}}/views.cljs"        "src/views.cljs"]
                              ["src/{{nested-dirs}}/debug/views.cljs"  "src/debug/views.cljs"]
                              ["test/{{nested-dirs}}/core_test.cljs"   "test/core_test.cljs"]]

            "+lein-figwheel" [["dev/user.clj"                          "lein-figwheel/dev/user.clj"]
                              ["project.clj"                           "lein-figwheel/project.clj"]
                              ["test/{{nested-dirs}}/test_runner.cljs" "lein-figwheel/test/test_runner.cljs"]]

            "+figwheel-main" [["dev/user.clj"                          "fighwheel-main/dev/user.clj"]
                              ["dev.cljs.edn"                          "figwheel-main/dev.cljs.edn"]
                              ["figwheel-main.edn"                     "figwheel-main/figwheel-main.edn"]
                              ["project.clj"                           "figwheel-main/project.clj"]
                              ["test.cljs.edn"                         "figwheel-main/test.cljs.edn"]
                              ["test/{{nested-dirs}}/test_runner.cljs" "figwheel-main/test/test_runner.cljs"]]

            "+shadow"        []})

(def opts (set (keys files)))

(defn select-files
  [args]
  (reduce into (get files :common) (vals (select-keys files args))))

(defn create-files
  [name args]
  (let [render (renderer "cljs-cli")
        main-ns (sanitize-ns name)
        data {:raw-name name
              :name (project-name name)
              :namespace (multi-segment main-ns)
              :main-ns main-ns
              :nested-dirs (name-to-path main-ns)
              :year (year)
              :date (date)}]
    (main/info "Generating fresh 'lein new' cljs-cli project.")
    (apply ->files data (select-files args))))

(defn args-valid?
  [args]
  (and (every? opts args)
       (= (count (filter opts args)) 1)))

(defn help
  []
  (println "USAGE: lein new cljs-cli [args...]")
  (println "")
  (println   "  Args can be one of the following:

    +lein-figwheel
        Generate a ClojureScript CLI template using lein-fighwheel.
        https://github.com/bhauman/lein-figwheel

    +figwheel-main
        Generate a ClojureScript CLI template using figwheel-main.
        https://figwheel.org/

    +shadow
        Generate a ClojureScript CLI template using shadow-cljs
        http://shadow-cljs.org/"))

(defn display-help
  []
  (help)
  (System/exit 0))

(defn display-error
  []
  (println "Invalid arguments to new cljs-cli template.")
  (println "")
  (help)
  (System/exit 1))

(defn cljs-cli
  "FIXME: write documentation"
  ([name]
   (create-files name ["+lein-figwheel"]))
  ([name & args]
   (if (args-valid? args)
     (create-files name args)
     (display-error))))

(comment
 (select-files [])
 (select-files ["+bad"])
 (select-files ["+lein-figwheel"])
 (args-valid? ["+lein-figwheel"])
 (args-valid? ["+figwheel-main"])
 (args-valid? ["+shadow"])
 (args-valid? [])
 (args-valid? ["+lein-figwheel" "+figwheel-main" "+shadow"])
 (args-valid? ["+false" "+lein-figwheel"])
 (help))
