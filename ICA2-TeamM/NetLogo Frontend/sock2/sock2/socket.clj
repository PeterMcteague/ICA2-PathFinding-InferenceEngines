
;====================================
; socket manufacture & control
;====================================


(import '(java.net ServerSocket Socket SocketException)
  '(java.io InputStreamReader OutputStreamWriter)
  '(clojure.lang LineNumberingPushbackReader))


;___ active socket is used as a global _____________

(def shrdlu-comms false)

(defn startup-server
  [port]
  (let [ss (new ServerSocket port)]
    (println "advertising " ss)
    (try (let [s (.accept ss)]
           (println "socket accepted " s)

           { :sock s
             :inp  (new LineNumberingPushbackReader
                     (new InputStreamReader (.getInputStream s)))
             :outp (new OutputStreamWriter (.getOutputStream s))
             }
           )
      (catch SocketException e))
    ))


(defn socket-write
  "low-level socket writer"
  [socket x]
  (binding [*out* (:outp socket)]
    (println x)
    ))


(defn socket-read
  "low-level socket reader"
  [socket]
  (read (:inp socket)))

(defn socket-input-waiting
  [socket]
  (.ready (:inp socket)))


;user=> (def s25 (startup-server 2222))
;advertising  #<ServerSocket ServerSocket[addr=0.0.0.0/0.0.0.0,port=0,localport=2225]>
;NB: Clj thread waits until socket is accepted by some other thread
;socket accepted  #<Socket Socket[addr=/152.105.17.36,port=55053,localport=2225]>
;#'user/s25
;user=> s25
;{:sock #<Socket Socket[addr=/152.105.17.36,port=55053,localport=2225]>, :inp #<LineNumberingPushbackReader clojure.lang.LineNumberingPushbackReader@176feac>, :outp #<OutputStreamWriter java.io.OutputStreamWriter@dc033a>}
;user=> (socket-read s25)
;test-message-1
;user=> (socket-read s25)
;test-message-2
;user=> (socket-write s25 "banana")
;nil

