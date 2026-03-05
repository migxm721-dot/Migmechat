/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common.metrics;

public class MetricsEnums {

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MetricType {
        BOOLEAN,
        STRING,
        INTEGER,
        DOUBLE;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum FusionRestMetrics implements MetricsEntryInterface
    {
        MIGBO_NEW_POST_APPLICATION(new StringMetricsEntry(Scope.FUSIONREST, "NewPost", "Application")),
        MIGBO_NEW_POST_TYPE(new StringMetricsEntry(Scope.FUSIONREST, "NewPost", "Type")),
        MIGBO_NEW_POST_ORIGINALITY(new StringMetricsEntry(Scope.FUSIONREST, "NewPost", "Originality")),
        TEST_PING(new StringMetricsEntry(Scope.FUSIONREST, "TestPing", "TestPing"));

        private MetricsEntry value;

        private FusionRestMetrics(MetricsEntry value) {
            this.value = value;
        }

        @Override
        public String getEventName() {
            return this.value.eventName;
        }

        @Override
        public String getMetricName() {
            return this.value.metricName;
        }

        @Override
        public MetricType getMetricType() {
            return this.value.metricType;
        }

        @Override
        public Scope getScope() {
            return this.value.getScope();
        }

        @Override
        public MetricsEntry getEntry() {
            return this.value;
        }

        @Override
        public String getSimpleName() {
            return (Object)((Object)this.value.scope) + ":" + this.value.eventName + ":" + this.value.metricName;
        }
    }

    public static interface MetricsEntryInterface {
        public String getEventName();

        public String getMetricName();

        public MetricType getMetricType();

        public Scope getScope();

        public MetricsEntry getEntry();

        public String getSimpleName();
    }

    public static class DoubleMetricsEntry
    extends MetricsEntry {
        DoubleMetricsEntry(Scope scope, String eventName, String metricName) {
            super(scope, eventName, metricName, MetricType.DOUBLE);
        }
    }

    public static class IntegerMetricsEntry
    extends MetricsEntry {
        IntegerMetricsEntry(Scope scope, String eventName, String metricName) {
            super(scope, eventName, metricName, MetricType.INTEGER);
        }
    }

    public static class StringMetricsEntry
    extends MetricsEntry {
        StringMetricsEntry(Scope scope, String eventName, String metricName) {
            super(scope, eventName, metricName, MetricType.STRING);
        }
    }

    public static class BooleanMetricsEntry
    extends MetricsEntry {
        BooleanMetricsEntry(Scope scope, String eventName, String metricName) {
            super(scope, eventName, metricName, MetricType.BOOLEAN);
        }
    }

    public static abstract class MetricsEntry {
        protected Scope scope;
        protected String eventName;
        protected String metricName;
        protected MetricType metricType;

        MetricsEntry(Scope scope, String eventName, String metricName, MetricType metricType) {
            this.scope = scope;
            this.eventName = eventName;
            this.metricName = metricName;
            this.metricType = metricType;
        }

        public Scope getScope() {
            return this.scope;
        }

        public String getEventName() {
            return this.eventName;
        }

        public String getMetricName() {
            return this.metricName;
        }

        public MetricType getMetricType() {
            return this.metricType;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Scope {
        AUTHENTICATIONSERVICE,
        BOTSERVICE,
        EVENTQUEUEWORKER,
        FUSIONREST,
        GATEWAY,
        JOBSCHEDULINGSERVICE,
        OBJECTCACHE,
        REGISTRY,
        REWARDSYSTEM,
        SESSIONCACHE,
        USERNOTIFICATIONSERVICE;

    }
}

