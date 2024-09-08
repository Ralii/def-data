(ns def-data.core
  (:require
   [malli.utils :as mu]
   [malli.core :as m]
   [clojure.string :as str]
   [cljs.analyzer :as analyzer]
   [clojure.walk :as walk]))

(defn- malli-core-in-not-found-or-in-last-position? [{:keys [path]}]
  (let [last-item (last path)]
  (cond

    (and (some #(= % :malli.core/in) (butlast path))
         (= last-item :malli.core/in)) false

    (= last-item :malli.core/in) true             ; True if :malli.core/in is at the last position

    (some #(= % :malli.core/in) (butlast path)) false  ; False if found anywhere else

    :else true)))

(defn- schema->accessor-defs [malli-schema]
  (->> malli-schema
       mu/subschemas
       (filter (fn [object]
                 (malli-core-in-not-found-or-in-last-position? object)))

       (map (fn [{:keys [path]}]
               (filterv (fn [item]
                            (and
                             (keyword? item)
                             (not= :malli.core/in item))) path)))
       distinct))

(defn- remove-namespace [symbol]
   (name (last (str/split (str symbol) #"/"))))

(defn- schema-path->accessor-def-name [schema-path]
  (when (not-empty schema-path)
    {:accessor-symbol (symbol (str/join "=>" (map name schema-path)))
     :keywords schema-path}))

(defn- resolve-symbol [env schema]
  (let [env-for-var (analyzer/resolve-var env schema)
        ns-for-var (-> env-for-var :ns)
        source-form (-> env-for-var
                        :root-source-info
                        :source-form
                        (nth 2))
        namespaced-symbols (clojure.walk/prewalk
                            (fn [item] (if (and (symbol? item)
                                                (not= "merge" (remove-namespace item)))
                                         (symbol (str ns-for-var "/" (name item)))
                                         item))
                            source-form)]
    namespaced-symbols))

(defn- resolve-symbols [env schema]
  (clojure.walk/prewalk
   #(if (and (symbol? %)
             (not (contains? (m/predicate-schemas) %)))
      (if (= (remove-namespace %) "merge")
        'malli.util/merge
        (resolve-symbol env %))
      %) schema))

(defn- accessor-defs->short-paths [accessor-defs]
  (distinct (->> accessor-defs
                 (mapcat #(take (count %) (iterate butlast %)))
                 (map #(vec %)))))

(defmacro def-data [fn-name base-key schema]
  (let [forms (resolve-symbols &env schema)
        function-accessors (->>
                            forms
                            eval
                            schema->accessor-defs
                            accessor-defs->short-paths
                            (map schema-path->accessor-def-name))]
    `(do (def ~fn-name ~schema)
         ~@(map
            (fn [accessor#]
              `(def ~(:accessor-symbol accessor#)
                 [~base-key ~@(:keywords accessor#)]))
            function-accessors))))
