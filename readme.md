Got file parsing to generate a trie tree <br>
Need to begin making the board functional so that I can begin implement AI 
algorithm <br>
<br>
Need to make the "dictionary" class, which will be able to check whether 
a word is valid or just add that functionality to the TrieNode class, will 
remove the need to have both the AI and the Board to have functionality that 
checks if a word is valid.

In the tiles - null represents out of bounds or "tile does not exist" 
<br> while '\0' represents an empty tile. 

We don't need to worry about resetting the character multiplier when counting the 
word score since the word counting function, when recurring into subwords, 
simply ignores them. 

Algorithm for Computer Player: 
First, compute all the "anchor" squares on the board and put them into a hashset.
The inner anchor square class has a reference to the board tile that it is 
pointing to as well as an array of "cross-checks" representing which letters
can be played on that anchor square. 
The cross-checks are computed by finding the top-most and left-most connected
tile so that an entire word is formed from the cross check and then 
loops through the alphabet seeing which words are placeable. 

@TODO 

- Make ComputerPlayer capable of computing cross checks 
- Move anchor class to its own file 
- In either the board or the computer player class, add some functionality to be
to get the "leftmost" coordinate of a connected word and the "topmost" coordinate
of a connected word (probably computerplayer) 

each anchor will be able to computer its own cross checks, it will be assigned
a board for which to do so. computing cross checks is down by passing in a 
hand. for each character in that hand, it will place a tile down and 
check with the board to see if what they placed is a valid word. 

Need to think about the design a little, the crosschecks should be computed
for every empty square and not just for the anchor squares. But... if something 
is not an anchor square and is empty all moves should be possible since 
everything around it is empty. 