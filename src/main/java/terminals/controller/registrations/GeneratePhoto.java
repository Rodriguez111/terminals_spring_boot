package terminals.controller.registrations;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class GeneratePhoto {
    private static final String PHOTO_FOLDER = "c:/terminals_resources/photos/";


    public GeneratePhoto() {
        createFoldersForPhoto();
    }

    private void createFoldersForPhoto() {
        File folder = new File(PHOTO_FOLDER);
        File terminalsPhotoFolder = new File(PHOTO_FOLDER + "terminalsphoto/");
        File usersPhotoFolder = new File(PHOTO_FOLDER + "usersphoto/");
        if (!folder.exists()) {
            terminalsPhotoFolder.mkdirs();
            usersPhotoFolder.mkdirs();
        }
    }

    @RequestMapping(value = "/generatephoto", method = RequestMethod.GET)
    protected void generatePhotos(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String folder = req.getParameter("folder");
        String fileName = req.getParameter("fileName");
        File photoFile = new File(PHOTO_FOLDER + folder + "/" + fileName);
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
