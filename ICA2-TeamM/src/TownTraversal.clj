(
  ns TownTraversal
  (:gen-class)
  (:require [org.clojars.cognesence.matcher.core :refer :all])
  (:require [org.clojars.cognesence.ops-search.core :refer :all])
  (:require [clojure.string :as str :refer [includes?]] :reload-all)
  (load-file "src/Inference Engines/Astar-search.clj")
  (load-file "src/Inference Engines/breadth-search.clj")
  (load-file "src/Tools/socket.clj")
  )

;; For benchmarking
(use 'criterium.core)

;; A-Star---------------------------------------------------------------------------------------------------------------

;; A legal move generator for the search. :cost represents the cost of taking a path , and state the change of taking
;; that path
(defn a*lmg [state]
  (let [n (:state state)
        c (:cost state)
        ]
    (case n

      "primary-school"
      (list
        {:state "bakery", :cost (+ c 10)}
        )

      "gregs-house"
      (list
        {:state "bakery", :cost (+ c 5)}
        )

      "bakery"
      (list
        {:state "gregs-house", :cost (+ c 5)}
        {:state "butchers", :cost (+ c 1)}
        {:state "flower-shop", :cost (+ c 6)}
        {:state "primary-school", :cost (+ c 10)}
        )

      "flower-shop"
      (list
        {:state "bakery", :cost (+ c 6)}
        {:state "chinese-takeaway", :cost (+ c 8)}
        {:state "hospital", :cost (+ c 10)}
        )

      "hospital"
      (list
        {:state "flower-shop", :cost (+ c 10)}
        )

      "butchers"
      (list
        {:state "bakery", :cost (+ c 1)}
        {:state "chinese-takeaway", :cost (+ c 9)}
        {:state "town-centre", :cost (+ c 5)}
        )

      "chinese-takeaway"
      (list
        {:state "flower-shop", :cost (+ c 8)}
        {:state "butchers", :cost (+ c 9)}
        {:state "the-club", :cost (+ c 6)}
        )

      "secondary-school"
      (list
        {:state "town-centre", :cost (+ c 1)}
        {:state "supermarket", :cost (+ c 9)}
        )

      "town-centre"
      (list
        {:state "butchers", :cost (+ c 5)}
        {:state "burger-town", :cost (+ c 10)}
        {:state "fire-station", :cost (+ c 3 )}
        {:state "secondary-school", :cost (+ c 1)}
        {:state "supermarket", :cost (+ c 8)}
        )

      "the-club"
      (list
        {:state "chinese-takeaway", :cost (+ c 6)}
        {:state "train-station", :cost (+ c 8)}
        {:state "police-station", :cost (+ c 2)}
        )

      "train-station"
      (list
        {:state "the-club", :cost (+ c 10)}
        )

      "supermarket"
      (list
        {:state "secondary-school", :cost (+ c 9)}
        {:state "town-centre", :cost (+ c 8)}
        {:state "dock", :cost (+ c 8)}
        )

      "burger-town"
      (list
        {:state "town-centre", :cost (+ c 10)}
        {:state "gym", :cost (+ c 9)}
        )

      "dock"
      (list
        {:state "supermarket", :cost (+ c 8)}
        {:state "church", :cost (+ c 1)}
        )

      "church"
      (list
        {:state "dock", :cost (+ c 1)}
        {:state "fire-station", :cost (+ c 7)}
        )

      "fire-station"
      (list
        {:state "church", :cost (+ c 7)}
        {:state "gym", :cost (+ c 9)}
        {:state "town-centre", :cost (+ c 3)}
        )

      "gym"
      (list
        {:state "fire-station", :cost (+ c 9)}
        {:state "burger-town", :cost (+ c 9)}
        {:state "police-station", :cost (+ c 2)}
        )

      "police-station"
      (list
        {:state "gym", :cost (+ c 2)}
        {:state "the-club", :cost (+ c 2)}
        )
      )
    ))

;; A helper function taking start and end locations from the LMG and running the search on them
(defn a*-traversal [start goal]
  (A*search {:state (str start), :cost 0} (str goal) a*lmg))

;; Breadth first--------------------------------------------------------------------------------------------------------

;; A legal move generator for the breadth first search showing states you can reach from your current state
(def breadth-first-lmg
  '{
    primary-school
    (
      bakery
      )
    gregs-house
    (
      bakery
      )
    bakery
    (
      gregs-house
      butchers
      flower-shop
      primary-school
      )
    flower-shop
    (
      bakery
      chinese-takeaway
      hospital
      )
    hospital
    (
      flower-shop
      )
    butchers
    (
      bakery
      chinese-takeaway
      town-centre
      )
    chinese-takeaway
    (
      flower-shop
      butchers
      the-club
      )
    secondary-school
    (
      town-centre
      supermarket
      )
    town-centre
    (
      butchers
      burger-town
      fire-station
      secondary-school
      supermarket
      )
    the-club
    (
      chinese-takeaway
      train-station
      police-station
      )
    supermarket
    (
      secondary-school
      town-centre
      dock
      )
    burger-town
    (
      town-centre
      gym
      )
    dock
    (
      supermarket
      church
      )
    church
    (
      dock
      fire-station
      )
    fire-station
    (
      church
      gym
      town-centre
      )
    gym
    (
      fire-station
      burger-town
      police-station
      )
    police-station
    (
      gym
      the-club
      )
    })

