/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class ScoreAndLevel
implements Cloneable,
Serializable {
    public int score;
    public int level;

    public ScoreAndLevel() {
    }

    public ScoreAndLevel(int score, int level) {
        this.score = score;
        this.level = level;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        ScoreAndLevel _r = null;
        try {
            _r = (ScoreAndLevel)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.score != _r.score) {
                return false;
            }
            return this.level == _r.level;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + this.score;
        __h = 5 * __h + this.level;
        return __h;
    }

    public Object clone() {
        Object o;
        block2: {
            o = null;
            try {
                o = super.clone();
            }
            catch (CloneNotSupportedException ex) {
                if ($assertionsDisabled) break block2;
                throw new AssertionError();
            }
        }
        return o;
    }

    public void __write(BasicStream __os) {
        __os.writeInt(this.score);
        __os.writeInt(this.level);
    }

    public void __read(BasicStream __is) {
        this.score = __is.readInt();
        this.level = __is.readInt();
    }
}

