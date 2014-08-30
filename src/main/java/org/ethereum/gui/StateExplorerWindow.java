package org.ethereum.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigInteger;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.ethereum.core.AccountState;
import org.ethereum.core.Block;
import org.ethereum.core.Transaction;
import org.ethereum.db.ContractDetails;
import org.ethereum.manager.WorldManager;
import org.ethereum.vm.DataWord;
import org.ethereum.vm.Program;
import org.ethereum.vm.Program.ProgramListener;
import org.spongycastle.util.encoders.Hex;

public class StateExplorerWindow extends JFrame{
	
	private ToolBar toolBar = null;
	private JTextField txfAccountAddress;
	private StateTextArea txaPrinter;
	
	private JTable tblStateDataTable;
	private StateDataTableModel dataModel;
	
	public StateExplorerWindow(ToolBar toolBar) {
		this.toolBar = toolBar;
		
        java.net.URL url = ClassLoader.getSystemResource("ethereum-icon.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        this.setIconImage(img);
        setTitle("State Explorer");
        setSize(700, 660);
        setLocation(115, 280);
        setResizable(false);
        
        /*
         * top search panel 
         */
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        
        Box horizontalBox = Box.createHorizontalBox();
        panel.add(horizontalBox);
        
        txfAccountAddress = new JTextField();
        horizontalBox.add(txfAccountAddress);
        txfAccountAddress.setColumns(30);
        
        JButton btnSearch = new JButton("Search");
        horizontalBox.add(btnSearch);
        btnSearch.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				searchAccount(txfAccountAddress.getText());
			}			
        });
        
        /*
         * center text panel
         */
        JPanel centerPanel = new JPanel();
        panel.add(centerPanel);
        
        txaPrinter = new StateTextArea();
        centerPanel.add(txaPrinter);
        
        /*
         * bottom data panel
         */      
        // data type choice boxes
        Box Hbox = Box.createHorizontalBox();
        panel.add(Hbox);
        
        String[] dataTypes = {"String", "Hex", "Number"};
        
        Box VBox1 = Box.createVerticalBox();
        VBox1.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l1 = new JLabel("Key Encoding");
        l1.setAlignmentX(LEFT_ALIGNMENT);
        JComboBox cmbKey = new JComboBox(dataTypes);
        cmbKey.setSelectedIndex(1);
        cmbKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cmb = (JComboBox) e.getSource();
				DataEncodingType t = DataEncodingType.getTypeFromString((String) cmb.getSelectedItem());
				dataModel.setKeyEncoding(t);
			}
		});
        VBox1.add(l1);
        VBox1.add(cmbKey);
        
        Box VBox2 = Box.createVerticalBox();
        VBox2.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l2 = new JLabel("Value Encoding");
        l2.setAlignmentX(LEFT_ALIGNMENT);
        JComboBox cmbValue = new JComboBox(dataTypes);
        cmbValue.setSelectedIndex(1);
        cmbValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cmb = (JComboBox) e.getSource();
				DataEncodingType t = DataEncodingType.getTypeFromString((String) cmb.getSelectedItem());
				dataModel.setValueEncoding(t);
			}
		});
        VBox2.add(l2);
        VBox2.add(cmbValue);
        
        Hbox.add(VBox1);
        Hbox.add(VBox2);
        
        // table
        tblStateDataTable = new JTable();
        dataModel = new StateDataTableModel();
        tblStateDataTable.setModel(dataModel);
        
        
        JScrollPane scrollPane = new JScrollPane(tblStateDataTable);
        scrollPane.setPreferredSize(new Dimension(600,200));
        panel.add(scrollPane);
	}
	
	private void searchAccount(String accountStr){
		txaPrinter.clean();
		txaPrinter.println(accountDetailsString(Hex.decode(accountStr), dataModel));
//		for (int i =0; i < WorldManager.getInstance().getBlockchain().getSize(); ++i) {
//            Block block = WorldManager.getInstance().getBlockchain().getByNumber(i);
//            for(final Transaction tx: block.getTransactionsList()){
//            	System.out.println(accountDetailsString(tx.getReceiveAddress()));
//		        System.out.println("\n\n\n\n");
//			}
//        }
//			
	}
	
	private String accountDetailsString(byte[] account, StateDataTableModel dataModel){	
		String ret = "";
		// 1) print account address
		ret = "Account: " + Hex.toHexString(account) + "\n";
		
		//2) print state 
		AccountState state = WorldManager.getInstance().getRepository().getAccountState(account);
		if(state != null)
			ret += state.toString() + "\n";
		
		//3) print storage
		ContractDetails contractDetails = WorldManager.getInstance().getRepository().getContractDetails(account);
		if(contractDetails != null) {
			Map<DataWord, DataWord> accountStorage = contractDetails.getStorage();
			dataModel.setData(accountStorage);
		}
		
		byte[] code = WorldManager.getInstance().getRepository().getCode(account);
		
		
		return ret;
	}
	
	private class StateDataTableModel extends AbstractTableModel {

		Map<DataWord, DataWord> data;
		DataEncodingType keyEncodingType = DataEncodingType.HEX;
		DataEncodingType valueEncodingType = DataEncodingType.HEX;
		String[] columns = new String[]{ "Key", "Value"};
		
		public StateDataTableModel() { }
		
		public StateDataTableModel(Map<DataWord, DataWord> initData) {
			setData(initData);
		}
		
		public void setData(Map<DataWord, DataWord> initData) {
			data = initData;
			fireTableDataChanged();
		}
		
		public void setKeyEncoding(DataEncodingType type) {
			keyEncodingType  = type;
			fireTableDataChanged();
		}
		
		public void setValueEncoding(DataEncodingType type) {
			valueEncodingType  = type;
			fireTableDataChanged();
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}
		
		@Override
		public int getRowCount() {
			return data == null? 0:data.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			DataWord key = (DataWord) this.data.keySet().toArray()[rowIndex];
			
			if(columnIndex == 0) {
				return getDataWithEncoding(key.getData(), keyEncodingType);
			}
			else {
				DataWord value = this.data.get(key);
				return getDataWithEncoding(value.getData(), valueEncodingType);
			}
		}
		
		private String getDataWithEncoding(byte[] data, DataEncodingType enc) {
			switch(enc) {
			case STRING:
				return new String(data);
			case HEX:
				return Hex.toHexString(data);
			case NUMBER:
				return new BigInteger(data).toString();
			}
			
			return data.toString();
		}
	}
	
	private enum DataEncodingType{
		STRING,
		HEX,
		NUMBER;
		
		static public DataEncodingType getTypeFromString(String value) {
			switch(value){
			case "String":
				return STRING;
			case "Hex":
				return HEX;
			case "Number":
				return NUMBER;
			}
			return STRING;
		}
	}
	
	private class StateTextArea extends TextArea {
		
		public StateTextArea() {
			super();
		}
		
		public void println(String txt) {
			setText(getText() + txt + "\n");
		}
		
		public void clean() {
			setText("");
		}
	}
}
