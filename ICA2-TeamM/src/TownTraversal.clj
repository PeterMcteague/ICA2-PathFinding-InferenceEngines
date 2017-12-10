(
  ns TownTraversal
  (:gen-class)
  (:require [org.clojars.cognesence.matcher.core :refer :all])
  (:require [org.clojars.cognesence.ops-search.core :refer :all])
  (load-file "src/Inference Engines/Astar-search.clj")
  (load-file "src/Inference Engines/breadth-search.clj")
  (load-file "src/Tools/socket.clj")
  )

(use 'criterium.core)

;; A-Star---------------------------------------------------------------------------------------------------------------

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

(defn a*-traversal [a b]
  (A*search {:state (str a), :cost 0} (str b) a*lmg))

;; Breadth first , no cost ---------------------------------------------------------------------------------------------

(def breadth-state
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

(defn breadth-traversal [a b]
  (breadth-search a b breadth-state))

;;ops-search------------------------------------------------------------------------------------------------------------

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

(defn ops-search-traversal [start end]
  (ops-search (list '(agent R) (list 'at 'R start)) (list (list 'at 'R end)) ops :world ops-world-state))


;;Functions with socket writing-----------------------------------------------------------------------------------------
(def s25 (startup-server 2222)) ;;socket initialization

(defn a*-traversal-send [start end list]
  (if (empty? list)
    (list)
    (do
      (println list)
      (println (get (first list) :state))
      (println "Sending" (str "move-to " (get (first list) :state)))
      (println)
      (Thread/sleep 1000)
      (socket-write s25 (str "move-to " (get (first list) :state)))
      (a*-traversal-send start end (rest list)))
    ))

(defn breadth-traversal-send [start end list]
  (if (empty? list)
    (list)
    (do
      (println list)
      (println (get (first list) :state))
      (println "Sending" (str "move-to " (get (first list) :state)))
      (println)
      (Thread/sleep 5000)
      (socket-write s25 (str "move-to " (get (first list) :state)))
      (breadth-traversal-send start end (rest list)))
    ))

(defn ops-search-traversal-send [start end text]
  (if (empty? list)
    (conj start text end)
    (do
      (println list)
      (println (get (first list) :state))
      (println "Sending" (str "move-to " (get (first list) :state)))
      (println)
      (Thread/sleep 5000)
      (socket-write s25 (str "move-to " (get (first list) :state)))
      (ops-search-traversal-send start end (rest text)))
    ))

(defn ops-search-traversal-send-wrapper [start end]
  (ops-search-traversal-send start end (get (ops-search-traversal start end) :txt))
  )

(defn breadth-traversal-send-wrapper [start end]
  (breadth-traversal-send start end (breadth-traversal start end))
  )

(defn a*-traversal-send-wrapper [start end]
  (a*-traversal-send start end (a*-traversal start end))
  )

(a*-traversal-send-wrapper 'gym 'primary-school)

;;Benchmarking functions------------------------------------------------------------------------------------------------
(defn a*-traversal-bench[a b]
  (with-progress-reporting (bench (a*-traversal a b) :verbose)))

(defn breadth-traversal-bench[a b]
  (with-progress-reporting (bench (breadth-traversal a b) :verbose)))

;;Testing--------------------------------------------------------------------------------------------------------------

(a*-traversal 'gym 'primary-school)
(breadth-traversal 'gym 'primary-school)
(ops-search-traversal 'gym 'secondary-school)