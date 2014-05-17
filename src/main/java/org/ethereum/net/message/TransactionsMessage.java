package org.ethereum.net.message;

import static org.ethereum.net.Command.TRANSACTIONS;

import org.ethereum.core.Transaction;
import org.ethereum.net.Command;
import org.ethereum.util.RLPItem;
import org.ethereum.util.RLPList;

import java.util.ArrayList;
import java.util.List;

/**
 * www.ethereumJ.com
 * User: Roman Mandeleil
 * Created on: 06/04/14 14:56
 */
public class TransactionsMessage extends Message {

    private List<Transaction> transactions = new ArrayList<Transaction>();

    public TransactionsMessage() {
    }

    public TransactionsMessage(RLPList rawData) {
        super(rawData);
    }

    @Override
    public void parseRLP() {
        RLPList paramsList = (RLPList) rawData.get(0);

        if (Command.fromInt(((RLPItem)(paramsList).get(0)).getRLPData()[0] & 0xFF) != TRANSACTIONS) {
            throw new Error("TransactionMessage: parsing for mal data");
        }

        transactions = new ArrayList<Transaction>();
        int size = paramsList.size();
        for (int i = 1; i < size; ++i){
            RLPList rlpTxData = (RLPList) paramsList.get(i);
            Transaction tx = new Transaction(rlpTxData.getRLPData());
            transactions.add(tx);
        }
        parsed = true;
    }

    public List<Transaction> getTransactions() {
        if (!parsed) parseRLP();
        return transactions;
    }

    @Override
    public byte[] getPayload() {
        return null;
    }

    public String toString() {
        if(!parsed) parseRLP();
        StringBuffer sb = new StringBuffer();
        for (Transaction transactionData : transactions){
            sb.append("   ").append(transactionData).append("\n");
        }
        return "Transactions Message [\n" + sb.toString() + " ]";
    }
}