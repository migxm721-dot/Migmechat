/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.movedeletedcontacts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MoveDeletedContacts {
    private static final String APP_NAME = "Move Deleted Contacts";
    static String dbHost;
    static String dbUsername;
    static String dbPassword;
    static Connection conn;
    static PreparedStatement psContactsToMove;
    static PreparedStatement psUpdatePhoneCall;
    static PreparedStatement psUpdateMessageDestination;
    static PreparedStatement psCopyContact;
    static PreparedStatement psDeleteContact;
    static ResultSet rs;
    static Vector<Integer> contactIDs;
    static int startContactId;
    static int endContactId;
    static int numContactsMoved;
    static SimpleDateFormat dateFormatter;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static void main(String[] args) {
        block73: {
            System.out.println("Move Deleted Contacts version @version@\nCopyright (c) 2005-2008 Project Goth Inc. All rights reserved");
            if (args.length != 5) {
                System.out.print("Usage: MoveDeletedContacts [Master Database Host] [Database Username] [Database Password] [Start Contact.ID (optional)] [End Contact.ID (optional)]");
                return;
            }
            System.out.println("Connecting to DB on host " + args[0] + " as " + args[1] + "/" + args[2]);
            String userName = args[1];
            String password = args[2];
            String url = "jdbc:mysql://" + args[0] + "/fusion";
            try {
                startContactId = Integer.parseInt(args[3]);
                endContactId = Integer.parseInt(args[4]);
            }
            catch (NumberFormatException e) {
                System.out.print("Error: Start and End Contact.IDs must be numeric");
                return;
            }
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection(url, userName, password);
            }
            catch (Exception e) {
                System.out.println("Unable to connect to DB. Exception: " + e.getMessage());
                return;
            }
            System.out.println(dateFormatter.format(new Date()) + " Starting process to move deleted contacts with IDs between " + startContactId + " and " + endContactId);
            psUpdatePhoneCall = conn.prepareStatement("UPDATE PhoneCall SET ContactID = NULL WHERE ContactID = ?");
            psUpdateMessageDestination = conn.prepareStatement("UPDATE MessageDestination SET ContactID = NULL WHERE ContactID = ?");
            psCopyContact = conn.prepareStatement("INSERT INTO ContactDeleted SELECT * FROM Contact WHERE ID = ?");
            psDeleteContact = conn.prepareStatement("DELETE FROM Contact WHERE ID = ?");
            do {
                conn.setAutoCommit(true);
                psContactsToMove = conn.prepareStatement("SELECT ID FROM Contact WHERE ID >= ? AND ID <= ? AND Status = 0");
                psContactsToMove.setInt(1, startContactId);
                System.out.print(dateFormatter.format(new Date()) + " Moving deleted contacts with IDs between " + startContactId + " and ");
                if ((startContactId += 10000) > endContactId) {
                    startContactId = endContactId;
                }
                System.out.println(startContactId + " (have moved " + numContactsMoved + " so far)");
                psContactsToMove.setInt(2, startContactId);
                rs = psContactsToMove.executeQuery();
                contactIDs.clear();
                while (rs.next()) {
                    contactIDs.add(rs.getInt(1));
                }
                rs.close();
                psContactsToMove.close();
                conn.setAutoCommit(false);
                for (int contactId : contactIDs) {
                    psUpdatePhoneCall.setInt(1, contactId);
                    psUpdatePhoneCall.executeUpdate();
                    psUpdateMessageDestination.setInt(1, contactId);
                    psUpdateMessageDestination.executeUpdate();
                    psCopyContact.setInt(1, contactId);
                    if (psCopyContact.executeUpdate() != 1) {
                        System.out.println("Unable to copy contact with ID " + contactId);
                        conn.rollback();
                        continue;
                    }
                    psDeleteContact.setInt(1, contactId);
                    if (psDeleteContact.executeUpdate() != 1) {
                        System.out.println("Unable to delete contact with ID " + contactId);
                        conn.rollback();
                        continue;
                    }
                    conn.commit();
                    ++numContactsMoved;
                }
            } while (startContactId < endContactId);
            Object var7_10 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (psContactsToMove != null) {
                    psContactsToMove.close();
                }
            }
            catch (SQLException e2) {
                psContactsToMove = null;
            }
            try {
                if (psUpdatePhoneCall != null) {
                    psUpdatePhoneCall.close();
                }
            }
            catch (SQLException e2) {
                psUpdatePhoneCall = null;
            }
            try {
                if (psUpdateMessageDestination != null) {
                    psUpdateMessageDestination.close();
                }
            }
            catch (SQLException e2) {
                psUpdateMessageDestination = null;
            }
            try {
                if (psCopyContact != null) {
                    psCopyContact.close();
                }
            }
            catch (SQLException e2) {
                psCopyContact = null;
            }
            try {
                if (psDeleteContact != null) {
                    psDeleteContact.close();
                }
            }
            catch (SQLException e2) {
                psDeleteContact = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block73;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block73;
            {
                catch (Exception e) {
                    try {
                        if (conn != null) {
                            conn.rollback();
                        }
                    }
                    catch (SQLException e2) {
                        // empty catch block
                    }
                    System.out.println("An exception occurred:");
                    e.printStackTrace();
                    Object var7_11 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        rs = null;
                    }
                    try {
                        if (psContactsToMove != null) {
                            psContactsToMove.close();
                        }
                    }
                    catch (SQLException e2) {
                        psContactsToMove = null;
                    }
                    try {
                        if (psUpdatePhoneCall != null) {
                            psUpdatePhoneCall.close();
                        }
                    }
                    catch (SQLException e2) {
                        psUpdatePhoneCall = null;
                    }
                    try {
                        if (psUpdateMessageDestination != null) {
                            psUpdateMessageDestination.close();
                        }
                    }
                    catch (SQLException e2) {
                        psUpdateMessageDestination = null;
                    }
                    try {
                        if (psCopyContact != null) {
                            psCopyContact.close();
                        }
                    }
                    catch (SQLException e2) {
                        psCopyContact = null;
                    }
                    try {
                        if (psDeleteContact != null) {
                            psDeleteContact.close();
                        }
                    }
                    catch (SQLException e2) {
                        psDeleteContact = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    return;
                }
            }
            catch (Throwable throwable) {
                Object var7_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (psContactsToMove != null) {
                        psContactsToMove.close();
                    }
                }
                catch (SQLException e2) {
                    psContactsToMove = null;
                }
                try {
                    if (psUpdatePhoneCall != null) {
                        psUpdatePhoneCall.close();
                    }
                }
                catch (SQLException e2) {
                    psUpdatePhoneCall = null;
                }
                try {
                    if (psUpdateMessageDestination != null) {
                        psUpdateMessageDestination.close();
                    }
                }
                catch (SQLException e2) {
                    psUpdateMessageDestination = null;
                }
                try {
                    if (psCopyContact != null) {
                        psCopyContact.close();
                    }
                }
                catch (SQLException e2) {
                    psCopyContact = null;
                }
                try {
                    if (psDeleteContact != null) {
                        psDeleteContact.close();
                    }
                }
                catch (SQLException e2) {
                    psDeleteContact = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
        System.out.println("Successfully moved " + numContactsMoved + " deleted contacts");
    }

    static {
        conn = null;
        psContactsToMove = null;
        psUpdatePhoneCall = null;
        psUpdateMessageDestination = null;
        psCopyContact = null;
        psDeleteContact = null;
        rs = null;
        contactIDs = new Vector();
        numContactsMoved = 0;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}

