/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.charts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.eclipsetrader.core.db.Bar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 */
public class DatePlot extends Composite
{
    private IndicatorPlot indicatorPlot;
    private ScalePlot scalePlot;
    private Color foreground = new Color(null, 0, 0, 0);
    private Color background = new Color(null, 255, 255, 255);
    private Color hilight = new Color(null, 255, 0, 0);
    private int scaleWidth = 75;
    private int interval = BarData.INTERVAL_DAILY;
//    private Map map = new HashMap();
    private List dateList = new ArrayList();
    private Map map = new HashMap();
    private BarData barData;
    private Label label;
    private Color labelColor = new Color(null, 255, 255, 0);
    private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
    private SimpleDateFormat tf = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$
    
    private class TickItem
    {
        public boolean flag = false;
        public boolean tick = false;
        public String text = "";
        public Date date;
        
        public TickItem(Date date)
        {
            this.date = date;
        }
    }

    public DatePlot(Composite parent, int style)
    {
        super(parent, style);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.marginWidth = gridLayout.marginHeight = 1;
        gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
        setLayout(gridLayout);
        
        indicatorPlot = new IndicatorPlot(this, SWT.NONE) {
            public void paintControl(PaintEvent e)
            {
                super.paintControl(e);

                Color foreground = getForeground();
                if (foreground != null)
                    e.gc.setForeground(foreground);
                e.gc.drawLine(0, 0, getBounds().width, 0);
                if (foreground != null)
                    foreground.dispose();
            }

            public void draw(GC gc)
            {
                Color background = getBackground();
                Color foreground = getForeground();
                Color hilight = getHilight();

                if (background != null)
                    gc.setBackground(background);

                gc.fillRectangle(0, 0, getPlotBounds().x, getPlotBounds().y);
                
                int x = getMarginWidth() + getGridWidth() / 2;
                for(Iterator iter = dateList.iterator(); iter.hasNext(); )
                {
                    TickItem item = (TickItem)iter.next();
                    if (item.tick)
                    {
                        if (item.flag && hilight != null)
                            gc.setForeground(hilight);
                        else if (foreground != null)
                            gc.setForeground(foreground);
                        gc.drawLine (x, 1, x, 6);
                        gc.drawString(item.text, x - 1, 7, true);
                    }
                    else
                    {
                        if (foreground != null)
                            gc.setForeground(foreground);
                        gc.drawLine (x, 1, x, 3);
                    }
                    
                    x += getGridWidth();
                }

                if (foreground != null)
                    foreground.dispose();
                if (background != null)
                    background.dispose();
                if (hilight != null)
                    hilight.dispose();
            }
        };
        indicatorPlot.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
        indicatorPlot.setForeground(foreground);
        indicatorPlot.setBackground(background);

        scalePlot = new ScalePlot(this, SWT.NONE) {
            public void draw(GC gc)
            {
                Color foreground = getForeground();

                if (foreground != null)
                    gc.setForeground(foreground);
                gc.drawLine(0, 0, getBounds().width, 0);
                gc.drawLine(0, 0, 0, getBounds().height);
                
                if (foreground != null)
                    foreground.dispose();
            }
        };
        GridData gridData = new GridData();
        gridData.widthHint = scaleWidth;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        scalePlot.setLayoutData(gridData);
        scalePlot.setForeground(foreground);
        scalePlot.setBackground(background);
        
        label = new Label(indicatorPlot, SWT.CENTER);
        label.setBackground(labelColor);
        label.setBounds(0, 0, 0, 0);

        pack();
    }

    public int getInterval()
    {
        return interval;
    }

    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    public IndicatorPlot getIndicatorPlot()
    {
        return indicatorPlot;
    }

    public ScalePlot getScalePlot()
    {
        return scalePlot;
    }

    public void setScaleWidth(int scaleWidth)
    {
        this.scaleWidth = scaleWidth;
        ((GridData)scalePlot.getLayoutData()).widthHint = this.scaleWidth;
    }

    public Color getHilight()
    {
        return hilight == null ? null : new Color(null, hilight.getRGB());
    }

    public void setHilight(Color hilightColor)
    {
        this.hilight = hilightColor == null ? null : new Color(null, hilightColor.getRGB());
    }

    public int mapToScreen(Date date)
    {
        if (date == null)
            return -1;
        
        Integer value = (Integer)map.get(date);
        if (value != null)
            return value.intValue();

        int index = 0;
        for(Iterator iter = dateList.iterator(); iter.hasNext(); )
        {
            TickItem item = (TickItem)iter.next();
            if (item.date.compareTo(date) >= 0)
                return indicatorPlot.getMarginWidth() + indicatorPlot.getGridWidth() / 2 + index * indicatorPlot.getGridWidth();
            index++;
        }
        
        return -1;
    }

