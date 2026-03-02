/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.Communicator;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.fdl.enums.PacketType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataUploadAddressBookContacts;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.PacketUtils;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.recommendation.collector.CollectedDataTypeEnum;
import com.projectgoth.fusion.recommendation.collector.DataCollectorException;
import com.projectgoth.fusion.recommendation.collector.DataCollectorUtils;
import com.projectgoth.fusion.recommendation.collector.DataUploadTicket;
import com.projectgoth.fusion.slice.CollectedAddressBookDataIce;
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServicePrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class FusionPktUploadAddressBookContacts
extends FusionPktDataUploadAddressBookContacts {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktUploadAddressBookContacts.class));
    private static final CollectedDataTypeEnum EXPECTED_DATA_TYPE = CollectedDataTypeEnum.ADDRESSBOOKCONTACT;
    private static final long EXPECTED_COLLECTED_DATA_TYPE_CODE = EXPECTED_DATA_TYPE.getCode();

    public FusionPktUploadAddressBookContacts(short transactionId) {
        super(transactionId);
    }

    public FusionPktUploadAddressBookContacts(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktUploadAddressBookContacts(FusionPacket packet) {
        super(packet);
    }

    public Gateway.ThreadPoolName getThreadPool() {
        return Gateway.ThreadPoolName.DATA_UPLOAD;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        return FusionPktUploadAddressBookContacts.processRequest(connection.getGatewayContext().getCommunicator(), connection.getUserID(), connection.getSessionID(), this.getTransactionId(), this.getDataType(), this.getDataList(), this.getTicketId());
    }

    public static FusionPacket[] processRequest(Communicator communicator, int userid, String sessionid, short transactionID, FusionPktDataUploadAddressBookContacts.DataType contactType, String[] contactValues, String ticketID) {
        long now = System.currentTimeMillis();
        if (communicator == null) {
            return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.ERROR, DataCollectorUtils.newErrorID(), transactionID, "Invalid communication state").setDetailedErrorMessage("'communicator' is null")).toArray();
        }
        if (sessionid == null) {
            return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.ERROR, DataCollectorUtils.newErrorID(), transactionID, "Invalid communication state").setDetailedErrorMessage("'sessionid' is null")).toArray();
        }
        if (StringUtil.isBlank(ticketID)) {
            return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("'ticketID' is blank")).toArray();
        }
        if (contactType == null) {
            return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("'contactType' is null")).toArray();
        }
        if (contactValues == null) {
            return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("'contactValues' is null")).toArray();
        }
        try {
            DataCollectorUtils.hitUploadDataRateLimit(userid, EXPECTED_DATA_TYPE);
            DataCollectorUtils.checkUserAccess(userid, EXPECTED_DATA_TYPE);
            DataUploadTicket uploadTicket = DataCollectorUtils.decryptDataUploadTicket(ticketID, sessionid, CollectedDataTypeEnum.ADDRESSBOOKCONTACT);
            if ((long)uploadTicket.dataType != EXPECTED_COLLECTED_DATA_TYPE_CODE) {
                return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("upload ticket was meant for uploading data type:[" + uploadTicket.dataType + "].Expected:[" + EXPECTED_COLLECTED_DATA_TYPE_CODE + "]")).toArray();
            }
            if (uploadTicket.userid != userid) {
                return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("upload ticket was meant for userid:[" + uploadTicket.userid + "].Current userid:[" + userid + "]")).toArray();
            }
            long timeDiff = now - uploadTicket.requestTimestamp;
            if (timeDiff >= 0L) {
                long maxTicketAgeMSec = 1000L * SystemProperty.getLong(SystemPropertyEntities.RecommendationServiceSettings.UPLOAD_TICKET_EXPIRY_SECS.forCollectedDataType(EXPECTED_DATA_TYPE));
                if (timeDiff > maxTicketAgeMSec) {
                    return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("Ticket expired.Configured max ticket age:[" + maxTicketAgeMSec + "] millisec.System timestamp:[" + now + "].Upload ticket request timestamp:[" + uploadTicket.requestTimestamp + "]")).toArray();
                }
            } else {
                return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("Invalid ticket timestamp or system time inaccurate.System timestamp:[" + now + "] Upload ticket request timestamp:[" + uploadTicket.requestTimestamp + "]")).toArray();
            }
            int maxNumberOfEntriesPerUpload = SystemProperty.getInt(SystemPropertyEntities.RecommendationServiceSettings.MAX_NUMBER_OF_ENTRIES_PER_UPLOAD.forCollectedDataType(CollectedDataTypeEnum.ADDRESSBOOKCONTACT));
            if (contactValues.length > maxNumberOfEntriesPerUpload) {
                return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("Number of uploaded contacts is [" + contactValues.length + "], which exceeds [" + maxNumberOfEntriesPerUpload + "]")).toArray();
            }
            long maxTotalEntrySizesPerUpload = SystemProperty.getLong(SystemPropertyEntities.RecommendationServiceSettings.MAX_TOTAL_ENTRY_SIZES_PER_UPLOAD.forCollectedDataType(CollectedDataTypeEnum.ADDRESSBOOKCONTACT));
            long totalEntrySizesUploaded = DataCollectorUtils.sumStringLength(contactValues);
            if (totalEntrySizesUploaded > maxTotalEntrySizesPerUpload) {
                return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionID, "Invalid input").setDetailedErrorMessage("Total entry sizes uploaded is [" + totalEntrySizesUploaded + "], which exceeds [" + maxTotalEntrySizesPerUpload + "]")).toArray();
            }
            CollectedAddressBookDataIce collectedAddrBook = new CollectedAddressBookDataIce();
            collectedAddrBook.contactType = contactType.value();
            collectedAddrBook.contactValues = contactValues;
            collectedAddrBook.createTimestamp = now;
            collectedAddrBook.dataType = uploadTicket.dataType;
            collectedAddrBook.submitterUserId = userid;
            RecommendationDataCollectionServicePrx rdcsPrx = DataCollectorUtils.getRDCSProxy(communicator);
            rdcsPrx.logData(collectedAddrBook);
            return new FusionPktOk(transactionID).toArray();
        }
        catch (DataCollectorException ex) {
            ErrorCause errorCause = ex.getErrorCause();
            if (errorCause instanceof ErrorCause.DataCollectorErrorReasonType) {
                ErrorCause.DataCollectorErrorReasonType dataCollectorReason = (ErrorCause.DataCollectorErrorReasonType)errorCause;
                return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, dataCollectorReason.getSeverityLevel(), DataCollectorUtils.newErrorID(), transactionID, "Unable to upload data").setExceptionCause(ex)).toArray();
            }
            return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.ERROR, DataCollectorUtils.newErrorID(), transactionID, "Unable to upload data").setExceptionCause(ex)).toArray();
        }
        catch (FusionExceptionWithRefCode ex) {
            String errorRef = false == StringUtil.isBlank(ex.errorRef) ? ex.errorRef : DataCollectorUtils.newErrorID();
            return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.ERROR, errorRef, transactionID, "Failed to upload data").setExceptionCause((Throwable)((Object)ex))).toArray();
        }
        catch (Exception ex) {
            return PacketUtils.logAndPrepareFusionPktError(new PacketUtils.ErrorLogData(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, log, Level.ERROR, DataCollectorUtils.newErrorID(), transactionID, "Failed to upload data").setExceptionCause(ex)).toArray();
        }
    }
}

