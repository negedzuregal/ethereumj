package org.ethereum.gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.ethereum.core.Account;
import org.ethereum.manager.WorldManager;
import org.spongycastle.util.encoders.Hex;

public class AccountsListWindow  extends JFrame {
	
	private JTable tblAccountsDataTable;
	
	public AccountsListWindow() {
		java.net.URL url = ClassLoader.getSystemResource("ethereum-icon.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        this.setIconImage(img);
        setTitle("State Explorer");
        setSize(400, 500);
        setLocation(50, 180);
        setResizable(false);
        
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        
        tblAccountsDataTable = new JTable();
        tblAccountsDataTable.setModel(new AccountsDataAdapter(new ArrayList<Account>()));
        
        
	}
	
	
	private class AccountsDataAdapter extends AbstractTableModel {
		List<Account> data;
		
		final String[] columns = new String[]{ "Account", "Balance"};
		
		public AccountsDataAdapter(List<Account> data) {
			this.data = data;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(columnIndex == 0) {
				return Hex.toHexString(data.get(rowIndex).getAddress());
			}
			else {
				return "";
			}
		}
	}

}
