/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Sampler {
    private List<Sample> samples = new LinkedList<Sample>();
    private String name;

    public Sampler(String name) {
        this.name = name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(long data) {
        List<Sample> list = this.samples;
        synchronized (list) {
            this.samples.add(new Sample(data));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Summary summarize() {
        Object[] tmp;
        List<Sample> list = this.samples;
        synchronized (list) {
            tmp = this.samples.toArray(new Sample[this.samples.size()]);
            this.samples.clear();
        }
        Summary summary = new Summary();
        summary.name = this.name;
        summary.samples = new Sample[tmp.length];
        if (tmp.length > 0) {
            System.arraycopy(tmp, 0, summary.samples, 0, tmp.length);
            Arrays.sort(tmp);
            summary.count = tmp.length;
            summary.min = ((Sample)tmp[0]).data;
            summary.max = ((Sample)tmp[tmp.length - 1]).data;
            summary.median = ((Sample)tmp[tmp.length / 2]).data;
            int count1Percentile = (int)Math.ceil((double)tmp.length / 100.0);
            int count5Percentile = (int)Math.ceil((double)tmp.length / 20.0);
            int pos1Percentile = tmp.length - count1Percentile;
            int pos5Percentile = tmp.length - count5Percentile;
            long sum = 0L;
            long sum1Percentile = 0L;
            long sum5Percentile = 0L;
            for (int i = 0; i < tmp.length; ++i) {
                sum += ((Sample)tmp[i]).data;
                if (i >= pos1Percentile) {
                    sum1Percentile += ((Sample)tmp[i]).data;
                }
                if (i < pos5Percentile) continue;
                sum5Percentile += ((Sample)tmp[i]).data;
            }
            summary.mean = (double)sum / (double)tmp.length;
            summary.mean1Percentile = (double)sum1Percentile / (double)count1Percentile;
            summary.mean5Percentile = (double)sum5Percentile / (double)count5Percentile;
            long spread = 0L;
            for (Object sample : tmp) {
                spread = (long)((double)spread + Math.pow((double)((Sample)sample).data - summary.mean, 2.0));
            }
            summary.standardDeviation = Math.sqrt((double)spread / (double)tmp.length);
        }
        return summary;
    }

    public static class Summary {
        public String name;
        public Sample[] samples;
        public int count;
        public long min;
        public long median;
        public long max;
        public double mean;
        public double mean1Percentile;
        public double mean5Percentile;
        public double standardDeviation;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Sample
    implements Comparable<Sample> {
        public long timestamp = System.currentTimeMillis();
        public long data;

        public Sample(long data) {
            this.data = data;
        }

        @Override
        public int compareTo(Sample sample) {
            return (int)(this.data - sample.data);
        }
    }
}

