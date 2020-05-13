# bb-spotify

Simple scripts to control your music from the command line

This can work on most MacOSX devices. Other platforms will
require the spotify API. Which will be updated soon.

These scripts can work for Apple Music too! Use `bb-apple-music.clj` instead.

## Instructions

 - Have the [Spotify](https://www.spotify.com/download/other/) app installed on your machine.

 - Have [bb](https://github.com/borkdude/babashka) installed on your machine.

## Commands available

```text

The following options are currently available, if you have the Spotify app installed.

  play    (-p, --play)             - play current track
  pause   (-s, --pause)            - pause current track
  * current (-c, --current)          - shows some information about current song
  next    (-n, --next)             - goes to next song in the playlist
  prev    (-l, --last)             - goes to previous song in the playlist
  repeat  (-r, --repeat)           - plays current track on repeat
  shuffle (-z, --shuffle)          - used to toggle shuffle on the playlist
  vol-up  (-u, --volume-up)        - increses volume by 10%
  vol-dn  (-d, --volume-down)      - decreses volume by 10%
  status  (-t, --status)           - current status of spotify player
  bb-spotify-help (-v, --sp-help)  - displays this information
  open    (-o, --open)             - opens the app
  close  (-x, --close)             - closes the app

```

*current doesn't work as intended for spotify. Check this [issue](https://github.com/kolharsam/bb-spotify/issues/1)

## Reference(s)

 - [shpotify](https://github.com/hnarayanan/shpotify)
