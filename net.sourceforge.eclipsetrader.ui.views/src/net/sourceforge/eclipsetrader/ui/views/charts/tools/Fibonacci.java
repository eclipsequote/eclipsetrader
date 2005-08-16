/*******************************************************************************
 * Copyright (c) 2005 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 *******************************************************************************/
package net.sourceforge.eclipsetrader.ui.views.charts.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 */
public class Fibonacci extends ToolPlugin
{
  private Point p1 = null;
  private Point p2 = null;
  private Point selected = null;
  private Color color = new Color(null, 0, 0, 0);
  private double[] retrace = { 1, 1, 2, 3, 5, 8 };
  private Date date1, date2;
  private double value1 = 0, value2 = 0;
  private int lastX = -1, lastY = -1;
  private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#containsPoint(int, int)
   */
  public boolean containsPoint(int x1, int y1)
  {
    if (p1 == null || p2 == null || isMousePressed() == true)
      return true;

    if (x1 < p1.x || x1 > p2.x)
      return false;
    
    double total = 0;
    for (int i = 0; i < retrace.length; i++)
      total += retrace[i];

    double step = Math.abs(p1.y - p2.y) / total;

    if (Math.abs(y1 - p1.y) <= 2)
      return true;
    
    if (p1.y < p2.y)
    {
      double y = p1.y;
      for (int i = 0; i < retrace.length - 1 && y < p2.y; i++)
      {
        y += step * retrace[i];
        if (Math.abs(y1 - (int)Math.round(y)) <= 2)
          return true;
      }
    }
    else
    {
      double y = p2.y;
      for (int i = 0; i < retrace.length - 1 && y < p1.y; i++)
      {
        y += step * retrace[i];
        if (Math.abs(y1 - (int)Math.round(y)) <= 2)
          return true;
      }
    }

    if (Math.abs(y1 - p2.y) <= 2)
      return true;
    
    return false;
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#containsPoint(int, int)
   */
  public boolean isOnHandle(int x, int y)
  {
    if (p1 == null || p2 == null || isMousePressed() == true)
      return true;
    
    if (Math.abs(x - p1.x) <= 2 && Math.abs(y - p1.y) <= 2)
      return true;
    else if (Math.abs(x - p2.x) <= 2 && Math.abs(y - p1.y) <= 2)
      return true;
    else if (Math.abs(x - p2.x) <= 2 && Math.abs(y - p2.y) <= 2)
      return true;
    else if (Math.abs(x - p1.x) <= 2 && Math.abs(y - p2.y) <= 2)
      return true;
    
    return false;
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#mousePressed(org.eclipse.swt.events.MouseEvent)
   */
  public void mousePressed(MouseEvent me)
  {
    super.mousePressed(me);
    
    if (p1 == null && p2 == null)
    {
      p1 = new Point(me.x, me.y);
      p2 = new Point(me.x, me.y);
      selected = p2;
    }
    else
    {
      if (Math.abs(me.x - p1.x) <= 2 && Math.abs(me.y - p1.y) <= 2)
        selected = p1;
      else if (Math.abs(me.x - p2.x) <= 2 && Math.abs(me.y - p1.y) <= 2)
      {
        int x = p1.x;
        p1.x = p2.x;
        p2.x = x;
        selected = p1;
      }
      else if (Math.abs(me.x - p2.x) <= 2 && Math.abs(me.y - p2.y) <= 2)
        selected = p2;
      else if (Math.abs(me.x - p1.x) <= 2 && Math.abs(me.y - p2.y) <= 2)
      {
        int y = p1.y;
        p1.y = p2.y;
        p2.y = y;
        selected = p2;
      }
      else
      {
        lastX = me.x;
        lastY = me.y;
      }
    }
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#mouseDragged(org.eclipse.swt.events.MouseEvent)
   */
  public void mouseDragged(MouseEvent me)
  {
    if (selected != null)
    {
      getCanvas().getChart().redraw(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x) + 1, Math.abs(p1.y - p2.y) + 1, true);
      selected.x = getX(getDate(me.x));
      selected.y = me.y;
    }
    else if (isMousePressed())
    {
      getCanvas().getChart().redraw(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x) + 1, Math.abs(p1.y - p2.y) + 1, true);
      p1.x += me.x - lastX;
      p2.x += me.x - lastX;
      p1.y += me.y - lastY;
      p2.y += me.y - lastY;
      lastX = me.x;
      lastY = me.y;
    }
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#mouseReleased(org.eclipse.swt.events.MouseEvent)
   */
  public void mouseReleased(MouseEvent me)
  {
    super.mouseReleased(me);

    selected = null;
    lastX = -1;
    lastY = -1;

    date1 = getDate(p1.x);
    value1 = getScaler().convertToVal(p1.y);
    date2 = getDate(p2.x);
    value2 = getScaler().convertToVal(p2.y);

    Map map = new HashMap();
    map.put("x1", df.format(date1));
    map.put("y1", String.valueOf(value1));
    map.put("x2", df.format(date2));
    map.put("y2", String.valueOf(value2));
    setParameters(map);
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#setParameter(java.lang.String,java.lang.String)
   */
  public void setParameter(String name, String value)
  {
    if (name.equals("x1"))
    {
      try {
        date1 = df.parse(value);
      } catch(Exception e) {}
    }
    else if (name.equals("y1"))
      value1 = Double.parseDouble(value);
    else if (name.equals("x2"))
    {
      try {
        date2 = df.parse(value);
      } catch(Exception e) {}
    }
    else if (name.equals("y2"))
      value2 = Double.parseDouble(value);

    super.setParameter(name, value);
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#invalidate()
   */
  public void invalidate()
  {
    if (date1 != null)
    {
      if (p1 == null)
        p1 = new Point(0, 0);
      p1.x = getX(date1);
      p1.y = getScaler().convertToY(value1);
    }

    if (date2 != null)
    {
      if (p2 == null)
        p2 = new Point(0, 0);
      p2.x = getX(date2);
      p2.y = getScaler().convertToY(value2);
    }
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#paintTool(org.eclipse.swt.graphics.GC)
   */
  public void paintTool(GC gc)
  {
    if (p1 != null && p2 != null)
    {
      double total = 0;
      for (int i = 0; i < retrace.length; i++)
        total += retrace[i];

      double step = Math.abs(p1.y - p2.y) / total;

      gc.setForeground(color);
      gc.drawLine(p1.x, p1.y, p2.x, p1.y);
      
      if (p1.y < p2.y)
      {
        double y = p1.y;
        for (int i = 0; i < retrace.length - 1 && y < p2.y; i++)
        {
          y += step * retrace[i];
          gc.drawLine(p1.x, (int)Math.round(y), p2.x, (int)Math.round(y));
        }
      }
      else
      {
        double y = p2.y;
        for (int i = 0; i < retrace.length - 1 && y < p1.y; i++)
        {
          y += step * retrace[i];
          gc.drawLine(p1.x, (int)Math.round(y), p2.x, (int)Math.round(y));
        }
      }

      gc.drawLine(p1.x, p2.y, p2.x, p2.y);
    }
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.ui.views.charts.ToolPlugin#openParametersDialog(org.eclipse.swt.widgets.Shell)
   */
  public boolean openParametersDialog(Shell parent)
  {
    // Select the line color
    ColorDialog colorDialog = new ColorDialog(parent, SWT.APPLICATION_MODAL);
    colorDialog.setRGB(color.getRGB());
    RGB newColor = colorDialog.open();
    if (newColor != null)
    {
      if (color != null)
        color.dispose();
      color = new Color(null, newColor);
    }
    
    return (newColor != null);
  }
}
