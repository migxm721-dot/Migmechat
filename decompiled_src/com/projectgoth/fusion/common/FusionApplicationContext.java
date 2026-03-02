/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionApplicationContext {
    private Semaphore semaphore = new Semaphore(1);

    protected FusionApplicationContext() {
        this.semaphore.acquireUninterruptibly();
    }

    protected <T> T extractProperty(WeakReference<T> reference) {
        if (null == reference) {
            try {
                this.semaphore.acquire();
            }
            catch (InterruptedException e) {
                return null;
            }
        }
        return reference.get();
    }

    public void build() {
        this.semaphore.release();
    }
}

