/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan S. Stratigakos - original qtstalker code
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.charts.indicators;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.Settings;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ATR extends IndicatorPlugin
{
    public static final RGB DEFAULT_COLOR = new RGB(0, 0, 192);
    public static final String DEFAULT_LABEL = "ATR";
    public static final int DEFAULT_LINETYPE = PlotLine.LINE;
    public static final int DEFAULT_SMOOTHING = 14;
    public static final int DEFAULT_MA_TYPE = EMA;
    private Color color = new Color(null, DEFAULT_COLOR);
    private String label = DEFAULT_LABEL;
    private int lineType = DEFAULT_LINETYPE;
    private int smoothing = DEFAULT_SMOOTHING;
    private int maType = DEFAULT_MA_TYPE;

    public ATR()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.charts.IndicatorPlugin#calculate()
     */
    public void calculate()
    {
        PlotLine tr = new PlotLine();
        int loop;
        for (loop = 0; loop < getBarData().size(); loop++)
        {
            double high = getBarData().getHigh(loop);
            double low = getBarData().getLow(loop);
            double close;
            if (loop > 0)
                close = getBarData().getClose(loop - 1);
            else
                close = high;

            double t = high - low;

            double t2 = Math.abs(high - close);
            if (t2 > t)
                t = t2;

            t2 = Math.abs(low - close);
            if (t2 > t)
                t = t2;

            tr.append(t);
        }

        if (smoothing > 1)
        {
            PlotLine ma = getMA(tr, maType, smoothing);
            ma.setColor(color);
            ma.setType(lineType);
            ma.setLabel(label);
            getOutput().add(ma);
        }
        else
        {
            tr.setColor(color);
            tr.setType(lineType);
            tr.setLabel(label);
            getOutput().add(tr);
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.charts.IndicatorPlugin#setParameters(net.sourceforge.eclipsetrader.charts.Settings)
     */
    public void setParameters(Settings settings)
    {
        color = settings.getColor("color", color);
        label = settings.getString("label", label);
        lineType = settings.getInteger("lineType", lineType).intValue();
        smoothing = settings.getInteger("smoothing", smoothing).intValue();
        maType = settings.getInteger("maType", maType).intValue();
    }
}