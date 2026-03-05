/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.support.ClassPathXmlApplicationContext
 */
package com.projectgoth.fusion.authentication;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AuthenticationServiceContext {
    private static ApplicationContext context = new ClassPathXmlApplicationContext("classpath:com/projectgoth/fusion/authentication/applicationContext-authenticationService.xml");

    public static ApplicationContext getContext() {
        return context;
    }
}

