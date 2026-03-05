/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MultiSampler;
import com.projectgoth.fusion.common.Sampler;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.GatewayContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class SamplingTask
implements Runnable {
    private final GatewayContext gatewayContext;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Gateway.class));
    private static final String SEPERATOR = ",";
    private static final String NEW_LINE = "\r\n";
    MultiSampler sampler = new MultiSampler();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private String samepleFile = System.getProperty("log.dir") + File.separator + System.getProperty("log.filename") + ".inst.sample.";
    private String summaryFile = System.getProperty("log.dir") + File.separator + System.getProperty("log.filename") + ".inst.summary.";

    public SamplingTask(GatewayContext gatewayContext) {
        this.gatewayContext = gatewayContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        try {
            boolean logSummary;
            List<Sampler.Summary> summaries = this.sampler.summarize();
            boolean logSample = SystemProperty.getBool("LogInstrumentationSample", true);
            if (logSample) {
                FileOutputStream out = new FileOutputStream(this.samepleFile + this.fileDateFormat.format(new Date()), true);
                for (Sampler.Summary summary : summaries) {
                    this.logSamples(summary.name, summary.samples, out);
                }
                out.close();
            }
            if (logSummary = SystemProperty.getBool("LogInstrumentationSummary", true)) {
                FileOutputStream out = new FileOutputStream(this.summaryFile + this.fileDateFormat.format(new Date()), true);
                for (Sampler.Summary summary : summaries) {
                    this.logSummary(summary, out);
                }
                out.close();
            }
        }
        catch (Exception e) {
            log.error((Object)"Error in summarizing instrumentation samples", (Throwable)e);
        }
        finally {
            long samplingInterval = SystemProperty.getLong("SamplingInverval", 60L) * 1000L;
            this.gatewayContext.getGatewayThreadPool().get((Object)Gateway.ThreadPoolName.PRIMARY).schedule(this, samplingInterval, TimeUnit.MILLISECONDS);
        }
    }

    private void logSamples(String name, Sampler.Sample[] samples, OutputStream out) throws IOException {
        for (Sampler.Sample sample : samples) {
            StringBuilder builder = new StringBuilder();
            builder.append(this.dateFormat.format(sample.timestamp)).append(SEPERATOR).append(name).append(SEPERATOR).append(1).append(SEPERATOR).append(sample.data).append(SEPERATOR).append(sample.data).append(NEW_LINE);
            out.write(builder.toString().getBytes());
        }
    }

    private void logSummary(Sampler.Summary summary, OutputStream out) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(this.dateFormat.format(new Date())).append(SEPERATOR).append(summary.name).append(SEPERATOR).append(summary.count).append(SEPERATOR).append(summary.min).append(SEPERATOR).append(summary.median).append(SEPERATOR).append(summary.max).append(SEPERATOR).append(this.decimalFormat.format(summary.mean)).append(SEPERATOR).append(this.decimalFormat.format(summary.mean1Percentile)).append(SEPERATOR).append(this.decimalFormat.format(summary.mean5Percentile)).append(SEPERATOR).append(this.decimalFormat.format(summary.standardDeviation)).append(NEW_LINE);
        out.write(builder.toString().getBytes());
    }
}

