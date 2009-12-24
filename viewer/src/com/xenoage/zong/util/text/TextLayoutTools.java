package com.xenoage.zong.util.text;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.xenoage.util.font.FontStyle;
import com.xenoage.zong.data.text.FormattedTextElement;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;


/**
 * Creates a {@link TextLayout} from a given {@link FormattedTextElement}.
 * 
 * @author Andreas Wenger
 */
public class TextLayoutTools
{
	
	
	public static TextLayout create(FormattedTextString element, FontRenderContext frc)
	{
		//create AttributedString
		AttributedString as = new AttributedString(element.getText());
    int length = element.getText().length();
    if (length > 0 && element.getStyle() != null)
    {
      as.addAttributes(createAttributesMap(element.getStyle()), 0, length);
    }
    //create TextLayout
    return new TextLayout(as.getIterator(), frc);
	}
	
	
	/**
   * Creates a Map with the attributes representing
   * the given style for an AttibutedString.
   */
  private static Map<TextAttribute, Object> createAttributesMap(
    FormattedTextStyle style)
  {
    Map<TextAttribute, Object> ret = new HashMap<TextAttribute, Object>();
    //font name
    Font font = style.getFont();
    ret.put(TextAttribute.FAMILY, font.getFamily());
    //font size
    ret.put(TextAttribute.SIZE, font.getSize2D());
    //color
    ret.put(TextAttribute.FOREGROUND, style.getColor());
    //bold
    EnumSet<FontStyle> fontStyle = style.getFontStyle();
    ret.put(TextAttribute.WEIGHT,
      (fontStyle.contains(FontStyle.Bold) ?
        TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR));
    //italic
    ret.put(TextAttribute.POSTURE,
      (fontStyle.contains(FontStyle.Italic) ?
        TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR));
    //underline
    ret.put(TextAttribute.UNDERLINE,
      (fontStyle.contains(FontStyle.Underline) ?
      	TextAttribute.UNDERLINE_ON : null));
    //striketrough - TODO
    //ret.put(TextAttribute.STRIKETHROUGH,
    //  (style.underline ? TextAttribute.STRIKETHROUGH_ON : null));
    //superscript
    switch (style.getSuperscript())
    {
    	case Super:
    		ret.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
    		break;
    	case Sub:
    		ret.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
    		break;
    	default:
    		ret.put(TextAttribute.SUPERSCRIPT, null);
    }
    return ret;
  }
	

}
