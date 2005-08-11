/*******************************************************************************
 * Copyright (c) 2004 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 *******************************************************************************/
package net.sourceforge.eclipsetrader.ui.views.charts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.eclipsetrader.BasicData;
import net.sourceforge.eclipsetrader.ChartData;
import net.sourceforge.eclipsetrader.IBackfillDataProvider;
import net.sourceforge.eclipsetrader.IBasicData;
import net.sourceforge.eclipsetrader.IChartData;
import net.sourceforge.eclipsetrader.IIndexDataProvider;
import net.sourceforge.eclipsetrader.IRealtimeChartListener;
import net.sourceforge.eclipsetrader.IRealtimeChartProvider;
import net.sourceforge.eclipsetrader.TraderPlugin;
import net.sourceforge.eclipsetrader.ui.internal.views.Messages;
import net.sourceforge.eclipsetrader.ui.internal.views.ViewsPlugin;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RealtimeChartView extends ChartView implements IRealtimeChartListener, DropTargetListener
{
  public static final String VIEW_ID = "net.sourceforge.eclipsetrader.ui.views.RealtimeChart";
  private IRealtimeChartProvider chartProvider;
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl(Composite parent)
  {
    df = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$

    super.createPartControl(parent);
    dateLabel.setText(Messages.getString("ChartView.Time")); //$NON-NLS-1$
    
    // Drag and drop support
    DropTarget target = new DropTarget(parent, DND.DROP_COPY);
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    target.setTransfer(types);
    target.addDropListener(this);

    // Restore del grafico precedente
    String id = getViewSite().getSecondaryId();
    String symbol = ViewsPlugin.getDefault().getPreferenceStore().getString("rtchart." + id); //$NON-NLS-1$
    if (!symbol.equals("")) //$NON-NLS-1$
    {
      IBasicData bd = TraderPlugin.getData(symbol);
      if (bd == null)
      {
        bd = new BasicData();
        bd.setSymbol(symbol);
        bd.setTicker(symbol);
        bd.setDescription(symbol);
      }
      setData(bd);
    }

//    getSite().setSelectionProvider(this);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#dispose()
   */
  public void dispose()
  {
    if (basicData != null && TraderPlugin.getDataProvider() instanceof IRealtimeChartProvider)
    {
      IRealtimeChartProvider rtp = (IRealtimeChartProvider)TraderPlugin.getDataProvider();
      rtp.removeRealtimeChartListener(basicData, this);
    }
    super.dispose();
  }

  public void reloadPreferences()
  {
    File folder = new File(Platform.getLocation().toFile(), "rtcharts"); //$NON-NLS-1$
    reloadPreferences(folder);
    setMargin(1);
    setWidth(3);
  }

  public void savePreferences()
  {
    File folder = new File(Platform.getLocation().toFile(), "rtcharts"); //$NON-NLS-1$
    savePreferences(folder);
  }

  public void setData(final IBasicData d)
  {
    if (basicData != null && chartProvider != null)
      chartProvider.removeRealtimeChartListener(basicData, this);
    chartProvider = null;

    if (isIndex(d) == true)
    {
      IIndexDataProvider ip = getIndexProvider(d);
      if (ip instanceof IRealtimeChartProvider)
        chartProvider = (IRealtimeChartProvider)ip;
      updateIndexData(d);
      setData(d, d.getTicker() + " - " + Messages.getString("RealtimeChartView.title"), "rtchart."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    else
    {
      if (TraderPlugin.getDataProvider() instanceof IRealtimeChartProvider)
        chartProvider = (IRealtimeChartProvider)TraderPlugin.getDataProvider();
      setData(d, d.getTicker() + " - " + Messages.getString("RealtimeChartView.title"), "rtchart."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    if (basicData != null && chartProvider != null)
      chartProvider.addRealtimeChartListener(basicData, this);
  }
  
  public IChartData[] getChartData(IBasicData data)
  {
    this.data = load();
    if (this.data == null && chartProvider != null)
      this.data = chartProvider.getHistoryData(basicData);
    return this.data;
/*    IChartData[] c = chartProvider.getHistoryData(basicData);
    if (c == null)
      c = load();
    return c;*/
/*    IChartData[] c = null;
    if (TraderPlugin.getDataProvider() instanceof IRealtimeChartProvider)
    {
      IRealtimeChartProvider rtp = (IRealtimeChartProvider)TraderPlugin.getDataProvider();
      c = rtp.getHistoryData(basicData);
    }
    if (c == null)
      c = load();
    return c;*/
  }
  
  public void refreshChart()
  {
    if (basicData != null)
    {
      if (isIndex(basicData) == true)
      {
        if (chartProvider instanceof IBackfillDataProvider)
          chartProvider.setHistoryData(basicData, ((IBackfillDataProvider)chartProvider).getIntradayData(basicData));
        else
          chartProvider.backfill(basicData);
        realtimeChartUpdated(chartProvider);
      }
      else if (TraderPlugin.getDataProvider() instanceof IRealtimeChartProvider)
      {
        IRealtimeChartProvider rtp = (IRealtimeChartProvider)TraderPlugin.getDataProvider();
        IBackfillDataProvider backfill = TraderPlugin.getBackfillDataProvider(); 
        if (backfill != null)
          rtp.setHistoryData(basicData, backfill.getIntradayData(basicData));
        else
          rtp.backfill(basicData);
        realtimeChartUpdated(rtp);
      }
    }
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
   */
  public void paintControl(PaintEvent e)
  {
    GC gc = e.gc;
    
    if (e.getSource() == bottombar)
    {
      gc.setForeground(textColor);
      gc.drawLine(0, 0, bottombar.getClientArea().width, 0); 

      if (basicData != null)
      {
        Calendar current = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$
        
        gc.setClipping(0, 0, bottombar.getClientArea().width - scaleWidth, bottombar.getClientArea().height);

        int x = margin + width / 2;
        if (container.getHorizontalBar().isVisible() == true)
          x -= container.getHorizontalBar().getSelection();
        for (int i = 0; i < data.length; i++, x += width)
        {
          current.setTime(data[i].getDate());
          if (i == 0 || (current.getTimeInMillis() - last.getTimeInMillis()) / 1000 >= 30*60)
          {
            String s = df.format(data[i].getDate());
            int x1 = x - gc.stringExtent(s).x / 2;
            gc.drawLine(x, 0, x, 5);
            gc.drawString(s, x1, 5);
            last.setTime(data[i].getDate());
            if (last.get(Calendar.MINUTE) < 30)
              last.add(Calendar.MINUTE, -last.get(Calendar.MINUTE));
            else
              last.add(Calendar.MINUTE, -(last.get(Calendar.MINUTE) - 30));
          }
        }
/*
        int lastValue = -1;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$
        
        gc.setClipping(0, 0, bottombar.getClientArea().width - scaleWidth, bottombar.getClientArea().height);

        int x = margin + width / 2;
        if (container.getHorizontalBar().isVisible() == true)
          x -= container.getHorizontalBar().getSelection();
        for (int i = 0; i < data.length; i++, x += width)
        {
          c.setTime(data[i].getDate());
          if (c.get(Calendar.HOUR_OF_DAY) != lastValue || c.get(Calendar.MINUTE) == 30 || c.get(Calendar.MINUTE) == 31)
          {
            String s = df.format(data[i].getDate());
            int x1 = x - gc.stringExtent(s).x / 2;
            gc.drawLine(x, 0, x, 5);
            gc.drawString(s, x1, 5);
            lastValue = c.get(Calendar.HOUR_OF_DAY);
          }
        }
*/
      }
    }
    else
    {
      gc.setForeground(textColor);
      Composite c = (Composite)e.getSource();
      gc.drawLine(0, c.getClientArea().height - 1, c.getClientArea().width, c.getClientArea().height - 1); 
    }
  }

  /* (non-Javadoc)
   * @see net.sourceforge.eclipsetrader.IRealtimeChartListener#realtimeChartUpdated(net.sourceforge.eclipsetrader.IRealtimeChartProvider)
   */
  public void realtimeChartUpdated(final IRealtimeChartProvider provider)
  {
    new Thread(new Runnable() {
      public void run() 
      {
        data = provider.getHistoryData(basicData);
        store();
        container.getDisplay().asyncExec(new Runnable() {
          public void run() {
            if (data != null && data.length > 0)
            {
              if (data[0].getMaxPrice() >= 10)
              {
                pf.setMinimumFractionDigits(2);
                pf.setMaximumFractionDigits(2);
                setScaleWidth(42);
              }
              else
              {
                pf.setMinimumFractionDigits(4);
                pf.setMaximumFractionDigits(4);
                setScaleWidth(50);
              }
            }
            else
              setScaleWidth(50);
            controlResized(null);
            bottombar.redraw();
            updateLabels();
          }
        });
        updateView();
      }
    }).start();
  }

  /* (non-Javadoc)
   * @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
   */
  public void dragEnter(DropTargetEvent event)
  {
    event.detail = DND.DROP_COPY;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.DropTargetEvent)
   */
  public void dragLeave(DropTargetEvent event)
  {
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
   */
  public void dragOperationChanged(DropTargetEvent event)
  {
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
   */
  public void dragOver(DropTargetEvent event)
  {
    event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
   */
  public void drop(DropTargetEvent event)
  {
    String[] item = ((String)event.data).split(";"); //$NON-NLS-1$
    String id = getViewSite().getSecondaryId();
    String symbol = item[1];
    ViewsPlugin.getDefault().getPreferenceStore().setValue("rtchart." + id, item[1]); //$NON-NLS-1$
    if (!symbol.equals("")) //$NON-NLS-1$
    {
      IBasicData bd = TraderPlugin.getData(symbol);
      if (bd == null)
      {
        bd = new BasicData();
        bd.setSymbol(symbol);
        bd.setTicker(symbol);
        bd.setDescription(symbol);
      }
      setData(bd);
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
   */
  public void dropAccept(DropTargetEvent event)
  {
  }

  /**
   * Load the chart data from the local storage.
   * <p></p>
   */
  private IChartData[] load()
  {
    Vector _data = new Vector();
    File folder = new File(Platform.getLocation().toFile(), "rtcharts"); //$NON-NLS-1$
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss"); //$NON-NLS-1$
    NumberFormat nf = NumberFormat.getInstance();
    
    nf.setGroupingUsed(true);
    nf.setMaximumFractionDigits(4);
    nf.setMinimumFractionDigits(4);
    nf.setMinimumIntegerDigits(1);
    
    File file = new File(folder, basicData.getSymbol().toLowerCase() + ".xml"); //$NON-NLS-1$
    if (file.exists() == true)
      try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        NodeList firstChild = document.getFirstChild().getChildNodes();
        for (int c = 0; c < firstChild.getLength(); c++)
        {
          if (firstChild.item(c).getNodeName().equalsIgnoreCase("data")) //$NON-NLS-1$
          {
            NodeList parent = firstChild.item(c).getChildNodes();
            IChartData cd = new ChartData();
            for (int i = 0; i < parent.getLength(); i++)
            {
              Node n = parent.item(i);
              Node value = n.getFirstChild();
              if (value != null)
              {
                if (n.getNodeName().equalsIgnoreCase("open") == true) //$NON-NLS-1$
                  cd.setOpenPrice(nf.parse(value.getNodeValue()).doubleValue());
                else if (n.getNodeName().equalsIgnoreCase("max") == true) //$NON-NLS-1$
                  cd.setMaxPrice(nf.parse(value.getNodeValue()).doubleValue());
                else if (n.getNodeName().equalsIgnoreCase("min") == true) //$NON-NLS-1$
                  cd.setMinPrice(nf.parse(value.getNodeValue()).doubleValue());
                else if (n.getNodeName().equalsIgnoreCase("close") == true) //$NON-NLS-1$
                  cd.setClosePrice(nf.parse(value.getNodeValue()).doubleValue());
                else if (n.getNodeName().equalsIgnoreCase("volume") == true) //$NON-NLS-1$
                  cd.setVolume(Integer.parseInt(value.getNodeValue()));
                else if (n.getNodeName().equalsIgnoreCase("date") == true) //$NON-NLS-1$
                {
                  try {
                    cd.setDate(df.parse(value.getNodeValue()));
                  } catch(Exception e) {};
                }
              }
            }
            _data.addElement(cd);
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      
    data = new IChartData[_data.size()];
    _data.toArray(data);
    
    return data;
  }

  /**
   * Save the chart data to the local storage.
   * <p></p>
   */
  private void store()
  {
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss"); //$NON-NLS-1$
    NumberFormat nf = NumberFormat.getInstance();
    
    nf.setGroupingUsed(true);
    nf.setMaximumFractionDigits(4);
    nf.setMinimumFractionDigits(4);
    nf.setMinimumIntegerDigits(1);

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.getDOMImplementation().createDocument("", "chart", null); //$NON-NLS-1$ //$NON-NLS-2$

      for (int i = 0; i < data.length; i++)
      {
        Element element = document.createElement("data"); //$NON-NLS-1$
        document.getDocumentElement().appendChild(element);

        Node node = document.createElement("date"); //$NON-NLS-1$
        element.appendChild(node);
        node.appendChild(document.createTextNode(df.format(data[i].getDate())));
        node = document.createElement("open"); //$NON-NLS-1$
        element.appendChild(node);
        node.appendChild(document.createTextNode(nf.format(data[i].getOpenPrice())));
        node = document.createElement("close"); //$NON-NLS-1$
        element.appendChild(node);
        node.appendChild(document.createTextNode(nf.format(data[i].getClosePrice())));
        node = document.createElement("max"); //$NON-NLS-1$
        element.appendChild(node);
        node.appendChild(document.createTextNode(nf.format(data[i].getMaxPrice())));
        node = document.createElement("min"); //$NON-NLS-1$
        element.appendChild(node);
        node.appendChild(document.createTextNode(nf.format(data[i].getMinPrice())));
        node = document.createElement("volume"); //$NON-NLS-1$
        element.appendChild(node);
        node.appendChild(document.createTextNode("" + data[i].getVolume())); //$NON-NLS-1$
      }

      File folder = new File(Platform.getLocation().toFile(), "rtcharts"); //$NON-NLS-1$
      folder.mkdirs();

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1"); //$NON-NLS-1$
      transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
      transformer.setOutputProperty("{http\u003a//xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
      DOMSource source = new DOMSource(document);
      BufferedWriter out = new BufferedWriter(new FileWriter(new File(folder, basicData.getSymbol().toLowerCase() + ".xml"))); //$NON-NLS-1$
      StreamResult result = new StreamResult(out);
      transformer.transform(source, result);
      out.flush();
      out.close();
    } catch (Exception ex) {};
  }
}
