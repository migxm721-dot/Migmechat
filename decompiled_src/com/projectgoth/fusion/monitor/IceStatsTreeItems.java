/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.swt.widgets.TreeItem
 */
package com.projectgoth.fusion.monitor;

import com.projectgoth.fusion.monitor.MonitorHelpers;
import com.projectgoth.fusion.slice.BaseServiceStats;
import java.text.DecimalFormat;
import java.util.Map;
import org.eclipse.swt.widgets.TreeItem;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IceStatsTreeItems {
    protected static final String NEWLINE = "\n";
    protected static final DecimalFormat DF = new DecimalFormat("#.##");
    protected TreeItem iceRequestStatsTreeItem;
    protected TreeItem iceRequestStatsEnabledTreeItem;
    protected TreeItem requestCountTreeItem;
    protected PromptAndContentPair requestCountByOriginTreeItems;
    protected PromptAndContentPair requestCountByOpTreeItems;
    protected PromptAndContentPair requestMeanProcessingTimeByOpTreeItems;
    protected PromptAndContentPair requestMaxProcessingTimeByOpTreeItems;
    protected PromptAndContentPair requestStdevProcessingTimeByOpTreeItems;
    protected PromptAndContentPair request95thPercentileProcessingTimeByOpTreeItems;
    protected PromptAndContentPair requestTotalProcessingTimeByOpTreeItems;
    protected TreeItem iceThreadStatsTreeItem;
    protected TreeItem iceThreadStatsContentTreeItem;

    public IceStatsTreeItems(TreeItem baseTreeItem) {
        this.iceRequestStatsTreeItem = new TreeItem(baseTreeItem, 0);
        this.iceRequestStatsTreeItem.setText("Ice connection stats (seconds):");
        this.iceRequestStatsEnabledTreeItem = new TreeItem(this.iceRequestStatsTreeItem, 0);
        this.requestCountTreeItem = new TreeItem(this.iceRequestStatsTreeItem, 0);
        this.requestCountByOriginTreeItems = new PromptAndContentPair(this.iceRequestStatsTreeItem, "Request count by origin:");
        this.requestCountByOpTreeItems = new PromptAndContentPair(this.iceRequestStatsTreeItem, "Request count by operation:");
        this.requestMeanProcessingTimeByOpTreeItems = new PromptAndContentPair(this.iceRequestStatsTreeItem, "Request mean processing time by operation:");
        this.requestMaxProcessingTimeByOpTreeItems = new PromptAndContentPair(this.iceRequestStatsTreeItem, "Request max processing time by operation:");
        this.requestStdevProcessingTimeByOpTreeItems = new PromptAndContentPair(this.iceRequestStatsTreeItem, "Request stdev in processing time by operation:");
        this.request95thPercentileProcessingTimeByOpTreeItems = new PromptAndContentPair(this.iceRequestStatsTreeItem, "95th percentile value (est) for request processing time by operation:");
        this.requestTotalProcessingTimeByOpTreeItems = new PromptAndContentPair(this.iceRequestStatsTreeItem, "Request total processing time by operation:");
        this.iceThreadStatsTreeItem = new TreeItem(baseTreeItem, 0);
        this.iceThreadStatsTreeItem.setText("Ice thread stats:");
        this.iceThreadStatsContentTreeItem = new TreeItem(this.iceThreadStatsTreeItem, 0);
    }

    public void update(BaseServiceStats stats) {
        this.updateRequestStats(stats);
        this.updateThreadStats(stats);
    }

    public void updateRequestStats(BaseServiceStats stats) {
        this.iceRequestStatsEnabledTreeItem.setText(stats.izeRequestStatsEnabled ? "ENABLED" : "DISABLED");
        if (!stats.izeRequestStatsEnabled) {
            return;
        }
        this.requestCountTreeItem.setText("Request count: " + stats.izeRequestCount);
        this.displaySortedCount(this.requestCountByOriginTreeItems, stats.izeRequestCountByOrigin, stats.izeRequestCount);
        this.displaySortedCount(this.requestCountByOpTreeItems, stats.izeRequestCountByOperation, stats.izeRequestCount);
        this.displaySortedCount(this.requestMeanProcessingTimeByOpTreeItems, stats.izeRequestMeanProcessingTimeByOperation);
        this.displaySortedCount(this.requestMaxProcessingTimeByOpTreeItems, stats.izeRequestMaxProcessingTimeByOperation);
        this.displaySortedCount(this.requestStdevProcessingTimeByOpTreeItems, stats.izeRequestStdevProcessingTimeByOperation);
        this.displaySortedCount(this.request95thPercentileProcessingTimeByOpTreeItems, stats.izeRequest95thPercentileProcessingTimeByOperation);
        this.displaySortedCount(this.requestTotalProcessingTimeByOpTreeItems, stats.izeRequestTotalProcessingTimeByOperation);
    }

    public void updateThreadStats(BaseServiceStats stats) {
        if (stats.izeThreadStatsEnabled) {
            this.iceThreadStatsContentTreeItem.setText("ENABLED \nObjectAdapterThreadPool: \nrunning=" + stats.izeObjectAdapterThreadPoolRunning + NEWLINE + "inUse=" + stats.izeObjectAdapterThreadPoolInUse + NEWLINE + "inUse high watermark=" + stats.izeObjectAdapterThreadPoolInUseHighWatermark + NEWLINE + "load=" + DF.format(stats.izeObjectAdapterThreadPoolLoad) + NEWLINE + "Config params: " + NEWLINE + "size=" + stats.izeObjectAdapterThreadPoolSize + NEWLINE + "sizeMax=" + stats.izeObjectAdapterThreadPoolSizeMax + NEWLINE + "sizeWarn=" + stats.izeObjectAdapterThreadPoolSizeWarn + NEWLINE + NEWLINE + "AMD dispatch thread pool:" + "running=" + stats.amdObjectAdapterThreadPoolRunning + NEWLINE + "inUse=" + stats.amdObjectAdapterThreadPoolInUse + NEWLINE + "inUse high watermark=" + stats.amdObjectAdapterThreadPoolInUseHighWatermark + NEWLINE + "size=" + stats.amdObjectAdapterThreadPoolSize + NEWLINE + "sizeMax=" + stats.amdObjectAdapterThreadPoolSizeMax + "queue length=" + stats.amdObjectAdapterThreadPoolQueueLength);
            if (stats.izeClientThreadPoolStatsEnabled) {
                this.iceThreadStatsContentTreeItem.setText(this.iceThreadStatsContentTreeItem.getText() + NEWLINE + NEWLINE + "Ice client ThreadPool: \n" + "running=" + stats.izeClientThreadPoolRunning + NEWLINE + "inUse=" + stats.izeClientThreadPoolInUse + NEWLINE + "inUse high watermark=" + stats.izeClientThreadPoolInUseHighWatermark + NEWLINE + "load=" + DF.format(stats.izeClientThreadPoolLoad) + NEWLINE + "Config params: " + NEWLINE + "size=" + stats.izeClientThreadPoolSize + NEWLINE + "sizeMax=" + stats.izeClientThreadPoolSizeMax + NEWLINE + "sizeWarn=" + stats.izeClientThreadPoolSizeWarn + NEWLINE);
            }
        } else {
            this.iceThreadStatsContentTreeItem.setText("DISABLED");
        }
    }

    private void displaySortedCount(PromptAndContentPair treeItems, Map<String, ? extends Number> counts) {
        this.displaySortedCount(treeItems, counts, null);
    }

    private void displaySortedCount(PromptAndContentPair treeItems, Map<String, ? extends Number> counts, Long total) {
        Map sorted = MonitorHelpers.sortDesc(counts);
        String display = MonitorHelpers.countsMapToString(sorted, total, DF);
        treeItems.setContent(display);
    }

    private class PromptAndContentPair {
        private TreeItem promptItem;
        private TreeItem contentItem;

        public PromptAndContentPair(TreeItem baseTreeItem, String prompt) {
            this.promptItem = new TreeItem(baseTreeItem, 0);
            this.promptItem.setText(prompt);
            this.contentItem = new TreeItem(this.promptItem, 0);
        }

        public void setContent(String s) {
            this.contentItem.setText(s);
        }
    }
}

