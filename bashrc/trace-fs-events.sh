traceFsEvents() {
  local logfile=$(mktemp)
  strace -ff -e trace=open,openat,unlink,rename,creat -s 4096 -o "$logfile" "$@"

  grep -hE 'open|unlink|rename|creat' "$logfile"* |
  awk '
    /open|openat/ {
      if ($0 ~ /O_WRONLY|O_RDWR|O_CREAT|O_TRUNC|O_APPEND/) {
        if (match($0, /"[^"]+"/)) {
          file = substr($0, RSTART + 1, RLENGTH - 2)
          print "Open:", file
        }
      }
    }
    /creat/ {
      if ($0 ~ /O_WRONLY|O_RDWR|O_CREAT|O_TRUNC|O_APPEND/) {
        if (match($0, /"[^"]+"/)) {
          file = substr($0, RSTART + 1, RLENGTH - 2)
          print "Create:", file
        }
      }
    }
    /unlink/ {
      if (match($0, /"[^"]+"/)) {
        file = substr($0, RSTART + 1, RLENGTH - 2)
        print "Delete:", file
      }
    }
    /rename/ {
      if (match($0, /"[^"]+"/)) {
        start = RSTART + 1
        len = RLENGTH - 2
        first = substr($0, start, len)
        rest = substr($0, RSTART + RLENGTH)
        if (match(rest, /"[^"]+"/)) {
          second = substr(rest, RSTART + 1, RLENGTH - 2)
          print "Rename from:", first, "to:", second
        }
      }
    }
  ' | uniq
}

