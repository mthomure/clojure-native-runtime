package native_runtime.examples;

import jnr.ffi.Struct;

// Note: Must be implemented in Java, since Clojure's gen-class does not support
// member fields.
public class Timeval extends Struct {
    public final time_t tv_sec = new time_t();
    public final SignedLong tv_usec = new SignedLong();

    public Timeval(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
}
