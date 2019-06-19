package namenode;

public class Block {
    private int datanode;
    private int blocknum;

    Block(int node, int bnum){
        datanode = node;
        blocknum = bnum;
    }

    public int getNode(){
        return datanode;
    }
    public int getNum(){
        return blocknum;
    }
}
