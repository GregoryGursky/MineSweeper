import java.util.*;

public class Board {
    private final short width;
    private final short height;
    private final short mines;
    private boolean mineClicked;
    private final short tilesNeeded;
    private short tilesClicked;
    private final Tile[][] board;
    private TileLoc[] mineLocations;

    public Board(short width, short height, short mines, TileLoc tileLoc){
        this.height = height;
        this.width = width;
        this.mines = mines;
        board = new Tile[height][width];
        tilesNeeded = (short) (width*height-mines);
        tilesClicked = 0;
        mineClicked = false;
        HashSet<TileLoc> startingBox = startingLoc(tileLoc);
        createMines(startingBox);
        createNumbers();
    }

    private HashSet<TileLoc> startingLoc(TileLoc clicked){
        HashSet<TileLoc> startingBox = new HashSet<>();
        for (short rowOff = (short) (clicked.row() - 1); rowOff < clicked.row() + 2; rowOff++) {
            if (validRow(rowOff)){
                for (short colOff = (short) (clicked.col() - 1); colOff < clicked.col() + 2; colOff++) {
                    if (validCol(colOff)){
                        startingBox.add(new TileLoc(colOff,rowOff));
                    }
                }
            }
        }
        return startingBox;
    }

    private void createMines(HashSet<TileLoc> startingBox){
        Random rng = new Random();
        short col, row;
        short minesMade = 0;
        mineLocations = new TileLoc[mines];
        while (minesMade < mines){
            row = (short) rng.nextInt(height);
            col = (short) rng.nextInt(width);
            if (board[row][col] == null && !startingBox.contains(new TileLoc(col,row))){
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

    public TileLoc[] rightClickTile(TileLoc clicked){
        Tile clickedTile = board[clicked.row()][clicked.col()];
        if (!clickedTile.isClicked()){ // only able to flag non-clicked tiles
            clickedTile.setFlag(); // change the flag val
            if (clickedTile.isFlagged())
                clickedTile.setGui(TileGUI.FLAG);
            else clickedTile.setGui(TileGUI.UNFLAG);
            return new TileLoc[]{clicked};
        }
        return null;
    }

    public TileLoc[] leftClickTile(TileLoc clicked){
        Tile clickedTile = board[clicked.row()][clicked.col()];
        if (!clickedTile.isFlagged()){ // can only click non-flagged tiles
            TileLoc[] locs;
            if (!clickedTile.isClicked()){ // clicked a new tile
                short tileVal = clickedTile.getVal();
                switch (tileVal){
                    case (9):
                        return endGame();
                    case (0):
                        locs = clickSurrounding(clicked);
                        finalPrep(locs);
                        return locs;
                    default:
                        locs = clickSelf(clicked);
                        finalPrep(locs);
                        return locs;
                }
            } else { // clicked a revealed tile
                locs = secondClick(clicked);
                finalPrep(locs);
                return locs;
            }
        }
        return null;
    }

    private void finalPrep(TileLoc[] finalPrep){
        tilesClicked += finalPrep.length;
        setGuis(finalPrep);
    }

    private TileLoc[] secondClick(TileLoc clicked) {
        LinkedList<Tile> adjTiles = adjFlagCheck(board[clicked.row()][clicked.col()]);
        HashSet<TileLoc> reveal = new HashSet<>(); // I need a HSet vs ArrList for secondClick touching a zero
        if (adjTiles != null){
            for (Tile tile: adjTiles){
                if (tile.getVal() == 9){
                    reveal.addAll(List.of(endGame()));
                } else{
                    if (!tile.isClicked()){
                        reveal.add(new TileLoc(tile.getCol(),tile.getRow()));
                        if (tile.getVal() == 0){
                            TileLoc[] zeros = clickSurrounding(new TileLoc(tile.getCol(),tile.getRow()));
                            ArrayList<TileLoc> zerosArr = new ArrayList<>(List.of(zeros));
                            reveal.addAll(zerosArr);
                        }
                    }
                    tile.setClicked();
                }
            }
            return reveal.toArray(new TileLoc[0]);
        } else {
            return new TileLoc[]{};
        }
    }

    private TileLoc[] endGame(){
        mineClicked = true;
        for (TileLoc tileLoc: mineLocations){
            board[tileLoc.row()][tileLoc.col()].setGui(TileGUI.BOMB);
        }
        return mineLocations;
    }

    private TileLoc[] clickSelf(TileLoc clicked){
        Tile clickedTile = board[clicked.row()][clicked.col()];
        if (!clickedTile.isClicked()) { // if not clicked
            clickedTile.setClicked();
            clickedTile.setGui(TileGUI.NUMBER);
        }
        return new TileLoc[]{clicked};
    }

    private LinkedList<Tile> adjFlagCheck(Tile clickedTile) {
        short flagsNeeded = clickedTile.getVal();
        LinkedList<Tile> adjTiles = new LinkedList<>();
        if (flagsNeeded != 0){
            for (short rowOff = (short) (clickedTile.getRow() - 1); rowOff < clickedTile.getRow() + 2; rowOff++) {
                if (validRow(rowOff)){
                    for (short colOff = (short) (clickedTile.getCol() - 1); colOff < clickedTile.getCol() + 2; colOff++) {
                        if (validCol(colOff)){
                            if (board[rowOff][colOff].isFlagged()){
                                flagsNeeded--;
                            } else {
                                if (!board[rowOff][colOff].isClicked())
                                    adjTiles.add(board[rowOff][colOff]);
                            }
                        }
                    }
                }
            }
        }
        return (flagsNeeded == 0)? adjTiles: null;
    }

    private TileLoc[] clickSurrounding(TileLoc clicked){
        Tile firstClick = board[clicked.row()][clicked.col()];
        firstClick.setClicked();
        LinkedList<Tile> adjTiles = getAdjZeros(firstClick);
        HashSet<Tile> allAdj = getAllZeroes(adjTiles);
        allAdj.add(board[clicked.row()][clicked.col()]);
        return tileLocConverter(allAdj);
    }

    private LinkedList<Tile> getAdjZeros(Tile clicked) {
        LinkedList<Tile> adjTiles = new LinkedList<>();
        for (short rowOff = (short) (clicked.getRow() - 1); rowOff < clicked.getRow() + 2; rowOff++) {
            if (validRow(rowOff)){
                for (short colOff = (short) (clicked.getCol() - 1); colOff < clicked.getCol() + 2; colOff++) {
                    if (validCol(colOff)){
                        Tile adjTile = board[rowOff][colOff];
                        if (!adjTile.isClicked()){
                            adjTile.setClicked();
                            adjTile.setGui(TileGUI.NUMBER);
                            adjTiles.add(adjTile);
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

    private void setGuis(TileLoc[] tileLocs){
        for (TileLoc tileLoc: tileLocs){
            Tile tile = board[tileLoc.row()][tileLoc.col()];
            short val = tile.getVal();
            if (val == 9) {
                tile.setGui(TileGUI.BOMB);
            } else {
                tile.setGui(TileGUI.NUMBER);
            }
        }
    }

    private boolean validCol(short col) {
        return (col >= 0 && col < width);
    }

    private boolean validRow(short row) {
        return (row >= 0 && row < height);
    }

    private TileLoc[] tileLocConverter(HashSet<Tile> allAdjTiles) {
        short maxIndex = (short) ((short) allAdjTiles.size());
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

    public short getTileVal(short row, short col){
        return board[row][col].getVal();
    }

    public TileGUI getTileGui(TileLoc tl) {
        return board[tl.row()][tl.col()].getGui();
    }

    public boolean mineCheck(){ return mineClicked; }

    public boolean winCheck(){
        return tilesClicked == tilesNeeded;
    }
}
