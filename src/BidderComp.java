
package auctionhouse;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;
/**
 **
 * @author HrithikTriv & Sanket Ladani
 */
public class BidderComp extends Agent {

	// The catalogue of books for sale (maps the title of a book to its price)
	public String itemName;

    // Check if CFP received
    public boolean CFPReceived = false;

    // The budget left for this bidder
    public int budget;

    // Random number generator
    static Random rn = new Random();

	// Put agent initializations here
	protected void setup() {

        // Setup budget randomly between 1000 - 2000
        budget = rn.nextInt(1000) + 1000;
		System.out.println("Hello! Bidder "+getAID().getName()+" is ready with budget " + budget);

		// Register as bidder to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("blind-auction");
		sd.setName("Blind-Auction");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour for receiving CFP from Auctioneer
		addBehaviour(new ReceiveCFPAsComp(this));

		// Add the behaviour for receiving item --as the auction winner
		addBehaviour(new ReceiveItemAsWinnerComp(this));

		// Add the behaviour for receiving INFORM
		addBehaviour(new ReceiveINFORMComp());
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Printout a dismissal message
		System.out.println("Bidder "+getAID().getName()+" terminating");
	}
}

/**
 * Process CFP as a computer, whether to bid on that item or not
 * All-in strategy. Bid with all the money it has.
 */
class ReceiveCFPAsComp extends CyclicBehaviour {

    private BidderComp myAgent;

    public ReceiveCFPAsComp(BidderComp agent) {
        super(agent);
        myAgent = agent;
    }

    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        ACLMessage msg = myAgent.receive(mt);

        // Check budget, if 0, terminates
        if (myAgent.budget <= 0){
            System.out.println("No budget left");
            myAgent.doDelete();
        }

        if (msg != null) {
            // CFP Message received. Process it
            String ans = msg.getContent();
            String[] parts = ans.split(",");
            String itemName = parts[0];
            int itemInitialPrice = Integer.parseInt(parts[1]);
            ACLMessage reply = msg.createReply();
            int bidPrice = myAgent.budget;

            System.out.println("Auction commenced. Current item is " + itemName);
            System.out.println("Current item initial price is " + itemInitialPrice);

            // Check if budget is adequate 
            if (myAgent.budget >= itemInitialPrice) {

                myAgent.itemName = itemName;
                
                // All-in!
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(String.valueOf(bidPrice));
                System.out.println(myAgent.getLocalName() + " sent bid with price " + bidPrice);
            }
            // Else, cannot join the auction
            else {
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("Not joining this one..");
                System.out.println(myAgent.getLocalName() + " is not joining this auction");
            }

            myAgent.send(reply);
        }
        else {
            block();
        }
    }
}

/**
 * Get the item as the auction winner
 */
class ReceiveItemAsWinnerComp extends CyclicBehaviour {

    private BidderComp myAgent;

    public ReceiveItemAsWinnerComp(BidderComp agent) {
        super(agent);
        myAgent = agent;
    }

    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            // ACCEPT_PROPOSAL Message received. Process it
            String ans = msg.getContent();
            String[] parts = ans.split(",");
            String itemName = parts[0];
            int price = Integer.parseInt(parts[1]);
            ACLMessage reply = msg.createReply();

            reply.setPerformative(ACLMessage.INFORM);
            System.out.println("Congratulations! You have won the auction");
            System.out.println(itemName+" is now yours! With the price $" + price);

            myAgent.send(reply);

            // Cut money from budget
            myAgent.budget -= price;            
        }
        else {
            block();
        }
    }
}

/**
 * Process CFP as a computer, whether to bid on that item or not
 * All-in strategy. Bid with all the money it has.
 */
class ReceiveINFORMComp extends CyclicBehaviour {
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            // INFORM Message received. Print it.
            System.out.println(msg.getContent());
        }
        else {
            block();
        }
    }
}

