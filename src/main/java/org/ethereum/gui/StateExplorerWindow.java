package org.ethereum.gui;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.ethereum.core.AccountState;
import org.ethereum.manager.WorldManager;

public class StateExplorerWindow extends JFrame{
	
	private ToolBar toolBar = null;
	private JTextField textField;
	
	public StateExplorerWindow(ToolBar toolBar) {
		this.toolBar = toolBar;
		
        java.net.URL url = ClassLoader.getSystemResource("ethereum-icon.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        this.setIconImage(img);
        setTitle("Transaction Explorer");
        setSize(500, 280);
        setLocation(115, 280);
        setResizable(false);
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);
        
        Box horizontalBox = Box.createHorizontalBox();
        panel.add(horizontalBox);
        
        textField = new JTextField();
        horizontalBox.add(textField);
        textField.setColumns(30);
        
        JButton btnSearch = new JButton("Search");
        horizontalBox.add(btnSearch);
        btnSearch.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}			
        });
	}
	
	private void searchAccount(String accountStr){
		AccountState state = WorldManager.getInstance().getRepository().getAccountState(accountStr.getBytes());
		System.out.println(state.toString());
	}
}
