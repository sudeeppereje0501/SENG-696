
package auctionhouse;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author HrithikTriv & Rajpalsinh
 */
class BidderHumanGUI extends JFrame {	

	private BidderHuman myAgent;
	
	private JTextField moneyField,priceField;
	
    BidderHumanGUI(BidderHuman a) {
		super(a.getLocalName() + ": Place bid or add money");
		
		myAgent = a;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(3, 4));
		p.add(new JLabel("Current money:"));
		p.add(new JLabel(String.valueOf(myAgent.getMoney())));
		p.add(new JLabel(""));
		p.add(new JLabel(""));
		p.add(new JLabel("Add money:"));
		moneyField = new JTextField(15);
		p.add(moneyField);

		JButton addButton = new JButton("Add");
		addButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        String money = moneyField.getText().trim();
                        myAgent.addMoney(Integer.parseInt(money));
                        moneyField.setText("");
                    }
                    catch (Exception e) {
                        JOptionPane.showMessageDialog(BidderHumanGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
                    }
                }
            } );
		p.add(addButton);
		p.add(new JLabel(""));

		p.add(new JLabel("Bid Price:"));
		priceField = new JTextField(15);
		p.add(priceField);

		JButton bidButton = new JButton("Bid");
		bidButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        String price = priceField.getText().trim();
                        myAgent.sendBid(Integer.parseInt(price));
                        priceField.setText("");
                    }
                    catch (Exception e) {
                        JOptionPane.showMessageDialog(BidderHumanGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
                    }
                }
            } );
		p.add(bidButton);

		JButton restButton = new JButton("Rest");
		restButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        myAgent.refuseBid();
                        priceField.setText("");
                    }
                    catch (Exception e) {
                        JOptionPane.showMessageDialog(BidderHumanGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
                    }
                }
            } );
		p.add(restButton);

		getContentPane().add(p, BorderLayout.CENTER);		
		
		// Make the agent terminate when the user closes 
		// the GUI using the button on the upper right corner	
		addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    myAgent.doDelete();
                }
            } );
		
		setResizable(false);
	}
	
	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}	
}

