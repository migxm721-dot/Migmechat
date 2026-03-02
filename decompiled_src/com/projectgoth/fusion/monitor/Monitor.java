/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Properties
 *  org.eclipse.swt.events.SelectionAdapter
 *  org.eclipse.swt.events.SelectionEvent
 *  org.eclipse.swt.events.SelectionListener
 *  org.eclipse.swt.layout.FillLayout
 *  org.eclipse.swt.widgets.Composite
 *  org.eclipse.swt.widgets.Decorations
 *  org.eclipse.swt.widgets.Display
 *  org.eclipse.swt.widgets.Event
 *  org.eclipse.swt.widgets.Layout
 *  org.eclipse.swt.widgets.Listener
 *  org.eclipse.swt.widgets.Menu
 *  org.eclipse.swt.widgets.MenuItem
 *  org.eclipse.swt.widgets.MessageBox
 *  org.eclipse.swt.widgets.Shell
 *  org.eclipse.swt.widgets.Tree
 *  org.eclipse.swt.widgets.TreeItem
 */
package com.projectgoth.fusion.monitor;

import Ice.Application;
import Ice.Properties;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.monitor.AuthenticationServiceMonitor;
import com.projectgoth.fusion.monitor.BaseMonitor;
import com.projectgoth.fusion.monitor.BlueLabelServiceMonitor;
import com.projectgoth.fusion.monitor.BotHunterMonitor;
import com.projectgoth.fusion.monitor.BotServiceMonitor;
import com.projectgoth.fusion.monitor.EmailAlertMonitor;
import com.projectgoth.fusion.monitor.EventQueueMonitor;
import com.projectgoth.fusion.monitor.EventStoreMonitor;
import com.projectgoth.fusion.monitor.EventSystemMonitor;
import com.projectgoth.fusion.monitor.GatewayMonitor;
import com.projectgoth.fusion.monitor.ImageServerMonitor;
import com.projectgoth.fusion.monitor.JobSchedulingServiceMonitor;
import com.projectgoth.fusion.monitor.MessageLoggerMonitor;
import com.projectgoth.fusion.monitor.ObjectCacheMonitor;
import com.projectgoth.fusion.monitor.RecommendationDataCollectionServiceMonitor;
import com.projectgoth.fusion.monitor.RecommendationGenerationServiceMonitor;
import com.projectgoth.fusion.monitor.RegistryMonitor;
import com.projectgoth.fusion.monitor.ReputationServiceMonitor;
import com.projectgoth.fusion.monitor.SMSEngineMonitor;
import com.projectgoth.fusion.monitor.SessionCacheMonitor;
import com.projectgoth.fusion.monitor.UserNotificationServiceMonitor;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class Monitor
extends Application {
    private static final String APP_NAME = "Monitor";
    private static final String CONFIG_FILE = ConfigUtils.getConfigDirectory() + "Monitor.cfg";
    public static Properties properties = null;
    public static String smtpHost;
    public static String smtpRecipients;
    public static String jdbcURL;
    public static Tree tree;
    public static LinkedList<RegistryMonitor> registryMonitors;
    public static LinkedList<AuthenticationServiceMonitor> authenticationServiceMonitors;
    public static LinkedList<ObjectCacheMonitor> objectCacheMonitors;
    public static LinkedList<GatewayMonitor> gatewayMonitors;
    public static LinkedList<SMSEngineMonitor> smsEngineMonitors;
    public static LinkedList<MessageLoggerMonitor> messageLoggerMonitors;
    public static LinkedList<SessionCacheMonitor> sessionCacheMonitors;
    public static LinkedList<EventSystemMonitor> eventSystemMonitors;
    public static LinkedList<EventStoreMonitor> eventStoreMonitors;
    public static LinkedList<ImageServerMonitor> imageServerMonitors;
    public static LinkedList<EmailAlertMonitor> emailAlertMonitors;
    public static LinkedList<JobSchedulingServiceMonitor> jobSchedulingMonitors;
    public static LinkedList<UserNotificationServiceMonitor> userNotificationServiceMonitors;
    public static LinkedList<BlueLabelServiceMonitor> blueLabelServiceMonitors;
    public static LinkedList<ReputationServiceMonitor> reputationServiceMonitors;
    public static LinkedList<BotServiceMonitor> botServiceMonitors;
    public static LinkedList<EventQueueMonitor> eventQueueMonitors;
    public static LinkedList<BotHunterMonitor> botHunterMonitors;
    public static LinkedList<RecommendationDataCollectionServiceMonitor> rdcsMonitors;
    public static LinkedList<RecommendationGenerationServiceMonitor> rgsMonitors;
    protected Shell shell;
    protected Display display;
    private TreeItem registriesNode;
    private TreeItem authenticationServiceNode;
    private TreeItem objectCachesNode;
    private TreeItem gatewaysNode;
    private TreeItem smsEngineNode;
    private TreeItem messageLoggerNode;
    private TreeItem sessionCacheNode;
    private TreeItem eventSystemNode;
    private TreeItem eventStoreNode;
    private TreeItem imageServerNode;
    private TreeItem emailAlertNode;
    private TreeItem jobSchedulingServiceNode;
    private TreeItem userNotificationServiceNode;
    private TreeItem blueLabelServiceNode;
    private TreeItem reputationServiceNode;
    private TreeItem botServiceNode;
    private TreeItem eventQueueNode;
    private TreeItem botHunterNode;
    private TreeItem rdcsNode;
    private TreeItem rgsNode;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);

    public static void main(String[] args) {
        try {
            Monitor window = new Monitor();
            if (args.length >= 1) {
                window.main(APP_NAME, args, args[0]);
            } else {
                window.main(APP_NAME, args, CONFIG_FILE);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int run(String[] arg0) {
        this.open();
        return 0;
    }

    public void open() {
        this.display = Display.getDefault();
        this.createContents();
        this.shell.open();
        this.shell.layout();
        if (!this.loadConfig()) {
            return;
        }
        this.initTree();
        this.startMonitors();
        while (!this.shell.isDisposed()) {
            if (this.display.readAndDispatch()) continue;
            this.display.sleep();
        }
        this.scheduler.shutdownNow();
        this.display.dispose();
    }

    private boolean loadConfig() {
        properties = Monitor.communicator().getProperties();
        smtpHost = properties.getProperty("SMTP.Host");
        if (smtpHost.length() == 0) {
            MessageBox messageBox = new MessageBox(this.shell, 33);
            messageBox.setMessage("You must specify a value for SMTP.Host in " + CONFIG_FILE);
            messageBox.open();
            return false;
        }
        smtpRecipients = properties.getProperty("SMTP.Recipients");
        if (smtpRecipients.length() == 0) {
            MessageBox messageBox = new MessageBox(this.shell, 33);
            messageBox.setMessage("You must specify a value for SMTP.Recipients in " + CONFIG_FILE);
            messageBox.open();
            return false;
        }
        jdbcURL = properties.getProperty("JDBC.URL");
        if (jdbcURL.length() == 0) {
            MessageBox messageBox = new MessageBox(this.shell, 33);
            messageBox.setMessage("You must specify a value for JDBC.URL in " + CONFIG_FILE);
            messageBox.open();
            return false;
        }
        return true;
    }

    private void initTree() {
        this.registriesNode = new TreeItem(tree, 0);
        this.registriesNode.setText("Registries");
        this.authenticationServiceNode = new TreeItem(tree, 0);
        this.authenticationServiceNode.setText("Authentication Servers");
        this.objectCachesNode = new TreeItem(tree, 0);
        this.objectCachesNode.setText("Object Caches");
        this.gatewaysNode = new TreeItem(tree, 0);
        this.gatewaysNode.setText("Gateways");
        this.smsEngineNode = new TreeItem(tree, 0);
        this.smsEngineNode.setText("SMSEngines");
        this.messageLoggerNode = new TreeItem(tree, 0);
        this.messageLoggerNode.setText("Message Loggers");
        this.sessionCacheNode = new TreeItem(tree, 0);
        this.sessionCacheNode.setText("Session Caches");
        this.eventQueueNode = new TreeItem(tree, 0);
        this.eventQueueNode.setText("Event Queue Workers");
        this.eventSystemNode = new TreeItem(tree, 0);
        this.eventSystemNode.setText("Event Systems");
        this.eventStoreNode = new TreeItem(tree, 0);
        this.eventStoreNode.setText("Event Stores");
        this.imageServerNode = new TreeItem(tree, 0);
        this.imageServerNode.setText("Image Servers");
        this.emailAlertNode = new TreeItem(tree, 0);
        this.emailAlertNode.setText("Email Alert Apps");
        this.jobSchedulingServiceNode = new TreeItem(tree, 0);
        this.jobSchedulingServiceNode.setText("Job Scheduling Services");
        this.userNotificationServiceNode = new TreeItem(tree, 0);
        this.userNotificationServiceNode.setText("User Notification Services");
        this.blueLabelServiceNode = new TreeItem(tree, 0);
        this.blueLabelServiceNode.setText("Blue Label Services");
        this.reputationServiceNode = new TreeItem(tree, 0);
        this.reputationServiceNode.setText("Reputation Services");
        this.botServiceNode = new TreeItem(tree, 0);
        this.botServiceNode.setText("Bot Services");
        this.botHunterNode = new TreeItem(tree, 0);
        this.botHunterNode.setText("Bot Hunter");
        this.rdcsNode = new TreeItem(tree, 0);
        this.rdcsNode.setText("Recommendation Data Collection Services");
        this.rgsNode = new TreeItem(tree, 0);
        this.rgsNode.setText("Recommendation Generation Services");
    }

    private void startMonitors() {
        long updateInterval = properties.getPropertyAsIntWithDefault("UpdateInterval", 60) * 1000;
        int numRegistries = properties.getPropertyAsInt("NumRegistries");
        for (int i = 1; i <= numRegistries; ++i) {
            HostAndPort hap = new HostAndPort("Registry", i);
            RegistryMonitor registryMonitor = new RegistryMonitor(hap.getHostName(), hap.getPort(), this.registriesNode);
            registryMonitors.add(registryMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(registryMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numAuths = properties.getPropertyAsInt("NumAuthenticationServers");
        for (int i = 1; i <= numAuths; ++i) {
            HostAndPort hap = new HostAndPort("AuthenticationService", i);
            AuthenticationServiceMonitor authenticationServiceMonitor = new AuthenticationServiceMonitor(hap.getHostName(), hap.getPort(), this.authenticationServiceNode);
            authenticationServiceMonitors.add(authenticationServiceMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(authenticationServiceMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numObjectCaches = properties.getPropertyAsInt("NumObjectCaches");
        for (int i = 1; i <= numObjectCaches; ++i) {
            HostAndPort hap = new HostAndPort("ObjectCache", i);
            ObjectCacheMonitor objectCacheMonitor = new ObjectCacheMonitor(hap.getHostName(), hap.getPort(), this.objectCachesNode);
            objectCacheMonitors.add(objectCacheMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(objectCacheMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numGateways = properties.getPropertyAsInt("NumGateways");
        for (int i = 1; i <= numGateways; ++i) {
            HostAndPort hap = new HostAndPort("Gateway", i);
            GatewayMonitor gatewayMonitor = new GatewayMonitor(hap.getHostName(), hap.getPort(), this.gatewaysNode);
            gatewayMonitors.add(gatewayMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(gatewayMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numSMSEngines = properties.getPropertyAsInt("NumSMSEngines");
        for (int i = 1; i <= numSMSEngines; ++i) {
            HostAndPort hap = new HostAndPort("SMSEngine", i);
            SMSEngineMonitor smsEngineMonitor = new SMSEngineMonitor(hap.getHostName(), hap.getPort(), this.smsEngineNode);
            smsEngineMonitors.add(smsEngineMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(smsEngineMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numMessageLoggers = properties.getPropertyAsInt("NumMessageLoggers");
        for (int i = 1; i <= numMessageLoggers; ++i) {
            HostAndPort hap = new HostAndPort("MessageLogger", i);
            MessageLoggerMonitor messageLoggerMonitor = new MessageLoggerMonitor(hap.getHostName(), hap.getPort(), this.messageLoggerNode);
            messageLoggerMonitors.add(messageLoggerMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(messageLoggerMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numSessionCaches = properties.getPropertyAsInt("NumSessionCaches");
        for (int i = 1; i <= numSessionCaches; ++i) {
            HostAndPort hap = new HostAndPort("SessionCache", i);
            SessionCacheMonitor sessionCacheMonitor = new SessionCacheMonitor(hap.getHostName(), hap.getPort(), this.sessionCacheNode);
            sessionCacheMonitors.add(sessionCacheMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(sessionCacheMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numEventQueueWorkers = properties.getPropertyAsInt("NumEventQueueWorkers");
        for (int i = 1; i <= numEventQueueWorkers; ++i) {
            HostAndPort hap = new HostAndPort("EventQueueWorker", i);
            EventQueueMonitor monitor = new EventQueueMonitor(hap.getHostName(), hap.getPort(), this.eventQueueNode);
            eventQueueMonitors.add(monitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(monitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numEventSystems = properties.getPropertyAsInt("NumEventSystems");
        for (int i = 1; i <= numEventSystems; ++i) {
            HostAndPort hap = new HostAndPort("EventSystem", i);
            EventSystemMonitor eventSystemMonitor = new EventSystemMonitor(hap.getHostName(), hap.getPort(), this.eventSystemNode);
            eventSystemMonitors.add(eventSystemMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(eventSystemMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numEventStores = properties.getPropertyAsInt("NumEventStores");
        for (int i = 1; i <= numEventStores; ++i) {
            HostAndPort hap = new HostAndPort("EventStore", i);
            EventStoreMonitor eventStoreMonitor = new EventStoreMonitor(hap.getHostName(), hap.getPort(), this.eventStoreNode);
            eventStoreMonitors.add(eventStoreMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(eventStoreMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numImageServers = properties.getPropertyAsInt("NumImageServers");
        for (int i = 1; i <= numImageServers; ++i) {
            HostAndPort hap = new HostAndPort("ImageServer", i);
            ImageServerMonitor imageServerMonitor = new ImageServerMonitor(hap.getHostName(), hap.getPort(), this.imageServerNode);
            imageServerMonitors.add(imageServerMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(imageServerMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numEmailAlertApps = properties.getPropertyAsInt("NumEmailAlertApps");
        for (int i = 1; i <= numEmailAlertApps; ++i) {
            HostAndPort hap = new HostAndPort("EmailAlert", i);
            EmailAlertMonitor emailAlertMonitor = new EmailAlertMonitor(hap.getHostName(), hap.getPort(), this.emailAlertNode);
            emailAlertMonitors.add(emailAlertMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(emailAlertMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numJobSchedulingServices = properties.getPropertyAsInt("NumJobSchedulingServices");
        for (int i = 1; i <= numJobSchedulingServices; ++i) {
            HostAndPort hap = new HostAndPort("JobSchedulingService", i);
            JobSchedulingServiceMonitor jobSchedulingServiceMonitor = new JobSchedulingServiceMonitor(hap.getHostName(), hap.getPort(), this.jobSchedulingServiceNode);
            jobSchedulingMonitors.add(jobSchedulingServiceMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(jobSchedulingServiceMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numUserNotificationServices = properties.getPropertyAsInt("NumUserNotificationServices");
        for (int i = 1; i <= numUserNotificationServices; ++i) {
            HostAndPort hap = new HostAndPort("UserNotificationService", i);
            UserNotificationServiceMonitor userNotificationServiceMonitor = new UserNotificationServiceMonitor(hap.getHostName(), hap.getPort(), this.userNotificationServiceNode);
            userNotificationServiceMonitors.add(userNotificationServiceMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(userNotificationServiceMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numBlueLabelServices = properties.getPropertyAsInt("NumBlueLabelServices");
        for (int i = 1; i <= numBlueLabelServices; ++i) {
            HostAndPort hap = new HostAndPort("BlueLabelService", i);
            BlueLabelServiceMonitor blueLabelServiceMonitor = new BlueLabelServiceMonitor(hap.getHostName(), hap.getPort(), this.blueLabelServiceNode);
            blueLabelServiceMonitors.add(blueLabelServiceMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(blueLabelServiceMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numReputationServices = properties.getPropertyAsInt("NumReputationServices");
        for (int i = 1; i <= numReputationServices; ++i) {
            HostAndPort hap = new HostAndPort("ReputationService", i);
            ReputationServiceMonitor reputationServiceMonitor = new ReputationServiceMonitor(hap.getHostName(), hap.getPort(), this.reputationServiceNode);
            reputationServiceMonitors.add(reputationServiceMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(reputationServiceMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numBotServices = properties.getPropertyAsInt("NumBotServices");
        for (int i = 1; i <= numBotServices; ++i) {
            HostAndPort hap = new HostAndPort("BotService", i);
            BotServiceMonitor botServiceMonitor = new BotServiceMonitor(hap.getHostName(), hap.getPort(), this.botServiceNode);
            botServiceMonitors.add(botServiceMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(botServiceMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numBotHunters = properties.getPropertyAsInt("NumBotHunters");
        for (int i = 1; i <= numBotHunters; ++i) {
            HostAndPort hap = new HostAndPort("BotHunter", i);
            BotHunterMonitor botHunterMonitor = new BotHunterMonitor(hap.getHostName(), hap.getPort(), this.botHunterNode);
            botHunterMonitors.add(botHunterMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(botHunterMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numRDCSs = properties.getPropertyAsInt("NumRecommendationDataCollectionServices");
        for (int i = 1; i <= numRDCSs; ++i) {
            HostAndPort hap = new HostAndPort("RecommendationDataCollectionService", i);
            RecommendationDataCollectionServiceMonitor rdcsMonitor = new RecommendationDataCollectionServiceMonitor(hap.getHostName(), hap.getPort(), this.rdcsNode);
            rdcsMonitors.add(rdcsMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(rdcsMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
        int numRGSs = properties.getPropertyAsInt("NumRecommendationGenerationServices");
        for (int i = 1; i <= numRGSs; ++i) {
            HostAndPort hap = new HostAndPort("RecommendationGenerationService", i);
            RecommendationGenerationServiceMonitor rgsMonitor = new RecommendationGenerationServiceMonitor(hap.getHostName(), hap.getPort(), this.rgsNode);
            rgsMonitors.add(rgsMonitor);
            this.scheduler.scheduleWithFixedDelay(new MonitorTask(rgsMonitor), 0L, updateInterval, TimeUnit.MILLISECONDS);
        }
    }

    protected void createContents() {
        this.shell = new Shell();
        this.shell.setLayout((Layout)new FillLayout());
        this.shell.setSize(500, 375);
        this.shell.setText("Fusion Monitor");
        Menu menu = new Menu((Decorations)this.shell, 2);
        this.shell.setMenuBar(menu);
        MenuItem fileMenuItem = new MenuItem(menu, 64);
        fileMenuItem.setText("File");
        Menu fileMenu = new Menu(fileMenuItem);
        fileMenuItem.setMenu(fileMenu);
        MenuItem fileExitMenuItem = new MenuItem(fileMenu, 0);
        fileExitMenuItem.addSelectionListener((SelectionListener)new SelectionAdapter(){

            public void widgetSelected(SelectionEvent e) {
                Monitor.this.shell.dispose();
            }
        });
        fileExitMenuItem.setText("Exit");
        tree = new Tree((Composite)this.shell, 2048);
        tree.addListener(17, new Listener(){

            public void handleEvent(Event e) {
                for (ObjectCacheMonitor objectCacheMonitor : objectCacheMonitors) {
                    if (!e.item.equals(objectCacheMonitor.userObjectsTreeItem)) continue;
                    objectCacheMonitor.loadUserObjects();
                }
                for (GatewayMonitor gatewayMonitor : gatewayMonitors) {
                    if (!e.item.equals(gatewayMonitor.threadPoolTreeItem) || !gatewayMonitor.threadPoolTreeItem.getExpanded()) continue;
                    gatewayMonitor.threadPoolTreeItem.setText("Thread Pools");
                }
            }
        });
    }

    static {
        registryMonitors = new LinkedList();
        authenticationServiceMonitors = new LinkedList();
        objectCacheMonitors = new LinkedList();
        gatewayMonitors = new LinkedList();
        smsEngineMonitors = new LinkedList();
        messageLoggerMonitors = new LinkedList();
        sessionCacheMonitors = new LinkedList();
        eventSystemMonitors = new LinkedList();
        eventStoreMonitors = new LinkedList();
        imageServerMonitors = new LinkedList();
        emailAlertMonitors = new LinkedList();
        jobSchedulingMonitors = new LinkedList();
        userNotificationServiceMonitors = new LinkedList();
        blueLabelServiceMonitors = new LinkedList();
        reputationServiceMonitors = new LinkedList();
        botServiceMonitors = new LinkedList();
        eventQueueMonitors = new LinkedList();
        botHunterMonitors = new LinkedList();
        rdcsMonitors = new LinkedList();
        rgsMonitors = new LinkedList();
    }

    private class HostAndPort {
        private final String hostName;
        private final Integer port;

        public HostAndPort(String propName, int propIndex) {
            String[] hostAndPort = properties.getProperty(propName + propIndex).toUpperCase().split(":");
            this.hostName = hostAndPort[0];
            this.port = hostAndPort.length > 1 ? Integer.valueOf(hostAndPort[1]) : null;
        }

        public String getHostName() {
            return this.hostName;
        }

        public Integer getPort() {
            return this.port;
        }
    }

    private class MonitorTask
    implements Runnable {
        private BaseMonitor baseMonitor;

        public MonitorTask(BaseMonitor baseMonitor) {
            this.baseMonitor = baseMonitor;
        }

        public void run() {
            this.baseMonitor.getStats();
            Display.getDefault().asyncExec((Runnable)this.baseMonitor);
        }
    }
}

