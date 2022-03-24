# How to use Program
### Scrabble Solver Program
The scrabble solver part of the project has "BoardSolver" as its main class. 
It is used in the way specified by the project specification. The solver takes
a dictionary file as a command line argument and reads from standard input <br>
to get boards to solve, and outputs solutions to standard output. 
<br> For example, to run solver.jar with dictionary "dict.txt", inputting a file 
"boards.txt" and outputting the solution to "output.txt", you would do: 
<br> ``java -jar solver.jar dict.txt < board.txt > output.txt``
<br> 
### Scrabble GUI Game 
To start up the Scrabble GUI game, simply run the "ScrabbleGUI.jar" jar file. 
<br> This will instantly start up the game, and you will be able to play. 
<br> In the middle of your screen is the Scrabble board, on which you can place the tiles that 
are at the bottom of the screen. The rules are the standard rules specified in the program 
specification. Every turn, you must place a word either horizontally or vertically so that 
at least one tile is connected to another on the board and form valid words. 
On your first move, one of the tiles must cross the middle square. <br>
If you break a rule while placing a move, you will be alerted to it when you submit your move
and be allowed to try again. <br> 
To place a move onto the board, simply select a tile from your "hand" (bottom of screen), this
will visually highlight the tile you selected. With the tile selected, simply click on a square
on the board and this will "place" your tile onto the board. 
<br> If you misplaced your tiles or want to put the tiles back into your hand so that 
you can make a new move, simply click the "reset" button in the bottom right corner and this 
will take everything you have placed onto the board off the board and allow you to place again.
<br> If you have placed tiles on the board such that you want your placement to be considered 
your "move", go ahead and hit the "submit" button to "lock in" your play. If you have followed
the rules validly, you will end your turn, have your score be updated, and the AI player will make 
their move, updating their score as well. You will draw enough tiles to fill your hand (if there 
are still tiles in the draw pile) and be able to make your next move. 
<br> If you believe that a move is not possible or simply want a new set of tiles, hit the 
"skip move" button. This will cause you to skip your turn, have the AI make its move, and give 
you a complete fresh set of tiles to work with. 
<br> The game ends when both the AI and the Player have skipped a move. Note, the AI will 
only do this if there are no more moves to make. You will in the bottom right be able to see 
who won the game based on who got the highest score. <br>
The Scrabble Game uses the "twl06.txt" dictionary file for finding valid words. 
# Data Structure / Algorithm 
The algorithm and data structure I used for this program was nearly identical to the one described 
by the "World's Fastest Scrabble Program" paper. The general overview of how the GUI game runs is
as follows: 
 1. Read in Dictionary File, generate Trie structure 
 2. Handle input from user until move is submitted 
 3. Make sure move is valid, if it is not go back to (2) otherwise move on 
 4. Have the AI player generate its move and play it 
 5. Go back to (2) unless end of game

<br> The BoardSolver program functions essentially in the same way, except it is not necessary to 
handle the player input, the AI just makes it move. Here are the steps in more detail: <br> 

#### Reading Dictionary: 
Parsing the input is a fairly trivial task, but what's more interesting is what we are doing with 
the data we parse. The general structure for holding the lexicon is the Trie, which means we have 
a tree where every node holds a single character. Nodes are either terminal node, which means the 
path from the root of the tree to the current node forms a valid word (forming word from the characters
seen along the path), or not a terminal node, which means that the current path from root does 
not represent a valid end of word. When we read a single word from the dictionary file, we loop
through each character in the word. Starting at the root of the tree, if the first character of the 
word is a child of the root, we simply move to that node. If the first character is not a child of the 
root, we make a new child and add it to the root with given character. For every character in the word,
we repeat this process. After reaching the end of the current word, we mark the node that we are currently
on as a terminal node. After we do this for every word in the dictionary file, we have our complete lexicon.
<br> The two main classes responsible for this process are the "TrieNode.java" and "TrieFileParser.java" 
classes, which after they are done with their data handling, will simply return the root of the Trie
to whatever component needs it. 

#### Handle Input From User / Validate Move: 
The user interacts with the game by clicking on a tile in their hand to "select" it and then 
clicks on the board to actually place that tile onto the board. This is done through some 
simple mouse click listeners, clicking on the hand sets a "selected" tile, and clicking on that
board simply moves the data from the selected tile onto the tile on the board that was clicked on. 
Resetting the move is fairly simple, there's a list holding every tile that the user has placed, 
so clicking reset just loops through that list, removes the data from the tiles on that list, and 
moves that data back to the hand. Skipping a move is also fairly trivial, simple take every character from the 
user's hand, put it into the draw pile, and let the AI take its move. When you submit a move, there are 
a couple of checks to ensure that the move is valid. The algorithm is pretty simple, it simply looks 
through the list of placed tiles, ensures that either all the rows are consistent or all the columns
of the tiles are consistent (which determines which direction the play is in). Then, the algorithm
finds the "root" of the word and traverses both the board and the trie until it hits the end of the 
word on the board, at which point it checks the current node in the trie to see if it's a terminal node.
If everything checks out, the word is scored and added to the total score. 

#### AI Move Generation 
The algorithm the AI uses is the same as the one described in the paper mentioned above. It's a 
recursive backtracking algorithm that first computes all the "roots" on the board (any empty tile 
adjacent to a non-empty one), then for each root it forms either a "left part" or a "top part", which
is essentially all possible permutations of moves from a hand. Then for each of these "left parts" or 
"top parts", it will navigate the Trie, see if the hand has any child of the Trie, place that character
on the board, and then make a recursive call to the next tile. If at any point while placing we hit 
a terminal node, we will check the score of the word and update the highest scoring word information
if need be. After all these recursive calls are done, we can simply place the highest scoring move
onto the board. 

#### End of Game Calculation
We compute the end of game pretty simple, if at the end of the AIs turn both the player and the 
AI have skipped their turn or not had a move to make, we call that the end of game. 

# Testing/Debugging Tools
The main testing/debugging tools are just printing stuff to the screen. The toString() method 
for most of the classes provides enough visualization to be able to see what's going on. 
There are no flags or anything to set to enable these, you would have to add print statements on
your own. 

# Known Issues / Unfinished Features
There is one secret issue that might be lurking. I have had the ComputerPlayer crash one time at a 
completely random moment in time. This happened only once in all of my testing, and I may have 
accidentally fixed it at some point, but as it stands there is still the possibility that the 
program may crash while playing. However, I have not been able to replicate this in all my 
play-testing, so it's unlikely to happen. <br> 
One other unfinished feature is the "end of game" scoring/winner computing. The game doesn't 
subtract the tiles that are left in either player's hand at the end of the game and just 
declares the winner to be the one with the highest score, or declares it a tie if the scores 
are equal. Other than that though, I think all the other features are implemented. 

