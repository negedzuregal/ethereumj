package org.ethereum.trie;

import java.util.ArrayList;
import java.util.List;

import org.ethereum.util.Value;

import static org.ethereum.util.CompactEncoder.unpackToNibbles;

/*
 * www.ethereumJ.com
 * @author: Nick Savers
 * Created on: 20/05/2014 10:44
 */
public class AccountIterator {
	
	private Trie trie;
	
	/**
	 * account addresses as nibbles
	 */
	private List<byte[]> accounts;
	private int iteratorIndex;

	public AccountIterator(Trie t) {
		this.trie = t;
		
		accounts = new ArrayList<byte[]>();
		
//		if (this.trie.getRoot() == "") {
//			return null;
//		}
		
		this.getNode(new Value(this.trie.getRoot()).asBytes(), null);
		
		iteratorIndex = 0;
	}

	// Some time in the near future this will need refactoring :-)
	// XXX Note to self, IsSlice == inline node. Str == sha3 to node
	private void workNode(Value currentNode, byte[] address) {
		if (currentNode.length() == 2) {
			byte[] k = unpackToNibbles(currentNode.get(0).asBytes());
			byte[] nibbles = this.concatenateNibbles(address, k);
			
			if (currentNode.get(1).asString() == "") {
				this.workNode(currentNode.get(1), nibbles);
			} else {
				if (k[k.length-1] == 16) {
					//this.values.add( currentNode.get(1).asString() );
					accounts.add(nibbles);
				} else {
					this.getNode(currentNode.get(1).asBytes(), nibbles);
				}
			}
		} else {
			for (int i = 0; i < currentNode.length(); i++) {
				if (i == 16 && currentNode.get(i).length() != 0) {
					//this.values.add( currentNode.get(i).asString() );
					accounts.add(address);
				} else {
					byte[] k = new byte[] { (byte)i };
					byte[] nibbles = this.concatenateNibbles(address, k);
					
					if (currentNode.get(i).asString() == "") {
						this.workNode(currentNode.get(i), nibbles);
					} else {
						String val = currentNode.get(i).asString();
						if (val != "") {
							//accounts.add(currentNode.get(1).asBytes());
							this.getNode(currentNode.get(i).asBytes(), nibbles);//val.getBytes());
						}
					}
				}
			}
		}
	}

	private void getNode(byte[] node, byte[] address) {
		Value currentNode = this.trie.getCache().get(node);
		this.workNode(currentNode, address);
	}
	
	private byte[] concatenateNibbles(byte[] a, byte[] b) {
		if(a == null)
			return b;
		
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	 //########################
	 //
	 // 		API
	 // 
	 //########################
	
	public boolean hasNext() {
		return accounts.size() > this.iteratorIndex ? true:false;
	}
	
	public byte[] next() {
		if(hasNext()) {
			this.iteratorIndex ++;
			return accounts.get(iteratorIndex -1);
		}
		
		return null;
	}
	
	public int size() {
		return accounts.size();
	}
}
