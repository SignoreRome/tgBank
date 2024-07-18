package ru.olbreslavets.tgbank.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.experimental.UtilityClass;

import java.io.FileOutputStream;
import java.util.Arrays;

@UtilityClass
public class DocumentUtil {

    public void generatePdf(String name, String text) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(name));
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);

            Arrays.stream(text.split("/")).forEach(line -> {
                Paragraph paragraph = new Paragraph(line, font);
                try {
                    document.add(paragraph);
                    // Добавляем пустую строку между абзацами для визуального эффекта
                    document.add(new Paragraph(" ", font));
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

}