;; A breadth first traversal helper function that runs breadth first search on the LMG taking arguments of the start
;; and goal states.
;; Example call (breadth-traversal 'gym 'primary-school)
(defn breadth-traversal [start goal]
  (breadth-search start goal breadth-first-lmg))

;;ops-search------------------------------------------------------------------------------------------------------------

;; A list of operations that can be used by the ops-search for finding answers to the town traversal problem
(def ops
  '{move    {:pre ( (agent ?agent)
                    (at ?agent ?p1)
                    (connects ?p1 ?p2)
                    )
             :add ((at ?agent ?p2))
             :del ((at ?agent ?p1))
             :txt (move ?p1 to ?p2)
             :cmd [move ?p2]
             }
    purchase {:pre ((agent ?agent)
                   (has ?obj ?place)
                   (at ?agent ?place)
                   )
            :add ((has ?obj ?agent))
            :txt (purchase ?obj from ?place)
            :cmd [purchase ?obj]
            }
    })

;; A world state representing the town in the town traversal problem
(def ops-world-state
  '#{(connects primary-school bakery)
     (connects gregs-house bakery)
     (connects bakery gregs-house) (connects bakery butchers) (connects bakery flower-shop) (connects bakery primary-school)
     (connects flower-shop bakery) (connects flower-shop chinese-takeaway) (connects flower-shop hospital)
     (connects hospital flower-shop)
     (connects butchers bakery) (connects butchers chinese-takeaway) (connects butchers town-centre)
     (connects chinese-takeaway flower-shop) (connects chinese-takeaway butchers) (connects chinese-takeaway the-club)
     (connects secondary-school town-centre) (connects secondary-school supermarket)
     (connects town-centre butchers) (connects town-centre burger-town) (connects town-centre fire-station) (connects town-centre secondary-school) (connects town-centre supermarket)
     (connects the-club chinese-takeaway) (connects the-club train-station) (connects the-club police-station)
     (connects supermarket secondary-school) (connects supermarket town-centre) (connects supermarket dock)
     (connects burger-town town-centre) (connects burger-town gym)
     (connects dock supermarket) (connects dock church)
     (connects church dock) (connects church fire-station)
     (connects fire-station church) (connects fire-station gym) (connects fire-station town-centre)
     (connects gym fire-station) (connects gym burger-town) (connects gym police-station)
     (connects police-station gym) (connects police-station the-club)

     (has chinese-food chinese-takeaway)
     })

