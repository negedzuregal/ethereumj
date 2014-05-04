package org.ethereum.net.message;

import java.util.ArrayList;
import java.util.List;

import static org.ethereum.net.Command.BLOCKS;

import org.ethereum.net.Command;
import org.ethereum.net.rlp.RLPItem;
import org.ethereum.net.rlp.RLPList;
import org.ethereum.net.vo.BlockData;
import org.ethereum.net.vo.TransactionData;

/**
 * www.ethereumJ.com
 * User: Roman Mandeleil
 * Created on: 06/04/14 14:56
 */
public class BlocksMessage extends Message {

    private List<BlockData> blockDataList = new ArrayList<BlockData>();

    public BlocksMessage(RLPList rawData) {
        super(rawData);
    }

    public void parseRLP() {

        RLPList paramsList = (RLPList) rawData.getElement(0);

        if ( Command.fromInt(((RLPItem)(paramsList).getElement(0)).getData()[0]) != BLOCKS){
            throw new Error("BlocksMessage: parsing for mal data");
        }

        for (int i = 1; i < paramsList.size(); ++i){
            RLPList rlpData = ((RLPList)paramsList.getElement(i));
            BlockData blockData = new BlockData(rlpData);
            this.blockDataList.add(blockData);
        }
        parsed = true;
    }

    @Override
    public byte[] getPayload() {
        return null;
    }

    public List<BlockData> getBlockDataList() {
        if (!parsed) parseRLP();
        return blockDataList;
    }

	public String toString() {

        StringBuffer sb = new StringBuffer();
        for (BlockData blockData : this.getBlockDataList()){
            sb.append("   ").append( blockData.toString() ).append("\n");

            List<TransactionData> transactions = blockData.getTransactionsList();
            for (TransactionData transactionData : transactions){
                sb.append("[").append(transactionData).append("]\n");
            }
        }

        return "Blocks Message [\n" +
                  sb.toString()
                + " ]";
    }
}