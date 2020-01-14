package ittalents.javaee.model.pojo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public abstract class ExporterToPdf {

    public static void export(String references, String object) {
        try {
            Document document = new Document();
            System.out.println("");
            PdfWriter.getInstance(document, new FileOutputStream(object + "Reference.pdf"));
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC, 20, BaseColor.BLACK);
            document.add(new Paragraph("Reference Information for " + object + System.lineSeparator(), titleFont));
            Font contentFont = FontFactory.getFont(FontFactory.TIMES_ITALIC, 15, BaseColor.BLACK);
            document.add(new Chunk(references, contentFont));
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
