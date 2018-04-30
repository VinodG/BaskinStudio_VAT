package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.TaxSkuMapDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class TaxSkuMapParser extends BaseHandler
{
    private Vector<TaxSkuMapDo> vecTaxSkuMaps ;
    private TaxSkuMapDo taxSkuMapDo;

    public TaxSkuMapParser(Context context) {
        super(context);
    }
    @Override
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
    {
        currentElement  = true;
        currentValue = new StringBuilder();
        if(localName.equalsIgnoreCase("TaxSkuMaps"))
        {
            vecTaxSkuMaps = new Vector<TaxSkuMapDo>();
        }
        else if(localName.equalsIgnoreCase("TaxSkuMapDco"))
        {
            taxSkuMapDo = new TaxSkuMapDo();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException
    {
        currentElement  = false;

        if(localName.equalsIgnoreCase("UID"))
        {
            taxSkuMapDo.UID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("SKUUID"))
        {
            taxSkuMapDo.SKUUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxUID"))
        {
            taxSkuMapDo.TaxUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ss"))
        {
            taxSkuMapDo.ss = StringUtils.getInt(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("CreatedTime"))
        {
            taxSkuMapDo.CreatedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ModifiedTime"))
        {
            taxSkuMapDo.ModifiedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxSkuMapDco"))
        {
            vecTaxSkuMaps.add(taxSkuMapDo);
        }
        else if(localName.equalsIgnoreCase("TaxSkuMaps"))
        {
            if(vecTaxSkuMaps != null && vecTaxSkuMaps.size() > 0)
                insertTaxGroupTaxesDos(vecTaxSkuMaps);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException
    {
        if (currentElement)
            currentValue.append(new String(ch, start, length));
    }
    private void insertTaxGroupTaxesDos(Vector<TaxSkuMapDo> vecTaxSkuMaps)
    {
        new TaxDa().insertTaxSkuMapDos(vecTaxSkuMaps);
    }


}
