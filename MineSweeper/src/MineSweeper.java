// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Stack;

/**
 *  Simple 'Minesweeper' program.
 *  There is a grid of cells, some of which contain a mine.
 *  The user can click on a cell to either expose it or to
 *  mark/unmark it.
 *  
 *  If the user exposes a cell with a mine, they lose.
 *  Otherwise, it is uncovered, and shows a number which represents the
 *  number of mines in the eight cells surrounding that one.
 *  If there are no mines adjacent to it, then all the unexposed cells
 *  immediately adjacent to it are exposed (and and so on)
 *
 *  If the user marks a cell, then they cannot expose the cell,
 *  (unless they unmark it first)
 *  When all squares with mines are marked, and all the squares without
 *  mines are exposed, the user has won.
 */
public class MineSweeper {

    public static final int ROWS = 15;
    public static final int COLS = 15;

    public static final double LEFT = 15;
    public static final double TOP = 80;
    public static final double CELL_SIZE = 20;

    public static final double FACE_BUTTON_SIZE = 35;

    //Counters at the top of the game window
    Counter markerCounter = new Counter(LEFT * 1.5, LEFT * 1.4, 3, 1.75);
    Counter timeCounter = new Counter(COLS * CELL_SIZE - LEFT * 3, LEFT * 1.4, 3, 1.75);

    int markerCount = 99;
    int time = 0;
    boolean gameRunning = false;
    boolean lost = false;
    boolean won = false;

    public boolean faceClicked = false;

