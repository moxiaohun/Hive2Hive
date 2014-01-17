package org.hive2hive.core.test.process.notify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tomp2p.futures.FutureGet;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.core.network.messages.direct.BaseDirectMessage;
import org.hive2hive.core.network.userprofiletask.UserProfileTask;
import org.hive2hive.core.process.notify.BaseNotificationMessageFactory;
import org.hive2hive.core.test.H2HTestData;
import org.hive2hive.core.test.network.NetworkTestUtil;
import org.junit.Assert;

/**
 * Simple message factory for testing. It counts the received messages by checking whether the
 * {@link TestDirectNotificationMessage} has put the ordered content (indicating that it arrived)
 * 
 * @author Nico
 * 
 */
public class CountingNotificationMessageFactory extends BaseNotificationMessageFactory {

	private final NetworkManager sender;
	private final List<String> testContentKeys;
	private final H2HTestData data = new H2HTestData(NetworkTestUtil.randomString());

	public CountingNotificationMessageFactory(NetworkManager sender) {
		this.sender = sender;
		testContentKeys = new ArrayList<String>();
	}

	public boolean allMsgsArrived() {
		return getSentMessageCount() == getArrivedMessageCount();
	}

	public int getSentMessageCount() {
		return testContentKeys.size();
	}

	public int getArrivedMessageCount() {
		int counter = 0;
		for (String contentKey : testContentKeys) {
			FutureGet futureGet = sender.getDataManager().get(Number160.createHash(sender.getNodeId()),
					H2HConstants.TOMP2P_DEFAULT_KEY, Number160.createHash(contentKey));
			futureGet.awaitUninterruptibly();
			if (futureGet.getData() == null) {
				continue;
			}

			try {
				H2HTestData gotData = (H2HTestData) futureGet.getData().object();
				if (gotData.getTestString().equalsIgnoreCase(data.getTestString())) {
					counter++;
				}
			} catch (ClassNotFoundException | IOException e) {
				Assert.fail();
			}
		}

		return counter;
	}

	@Override
	public BaseDirectMessage createPrivateNotificationMessage(PeerAddress receiver) {
		String contentKey = NetworkTestUtil.randomString();
		testContentKeys.add(contentKey);
		return new TestDirectNotificationMessage(receiver, sender.getNodeId(), contentKey, data);
	}

	@Override
	public UserProfileTask createUserProfileTask() {
		// TODO
		return null;
	}

}
