package com.xenoage.zong.print;

import static com.xenoage.util.iterators.It.it;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.xenoage.util.Units;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.iterators.It;
import com.xenoage.util.logging.Log;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.renderer.printer.page.PrinterPageLayoutRenderer;


/**
 * This class allows the user to print out
 * the current score into a PDF file.
 * 
 * The printing functions are the same as for printing out with
 * Java2D, but the target is iText instead of the printer driver.
 *
 * @author Andreas Wenger
 */
public final class PDFPrinter
{
  
  
  /**
   * Prints the given {@link Layout} into the given PDF {@link File}.
   */
  public static void print(Layout layout, File file)
  {
  	
    Document document = new Document();
    PdfWriter writer = null;
    try
    {
    	writer = PdfWriter.getInstance(document, new FileOutputStream(file));
    } catch (Exception e)
    {
      App.err().report(ErrorLevel.Warning, Voc.Error_CouldNotSaveDocument);
    }

    document.open();
    PdfContentByte cb = writer.getDirectContent();
    PrinterPageLayoutRenderer renderer = PrinterPageLayoutRenderer.getInstance();
    
    It<Page> pages = it(layout.getPages());
    for (Page page : pages)
    {
    	//create PDF page
    	Size2f pageSize = page.getFormat().getSize();
    	int width = Units.mmToPx(pageSize.width, 1);
      int height = Units.mmToPx(pageSize.height, 1);
      PdfTemplate tp = cb.createTemplate(width, height);
      //fill PDF page
      Graphics2D g2d = tp.createGraphics(width, height, new DefaultFontMapper());
      Log.log(Log.MESSAGE, "Printing page " + pages.getIndex() + "...");
      renderer.paint(layout, pages.getIndex(), new SwingGraphicsContext(g2d, 1f));
      //finish page
      g2d.dispose();
      cb.addTemplate(tp, 0, 0);
    }

    document.close();
  }


}
