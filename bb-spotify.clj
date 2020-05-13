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
        "  close  (-x, --close)             - closes the app"
        ""
        "  More updates will coming soon, but for now you    "
        "  can control the Spotify App from the command line."
        ""]
       (str/join \newline)))

;; TODO: check if we can use the search api without any auth token

;; Shell execution returns : {:exit 0, :out , :err }

;; TODO: current, repeat, shuffle, vol-up, vol-down, status

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
        err-status (:err future-status)]
    (when (not= "" err-status)
      (println "There was an error opening the app" err-status))))

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
        ;; insert more cases here
        :else
        (print usage-help)))))

;; The main part that is executed
(main get-cl-options)
