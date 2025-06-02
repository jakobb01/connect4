import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Connect4 {

    /**
     * Program: Connect4.java
     * Purpose: Stacking disk game for 2 players
     * Creator: Chris Clarke
     * Created: 19.08.2007
     * Modified: 29.11.2012 (JFrame)
     */

    public static void main(String[] args) {
        Connect4JFrame frame = new Connect4JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Connect4JFrame extends JFrame implements ActionListener {

    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    private Label lblSpacer;
    MenuItem newMI, exitMI, redMI, yellowMI;
    int[][] theArray;
    boolean end = false;
    boolean gameStart;
    public static final int BLANK = 0;
    public static final int RED = 1;
    public static final int YELLOW = 2;

    public static final int MAXROW = 6; // 6 rows
    public static final int MAXCOL = 7; // 7 columns

    public static final String SPACE = "                  "; // 18 spaces

    // agent flag (false for human, true for agent)
    boolean redIsRandom = false;
    boolean yellowIsRandom = false;
    boolean redIsMinimax = false;
    boolean yellowIsMinimax = false;

    // random agent selects a random column
    private int getRandomMove() {
        Random rand = new Random();
        int col;
        do {
            col = rand.nextInt(MAXCOL); // generate random from 0 to 6
        } while (!isValidMove(col)); // check column is valid for move
        return col;
    }

    // check column is valid for move
    private boolean isValidMove(int col) {
        return theArray[0][col] == BLANK;
    }

    int activeColour = RED;

    // vars to track move counts for each player and total
    int redMoveCount = 0;
    int yellowMoveCount = 0;
    int totalMoveCount = 0;

    JLabel moveCountLabel;
    JLabel moveTimeLabel;
    JLabel scoreMinMaxLabel;

    public Connect4JFrame() {
        moveCountLabel = new JLabel("Red: 0 moves | Yellow: 0 moves | Total: 0 moves");
        moveTimeLabel = new JLabel("| Last move time: 0 ms |");
        scoreMinMaxLabel = new JLabel("Evaluation score of minmax: 0");
        setTitle("Connect4 by Chris Clarke");
        MenuBar mbar = new MenuBar();
        Menu fileMenu = new Menu("File");
        newMI = new MenuItem("New");
        newMI.addActionListener(this);
        fileMenu.add(newMI);
        exitMI = new MenuItem("Exit");
        exitMI.addActionListener(this);
        fileMenu.add(exitMI);
        mbar.add(fileMenu);
        Menu optMenu = new Menu("Options");
        redMI = new MenuItem("Red starts");
        redMI.addActionListener(this);
        optMenu.add(redMI);
        yellowMI = new MenuItem("Yellow starts");
        yellowMI.addActionListener(this);
        optMenu.add(yellowMI);
        mbar.add(optMenu);

        // "Players" menu to choose between human and random players
        Menu playersMenu = new Menu("Players");
        CheckboxMenuItem humanRed = new CheckboxMenuItem("Red: Human", true);
        CheckboxMenuItem randomRed = new CheckboxMenuItem("Red: Random", false);
        CheckboxMenuItem humanYellow = new CheckboxMenuItem("Yellow: Human", true);
        CheckboxMenuItem randomYellow = new CheckboxMenuItem("Yellow: Random", false);
        CheckboxMenuItem minimaxRed = new CheckboxMenuItem("Red: Minimax", false);
        CheckboxMenuItem minimaxYellow = new CheckboxMenuItem("Yellow: Minimax", false);

        humanRed.addItemListener(e -> {
            redIsMinimax = false;
            redIsRandom = false;
            humanRed.setState(true);
            randomRed.setState(false);
            minimaxRed.setState(false);
        });

        humanYellow.addItemListener(e -> {
            yellowIsMinimax = false;
            yellowIsRandom = false;
            humanYellow.setState(true);
            randomYellow.setState(false);
            minimaxYellow.setState(false);
        });

        randomRed.addItemListener(e -> {
            redIsMinimax = false;
            redIsRandom = true;
            humanRed.setState(false);
            randomRed.setState(true);
            minimaxRed.setState(false);
        });

        randomYellow.addItemListener(e -> {
            yellowIsMinimax = false;
            yellowIsRandom = true;
            humanYellow.setState(false);
            randomYellow.setState(true);
            minimaxYellow.setState(false);
        });

        minimaxRed.addItemListener(e -> {
            redIsMinimax = true;
            redIsRandom = false;
            humanRed.setState(false);
            randomRed.setState(false);
            minimaxRed.setState(true);
        });

        minimaxYellow.addItemListener(e -> {
            yellowIsMinimax = true;
            yellowIsRandom = false;
            humanYellow.setState(false);
            randomYellow.setState(false);
            minimaxYellow.setState(true);
        });

        // add selection menu items
        playersMenu.add(humanRed);
        playersMenu.add(randomRed);
        playersMenu.add(minimaxRed);
        playersMenu.add(humanYellow);
        playersMenu.add(randomYellow);
        playersMenu.add(minimaxYellow);
        mbar.add(playersMenu);

        setMenuBar(mbar);

        // build control panel.
        Panel panel = new Panel();
        Panel panel2 = new Panel();

        btn1 = new Button("1");
        btn1.addActionListener(this);
        panel.add(btn1);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn2 = new Button("2");
        btn2.addActionListener(this);
        panel.add(btn2);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn3 = new Button("3");
        btn3.addActionListener(this);
        panel.add(btn3);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn4 = new Button("4");
        btn4.addActionListener(this);
        panel.add(btn4);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn5 = new Button("5");
        btn5.addActionListener(this);
        panel.add(btn5);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn6 = new Button("6");
        btn6.addActionListener(this);
        panel.add(btn6);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn7 = new Button("7");
        btn7.addActionListener(this);
        panel.add(btn7);

        panel2.add(moveCountLabel);
        panel2.add(moveTimeLabel);
        panel2.add(scoreMinMaxLabel);
        add(panel, BorderLayout.NORTH);
        add(panel2, BorderLayout.SOUTH);
        initialize();
        setSize(1024, 768);
    }

    // initialize
    public void initialize() {
        theArray = new int[MAXROW][MAXCOL];
        for (int row = 0; row < MAXROW; row++)
            for (int col = 0; col < MAXCOL; col++)
                theArray[row][col] = BLANK;
        gameStart = false;

        redMoveCount = 0;
        yellowMoveCount = 0;
        totalMoveCount = 0;
        updateCount();
        updateMiniMaxScr();
    } 

    public void paint(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(110, 50, 100 + 100 * MAXCOL, 100 + 100 * MAXROW);
        for (int row = 0; row < MAXROW; row++)
            for (int col = 0; col < MAXCOL; col++) {
                if (theArray[row][col] == BLANK)
                    g.setColor(Color.WHITE);
                if (theArray[row][col] == RED)
                    g.setColor(Color.RED);
                if (theArray[row][col] == YELLOW)
                    g.setColor(Color.YELLOW);
                g.fillOval(160 + 100 * col, 100 + 100 * row, 100, 100);
            }
        check4(g);
    }

    // helper to handle agent moves after each turn
    private void helperPlayAgent() {
        if (end)
            return;

        long startTime = System.currentTimeMillis();
        int col = -1;

        if (activeColour == RED && redIsMinimax) {
            col = getMinimaxMove(RED, 7); // depth should be adjusted based on good enough solution
        } else if (activeColour == YELLOW && yellowIsMinimax) {
            col = getMinimaxMove(YELLOW, 7); // when d=7, it takes around 1s for agent to make a move
        } else if (activeColour == RED && redIsRandom) {
            col = getRandomMove();
        } else if (activeColour == YELLOW && yellowIsRandom) {
            col = getRandomMove();
        }

        if (col != -1) {
            putDisk(col + 1);
            long endTime = System.currentTimeMillis();
            moveTimeLabel.setText("Last move time: " + (endTime - startTime) + " ms");
            SwingUtilities.invokeLater(() -> helperPlayAgent());
        }
    }

    // putDisk to increment move counts
    public void putDisk(int n) {
        if (end)
            return;
        gameStart = true;
        int row;
        n--;
        for (row = 0; row < MAXROW; row++)
            if (theArray[row][n] > 0)
                break;
        if (row > 0) {
            theArray[--row][n] = activeColour;
            if (activeColour == RED) {
                redMoveCount++;
                activeColour = YELLOW;
            } else {
                yellowMoveCount++;
                activeColour = RED;
            }
            totalMoveCount++;
            updateCount();
            updateMiniMaxScr();
            repaint();
        }
    }

    public void displayWinner(Graphics g, int n) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier", Font.BOLD, 100));
        if (n == RED)
            g.drawString("Red wins!", 100, 400);
        else
            g.drawString("Yellow wins!", 100, 400);
        end = true;
    }

    public void check4(Graphics g) {
        // see if there are 4 disks in a row: horizontal, vertical or diagonal
        // horizontal rows
        for (int row = 0; row < MAXROW; row++) {
            for (int col = 0; col < MAXCOL - 3; col++) {
                int curr = theArray[row][col];
                if (curr > 0
                        && curr == theArray[row][col + 1]
                        && curr == theArray[row][col + 2]
                        && curr == theArray[row][col + 3]) {
                    displayWinner(g, theArray[row][col]);
                }
            }
        }
        // vertical columns
        for (int col = 0; col < MAXCOL; col++) {
            for (int row = 0; row < MAXROW - 3; row++) {
                int curr = theArray[row][col];
                if (curr > 0
                        && curr == theArray[row + 1][col]
                        && curr == theArray[row + 2][col]
                        && curr == theArray[row + 3][col])
                    displayWinner(g, theArray[row][col]);
            }
        }
        // diagonal lower left to upper right
        for (int row = 0; row < MAXROW - 3; row++) {
            for (int col = 0; col < MAXCOL - 3; col++) {
                int curr = theArray[row][col];
                if (curr > 0
                        && curr == theArray[row + 1][col + 1]
                        && curr == theArray[row + 2][col + 2]
                        && curr == theArray[row + 3][col + 3])
                    displayWinner(g, theArray[row][col]);
            }
        }
        // diagonal upper left to lower right
        for (int row = MAXROW - 1; row >= 3; row--) {
            for (int col = 0; col < MAXCOL - 3; col++) {
                int curr = theArray[row][col];
                if (curr > 0
                        && curr == theArray[row - 1][col + 1]
                        && curr == theArray[row - 2][col + 2]
                        && curr == theArray[row - 3][col + 3])
                    displayWinner(g, theArray[row][col]);
            }
        }
    }

    public void updateCount() {
        moveCountLabel.setText("Red: " + redMoveCount + " moves | Yellow: " + yellowMoveCount + " moves | Total: "
                + totalMoveCount + " moves");
    }

    public void updateMiniMaxScr() {
        int score = evaluateBoard(theArray, activeColour);
        scoreMinMaxLabel.setText("Evaluation score of minmax: " + score);
    }

    // function that handles buttons pressed
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btn1)
            putDisk(1);
        else if (e.getSource() == btn2)
            putDisk(2);
        else if (e.getSource() == btn3)
            putDisk(3);
        else if (e.getSource() == btn4)
            putDisk(4);
        else if (e.getSource() == btn5)
            putDisk(5);
        else if (e.getSource() == btn6)
            putDisk(6);
        else if (e.getSource() == btn7)
            putDisk(7);
        else if (e.getSource() == newMI) {
            end = false;
            initialize();
            repaint();
            // when we start a new game, let bot play if its his turn
            SwingUtilities.invokeLater(() -> helperPlayAgent());
            return;
        } else if (e.getSource() == exitMI) {
            System.exit(0);
        } else if (e.getSource() == redMI) {
            if (!gameStart)
                activeColour = RED;
        } else if (e.getSource() == yellowMI) {
            if (!gameStart)
                activeColour = YELLOW;
        }
        // after human move call helperPlayAgent
        SwingUtilities.invokeLater(() -> helperPlayAgent());
    }

    
    //------------minimax logic----------------
    private int getMinimaxMove(int player, int depth) {
        int bestScore = Integer.MIN_VALUE; // just for starting, to not get stuck on IF statement
        int bestCol = -1; // invalid move, should be updated
        // go through all the columns | of the matrix and score them
        for (int col = 0; col < MAXCOL; col++) {
            if (!isValidMove(col))
                continue;
            int[][] copy = copyArray(theArray);
            makeMove(copy, col, player);
            int score = minimax(copy, depth - 1, false, player, player == RED ? YELLOW : RED);
            // update scoring if better then the previous
            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }
        }
        return bestCol;
    }

    private int minimax(int[][] board, int depth, boolean maximizing, int agent, int opponent) {
        int winner = checkWinner(board);
        // ends the game if winner is selected
        if (winner == agent)
            return Integer.MAX_VALUE; // absolute best
        if (winner == opponent)
            return Integer.MIN_VALUE; // absolute worst
        if (isBoardFull(board) || depth == 0)
            return evaluateBoard(board, agent);

        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int col = 0; col < MAXCOL; col++) {
                if (!isValidMove(board, col))
                    continue;
                int[][] copy = copyArray(board);
                makeMove(copy, col, agent);
                int eval = minimax(copy, depth - 1, false, agent, opponent);
                // take max if maximizing
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col = 0; col < MAXCOL; col++) {
                if (!isValidMove(board, col))
                    continue;
                int[][] copy = copyArray(board);
                makeMove(copy, col, opponent);
                int eval = minimax(copy, depth - 1, true, agent, opponent);
                // take min if minimizing
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    // evaluation function
    private int evaluateBoard(int[][] board, int player) {
        // positive score for 2/3 in a row, negative score for opponent having good placements
        int score = 0;
        int opponent = (player == RED) ? YELLOW : RED;
        score += countStreaks(board, player, 2) * 10;
        score += countStreaks(board, player, 3) * 100;
        score -= countStreaks(board, opponent, 2) * 12;
        score -= countStreaks(board, opponent, 3) * 120;
        return score;
    }

    // check how many streaks does any player have on a board
    private int countStreaks(int[][] board, int player, int streak) {
        int count = 0;
        // start double for loop row, col
        for (int row = 0; row < MAXROW; row++) {
            for (int col = 0; col < MAXCOL; col++) {
                // - horizontal counting
                // first, check if there is even enough space 
                if (col + streak <= MAXCOL) {
                    boolean found = true;
                    // check if the streak is from a specified player
                    for (int i = 0; i < streak; i++)
                        if (board[row][col + i] != player)
                            found = false;
                    // increase count if found
                    if (found)
                        count++;
                }
                // | vertical cnt
                if (row + streak <= MAXROW) {
                    boolean found = true;
                    for (int i = 0; i < streak; i++)
                        if (board[row + i][col] != player)
                            found = false;
                    if (found)
                        count++;
                }
                // / diagonal cnt
                if (row + streak <= MAXROW && col + streak <= MAXCOL) {
                    boolean found = true;
                    for (int i = 0; i < streak; i++)
                        if (board[row + i][col + i] != player)
                            found = false;
                    if (found)
                        count++;
                }
                // \ diagonal cnt
                if (row - streak + 1 >= 0 && col + streak <= MAXCOL) {
                    boolean found = true;
                    for (int i = 0; i < streak; i++)
                        if (board[row - i][col + i] != player)
                            found = false;
                    if (found)
                        count++;
                }
            }
        }
        return count;
    }

    private boolean isValidMove(int[][] board, int col) {
        return board[0][col] == BLANK;
    }

    private boolean isBoardFull(int[][] board) {
        for (int col = 0; col < MAXCOL; col++)
            if (board[0][col] == BLANK)
                return false;
        return true;
    }

    private void makeMove(int[][] board, int col, int player) {
        for (int row = MAXROW - 1; row >= 0; row--) {
            if (board[row][col] == BLANK) {
                board[row][col] = player;
                break;
            }
        }
    }

    private int[][] copyArray(int[][] src) {
        int[][] dst = new int[MAXROW][MAXCOL];
        for (int i = 0; i < MAXROW; i++)
            System.arraycopy(src[i], 0, dst[i], 0, MAXCOL);
        return dst;
    }

    private int checkWinner(int[][] board) {
        // returns RED, YELLOW, or 0 for no winner
        // hori checks
        for (int row = 0; row < MAXROW; row++)
            for (int col = 0; col < MAXCOL - 3; col++)
                if (board[row][col] > 0 &&
                        board[row][col] == board[row][col + 1] &&
                        board[row][col] == board[row][col + 2] &&
                        board[row][col] == board[row][col + 3])
                    return board[row][col];
        // vertical
        for (int col = 0; col < MAXCOL; col++)
            for (int row = 0; row < MAXROW - 3; row++)
                if (board[row][col] > 0 &&
                        board[row][col] == board[row + 1][col] &&
                        board[row][col] == board[row + 2][col] &&
                        board[row][col] == board[row + 3][col])
                    return board[row][col];
        // diag
        for (int row = 0; row < MAXROW - 3; row++)
            for (int col = 0; col < MAXCOL - 3; col++)
                if (board[row][col] > 0 &&
                        board[row][col] == board[row + 1][col + 1] &&
                        board[row][col] == board[row + 2][col + 2] &&
                        board[row][col] == board[row + 3][col + 3])
                    return board[row][col];
        // diag
        for (int row = 3; row < MAXROW; row++)
            for (int col = 0; col < MAXCOL - 3; col++)
                if (board[row][col] > 0 &&
                        board[row][col] == board[row - 1][col + 1] &&
                        board[row][col] == board[row - 2][col + 2] &&
                        board[row][col] == board[row - 3][col + 3])
                    return board[row][col];
        return 0;
    }
}