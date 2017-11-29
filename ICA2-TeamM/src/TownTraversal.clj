(
  ns TownTraversal
  (load-file "src/Inference Engines/Astar-search.clj")
  (load-file "src/Inference Engines/planner.clj")
  (load-file "src/Inference Engines/breadth-search.clj")
  )

;;Planner

(def ops
  '{
    :name move
    :achieves (at ?agent ?y)
    :pre ((agent ?agent) (at ?agent ?x) (connected ?x ?y))
    :add ((at ?agent y))
    :del ((at ?agent x))
    :txt ((moved from ?x to ?y))
    :cmd [move-to ?y]
    }
  )

;; Need world states described to it

;; A-Star

;; Breadth first (Currently doesn't take into account cost , will see if possible)

(def town-map-keyless-costless
  '{
    primary-school
    {
     bakery
     }
    gregs-house
    {
     bakery
     }
    bakery
    {
     gregs-house
     butchers
     flower-shop
     primary-school
     }
    flower-shop
    {
     bakery
     chinese-takeaway
     hospital
     }
    hospital
    {
     flower-shop
     }
    butchers
    {
     bakery
     chinese-takeaway
     town-centre
     }
    chinese-takeaway
    {
     flower-shop
     butchers
     the-club
     }
    secondary-school
    {
     town-centre
     supermarket
     }
    town-centre
    {
     butchers
     burger-town
     fire-station
     secondary-school
     supermarket
     }
    the-club
    {
     chinese-takeaway
     train-station
     police-station
     }
    supermarket
    {
     secondary-school
     town-centre
     dock
     }
    burger-town
    {
     town-centre
     gym
     }
    dock
    {
     supermarket
     church
     }
    church
    {
     dock
     fire-station
     }
    fire-station
    {
     church
     gym
     }
    gym
    {
     fire-station
     burger-town
     police-station
     }
    police-station
    {
     gym
     the-club
     }
    })

(breadth-search 'police-station 'gym town-map-keyless-costless)
