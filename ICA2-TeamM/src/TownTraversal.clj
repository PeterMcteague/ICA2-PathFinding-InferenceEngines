(
  ns TownTraversal
  (load-file "src/Inference Engines/Astar-search.clj")
  (load-file "src/Inference Engines/planner.clj")
  (load-file "src/Inference Engines/breadth-search.clj")
  )

;;A map of town nodes to locations they're connected to and the costs to travel those connections
(def town-map-keyd
  '{
    :primary-school
    {
     :bakery {:cost 10}
     }
    :gregs-house
    {
     :bakery {:cost 5}
     }
    :bakery
    {
     :gregs-house {:cost 5}
     :butchers {:cost 1}
     :flower-shop {:cost 6}
     :primary-school {:cost 10}
     }
    :flower-shop
    {
     :bakery {:cost 6}
     :chinese-takeaway {:cost 8}
     :hospital {:cost 10}
     }
    :hospital
    {
     :flower-shop {:cost 10}
     }
    :butchers
    {
     :bakery {:cost 1}
     :chinese-takeaway {:cost 9}
     :town-centre {:cost 5}
     }
    :chinese-takeaway
    {
     :flower-shop {:cost 8}
     :butchers {:cost 9}
     :the-club {:cost 6}
     }
    :secondary-school
    {
     :town-centre {:cost 1}
     :supermarket {:cost 9}
     }
    :town-centre
    {
     :butchers {:cost 5}
     :burger-town {:cost 10}
     :fire-station {:cost 3}
     :secondary-school {:cost 1}
     :supermarket {:cost 8}
     }
    :the-club
    {
     :chinese-takeaway {:cost 6}
     :train-station {:cost 8}
     :police-station {:cost 2}
     }
    :supermarket
    {
     :secondary-school {:cost 9}
     :town-centre {:cost 8}
     :dock {:cost 8}
     }
    :burger-town
    {
     :town-centre {:cost 10}
     :gym {:cost 9}
     }
    :dock
    {
     :supermarket {:cost 8}
     :church {:cost 1}
     }
    :church
    {
     :dock {:cost 1}
     :fire-station {:cost 7}
     }
    :fire-station
    {
     :church {:cost 7}
     :gym {:cost 9}
     }
    :gym
    {
     :fire-station {:cost 9}
     :burger-town {:cost 9}
     :police-station {:cost 2}
     }
    :police-station
    {
     :gym {:cost 2}
     :the-club {:cost 2}
     }
    })

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

;;A-Star

;;Planner

;;Breadth first
