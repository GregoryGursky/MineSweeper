import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class Board {
    private final short width;
    private final short height;
    private final short mines;
    private final short tilesNeeded;
    private short tilesClicked;
    private final Tile[][] board;
    private TileLoc[] mineLocations;

    public Board (short width , short height, short mines){
        this.height = height;
        this.width = width;
        this.mines = mines;
        board = new Tile[height][width];
        tilesNeeded = (short) (width*height-mines);
        tilesClicked = 0;
        createMines();
        createNumbers();
    }


    private void createMines(){
        Random rng = new Random();
        short col, row;
        short minesMade = 0;
        mineLocations = new TileLoc[mines];
        while (minesMade < mines){
            col = (short) rng.nextInt(height);
            row = (short) rng.nextInt(width);
            if (board[col][row] == null){
                board[col][row] = new Tile((short) 9, col,row);
                mineLocations[minesMade] = new TileLoc(col,row);
                minesMade++;
            }
        }
    }

    private void createNumbers() {
        for (short col = 0; col < height; col++) {
            for (short row = 0; row < width; row++) {
                if (board[col][row] == null){
                    short val = getVal(col,row);
                    board[col][row] = new Tile(val, col, row);
                }
            }
        }
    }


    private short getVal(int col, int row){
        short minesTouching = 0;
        for (int heightOffSet = -1; heightOffSet <= 1; heightOffSet++) { // iterate adj tiles
            for (int widthOffSet = -1; widthOffSet <= 1; widthOffSet++) {
                int tempCol = col + heightOffSet;
                int tempRow = row + widthOffSet;
                if (tempCol >= 0 && tempCol < height){ // bounds check
                    if (tempRow >= 0 && tempRow < width){
                        if (board[tempCol][tempRow] != null && board[tempCol][tempRow].getVal() == 9){
                            minesTouching++;
                        }
                    }
                }
            }
        }
        return minesTouching;
    }

    public GUI_Response rightClickTile(TileLoc clicked){
        Tile clickedTile = board[clicked.col()][clicked.row()];
        if (!clickedTile.isClicked()){ // only able to flag non-clicked tiles
            clickedTile.setFlag(); // change the flag val
            if (board[clicked.col()][clicked.row()].isFlagged())
                return new GUI_Response(new TileLoc[]{clicked}, TileGUI.FLAG, true);
            else
                return new GUI_Response(new TileLoc[]{clicked}, TileGUI.UNFLAG, true);
        }
        return null;
    }

    public GUI_Response leftClickTile(TileLoc clicked){
        Tile clickedTile = board[clicked.col()][clicked.row()];
        if (!clickedTile.isFlagged()){ // can only click non-flagged tiles
            if (!clickedTile.isClicked()){ // clicked a new tile
                short tileVal = (short) clickedTile.getVal();
                return switch (tileVal) {
                    case 9 -> endGame();
                    case 0 -> clickSurrounding(clicked);
                    default -> clickSelf(clicked);
                };
            } else // clicked a revealed tile
                return clickSurrounding(clicked);
        }
        return null;
    }

    private GUI_Response endGame(){
        return new GUI_Response(mineLocations,TileGUI.BOMB, false);
    }

    private GUI_Response clickSelf(TileLoc clicked){
        Tile clickedTile = board[clicked.col()][clicked.row()];
        if (!clickedTile.isClicked()) { // if not clicked
            clickedTile.setClicked();
            TileLoc[] reveal = {clicked};
            return new GUI_Response(reveal,TileGUI.NUMBER , (++tilesClicked == tilesNeeded));
        } else {
            return clickSurrounding(clicked);
        }
    }

    private GUI_Response clickSurrounding(TileLoc clicked){
        board[clicked.col()][clicked.row()].setClicked();
        // TODO add bounds check
        Stack<TileLoc> toReveal = new Stack<>();
        LinkedList<Tile> zeroes = getInitial(clicked, toReveal); // this should work b/c they reference the same memory

        if (!zeroes.isEmpty())
            toReveal.addAll(getAllZeroes(zeroes));

        tilesClicked += toReveal.size();
        return new GUI_Response(toReveal.toArray(new TileLoc[toReveal.size()]),TileGUI.NUMBER,(tilesClicked == tilesNeeded));
    }

    private LinkedList<Tile> getInitial(TileLoc clicked, Stack<TileLoc> toReveal){
        LinkedList<Tile> zeroes = new LinkedList<>();
        short offSetY;
        short offSetX;

        for (short colOff = -1; colOff < 2; colOff++) {
            offSetY = (short) (clicked.col() + colOff);
            for (short rowOff = -1; rowOff < 2; rowOff++) {
                offSetX = (short) (clicked.row() + rowOff);
                Tile adjTile = board[offSetY][offSetX];
                toReveal.add(new TileLoc(offSetY,offSetX));
                if (adjTile.getVal() == 0){
                    adjTile.setClicked();
                    zeroes.push(adjTile);
                }
            }
        }
        return zeroes;
    }

    private Stack<TileLoc> getAllZeroes(LinkedList<Tile> zeroes) {
        // TODO add bounds check
        Stack<TileLoc> allAdj = new Stack<>();
        while (!zeroes.isEmpty()){
            Tile nextZero = zeroes.pop();
            nextZero.setClicked();
            short offSetY;
            short offSetX;
            for (short rowOff = -1; rowOff < 2; rowOff++) { // for all  adj
                offSetY = (short) (nextZero.getRow() + rowOff);
                for (short colOff = -1; colOff < 2; colOff++) {
                    offSetX = (short) (nextZero.getCol() + colOff);
                    Tile adjTile = board[offSetY][offSetX];
                    allAdj.push(new TileLoc(offSetY,offSetX)); // add adj to return list
                    if (adjTile.getVal() == 0){
                        zeroes.push(adjTile);
                    }
                }
            }

        }
        return allAdj;
    }

    public void printBoard(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(board[i][j].getVal() + " ");
            }
            System.out.println("");
        }
    }
}
