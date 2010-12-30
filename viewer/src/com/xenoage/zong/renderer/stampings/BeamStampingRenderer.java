package com.xenoage.zong.renderer.stampings;

import java.awt.Color;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.musiclayout.stampings.BeamStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;


/**
 * Renderer for a {@link BeamStamping}.
 *
 * @author Andreas Wenger
 */
public class BeamStampingRenderer
{

	
	/**
	 * Paints the given beam stamping using the given rendering parameters.
	 */
  public static void paint(BeamStamping beam, RenderingParams params)
  {
    float scaling = params.getScaling();

    //TODO: stem should be thinner than lineWidth?
    float stemWidthMm = beam.getParentStaff().getLineWidth(); 
    
    //TODO: does not exactly match the stem
    //stem seems to be too thin and too high?!
    int x1Px = Units.mmToPx(beam.getX1() - stemWidthMm/2f, scaling); // + 2; //+2 TEST
    int x2Px = Units.mmToPx(beam.getX2() + stemWidthMm/2f, scaling); // - 2; //-2 TEST
    
    Color color = Color.black;
    
    float leftYStart = 0;
    float rightYStart = 0;
    
    StaffStamping staff1 = beam.getStaffStamping1();
    StaffStamping staff2 = beam.getStaffStamping2();
    
    if (params.getQuality() == RenderingQuality.Screen)
    {
      //render on screen
      float staff1YPos = Units.mmToPx(staff1.getPosition().y, scaling);
      float staff2YPos = Units.mmToPx(staff2.getPosition().y, scaling);
      ScreenStaff screenStaff1 = staff1.getScreenInfo().getScreenStaff(scaling);
      ScreenStaff screenStaff2 = staff2.getScreenInfo().getScreenStaff(scaling);
      leftYStart = staff1YPos + screenStaff1.getLPPxCorrected(beam.getLP1());
      rightYStart = staff2YPos + screenStaff2.getLPPxCorrected(beam.getLP2());
    } 
    else if (params.getQuality() == RenderingQuality.Print)
    {
      leftYStart = Units.mmToPxFloat(
        staff1.computeYMm(beam.getLP1()), scaling);
      rightYStart = Units.mmToPxFloat(
        staff2.computeYMm(beam.getLP2()), scaling);
    }
    
    float beamHeightPx = Units.mmToPxFloat(
      BeamStamping.beamHeight * staff1.getInterlineSpace(), scaling);
    
    //TODO: avoid edges at the stem end points
    
    //left lower point
  	Point2f[] points = new Point2f[4];
    points[0] = new Point2f(x1Px, leftYStart - 0.5f * beamHeightPx);
    //left upper point
    points[1] = new Point2f(x1Px, points[0].y + beamHeightPx);
    //right lower point
    points[2] = new Point2f(x2Px, rightYStart - 0.5f * beamHeightPx);
    //right upper point
    points[3] = new Point2f(x2Px, points[2].y + beamHeightPx);
    
    float isPx = Units.mmToPxFloat(staff1.getInterlineSpace(), scaling);
    
    params.getRenderTarget().drawBeam(points, /* Color.green /*/ color /**/, isPx);
    
  }
  
  
}
