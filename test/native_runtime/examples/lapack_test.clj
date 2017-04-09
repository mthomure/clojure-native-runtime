(ns native-runtime.examples.lapack-test
  (:require [midje.sweet :refer :all]
            [native-runtime.examples.lapack :refer :all]))

(fact "about dscal"
  (let [x (double-array [0 1 2])]
    (dscal x 10.0)
    (into [] x) => [0.0 10.0 20.0]))

;; See also: https://software.intel.com/sites/products/documentation/doclib/mkl_sa/11/mkl_lapack_examples/dgesdd_ex.c.htm

(defn arr->mat [buf nrows ncols]
  (when (not= (* nrows ncols) (count buf))
    (throw (ex-info "buffer size mismatch"
                    {:buf-size (count buf) :nrows nrows :ncols ncols})))
  (partition ncols buf))

(defn transpose [mat]
  (apply map list mat))

(defn roughly-arr [expected delta]
  (apply just (map #(roughly % delta) expected)))

(defn roughly-mat [expected delta]
  (apply just (map #(roughly-arr % delta) expected)))

(facts "about dgesdd"
  (let [jobz "S"
        m 6
        n 4
        lda 6
        a (double-array
           [ 7.52, -0.76,  5.13, -4.75,  1.33, -2.40,
            -1.10,  0.62,  6.62,  8.52,  4.91, -6.77,
            -7.95,  9.34, -5.66,  5.75, -5.49,  2.34,
            1.08, -7.10,  0.87,  5.30, -3.52,  3.95])
        s (double-array 4)
        ldu 6
        u (double-array (* ldu n))
        ldvt 4
        vt (double-array (* ldvt n))
        iwork (int-array 16)
        lwork (query-dgesdd jobz m n a lda s u ldu vt ldvt iwork)
        work (double-array lwork)]
    (dgesdd! jobz m n a lda s u ldu vt ldvt work lwork iwork)
    ;; Singular values
    (vec s) => (roughly-arr [18.37 13.63 10.85 4.49] 1e-2)
    ;; Left singular vectors
    (transpose (arr->mat u 4 6)) => (roughly-mat
                                     [[-0.57 0.18 0.01 0.53]
                                      [0.46 -0.11 -0.72 0.42]
                                      [-0.45 -0.41 0.00 0.36]
                                      [0.33 -0.69 0.49 0.19]
                                      [-0.32 -0.31 -0.28 -0.61]
                                      [0.21 0.46 0.39 0.09]]
                                     1e-2)
    ;; Right singular vectors
    (transpose (arr->mat vt 4 4)) => (roughly-mat
                                      [[-0.52 -0.12 0.85 -0.03]
                                       [0.08 -0.99 -0.09 -0.01]
                                       [-0.28 -0.02 -0.14 0.95]
                                       [0.81 0.01 0.50 0.31]]
                                      1e-2)))
