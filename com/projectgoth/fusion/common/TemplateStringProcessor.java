/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.projectgoth.fusion.common;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TemplateStringProcessor {
    private static final char VARIABLE_MARKER = '#';
    private static final int REQUIRED_MARKER_LENGTH = 2;

    private static String getValue(StringBuilder variableNameContainer, Map<String, String> parameters) {
        String paramName;
        String rawParamName = variableNameContainer.substring(2, variableNameContainer.length() - 2);
        boolean doHTMLEscape = false;
        if (rawParamName.length() > 0) {
            if (rawParamName.startsWith("^")) {
                paramName = rawParamName.substring(1);
                doHTMLEscape = true;
            } else {
                paramName = rawParamName;
            }
        } else {
            paramName = rawParamName;
        }
        if (parameters.containsKey(paramName)) {
            String paramValue = parameters.get(paramName);
            if (doHTMLEscape) {
                return StringEscapeUtils.escapeHtml3((String)paramValue);
            }
            return paramValue;
        }
        return variableNameContainer.toString();
    }

    public static void process(Writer outputWriter, String templateString, Map<String, String> parameters) throws IOException {
        if (templateString != null) {
            int receivedMarkerCount = 0;
            boolean variablePlaceHolderHasStarted = false;
            StringBuilder variableNameContainer = new StringBuilder();
            int templateStringLen = templateString.length();
            block3: for (int i = 0; i < templateStringLen; ++i) {
                char c = templateString.charAt(i);
                switch (c) {
                    case '#': {
                        variableNameContainer.append(c);
                        if (++receivedMarkerCount != 2) continue block3;
                        receivedMarkerCount = 0;
                        if (variablePlaceHolderHasStarted) {
                            variablePlaceHolderHasStarted = false;
                            String paramValue = TemplateStringProcessor.getValue(variableNameContainer, parameters);
                            variableNameContainer = new StringBuilder();
                            if (paramValue == null) continue block3;
                            outputWriter.write(paramValue.toString());
                            continue block3;
                        }
                        variablePlaceHolderHasStarted = true;
                        continue block3;
                    }
                    default: {
                        if (variablePlaceHolderHasStarted) {
                            receivedMarkerCount = 0;
                            variableNameContainer.append(c);
                            continue block3;
                        }
                        if (receivedMarkerCount > 0) {
                            receivedMarkerCount = 0;
                            outputWriter.append(variableNameContainer);
                            variableNameContainer = new StringBuilder();
                        }
                        outputWriter.append(c);
                    }
                }
            }
            outputWriter.append(variableNameContainer);
        }
    }

    public static String process(String templateString, Map<String, String> parameters) throws IOException {
        StringWriter sw = new StringWriter();
        TemplateStringProcessor.process(sw, templateString, parameters);
        return sw.toString();
    }
}

