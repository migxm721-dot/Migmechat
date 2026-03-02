/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.voiceengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.voiceengine.CallMakerI;
import com.projectgoth.fusion.voiceengine.CallingCard;
import com.projectgoth.fusion.voiceengine.ExtendedControl;
import com.projectgoth.fusion.voiceengine.FastAGIChannel;
import com.projectgoth.fusion.voiceengine.FastAGICommand;
import com.projectgoth.fusion.voiceengine.FastAGIServer;
import com.projectgoth.fusion.voiceengine.LogicHelper;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FastAGIWorker
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FastAGIWorker.class));
    private FastAGIServer server;
    private CallMakerI callMaker;
    private Socket socket;

    public FastAGIWorker(FastAGIServer server, CallMakerI callMaker, Socket socket) {
        this.server = server;
        this.callMaker = callMaker;
        this.socket = socket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void run() {
        block11: {
            block10: {
                FastAGICommand command = new FastAGICommand(this.socket.getInputStream());
                log.debug((Object)command.getRawCommand());
                String request = command.getRequest();
                if (!"callingcard".equalsIgnoreCase(request)) break block10;
                this.startCallingCard(command);
            }
            Object var4_5 = null;
            try {
                this.socket.close();
            }
            catch (Exception e2) {}
            break block11;
            {
                catch (CreateException e) {
                    log.warn((Object)"Unable to create EJB for database operations", (Throwable)e);
                    Object var4_6 = null;
                    try {
                        this.socket.close();
                    }
                    catch (Exception e2) {}
                    break block11;
                }
                catch (Exception e) {
                    log.warn((Object)(e.getClass().getName() + " occured while processing FastAGI request"), (Throwable)e);
                    Object var4_7 = null;
                    try {
                        this.socket.close();
                    }
                    catch (Exception e2) {}
                }
            }
            catch (Throwable throwable) {
                Object var4_8 = null;
                try {
                    this.socket.close();
                }
                catch (Exception e2) {
                    // empty catch block
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void startCallingCard(FastAGICommand command) throws CreateException, RemoteException, IOException {
        FastAGIChannel channel = null;
        ExtendedControl control = null;
        LogicHelper helper = null;
        CallingCard callingCard = null;
        try {
            try {
                log.warn((Object)"Starting the calling card system");
                channel = new FastAGIChannel(this, this.server, command, this.socket);
                control = new ExtendedControl(channel);
                helper = new LogicHelper(this, this.server, command, control, this.callMaker);
                callingCard = new CallingCard(helper, control);
                callingCard.execute();
            }
            catch (CreateException e) {
                log.warn((Object)("Error running the calling card system (ex1); " + e.getMessage()));
                Object var8_7 = null;
                log.info((Object)"Completed the calling card system");
                return;
            }
            catch (RemoteException e) {
                log.warn((Object)("Error running the calling card system (ex2); " + e.getMessage()));
                Object var8_8 = null;
                log.info((Object)"Completed the calling card system");
                return;
            }
            catch (IOException e) {
                log.warn((Object)("Error running the calling card system (ex3); " + e.getMessage()));
                Object var8_9 = null;
                log.info((Object)"Completed the calling card system");
                return;
            }
            Object var8_6 = null;
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
            log.info((Object)"Completed the calling card system");
            throw throwable;
        }
        log.info((Object)"Completed the calling card system");
    }
}

