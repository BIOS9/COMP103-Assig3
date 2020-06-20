/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.UI;

import java.awt.*;

/**
 * One 7-segment digit display for the counters
 */
public class SegmentDisplay {
    public double x, y;
    public double scale = 1;
    public static final double HEIGHT = 16.5;
    public static final double WIDTH = 8.5;

    //X position the vertexes for each segment
    private double[][] segmentsX = new double[][]{
            {0, 8, 6, 2},
            {2, 2, 0, 0},
            {0, 8, 6, 2},
            {0, 0, 2, 2},
            {0, 1.5, 5.5, 7, 5.5, 1.5}
    };

    //Y position the vertexes for each segment
    private double[][] segmentsY = new double[][]{
            {0, 0, 2, 2},
            {0, 8, 6, 2},
            {2, 2, 0, 0},
            {0, 8, 6, 2},
            {1.5, 0, 0, 1.5, 3, 3}
    };

    //Offsets used to move segments to different places to create different numbers
    private double[][] segmentOffsets = new double[][]{
            {0.25, 0},
            {6.5, 0.25},
            {6.5, 8.25},
            {0.25, 14.5},
            {0, 8.25},
            {0, 0.25},
            {0.75, 6.75},
    };

    //Stores what segments should be turned on for each number
    private boolean[][] numberMasks = new boolean[][]{
            { true, true, true, true, true, true, false }, //0
            { false, true, true, false, false, false, false }, //1
            { true, true, false, true, true, false, true }, //2
            { true, true, true, true, false, false, true }, //3
            { false, true, true, false, false, true, true }, //4
            { true, false, true, true, false, true, true }, //5
            { true, false, true, true, true, true, true }, //6
            { true, true, true, false, false, false, false }, //7
            { true, true, true, true, true, true, true }, //8
            { true, true, true, true, false, true, true }, //9

    };

    public SegmentDisplay(double x, double y, double scale)
    {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    /**
     * Displays a number on the segment
     * @param number Number to display
     */
    public void draw(int number)
    {
        UI.setColor(Color.black);
        UI.fillRect(x, y, WIDTH * scale, HEIGHT * scale);;

        //Draw segments using the mask for the specified number
        drawSegment(0, 0, numberMasks[number][0]);
        drawSegment(1, 1, numberMasks[number][1]);
        drawSegment(1, 2, numberMasks[number][2]);
        drawSegment(2, 3, numberMasks[number][3]);
        drawSegment(3, 4, numberMasks[number][4]);
        drawSegment(3, 5, numberMasks[number][5]);
        drawSegment(4, 6, numberMasks[number][6]);
    }

    /**
     * Displays a negative sign instead of a number
     */
    public void drawNegative()
    {
        UI.setColor(Color.black);
        UI.fillRect(x, y, WIDTH * scale, HEIGHT * scale);;

        drawSegment(0, 0, false);
        drawSegment(1, 1, false);
        drawSegment(1, 2, false);
        drawSegment(2, 3, false);
        drawSegment(3, 4, false);
        drawSegment(3, 5, false);
        drawSegment(4, 6, true);
    }

    /**
     * Draws one segment as either on or off
     * @param index What segment style to draw
     * @param offsetIndex What position offset to use
     * @param on Whether the segment is on or off
     */
    private void drawSegment(int index, int offsetIndex, boolean on) {
        double[] tempX = new double[segmentsX[index].length];
        double[] tempY = new double[segmentsY[index].length];

        //Get the x positions of the translated vertexes
        for (int i = 0; i < segmentsX[index].length; ++i)
            tempX[i] = x + (segmentsX[index][i] + segmentOffsets[offsetIndex][0]) * scale;

        //Get the y positions of the translated vertexes
        for (int i = 0; i < segmentsY[index].length; ++i)
            tempY[i] = y + (segmentsY[index][i] + segmentOffsets[offsetIndex][1]) * scale;

        //Draw
        UI.setColor(on ? Color.red : Color.decode("#440000"));
        UI.fillPolygon(tempX, tempY, tempX.length);
    }


}
