package org.infinispan;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@State(Scope.Thread)
public class FixedBitsSetup {

    @Param("123456789000000011")
    long value;

    public final byte[] writeArray = new byte[8];
    public final byte[] readArray = new byte[8];

    @Setup
    public void setup() {
        var os = new ByteArrayOutputStream(8);
        try (var dos = new DataOutputStream(os)) {
            dos.writeLong(value);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.arraycopy(os.toByteArray(), 0, readArray, 0, 8);
    }

}
