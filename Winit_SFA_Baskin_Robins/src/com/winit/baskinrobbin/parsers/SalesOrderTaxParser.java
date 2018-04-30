package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.SalesOrderTaxDivisionDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class SalesOrderTaxParser extends BaseHandler
{
    private Vector<SalesOrderTaxDivisionDO> vecTaxSlaseDos ;
    private SalesOrderTaxDivisionDO taxSalesDo;

    public SalesOrderTaxParser(Context context) {
        super(context);
    }
    @Override
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
    {
        currentElement  = true;
        currentValue = new StringBuilder();
        if(localName.equalsIgnoreCase("SalesOrderTaxs"))
        {
            vecTaxSlaseDos = new Vector<SalesOrderTaxDivisionDO>();
        }
        else if(localName.equalsIgnoreCase("SalesOrderTaxDco"))
        {
            taxSalesDo = new SalesOrderTaxDivisionDO();
        }
    }
    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException
    {
        currentElement  = false;

        if(localName.equalsIgnoreCase("Id"))
        {
            taxSalesDo.Id = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("UID"))
        {
            taxSalesDo.UID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("SalesOrderUID"))
        {
            taxSalesDo.SalesOrderUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("SalesOrderLineUID"))
        {
            taxSalesDo.SalesOrderLineUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxUID"))
        {
            taxSalesDo.TaxUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxSlabUID"))
        {
            taxSalesDo.TaxSlabUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxAmount"))
        {
            taxSalesDo.TaxAmount = StringUtils.getDouble(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("TaxName"))
        {
            taxSalesDo.TaxName = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ApplicableAt"))
        {
            taxSalesDo.ApplicableAt = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("DependentTaxUID"))
        {
            taxSalesDo.DependentTaxUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("DependentTaxName"))
        {
            taxSalesDo.DependentTaxName = (currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("TaxCalculationType"))
        {
            taxSalesDo.TaxCalculationType = (currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("BaseTaxRate"))
        {
            taxSalesDo.BaseTaxRate = StringUtils.getDouble(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("RangeStart"))
        {
            taxSalesDo.RangeStart = StringUtils.getDouble(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("RangeEnd"))
        {
            taxSalesDo.RangeEnd = StringUtils.getDouble(currentValue.toString());
        }

        else if(localName.equalsIgnoreCase("TaxRate"))
        {
            taxSalesDo.TaxRate =StringUtils.getDouble(currentValue.toString());
        }

        else if(localName.equalsIgnoreCase("SalesOrderTaxDco"))
        {
            vecTaxSlaseDos.add(taxSalesDo);
        }
        else if(localName.equalsIgnoreCase("SalesOrderTaxs"))
        {
            if(vecTaxSlaseDos != null && vecTaxSlaseDos.size() > 0)
                insert(vecTaxSlaseDos);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException
    {
        if (currentElement)
            currentValue.append(new String(ch, start, length));
    }
    private void insert(Vector<SalesOrderTaxDivisionDO> vecTaxSlaseDos)
    {
        new TaxDa().insertSalesOrderTax(vecTaxSlaseDos);
    }
}
