/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector.addressbook;

import com.projectgoth.fusion.recommendation.collector.CollectedDataTypeEnum;
import com.projectgoth.fusion.recommendation.collector.CollectorTransformationException;
import com.projectgoth.fusion.recommendation.collector.ICollectorTransformation;
import com.projectgoth.fusion.recommendation.collector.addressbook.AddressBookRecordData;
import com.projectgoth.fusion.slice.CollectedAddressBookDataIce;
import com.projectgoth.fusion.slice.CollectedDataIce;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CollectedAddressBookDataTransformer
implements ICollectorTransformation<AddressBookRecordData> {
    private final String name;

    public CollectedAddressBookDataTransformer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<AddressBookRecordData> toLoggables(CollectedDataIce dataIce) throws CollectorTransformationException {
        if (dataIce.dataType == CollectedDataTypeEnum.ADDRESSBOOKCONTACT.getCode()) {
            CollectedAddressBookDataIce collectedAddrBookDataIce = (CollectedAddressBookDataIce)dataIce;
            if (collectedAddrBookDataIce.contactValues == null) {
                throw new CollectorTransformationException("Null contact values");
            }
            ArrayList<AddressBookRecordData> addressBookRecordDataList = new ArrayList<AddressBookRecordData>(collectedAddrBookDataIce.contactValues.length);
            for (String contactValue : collectedAddrBookDataIce.contactValues) {
                AddressBookRecordData addressBookRecordData = new AddressBookRecordData();
                addressBookRecordData.contactType = collectedAddrBookDataIce.contactType;
                addressBookRecordData.contactValue = contactValue;
                addressBookRecordData.submitterUserID = collectedAddrBookDataIce.submitterUserId;
                addressBookRecordData.createDate = new Date(collectedAddrBookDataIce.createTimestamp);
                addressBookRecordDataList.add(addressBookRecordData);
            }
            return addressBookRecordDataList;
        }
        throw new CollectorTransformationException("Don't understand dataIce.type " + dataIce.dataType);
    }
}

