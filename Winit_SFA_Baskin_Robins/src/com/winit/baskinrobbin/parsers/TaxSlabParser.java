package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.TaxSlabDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class TaxSlabParser extends BaseHandler
{
    private Vector<TaxSlabDo> vecTaxSlabDos ;
    private TaxSlabDo taxSlabDo;

    public TaxSlabParser(Context context) {
        super(context);
    }
    @Override
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
    {
        currentElement  = true;
        currentValue = new StringBuilder();
        if(localName.equalsIgnoreCase("TaxSlabs"))
        {
            vecTaxSlabDos = new Vector<TaxSlabDo>();
        }
        else if(localName.equalsIgnoreCase("TaxSlabDco"))
        {
            taxSlabDo = new TaxSlabDo();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException
    {
        currentElement  = false;

        if(localName.equalsIgnoreCase("UID"))
        {
            taxSlabDo.UID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxUID"))
        {
            taxSlabDo.TaxUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("RangeStart"))
        {
            taxSlabDo.RangeStart = StringUtils.getFloat(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("RangeEnd"))
        {
            taxSlabDo.RangeEnd = StringUtils.getFloat(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("TaxRate"))
        {
            taxSlabDo.TaxRate = StringUtils.getFloat(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("Status"))
        {
            taxSlabDo.Status = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ss"))
        {
            taxSlabDo.ss =StringUtils.getInt(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("ValidFrom"))
        {
            taxSlabDo.ValidFrom = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ValidUpTo"))
        {
            taxSlabDo.ValidUpTo = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("CreatedTime"))
        {
            taxSlabDo.CreatedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ModifiedTime"))
        {
            taxSlabDo.ModifiedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxSlabDco"))
        {
            vecTaxSlabDos.add(taxSlabDo);
        }
        else if(localName.equalsIgnoreCase("TaxSlab"))
        {
            if(vecTaxSlabDos != null && vecTaxSlabDos.size() > 0)
                insertTaxGroupTaxesDos(vecTaxSlabDos);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException
    {
        if (currentElement)
            currentValue.append(new String(ch, start, length));
    }
    private void insertTaxGroupTaxesDos(Vector<TaxSlabDo> vecTaxSlabDos)
    {
        new TaxDa().insertTaxSlabDos(vecTaxSlabDos);
    }


}
