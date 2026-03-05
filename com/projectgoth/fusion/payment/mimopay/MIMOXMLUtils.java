/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 */
package com.projectgoth.fusion.payment.mimopay;

import com.projectgoth.fusion.payment.mimopay.CreditReloadRequest;
import com.projectgoth.fusion.payment.mimopay.CreditReloadResponse;
import com.projectgoth.fusion.payment.mimopay.PendingStatusInquiryRequest;
import com.projectgoth.fusion.payment.mimopay.PendingStatusInquiryResponse;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MIMOXMLUtils {
    private static final JAXBContext JAXB_CTX_MIMO_DATA;
    private static final ThreadLocal<Unmarshaller> TL_UNMARSHALLER;
    private static final ThreadLocal<Marshaller> TL_MARSHALLER;

    public static String serializeToXML(Object dataObject) throws JAXBException {
        Marshaller marshaller = TL_MARSHALLER.get();
        if (marshaller == null) {
            marshaller = JAXB_CTX_MIMO_DATA.createMarshaller();
            TL_MARSHALLER.set(marshaller);
        }
        StringWriter sw = new StringWriter();
        try {
            marshaller.marshal(dataObject, (Writer)sw);
            return sw.toString();
        }
        catch (JAXBException e) {
            TL_MARSHALLER.set(null);
            throw e;
        }
    }

    public static <T> T deserializeFromXML(Reader reader, Class<T> declaredType) throws JAXBException {
        Unmarshaller unmarshaller = TL_UNMARSHALLER.get();
        if (unmarshaller == null) {
            unmarshaller = JAXB_CTX_MIMO_DATA.createUnmarshaller();
            TL_UNMARSHALLER.set(unmarshaller);
        }
        try {
            JAXBElement element = unmarshaller.unmarshal((Source)new StreamSource(reader), declaredType);
            if (element != null) {
                return (T)element.getValue();
            }
            throw new JAXBException("unmarshaller returns a null JAXBElement");
        }
        catch (JAXBException e) {
            TL_MARSHALLER.set(null);
            throw e;
        }
    }

    static {
        TL_UNMARSHALLER = new ThreadLocal();
        TL_MARSHALLER = new ThreadLocal();
        try {
            JAXB_CTX_MIMO_DATA = JAXBContext.newInstance((Class[])new Class[]{CreditReloadRequest.class, CreditReloadResponse.class, PendingStatusInquiryRequest.class, PendingStatusInquiryResponse.class});
        }
        catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

