/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Handles and differentiates right and left click for the game
 */
public class MouseHandler implements MouseListener {
    MineSweeper sweeper; //Minesweeper instance that is using this handler

    public MouseHandler(MineSweeper sweeper)
    {
        this.sweeper = sweeper;
    }

    public void mousePressed(MouseEvent e) {
        if(sweeper.isPointOnFace(e.getX(), e.getY())) { //If the smiley face has been clicked
            sweeper.drawFace(4); //Draw clicked status
            sweeper.faceClicked = true;
        }
        else if(SwingUtilities.isLeftMouseButton(e))
            sweeper.drawFace(1); //Draw :O face
    }

    public void mouseReleased(MouseEvent e) {
        if(sweeper.faceClicked) { //Release on face clicked
            sweeper.faceClicked = false;
            sweeper.makeGrid(); //Reset game
        }
        else {
            if(sweeper.lost)
                sweeper.drawFace(2); //Draw x( dead face
            else if(sweeper.won)
                sweeper.drawFace(3); //Draw B) face
            else sweeper.drawFace(0); //Draw standard :) face
        }

        if(sweeper.lost || sweeper.won) return;

        //Get cell clicked
        int row = (int)((e.getY()-MineSweeper.TOP)/MineSweeper.CELL_SIZE);
        int col = (int)((e.getX()-MineSweeper.LEFT)/MineSweeper.CELL_SIZE);

        if (row>=0 && row < MineSweeper.ROWS && col >= 0 && col < MineSweeper.COLS){ //Check within bounds
            if (SwingUtilities.isRightMouseButton(e)) { sweeper.mark(row, col);}
            else if(SwingUtilities.isLeftMouseButton(e)){ sweeper.tryExpose(row, col); }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }
}
