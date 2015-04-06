package players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.*;

public class Player2 extends Player {

	private Random rnd = new Random(20);
	@Override
	protected List<Card> requestCards(int card, Controller controller) {
		Card[] hand = getHand();
		List<Card> ret =  new ArrayList<Card>();
		for (Card c : hand) {
			if (c.getNumber() == card) {
				ret.add(c);
			}
		}
		if (ret.size() == 0) ret.add(hand[0]);
		return ret;
	}

	@Override
	protected boolean bs(Player player, int card, int numberOfCards,
			Controller controller) {
		return rnd.nextDouble() < 0.2;
	}
	
	public String toString() {
		return "Player 2";
	}

}
