
package auctionhouse;

import jade.core.AID;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author SPereje &  Rajpalsinh
 */
class AuctionManagerGUI extends JFrame {	

	private AuctionManager myAgent;
	
	private JTextField titleField, priceField;
	
    AuctionManagerGUI(AuctionManager a) {
		super(a.getLocalName() + ": Add item");
		
		myAgent = a;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 2));
		p.add(new JLabel("Item Name:"));
		titleField = new JTextField(15);
		p.add(titleField);
		p.add(new JLabel("Initial Price:"));
		priceField = new JTextField(15);
		p.add(priceField);
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        String title = titleField.getText().trim();
                        String price = priceField.getText().trim();
                        myAgent.updateCatalogue(title, Integer.parseInt(price));
                        titleField.setText("");
                        priceField.setText("");
                    }
                    catch (Exception e) {
                        JOptionPane.showMessageDialog(AuctionManagerGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
                    }
                }
            } );
		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);
		
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

