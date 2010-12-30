package com.xenoage.zong.renderer;


/**
 * Parameters for rendering, like the Graphics2D
 * instance, scaling and quality.
 *
 * @author Andreas Wenger
 */
public class RenderingParams
{
  
  //the render target
  private RenderTarget renderTarget;
  
  //scaling (zoom * other factors)
  //1: 72 dpi, 2 = 144 dpi, ...
  private float scaling = 1f;
  
  //quality
  private RenderingQuality quality = RenderingQuality.Screen;
  
  
  /**
   * Creates a new rendering parameters.
   * @param renderTarget      the rendering target
   * @param scaling           the current scaling (zoom * other factors, if available)
   * @param quality           the quality level
   */
  public RenderingParams(RenderTarget renderTarget, float scaling, RenderingQuality quality)
  {
    this.renderTarget = renderTarget;
    this.scaling = scaling;
    this.quality = quality;
  }
  
  
  public RenderingQuality getQuality()
  {
    return quality;
  }

  
  public float getScaling()
  {
    return scaling;
  }

  
  public RenderTarget getRenderTarget()
  {
    return renderTarget;
  }
  

}
