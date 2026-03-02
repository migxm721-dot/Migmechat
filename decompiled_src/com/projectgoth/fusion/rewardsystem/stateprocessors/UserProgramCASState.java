/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Response
 *  redis.clients.jedis.Transaction
 *  redis.clients.jedis.exceptions.JedisException
 */
package com.projectgoth.fusion.rewardsystem.stateprocessors;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.log4j.Log4JUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.stateprocessors.RewardProgramStateHandler;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

public class UserProgramCASState {
    private static final Logger log = Log4JUtils.getLogger(UserProgramCASState.class);
    private static ConcurrentHashMap<String, RewardProgramStateHandler> handlers = new ConcurrentHashMap();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean apply(Jedis r, RewardProgramData program, RewardProgramTrigger trigger, String stateParentKey) throws Exception {
        RewardProgramStateHandler.PerformReturn ret;
        String handlerClassFullPath = program.getRewardProgramStateHandlerClassFullPath();
        if (StringUtil.isBlank(handlerClassFullPath)) {
            return true;
        }
        RewardProgramStateHandler handler = UserProgramCASState.getInstance(handlerClassFullPath);
        if (handler == null) {
            return false;
        }
        String stateKey = stateParentKey + ":" + handler.getStateKeySuffix();
        try {
            while (true) {
                Object var10_11;
                StateTxn txn;
                block11: {
                    block10: {
                        txn = new StateTxn(r, stateKey);
                        try {
                            block13: {
                                block12: {
                                    ret = handler.perform(program, trigger, txn.state);
                                    if (!ret.save) break block12;
                                    if (!txn.tryCommit(ret.newState)) break block13;
                                }
                                var10_11 = null;
                                break block10;
                            }
                            if (log.isDebugEnabled()) {
                                log.debug((Object)(Thread.currentThread().getName() + " transaction conflicted. Restarting transaction"));
                            }
                            break block11;
                        }
                        catch (Throwable throwable) {
                            var10_11 = null;
                            txn.close();
                            throw throwable;
                        }
                    }
                    txn.close();
                    return ret.cont;
                }
                var10_11 = null;
                txn.close();
            }
        }
        catch (JedisException e) {
            log.error((Object)("Jedis exception:" + (Object)((Object)e)), (Throwable)e);
            ret = RewardProgramStateHandler.PerformReturn.NOTHING;
            return ret.cont;
        }
        catch (Exception e) {
            log.error((Object)("Uncaught exception:" + e), (Throwable)e);
            ret = RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        return ret.cont;
    }

    private static RewardProgramStateHandler getInstance(String classFullPath) {
        RewardProgramStateHandler currentHandler = handlers.get(classFullPath);
        if (null == currentHandler) {
            RewardProgramStateHandler newHandler = UserProgramCASState.instantiate(classFullPath);
            if (null == newHandler) {
                return null;
            }
            handlers.put(classFullPath, newHandler);
            return newHandler;
        }
        return currentHandler;
    }

    private static RewardProgramStateHandler instantiate(String classFullPath) {
        try {
            URL[] cp;
            URLClassLoader urlcl;
            Class<?> clazz;
            File f = new File(classFullPath);
            String className = f.getName();
            String parent = f.getParent();
            if (null != parent && (clazz = (urlcl = new URLClassLoader(cp = new URL[]{new URL(f.getParent())})).loadClass(className)) != null) {
                return (RewardProgramStateHandler)RewardProgramStateHandler.class.cast(clazz.newInstance());
            }
            return (RewardProgramStateHandler)RewardProgramStateHandler.class.cast(Class.forName(className).newInstance());
        }
        catch (Exception e) {
            log.error((Object)("Unable to load RewardProgramStateHandler [" + classFullPath + "]. Exception:" + e), (Throwable)e);
            return null;
        }
    }

    private static class StateTxn {
        private final Jedis j;
        private final String key;
        public final String state;
        private final Transaction txn;
        private FSM_STATE fsm = FSM_STATE.UNINITIALISED;

        public StateTxn(Jedis j, String key) throws JedisException {
            this.j = j;
            this.key = key;
            this.watch(this.key);
            this.fsm = FSM_STATE.WATCHING;
            this.state = j.get(key);
            this.txn = this.multi();
            this.fsm = FSM_STATE.OPEN;
        }

        private void watch(String key) throws JedisException {
            String result = this.j.watch(new String[]{key});
            if (!"ok".equalsIgnoreCase(result)) {
                this.fsm = FSM_STATE.ERROR;
                throw new JedisException("watch(" + key + ") does not return 'ok'. Result:[" + result + "]");
            }
        }

        private Transaction multi() throws JedisException {
            Transaction txn = this.j.multi();
            if (txn == null) {
                this.j.unwatch();
                this.fsm = FSM_STATE.ERROR;
                throw new JedisException("multi() call returns null transaction");
            }
            return txn;
        }

        public boolean tryCommit(String newState) throws JedisException {
            Response setResponse = this.txn.set(this.key, newState);
            List execResult = this.txn.exec();
            if (execResult == null) {
                this.fsm = FSM_STATE.CONFLICT;
                return false;
            }
            String setResponseVal = (String)setResponse.get();
            if (!"ok".equalsIgnoreCase(setResponseVal)) {
                this.fsm = FSM_STATE.ERROR;
                throw new JedisException("set (" + this.key + ") does not return 'ok'");
            }
            this.fsm = FSM_STATE.COMMITTED;
            return true;
        }

        public void close() throws JedisException {
            if (FSM_STATE.OPEN == this.fsm) {
                this.txn.discard();
                this.fsm = FSM_STATE.ABORTED;
            } else if (FSM_STATE.WATCHING == this.fsm) {
                this.j.unwatch();
                this.fsm = FSM_STATE.ABORTED;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static enum FSM_STATE {
            UNINITIALISED,
            WATCHING,
            OPEN,
            ABORTED,
            COMMITTED,
            CONFLICT,
            ERROR;

        }
    }
}

