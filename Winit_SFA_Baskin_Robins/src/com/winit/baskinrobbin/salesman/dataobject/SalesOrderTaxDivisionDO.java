package com.winit.baskinrobbin.salesman.dataobject;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class SalesOrderTaxDivisionDO extends BaseObject {
    public String Id = "";
    public String UID = "";
    public String SalesOrderUID = "";
    public String SalesOrderLineUID = "";
    public String TaxUID = "";
    public String TaxSlabUID = "";
    public double TaxAmount = 0.0f;
    public String TaxName = "";
    public String ApplicableAt = "";
    public String DependentTaxUID = "";
    public String DependentTaxName = "";
    public String TaxCalculationType = "";
    public double BaseTaxRate = 0.0f;
    public double RangeStart = 0.0f;
    public double RangeEnd = 0.0f;
    public double TaxRate = 0.0f;
}
