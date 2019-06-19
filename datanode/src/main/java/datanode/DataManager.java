package datanode;

import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
class DataManager {

    @Value("${server.port}")
    private int port;

    private int blocknum;
    private List blocks;
    private String Path;

    DataManager(){
        blocknum = 0;
        blocks = new ArrayList();
    }

    public int SaveBlock(InputStream in_s){

        Path = "blocks/" + String.valueOf(port);
        File Dir = new File(Path);
        if(!Dir.exists()){
            Dir.mkdir();
        }

        System.out.println("port:" + port);
        int num = blocknum+1;
        String blockname = Path + "/"+ String.valueOf(num);
        blocks.add(num);
        System.out.println("newblock"+ num);
        File f = new File(blockname);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(blockname);
            byte[] b = new byte[1024];
            int length;
            while ((length = in_s.read(b)) > 0) {
                fos.write(b, 0, length);
            }
            fos.close();
        } catch (IOException e){
            return 0;
        }
        blocknum = blocknum+1;
        return num;
    }

    public int getPort(){
        return port;
    }
}
