package players;

import java.util.ArrayList;
import java.util.List;

import controller.*;

public class Player1 extends Player{
  // These are all example players
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
	protected boolean bs(Player player, int card, int numberOfCards, Controller controller) {
		return numberOfCards >= 3;
	}

	public String toString() {
		return "Player 1";
	}
}
