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
 * BarChartDemo01View.java
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


import java.util.Vector;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.CategoryAxis;
import org.afree.chart.axis.CategoryLabelPositions;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.plot.CategoryPlot;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.renderer.category.BarRenderer;
import org.afree.data.category.CategoryDataset;
import org.afree.data.category.DefaultCategoryDataset;
import org.afree.graphics.GradientColor;
import org.afree.graphics.SolidColor;

import android.content.Context;
import android.graphics.Color;

import com.winit.baskinrobbin.salesman.dataobject.SalesTargetDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

/**
 * BarChartDemo01View
 */
public class BarChartDemo01View extends DemoView {

	private static Vector<SalesTargetDO> vecSalesTargetDOs;
	private static String strCat = "";
    /**
     * constructor
     * @param context
     */
	
	
    public BarChartDemo01View(Context context, Vector<SalesTargetDO> vecSalesTargetDOs, String strCat) {
        super(context);

        this.vecSalesTargetDOs = vecSalesTargetDOs;
        this.strCat = strCat;
        CategoryDataset dataset = createDataset();
        AFreeChart chart = createChart(dataset);
        setChart(chart);
    }

    /**
     * Returns a sample dataset.
     *
     * @return The dataset.
     */
    private static CategoryDataset createDataset() {

        // row keys...
        String series1 = "Target";
        String series2 = "Achieved";
//        String series3 = "Third";

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SalesTargetDO salesTargetDO : vecSalesTargetDOs) {
        	 String category = salesTargetDO.cat;
        	 dataset.addValue(StringUtils.getFloat(salesTargetDO.target), series1, category);
        	 dataset.addValue(StringUtils.getFloat(salesTargetDO.achived), series2, category);
		}
        // column keys...
//        String category1 = "Category 1";
//        String category2 = "Category 2";
//        String category3 = "Category 3";
//        String category4 = "Category 4";
//        String category5 = "Category 5";

        // create the dataset...
        

//        dataset.addValue(1.0, series1, category1);
//        dataset.addValue(4.0, series1, category2);
//        dataset.addValue(3.0, series1, category3);
//        dataset.addValue(5.0, series1, category4);
//        dataset.addValue(5.0, series1, category5);
//
//        dataset.addValue(5.0, series2, category1);
//        dataset.addValue(7.0, series2, category2);
//        dataset.addValue(6.0, series2, category3);
//        dataset.addValue(8.0, series2, category4);
//        dataset.addValue(4.0, series2, category5);

//        dataset.addValue(4.0, series3, category1);
//        dataset.addValue(3.0, series3, category2);
//        dataset.addValue(2.0, series3, category3);
//        dataset.addValue(3.0, series3, category4);
//        dataset.addValue(6.0, series3, category5);

        return dataset;

    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private static AFreeChart createChart(CategoryDataset dataset) {

    	
        // create the chart...
        AFreeChart chart = ChartFactory.createBarChart(
            "",      // chart title
            strCat,               // domain axis label
            "Value",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            false,                     // include legend
            false,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaintType(new SolidColor(Color.WHITE));

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        
        GradientColor gp0 = null;
        GradientColor gp1;
        if(strCat.equals("Brands"))
        {
        	gp0 = new GradientColor(Color.parseColor("#FF6633"), Color.rgb(0, 0, 64));
        	gp1 = new GradientColor(Color.parseColor("#AAAA66"), Color.rgb(0, 64, 0));
        }
        else  if(strCat.equals("Categories"))
        {
        	gp0 = new GradientColor(Color.parseColor("#009999"), Color.rgb(0, 0, 64));
        	gp1 = new GradientColor(Color.parseColor("#00AA66"), Color.rgb(0, 64, 0));
        }
        else  
        {
        	gp0 = new GradientColor(Color.parseColor("#0A94D1"), Color.rgb(0, 0, 64));
        	gp1 = new GradientColor(Color.parseColor("#545454"), Color.rgb(0, 64, 0));
        }
        
        
        GradientColor gp2 = new GradientColor(Color.RED, Color.rgb(64, 0, 0));
        renderer.setSeriesPaintType(0, gp0);
        renderer.setSeriesPaintType(1, gp1);
        renderer.setSeriesPaintType(2, gp2);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }
}
