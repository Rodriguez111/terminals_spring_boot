package terminals.controller.registrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class GeneratePhoto {

    @Value("${photos-dir}")
    private String photoFolder;

    @RequestMapping(value = "/generatephoto", method = RequestMethod.GET)
    protected void generatePhotos(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String folder = req.getParameter("folder");
        String fileName = req.getParameter("fileName");
        File photoFile = new File(photoFolder + "/" + folder + "/" + fileName);
        FileInputStream fileInputStream = new FileInputStream(photoFile);
        int bufferSize = 1024 * 100;
        byte[] buffer = new byte[bufferSize];
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        OutputStream outputStream = resp.getOutputStream();

        resp.setContentType("image/gif");
        while (bytesRead > 0) {
            outputStream.write(buffer);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        fileInputStream.close();
        outputStream.flush();
        outputStream.close();
    }

}
