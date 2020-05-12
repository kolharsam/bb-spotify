#!/usr/bin/env bb

(ns bb-spotify.core
  (:require [clojure.string :as str]
            [clojure.java.shell :as shell]
            [clojure.tools.cli :as tools.cli]))

(def cli-options
  [["play" "--play"]
   ["pause" "--pause"]
   ["current" "--current" "-c"]
   ["next" "--next" "-n"]
   ["prev" "--previous" "-l"]  ;; l for last can't really use p here I guess
   ["repeat" "--repeat" "-r"]
   ["shuffle" "--shuffle" "-s"]
   ["vol-up" "--volume-up" "-vu"]
   ["vol-dn" "--volume-down" "-vd"]
   ["status" "--status" "-st"]
   ["help" "--help" "-h"]])

(defn usage-help []
  (->> ["The following options are currently available, if you have the Spotify app installed."
        ""
        "  play      - play current track  "
        "  pause     - pause current track  "
        "  current   - shows some information about current song  "
        "  next      - goes to next song in the playlist  "
        "  prev      - goes to previous song in the playlist  "
        "  repeat    - plays current track on repeat "
        "  shuffle   - used to toggle shuffle on the playlist  "
        "  vol-up    - increses volume  "
        "  vol-dn    - decreses volume  "
        "  status    - current status of spotify player  "
        "  help      - displays this information  "
        ""
        "  More updates will coming soon, but for now you can control the Spotify App "
        "  from the command line."
        ""]
       (str/join \newline)))

;; (usage-help)

;; osascripts are secret of making this work on you your machine. Will add the web api soon

;; check if we can use the search api without any auth

;; (shell/sh "osascript" "-e" "tell application \"Spotify\" to play next track")

(defn is-spotify-installed? []
  ;; make this a future - deref at main or any other command that might need it.
  ;; show error - point to the download link.
  (str/includes? (:out (shell/sh "mdfind" "kMDItemKind == 'Application'")) "Spotify"))

(defn -main [& args]
  (tools.cli/parse-opts args cli-options))
