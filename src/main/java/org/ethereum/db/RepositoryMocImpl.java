package org.ethereum.db;

import org.ethereum.trie.TrackTrie;
import org.ethereum.trie.Trie;

public class RepositoryMocImpl extends Repository {
	public RepositoryMocImpl() {
		super("blockchainMoc", "detailsMoc", "stateMoc");
    }
}
