package org.hive2hive.core.process.util;

import org.hive2hive.core.model.FileTreeNode;
import org.hive2hive.core.process.Process;

/**
 * Additionally holds a {@link FileTreeNode}
 * 
 * @author Nico
 * 
 */
public class NodeProcessTreeNode extends ProcessTreeNode {

	private final FileTreeNode node;

	public NodeProcessTreeNode(Process process, ProcessTreeNode parent, FileTreeNode node) {
		super(process, parent);
		this.node = node;
	}

	public NodeProcessTreeNode() {
		super();
		this.node = null;
	}

	public FileTreeNode getNode() {
		return node;
	}
}