(ns native-runtime.examples.jnr-ffi
  (:import [jnr.ffi LibraryLoader Memory]
           [jnr.ffi.annotations Out Transient]
           [native_runtime.examples Timeval]))

;; See also https://github.com/jnr/jnr-ffi-examples/
;; Note: type annotations (and decorators) must be fully qualified.

(definterface Compare
  (^{:tag int
     jnr.ffi.annotations.Delegate true}
   compare [^jnr.ffi.Pointer p1 ^jnr.ffi.Pointer p2]))

(definterface LibC
  ;; getlogin
  (^String getlogin [])
  ;; getpid
  (^{:tag long pid_t true}
   getpid [])
  (^{:tag long pid_t true}
   getppid [])
  ;; gettimeofday
  (^int gettimeofday
   [^{:tag native_runtime.examples.Timeval Out true Transient true} tv
    ^jnr.ffi.Pointer unused])
  ;; qsort
  (^int qsort [^ints data ^int count ^int width
               ^native_runtime.examples.jnr_ffi.Compare compare])
  (^int qsort [^jnr.ffi.Pointer data ^int count ^int width
               ^native_runtime.examples.jnr_ffi.Compare compare])
  (^int qsort [^java.nio.Buffer data ^int count ^int width
               ^native_runtime.examples.jnr_ffi.Compare compare]))

(def lib (memoize (fn [] (.load (LibraryLoader/create LibC) "c"))))
(def runtime (memoize (fn [] (jnr.ffi.Runtime/getRuntime (lib)))))

(defn login []
  (.getlogin (lib)))

(defn pid []
  {:pid (.getpid (lib))
   :ppid (.getppid (lib))})

(defn timeofday []
  (let [tv (Timeval. (runtime))]
    (.gettimeofday (lib) tv nil)
    {:tv-sec (.get (.tv_sec tv))
     :tv-usec (.get (.tv_usec tv))}))

(deftype IntCompare []
  native_runtime.examples.jnr_ffi.Compare
  (compare [this p1 p2]
    (let [i1 (.getInt p1 0)
          i2 (.getInt p2 0)]
      (cond
        (< i1 i2) -1
        (> i1 i2) 1
        :else 0))))

(defn int-compare []
  (IntCompare.))
