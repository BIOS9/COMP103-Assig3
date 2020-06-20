// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;
import java.awt.Color;

/** Represents information about one cell in a MineSweeper map.
 *   It records
 *     its location (Row and Column)
 *     whether it has a mine or not
 *     how many cells around it have mines
 *     whether it is marked or unmarked
 *     whether it is hidden or exposed
 *   Its constructor must specify the location and
 *     whether it has a mine or not
 *   It has methods to 
 *     draw itself, (showing its state appropriately) given origin of the map.
 *     set the number of mines around it.
 *     report whether it has a mine and whether it is exposed
 *     change its state between marked and unmarked
 *     change its state to exposed
 */
public class Cell{
    // Fields
    private boolean mine;
    private int adjacentMines = 0;
    private boolean marked = false;
    private boolean markedSafe = false;
    private boolean exposed = false;

    public static final Color LIGHT_GREEN = new Color(0,255,0);
    public static final Color DARK_GREEN = new Color(0,127,0);

    // Constructors
    /** Construct a new Cell object
     */
    public Cell(boolean mine){
        this.mine = mine;
    }

    // Methods
    /** Get the number of mines adjacent to this cell  */
    public int getAdjacentMines(){
        return adjacentMines;
    }

    /** Record the number of adjacent mines */
    public void setAdjacentMines(int num){
        adjacentMines = num;
    }

    /** Does the cell contain a mine? */
    public boolean hasMine(){
        return mine;
    }

    /** Is the cell exposed already? */
    public boolean isExposed(){
        return this.exposed;
    }

    /** Is the cell currently marked? */
    public boolean isMarked(){
        return this.marked;
    }

    /** Set the cell to be marked */
    public void setMarked(boolean marked){
        this.marked = marked;
        if(marked)
            markedSafe = false;
    }

    /** Set the cell to be exposed? */
    public void setExposed(){
        this.exposed = true;
    }

    /** set the safe status of the cell, for use with the AI class */
    public void setMarkedSafe(boolean safe)
    {
        this.markedSafe = safe;
        if(safe)
            marked = false;
    }

    /** Toggle the mark */
    public void toggleMark(){
        this.marked = !this.marked;
        if(marked)
            markedSafe = false;
    }

    /** Draw the cell */
    public void draw(double x, double y, double size){
        if (exposed){ drawExposed(x, y, size); }
        else        { drawHidden(x, y, size); }
    }

    /** Draw white outline and red number or mine */
    private void drawExposed(double x, double y, double size){
        UI.setLineWidth(1);
        UI.setColor(Color.decode("#8A8A8A"));
        UI.drawRect(x, y, size - 1, size - 1);
        //UI.setColor(Color.decode("#adadad"));
        UI.setColor(Color.decode("#BDBDBD"));
        UI.fillRect(x + 1, y + 1, size - 2, size - 2);
        if (mine){
            UI.setColor(Color.black);
            //Draw cross
            UI.fillOval(x + 4 , y + 4, size - 8, size - 8);
            UI.drawLine(x + 3, y + 3, x + size - 3, y + size - 3);
            UI.setLineWidth(2);
            UI.drawLine(x + size - 3, y + 3, x + 3, y + size - 3);
            UI.drawLine(x + (size/2), y + 3, x + (size/2), y + size - 3);
            UI.drawLine(x + 3, y + (size/2), x + size - 3, y + (size/2));

            //Draw white reflection
            UI.setColor(Color.white);
            UI.fillOval(x + 7, y + 7, 3, 3);
        }
        else if (adjacentMines > 0){
            //Set text color based on number
            switch (adjacentMines)
            {
                //Standard minesweeper text colours
                case 1: UI.setColor(Color.decode("#0100FE")); break;
                case 2: UI.setColor(Color.decode("#017F01")); break;
                case 3: UI.setColor(Color.decode("#FE0000")); break;
                case 4: UI.setColor(Color.decode("#010080")); break;
                case 5: UI.setColor(Color.decode("#810102")); break;
                case 6: UI.setColor(Color.decode("#008081")); break;
                case 7: UI.setColor(Color.decode("#000000")); break;
                case 8: UI.setColor(Color.decode("#7F7F7F")); break;
            }
            UI.setFontSize(16);
            UI.drawString(""+adjacentMines, x+size/2-5, y+size/2+5);
            UI.drawString(""+adjacentMines, x+size/2-4, y+size/2+5);
        }
    }

    /** Fill dark green with red mark */
    private void drawHidden(double x, double y, double size){
        //UI.setColor(DARK_GREEN);
        //UI.fillRect(x+1, y+1, size-2, size-2);

        //Draw light shadow
        UI.setColor(Color.decode("#EEEEEE"));
        UI.fillPolygon(new double[] { x, x + size, x }, new double[] { y, y, y + size }, 3);

        //Draw dark shadow
        UI.setColor(Color.decode("#7B7B7B"));
        UI.fillPolygon(new double[] { x + size, x + size, x }, new double[] { y, y + size, y + size }, 3);

        //Draw cell fill
        UI.setColor(Color.decode("#BDBDBD"));
        UI.fillRect(x + 3, y + 3, size - 6, size - 6);

        if (marked || markedSafe){

            //Draw base and flagpole
            UI.setColor(Color.black);
            UI.setLineWidth(3);
            UI.drawLine(x + 5, y + 15, x + 14, y + 15);
            UI.setLineWidth(2);
            UI.drawLine(x + 7, y + 13, x + 13, y + 13);
            UI.drawLine(x + 10, y + 13, x + 10, y + 5);

            //Choose red or green flag
            if(marked)
                UI.setColor(Color.red);
            else
                UI.setColor(Color.green.darker());

            //Draw flag
            UI.drawPolygon(
                    new double[] { x + 10, x + 10, x + 6 },
                    new double[] { y + 5, y + 9, y + 7 },
                    3);
        }
    }

}
