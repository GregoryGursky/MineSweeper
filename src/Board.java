import java.util.HashSet;
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
            row = (short) rng.nextInt(height);
            col = (short) rng.nextInt(width);
            if (board[row][col] == null){
                board[row][col] = new Tile((short) 9, col, row);
                mineLocations[minesMade++] = new TileLoc(col,row);
            }
        }
    }

    private void createNumbers() {
        for (short row = 0; row < height; row++) {
            for (short col = 0; col < width; col++) {
                if (board[row][col] == null){
                    short val = getVal(row,col);
                    board[row][col] = new Tile(val, col, row);
                }
            }
        }
    }

    private short getVal(short row, short col){
        short minesTouching = 0;
        for (short heightOffSet = (short) (row - 1); heightOffSet < row + 2; heightOffSet++) { // iterate adj tiles
            if (validRow(heightOffSet)){
                for (short widthOffSet = (short) (col - 1); widthOffSet < col + 2; widthOffSet++) {
                    if (validCol(widthOffSet)){
                        if (board[heightOffSet][widthOffSet] != null && board[heightOffSet][widthOffSet].getVal() == 9)
                            minesTouching++;
                    }
                }
            }
        }
        return minesTouching;
    }

    public short getTileVal(short row, short col){
        return board[row][col].getVal();
    }

    public GUI_Response rightClickTile(TileLoc clicked){
        Tile clickedTile = board[clicked.row()][clicked.col()];
        if (!clickedTile.isClicked()){ // only able to flag non-clicked tiles
            clickedTile.setFlag(); // change the flag val
            if (board[clicked.row()][clicked.col()].isFlagged())
                return new GUI_Response(new TileLoc[]{clicked}, TileGUI.FLAG, true);
            else
                return new GUI_Response(new TileLoc[]{clicked}, TileGUI.UNFLAG, true);
        }
        return null;
    }

    public GUI_Response leftClickTile(TileLoc clicked){
        Tile clickedTile = board[clicked.row()][clicked.col()];
        if (!clickedTile.isFlagged()){ // can only click non-flagged tiles
            if (!clickedTile.isClicked()){ // clicked a new tile
                short tileVal = clickedTile.getVal();
                return switch (tileVal) {
                    case 9 -> endGame();
                    case 0 -> clickSurrounding(clicked);
                    default -> clickSelf(clicked);
                };
            } //else // clicked a revealed tile
//                return clickSurrounding(clicked);
        }
        return null;
    }

    private GUI_Response endGame(){
        return new GUI_Response(mineLocations,TileGUI.BOMB, false);
    }

    private GUI_Response clickSelf(TileLoc clicked){
        Tile clickedTile = board[clicked.row()][clicked.col()];
        if (!clickedTile.isClicked()) { // if not clicked
            clickedTile.setClicked();
            return new GUI_Response(new TileLoc[] {clicked},TileGUI.NUMBER , (++tilesClicked != tilesNeeded));
        } else {
            //return clickSurrounding(clicked);
            return new GUI_Response(new TileLoc[]{clicked},TileGUI.NUMBER , (++tilesClicked != tilesNeeded));

        }
    }

    private GUI_Response clickSurrounding(TileLoc clicked){
        Tile firstClick = board[clicked.row()][clicked.col()];
        firstClick.setClicked();
        LinkedList<Tile> adjTiles = getAdjZeros(firstClick);
        HashSet<Tile> allAdj = getAllZeroes(adjTiles);
        TileLoc[] reveal = tileLocConverter(allAdj);
        reveal[reveal.length - 1] = clicked; // getAdjZeros() has a check on tile.isClicked
        return new GUI_Response(reveal, TileGUI.NUMBER, !(tilesClicked == tilesNeeded));
    }

    private LinkedList<Tile> getAdjZeros(Tile clicked) {
        LinkedList<Tile> adjTiles = new LinkedList<>();
        for (short rowOff = (short) (clicked.getRow() - 1); rowOff < clicked.getRow() + 2; rowOff++) {
            if (validRow(rowOff)){
                for (short colOff = (short) (clicked.getCol() - 1); colOff < clicked.getCol() + 2; colOff++) {
                    if (validCol(colOff)){
                        if (!board[rowOff][colOff].isClicked()){
                            board[rowOff][colOff].setClicked();
                            adjTiles.add(board[rowOff][colOff]);
                            tilesClicked++;
                        }
                    }
                }
            }
        }
        return adjTiles;
    }

    private HashSet<Tile> getAllZeroes(LinkedList<Tile> zeros) {
        HashSet<Tile> allAdjTiles = new HashSet<>(zeros);
        Stack<Tile> newAdjTiles = new Stack<>();
        newAdjTiles.addAll(zeros);

        while (newAdjTiles.size() > 0){
            Tile adjTile = newAdjTiles.pop();
            adjTile.setClicked();
            tilesClicked++;
            if (adjTile.getVal() == 0){
                for (short rowOff = (short) (adjTile.getRow() - 1); rowOff < adjTile.getRow() + 2; rowOff++) {
                    if (validRow(rowOff)){
                        for (short colOff = (short) (adjTile.getCol() - 1); colOff < adjTile.getCol() + 2; colOff++){
                            if (validCol(colOff)){
                                Tile tileOff = board[rowOff][colOff];
                                if (!tileOff.isClicked()){
                                    if (allAdjTiles.add(tileOff)){
                                        newAdjTiles.add(tileOff);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return allAdjTiles;
    }

    private boolean validCol(short col) {
        return (col >= 0 && col < width);
    }

    private boolean validRow(short row) {
        return (row >= 0 && row < height);
    }

    private TileLoc[] tileLocConverter(HashSet<Tile> allAdjTiles) {
        short maxIndex = (short) ((short) allAdjTiles.size() + 1); // see clickSurrounding()
        short index = 0;
        TileLoc[] converted = new TileLoc[maxIndex];
        for (Tile tile: allAdjTiles) {
            converted[index] = new TileLoc(tile.getCol(), tile.getRow());
            index++;
        }
        return converted;
    }

    public void printBoard(){
        for (short i = 0; i < height; i++) {
            for (short j = 0; j < width; j++) {
                System.out.print(board[i][j].getVal() + " ");
            }
            System.out.println("");
        }
    }

    public boolean isClicked(short rowOff, short colOff) {
        return board[rowOff][colOff].isClicked();
    }

    public boolean isFlagged(short rowOff, short colOff) {
        return board[rowOff][colOff].isFlagged();
    }
}
