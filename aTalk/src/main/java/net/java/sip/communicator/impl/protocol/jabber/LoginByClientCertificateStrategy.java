/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license. See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.protocol.jabber;

import net.java.sip.communicator.service.certificate.CertificateService;
import net.java.sip.communicator.service.protocol.*;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.sasl.provided.SASLExternalMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.parts.Resourcepart;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import timber.log.Timber;

/**
 * Login to Jabber using a client certificate (as defined in the account configuration)
 *
 * @author Stefan Sieber
 * @author Eng Chong Meng
 */
class LoginByClientCertificateStrategy implements JabberLoginStrategy
{
    private AccountID accountID;

    /**
     * Creates a new instance of this class.
     *
     * @param accountID The account to use for the strategy.
     */
    public LoginByClientCertificateStrategy(AccountID accountID)
    {
        this.accountID = accountID;
    }

    /**
     * Does nothing.
     *
     * @param authority unused
     * @param reasonCode unused
     * @return always <tt>null</tt>
     */
    public UserCredentials prepareLogin(SecurityAuthority authority, int reasonCode, String reason, Boolean isShowAlways)
    {
        // password is retrieved later when opening the key store.
        return null;
    }

    /**
     * Does nothing.
     *
     * @return always <tt>true</tt>
     */
    public boolean loginPreparationSuccessful()
    {
        return true;
    }

    /**
     * Always true as the authentication occurs with the TLS client certificate.
     *
     * @return always <tt>true</tt>
     */
    public boolean isTlsRequired()
    {
        return true;
    }

    /**
     * Creates the SSLContext for the XMPP connection configured with a customized TrustManager and
     * a KeyManager based on the selected client certificate.
     *
     * @param certificateService certificate service to retrieve the SSL context
     * @param trustManager Trust manager to use for the context
     * @return Configured and initialized SSL Context
     * @throws GeneralSecurityException
     */
    public SSLContext createSslContext(CertificateService certificateService, X509TrustManager trustManager)
            throws GeneralSecurityException
    {
        String certConfigName = accountID.getAccountPropertyString(ProtocolProviderFactory.CLIENT_TLS_CERTIFICATE);
        return certificateService.getSSLContext(certConfigName, trustManager);
    }

    /**
     * Performs the login on the XMPP connection using the SASL EXTERNAL mechanism.
     *
     * @param connection The connection on which the login is performed.
     * @param userName The username for the login.
     * @param resource The XMPP resource.
     * @return true when the login succeeded, false when the certificate wasn't accepted.
     * @throws XMPPException
     */
    public boolean login(XMPPTCPConnection connection, String userName, Resourcepart resource)
            throws XMPPException, SmackException
    {
        SASLAuthentication.registerSASLMechanism(new SASLExternalMechanism());

        // user/password MUST be empty. In fact they shouldn't be
        // necessary at all because the user name is derived from the client certificate.
        try {
            try {
                connection.login("", "", resource);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } catch (XMPPException | SmackException ex) {
            if (ex.getMessage().contains("EXTERNAL failed: not-authorized")) {
                Timber.e(ex, "Certificate login failed");
                return false;
            }
            throw ex;
        }
    }

    @Override
    public boolean registerAccount(ProtocolProviderServiceJabberImpl pps, AccountID accountId)
            throws XMPPException, SmackException
    {
        return false;
    }
}
