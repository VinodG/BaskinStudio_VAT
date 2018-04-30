package com.winit.baskinrobbin.salesman.dataobject;

public class PromotionBundledItemDO 
{
	public long PromotionBundleItemId;
	public long PromotionDetailId;
	public long OrderType;
	public String ItemCode;
	public String Ordered;
	public String OrderUOM;
	public String OfferItemCode;
	public long OfferType;
	public String Offered;
	public String OfferUOM;
}
//[PromotionDetailId] [int] NULL,
//[ItemCode] [varchar](50) NULL,
//[OrderType] [int] NULL,
//[Ordered] [float] NULL,
//[OrderUOM] [varchar](50) NULL,
//[OfferItemCode] [varchar](50) NULL,
//[OfferType] [int] NULL,
//[Offered] [float] NULL,
//[OfferUOM] [varchar](50) NULL,
//[ModifiedDate] [int] NULL,
//[ModifiedTime] [int] NULL)