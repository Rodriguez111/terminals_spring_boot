package terminals.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import terminals.service.BarcodeGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class GenerateBarcode {

    private BarcodeGenerator barcodeGenerator;

    @RequestMapping("/generatebarcode")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String barcodeText = req.getParameter("barcodeText");
        byte[] buffer = barcodeGenerator.getBarcodeImage(barcodeText).toByteArray();
        resp.setContentType("image/gif");
        resp.setContentLength(buffer.length);
        resp.getOutputStream().write(buffer);
    }

    @Autowired
    public void setBarcodeGenerator(BarcodeGenerator barcodeGenerator) {
        this.barcodeGenerator = barcodeGenerator;
    }
}
