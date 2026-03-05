/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectPrx
 *  Ice.Util
 */
package com.projectgoth.fusion.maintenance;

import Ice.Communicator;
import Ice.ObjectPrx;
import Ice.Util;
import com.projectgoth.fusion.common.PortRegistry;
import com.projectgoth.fusion.slice.GroupEvent;
import com.projectgoth.fusion.slice.JobSchedulingServicePrx;
import com.projectgoth.fusion.slice.JobSchedulingServicePrxHelper;
import java.util.Calendar;
import java.util.TimeZone;

public class JobMaintenance {
    private static Communicator iceCommunicator = Util.initialize((String[])new String[0]);
    private static JobSchedulingServicePrx jobSchedulingServicePrx;

    public static JobSchedulingServicePrx getJobSchedulingServiceProxy(String hostname) throws Exception {
        if (jobSchedulingServicePrx == null) {
            if (iceCommunicator == null) {
                throw new Exception("Ice communicator has not been initialized");
            }
            ObjectPrx base = iceCommunicator.stringToProxy("JobSchedulingService: tcp -h " + hostname + " -p " + PortRegistry.JOB_SCHEDULING_SERVICE.getPort() + " -t 5000");
            jobSchedulingServicePrx = JobSchedulingServicePrxHelper.checkedCast(base);
            if (jobSchedulingServicePrx == null) {
                throw new Exception("Invalid JobSchedulingService proxy");
            }
        }
        return jobSchedulingServicePrx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: JobMaintenance <job scheduling hostname>");
            System.exit(1);
        }
        try {
            try {
                String hostname = args[0];
                TimeZone tz = TimeZone.getTimeZone("Africa/Johannesburg");
                Calendar calendar = Calendar.getInstance(tz);
                calendar.set(5, 2);
                calendar.set(2, 4);
                calendar.set(11, 18);
                calendar.set(12, 30);
                calendar.set(13, 0);
                GroupEvent groupEvent = new GroupEvent();
                groupEvent.groupId = 12;
                groupEvent.startTime = calendar.getTimeInMillis();
                groupEvent.chatRoomName = "KXIP Stadium";
                groupEvent.duration = 60;
                groupEvent.status = 1;
                groupEvent.description = "Chat with Yousuf Abdulla, Kings XI Punjab Star";
                int eventId = JobMaintenance.getJobSchedulingServiceProxy(hostname).scheduleFusionGroupEvent(groupEvent);
                System.out.println("event id " + eventId);
                groupEvent.id = eventId;
            }
            catch (Exception e) {
                e.printStackTrace();
                Object var7_8 = null;
                iceCommunicator.shutdown();
                System.exit(0);
                return;
            }
            Object var7_7 = null;
        }
        catch (Throwable throwable) {
            Object var7_9 = null;
            iceCommunicator.shutdown();
            System.exit(0);
            throw throwable;
        }
        iceCommunicator.shutdown();
        System.exit(0);
    }
}

