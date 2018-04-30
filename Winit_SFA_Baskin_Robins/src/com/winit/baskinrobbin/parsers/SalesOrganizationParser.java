package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.SalesOrganizationDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class SalesOrganizationParser extends BaseHandler
{
    private Vector<SalesOrganizationDO> vecSalesOrgDos ;
    private SalesOrganizationDO SalesOrgDo;

    public SalesOrganizationParser(Context context) {
        super(context);
    }
    @Override
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
    {
        currentElement  = true;
        currentValue = new StringBuilder();
        if(localName.equalsIgnoreCase("SalesOrganizations"))
        {
            vecSalesOrgDos = new Vector<SalesOrganizationDO>();
        }
        else if(localName.equalsIgnoreCase("SalesOrganizationDco"))
        {
            SalesOrgDo = new SalesOrganizationDO();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException
    {
        currentElement  = false;

        if(localName.equalsIgnoreCase("SalesOrgId"))
        {
            SalesOrgDo.SalesOrgId = StringUtils.getInt(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("Description"))
        {
            SalesOrgDo.Description = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("Code"))
        {
            SalesOrgDo.Code = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("isActive"))
        {
            SalesOrgDo.isActive = StringUtils.getBoolean(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("CurrencyCode"))
        {
            SalesOrgDo.CurrencyCode = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("IsFullSyncInWifi"))
        {
            SalesOrgDo.IsFullSyncInWifi = StringUtils.getBoolean(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("ERPInstance"))
        {
            SalesOrgDo.ERPInstance = (currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("IsVatEnabled"))
        {
            SalesOrgDo.IsVatEnabled = StringUtils.getBoolean(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("SalesOrganizationDco"))
        {
            vecSalesOrgDos.add(SalesOrgDo);
        }
        else if(localName.equalsIgnoreCase("SalesOrganizations"))
        {
            if(vecSalesOrgDos != null && vecSalesOrgDos.size() > 0)
                insert(vecSalesOrgDos);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException
    {
        if (currentElement)
            currentValue.append(new String(ch, start, length));
    }
    private void insert( Vector<SalesOrganizationDO> vecSalesOrgDos)
    {
        new TaxDa().insertSalesOrg(vecSalesOrgDos);
    }
}
