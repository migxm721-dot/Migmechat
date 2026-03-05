/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.support.ClassPathXmlApplicationContext
 */
package com.projectgoth.fusion.reputation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReputationServiceContext {
    private static ApplicationContext context = new ClassPathXmlApplicationContext("classpath:com/projectgoth/fusion/reputation/applicationContext-reputationService.xml");

    public static ApplicationContext getContext() {
        return context;
    }
}

