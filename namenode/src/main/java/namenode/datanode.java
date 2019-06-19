package namenode;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class datanode implements  Comparable<datanode>{
    private int port;
    private int blocknum;
    private List blocks;
    datanode(int p){
        port = p;
        blocknum = 0;
        blocks = new ArrayList();
    }

    @Override
    public int compareTo(datanode a) {
        return getLoad() - a.getLoad();
    }

    public int saveBlock(InputStream in_s){
        blocknum++;
        String url = "http://localhost:"+port;
        RestTemplate client = new RestTemplate();
        String response = "0";
        try {
            byte[] buffer = new byte[in_s.available()];
            in_s.read(buffer);
            File temp = new File("tempfile");
            OutputStream o_s = new FileOutputStream(temp);
            o_s.write(buffer);
            FileSystemResource resource = new FileSystemResource(new File("tempfile"));
            MultiValueMap<String,Object> param = new LinkedMultiValueMap<>();
            System.out.println("send to datanode");
            param.add("file",resource);
            response  = client.postForObject(url, param, String.class);
            System.out.println("response" + response);
        } catch (Exception e) {
            return 0;
        }
        return Integer.parseInt(response);
    }

    public int getLoad(){
        return blocknum;
    }

    public int getPort(){
        return port;
    }
}

