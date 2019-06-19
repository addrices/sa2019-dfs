package namenode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.event.EventListener;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
class namenodeController {

    @Autowired
    private fileManager filemanager;

    @Autowired
    private  datanodeManager datanodemanager;

    @Autowired
    private HttpServletRequest request;

    @EventListener
    public void listen(EurekaInstanceCanceledEvent event) throws IOException {
        return;
    }

    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        String dataNodeUrl = instanceInfo.getHomePageUrl();
        int port = instanceInfo.getPort();
        System.out.println(port);
        datanodemanager.addDatanode(port);
        System.out.println("注册");
    }

    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        System.out.println(event.getServerId() + " 续约");
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
        System.out.println("注册中心 启动");
    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        System.out.println("Eureka Server 启动");
    }

    @GetMapping("/dir/")
    public String get_dir(){
        return filemanager.getDir();
    }

    @GetMapping("/download/**")
    public void download(HttpServletResponse response){
        String Path = request.getRequestURI().replaceFirst("/download/", "");
        System.out.println("download" + Path);
        byte[] buffer = new byte[1024];
        OutputStream os = null; //输出流
        FileInputStream in_f = null;
        BufferedInputStream bis = null;
        response.setHeader("Content-Disposition", "attachment;fileName=" + Path);
        try{
            os = response.getOutputStream();
            myfile f = filemanager.findFile(Path);
            System.out.println("download File" + f.getName());
            datanodemanager.get2Datanode(f);
            in_f = new FileInputStream("FILE");
            if(in_f == null){
                System.out.println("nofile");
                return;
            }
            bis = new BufferedInputStream(in_f);
            int i = bis.read(buffer);
            while(i != -1){
                os.write(buffer,0,i);
                i = bis.read(buffer);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("getfile");
    }

    @PutMapping("/upload/**")
    public String upload(@RequestParam("file") MultipartFile file) {
        String Path = request.getRequestURI().replaceFirst("/upload/", "");
        myfile f = filemanager.addFile(Path);
        try {
            InputStream in_s = file.getInputStream();
            System.out.println("save2Datanode");
            datanodemanager.save2Datanode(in_s,f);
        } catch (IOException e) {
            return "upload fail";
        }
        return "upload success";
    }

    @PutMapping("/newdir/**")
    public String addDir(){
        String Path = request.getRequestURI().replaceFirst("/newdir/","");
        if(filemanager.addDir(Path)){
            return "addDir" + Path;
        }
        else{
            return "addDir Fail";
        }
    }

    @GetMapping("/delete/**")
    public String delete(){
        String Path = request.getRequestURI().replaceFirst("/delete/","");
        if(filemanager.delfile(Path)){
            return "delDir" + Path;
        }
        else{
            return "delDir Fail";
        }
    }
}