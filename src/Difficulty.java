public enum Difficulty {
    Easy ((short) 9,(short)9,(short)10,(short)40),
    Medium((short) 16,(short)16,(short)40,(short)35),
    Hard((short) 16,(short)30,(short)99,(short)32);

    private final short row;
    private final short col;
    private final short mines;
    private final short tileSize;
    Difficulty(short row, short col, short mines, short tileSize) {
        this.row = row;
        this.col = col;
        this.mines = mines;
        this.tileSize = tileSize;
    }
    public short getRow() {
        return row;
    }
    public short getCol() {
        return col;
    }
    public short getMines() {
        return mines;
    }
    public short getTileSize() {
        return tileSize;
    }
}
