package scrabble;

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

    public void scrabblePile() {
        for(int i = 'a'; i <= 'z'; i++) {
            add(freq[i-'a'], (char)i);
        }
        add(2,'*');
        Collections.shuffle(pile);
    }

    public Character draw() {
        return pile.remove(0);
    }

    public boolean isEmpty() {
        return pile.isEmpty();
    }

    private void add(int freq, Character character) {
        while(freq-- > 0) pile.add(character);
    }

}
