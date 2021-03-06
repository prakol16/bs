package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import players.*;

public class Controller {
	private List<Card> discardPile;
	private List<Player> players;
	public Random rnd = new Random();
	private int card;
	private static final Class<?>[] playerClasses = {
		Player1.class, Player2.class, Player3.class, Player4.class, PlayerTruthy.class
	};
	public static void main(String[] args) {
		Controller c = new Controller();
		Map<Class<?>, Integer> scores = new HashMap<Class<?>, Integer>();
		for (Class<?> playerClass : playerClasses) {
			scores.put(playerClass, 0);
		}
		for (int i = 0; i < 1000; ++i) {
			Player winner = c.playGame(false);
			Class<?> winnerClass = winner.getClass();
			scores.put(winnerClass, scores.get(winnerClass) + 1); 
		}
		System.out.println(scores);
	}
	private List<Class<?>> getPlayerClasses() {
		List<Class<?>> playerClassList = Arrays.asList(playerClasses);
		Collections.shuffle(playerClassList, rnd);
		return playerClassList;
	}
	private Player playGame(boolean toLog) {
		players = new ArrayList<>();
		discardPile = new ArrayList<>();
		for (Class<?> playerClass : getPlayerClasses()) {
			try {
				players.add((Player) playerClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
			if (players.size() == 4) break;
		}
		List<Card> deck = new ArrayList<Card>(52);
		for (int i = 0; i < 13; ++i) {
			for (int j = 0; j < 4; ++j) {
				deck.add(new Card(i));
			}
		}
		Collections.shuffle(deck, rnd);
		for (int i = 0; i < 4; ++i) {
			Player p = players.get(i);
			p.drawCards(deck.subList(i * 13, (i + 1) * 13));
		}
		for (Player p : players) {
			p.initialize(this);
		}
		if (toLog) {
			System.out.println(this.asString());
		}
		for (int i = 0; i < 1000; ++i) {
			runRound(players, toLog);
			
			if (toLog) {
				System.out.println(this.asString());
			}
			Player winner = getWinner();
			if (winner != null) {
				if (toLog) {
					System.out.println(winner + " won!");
				}
				return winner;
			}
		}
		return null;
	}
	private Player getWinner() {
		for (Player p : players) {
			if (p.handSize() == 0) return p;
		}
		return null;
	}
	private void runRound(List<Player> players, boolean toLog) {
		for (Player player : players) {
			Set<Card> cardsPlayed = new HashSet<Card>(player.requestCards(card, this));
			if (cardsPlayed.size() == 0) throw new RuntimeException("Player " + player + " played zero cards");
			boolean isBS = false;
			for (Card c : cardsPlayed) {
				if (c.getNumber() != card) {
					isBS = true;
					break;
				}
			}
			
			if (toLog) {
				System.out.print(player + " played " + cardsPlayed.size() + " cards of " + card);
				if (isBS) System.out.print(" (which are actually " + cardsPlayed + ")");
				System.out.println();
			}
			
			
			player.removeCards(cardsPlayed);
			discardPile.addAll(cardsPlayed);
			List<Player> bs = new ArrayList<>();
			for (Player p : players) {
				if (p == player) continue;
				if (p.bs(player, card, cardsPlayed.size(), this)) {
					bs.add(p);
				}
			}
			if (bs.size() != 0) {
				Player playerThinksBS = bs.get(rnd.nextInt(bs.size()));
				
				if (toLog) {
					System.out.println(playerThinksBS + " called BS");
				}
				
				if (isBS) {
					player.drawCards(discardPile);
					discardPile.clear();
					discardPile.add(playerThinksBS.removeRandomCard(rnd));
				} else {
					playerThinksBS.drawCards(discardPile);
					discardPile.clear();
				}
			}
			card++;
			card %= 13;
			if (getWinner() != null) {
				break;
			}
		}
		for (Player p : players) {
			p.update(this);
		}
	}

	public int getDiscardPileSize() {
		return discardPile.size();
	}
	public List<Player> getPlayers() {
		return players;
	}
	private String asString() {
		StringBuilder str = new StringBuilder();
		for (Player p : players) {
			str.append(p);
			str.append(": ");
			str.append(p.handAsString());
			str.append(" " + p.handSize() + " cards");
			str.append('\n');
		}
		str.append("Discard pile: ");
		str.append(discardPile);
		str.append(" " + discardPile.size() + " cards");
		str.append('\n');
		return str.toString();
	}
}
