(ns ^:figwheel-always syncinar.core
    (:require [reagent.core :as reagent]
              [clojure.set :as set])
    (:import [goog.events KeyCodes]))

(enable-console-print!)

(defn navigate [step animal-idx label able? f]
  [:button (cond (and able? (not= "Done" @step)) {:on-click f}
                 (not= "Start!" @step) {:class "disabled"})
   label])

(defn key-match [k e]
  (= (aget KeyCodes k) (.-keyCode e)))

(def codename
  (set/map-invert (js->clj KeyCodes)))

(defn ex10 [animals]
  (let [animal-idx (reagent/atom 0)
        max-idx (dec (count animals))
        can-prev? #(pos? @animal-idx)
        nav-prev! #(swap! animal-idx dec)
        can-next? #(< @animal-idx max-idx)
        nav-next! #(swap! animal-idx inc)
        steps ["Start!" "Stop!" "Done"]
        step (reagent/atom "Start!")
        next-step (zipmap steps (rest steps))
        keydown (fn a-keydown-handler [e]
                  (when (= "Stop!" @step)
                    (case (codename (.-keyCode e))
                      "LEFT" (when (can-prev?) (nav-prev!))
                      "RIGHT" (when (can-next?) (nav-next!))
                      :unhandled)))]
    (reagent/create-class
     {:display-name "ex10"
      :component-did-mount
      (fn ex10-did-mount [this]
        (.addEventListener js/document "keydown" keydown))
      :component-will-unmount
      (fn ex10-will-unmount [this]
        (.removeEventListener js/document "keydown" keydown))
      :reagent-render
      (fn ex10-render [animals]
        [:div.example
         [:h2 "Example 10"]
         [:table
          [:tbody
           [:tr
            [:td.left
             [:button (if (= "Done" @step)
                        {:class "disabled"}
                        {:on-click #(swap! step next-step)})
              @step]
             \space
             [navigate step animal-idx "Previous" (can-prev?) nav-prev!]
             \space
             [navigate step animal-idx "Next" (can-next?) nav-next!]]
            [:td.display
             [:span (when (= "Stop!" @step)
                      (animals @animal-idx))]]]]]])})))

(defn main-page []
  [:div
   [:center
    [:h1 "Welcome to Syncinar"]
    [:img {:src "img/flying-spaghetti-monster-w.jpg"}]
    [:a {:href "github"}]]
   [:hr]
   [ex10 ["aardvark" "beetle" "cat" "dog" "elk" "ferret"
          "goose" "hippo" "ibis" "jellyfish" "kangaroo"]]])

(reagent/render-component main-page (. js/document (getElementById "app")))
