/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 * 
pHideExtendedAwayStatus * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.java.sip.communicator.impl.protocol.jabber;

import net.java.sip.communicator.service.certificate.CertificateService;
import net.java.sip.communicator.service.protocol.*;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.parts.Resourcepart;

import java.security.GeneralSecurityException;

import javax.net.ssl.*;

/**
 * Implements anonymous login strategy for the purpose of some server side technologies. This makes
 * not much sense to be used with Jitsi directly.
 *
 * @see JabberAccountIDImpl#ANONYMOUS_AUTH
 *
 * @author Pawel Domas
 */
public class AnonymousLoginStrategy implements JabberLoginStrategy
{
	/**
	 * <tt>UserCredentials</tt> used by accompanying services.
	 */
	private final UserCredentials credentials;

	/**
	 * Creates new anonymous login strategy instance.
	 * 
	 * @param login
	 *        user login only for the purpose of returning <tt>UserCredentials</tt> that are used by
	 *        accompanying services.
	 */
	public AnonymousLoginStrategy(String login)
	{
		this.credentials = new UserCredentials();

		credentials.setUserName(login);

		// FIXME: consider including password for TURN authentication ?
		credentials.setPassword(new char[] {});
	}

	@Override
	public UserCredentials prepareLogin(SecurityAuthority authority, int reasonCode, String reason,
			Boolean isShowAlways)
	{
		return credentials;
	}

	@Override
	public boolean loginPreparationSuccessful()
	{
		return true;
	}

	@Override
	public boolean login(XMPPTCPConnection connection, String userName, Resourcepart resource)
		throws XMPPException, SmackException
	{
		// connection.loginAnonymously();
		return true;
	}

	@Override
	public boolean registerAccount(ProtocolProviderServiceJabberImpl pps, AccountID accountId)
			throws XMPPException, SmackException {
		return true;
	}

	@Override
	public boolean isTlsRequired()
	{
		return false;
	}

	@Override
	public SSLContext createSslContext(CertificateService certificateService,
		X509TrustManager trustManager)
		throws GeneralSecurityException
	{
        return certificateService.getSSLContext(trustManager);
	}
}
