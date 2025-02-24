package org.infinispan;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = {
        "-Xmx10G",
        "-Xms10G",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-Xss512k",
})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FixedBitsBenchmark {

    // protobuf encoding is little endian
    public static final VarHandle LONG = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    public static final VarHandle INT = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);

    @Benchmark
    public int testFixed64Bits_incr(FixedBitsSetup setup) {
        int offset = 0;
        long value = setup.value;
        setup.writeArray[offset++] = (byte) (value & 0xFF);
        setup.writeArray[offset++] = (byte) ((value >> 8) & 0xFF);
        setup.writeArray[offset++] = (byte) ((value >> 16) & 0xFF);
        setup.writeArray[offset++] = (byte) ((value >> 24) & 0xFF);
        setup.writeArray[offset++] = (byte) ((int) (value >> 32) & 0xFF);
        setup.writeArray[offset++] = (byte) ((int) (value >> 40) & 0xFF);
        setup.writeArray[offset++] = (byte) ((int) (value >> 48) & 0xFF);
        setup.writeArray[offset++] = (byte) ((int) (value >> 56) & 0xFF);
        return offset;
    }

    @Benchmark
    public int testFixed64Bits_static(FixedBitsSetup setup) {
        int offset = 0;
        long value = setup.value;
        setup.writeArray[offset] = (byte) (value);
        setup.writeArray[offset + 1] = (byte) (value >> 8);
        setup.writeArray[offset + 2] = (byte) (value >> 16);
        setup.writeArray[offset + 3] = (byte) (value >> 24);
        setup.writeArray[offset + 4] = (byte) (value >> 32);
        setup.writeArray[offset + 5] = (byte) (value >> 40);
        setup.writeArray[offset + 6] = (byte) (value >> 48);
        setup.writeArray[offset + 7] = (byte) (value >> 56);
        return offset + 8;
    }

    @Benchmark
    public int testFixed64Bits_varhandle(FixedBitsSetup setup) {
        var offset = 0;
        LONG.set(setup.writeArray, offset, setup.value);
        return offset + 8;
    }

    @Benchmark
    public int testFixed32Bits_incr(FixedBitsSetup setup) {
        int offset = 0;
        int value = (int) setup.value;
        setup.writeArray[offset++] = (byte) (value);
        setup.writeArray[offset++] = (byte) (value >> 8);
        setup.writeArray[offset++] = (byte) (value >> 16);
        setup.writeArray[offset++] = (byte) (value >> 24);
        return offset;
    }

    @Benchmark
    public int testFixed32Bits_static(FixedBitsSetup setup) {
        int offset = 0;
        int value = (int) setup.value;
        setup.writeArray[offset] = (byte) (value);
        setup.writeArray[offset + 1] = (byte) (value >> 8);
        setup.writeArray[offset + 2] = (byte) (value >> 16);
        setup.writeArray[offset + 3] = (byte) (value >> 24);
        return offset + 8;
    }

    @Benchmark
    public int testFixed32Bits_varhandle(FixedBitsSetup setup) {
        var offset = 0;
        INT.set(setup.writeArray, 0, (int) setup.value);
        return offset + 4;
    }

    @Benchmark
    public int readFixed64Bits_static(FixedBitsSetup setup, Blackhole blackhole) {
        var array = setup.readArray;
        var offset = 0;
        long value = (array[offset] & 0xFFL)
                | ((array[offset + 1] & 0xFFL) << 8)
                | ((array[offset + 2] & 0xFFL) << 16)
                | ((array[offset + 3] & 0xFFL) << 24)
                | ((array[offset + 4] & 0xFFL) << 32)
                | ((array[offset + 5] & 0xFFL) << 40)
                | ((array[offset + 6] & 0xFFL) << 48)
                | ((array[offset + 7] & 0xFFL) << 56);
        blackhole.consume(value);
        return offset + 8;
    }

    @Benchmark
    public int readFixed64Bits_varhandle(FixedBitsSetup setup, Blackhole blackhole) {
        var offset = 0;
        long value = (long) LONG.get(setup.readArray, offset);
        blackhole.consume(value);
        return offset + 8;
    }

    @Benchmark
    public int readFixed32Bits_static(FixedBitsSetup setup, Blackhole blackhole) {
        var array = setup.readArray;
        var offset = 0;
        int value = (array[offset] & 0xFF)
                | ((array[offset + 1] & 0xFF) << 8)
                | ((array[offset + 2] & 0xFF) << 16)
                | ((array[offset + 3] & 0xFF) << 24);
        blackhole.consume(value);
        return offset + 4;
    }

    @Benchmark
    public int readFixed32Bits_varhandle(FixedBitsSetup setup, Blackhole blackhole) {
        var offset = 0;
        int value = (int) INT.get(setup.readArray, offset);
        blackhole.consume(value);
        return offset + 4;
    }

}
