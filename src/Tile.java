public class Tile {
    private final short val;

    private final short col;
    private final short row;
    private boolean clicked;
    private boolean flagged;

    protected Tile (short val,short col, short row){
            this.val = val;
            this.col = col;
            this.row = row;
            this.clicked = false;
            this.flagged = false;
    }
    public short getCol() { return col; }

    public short getRow() { return row; }

    public int getVal() {
        return val;
    }

    public void setClicked() {
        this.clicked = true;
    }

    public void setFlag() {
        flagged = !flagged;
    }

    public boolean isClicked() {
        return clicked;
    }

    public boolean isFlagged() {
        return flagged;
    }
}
