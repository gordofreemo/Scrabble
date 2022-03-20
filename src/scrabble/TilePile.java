package scrabble;

/**
 * Andrew Geyko
 * This class is responsible for the "pile" object where players draw their
 * tiles from.
 */

import java.util.ArrayList;
import java.util.Collections;

public class TilePile {
    private ArrayList<Character> pile;
    private static int[] freq = {
            9,2,2,4,12,2,3,2,9,1,1,4,2,6,8,2,1,6,4,6,4,2,2,1,2,1
    };

    public TilePile() {
        pile = new ArrayList<>();
    }

    /**
     * Adds a given character to the tile
     * @param c - character to add to the pile
     */
    public void add(Character c) {
        pile.add(c);
    }

    /**
     * Randomizes the ordering of elements in the pile
     */
    public void shuffle() {
        Collections.shuffle(pile);
    }

    /**
     * Makes the current pile into one containing the elements that
     * a standard scrabble draw pile has.
     */
    public void scrabblePile() {
        for(int i = 'a'; i <= 'z'; i++) {
            add(freq[i-'a'], (char)i);
        }
        add(2,'*');
        shuffle();
    }

    /**
     * Get a random element from the pile
     * @return - element from pile
     */
    public Character draw() {
        return pile.remove(0);
    }

    /**
     * @return - true if pile is empty, false otherwise
     */
    public boolean isEmpty() {
        return pile.isEmpty();
    }

    /**
     * Add a given character n times to the pile
     * @param freq - how many times to add given character
     * @param character - given character to add
     */
    private void add(int freq, Character character) {
        while(freq-- > 0) pile.add(character);
    }

}
