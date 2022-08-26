public class Tile {
    private final short val;
    private final short col;
    private final short row;
    private boolean clicked;
    private boolean flagged;
    private TileGUI gui;
    protected Tile (short val,short col, short row){
            this.val = val;
            this.col = col;
            this.row = row;
            this.clicked = false;
            this.flagged = false;
    }

    public void setGui(TileGUI gui) {
        this.gui = gui;
    }
    public void setClicked() {
        this.clicked = true;
    }
    public void setFlag() {
        flagged = !flagged;
    }
    public short getCol() { return col; }
    public short getRow() { return row; }
    public short getVal() { return val; }
    public TileGUI getGui() {
        return gui;
    }
    public boolean isClicked() { return clicked;}
    public boolean isFlagged() {
        return flagged;
    }
}
