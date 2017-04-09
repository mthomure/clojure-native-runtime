(ns native-runtime.examples.jnr-ffi-test
  (:import [java.nio Buffer ByteBuffer ByteOrder IntBuffer]
           [jnr.ffi Memory])
  (:require [midje.sweet :refer :all]
            [native-runtime.examples.jnr-ffi :refer :all]))

(fact
  (lib) => anything)

(fact
  (login) => string?)

(fact
  (pid) => (just {:pid integer?
                  :ppid integer?}))

(fact
  (timeofday) => (just {:tv-sec integer?
                        :tv-usec integer?}))

(facts "about qsort"

  (fact "using java int[] array"
    (let [numbers (int-array [2 1])]
      (.qsort (lib) numbers 2 4 (int-compare))
      (into [] numbers) => [1 2]))

  (fact "using native memory"
    (let [mem (Memory/allocate (runtime) 8)]
      (doto mem
        (.putInt 0 4)
        (.putInt 4 3))
      (.qsort (lib) mem 2 4 (int-compare))
      [(.getInt mem 0) (.getInt mem 4)] => [3 4]))

  (fact "using NIO buffer"
    (let [buf (-> (ByteBuffer/allocateDirect 8)
                  (.order (ByteOrder/nativeOrder))
                  (.asIntBuffer))]
      (doto buf
        (.put 0 6)
        (.put 1 5))  ;; offset is in units of int elements
      (.qsort (lib) buf 2 4 (int-compare))
      [(.get buf 0) (.get buf 1)] => [5 6])))
