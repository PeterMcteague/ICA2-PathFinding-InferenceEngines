(
  ns TownTraversal
  (:gen-class)
  (load-file "src/Inference Engines/Astar-search.clj")
  (load-file "src/Inference Engines/breadth-search.clj")
  (load-file "src/Tools/socket.clj")
  (use 'criterium.core)
  )

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

(a*-traversal 'gym 'primary-school)

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

(breadth-traversal 'gym 'primary-school)

;;Socket startup--------------------------------------------------------------------------------------------------------

(def s25 (startup-server 2222))

;;Test functions--------------------------------------------------------------------------------------------------------
(defn a*-traversal-send [a b]
  ((socket-write (map #(get % :state) (a*-traversal a b)))))

(defn breadth-traversal-send [a b]
  ((socket-write (breadth-traversal a b))))

;;Benchmarking functions------------------------------------------------------------------------------------------------
(a*-traversal-send 'gym 'primary-school)
(bench (map #(get % :state) (a*-traversal 'gym 'primary-school)))