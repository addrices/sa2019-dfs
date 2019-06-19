package namenode;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;

@Service
class fileManager {
    String name;
    myfile home;

    fileManager(){
        name = "im filemanager";
        home = new myfile("home",true);
    }

    public String getName(){
        return name;
    }

    public boolean addDir(String Path){
        String[] dividePath=Path.split("/");
        int length = dividePath.length;
        String name = dividePath[length-1];
        myfile currDir = home.findDir(dividePath,0);
        if(currDir == null){
            return false;
        }
        myfile newfile = new myfile(name,true);
        currDir.addFile(newfile);
        return true;
    }

    public myfile addFile(String Path){
        System.out.println("Path" +Path);
        String[] dividePath=Path.split("/");
        int length = dividePath.length;
        String name = dividePath[length-1];
        myfile currDir = home.findDir(dividePath,0);
        if(currDir == null){
            return null;
        }
        myfile newfile = new myfile(name,false);
        currDir.addFile(newfile);
        return newfile;
    }

    public FileInputStream getFile(String Path){
        String[] dividePath=Path.split("/");
        int length = dividePath.length;
        String name = dividePath[length-1];
        System.out.println("getfile "+ name);
        myfile f = home.findFile(dividePath,0);
        System.out.println(f.getName());
        if(f == null){
            return null;
        }
        return null;
    }

    public String getDir(){
        String Dir = "";
        Dir = home.getDir(Dir,0);
        return Dir;
    }

    public myfile findFile(String Path){
        System.out.println("Path" + Path);
        String[] dividePath=Path.split("/");
        int length = dividePath.length;
        String name = dividePath[length-1];
        myfile f = home.findFile(dividePath,0);
        if(f == null){
            System.out.println("f = NULL");
            return null;
        }
        return f;
    }

    public boolean delfile(String Path){
        String[] dividePath=Path.split("/");
        int length = dividePath.length;
        String name = dividePath[length-1];
        myfile currDir = home.findDir(dividePath,0);
        System.out.println("deldir" + currDir.getName());
        if(currDir == null){
            return false;
        }
        currDir.delFile(name);
        return true;
    }
}
