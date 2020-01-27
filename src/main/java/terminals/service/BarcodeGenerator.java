package terminals.service;


import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Component;
import java.awt.image.BufferedImage;
import java.io.*;

@Component
public class BarcodeGenerator {

    public ByteArrayOutputStream getBarcodeImage(String barcodeText) {
        Code128Bean bean = new Code128Bean();
        int resolution = 600;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                out, "image/x-png", resolution, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        bean.setMsgPosition(HumanReadablePlacement.HRP_NONE); //скрываем оригинальную подпись
        bean.setHeight(2.0);
        bean.generateBarcode(canvas, barcodeText); //генерируем ШК
        try {
            canvas.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }
}
