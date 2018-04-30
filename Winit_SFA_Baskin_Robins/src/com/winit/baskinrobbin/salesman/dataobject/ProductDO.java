package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import android.widget.EditText;

@SuppressWarnings("serial")
public class ProductDO implements Serializable, Cloneable
{
	public String LineNo = "";
	public String OrderNo = "" ;
	public String RelatedLineId = "";
	public String ProductId = "";
	public String CategoryId ="" ;
	public String SKU = "";
	public String Description ="";
	public String Description1 ="";
	public int UnitsPerCases;
	public String BatchCode = "";
	public String UOM =""; 
	public String secondaryUOM =""; 
	public String primaryUOM =""; 
	public String CaseBarCode ="";
	public String UnitBarCode;
	public String ItemType;
	public String ItemSubType ="";
	public String IsStructural;
	public String PricingKey;
	public String brand;
	public String invoiceNo = "";
	public String cases = "";
	public String units = "";
	public String promotionId = "";
	public String promotionType = "";
	
	public String LOT_CONTROL_CODE = "";
	
	public String preCases = "0";
	public String preUnits = "0";
	
	public String lpoOrderedUnit = "0";
	//Not to modify
	public String ActpreUnits = "0";
	public float totalCases = 0;
	
	public String recomCases = "0.0";
	public float recomUnits = 0;
	public String strExpiryDate = "";
	
	public float deliveredCases;
	public float orderedCases;
	public float inventoryQty=0.0f;
	public int orderedUnits;
	
	public double itemPrice;
	public Double totalPrice;
	
	//added by awaneesh for promotion
	public float returnedCases;
	public int returnedUnits;
	public boolean isReccomended = false;
	public boolean isDiscountApplied = false, isRefreshed = false;
	public double Discount = 0;
	
	public int discountType =0;//0 == percentage and 1==amount
	
	//added by awaneesh for promotion
	public float actualDiscount = 0;
	public String reason ="";
	public String remarks = "";
	public String LotNumber = "";
	public boolean isMusthave;
	public boolean isSelected;
	public int musthaveQty;
	public float discountPerCentReturn;
	public boolean isReturnCategory;
	public double casePrice 		  = 0;
//	public double unitSellingPrice = 0;
	public double unitSellingPrice = 0;
	public Double invoiceAmount    = 0.0;
	public double invoiceAmountwithoutTax    = 0;//Added For Storing the invoice value without TAX
	public Double discountAmount   = 0.0;
	public Double discountAmountONEach   = 0.0;

	public String discountDesc   = "";
	
	public Double priceUsedLevel1  = 0.0;
	public Double priceUsedLevel2   = 0.0;
	
	public EditText etCases = null,etUnits = null, etEmptyJar=null;
	public boolean isAdvanceOrder;
	
	public boolean isPromotional = false;
	public float depositPrice;
	public float emptyCasePrice = 0;
	public double DiscountAmt = 0;
	public double DiscountAmtReturnBackup = 0;
	public int PromoLineNo;
	public String promoCode;
	public String TaxGroupCode = "0";
	public float TaxPercentage = 0;
//	public float relatedLineID = 0;
	//=========================
	public float discountPercent = 0;

	/**********************Added For VAT***********************************/
	public int totalUnits = 0;
	public String TrxReasonCode = "";
	public Double LineTaxAmount=0.0;
	public Double ProrataTaxAmount=0.0;
	public double TotalTax=0.0;
	public double  OriginalLineTaxAmount=0;
	public float  OriginalProrataTaxAmount=0;
	public float  OriginalTotalTax=0;

	public float QuantityInStock=0;
	public String RefTrxCode="";

	public boolean IsTaxApplicableFortheItem=false;

	public ArrayList<ItemWiseTaxViewDO> arrApplicableTaxonItems = new ArrayList<ItemWiseTaxViewDO>();
	public ArrayList<ItemWiseTaxViewDO> arrAppliedTaxes = new ArrayList<ItemWiseTaxViewDO>();
	/*********************************************************/

	public String OrderType = "";
	
	public String categoryName="";
	
	public ArrayList<String> vecDamageImages;
	
	public Vector<DamageImageDO> vecDamageImagesNew;
	public Vector<String> vecUOM = new Vector<String>();
	public boolean isDeleteOpen = true;
	//public int quantityBU;
	public float quantityBU;
	public ArrayList<DiscountDO> arrDiscList = new ArrayList<DiscountDO>();
	
    public static final String ITEM_UOM_LEVEL3="PCS";//bottom level UOM

	public static Object getItemUomLevel3() 
	{
		// TODO Auto-generated method stub
		return ITEM_UOM_LEVEL3;
	}
	@Override
	public Object clone() throws CloneNotSupportedException 
	{
		ProductDO clone = new ProductDO();
		
		clone.ProductId 	= 	this.ProductId;
		clone.SKU 			= 	this.SKU;
		clone.CategoryId 	=	this.CategoryId;
		clone.Description 	= 	this.Description;
		clone.UnitsPerCases	=	this.UnitsPerCases;
		clone.BatchCode 	= 	this.BatchCode;
		clone.UOM 			= 	this.UOM;
		clone.CaseBarCode 	= 	this.CaseBarCode;
		clone.UnitBarCode	= 	this.UnitBarCode;
		clone.ItemType		= 	this.ItemType;
		clone.PricingKey	= 	this.PricingKey;
		clone.brand			= 	this.brand;
		clone.cases 		= 	this.cases;
		clone.units 		= 	this.units;
		clone.preCases 		= 	this.preCases;
		clone.preUnits 		= 	this.preUnits;
		clone.orderedCases	= 	this.orderedCases;
		clone.orderedUnits	= 	this.orderedUnits;
		clone.itemPrice		= 	this.itemPrice;
		clone.returnedCases	= 	this.returnedCases;
		clone.returnedUnits	= 	this.returnedUnits;

		clone.recomCases	= 	this.recomCases;
		clone.recomUnits	= 	this.recomUnits;
		clone.reason 		= 	this.reason;
		clone.isMusthave	= 	this.isMusthave;
		clone.musthaveQty 	= 	this.musthaveQty;
		clone.categoryName  =   this.categoryName;
		clone.discountAmount=   this.discountAmount;
		clone.strExpiryDate =   this.strExpiryDate;
		clone.primaryUOM 	=   this.primaryUOM;
		clone.quantityBU 	=   this.quantityBU;
//=====================Added For VAT==================
		clone.totalUnits 	=   this.totalUnits;
		clone.TrxReasonCode = this.TrxReasonCode;
		clone.LineTaxAmount=this.LineTaxAmount;
		clone.ProrataTaxAmount=this.ProrataTaxAmount;
		clone.TotalTax=this.TotalTax;

		clone.OriginalLineTaxAmount=this.OriginalLineTaxAmount;
		clone.OriginalProrataTaxAmount=this.OriginalProrataTaxAmount;
		clone.OriginalTotalTax=this.OriginalTotalTax;

		clone.QuantityInStock=this.QuantityInStock;
		clone.RefTrxCode=this.RefTrxCode;
		clone.RelatedLineId=this.RelatedLineId;
		return clone;
	}
}