    public Date mapToDate(int x)
    {
        int index = (x - indicatorPlot.getMarginWidth()) / indicatorPlot.getGridWidth();
        if (index < 0)
        {
            if (dateList.size() > 0)
                return ((TickItem)dateList.get(0)).date;
            return null;
        }
        if (index >= dateList.size())
        {
            if (dateList.size() > 0)
                return ((TickItem)dateList.get(dateList.size() - 1)).date;
            return null;
        }
        return ((TickItem)dateList.get(index)).date;
    }
    
    public BarData getBarData()
    {
        return barData;
    }
    
    public void setBarData(BarData barData)
    {
        if (barData == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
        this.barData = barData;

        dateList.clear();
        map.clear();

        switch (interval)
        {
            case BarData.INTERVAL_MINUTE1:
            case BarData.INTERVAL_MINUTE2:
            case BarData.INTERVAL_MINUTE5:
            case BarData.INTERVAL_MINUTE10:
            case BarData.INTERVAL_MINUTE15:
            case BarData.INTERVAL_MINUTE30:
            case BarData.INTERVAL_MINUTE60:
                getMinuteDate();
                break;
            case BarData.INTERVAL_WEEKLY:
                getWeeklyDate();
                break;
            case BarData.INTERVAL_MONTHLY:
                getMonthlyDate();
                break;
            default:
                getDailyDate();
                break;
        }

        if (dateList.size() != 0)
            indicatorPlot.setPlotSize(dateList.size() * indicatorPlot.getGridWidth() + indicatorPlot.getMarginWidth() * 2, indicatorPlot.getPlotSize().y);
        else
            indicatorPlot.setPlotSize(0, indicatorPlot.getPlotSize().y);
        
        redraw();
    }
    
    private void getDailyDate()
    {
        SimpleDateFormat monthYearFormatter = new SimpleDateFormat("MMM, yyyy"); //$NON-NLS-1$

        Calendar oldDate = null;
        Calendar currentDate = Calendar.getInstance();

        int x = indicatorPlot.getMarginWidth() + indicatorPlot.getGridWidth() / 2;
        
        for (Iterator iter = barData.iterator(); iter.hasNext(); )
        {
            Bar bar = (Bar)iter.next();
            currentDate.setTime(bar.getDate());

            TickItem item = new TickItem(bar.getDate());

            if (oldDate == null || currentDate.get(Calendar.DAY_OF_YEAR) != oldDate.get(Calendar.DAY_OF_YEAR))
            {
                if (oldDate == null)
                {
                    oldDate = Calendar.getInstance();
                    oldDate.setTime(bar.getDate());
                }
                
                if (currentDate.get(Calendar.MONTH) != oldDate.get(Calendar.MONTH))
                    item.tick = true;
                if (currentDate.get(Calendar.YEAR) != oldDate.get(Calendar.YEAR))
                    item.flag = true;
                item.text = monthYearFormatter.format(currentDate.getTime());

                oldDate.setTime(bar.getDate());
            }

            dateList.add(item);
            map.put(item.date, new Integer(x));

            x += indicatorPlot.getGridWidth();
        }
    }
    
    private void getWeeklyDate()
    {
        SimpleDateFormat monthYearFormatter = new SimpleDateFormat("yy"); //$NON-NLS-1$
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM"); //$NON-NLS-1$

        Calendar oldDate = null;
        Calendar currentDate = Calendar.getInstance();

        int x = indicatorPlot.getMarginWidth() + indicatorPlot.getGridWidth() / 2;
        
        for (Iterator iter = barData.iterator(); iter.hasNext(); )
        {
            Bar bar = (Bar)iter.next();
            currentDate.setTime(bar.getDate());

            TickItem item = new TickItem(bar.getDate());

            if (oldDate == null || currentDate.get(Calendar.MONTH) != oldDate.get(Calendar.MONTH))
            {
                if (oldDate == null)
                {
                    oldDate = Calendar.getInstance();
                    oldDate.setTime(bar.getDate());
                }
                
                if (currentDate.get(Calendar.MONTH) != oldDate.get(Calendar.MONTH))
                {
                    item.tick = true;
                    item.text = monthFormatter.format(currentDate.getTime()).substring(0, 1).toUpperCase();
                }
                if (currentDate.get(Calendar.YEAR) != oldDate.get(Calendar.YEAR))
                {
                    item.tick = true;
                    item.flag = true;
                    item.text = monthYearFormatter.format(currentDate.getTime());
                }

                oldDate.setTime(bar.getDate());
            }

            dateList.add(item);
            map.put(item.date, new Integer(x));

            x += indicatorPlot.getGridWidth();
        }
    }
    
    private void getMonthlyDate()
    {
        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy"); //$NON-NLS-1$

        Calendar oldDate = null;
        Calendar currentDate = Calendar.getInstance();

        int x = indicatorPlot.getMarginWidth() + indicatorPlot.getGridWidth() / 2;
        
        for (Iterator iter = barData.iterator(); iter.hasNext(); )
        {
            Bar bar = (Bar)iter.next();
            currentDate.setTime(bar.getDate());

            TickItem item = new TickItem(bar.getDate());

            if (oldDate == null || currentDate.get(Calendar.MONTH) != oldDate.get(Calendar.MONTH))
            {
                if (oldDate == null)
                {
                    oldDate = Calendar.getInstance();
                    oldDate.setTime(bar.getDate());
                }
                
                if (currentDate.get(Calendar.MONTH) != oldDate.get(Calendar.MONTH))
                    item.tick = true;
                if (currentDate.get(Calendar.YEAR) != oldDate.get(Calendar.YEAR))
                {
                    item.tick = true;
                    item.flag = true;
                    item.text = yearFormatter.format(currentDate.getTime());
                }

                oldDate.setTime(bar.getDate());
            }

            dateList.add(item);
            map.put(item.date, new Integer(x));

            x += indicatorPlot.getGridWidth();
        }
    }
    
    private void getMinuteDate()
    {
        SimpleDateFormat monthDayFormatter = new SimpleDateFormat("MMM d"); //$NON-NLS-1$

        Calendar nextHour = null;
        Calendar oldDay = Calendar.getInstance();

        int x = indicatorPlot.getMarginWidth() + indicatorPlot.getGridWidth() / 2;

        for (Iterator iter = barData.iterator(); iter.hasNext(); )
        {
            Bar bar = (Bar)iter.next();

            if (nextHour == null)
            {
                nextHour = Calendar.getInstance();
                nextHour.setTime(bar.getDate());
                nextHour.set(Calendar.MINUTE, 0);
                nextHour.set(Calendar.SECOND, 0);
                if (interval != BarData.INTERVAL_MINUTE1 && interval != BarData.INTERVAL_MINUTE2)
                    nextHour.add(Calendar.SECOND, 7200);
                else
                    nextHour.add(Calendar.SECOND, 3600);
            }
            if (oldDay == null)
            {
                oldDay = Calendar.getInstance();
                oldDay.setTime(bar.getDate());
            }
            
            TickItem item = new TickItem(bar.getDate());

            Calendar date = Calendar.getInstance();
            date.setTime(bar.getDate());

            if (date.get(Calendar.DATE) != oldDay.get(Calendar.DATE))
            {
                item.tick = true;
                item.flag = true;
                item.text = monthDayFormatter.format(date.getTime());
                oldDay = date;
            }
            else
            {
                if (date.after(nextHour) || date.equals(nextHour))
                {
                    if (interval < BarData.INTERVAL_MINUTE30)
                    {
                        item.tick = true;
                        item.flag = false;
                        item.text = date.get(Calendar.HOUR_OF_DAY) + ":00";
                    }
                }
            }

            if (date.after(nextHour) || date.equals(nextHour))
            {
                nextHour = (Calendar) date.clone();
                nextHour.set(Calendar.MINUTE, 0);
                nextHour.set(Calendar.SECOND, 0);
                if (interval != BarData.INTERVAL_MINUTE1 && interval != BarData.INTERVAL_MINUTE2)
                    nextHour.add(Calendar.SECOND, 7200);
                else
                    nextHour.add(Calendar.SECOND, 3600);
            }

            dateList.add(item);
            map.put(item.date, new Integer(x));

            x += indicatorPlot.getGridWidth();
        }
    }
    
    public void setLabel(int x)
    {
        int index = (x - getIndicatorPlot().getPlotLocation().x - indicatorPlot.getMarginWidth()) / indicatorPlot.getGridWidth();
        if (index >= 0 && index < dateList.size())
        {
            if (interval < BarData.INTERVAL_DAILY)
                label.setText(" " + tf.format(((TickItem)dateList.get(index)).date) + " ");
            else
                label.setText(" " + df.format(((TickItem)dateList.get(index)).date) + " ");
            label.pack();
            label.setBounds(x - label.getBounds().width / 2, 1, label.getBounds().width, 14);
        }
    }
    
    public void hideLabel()
    {
        label.setBounds(0, 0, 0, 0);
    }
    
    public List getDateList()
    {
        return dateList;
    }
}
