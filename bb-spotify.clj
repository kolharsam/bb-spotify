#!/usr/bin/env bb

(ns bb-spotify.core
  (:require [clojure.string :as str]
            [clojure.java.shell :as shell]
            [clojure.tools.cli :as tools.cli]))

;; TODO: Make cl-options more friendlier

(def cl-options
  [["-p" "--play"  "play"]
   ["-s" "--pause" "pause"]    ;; s for stop
   ["-c" "--current" "current"]
   ["-n" "--next" "next"]
   ["-l" "--prev" "previous"]  ;; l for last
   ["-r" "--repeat" "repeat"]
   ["-z" "--shuffle" "shuffle"]
   ["-u" "--volume-up" "volume up"]
   ["-d" "--volume-down" "volume down"]
   ["-t" "--status" "status"]
   ["-o" "--open" "open"]
   ["-x" "--close" "close"]
   ["-h" "--share" "share"]
   ["-v" "--sp-help" "bb-spotify-help"]])

(def usage-help
  (->> ["The following options are currently available, if you have the Spotify app installed."
        ""
        "  play    (-p, --play)             - play current track  "
        "  pause   (-s, --pause)            - pause current track  "
        "  current (-c, --current)          - shows some information about current song  "
        "  next    (-n, --next)             - goes to next song in the playlist  "
        "  prev    (-l, --last)             - goes to previous song in the playlist  "
        "  repeat  (-r, --repeat)           - plays current track on repeat "
        "  shuffle (-z, --shuffle)          - used to toggle shuffle on the playlist  "
        "  vol-up  (-u, --volume-up)        - increses volume  "
        "  vol-dn  (-d, --volume-down)      - decreses volume  "
        "  status  (-t, --status)           - current status of spotify player  "
        "  bb-spotify-help (-v, --sp-help)  - displays this information  "
        "  open    (-o, --open)             - opens the app "
        "  close   (-x, --close)            - closes the app"
        "  share   (-h, --share)            - copies link to current song to clipboard   "
        ""
        "  More updates will coming soon, but for now you    "
        "  can control the Spotify App from the command line."
        ""]
       (str/join \newline)))

;; TODO: check if we can use the search api without any auth token

;; Shell execution returns : {:exit 0, :out "", :err ""}

;; FIX: current

;; TODO: vol-up, vol-down

;; To try: (shell/sh "osascript" "-e" "tell application \"Spotify\" to shuffling")

(def get-cl-options
  (:options (tools.cli/parse-opts *command-line-args* cl-options)))

(def is-spotify-installed?
  (str/includes? (:out (shell/sh "mdfind" "kMDItemKind == 'Application'")) "Spotify"))

(def show-download-link
  (->> ["Spotify app isn't there on your system, make sure you have it installed!"
        ""
        "You can download the app from here: https://www.spotify.com/in/download/other/"]
       (str/join \newline)))

(defn exec-script
  [doc body]
  (println doc)
  (let [script-future (future (shell/sh "osascript" "-e" (str "tell application \"Spotify\" " body)))
        future-status @script-future
        err-status (:err future-status)
        out-status (:out future-status)]
    (if (not= "" err-status)
      (println "There was an error opening the app" err-status)
      (when (not= "" out-status)
        out-status))))

(defn open-app []
  (exec-script "Opening app..." "to activate"))

(defn close-app []
  (exec-script "Quitting Spotify!" "to quit"))

(defn play []
  (exec-script "Playing current song in queue!" "to play"))

(defn pause []
  (exec-script "Pausing current song" "to pause"))

(defn next-track []
  (exec-script "Playing next track!" "to play next track"))

(defn prev-track []
  (exec-script "Playing previous track!" "\nset player position to 0\n previous track\n end tell"))

;; This doesn't work
;; (defn current-track-info []
  ;; (println "Track Information: ")
  ;; (let [artist-info (exec-script "" "to artist of current track")
        ;; album-info (exec-script "" "to album of current track")
        ;; track-info (exec-script "" "to name of current track")]
;; (println artist-info album-info track-info)))

(defn repeat-track []
  (exec-script "Replaying the current track!" "set player position to 0"))

(defn shuffle-playlist []
  (let [current-shuffle-state (exec-script "" "to shuffling")]
    (if (= "true\n" current-shuffle-state)
      (println "Turning off shuffle!")
      (println "Turning on shuffle!"))
    (exec-script "" "to set shuffling to not shuffling")))

(defn player-status []
  (let [player-state (exec-script "" "to player state")
        player-state-str (nth (str/split player-state #"\n") 0)]
    (println "Your player is:" player-state-str)))

(defn share-link []
  (let [spotify-uri (exec-script "Getting link..." "to spotify url of current track")
        uri-split (str/split spotify-uri #"spotify:track:")
        url-gen (str/join "https://open.spotify.com/track/" uri-split)]
    (print url-gen)))

(defn main [options]
  (if (not is-spotify-installed?)
    (println show-download-link)
    (let [par-contains (partial contains? options)]
      (cond
        (par-contains :sp-help) (println usage-help)
        (par-contains :open) (open-app)
        (par-contains :close) (close-app)
        (par-contains :play) (play)
        (par-contains :pause) (pause)
        (par-contains :next) (next-track)
        (par-contains :prev) (prev-track)
        ;; (par-contains :current) (current-track-info)
        (par-contains :repeat) (repeat-track)
        (par-contains :shuffle) (shuffle-playlist)
        (par-contains :status) (player-status)
        (par-contains :share) (share-link)
        ;; insert more cases here
        :else
        (print usage-help)))))

;; The main part that is executed
(main get-cl-options)
