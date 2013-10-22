package org.hive2hive.core.test.network.messaging;

import org.hive2hive.core.network.messages.AcceptanceReply;
import org.hive2hive.core.network.messages.BaseMessage;
import org.hive2hive.core.test.network.data.TestDataWrapper;

/**
 * This test message is used to put locally some content into the target node where the location key is equals
 * the node id of receiver node. This behavior is used to check if this message is actually sent to the target
 * and executed there successfully.
 * 
 * @author Seppi
 * 
 */
public class TestMessageOneWay extends BaseMessage {

	private static final long serialVersionUID = 880089170139661640L;

	private final String contentKey;
	private final TestDataWrapper wrapper;

	public TestMessageOneWay(String targetKey, String contentKey,
			TestDataWrapper wrapper) {
		super(createMessageID(), targetKey);
		this.contentKey = contentKey;
		this.wrapper = wrapper;
	}

	@Override
	public void run() {
		networkManager.putLocal(networkManager.getNodeId(), contentKey, wrapper);
	}

	@Override
	public AcceptanceReply accept() {
		return AcceptanceReply.OK;
	}

}