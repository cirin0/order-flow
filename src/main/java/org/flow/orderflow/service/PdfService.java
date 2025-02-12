package org.flow.orderflow.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.dto.order.OrderItemDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {
  private static Font HEADER_FONT;
  private static Font SUBHEADER_FONT;
  private static Font NORMAL_FONT;
  private static Font SMALL_FONT;

  static {
    try {
      BaseFont baseFont = BaseFont.createFont(
        "static/fonts/FreeSans.ttf",
        "CP1251",
        BaseFont.EMBEDDED
      );
      HEADER_FONT = new Font(baseFont, 18, Font.BOLD);
      SUBHEADER_FONT = new Font(baseFont, 14, Font.BOLD);
      NORMAL_FONT = new Font(baseFont, 12, Font.NORMAL);
      SMALL_FONT = new Font(baseFont, 10, Font.NORMAL);
    } catch (Exception e) {
      HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
      SUBHEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
      NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
      SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    }
  }

  public byte[] generateInvoice(OrderDto order) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4);
      PdfWriter.getInstance(document, out);
      document.open();

      Paragraph header = new Paragraph("Дякуємо за ваше замовлення!", HEADER_FONT);
      header.setAlignment(Element.ALIGN_CENTER);
      document.add(header);

      Paragraph subheader = new Paragraph(
        "Ми обробляємо ваше замовлення і повідомимо вас про його статус.",
        NORMAL_FONT
      );
      subheader.setAlignment(Element.ALIGN_CENTER);
      subheader.setSpacingAfter(20);
      document.add(subheader);

      document.add(new Paragraph("Деталі замовлення", SUBHEADER_FONT));
      document.add(new Paragraph("Номер замовлення: " + order.getId(), NORMAL_FONT));

      if (order.getOrderDate() != null) {
        document.add(new Paragraph("Дата замовлення: " +
          order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
          NORMAL_FONT));
      }

      PdfPTable table = new PdfPTable(4);
      table.setWidthPercentage(100);
      table.setSpacingBefore(20);
      table.setSpacingAfter(20);

      String[] headers = {"Товар", "Кількість", "Ціна", "Всього"};

      for (String columnTitle : headers) {
        PdfPCell tableHeader = new PdfPCell();
        tableHeader.setBackgroundColor(new BaseColor(248, 249, 250));
        tableHeader.setBorderWidth(1);
        tableHeader.setPadding(8);
        tableHeader.setPhrase(new Phrase(columnTitle, NORMAL_FONT));
        table.addCell(tableHeader);
      }

      for (OrderItemDto item : order.getItems()) {
        PdfPCell productCell = new PdfPCell(new Phrase(item.getProductName(), NORMAL_FONT));
        productCell.setPadding(8);
        table.addCell(productCell);

        PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), NORMAL_FONT));
        quantityCell.setPadding(8);
        table.addCell(quantityCell);

        PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%.2f UAH", item.getPrice()), NORMAL_FONT));
        priceCell.setPadding(8);
        table.addCell(priceCell);

        PdfPCell totalCell = new PdfPCell(new Phrase(
          String.format("%.2f UAH", item.getPrice() * item.getQuantity()),
          NORMAL_FONT
        ));
        totalCell.setPadding(8);
        table.addCell(totalCell);
      }
      document.add(table);

      Paragraph total = new Paragraph(
        String.format("Загальна сума: %.2f UAH", order.getTotalPrice()),
        SUBHEADER_FONT
      );
      total.setAlignment(Element.ALIGN_RIGHT);
      document.add(total);

      Paragraph footer = new Paragraph();
      footer.add(new Phrase(
        "Якщо у вас є питання щодо замовлення, будь ласка, зв'яжіться з нашою службою підтримки.\n",
        SMALL_FONT
      ));
      footer.add(new Phrase("Дякуємо за покупку!", SMALL_FONT));
      footer.setAlignment(Element.ALIGN_CENTER);
      footer.setSpacingBefore(30);
      document.add(footer);
      document.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate PDF", e);
    }
  }
}
