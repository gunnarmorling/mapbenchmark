/*
 * Copyright Gunnar Morling.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package dev.morling;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.minperf.BitBuffer;
import org.minperf.RecSplitBuilder;
import org.minperf.RecSplitEvaluator;
import org.minperf.universal.StringHash;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

public class MyBenchmark {

    @State(Scope.Thread)
    public static class MyState {

        @Param({"Key-10" }) // "Sarah", "Alice"})
            //, "Bruce", "Devon", "Gary", "Darran", "Max", "Jim", "Terrance"})
        public String name;

        @Param("100")
        public int size;


        public Map<String, String> valuesMap;
        public Map<String, String> valuesImmutableMap;
        public String[] valuesArray;

        public RecSplitEvaluator<String> eval;

        @Setup
        public void setup() {

            Set<String> keys = new LinkedHashSet<>();
            valuesMap = new HashMap();

//            Map.

            for(int i = 1; i <= size; i++) {
                valuesMap.put("Key-" + i,  "Value-" + i);
//                valuesImmutableMap.put("Key-" + i,  "Value-" + i);
                keys.add("Key-" + i);
            }

            valuesArray = new String[size];
            byte[] desc = RecSplitBuilder.newInstance(new StringHash())
                    .leafSize(8)
                    .averageBucketSize(14)
                    .generate(keys).toByteArray();

            eval = RecSplitBuilder.newInstance(new StringHash())
                    .leafSize(8)
                    .averageBucketSize(14)
                    .buildEvaluator(new BitBuffer(desc));

            for(int i = 1; i <= size; i++) {
                int index = eval.evaluate("Key-" + i);
                valuesArray[index] = "Value-" + i;
            }
        }

        public long get(String name) {
            switch(name) {
                case "Bob": return 1L;
                case "Sarah": return 2L;
                case "Alice": return 3L;
                case "Bruce": return 4L;
                case "Devon": return 5L;
                case "Gary": return 6L;
                case "Darran": return 7L;
                case "Max": return 8L;
                case "Jim": return 9L;
                case "Terrance": return 10L;
                default: return -1;
            }
        }
    }

//    @Benchmark
////    @OperationsPerInvocation(10)
//    public void testImmutableMap(MyState state, Blackhole blackhole) {
//        blackhole.consume(state.valuesImmutableMap.get(state.name));
//    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testMap(MyState state, Blackhole blackhole) {
        blackhole.consume(state.valuesMap.get(state.name));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testPerfectMinimalHash(MyState state, Blackhole blackhole) {
        blackhole.consume(state.eval.evaluate(state.name));
    }

//    @Benchmark
//    public void testSwitch(MyState state, Blackhole blackhole) {
//        blackhole.consume(state.get(state.name));
//    }
}