;; A helper function taking start and goal locations and running ops-search on them using the world state and operations
;; above.
;; Example call: (ops-search-traversal 'gym 'primary-school)
(defn ops-search-traversal [start goal]
  (ops-search (list '(agent R) (list 'at 'R start)) (list (list 'at 'R goal)) ops :world ops-world-state))

;; A more advanced helper function for running ops-search, taking states as arguments instead.
;; States are taken as lists of tuples. E.g. '((agent R) (at R gym))
;; Example call: (ops-search-advanced (list '(agent R) '(at R gym)) (list '(at R gym) '(has chinese-food R)))
(defn ops-search-advanced [start-conditions end-conditions]
  (ops-search start-conditions end-conditions ops :world ops-world-state))


;;Functions with socket writing-----------------------------------------------------------------------------------------

;; Starts a connection on local port 2222 for socket connectivity to another Java application.
(def s25 (startup-server 2222))

;; A function that takes the result of an a* search and sends commands through a socket 1 second at a time to show
;; traversal.
;; Example call:
;; (a*-traversal-send
;; 'gym
;; 'primary-school
;  ({:state "gym", :cost 0}
;  {:state "fire-station", :cost 9}
;  {:state "town-centre", :cost 12}
;  {:state "butchers", :cost 17}
;  {:state "bakery", :cost 18}
;  {:state "primary-school", :cost 28}))
(defn a*-traversal-send [start end text]
  (if (empty? text)
    ()
    (do
      (println text)
      (println (get (first text) :state))
      (println "Sending" (str "move-to " (get (first text) :state)))
      (println)
      (socket-write s25 (str "move-to " (get (first text) :state)))
      (Thread/sleep 1000)
      (a*-traversal-send start end (rest text)))
    ))

;; A function that takes the result of a breadth first search and sends commands through a socket 1 second at a time to show
;; traversal.
;; Example call: (breadth-traversal-send 'gym 'fire-station '(gym fire-station))
(defn breadth-traversal-send [start end text]
  (if (empty? text)
    ()
    (do
      (println text)
      (println (first text))
      (println "Sending" (str "move-to " (first text)))
      (println)
      (socket-write s25 (str "move-to " (first text)))
      (Thread/sleep 1000)
      (breadth-traversal-send start end (rest text)))
    ))

;; A function that takes the result of a ops-search and sends commands through a socket 1 second at a time to show
;; traversal.
;; Example call: (ops-search-traversal-send
;; 'gym
;; 'fire-station
;; '((move gym to burger-town) (move burger-town to town-centre) (move town-centre to secondary-school)))
(defn ops-search-traversal-send [start end text]
  (if (empty? text)
    ()
    (do
      (println text)
      (println (first text))

      (if (str/includes? (first text) (str "move " start))
        (do
          (println "Sending " (str "move-to " start))
          (socket-write s25 (str "move-to " start))))

      (if (str/includes? (first text) (str "move "))
        (do
          (println "Sending" (str "move-to " (last (first text))))
          (socket-write s25 (str "move-to " (last (first text))))))

      (if (str/includes? (first text) (str "purchase "))
        (do
          (println "Sending" (str "purchase " (last (first text))))
          (socket-write s25 (str "purchase " (last (first text))))))

      (println)
      (Thread/sleep 1000)
      (ops-search-traversal-send start end (rest text)))
    ))

;; Helper functions that call the above functions and just send start -> goal.------------------------------------------
;; Example call (function 'gym 'town)-----------------------------------------------------------------------------------
(defn ops-search-traversal-send-wrapper [start end]
  (ops-search-traversal-send start end (get (ops-search-traversal start end) :txt))
  )

(defn breadth-traversal-send-wrapper [start end]
  (breadth-traversal-send start end (breadth-traversal start end))
  )

(defn a*-traversal-send-wrapper [start end]
  (a*-traversal-send start end (a*-traversal start end))
  )

;; This one is for going from state -> state using the advanced ops-search
(defn ops-search-advanced-send-wrapper [start-states end-states]
  (ops-search-traversal-send start-states end-states (get (ops-search-advanced start-states end-states) :txt)))

;; Example calls
(a*-traversal-send-wrapper 'gym 'primary-school)
(ops-search-traversal-send-wrapper 'gym 'primary-school)
(ops-search-advanced-send-wrapper (list '(agent R) '(at R gym)) (list '(at R gym) '(has chinese-food R)))
(breadth-traversal-send-wrapper 'gym 'primary-school)

;;Benchmarking functions------------------------------------------------------------------------------------------------

(defn a*-traversal-bench[a b]
  (with-progress-reporting (bench (a*-traversal a b) :verbose)))

(defn breadth-traversal-bench[a b]
  (with-progress-reporting (bench (breadth-traversal a b) :verbose)))

(defn ops-traversal-bench[a b]
  (with-progress-reporting (bench (ops-search-traversal a b) :verbose)))

;;Some bench results----------------------------------------------------------------------------------------------------

(breadth-traversal-bench 'gym 'primary-school)

;Evaluation count : 717720 in 60 samples of 11962 calls.
;Execution time sample mean : 83.667916 µs
;Execution time mean : 83.676647 µs
;Execution time sample std-deviation : 1.781365 µs
;Execution time std-deviation : 1.809285 µs
;Execution time lower quantile : 82.532246 µs ( 2.5%)
;Execution time upper quantile : 89.190184 µs (97.5%)
;Overhead used : 1.271845 ns
;
;Found 8 outliers in 60 samples (13.3333 %)
;low-severe	 5 (8.3333 %)
;low-mild	 3 (5.0000 %)
;Variance from outliers : 9.4430 % Variance is slightly inflated by outliers

(a*-traversal-bench 'gym 'primary-school)
;Evaluation count : 728580 in 60 samples of 12143 calls.
;Execution time sample mean : 82.197156 µs
;Execution time mean : 82.198822 µs
;Execution time sample std-deviation : 151.793173 ns
;Execution time std-deviation : 156.290990 ns
;Execution time lower quantile : 81.940021 µs ( 2.5%)
;Execution time upper quantile : 82.521481 µs (97.5%)
;Overhead used : 1.271845 ns
;
;Found 3 outliers in 60 samples (5.0000 %)
;low-severe	 3 (5.0000 %)
;Variance from outliers : 1.6389 % Variance is slightly inflated by outliers

(ops-traversal-bench 'gym 'primary-school)
;Evaluation count : 2880 in 60 samples of 48 calls.
;Execution time sample mean : 21.057407 ms
;Execution time mean : 21.060160 ms
;Execution time sample std-deviation : 253.456608 µs
;Execution time std-deviation : 255.179251 µs
;Execution time lower quantile : 20.956250 ms ( 2.5%)
;Execution time upper quantile : 21.234292 ms (97.5%)
;Overhead used : 1.271845 ns
;
;Found 3 outliers in 60 samples (5.0000 %)
;low-severe	 1 (1.6667 %)
;low-mild	 2 (3.3333 %)
;Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
