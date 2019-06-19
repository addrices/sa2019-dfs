package namenode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
class datanodeManager {
    @Value("${datanodeManager.size}")
    private int blockSize;

    @Value("${datanodeManager.replicas}")
    private int blockReplicas;

    private List<datanode> datanodes;
    private int datanodenum;

    datanodeManager(){
        datanodenum = 0;
        datanodes = new ArrayList<datanode>();
    }


    public boolean addDatanode(int portNum){
        boolean flag = true;
        for(int i = 0; i < datanodenum; i++){
            if(portNum == datanodes.get(i).getPort()){
                flag = false;
            }
        }
        if(flag) {
            datanodenum = datanodenum + 1;
            datanode newnode = new datanode(portNum);
            datanodes.add(newnode);
            return true;
        }
        else
            return false;
    }

    public boolean save2Datanode(InputStream in_s,myfile file){
        List<Block> Bs = new ArrayList<Block>();
        int num = 0;
        if(datanodenum < blockReplicas){
            return false;
        }
        byte[] buffer = new byte[blockSize];
        System.out.println("Replicas" + blockReplicas);
        try {
            int i = in_s.read(buffer);
            while(i != -1){
                List<datanode> lownodes = LowloadDatanode(blockReplicas);
                InputStream new_ins = new ByteArrayInputStream(buffer, 0, i);
                String str = new String(buffer,0,i);
                System.out.println(str);
                for(int q = 0; q < lownodes.size(); q++) {
                    datanode target = lownodes.get(q);
                    int blocknum = target.saveBlock(new_ins);
                    Block b = new Block(target.getPort(), blocknum);
                    if(q == 0) {
                        Bs.add(b);
                    }
                }
                i = in_s.read(buffer);
                num = num + 1;
            }
        } catch (IOException e){
            return false;
        }
        System.out.println(num);
        System.out.println("SetBlockInfo");
        System.out.println(file.getName());
        file.setBlockinfo(num,Bs);
        return true;
    }

    public boolean get2Datanode(myfile file){
        List<Block> Bs = file.getBlockinfo();
        try {
            OutputStream os = new FileOutputStream("FILE");
            for(int i = 0; i < Bs.size(); i++){
                String url = "http://localhost:"+Bs.get(i).getNode() + "/" + Bs.get(i).getNum();
                System.out.println("get url:" + url);
                RestTemplate client = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                HttpMethod method = HttpMethod.GET;
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                HttpEntity requestEntity = new HttpEntity<>(headers);
                ResponseEntity<byte[]> response = client.exchange(url, method,requestEntity,byte[].class);
                byte[] f = response.getBody();
                String str = new String(f);
                System.out.println(str);
                os.write(f);
            }
        } catch (IOException e){
            return false;
        }
        return true;
    }

    private List<datanode> LowloadDatanode(int num){
        if(datanodes.size() == 0)
            return null;
        List<datanode> lownode = new ArrayList<datanode>();
        Collections.sort(datanodes);
        for(int i = 0; i < num; i++){
            lownode.add(datanodes.get(i));
        }
        return lownode;
        /*int minload = datanodes.get(0).getLoad();
        datanode currnode = datanodes.get(0);
        for(int i = 1; i < datanodes.size();i++){
            System.out.println("datanodes:" + datanodes.get(i).getPort());
            if(datanodes.get(i).getLoad() < minload){
                currnode = datanodes.get(i);
            }
        }
        return currnode;
         */
    }
}
