package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.TaxGroupDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class TaxGroupParser extends BaseHandler
{
    private Vector<TaxGroupDo> vecTaxGroupDos ;
    private TaxGroupDo taxGroupDo;

    public TaxGroupParser(Context context) {
        super(context);
    }
    @Override
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
    {
        currentElement  = true;
        currentValue = new StringBuilder();
        if(localName.equalsIgnoreCase("TaxGroups"))
        {
            vecTaxGroupDos = new Vector<TaxGroupDo>();
        }
        else if(localName.equalsIgnoreCase("TaxGroupDco"))
        {
            taxGroupDo = new TaxGroupDo();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException
    {
        currentElement  = false;

        if(localName.equalsIgnoreCase("UID"))
        {
            taxGroupDo.UID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("Name"))
        {
            taxGroupDo.Name = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("Description"))
        {
            taxGroupDo.Description = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ss"))
        {
            taxGroupDo.ss = StringUtils.getInt(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("CreatedTime"))
        {
            taxGroupDo.CreatedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ModifiedTime"))
        {
            taxGroupDo.ModifiedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxGroupDco"))
        {
            vecTaxGroupDos.add(taxGroupDo);
        }
        else if(localName.equalsIgnoreCase("TaxGroups"))
        {
            if(vecTaxGroupDos != null && vecTaxGroupDos.size() > 0)
                insertTaxGroupDos(vecTaxGroupDos);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException
    {
        if (currentElement)
            currentValue.append(new String(ch, start, length));
    }
    private void insertTaxGroupDos(Vector<TaxGroupDo> vecTaxDos)
    {
        new TaxDa().insertTaxGroupDos(vecTaxDos);
    }


}
