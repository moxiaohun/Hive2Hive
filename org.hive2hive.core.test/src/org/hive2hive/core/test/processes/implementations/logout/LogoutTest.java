package org.hive2hive.core.test.processes.implementations.logout;

import java.io.IOException;
import java.util.List;

import net.tomp2p.futures.FutureGet;
import net.tomp2p.peers.Number160;

import org.apache.commons.io.FileUtils;
import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.model.Locations;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.core.processes.ProcessFactory;
import org.hive2hive.core.processes.framework.interfaces.IProcessComponent;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.core.test.H2HJUnitTest;
import org.hive2hive.core.test.network.NetworkTestUtil;
import org.hive2hive.core.test.processes.util.UseCaseTestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the logout procedure.
 * 
 * @author Christian
 * 
 */
public class LogoutTest extends H2HJUnitTest {

	private static final int networkSize = 3;
	private static List<NetworkManager> network;
	private static UserCredentials userCredentials;

	@BeforeClass
	public static void initTest() throws Exception {
		testClass = LogoutTest.class;
		beforeClass();

		network = NetworkTestUtil.createNetwork(networkSize);
		userCredentials = NetworkTestUtil.generateRandomCredentials();

		UseCaseTestUtil.registerAndLogin(userCredentials, network.get(0), FileUtils.getTempDirectory());
	}

	@Test
	public void testLogout() throws ClassNotFoundException, IOException, NoSessionException,
			NoPeerConnectionException {
		NetworkManager client = network.get(0);

		// verify the locations map before logout
		FutureGet futureGet = client.getDataManager().get(Number160.createHash(userCredentials.getUserId()),
				H2HConstants.TOMP2P_DEFAULT_KEY, Number160.createHash(H2HConstants.USER_LOCATIONS));
		futureGet.awaitUninterruptibly();
		futureGet.getFutureRequests().awaitUninterruptibly();
		Locations locations = (Locations) futureGet.getData().object();

		Assert.assertEquals(1, locations.getPeerAddresses().size());

		// logout
		IProcessComponent process = ProcessFactory.instance().createLogoutProcess(client);
		UseCaseTestUtil.executeProcess(process);

		// verify the locations map after logout
		FutureGet futureGet2 = client.getDataManager().get(Number160.createHash(userCredentials.getUserId()),
				H2HConstants.TOMP2P_DEFAULT_KEY, Number160.createHash(H2HConstants.USER_LOCATIONS));
		futureGet2.awaitUninterruptibly();
		futureGet2.getFutureRequests().awaitUninterruptibly();
		Locations locations2 = (Locations) futureGet2.getData().object();

		Assert.assertEquals(0, locations2.getPeerAddresses().size());
	}

	@AfterClass
	public static void endTest() {
		NetworkTestUtil.shutdownNetwork(network);
		afterClass();
	}
}