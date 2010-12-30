package com.xenoage.zong.data.text;

import com.xenoage.zong.core.text.Alignment;


/**
 * Test cases for a {@link FormattedText}.
 *
 * @author Andreas Wenger
 */
public class FormattedTextTry
{


  public static FormattedText getMixedStyleText()
  {
  	FormattedText text = new FormattedText();
  	FormattedTextParagraph p = FormattedTextParagraphTest.getMixedStyleTextParagraph();
  	p.setAlignment(Alignment.Center);
    text.addParagraph(p);
    p = FormattedTextParagraphTest.getMixedStyleTextParagraph();
  	p.setAlignment(Alignment.Left);
    text.addParagraph(p);
    p = FormattedTextParagraphTest.getMixedStyleTextParagraph();
  	p.setAlignment(Alignment.Right);
    text.addParagraph(p);
    return text;
  }
  
}
