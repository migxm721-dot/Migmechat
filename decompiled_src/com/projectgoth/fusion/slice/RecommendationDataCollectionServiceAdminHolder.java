/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Object
 *  IceInternal.Ex
 *  IceInternal.Patcher
 */
package com.projectgoth.fusion.slice;

import Ice.Object;
import IceInternal.Ex;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceAdmin;

public final class RecommendationDataCollectionServiceAdminHolder {
    public RecommendationDataCollectionServiceAdmin value;

    public RecommendationDataCollectionServiceAdminHolder() {
    }

    public RecommendationDataCollectionServiceAdminHolder(RecommendationDataCollectionServiceAdmin value) {
        this.value = value;
    }

    public Patcher getPatcher() {
        return new Patcher();
    }

    public class Patcher
    implements IceInternal.Patcher {
        public void patch(Object v) {
            try {
                RecommendationDataCollectionServiceAdminHolder.this.value = (RecommendationDataCollectionServiceAdmin)v;
            }
            catch (ClassCastException ex) {
                Ex.throwUOE((String)this.type(), (String)v.ice_id());
            }
        }

        public String type() {
            return "::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceAdmin";
        }
    }
}

