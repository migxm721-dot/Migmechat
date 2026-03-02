/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.keyczar.Crypter
 */
package com.projectgoth.fusion.dao;

import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.authentication.domain.PersistedCredential;
import java.util.List;
import org.keyczar.Crypter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface CredentialDAO {
    public PersistedCredential getCredential(int var1, PasswordType var2);

    public PersistedCredential getCredentialFromNewOrOldTable(int var1, PasswordType var2, Crypter var3);

    public Byte[] availableCredentialTypes(int var1);

    public List<PersistedCredential> getCredentialsByTypes(int var1, PasswordType[] var2);

    public List<PersistedCredential> getAllCredentials(int var1);

    public List<PersistedCredential> getAllCredentialsFromMaster(int var1);

    public List<PersistedCredential> getAllCredentialsFromOldTable(int var1);

    public int createCredential(PersistedCredential var1);

    public int[] createCredentials(List<PersistedCredential> var1);

    public int updateCredentialPassword(PersistedCredential var1);

    public int updateCredentialUsernameAndPassword(PersistedCredential var1);

    public int removeCredential(PersistedCredential var1);

    public int userIDForUsername(String var1);

    public boolean credentialExists(String var1, PasswordType var2);

    public List<PersistedCredential> getCredentialsByUsernameAndPasswordType(String var1, PasswordType var2);
}

