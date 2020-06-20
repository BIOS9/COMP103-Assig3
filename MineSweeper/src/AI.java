/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

/**
 * Basic Minesweeper AI to recommend actions to the user.
 * Can solve basic boards where there is no guessing involved.
 */
public class AI {

    /**
     * Converts board of Cell objects into a board of integers.
     * Each integer is the value of adjacent mines.
     *
     * This method is for use with the other methods of the AI class/
     *
     * @param board Game board
     * @return Integer board
     */
    public static int[][] createVisibleIntBoard(Cell[][] board)
    {
        int[][] newBoard = new int[board.length][board[0].length];

        for(int row = 0; row < board.length; ++row)
            for(int col = 0; col < board[row].length; ++col)
            {
                if(board[row][col].isExposed()) //Only use cells that are exposed to user
                    newBoard[row][col] = board[row][col].getAdjacentMines();
                else if(board[row][col].isMarked()) //Marked cells have a 100% chance of having a bomb
                    newBoard[row][col] = 100;
                else //Cell is hidden
                    newBoard[row][col] = -1;
            }

        return newBoard;
    }

    /**
     * Find and return available game actions.
     * @param board Board to solve a layer of
     * @return Returns 2d array of actions. One action is returned for each cell, -1 is no action, 0 is a safe cell, 1 is a cell with a mine
     */
    public static int[][] createMoves(int[][] board)
    {
        int[][] boardSolution = new int[board.length][board[0].length];

        //Initialize solution as all unknown/no action
        for(int row = 0; row < board.length; ++row)
            for(int col = 0; col < board[row].length; ++col)
                boardSolution[row][col] = -1;


        for(int row = 0; row < board.length; ++row)
        for(int col = 0; col < board[row].length; ++col)
        {
            if(board[row][col] > 0) //If the cell has any adjacent mines
            {
                int[][] square = getAdjacentSquare(row, col, board);
                int[][] solution = solveSquare(square); //Attempt to solve this cell

                //Apply solution
                for(int sRow = -1; sRow <= 1; ++sRow)
                for(int sCol = -1; sCol <= 1; ++sCol)
                {
                    if(row+sRow >= 0 && row+sRow < board.length && col+sCol >= 0 && col+sCol < board[0].length) //Ensure cell is within the game board
                        //If the cell doesn't already have a defined action, apply an action
                        if(boardSolution[row + sRow][col + sCol] == -1)
                            boardSolution[row + sRow][col + sCol] = solution[sRow+1][sCol+1];
                }
            }
        }
        return boardSolution;
    }

    /**
     * Get 3x3 square of cells around and including a cell at s specified position
     * @param row Row of cell to get cells around
     * @param col Column of cell to get cells around
     * @param board Board to get cells from
     * @return 3x3 square of adjacent cells
     */
    private static int[][] getAdjacentSquare(int row, int col, int[][] board)
    {
        int[][] square = new int[3][3];
        for(int r = -1; r <= 1; ++r)
        for(int c = -1; c <= 1; ++c)
            if(row + r >= 0 && row + r < board.length && col + c >= 0 && col + c < board[0].length) //Ensure cell is inside the board
                square[r+1][c+1] = board[r + row][c + col];
            else
                square[r+1][c+1] = -2; //Invalid square

        return square;
    }

    /**
     * Attempts to solve a 3x3 square of the board.
     * @param square Integer square of adjacent mines. Each integer should contain the number of adjacent mines
     * @return Recommended actions. -1 = no action/unknown, 0 = safe, 1 = mine
     */
    private static int[][] solveSquare(int[][] square)
    {
        int[][] solution = new int[3][3];

        //Initialize solution as unknown
        for(int row = 0; row < 3; ++row)
        for(int col = 0; col < 3; ++col)
        {
            solution[row][col] = -1;
        }

        //Get mine count from middle square
        int mines = square[1][1];
        int unknowns = 0; //Number of covered cells

        for(int row = 0; row < 3; ++row)
        for(int col = 0; col < 3; ++col)
        {
            //If there is a marked square, remove one of the mines
            if(square[row][col] == 100)
                --mines;
            else if(square[row][col] == -1) //If cell is unknown/covered
                unknowns++;
        }

        square[1][1] = 0; //Zero chance of middle square being a mine

        if(unknowns == 0)
            return solution;

        //Probability that the adjacent cells have mines
        double distributedProbability = (double) mines / (double) unknowns;

        /*
        This could be extended in the future to choose cell with lowest
        probability when there is no 100% correct solution.
         */

        //If there is no definite solution
        if(distributedProbability != 0 && distributedProbability != 1)
            return solution;

        //Set square probabilities
        for(int row = 0; row < 3; ++row)
        for(int col = 0; col < 3; ++col)
        {
            if(square[row][col] == -1) //If cell is unknown/covered
                solution[row][col] = (int)distributedProbability;
        }

        return solution;
    }
}
