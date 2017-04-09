(ns native-runtime.examples.lapack
  (:import [jnr.ffi LibraryLoader]
           [jnr.ffi.byref IntByReference]))

(definterface LibLapack
  (^void cblas_dscal [^int n ^double a ^doubles x ^int incx])
  (^void dgesdd
   [^String jobz
    ^jnr.ffi.byref.IntByReference m
    ^jnr.ffi.byref.IntByReference n
    ^doubles a
    ^jnr.ffi.byref.IntByReference lda
    ^doubles s
    ^doubles u
    ^jnr.ffi.byref.IntByReference ldu
    ^doubles vt
    ^jnr.ffi.byref.IntByReference ldvt
    ^doubles work
    ^jnr.ffi.byref.IntByReference lwork
    ^ints iwork
    ^jnr.ffi.byref.IntByReference info]))

(def lib (memoize (fn [] (.load (LibraryLoader/create LibLapack) "LAPACK"))))
(def runtime (memoize (fn [] (jnr.ffi.Runtime/getRuntime (lib)))))

;; See also: https://software.intel.com/en-us/node/520743#54F97254-4ED4-46CE-90F9-AA3C745AD840
(defn dscal [x a]
  (.cblas_dscal (lib) (count x) a x 1))

;; See also: http://www.netlib.org/lapack/explore-html/d1/d7e/group__double_g_esing_ga76f797b6a9e278ad7b21aae2b4a55d76.html#ga76f797b6a9e278ad7b21aae2b4a55d76
(defn dgesdd! [jobz m n a lda s u ldu vt ldvt work lwork iwork]
  (let [info (IntByReference.)]
    (.dgesdd (lib)
             jobz
             (IntByReference. m)
             (IntByReference. n)
             a
             (IntByReference. m)
             s
             u
             (IntByReference. ldu)
             vt
             (IntByReference. ldvt)
             work
             (IntByReference. lwork)
             iwork
             info)
    (when (not (zero? (.intValue info)))
      (throw (RuntimeException. "Error calling dgesdd")))))

(defn query-dgesdd [jobz m n a lda s u ldu vt ldvt iwork]
  (let [work (double-array 1)
        lwork -1]
    (dgesdd! jobz m n a lda s u ldu vt ldvt work lwork iwork)
    (int (get work 0))))
