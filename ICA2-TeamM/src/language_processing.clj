(ns language-processing
  (:require [org.clojars.cognesence.matcher.core :refer :all])
  (:require [TownTraversal :as tt])
  )


(def lexicon
  '{
    primary-school   {:cat noun :sem place}
    hospital         {:cat noun :sem place}
    flower-shop      {:cat noun :sem place}
    bakery           {:cat noun :sem place}
    gregs-house      {:cat noun :sem place}
    chinese-takeaway {:cat noun :sem place}
    butchers         {:cat noun :sem place}
    train-station    {:cat noun :sem place}
    the-club         {:cat noun :sem place}
    town-centre      {:cat noun :sem place}
    secondary-school {:cat noun :sem place}
    burger-town      {:cat noun :sem place}
    supermarket      {:cat noun :sem place}
    police-station   {:cat noun :sem place}
    gym    		       {:cat noun :sem place}
    fire-station     {:cat noun :sem place}
    church           {:cat noun :sem place}
    dock             {:cat noun :sem place}

    the              {:cat det}
    from             {:cat det}
    a                {:cat det}
    an               {:cat det}
    any              {:cat det}
    to               {:cat det}

    move             {:cat verb, :sem move}
    go               {:cat verb, :sem move}
    walk             {:cat verb, :sem move}
    })


(def sentence-morph-rules
  '(
     ;;--Sentence basis--
     ((??a and then ??b)         => (??a stop ??b))
     ((??a then ??b)             => (??a stop ??b))
     ))

(def word-morph-rules
  '(
     ;;--Word basis--
     (from the                => from)
     (to the                  => to)
     )
  )

;;--Morphology application functions using matcher by Simon Lynch
;;--http://s573859921.websitehome.co.uk/pub/clj/tools/shrdlu-files.zip
(defn apply-morph-rules[rules sentence]
  (if (empty? rules) sentence
         (mlet ['(?pre => ?post) (first rules)]
               (mif [(? pre) sentence]
                    (recur rules (mout (? post)))
                    (recur (rest rules) sentence)
                    ))
         ))

(defn compile-word-rules
  "extends rules so they may be more easily applied"
  ; (?x ?y -> ?z) becomes (??a ?x ?y ??b -> ??a ?z ??b)
  [rules]
  (with-mvars {'aa (gensym '??a), 'bb (gensym '??b)}
              (mfor ['(??pre => ??post) rules]
                    (mout '((?aa ??pre ?bb) => (?aa ??post ?bb)))
                    )))

(let [morph-rules (concat (compile-word-rules word-morph-rules)
                          sentence-morph-rules)]
  (defn morph [sentence]
    (apply-morph-rules morph-rules sentence))
  )

;(morph '(move from the church to the dock then move from the dock to the gym))

;;-----Word forms-------------------------------------------------------------------------------------------------------
(defn get-cat [x]
  (get (get lexicon x) :cat)
  )

(defn get-sem [x]
  (get (get lexicon x) :sem)
  )

(defn check-word [expected obj]
  (or (= expected (get-sem obj)) (= expected (get-cat obj)))
  )

(defn adj?   [x] (check-word 'adj x))
(defn det?   [x] (check-word 'det x))
(defn noun?  [x] (check-word 'noun x))
(defn verb?  [x] (check-word 'verb x))
(defn move?  [x] (check-word 'move x))
(defn place? [x] (check-word 'place x))

;; Use defmatch
(defmatch isMovementCommand? []
  (((-> ?d move?) (-> ?p place?) (-> ?p2 place?))
    :=> (boolean 1)
  ))

;(isMovementCommand? '(move gym the-club))

(defn runInput [sentence]
  (if (= (isMovementCommand? (morph sentence)) true)
    (tt/ops-search-traversal-send-wrapper(first (rest sentence)) (first (rest (rest sentence))))
    ("The input was not valid.")
    ))

;;(runInput '(move gym the-club))








