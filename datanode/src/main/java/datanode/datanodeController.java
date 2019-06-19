package datanode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RestController
class datanodeController {

    @Autowired
    DataManager DataManager;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/{num}")
    public ResponseEntity<byte[]> get(@PathVariable("num") String num){
        System.out.println("get " + num + "block");
        String Path = "blocks/" + DataManager.getPort() + "/" + num;
        InputStream in_f;
        byte[] buffer;
        try {
            in_f = new FileInputStream(Path);
            buffer = new byte[in_f.available()];
            System.out.println(in_f.available());
            in_f.read(buffer);
        } catch (IOException e) {
            return null;
        }
        String re = new String(buffer);
        System.out.println(re);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION).body(buffer);

    }

    //@RequestParam("file") MultipartFile file
    @PostMapping("/")
    public String put(@RequestParam("file") MultipartFile file) throws IOException{
        int a = 0;
        try {
            InputStream in_f = file.getInputStream();
             a = DataManager.SaveBlock(in_f);
        } catch (IOException e) {
             e.printStackTrace();
        }
        return String.valueOf(a);
    }
}