package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.OrgDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class OrgParser extends BaseHandler
{
    private Vector<OrgDO> vecOrgdo ;
    private OrgDO orgdo;

    public OrgParser(Context context) {
        super(context);
    }
    @Override
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
    {
        currentElement  = true;
        currentValue = new StringBuilder();
        if(localName.equalsIgnoreCase("Orgs"))
        {
            vecOrgdo = new Vector<OrgDO>();
        }
        else if(localName.equalsIgnoreCase("OrgDco"))
        {
            orgdo = new OrgDO();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException
    {
        currentElement  = false;

        if(localName.equalsIgnoreCase("Id"))
        {
            orgdo.Id = StringUtils.getInt(currentValue.toString());
        }
        else if(localName.equalsIgnoreCase("UID"))
        {
            orgdo.UID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxUID"))
        {
            orgdo.TaxUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("Code"))
        {
            orgdo.Code = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("Name"))
        {
            orgdo.Name = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ParentUID"))
        {
            orgdo.ParentUID =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("CountryUID"))
        {
            orgdo.CountryUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("TaxGroupUID"))
        {
            orgdo.TaxGroupUID = currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("CreatedTime"))
        {
            orgdo.CreatedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("ModifiedTime"))
        {
            orgdo.ModifiedTime =currentValue.toString();
        }
        else if(localName.equalsIgnoreCase("OrgDco"))
        {
            vecOrgdo.add(orgdo);
        }
        else if(localName.equalsIgnoreCase("Orgs"))
        {
            if(vecOrgdo != null && vecOrgdo.size() > 0)
                insertDos(vecOrgdo);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException
    {
        if (currentElement)
            currentValue.append(new String(ch, start, length));
    }
    private void insertDos(Vector<OrgDO> vecOrgdo)
    {
        new TaxDa().insertOrg(vecOrgdo);
    }


}