    private Cell[][] cells;
    /**
     * Construct a new MineSweeper object
     * and set up the GUI
     */
    public MineSweeper(){
        //Timer thread to increment time counter every second
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (gameRunning)
                        ++time;
                    timeCounter.display(time);
                } catch (Exception e) {
                }
            }
        }).start();

        //Render nicely
        UI.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        setupGUI();
        makeGrid();
    }

    /** setup buttons */
    public void setupGUI(){

        //Get the ecs100.ECS100Canvas UI component to add a "proper" mouse listener that supports right click
        Container c= (Container)UI.getFrame().getComponent(0);
        //printContents(c, 0);
        c = (Container)c.getComponent(1);
        c = (Container)c.getComponent(0);
        c = (Container)c.getComponent(2);
        c = (Container)c.getComponent(1);
        c = (Container)c.getComponent(0);
        Component canvas = c.getComponent(0);
        canvas.addMouseListener(new MouseHandler(this));

        UI.addButton("AI", this::runAI);

        UI.setDivider(0.2);
        UI.println("1. Click to expose");
        UI.println("2. Right click to mark");
        UI.println("3. Click face to reset");
    }

    private void runAI()
    {
        for(int i = 0; i < 2; i++) { //Run twice to get safe and unsafe cells

            int[][] board = AI.createVisibleIntBoard(cells); //Create array of integers from the visible board
            int[][] actions = AI.createMoves(board); //Calculate the actions that can be taken

            //Apply action
            for (int row = 0; row < actions.length; ++row) {
                for (int col = 0; col < actions[0].length; ++col) {

                    if (actions[row][col] == 0)
                        cells[row][col].setMarkedSafe(true);

                    else if (actions[row][col] == 1) {
                        --markerCount;
                        cells[row][col].setMarked(true);
                    }

                    redrawCell(row, col);
                }
            }
        }
        markerCounter.display(markerCount);
    }

    /**
     * Recursively prints out the content tree of a UI container
     * Used to find where in the UI component tree the ECS100Canvas object is
     */
    public void printContents(Container c, int depth)
    {
        for(Component comp : c.getComponents())
        {
            for(int i = 0; i < depth; ++i)
                UI.print("  ");

            UI.println(comp.getClass().getName());

            if(comp instanceof Container)
                printContents((Container)comp, depth + 1);

        }
    }

    // Other Methods

    /** 
     * The player has clicked on a cell to expose it
     * - if it is already exposed or marked, do nothing.
     * - if it's a mine: lose (call drawLose()) 
     * - otherwise expose it (call exposeCellAt)
     * then check to see if the player has won and call drawWon() if they have.
     * (This method is not recursive)
     */
    public void tryExpose(int row, int col){
        if(row >= ROWS || col >= COLS || row < 0 || col < 0) return;
        gameRunning = true;
        Cell cell = cells[row][col];
        if(cell.isExposed() || cell.isMarked()) return;
        if(cell.hasMine())
        {
            drawLose();
            return;
        }
        else
            exposeCellAt(row, col);

        if (hasWon()){
            drawWin();
        }
    }

    /** 
     *  Expose a cell, and spread to its neighbours if safe to do so.
     *  It is guaranteed that this cell is safe to expose (ie, does not have a mine).
     *  If it is already exposed, we are done.
     *  Otherwise expose it, and redraw it.
     *  If the number of adjacent mines of this cell is 0, then
     *     expose all its neighbours (which are safe to expose)
     *     (and if they have no adjacent mine, expose their neighbours, and ....)
     */
    public void exposeCellAt(int row, int col){
        Stack<int[]> recursiveStack = new Stack<>();
        recursiveStack.push(new int[] {row, col});

        while(recursiveStack.size() > 0)
        {
            int[] pos = recursiveStack.pop();
            Cell cell = cells[pos[0]][pos[1]];

            if(cell.getAdjacentMines() == 0 && !cell.isExposed())
            {
                //Iterate over each cell in a 3x3 grid centred on the current cell
                for(int r = -1; r < 2; ++r)
                    for(int c = -1; c < 2; ++c)
                        if(pos[0] + r < ROWS && pos[0] + r >= 0 && pos[1] + c < COLS && pos[1] + c >= 0)
                        {
                            recursiveStack.push(new int[] {pos[0] + r, pos[1] + c});
                        }
            }

            if(!cell.isMarked()) {
                cell.setExposed();
                redrawCell(pos[0], pos[1]);
            }
        }
    }


    /**
     * Mark/unmark the cell.
     * If the cell is exposed, don't do anything,
     * If it is marked, unmark it.
     * otherwise mark it and redraw.
     * (Marking cannot make the player win or lose)
     */
    public void mark(int row, int col){
        if(row >= ROWS || col >= COLS || row < 0 || col < 0) return;
        Cell cell = cells[row][col];
        if(cell.isExposed()) return;


        if(cell.isMarked())
            ++markerCount;
        else
            --markerCount;

        cell.toggleMark();
        redrawCell(row, col);

        markerCounter.display(markerCount); //Update marker counter
    }

    /** 
     * Returns true if the player has won:
     * If all the cells without a mine have been exposed, then the player has won.
     */
    public boolean hasWon(){
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++) {
                if(!cells[row][col].hasMine() && !cells[row][col].isExposed())
                    return false;
            }
        }
        return true;
    }

    /**
     * Redraws a cell at a given board position on the screen
     */
    public void redrawCell(int row, int col)
    {
        cells[row][col].draw(LEFT+col*CELL_SIZE, TOP+row*CELL_SIZE, CELL_SIZE);
    }

    /**
     * Construct a grid with random mines.
     */
    public void makeGrid(){
        UI.clearGraphics();
        gameRunning = false;
        time = 0;
        lost = false;
        won = false;
        //--------MAIN BODY---------

        //Draw main game block
        UI.setColor(Color.decode("#BDBDBD"));
        UI.fillRect(0, 0, COLS * CELL_SIZE + (LEFT * 2), ROWS * CELL_SIZE + TOP + LEFT);

        //Draw main inner dark shadow
        UI.setColor(Color.decode("#7B7B7B"));
        UI.fillPolygon(new double[] { LEFT - 2.5, COLS * CELL_SIZE + LEFT + 2.5, LEFT - 2.5 }, new double[] { TOP - 2.5, TOP - 2.5, ROWS * CELL_SIZE + TOP + 2.5 }, 3);

        //Draw main inner light shadow
        UI.setColor(Color.decode("#EEEEEE"));
        UI.fillPolygon(new double[] { COLS * CELL_SIZE + LEFT + 2.5, COLS * CELL_SIZE + LEFT + 2.5, LEFT - 2.5 }, new double[] { ROWS * CELL_SIZE + TOP + 2.5, TOP - 2.5, ROWS * CELL_SIZE + TOP + 2.5 }, 3);

        //---------HEADER------------

        //Draw header inner dark shadow
        UI.setColor(Color.decode("#7B7B7B"));
        UI.fillRect(LEFT - 2.5, LEFT - 2.5, COLS * CELL_SIZE + 5, TOP - (LEFT * 2));

        //Draw header inner light shadow
        UI.setColor(Color.decode("#EEEEEE"));
        UI.fillPolygon(new double[] { COLS * CELL_SIZE + LEFT + 2.5, COLS * CELL_SIZE + LEFT + 2.5, COLS * CELL_SIZE - TOP + (LEFT * 3) }, new double[] { TOP - LEFT - 2.5, LEFT - 2.5, TOP - LEFT - 2.5 }, 3);
        UI.fillPolygon(new double[] { LEFT - 2.5, 40, 40 }, new double[] { TOP - LEFT - 2.5, TOP - 42.5, TOP - LEFT - 2.5 }, 3);
        UI.fillRect(LEFT + 10, TOP - LEFT - 10, COLS * CELL_SIZE - 10, 7);

        //Draw header ill
        UI.setColor(Color.decode("#BDBDBD"));
        UI.fillRect(LEFT, LEFT, COLS * CELL_SIZE, TOP - (LEFT * 2) - 5);

        int mineCount = 0;

        this.cells = new Cell[ROWS][COLS];
        for (int row=0; row < ROWS; row++){
            double y = TOP+row*CELL_SIZE;
            for (int col=0; col<COLS; col++){
                double x =LEFT+col*CELL_SIZE;
                boolean isMine = Math.random()<0.1;     // approx 1 in 10 cells is a mine
                if(isMine)
                    ++mineCount;
                this.cells[row][col] = new Cell(isMine);
                this.cells[row][col].draw(x, y, CELL_SIZE);
            }
        }
        // now compute the number of adjacent mines for each cell
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                int count = 0;
                //look at each cell in the neighbourhood.
                for (int r=Math.max(row-1,0); r<Math.min(row+2, ROWS); r++){
                    for (int c=Math.max(col-1,0); c<Math.min(col+2, COLS); c++){
                        if (cells[r][c].hasMine())
                            count++;
                    }
                }
                if (this.cells[row][col].hasMine())
                    count--;  // we weren't suppose to count this cell, just the adjacent ones.

                this.cells[row][col].setAdjacentMines(count);
            }
        }
        drawFace(0);
        markerCount = mineCount;
        markerCounter.display(markerCount);
        timeCounter.display(time);
    }

    /** Draw a message telling the player they have won */
    public void drawWin(){
        won = true;
        gameRunning = false;

        //Show all bombs
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                cells[row][col].setMarked(true);
                redrawCell(row, col);
            }
        }

        markerCount = 0;
        markerCounter.display(markerCount);

        //Draw winning B) face
        drawFace(3);
        UI.setFontSize(28);
        UI.drawString("You Win!", LEFT + COLS*CELL_SIZE + 20, TOP + ROWS*CELL_SIZE/2);
        UI.setFontSize(12);
    }

    /**
     * Draw a message telling the player they have lost
     * and expose all the cells and redraw them
     */
    public void drawLose(){
        lost = true;
        gameRunning = false;
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                cells[row][col].setExposed();
                cells[row][col].draw(LEFT+col*CELL_SIZE, TOP+row*CELL_SIZE, CELL_SIZE);
            }
        }
        UI.setFontSize(28);
        UI.drawString("You Lose!", LEFT + COLS*CELL_SIZE+20, TOP + ROWS*CELL_SIZE/2);
        UI.setFontSize(12);
        drawFace(2);
    }

    /**
     * Method to calculate if the cursor is on the face reset button
     * Used when detecting a click
     */
    public boolean isPointOnFace(double x, double y)
    {
        double faceX = LEFT + (COLS * CELL_SIZE / 2) - (FACE_BUTTON_SIZE / 2);
        double faceY = (TOP / 2) - (FACE_BUTTON_SIZE / 2) - 2.5;

        return (x >= faceX && y >= faceY && x <= faceX + FACE_BUTTON_SIZE && y <= faceY + FACE_BUTTON_SIZE);
    }

    public void drawFace(int state)
    {
        UI.setLineWidth(1);
        double x = LEFT + (COLS * CELL_SIZE / 2) - (FACE_BUTTON_SIZE / 2);
        double y = (TOP / 2) - (FACE_BUTTON_SIZE / 2) - 2.5;

        if(state == 4) //State 4 is clicked state
            UI.setColor(Color.decode("#7B7B7B"));
        else
            UI.setColor(Color.decode("#EEEEEE"));
        //Light shadow
        UI.fillRect(x, y, FACE_BUTTON_SIZE, FACE_BUTTON_SIZE);

        //Dark shadow
        if(state == 4)
            UI.setColor(Color.decode("#EEEEEE"));
        else
            UI.setColor(Color.decode("#7B7B7B"));
        UI.fillPolygon(new double[] { x, x + FACE_BUTTON_SIZE, x + FACE_BUTTON_SIZE }, new double[] { y + FACE_BUTTON_SIZE, y, y + FACE_BUTTON_SIZE }, 3);

        //Main fill
        UI.setColor(Color.decode("#BDBDBD"));
        UI.fillRect(x + 2, y + 2.5, FACE_BUTTON_SIZE - 5, FACE_BUTTON_SIZE - 5);

        if(state == 4)
        {
            //Shift down and right when clicked
            x += 1;
            y += 1;
        }

        //Face fill
        UI.setColor(Color.yellow);
        UI.fillOval(x + 5, y + 5, FACE_BUTTON_SIZE - 12, FACE_BUTTON_SIZE - 12);

        //Face outline
        UI.setColor(Color.black);
        UI.drawOval(x + 5, y + 5, FACE_BUTTON_SIZE - 12, FACE_BUTTON_SIZE - 12);

        switch(state)
        {
            case 4: //Clicked state
            case 0: //(: Default face state
                //Eyes
                UI.fillOval(x + 10, y + 10, 3, 3.5);
                UI.fillOval(x + 19, y + 10, 3, 3.5);

                //Mouth
                UI.setLineWidth(2);
                UI.getGraphics().drawArc((int)x + 11, (int)y + 16, 12, 8, 180, 180); //Face sometimes fails to draw, draw it twice
                UI.getGraphics().drawArc((int)x + 11, (int)y + 16, 12, 8, 180, 180);
                break;
            case 1: //O: careful state
                //Eyes
                UI.fillOval(x + 9, y + 10, 5, 5);
                UI.fillOval(x + 18, y + 10, 5, 5);

                //Mouth
                UI.setLineWidth(1.5);
                UI.drawOval(x + 14, y + 18, 6, 6);
                break;
            case 2: //)X dead state
                //Eyes
                UI.setLineWidth(2);
                UI.drawLine(x + 11, y + 11, x + 14, y + 14);
                UI.drawLine(x + 11, y + 14, x + 14, y + 11);

                UI.drawLine(x + 19, y + 11, x + 22, y + 14);
                UI.drawLine(x + 19, y + 14, x + 22, y + 11);

                //Mouth
                UI.getGraphics().drawArc((int)x + 11, (int)y + 18, 12, 8, 1, 180);
                break;
            case 3: //B) win state
                //Eyes
                UI.fillOval(x + 9, y + 10, 7, 7);
                UI.fillOval(x + 17, y + 10, 7, 7);
                UI.setLineWidth(3);
                UI.drawLine(x + 13, y + 11, x + 19, y + 11);

                //Sunglasses sides
                UI.setLineWidth(2);
                UI.drawLine(x + 6, y + 15, x + 10, y + 11);
                UI.drawLine(x + 23, y + 11, x + 27, y + 15);
                //Mouth
                UI.setLineWidth(2);
                UI.getGraphics().drawArc((int)x + 11, (int)y + 16, 12, 8, 180, 180); //Face sometimes fails to draw, draw it twice
                UI.getGraphics().drawArc((int)x + 11, (int)y + 16, 12, 8, 180, 180);
                break;

        }
        UI.setLineWidth(1);
    }

    // Main
    public static void main(String[] arguments){
        new MineSweeper();
    }        

}
