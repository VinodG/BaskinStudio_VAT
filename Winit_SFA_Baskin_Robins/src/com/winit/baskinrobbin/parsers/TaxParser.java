package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.TaxDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class TaxParser extends BaseHandler
{
    private Vector<TaxDo> vecTaxDos ;
    private TaxDo taxDo;

    public TaxParser(Context context) {
        super(context);
    }
    @Override
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
    {
        currentElement  = true;
        currentValue = new StringBuilder();
        if(localName.equalsIgnoreCase("Taxes"))
        {
            vecTaxDos = new Vector<TaxDo>();
        }
        else if(localName.equalsIgnoreCase("TaxMasterDco"))
        {
            taxDo = new TaxDo();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException
    {
        currentElement  = false;

        if(localName.equalsIgnoreCase("UID"))
        {
            taxDo.UID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("Name"))
        {
            taxDo.Name = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ApplicableAt"))
        {
            taxDo.ApplicableAt = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("DependentTaxUID"))
        {
            taxDo.DependentTaxUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxCalculationType"))
        {
            taxDo.TaxCalculationType = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("BaseTaxRate"))
        {
            taxDo.BaseTaxRate = StringUtils.getFloat(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("Status"))
        {
            taxDo.Status = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ValidFrom"))
        {
            taxDo.ValidFrom = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ValidUpto"))
        {
            taxDo.ValidUpto = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ss"))
        {
            taxDo.ss =StringUtils.getInt(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("CreatedTime"))
        {
            taxDo.CreatedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ModifiedTime"))
        {
            taxDo.ModifiedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxMasterDco"))
        {
            vecTaxDos.add(taxDo);
        }
        else if(localName.equalsIgnoreCase("Taxes"))
        {
            if(vecTaxDos != null && vecTaxDos.size() > 0)
                insertTax(vecTaxDos);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException
    {
        if (currentElement)
            currentValue.append(new String(ch, start, length));
    }
    private void insertTax(Vector<TaxDo> vecTaxDos)
    {
        new TaxDa().insertTax(vecTaxDos);
    }


}
