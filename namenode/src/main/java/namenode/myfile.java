package namenode;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class myfile {

    private String filename;
    private int blocknum;
    private List<myfile> subfiles;
    private Boolean is_dir;
    private List<Block> blocks;

    myfile(String name, boolean dir){
        filename = name;
        is_dir = dir;
        subfiles = new ArrayList<myfile>();
    }

    public boolean addFile(myfile newfile){
        subfiles.add(newfile);
        return true;
    }

    public myfile findDir(String[] dividePath,int p){
        System.out.println("findFile in " + filename + " p=" + p);
        if(dividePath.length-1 == p)
            return this;
        for(int i = 0;i < subfiles.size(); i++){
            if(dividePath[p].equals(subfiles.get(i).getName()))
                return subfiles.get(i).findDir(dividePath,p+1);
        }
        return null;
    }

    public myfile findFile(String[] dividePath,int p){
        System.out.println("findFile in " + filename + " p=" + p);
        if(dividePath.length == p)
            return this;
        for(int i = 0;i < subfiles.size(); i++){
            if(dividePath[p].equals(subfiles.get(i).getName()))
                return subfiles.get(i).findFile(dividePath,p+1);
        }
        return null;
    }


    public String getName(){
        return filename;
    }

    public Boolean ifdir(){ return is_dir;}

    public void setBlockinfo(int bnum, List<Block> bs){
        blocknum = bnum;
        blocks = bs;
        return;
    }

    public  List<Block> getBlockinfo(){
        return blocks;
    }

    public String getDir(String str, int a){
        System.out.println(str);
        for(int i = 0; i < a; i ++){
            str = str + "    ";
        }
        if(is_dir == false){
            return str + filename + "\n";
        }
        else{
            str = str + filename + "\n";
            for(int i= 0; i < subfiles.size(); i++){
                str = subfiles.get(i).getDir(str, a+1);
            }
        }
        return str;
    }

    public boolean delFile(String name){
        for(int i = 0;i < subfiles.size(); i++){
            if(subfiles.get(i).getName().equals(name)){
                subfiles.remove(i);
            }
        }
        return true;
    }
}
