#!/usr/bin/env bb

(ns bb-apple-music.core
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
   ["-v" "--music-help" "bb-music-help"]])

(def usage-help
  (->> ["The following options are currently available, if you have the Music app installed."
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
        "  bb-music-help (-v, --music-help) - displays this information  "
        "  open    (-o, --open)             - opens the app "
        "  close   (-x, --close)            - closes the app"
        ""
        "  More updates will coming soon, but for now you    "
        "  can control the Music App from the command line."
        ""]
       (str/join \newline)))

;; TODO: vol-up, vol-down

;; To try: (shell/sh "osascript" "-e" "tell application \"Spotify\" to shuffling")

(def get-cl-options
  (:options (tools.cli/parse-opts *command-line-args* cl-options)))

(def is-music-installed?
  (str/includes? (:out (shell/sh "mdfind" "kMDItemKind == 'Application'")) "Music"))

(def show-download-link
  (->> ["Music app isn't there on your system, make sure you have it installed!"]
       (str/join \newline)))

(defn exec-script
  [doc body]
  (println doc)
  (let [script-future (future (shell/sh "osascript" "-e" (str "tell application \"Music\" " body)))
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
  (exec-script "Quitting Music!" "to quit"))

(defn play []
  (exec-script "Playing current song in queue!" "to play"))

(defn pause []
  (exec-script "Pausing current song" "to pause"))

(defn next-track []
  (exec-script "Playing next track!" "to play next track"))

(defn prev-track []
  (exec-script "Playing previous track!" "\nset player position to 0\n previous track\n end tell"))

(defn current-track-info []
  (let [artist-info (exec-script "Getting Artist Info..." "to artist of current track")
        album-info (exec-script "Getting Album Info..." "to album of current track")
        track-info (exec-script "Getting Song Name..." "to name of current track")]
    (println (->> [""
                   (str "Song : " track-info)
                   (str "Artist : " artist-info)
                   (str "Album : " album-info)]
                  (str/join \newline)))))

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
    (println "You're player is:" player-state-str)))

(defn main [options]
  (if (not is-music-installed?)
    (println show-download-link)
    (let [par-contains (partial contains? options)]
      (cond
        (par-contains :music-help) (println usage-help)
        (par-contains :open) (open-app)
        (par-contains :close) (close-app)
        (par-contains :play) (play)
        (par-contains :pause) (pause)
        (par-contains :next) (next-track)
        (par-contains :prev) (prev-track)
        (par-contains :current) (current-track-info)
        (par-contains :repeat) (repeat-track)
        (par-contains :shuffle) (shuffle-playlist)
        (par-contains :status) (player-status)
        ;; insert more cases here
        :else
        (print usage-help)))))

;; The main part that is executed
(main get-cl-options)
