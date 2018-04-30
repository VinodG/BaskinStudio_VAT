package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.TaxGroupTaxesDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class TaxGroupTaxesParser extends BaseHandler
{
    private Vector<TaxGroupTaxesDo> vecTaxGroupTaxesDos ;
    private TaxGroupTaxesDo taxGroupTaxesDo;

    public TaxGroupTaxesParser(Context context) {
        super(context);
    }
    @Override
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
    {
        currentElement  = true;
        currentValue = new StringBuilder();
        if(localName.equalsIgnoreCase("TaxGroupTaxes"))
        {
            vecTaxGroupTaxesDos = new Vector<TaxGroupTaxesDo>();
        }
        else if(localName.equalsIgnoreCase("TaxGroupTaxesDco"))
        {
            taxGroupTaxesDo = new TaxGroupTaxesDo();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException
    {
        currentElement  = false;

        if(localName.equalsIgnoreCase("UID"))
        {
            taxGroupTaxesDo.UID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxGroupUID"))
        {
            taxGroupTaxesDo.TaxGroupUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxUID"))
        {
            taxGroupTaxesDo.TaxUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ss"))
        {
            taxGroupTaxesDo.ss = StringUtils.getInt(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("CreatedTime"))
        {
            taxGroupTaxesDo.CreatedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ModifiedTime"))
        {
            taxGroupTaxesDo.ModifiedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxGroupTaxesDco"))
        {
            vecTaxGroupTaxesDos.add(taxGroupTaxesDo);
        }
        else if(localName.equalsIgnoreCase("TaxGroupTaxes"))
        {
            if(vecTaxGroupTaxesDos != null && vecTaxGroupTaxesDos.size() > 0)
                insertTaxGroupTaxesDos(vecTaxGroupTaxesDos);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException
    {
        if (currentElement)
            currentValue.append(new String(ch, start, length));
    }
    private void insertTaxGroupTaxesDos(Vector<TaxGroupTaxesDo> vecTaxDos)
    {
        new TaxDa().insertTaxGroupTaxesDos(vecTaxDos);
    }


}
