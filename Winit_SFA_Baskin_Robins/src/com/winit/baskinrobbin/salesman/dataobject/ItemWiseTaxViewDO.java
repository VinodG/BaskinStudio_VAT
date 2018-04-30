package com.winit.baskinrobbin.salesman.dataobject;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class ItemWiseTaxViewDO extends BaseObject implements Cloneable {
    public String TaxGroupUID ;
    public String TaxUID ;
    public String TaxSlabUID ;
    public String TaxSKUMapUID ;
    public String TaxName ;
    public String ApplicableAt ;
    public String DependentTaxUID ;
    public String TaxCalculationType ;
    public float BaseTaxRate ;
    public float RangeStart ;
    public float RangeEnd ;
    public float TaxRate ;
    public String SKUUID ;
    public String TaxGroupName ;
    public String TaxGroupDescription ;
    public float TaxAmount ;


}