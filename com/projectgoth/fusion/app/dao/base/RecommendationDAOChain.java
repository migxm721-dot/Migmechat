/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.base;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.DAOChain;
import com.projectgoth.fusion.recommendation.RecommendationTransform;
import com.projectgoth.fusion.recommendation.RecommendationTransformParam;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RecommendationDAOChain
implements DAOChain {
    private RecommendationDAOChain nextRead;
    private RecommendationDAOChain nextWrite;

    @Override
    public void setNextRead(DAOChain a) {
        this.nextRead = (RecommendationDAOChain)a;
    }

    @Override
    public void setNextWrite(DAOChain a) {
        this.nextWrite = (RecommendationDAOChain)a;
    }

    public Map<String, RecommendationTransformParam> getRecommendationTransformParamsByName(int transformID) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getRecommendationTransformParamsByName(transformID);
        }
        throw new DAOException("Unable to getRecommendationTransformParams");
    }

    public List<RecommendationTransformParam> getRecommendationTransformParams(int transformID) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getRecommendationTransformParams(transformID);
        }
        throw new DAOException("Unable to getRecommendationTransformParams");
    }

    public RecommendationTransform getRecommendationTransform(int transformID) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getRecommendationTransform(transformID);
        }
        throw new DAOException("Unable to getRecommendationTransform");
    }

    public List<RecommendationTransform> getActiveRecommendationTransforms() throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getActiveRecommendationTransforms();
        }
        throw new DAOException("Unable to getRecommendationTransforms");
    }
}

