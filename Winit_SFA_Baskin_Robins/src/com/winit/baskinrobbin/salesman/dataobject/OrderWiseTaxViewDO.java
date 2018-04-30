package com.winit.baskinrobbin.salesman.dataobject;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class OrderWiseTaxViewDO  extends BaseObject {

    public String TaxGroupUID ;
    public String TaxUID ;
    public String TaxSlabUID ;
    public String TaxName ;
    public String ApplicableAt ;
    public String DependentTaxUID ;
    public String TaxCalculationType ;
    public float BaseTaxRate ;
    public float RangeStart ;
    public float RangeEnd ;
    public float TaxRate ;
    public float TaxAmount ;

    //Added for Reference
    public String SKUUID ;
    public OrderWiseTaxViewDO(OrderWiseTaxViewDO orderWiseTaxViewDO)
    {
        this.TaxGroupUID = orderWiseTaxViewDO.TaxGroupUID;
        this.TaxUID = orderWiseTaxViewDO.TaxUID;
        this.TaxSlabUID = orderWiseTaxViewDO.TaxSlabUID;
        this.TaxName = orderWiseTaxViewDO.TaxName;
        this.ApplicableAt = orderWiseTaxViewDO.ApplicableAt;
        this.DependentTaxUID = orderWiseTaxViewDO.DependentTaxUID;
        this.TaxCalculationType = orderWiseTaxViewDO.TaxCalculationType;
        this.BaseTaxRate = orderWiseTaxViewDO.BaseTaxRate;
        this.RangeStart = orderWiseTaxViewDO.RangeStart;
        this.RangeEnd = orderWiseTaxViewDO.RangeEnd;
        this.TaxRate = orderWiseTaxViewDO.TaxRate;
        this.TaxAmount = orderWiseTaxViewDO.TaxAmount;
        this.SKUUID = orderWiseTaxViewDO.SKUUID;
    }
    public OrderWiseTaxViewDO(){
    }
}
