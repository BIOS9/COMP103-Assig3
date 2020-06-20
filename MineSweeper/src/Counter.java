/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.UI;

import java.awt.*;

/**
 * Combines multiple segment displays into a counter that can display positive and negative whole numbers
 */
public class Counter {
    private static final double PADDING = 1.5;

    public double x, y;
    public double scale;
    public final double width, height;
    private int displays;
    SegmentDisplay[] segmentDisplays; //Segments to use in the display

    /**
     * Constructor
     * @param x Global X position of the counter
     * @param y Global Y position of the counter
     * @param displays How many "places" or displays to use
     * @param scale How large to make the display
     */
    public Counter(double x, double y, int displays, double scale)
    {
        this.displays = displays;
        this.x = x;
        this.y = y;
        this.scale = scale;

        //Initialize displays
        segmentDisplays = new SegmentDisplay[displays];
        for(int i = 0; i < displays; ++i)
        {
            segmentDisplays[i] = new SegmentDisplay((i * (SegmentDisplay.WIDTH + PADDING) * scale) + (PADDING * scale) + x, PADDING * scale + y, scale);
        }

        width = (SegmentDisplay.WIDTH + PADDING) * scale * displays + PADDING*scale;
        height = (SegmentDisplay.HEIGHT + PADDING*2) * scale;
    }

    /**
     * Display a number on the counter
     * @param value Number to display
     */
    public void display(int value)
    {
        UI.setColor(Color.black);
        UI.fillRect(x, y, width, height);


        for(int i = 0; i < displays; ++i)
        {
            //If number is negative, dedicate one segment to display a '-' sign
            if(value < 0 && i == displays - 1)
                segmentDisplays[0].drawNegative();
            else {
                int place = Math.abs(value % (int) Math.pow(10, i + 1)); //Get one place from the number to display
                place /= (int) Math.pow(10, i); //Shift the place to the ones unit so its within 0 <= num < 10
                segmentDisplays[displays - i - 1].draw(place);
            }
        }
    }


}
