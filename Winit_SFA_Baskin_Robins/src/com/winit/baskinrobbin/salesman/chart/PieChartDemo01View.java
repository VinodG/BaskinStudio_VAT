/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    AFreeChart: http://code.google.com/p/afreechart/
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -----------------
 * PieChartDemo01View.java
 * -----------------
 * (C) Copyright 2010, 2011, by ICOMSYSTECH Co.,Ltd.
 *
 * Original Author:  Niwano Masayoshi (for ICOMSYSTECH Co.,Ltd);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 19-Nov-2010 : Version 0.0.1 (NM);
 */

package com.winit.baskinrobbin.salesman.chart;

import java.text.DecimalFormat;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.plot.PiePlot;
import org.afree.chart.title.TextTitle;
import org.afree.data.general.DefaultPieDataset;
import org.afree.data.general.PieDataset;
import org.afree.graphics.geom.Font;

import android.content.Context;
import android.graphics.Typeface;

/**
 * PieChartDemo01View
 */
public class PieChartDemo01View extends DemoView {

    /**
     * constructor
     * @param context
     */
	
	private static Double one;
	private static Double two;
	private static Double percentOne;
	private static Double percentTwo;
	private static boolean isPayment = false;
	private Context context;
    public PieChartDemo01View(Context context, Double one, Double two, boolean isPayment, String str) {
        super(context);

        this.one = one;
        this.two = two;
        this.isPayment = isPayment;
        final PieDataset dataset = createDataset(str);
        final AFreeChart chart = createChart(dataset);
        this.context = context;
        setChart(chart);
    }

    /**
     * Creates a sample dataset.
     * @return a sample dataset.
     */
    private static PieDataset createDataset(String str) 
    {
    	
    	DecimalFormat dfff = new DecimalFormat("##.##");
		dfff.setMaximumFractionDigits(2);
		dfff.setMinimumFractionDigits(2);
    	
    	percentOne = one *100/(one + two);
    	percentTwo = two *100/(one + two);
    	
    	if(percentOne == 0)
    		one = (double) 1;
    	
    	if(percentTwo == 0)
    		two = (double) 1;
    	
        DefaultPieDataset dataset = new DefaultPieDataset();
    	 if(isPayment)
         {
 	        dataset.setValue(" "+dfff.format(percentOne)+"%"+" ("+str+" "+one+") ",one);
 	        dataset.setValue(" "+dfff.format(percentTwo)+"%"+" ("+str+" "+two+") ", two);
         }
         else
         {
         	dataset.setValue(" "+dfff.format(percentOne)+"%"+" ("+one+") ",one);
 	        dataset.setValue(" "+dfff.format(percentTwo)+"%"+" ("+two+") ", two);
         }
        	 
        return dataset;
    }

    /**
     * Creates a chart.
     * @param dataset the data-set.
     * @return a chart.
     */
    private static AFreeChart createChart(PieDataset dataset) {

        AFreeChart chart = ChartFactory.createPieChart(
        		"", // chart title
                dataset, // data
                false, // include legend
                false,
                false);
        TextTitle title = chart.getTitle();
        
        title.setToolTipText("A title tooltip!");

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Typeface.NORMAL, 22));
        plot.setNoDataMessage("No data available");
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        return chart;

    }
}
