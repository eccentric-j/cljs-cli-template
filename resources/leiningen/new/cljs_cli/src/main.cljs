(ns {{main-ns}}.main
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [mount.core :refer [defstate] :as mount]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [{{main-ns}}.core :refer [render screen]]
   [{{main-ns}}.events]
   [{{main-ns}}.keys :as keys]
   [{{main-ns}}.subs]
   [{{main-ns}}.views :as views]))

(def cli-options
  [["-p" "--port PORT" "port number"
    :default 80
    :parse-fn #(js/Number %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :update-fn inc]
   ["-h" "--help"]])

(defn args->opts
  [args]
  (parse-opts args cli-options))

(defn init!
  [view & {:keys [opts]}]
  (mount/start)
  (rf/dispatch-sync [:init (:options opts)])
  (-> (r/reactify-component view)
      (r/create-element #js {})
      (render @screen)))

(defn reload!
  [view]
  (-> (r/reactify-component view)
      (r/create-element #js {})
      (render @screen)))

(defn main!
  [& args]
  (init! views/root :opts (args->opts args)))

(set! *main-cli-fn* main!)
