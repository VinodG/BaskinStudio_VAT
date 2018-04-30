package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.OfflineDA;
import com.winit.baskinrobbin.salesman.common.OfflineDA.OfflineDataType;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSite_NewDO;
import com.winit.baskinrobbin.salesman.dataobject.DiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.ItemWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.TrxHeaderDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import static java.sql.Types.FLOAT;

public class OrderDA 
{
	DecimalFormat dff = new DecimalFormat("##.##");
	OfflineDA offlineDA=new OfflineDA();
	public String insertOrderDetails_Promo(OrderDO orderDO, Vector<ProductDO> vecOrderedProduct, ArrayList<ProductDO> vecOfferProducts, Preference preference)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.beginTransaction();
				if(orderDO.OrderId == null || orderDO.OrderId.length() <= 0)
				{
					orderId=offlineDA.getNextSequenceNumber(OfflineDataType.ORDER,objSqliteDB);
					preference.saveStringInPreference(Preference.ORDER_NO, orderId);
					preference.commitPreference();
					/*String query = "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 " +
							       "AND id NOT IN(SELECT OrderId FROM tblOrderHeader) " +
							       "AND id NOT IN(SELECT TrxCode FROM tblPaymentInvoice) " +
							       "Order By id Limit 1";
					cursor = objSqliteDB.rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						orderId = cursor.getString(0);
						preference.saveStringInPreference(Preference.ORDER_NO, orderId);
						preference.commitPreference();
					}
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();*/
				}
				else
				{
					orderId = orderDO.OrderId;
					preference.saveStringInPreference(Preference.ORDER_NO, orderId);
					preference.commitPreference();
				}
				
				if(orderId != null && !orderId.equalsIgnoreCase(""))
				{
					//objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
					offlineDA.updateSequenceNumberStatus(orderId,objSqliteDB);
						
					SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderHeader(OrderId,AppOrderId,JourneyCode," +
												 "VisitCode,EmpNo,SiteNo,OrderDate,OrderType,SubType,CurrencyCode,PaymentType,TotalAmount," +
												 "TrxReasonCode,CustomerSignature,SalesmanSignature,PaymentCode,LPOCode,DeliveryDate," +
												 "StampDate,StampImage,TRXStatus,Status, SiteName, TotalDiscountAmount, VehicleCode, " +
												 "DeliveredBy, TRANSACTION_TYPE_NAME, TRANSACTION_TYPE_KEY, Batch_Source_Name, " +
												 "Cust_Trx_Type_Name,RoundOffValue, LPOStatus, SourceVehicleCode" +
							",VatAmount,TotalAmountWithVat,ProrataTaxAmount,TotalTax,IsCustomerSigPushed,IsSalesmanSigPushed) VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					
					SQLiteStatement stmtUpdatePayment = objSqliteDB.compileStatement("UPDATE tblPaymentHeader SET Status = ? WHERE ReceiptId IN(SELECT ReceiptId FROM tblPaymentInvoice WHERE TrxCode = ?)");
					
					if(orderDO != null)
					{
						orderDO.OrderId = orderId;
						stmtInsert.bindString(1, orderDO.OrderId);
						stmtInsert.bindString(2, orderDO.strUUID);
						stmtInsert.bindString(3, orderDO.JourneyCode);
						stmtInsert.bindString(4, orderDO.VisitCode);
						stmtInsert.bindString(5, orderDO.empNo);
						stmtInsert.bindString(6, orderDO.CustomerSiteId);
						stmtInsert.bindString(7, orderDO.InvoiceDate);
						stmtInsert.bindString(8, orderDO.orderType);
						stmtInsert.bindString(9, orderDO.orderSubType);
						stmtInsert.bindString(10, orderDO.CurrencyCode);
						stmtInsert.bindString(11, orderDO.PaymentType);
						stmtInsert.bindString(12, ""+orderDO.TotalAmount);
						stmtInsert.bindString(13, orderDO.TrxReasonCode);
						stmtInsert.bindString(14, orderDO.strCustomerSign);
						stmtInsert.bindString(15, orderDO.strPresellerSign);
						stmtInsert.bindString(16, orderDO.PaymentCode);
						stmtInsert.bindString(17, orderDO.LPOCode);
						
						stmtInsert.bindString(18, orderDO.DeliveryDate);
						stmtInsert.bindString(19, orderDO.StampDate);
						stmtInsert.bindString(20, orderDO.StampImage);
						stmtInsert.bindString(21, orderDO.TRXStatus);
						stmtInsert.bindString(22, ""+orderDO.pushStatus);
						stmtInsert.bindString(23, ""+orderDO.strCustomerName);
						stmtInsert.bindString(24, ""+orderDO.Discount);
						stmtInsert.bindString(25, ""+orderDO.vehicleNo);
						stmtInsert.bindString(26, ""+orderDO.salesmanCode);
						
						stmtInsert.bindString(27, ""+orderDO.TRANSACTION_TYPE_VALUE);
						stmtInsert.bindString(28, ""+orderDO.TRANSACTION_TYPE_KEY);
						
						stmtInsert.bindString(29, ""+orderDO.Batch_Source_Name);
						stmtInsert.bindString(30, ""+orderDO.Trx_Type_Name);
						stmtInsert.bindString(31, ""+orderDO.roundOffVal);
						stmtInsert.bindString(32, ""+orderDO.LPOStatus);
						stmtInsert.bindString(33, ""+orderDO.SourceVehicleCode);

//						stmtInsert.bindString(34, ""+StringUtils.round(orderDO.VatAmount+"",2));
//						stmtInsert.bindString(35, ""+StringUtils.round(orderDO.TotalAmountWithVat+"",2));
//						stmtInsert.bindString(36, ""+StringUtils.round(orderDO.ProrataTaxAmount+"",2));
//						stmtInsert.bindString(37, ""+StringUtils.round(orderDO.TotalTax+"",2));

						stmtInsert.bindString(34, ""+/*StringUtils.round(*/orderDO.VatAmount+"");
						stmtInsert.bindString(35, ""+/*StringUtils.round(*/orderDO.TotalAmountWithVat+"");
						stmtInsert.bindString(36, ""+/*StringUtils.round(*/orderDO.ProrataTaxAmount+"");
						stmtInsert.bindString(37, ""+/*StringUtils.round(*/orderDO.TotalTax+"");
						stmtInsert.bindString(38, "false");
						stmtInsert.bindString(39, "false");
						SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblOrderDetail (LineNo, OrderNo, ItemCode," +
										" ItemType, ItemDescription, ItemPrice, UnitSellingPrice, UOM, Cases," +
								" Units, QuantityBU, DelivrdBU, PriceUsedLevel1, PriceUsedLevel2,TaxPercentage," +
								" TotalDiscountAmount, Status, PromoID, PromoType,TRXStatus, ExpiryDate, ReasonCode," +
								" RelatedLineID,TrxReasonCode, EmptyJarQT, EmptyJarDepositePrice, BatchNumber, DiscountDiscription" +
								",LineTaxAmount,ProrataTaxAmount,TotalTax,QuantityInStock) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						//"LineTaxAmount" FLOAT, "ProrataTaxAmount" FLOAT, "TotalTax" FLOAT)
						SQLiteStatement stmtDisc = objSqliteDB.compileStatement("INSERT INTO tblOrderDiscountDetail "
								+ "(DiscountLineNo,ItemLineNo,OrderNumber,ItemCode,UOM,DiscountAmount,Quantity,Description)" +
								" VALUES(?,?,?,?,?,?,?,?)");
						
						int count = 0;
						
						for(ProductDO orderDetailsDO : vecOrderedProduct)
						{
							double discount ;
							try 
							{
								discount = (new BigDecimal(orderDetailsDO.preUnits).multiply( new BigDecimal(orderDetailsDO.discountAmount+""))).doubleValue();
							} 
							catch (Exception e)
							{
								discount = orderDetailsDO.discountAmount;
							}
							
							
							if(orderDetailsDO.LineNo == null || orderDetailsDO.LineNo.length() <= 0)
								orderDetailsDO.LineNo = ""+(vecOrderedProduct.size()+count++);
							
							stmtInsertOrder.bindString(1, ""+orderDetailsDO.LineNo);
							stmtInsertOrder.bindString(2, orderDO.OrderId);
							stmtInsertOrder.bindString(3, orderDetailsDO.SKU);
							
							if(orderDetailsDO.isPromotional)
								stmtInsertOrder.bindString(4, "F");
							else
								stmtInsertOrder.bindString(4, "O");
							
							stmtInsertOrder.bindString(5, orderDetailsDO.Description);
							stmtInsertOrder.bindString(6, ""+orderDetailsDO.itemPrice);
							stmtInsertOrder.bindString(7, ""+orderDetailsDO.unitSellingPrice);
							stmtInsertOrder.bindString(8, ""+orderDetailsDO.UOM);
							stmtInsertOrder.bindString(9, ""+orderDetailsDO.preCases);
							stmtInsertOrder.bindString(10, ""+orderDetailsDO.preUnits);
							stmtInsertOrder.bindString(11, ""+orderDetailsDO.totalCases);
							stmtInsertOrder.bindString(12, ""+orderDetailsDO.totalCases);
							
							stmtInsertOrder.bindString(13, ""+(orderDetailsDO.invoiceAmount+discount));
							stmtInsertOrder.bindString(14, ""+orderDetailsDO.invoiceAmount);
							stmtInsertOrder.bindString(15, ""+orderDetailsDO.TaxPercentage);  // Added for line level TAX %
							stmtInsertOrder.bindString(16, ""+discount);
							
							
							stmtInsertOrder.bindString(17, ""+orderDO.pushStatus);
							stmtInsertOrder.bindString(18, ""+orderDetailsDO.promoCode);
							stmtInsertOrder.bindString(19, "");
							stmtInsertOrder.bindString(20, ""+orderDO.TRXStatus);
							
							stmtInsertOrder.bindString(21, ""+orderDetailsDO.strExpiryDate);
							stmtInsertOrder.bindString(22, ""+orderDetailsDO.reason);
							stmtInsertOrder.bindString(23, ""+orderDetailsDO.PromoLineNo);
							stmtInsertOrder.bindString(24, ""+orderDO.OrderId);
							stmtInsertOrder.bindString(25, ""+orderDetailsDO.recomUnits);
							stmtInsertOrder.bindString(26, ""+orderDetailsDO.depositPrice);
							stmtInsertOrder.bindString(27, ""+orderDetailsDO.BatchCode);
							stmtInsertOrder.bindString(28, ""+orderDetailsDO.discountDesc);

//							stmtInsertOrder.bindString(29, ""+StringUtils.round(orderDetailsDO.LineTaxAmount,2));
//							stmtInsertOrder.bindString(30, ""+StringUtils.round(orderDetailsDO.ProrataTaxAmount,2));
//							stmtInsertOrder.bindString(31, ""+StringUtils.round(orderDetailsDO.TotalTax,2));

							stmtInsertOrder.bindString(29, ""+/*StringUtils.round(*/orderDetailsDO.LineTaxAmount);
							stmtInsertOrder.bindString(30, ""+/*StringUtils.round(*/orderDetailsDO.ProrataTaxAmount);
							stmtInsertOrder.bindString(31, ""+/*StringUtils.round(*/orderDetailsDO.TotalTax);
							stmtInsertOrder.bindString(32, ""+orderDetailsDO.preUnits); //QuantityInstock
							int innerCount = 0;
							for(DiscountDO discountDO : orderDetailsDO.arrDiscList)
							{
								stmtDisc.bindString(1, ""+(++innerCount));
								stmtDisc.bindString(2, ""+orderDetailsDO.LineNo);
								stmtDisc.bindString(3, ""+orderDO.OrderId);
								stmtDisc.bindString(4, ""+orderDetailsDO.SKU);
								stmtDisc.bindString(5, ""+orderDetailsDO.UOM);
								
								stmtDisc.bindString(6, ""+discountDO.fDiscountAmt);
								stmtDisc.bindString(7, ""+orderDetailsDO.quantityBU);
								stmtDisc.bindString(8, ""+discountDO.description);
								stmtDisc.executeInsert();
							}
							
							stmtInsertOrder.executeInsert();
						}


						//ADDED TO INSER INTO TblSalesOrderTAX
						try{
							SQLiteStatement smtSalesOrderTax = objSqliteDB
									.compileStatement("INSERT INTO SalesOrderTax (Id, UID, SalesOrderUID, SalesOrderLineUID, "
											+ "TaxUID, TaxSlabUID,"
											+ "TaxAmount, TaxName,ApplicableAt,DependentTaxUID,DependentTaxName,TaxCalculationType,BaseTaxRate" +
											",RangeStart,RangeEnd,TaxRate) "
											+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

							String queryDeleteTaxItemsDetail = "delete from SalesOrderTax where SalesOrderUID='"
									+ orderDO.OrderId + "'";
							objSqliteDB.execSQL(queryDeleteTaxItemsDetail);

							if(orderDO.arrOrderwiseAppliedTax!=null && orderDO.arrOrderwiseAppliedTax.size()>0){
								{
									for (OrderWiseTaxViewDO appliedHeaderTaxDo : orderDO.arrOrderwiseAppliedTax) {
										smtSalesOrderTax.bindString(1, "");//trxHeaderDO.Id
										smtSalesOrderTax.bindString(2,""+orderDO.strUUID);//UID
										smtSalesOrderTax.bindString(3, ""+orderDO.OrderId);//SalesOrderUID
										smtSalesOrderTax.bindString(4,"0");//SalesOrderLineUID
										smtSalesOrderTax.bindString(5, ""+appliedHeaderTaxDo.TaxUID);
										smtSalesOrderTax.bindString(6, ""+appliedHeaderTaxDo.TaxSlabUID);
										smtSalesOrderTax.bindString(7, ""+ appliedHeaderTaxDo.TaxAmount);
										smtSalesOrderTax.bindString(8, ""+appliedHeaderTaxDo.TaxName);
										smtSalesOrderTax.bindString(9, ""+appliedHeaderTaxDo.ApplicableAt);
										smtSalesOrderTax.bindString(10, ""+appliedHeaderTaxDo.DependentTaxUID);
										smtSalesOrderTax.bindString(11, "");//appliedHeaderTaxDo.DependentTaxName
										smtSalesOrderTax.bindString(12, ""+appliedHeaderTaxDo.TaxCalculationType);
										smtSalesOrderTax.bindString(13, ""+appliedHeaderTaxDo.BaseTaxRate);
										smtSalesOrderTax.bindString(14, ""+appliedHeaderTaxDo.RangeStart);
										smtSalesOrderTax.bindString(15, ""+appliedHeaderTaxDo.RangeEnd);
										smtSalesOrderTax.bindString(16, ""+appliedHeaderTaxDo.TaxRate);
										smtSalesOrderTax.executeInsert();
									}

								}

							}
							if(vecOrderedProduct!=null && vecOrderedProduct.size()>0)
							{

								for(ProductDO trxDetailsDO:vecOrderedProduct){

									for (ItemWiseTaxViewDO appliedTaxDo : trxDetailsDO.arrAppliedTaxes) {
										smtSalesOrderTax.bindString(1, "");//trxHeaderDO.Id
										smtSalesOrderTax.bindString(2,""+orderDO.strUUID);//UID
										smtSalesOrderTax.bindString(3, ""+orderDO.OrderId);//SalesOrderUID
										smtSalesOrderTax.bindString(4,""+trxDetailsDO.LineNo);//SalesOrderLineUID
										smtSalesOrderTax.bindString(5, ""+appliedTaxDo.TaxUID);
										smtSalesOrderTax.bindString(6, ""+appliedTaxDo.TaxSlabUID);
										smtSalesOrderTax.bindString(7, ""+ StringUtils.round(""+appliedTaxDo.TaxAmount,2));
										smtSalesOrderTax.bindString(8, ""+appliedTaxDo.TaxName);
										smtSalesOrderTax.bindString(9, ""+appliedTaxDo.ApplicableAt);
										smtSalesOrderTax.bindString(10, ""+appliedTaxDo.DependentTaxUID);
										smtSalesOrderTax.bindString(11, "");//appliedTaxDo.DependentTaxName
										smtSalesOrderTax.bindString(12, ""+appliedTaxDo.TaxCalculationType);
										smtSalesOrderTax.bindString(13, ""+appliedTaxDo.BaseTaxRate);
										smtSalesOrderTax.bindString(14, ""+appliedTaxDo.RangeStart);
										smtSalesOrderTax.bindString(15, ""+appliedTaxDo.RangeEnd);
										smtSalesOrderTax.bindString(16, ""+appliedTaxDo.TaxRate);
										smtSalesOrderTax.executeInsert();
									}

								}
							}
							smtSalesOrderTax.close();
						}catch(Exception e){
							e.printStackTrace();
						}



						SQLiteStatement stmtOffers = objSqliteDB.compileStatement("INSERT INTO tblTrxPromotion (OrderId, ItemCode, DiscountAmount," +
								" DiscountPercentage, OrgCode, PromotionID, FactSheetCode, Status, CreatedOn," +
								" TrxStatus, PromotionType, TrxDetailsLineNo, ItemType, IsStructural)" +
								" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						
						for(ProductDO orderDetailsDO : vecOfferProducts)
						{
							stmtOffers.bindString(1, orderDO.OrderId);
							stmtOffers.bindString(2, orderDetailsDO.SKU);
							stmtOffers.bindString(3, ""+orderDetailsDO.discountAmount);
							stmtOffers.bindString(4, ""+orderDetailsDO.Discount);
							stmtOffers.bindString(5, "");
							
							stmtOffers.bindString(6, ""+orderDetailsDO.promotionId);
							stmtOffers.bindString(7, "");
							stmtOffers.bindString(8, ""+orderDO.pushStatus);
							stmtOffers.bindString(9, ""+CalendarUtils.getCurrentDateTime());
							stmtOffers.bindString(10, ""+orderDO.TRXStatus);
							stmtOffers.bindString(11, ""+orderDetailsDO.promotionType);
							
							stmtOffers.bindString(12, "1");
							stmtOffers.bindString(13, ""+orderDetailsDO.ItemType);
							stmtOffers.bindString(14, ""+orderDetailsDO.IsStructural);
							stmtOffers.executeInsert();
						}
						
						stmtInsert.executeInsert();
						stmtInsert.close();
						
						stmtUpdatePayment.bindString(1, "0");
						stmtUpdatePayment.bindString(2, orderDO.OrderId);
						stmtUpdatePayment.execute();
						
						stmtInsertOrder.close();
						stmtOffers.close();
						stmtUpdatePayment.close();
						stmtDisc.close();
						
					}
				}
				objSqliteDB.setTransactionSuccessful();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB != null && objSqliteDB.isOpen())
				{
					objSqliteDB.endTransaction();
					objSqliteDB.close();
				}
			}
			return orderId;
		}
	}
	
	public String insertOrderDetails_PromoNoOffer(OrderDO orderDO, Vector<ProductDO> vecOrderedProduct, Preference preference, String TYPE)
	{
		SQLiteDatabase objSqliteDB = null;
		String orderId = "";
		Cursor cursor = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			if(TYPE.equalsIgnoreCase(AppConstants.Order)){
				orderId=offlineDA.getNextSequenceNumber(OfflineDataType.ORDER,objSqliteDB);
			}
			else if(TYPE.equalsIgnoreCase(AppConstants.Return)){
				orderId=offlineDA.getNextSequenceNumber(OfflineDataType.RETURN,objSqliteDB);
			}
			
			preference.saveStringInPreference(Preference.ORDER_NO, orderId);
			preference.commitPreference();
			
			
			/*String query = "SELECT id from tblOfflineData where Type ='"+TYPE+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
			cursor = objSqliteDB.rawQuery(query, null);
			if(cursor.moveToFirst())
			{
				orderId = cursor.getString(0);
				preference.saveStringInPreference(Preference.ORDER_NO, orderId);
				preference.commitPreference();
			}
			
			if(cursor!=null && !cursor.isClosed())
				cursor.close();*/
			
			if(orderId != null && !orderId.equalsIgnoreCase(""))
			{
				//objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
//				offlineDA.updateSequenceNumberStatus(orderId,objSqliteDB);
					
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderHeader(OrderId,AppOrderId,JourneyCode," +
											 "VisitCode,EmpNo,SiteNo,OrderDate,OrderType,SubType,CurrencyCode,PaymentType,TotalAmount," +
											 "TrxReasonCode,CustomerSignature,SalesmanSignature,PaymentCode,LPOCode,DeliveryDate," +
											 "StampDate,StampImage,TRXStatus,Status,SiteName, TotalDiscountAmount, VehicleCode, DeliveredBy, " +
											 "TRANSACTION_TYPE_NAME, TRANSACTION_TYPE_KEY, Batch_Source_Name, Cust_Trx_Type_Name, RoundOffValue" +
						  				     ",VatAmount,TotalAmountWithVat,ProrataTaxAmount,TotalTax) VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				if(orderDO != null)
				{
					orderDO.OrderId = orderId;
					stmtInsert.bindString(1, orderDO.OrderId);
					stmtInsert.bindString(2, orderDO.strUUID);
					stmtInsert.bindString(3, orderDO.JourneyCode);
					stmtInsert.bindString(4, orderDO.VisitCode);
					stmtInsert.bindString(5, orderDO.empNo);
					stmtInsert.bindString(6, orderDO.CustomerSiteId);
					stmtInsert.bindString(7, orderDO.InvoiceDate);
					stmtInsert.bindString(8, orderDO.orderType);
					stmtInsert.bindString(9, orderDO.orderSubType);
					stmtInsert.bindString(10, orderDO.CurrencyCode);
					stmtInsert.bindString(11, orderDO.PaymentType);
					if(TYPE.equalsIgnoreCase(AppConstants.Return))
						stmtInsert.bindString(12, ""+-orderDO.TotalAmount);
					else
						stmtInsert.bindString(12, ""+orderDO.TotalAmount);
					stmtInsert.bindString(13, orderDO.TrxReasonCode);
					stmtInsert.bindString(14, orderDO.strCustomerSign);
					stmtInsert.bindString(15, orderDO.strPresellerSign);
					stmtInsert.bindString(16, orderDO.PaymentCode);
					stmtInsert.bindString(17, orderDO.LPOCode);
					
					stmtInsert.bindString(18, orderDO.DeliveryDate);
					stmtInsert.bindString(19, orderDO.StampDate);
					stmtInsert.bindString(20, orderDO.StampImage);
					stmtInsert.bindString(21, orderDO.TRXStatus);
					stmtInsert.bindString(22, ""+orderDO.pushStatus);
					stmtInsert.bindString(23, ""+orderDO.strCustomerName);
					stmtInsert.bindString(24, ""+orderDO.Discount);
					stmtInsert.bindString(25, ""+orderDO.vehicleNo);
					stmtInsert.bindString(26, ""+orderDO.salesmanCode);
					stmtInsert.bindString(27, ""+orderDO.TRANSACTION_TYPE_VALUE);
					stmtInsert.bindString(28, ""+orderDO.TRANSACTION_TYPE_KEY);
					
					stmtInsert.bindString(29, ""+orderDO.Batch_Source_Name);
					stmtInsert.bindString(30, ""+orderDO.Trx_Type_Name);
					stmtInsert.bindString(31, ""+orderDO.roundOffVal);

					stmtInsert.bindString(32, ""+orderDO.VatAmount);
					if(TYPE.equalsIgnoreCase(AppConstants.Return))
						stmtInsert.bindString(33, ""+-orderDO.TotalAmountWithVat);
					else
						stmtInsert.bindString(33, ""+orderDO.TotalAmountWithVat);
					stmtInsert.bindString(34, ""+orderDO.ProrataTaxAmount);
					stmtInsert.bindString(35, ""+orderDO.TotalTax);

					SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblOrderDetail (LineNo, OrderNo, ItemCode," +
									" ItemType, ItemDescription, ItemPrice, UnitSellingPrice, UOM, Cases," +
							" Units, QuantityBU, DelivrdBU, PriceUsedLevel1, PriceUsedLevel2,TaxPercentage," +
							" TotalDiscountAmount, Status, PromoID, PromoType,TRXStatus, ExpiryDate, ReasonCode," +
							" RelatedLineID,TrxReasonCode, EmptyJarQT, EmptyJarDepositePrice, BatchNumber, Remarks, LotNo, DiscountDiscription" +
							",LineTaxAmount,ProrataTaxAmount,TotalTax,QuantityInStock,RefTrxCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					
					SQLiteStatement stmtDisc = objSqliteDB.compileStatement("INSERT INTO tblOrderDiscountDetail "
							+ "(DiscountLineNo,ItemLineNo,OrderNumber,ItemCode,UOM,DiscountAmount,Quantity,Description)" +
							" VALUES(?,?,?,?,?,?,?,?)");
					
					SQLiteStatement stmtInsertOrderImage = objSqliteDB.compileStatement("INSERT INTO tblOrderImage (OrderNo, ItemCode, LineNo," +
							" ImagePath, CapturedDate) VALUES(?,?,?,?,?)");
					//"LineTaxAmount" FLOAT, "ProrataTaxAmount" FLOAT, "TotalTax" FLOAT)
					int count = 1;
					for(ProductDO orderDetailsDO : vecOrderedProduct)
					{
						if(orderDetailsDO.LineNo == null || orderDetailsDO.LineNo.length() <= 0)
							orderDetailsDO.LineNo = ""+(vecOrderedProduct.size()+count++);
						
						stmtInsertOrder.bindString(1, ""+orderDetailsDO.LineNo);
						stmtInsertOrder.bindString(2, orderDO.OrderId);
						stmtInsertOrder.bindString(3, orderDetailsDO.SKU);
						
						if(orderDetailsDO.isPromotional)
							stmtInsertOrder.bindString(4, "F");
						else
							stmtInsertOrder.bindString(4, "O");
						
						stmtInsertOrder.bindString(5, orderDetailsDO.Description);
						stmtInsertOrder.bindString(6, ""+orderDetailsDO.itemPrice);
						stmtInsertOrder.bindString(7, ""+orderDetailsDO.unitSellingPrice);
						stmtInsertOrder.bindString(8, ""+orderDetailsDO.UOM);
						stmtInsertOrder.bindString(9, ""+orderDetailsDO.preCases);
						stmtInsertOrder.bindString(10, ""+orderDetailsDO.preUnits);
						stmtInsertOrder.bindString(11, ""+orderDetailsDO.totalCases);
						stmtInsertOrder.bindString(12, ""+orderDetailsDO.totalCases);
						
						stmtInsertOrder.bindString(13, ""+(orderDetailsDO.invoiceAmount + orderDetailsDO.discountAmount));
						stmtInsertOrder.bindString(14, ""+orderDetailsDO.invoiceAmount);
						stmtInsertOrder.bindString(15, ""+orderDetailsDO.TaxPercentage);
						stmtInsertOrder.bindString(16, ""+orderDetailsDO.discountAmount);
						
						stmtInsertOrder.bindString(17, ""+orderDO.pushStatus);
						stmtInsertOrder.bindString(18, ""+orderDetailsDO.promoCode);
						stmtInsertOrder.bindString(19, ""+orderDetailsDO.Description1);
						stmtInsertOrder.bindString(20, ""+orderDO.TRXStatus);
						
						stmtInsertOrder.bindString(21, ""+orderDetailsDO.strExpiryDate);
						stmtInsertOrder.bindString(22, ""+orderDetailsDO.reason);
						stmtInsertOrder.bindString(23, ""+orderDetailsDO.RelatedLineId);
						stmtInsertOrder.bindString(24, ""+orderDO.OrderId);
						stmtInsertOrder.bindString(26, ""+orderDetailsDO.depositPrice);
						stmtInsertOrder.bindString(27, ""+orderDetailsDO.BatchCode);
						stmtInsertOrder.bindString(28, ""+orderDetailsDO.remarks);
						stmtInsertOrder.bindString(29, ""+orderDetailsDO.LotNumber);
						stmtInsertOrder.bindString(30, ""+orderDetailsDO.discountDesc);
						if(TYPE.equalsIgnoreCase(AppConstants.Return))
							stmtInsertOrder.bindString(31, ""+orderDetailsDO.OriginalLineTaxAmount);
						else
							stmtInsertOrder.bindString(31, ""+orderDetailsDO.LineTaxAmount);
						stmtInsertOrder.bindString(32, ""+orderDetailsDO.ProrataTaxAmount);
						stmtInsertOrder.bindString(33, ""+orderDetailsDO.TotalTax);
						stmtInsertOrder.bindString(34, ""+orderDetailsDO.QuantityInStock);
						stmtInsertOrder.bindString(35, ""+orderDetailsDO.RefTrxCode);

						int innerCount = 0;
						for(DiscountDO discountDO : orderDetailsDO.arrDiscList)
						{
							stmtDisc.bindString(1, ""+(++innerCount));
							stmtDisc.bindString(2, ""+orderDetailsDO.LineNo);
							stmtDisc.bindString(3, ""+orderDO.OrderId);
							stmtDisc.bindString(4, ""+orderDetailsDO.SKU);
							stmtDisc.bindString(5, ""+orderDetailsDO.UOM);
							
							stmtDisc.bindString(6, ""+discountDO.fDiscountAmt);
							stmtDisc.bindString(7, ""+orderDetailsDO.quantityBU);
							stmtDisc.bindString(8, ""+discountDO.description);
							stmtDisc.executeInsert();
						}
						
						stmtInsertOrder.executeInsert();
						
						if(orderDetailsDO.vecDamageImages != null && orderDetailsDO.vecDamageImages.size()>0)
						{
							int count1 = 1;
							for (String str : orderDetailsDO.vecDamageImages) {
								stmtInsertOrderImage.bindString(1, orderDO.OrderId);
								stmtInsertOrderImage.bindString(2, orderDetailsDO.SKU);
								stmtInsertOrderImage.bindString(3, (count1++)+"");
								stmtInsertOrderImage.bindString(4, str);
								stmtInsertOrderImage.bindString(5, CalendarUtils.getCurrentDateTime());
								stmtInsertOrderImage.executeInsert();
							}
						}
					}
					
					stmtInsert.executeInsert();
					stmtInsert.close();
					stmtInsertOrder.close();
					stmtDisc.close();
					offlineDA.updateSequenceNumberStatus(orderId,objSqliteDB);
				}
			}
			return orderId;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
			
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
	}

	public boolean updateOrderModifyByReturnByInvoice_(OrderDO orderDO, Vector<ProductDO> vecOrderedProduct, Preference preference, String TYPE)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				SQLiteStatement stmtUpdateOrder = null;
				SQLiteStatement stmtQuantityInstock = null;

				 if(TYPE.equalsIgnoreCase("Return"))
				{
					stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblOrderDetail SET QuantityInStock=? WHERE OrderNo = ? AND ItemCode=? AND LineNo = ?");
					String strQuantityInstock = "Select " +
							"QuantityInstock " +
							"from tblOrderDetail WHERE OrderNo = ? AND ItemCode=?  AND LineNo = ?";

				/*	SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblOrderDetail (LineNo, OrderNo, ItemCode," +
							" ItemType, ItemDescription, ItemPrice, UnitSellingPrice, UOM, Cases," +
							" Units, QuantityBU, DelivrdBU, PriceUsedLevel1, PriceUsedLevel2,TaxPercentage," +
							" TotalDiscountAmount, Status, PromoID, PromoType,TRXStatus, ExpiryDate, ReasonCode," +
							" RelatedLineID,TrxReasonCode, EmptyJarQT, EmptyJarDepositePrice, BatchNumber, Remarks, LotNo, DiscountDiscription" +
							",LineTaxAmount,ProrataTaxAmount,TotalTax) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");*/


					stmtQuantityInstock = objSqliteDB.compileStatement(strQuantityInstock);

					for (ProductDO trxDetailsDO : vecOrderedProduct) {

						stmtQuantityInstock.bindString(1, ""+trxDetailsDO.RefTrxCode);
						stmtQuantityInstock.bindString(2, trxDetailsDO.SKU);
						stmtQuantityInstock.bindString(3, ""+trxDetailsDO.RelatedLineId);

						long remQty = getValue(stmtQuantityInstock);

						stmtUpdateOrder.bindLong(1, remQty - StringUtils.getLong(trxDetailsDO.preUnits));
						stmtUpdateOrder.bindString(2, trxDetailsDO.RefTrxCode);
						stmtUpdateOrder.bindString(3, trxDetailsDO.SKU);
						stmtUpdateOrder.bindString(4, ""+trxDetailsDO.RelatedLineId);
						stmtUpdateOrder.execute();
					}

				}

				stmtQuantityInstock.close();
				stmtUpdateOrder.close();

				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();

					objSqliteDB.close();
			}
		}
	}

	private long getValue(SQLiteStatement stmtQuantityInstock)
	{
		long val = 0;
		try
		{
			val = stmtQuantityInstock.simpleQueryForLong();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return val;
	}
	public boolean updateHoldOrderStatus(String orderId)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET TRXStatus = ?, Status = '1' where OrderId = ?");
			stmtUpdate.bindString(1, "D");
			stmtUpdate.bindString(2, ""+orderId);
			stmtUpdate.execute();
			stmtUpdate.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		return true;
	}
	
	public boolean updateHoldOrderPushStatus(OrderDO orderDO, int status)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET Status = ?,TRXStatus=?,OrderDate=?,CustomerSignature = ?,SalesmanSignature=? where OrderId = ?");
			stmtUpdate.bindString(1, status+"");
			stmtUpdate.bindString(2, "D");
			stmtUpdate.bindString(3, ""+CalendarUtils.getCurrentDateTime());
			stmtUpdate.bindString(4, ""+orderDO.strCustomerSign);
			stmtUpdate.bindString(5, ""+orderDO.strPresellerSign);
			stmtUpdate.bindString(6, ""+orderDO.OrderId);
			stmtUpdate.execute();
			stmtUpdate.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		return true;
	}
	
	public boolean updateLPOPushStatus(OrderDO orderDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB  = DatabaseHelper.openDataBase();
			String query = "UPDATE tblOrderHeader SET LPOStatus = ?, TRXStatus=?, StampDate=?, CustomerSignature = ?, SalesmanSignature=?, DeliveredBy=? where OrderId = ?";
			SQLiteStatement stmtUpdate = objSqliteDB.compileStatement(query);
			stmtUpdate.bindString(1, orderDO.LPOStatus+"");
			stmtUpdate.bindString(2, ""+orderDO.TRXStatus);
			stmtUpdate.bindString(3, ""+orderDO.StampDate);
			stmtUpdate.bindString(4, ""+orderDO.strCustomerSign);
			stmtUpdate.bindString(5, ""+orderDO.strPresellerSign);
			stmtUpdate.bindString(6, ""+orderDO.salesmanCode);
			stmtUpdate.bindString(7, ""+orderDO.OrderId);
			stmtUpdate.execute();
			stmtUpdate.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		return true;
	}
	
	public boolean updateOrderModifyInventoryStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=? where ItemCode = ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity From tblVanStock where ItemCode = '"+productDO.SKU+"'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 		= cursor.getFloat(0);
							float totalQty 		= cursor.getFloat(1);
							
							availQty 			= availQty + StringUtils.getFloat(productDO.ActpreUnits);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+totalQty);
							stmtUpdateQty.bindString(3, ""+productDO.SKU);
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean updateInventoryStatusReturn(Vector<ProductDO> vecProductDO, String date, String userCode, String TAG_REASON, String vehicleCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				
				String querySal 				=   "INSERT INTO tblVanStock (VanStockId, UserCode, ItemCode, SellableQuantity, ReturnedQuantity, TotalQuantity, NonSellableQuantity, BatchNumber,ExpiryDate,UOM) VALUES(?,?,?,?,?,?,?,?,?,?)";
				String queryNonSal				=   "INSERT INTO tblNonSellableItems (NonSellableItemId, OrgCode, UserCode, ItemCode, ReceivedQty,UnloadedQty, BatchNumber, ExpiryDate, VehicleCode, UOM, Reason) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
				String updateSal 				=   "Update tblVanStock set TotalQuantity=?, NonSellableQuantity = ?, SellableQuantity = ? where ItemCode = ?";
				String updateNonSal				=   "Update tblNonSellableItems set ReceivedQty=? WHERE ItemCode = ? AND ExpiryDate = ? AND Reason= ?";
				
//				checkColumnAvail(objSqliteDB);
				
				SQLiteStatement stmtInsertS		= 	objSqliteDB.compileStatement(querySal);
				SQLiteStatement stmtUpdateS		= 	objSqliteDB.compileStatement(updateSal);
				SQLiteStatement stmtInsertNONS 	= 	objSqliteDB.compileStatement(queryNonSal);
				SQLiteStatement stmtUpdateNONS	= 	objSqliteDB.compileStatement(updateNonSal);
				
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						if(productDO.reason.equalsIgnoreCase(TAG_REASON))
							insertUpdateVanStock(objSqliteDB, stmtUpdateS, stmtInsertS, productDO, TAG_REASON, userCode);
						else
							insertUpdateNonSellableItems(objSqliteDB, stmtUpdateNONS, stmtInsertNONS, productDO, userCode, vehicleCode);
					}
				}
				
				stmtInsertS.close();
				stmtUpdateS.close();
				stmtInsertNONS.close();
				stmtUpdateNONS.close();
				
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	private void checkColumnAvail(SQLiteDatabase objSqliteDB)
	{
		try 
		{
			Cursor cursor 					= 	null;
			String checkColumn 				= 	"SELECT SQL FROM sqlite_master WHERE tbl_name = 'tblNonSellableItems' AND type = 'table'";
			
			cursor = objSqliteDB.rawQuery(checkColumn, null);
			
			if(cursor.moveToFirst())
			{
				String SQL = cursor.getString(0);
				if(!SQL.toLowerCase().contains("reason"))
				{
					SQLiteStatement stmtAlter	= 	objSqliteDB.compileStatement("ALTER TABLE tblNonSellableItems ADD COLUMN \"Reason\" VARCHAR");
					stmtAlter.execute();
				}
			}
			
			if(cursor != null && !cursor.isClosed())
				cursor.close();	
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void insertUpdateVanStock(SQLiteDatabase objSqliteDB, SQLiteStatement stmtUpdateQty, 
			SQLiteStatement stmtInsert, ProductDO productDO, String TAG_REASON, String userCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				String strQuery 	= 	"SELECT TotalQuantity,SellableQuantity, NonSellableQuantity From tblVanStock where ItemCode = '"+productDO.SKU+"'";
				Cursor cursor 		= 	objSqliteDB.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					float TotalQuantity 		= cursor.getFloat(0);
					float SellableQuantity 		= cursor.getFloat(1);
					float NonSellableQuantity 	= cursor.getFloat(2);
					
					if(productDO.reason.equalsIgnoreCase(TAG_REASON))
					{
						TotalQuantity    = TotalQuantity + productDO.quantityBU; 
						SellableQuantity = SellableQuantity + productDO.quantityBU;
					}
					else
						NonSellableQuantity = NonSellableQuantity + productDO.quantityBU;
					
					if(TotalQuantity > 0 )
						stmtUpdateQty.bindString(1, ""+TotalQuantity);
					else
						stmtUpdateQty.bindString(1, "0");
					
					
					if(NonSellableQuantity > 0 )
						stmtUpdateQty.bindString(2, ""+NonSellableQuantity);
					else
						stmtUpdateQty.bindString(2, "0");
					
					if(SellableQuantity > 0 )
						stmtUpdateQty.bindString(3, ""+SellableQuantity);
					else
						stmtUpdateQty.bindString(3, "0");
					
					stmtUpdateQty.bindString(4, ""+productDO.SKU);
					stmtUpdateQty.execute();
				}
				else
				{
					stmtInsert.bindString(1, ""+(getVanStockId(objSqliteDB)+1));
					stmtInsert.bindString(2, ""+userCode);
					stmtInsert.bindString(3, ""+productDO.SKU);
					
					if(productDO.reason.equalsIgnoreCase("Good Condition"))
						stmtInsert.bindString(4, productDO.quantityBU+"");
					else
						stmtInsert.bindString(4, 0+"");
					
					stmtInsert.bindString(5, "0");
					stmtInsert.bindString(6, ""+productDO.quantityBU);
					
					if(productDO.reason.equalsIgnoreCase("Good Condition"))
						stmtInsert.bindString(7, "0");
					else
						stmtInsert.bindString(7, ""+productDO.quantityBU);
					
					stmtInsert.bindString(8, ""+productDO.BatchCode);
					stmtInsert.bindString(9, ""+productDO.strExpiryDate);
					stmtInsert.bindString(10,""+productDO.UOM);
					stmtInsert.executeInsert();
				}
				if(cursor != null && !cursor.isClosed())
					cursor.close();		
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	public boolean updateInventoryStatusReplacement(Vector<ProductDO> vecProductDO, String date, String userCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 					= 	null;
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblVanStock (VanStockId, UserCode, ItemCode, SellableQuantity, ReturnedQuantity, TotalQuantity,BatchNumber,ExpiryDate,UOM) VALUES(?,?,?,?,?,?,?,?,?)");
				String strUpdate 				= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=?, ReturnedQuantity=?, NonSellableQuantity = ? where ItemCode = ?";
				
				
				SQLiteStatement stmtUpdateQty	= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						updateInventoryReceipt(objSqliteDB, cursor, productDO, stmtUpdateQty, stmtInsert, userCode);
						updateInventoryIssue(objSqliteDB, cursor, productDO, stmtUpdateQty, stmtInsert, userCode);
					}
				}
				stmtUpdateQty.close();
				stmtInsert.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	private void updateInventoryReceipt(SQLiteDatabase objSqliteDB, Cursor cursor, ProductDO productDO, 
			SQLiteStatement stmtUpdateQty, SQLiteStatement stmtInsert, String userCode) 
	{
		String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity, ReturnedQuantity, NonSellableQuantity From tblVanStock where ItemCode = '"+productDO.SKU+"'";
		cursor 				=   objSqliteDB.rawQuery(strQuery, null);
		
		if(cursor.moveToFirst())
		{
			float availQty 		      = cursor.getFloat(0);
			float totalQty 		      = cursor.getFloat(1);
			float returnQty			  = cursor.getFloat(2);
			float NonSellableQuantity = cursor.getFloat(3);
			
			availQty 				  = availQty  + (productDO.quantityBU);
			totalQty				  = totalQty  + (productDO.quantityBU);
			returnQty		          = returnQty + (productDO.quantityBU);
			
			if(availQty > 0 )
				stmtUpdateQty.bindString(1, ""+availQty);
			else
				stmtUpdateQty.bindString(1, "0");
			
			stmtUpdateQty.bindString(2, ""+totalQty);
			stmtUpdateQty.bindString(3, ""+returnQty);
			stmtUpdateQty.bindString(4, ""+NonSellableQuantity);
			stmtUpdateQty.bindString(5, ""+productDO.SKU);
			stmtUpdateQty.execute();
		}
		else
		{
			stmtInsert.bindString(1, ""+(getVanStockId(objSqliteDB)+1));
			stmtInsert.bindString(2, ""+userCode);
			stmtInsert.bindString(3, ""+productDO.SKU);
			stmtInsert.bindString(4, ""+(productDO.quantityBU));
			stmtInsert.bindString(5, "0");
			stmtInsert.bindString(6, ""+(productDO.quantityBU));
			stmtInsert.bindString(7, ""+productDO.BatchCode);
			stmtInsert.bindString(8, ""+productDO.strExpiryDate);
			stmtInsert.bindString(9, ""+productDO.UOM);
			stmtInsert.executeInsert();
		}
		if(cursor != null && !cursor.isClosed())
			cursor.close();		
	}

	private void updateInventoryIssue(SQLiteDatabase objSqliteDB, Cursor cursor, ProductDO productDO, 
			SQLiteStatement stmtUpdateQty, SQLiteStatement stmtInsert, String userCode) 
	{
		String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity, ReturnedQuantity, NonSellableQuantity From tblVanStock where ItemCode = '"+productDO.RelatedLineId+"'";
		cursor 	= objSqliteDB.rawQuery(strQuery, null);
		if(cursor.moveToFirst())
		{
			float availQty 		      = cursor.getFloat(0);
			float totalQty 		      = cursor.getFloat(1);
			float returnQty			  = cursor.getFloat(2);
			float NonSellableQuantity = cursor.getFloat(3);
			
			returnQty		= returnQty + (productDO.quantityBU);
			availQty 		= availQty  - (productDO.quantityBU);
			
			if(availQty > 0 )
				stmtUpdateQty.bindString(1, ""+availQty);
			else
				stmtUpdateQty.bindString(1, "0");
			
			stmtUpdateQty.bindString(2, ""+totalQty);
			stmtUpdateQty.bindString(3, ""+returnQty);
			stmtUpdateQty.bindString(4, ""+NonSellableQuantity);
			stmtUpdateQty.bindString(5, ""+productDO.RelatedLineId);
			stmtUpdateQty.execute();
		}
		else
		{
			stmtInsert.bindString(1, ""+(getVanStockId(objSqliteDB)+1));
			stmtInsert.bindString(2, ""+userCode);
			stmtInsert.bindString(3, ""+productDO.RelatedLineId);
			stmtInsert.bindString(4, ""+(productDO.quantityBU));
			stmtInsert.bindString(5, "0");
			stmtInsert.bindString(6, ""+(productDO.quantityBU));
			stmtInsert.bindString(7, ""+productDO.BatchCode);
			stmtInsert.bindString(8, ""+productDO.strExpiryDate);
			stmtInsert.bindString(9, ""+productDO.UOM);
			stmtInsert.bindString(10, ""+0);
			stmtInsert.executeInsert();
		}
		if(cursor != null && !cursor.isClosed())
			cursor.close();		
	}
	
	private int getVanStockId(SQLiteDatabase objSqliteDB)
	{
		int id = 0;
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				Cursor cursor = null;
				if (objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();

				String strQuery = "SELECT MAX(VanStockId) FROM tblVanStock";
				cursor = objSqliteDB.rawQuery(strQuery, null);
				if (cursor.moveToFirst()) {
					id = cursor.getInt(0);
				}

				if (cursor != null && !cursor.isClosed())
					cursor.close();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return id;
		
	}
	public boolean updateInventoryStatus_Old(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=? where ItemCode = ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity From tblVanStock where ItemCode = '"+productDO.SKU+"'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 		= cursor.getFloat(0);
							float totalQty 		= cursor.getFloat(1);
							if(productDO.reason.equalsIgnoreCase("Good Condition"))
							{
//								availQty 		= availQty + productDO.totalCases;
//								totalQty		= totalQty + productDO.totalCases;
								
								availQty 		= availQty + StringUtils.getFloat(productDO.preUnits);
								totalQty		= totalQty + StringUtils.getFloat(productDO.preUnits);
							}
							else
							{
								availQty 		= availQty - StringUtils.getFloat(productDO.preUnits);
							}
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+totalQty);
							stmtUpdateQty.bindString(3, ""+productDO.SKU);
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean updateInventoryStatus_New(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVanStock set SellableQuantity=?, TotalQuantity=?, LPOQuantity=? where ItemCode = ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT SellableQuantity, TotalQuantity, LPOQuantity From tblVanStock where ItemCode = '"+productDO.SKU+"'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 		= cursor.getFloat(0);
							float totalQty 		= cursor.getFloat(1);
							float LPOQty 		= cursor.getFloat(2);
							float orderQtyTemp=productDO.quantityBU;
							if(orderQtyTemp==0)
								orderQtyTemp=StringUtils.getFloat(productDO.preUnits);
							availQty 			= availQty - (orderQtyTemp);
//							availQty 			= availQty - (productDO.quantityBU); priviously used  now it iscommented as sytock is not deducted in onhold order

							if(productDO.OrderType.equalsIgnoreCase(AppConstants.LPO_ORDER))
								LPOQty = LPOQty -(orderQtyTemp);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+totalQty);
							stmtUpdateQty.bindString(3, ""+LPOQty);
							stmtUpdateQty.bindString(4, ""+productDO.SKU);
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean updateInventoryInStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVMSalesmanInventory  set availQty=? where ItemCode = ? AND  Date like ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT availQty From  tblVMSalesmanInventory where ItemCode = '"+productDO.SKU+"' AND Date LIKE '"+date+"%'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 	= StringUtils.getFloat(cursor.getString(0));
							availQty 		= availQty + StringUtils.getFloat(productDO.preCases);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+productDO.SKU);
							stmtUpdateQty.bindString(3, date+"%");
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	public boolean updateInventoryOutStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVMSalesmanInventory  set availQty=? where ItemCode = ? AND  Date like ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT availQty From  tblVMSalesmanInventory where ItemCode = '"+productDO.SKU+"' AND Date LIKE '"+date+"%'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 	= StringUtils.getFloat(cursor.getString(0));
							availQty 		= availQty - StringUtils.getFloat(productDO.preCases);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+productDO.SKU);
							stmtUpdateQty.bindString(3, date+"%");
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	public boolean InsertupdateInventoryStatus(Vector<ProductDO> vecProductDO, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblVMSalesmanInventory  set availQty=? where ItemCode = ? AND  Date like ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(ProductDO productDO : vecProductDO)
					{
						String strQuery 	= 	"SELECT availQty From  tblVMSalesmanInventory where ItemCode = '"+productDO.SKU+"' AND Date LIKE '"+date+"%'";
						cursor 	= objSqliteDB.rawQuery(strQuery, null);
						if(cursor.moveToFirst())
						{
							float availQty 	= StringUtils.getFloat(cursor.getString(0));
							availQty 		= availQty + StringUtils.getFloat(productDO.preCases);
							
							if(availQty > 0 )
								stmtUpdateQty.bindString(1, ""+availQty);
							else
								stmtUpdateQty.bindString(1, "0");
							
							stmtUpdateQty.bindString(2, ""+productDO.SKU);
							stmtUpdateQty.bindString(3, date+"%");
							stmtUpdateQty.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	//Method to get the orders generated current day for the current customer  
	public Vector<OrderDO> getCustomerOrderList(String customerSiteID, String currentDate) 
	{
		synchronized(MyApplication.MyLock) 
		{
			OrderDO objCustomerOrder;
			Vector<OrderDO> vecOrderList  =  new Vector<OrderDO>();
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "SELECT OrderId,AppOrderId,EmpNo,SiteNo,OrderDate,OrderType,SubType,TrxReasonCode," +
								  "DeliveryDate,TRXStatus,Status, Batch_Source_Name, Cust_Trx_Type_Name FROM tblOrderHeader where SiteNo ='"+customerSiteID+"' " +
								  "and (OrderDate like '"+currentDate+"%' OR DeliveryDate LIKE '"+CalendarUtils.getOrderPostDate()+"%')";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						objCustomerOrder 				= 	new OrderDO();
						objCustomerOrder.OrderId		=	cursor.getString(0);
						objCustomerOrder.strUUID 		=	cursor.getString(1);
						objCustomerOrder.empNo			=	cursor.getString(2);
						objCustomerOrder.CustomerSiteId =	cursor.getString(3);
						objCustomerOrder.InvoiceDate 	=	cursor.getString(4);
						objCustomerOrder.orderType		=	cursor.getString(5);
						
						objCustomerOrder.orderSubType 	=	cursor.getString(6);
						
						objCustomerOrder.TrxReasonCode 	=	cursor.getString(7);
						
						objCustomerOrder.DeliveryDate 	=	cursor.getString(8);
						objCustomerOrder.TRXStatus		=	cursor.getString(9);
						objCustomerOrder.pushStatus		=	cursor.getInt(10);
						
						objCustomerOrder.Batch_Source_Name	= 	cursor.getString(11);
						objCustomerOrder.Trx_Type_Name	= 	cursor.getString(12);
						vecOrderList.add(objCustomerOrder);
					}
					while(cursor.moveToNext());
				}
				

				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
//				strQuery = "SELECT ReceiptId,AppPaymentId,EmpNo,SiteID,PaymentDate,PaymentType FROM tblPaymentHeader where SiteID =='"+customerSiteID+"'" +
//						   " and PaymentDate like '"+CalendarUtils.getOrderPostDate()+"%'";
//				
//				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
//				
//				if(cursor.moveToFirst())
//				{
//					do
//					{
//						objCustomerOrder 				= 	new OrderDO();
//						objCustomerOrder.OrderId		=	cursor.getString(0);
//						objCustomerOrder.strUUID 		=	cursor.getString(1);
//						objCustomerOrder.empNo			=	cursor.getString(2);
//						objCustomerOrder.CustomerSiteId =	cursor.getString(3);
//						objCustomerOrder.InvoiceDate 	=	cursor.getString(4);
//						objCustomerOrder.orderType		=	cursor.getString(5);
//						objCustomerOrder.orderSubType 	=	AppConstants.Receipt;
//						vecOrderList.add(objCustomerOrder);
//					}
//					while(cursor.moveToNext());
//				}

//				if(cursor!=null && !cursor.isClosed())
//					cursor.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return vecOrderList;
		}
	}

	
	//Method to get the orders generated current day for the current customer  
	public Vector<OrderDO> getCustomerAdvanceOrderList(String customerSiteID, String currentDate, String orderType) 
	{
		synchronized(MyApplication.MyLock) 
		{
			OrderDO objCustomerOrder;
			Vector<OrderDO> vecOrderList  =  new Vector<OrderDO>();
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "SELECT * FROM tblOrderHeader where SiteNo ='"+customerSiteID+"' and DeliveryDate like '"+currentDate+"%' AND SubType='"+orderType+"'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						objCustomerOrder 				= 	new OrderDO();
						objCustomerOrder.OrderId		=	cursor.getString(0);
						objCustomerOrder.PresellerId 	=	cursor.getString(1);
						objCustomerOrder.CustomerSiteId =	cursor.getString(2);
						objCustomerOrder.Discount		=	cursor.getDouble(3);
						objCustomerOrder.TotalAmount 	=	cursor.getDouble(4);
						objCustomerOrder.DeliveryStatus =	cursor.getString(5);
						objCustomerOrder.DeliveryDate	=	cursor.getString(6);
						objCustomerOrder.BalanceAmount 	=	cursor.getLong(7);
						objCustomerOrder.InvoiceNumber 	=	cursor.getString(8);
						objCustomerOrder.InvoiceDate	=	cursor.getString(9);
						objCustomerOrder.DeliveryAgentId=	cursor.getString(10);
						objCustomerOrder.orderType		=	cursor.getString(11);
						objCustomerOrder.strCustomerSign=	cursor.getString(12);
						objCustomerOrder.strPresellerSign=	cursor.getString(13);
						objCustomerOrder.strUUID 		=	cursor.getString(14);
						vecOrderList.add(objCustomerOrder);
					}
					while(cursor.moveToNext());
					
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return vecOrderList;
		}
	}


	public int getAdvenceOrderBySiteId(String orderType, String siteId, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "SELECT COUNT (*) FROM tblOrderHeader where SubType='"+orderType+"' AND SiteNo='"+siteId+"' AND DeliveryDate LIKE '"+date+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	

	public boolean holdOrderPushStatus(String siteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			boolean pushStatus = false;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "SELECT Status FROM tblOrderHeader where OrderId='"+siteId+"'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					if(status == 1)
						pushStatus = true;
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return pushStatus;
		}
	}

	
	public int getProductiveOrders()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				// Old
//				String strQuery = "select count(*) from ( "+
//						"select DISTINCT toh.SiteNo from tblOrderHeader toh where TRXStatus = 'D'"+
//						"union "+
//						"select DISTINCT tph.SiteId from tblPaymentHeader tph "+
//						") temp";
				String strQuery = "select count(DISTINCT toh.SiteNo) from tblOrderHeader toh " +
						"where TRXStatus = 'D' AND OrderDate  like '"+CalendarUtils.getCurrentDateAsString()+"%'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return status;
		}
	}
	
	public boolean insertAdvanceOrderDetails(Vector<CustomerSite_NewDO> vecSalesManCustomerDetailDOs)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblOrderHeader(OrderId,AppOrderId,JourneyCode," +
					 "VisitCode,EmpNo,SiteNo,OrderDate,OrderType,SubType,CurrencyCode,PaymentType,TotalAmount," +
					 "TrxReasonCode,CustomerSignature,SalesmanSignature,PaymentCode,LPOCode,DeliveryDate," +
					 "StampDate,StampImage,TRXStatus,Status, TRANSACTION_TYPE_NAME, TRANSACTION_TYPE_KEY, Batch_Source_Name, Cust_Trx_Type_Name, RoundOffValue, LPOStatus" +
					",VatAmount,TotalAmountWithVat,ProrataTaxAmount,TotalTax)" +
					 " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblOrderDetail (LineNo, OrderNo, ItemCode," +
					" ItemType, ItemDescription, ItemPrice, UnitSellingPrice, UOM, Cases," +
			" Units, QuantityBU, DelivrdBU, PriceUsedLevel1, PriceUsedLevel2,TaxPercentage," +
			" TotalDiscountAmount, Status, PromoID, PromoType,TRXStatus, ExpiryDate, ReasonCode," +
			" RelatedLineID,TrxReasonCode,BatchNumber, Remarks, LotNo, DiscountDiscription" +
					",LineTaxAmount,ProrataTaxAmount,TotalTax, QuantityInStock,RefTrxCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			//"LineTaxAmount" FLOAT, "ProrataTaxAmount" FLOAT, "TotalTax" FLOAT)
			SQLiteStatement stmtSelOrder = objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblOrderHeader WHERE OrderId=?");
			
			
			if(vecSalesManCustomerDetailDOs != null && vecSalesManCustomerDetailDOs.size() > 0)
			{
				for(CustomerSite_NewDO coCustomerSite_NewDO : vecSalesManCustomerDetailDOs)
				{
					if(coCustomerSite_NewDO != null && coCustomerSite_NewDO.vecOrderDO != null && coCustomerSite_NewDO.vecOrderDO.size() > 0)
					{
						for(OrderDO orderDO : coCustomerSite_NewDO.vecOrderDO)
						{
							stmtSelOrder.bindString(1, orderDO.OrderId);
							long count = stmtSelOrder.simpleQueryForLong();
							if(count <= 0)
								insertAdvanceOrderDetails(objSqliteDB, orderDO, stmtInsert, stmtInsertOrder);
							else
							{
								if(orderDO.orderSubType.equalsIgnoreCase(AppConstants.LPO_ORDER))
								{
									objSqliteDB.execSQL("DELETE FROM tblOrderHeader WHERE OrderId = '"+orderDO.OrderId+"'");
									objSqliteDB.execSQL("DELETE FROM tblOrderDetail WHERE OrderNo = '"+orderDO.OrderId+"'");
									objSqliteDB.execSQL("DELETE FROM tblOrderDiscountDetail WHERE OrderNumber = '"+orderDO.OrderId+"'");
									
									insertAdvanceOrderDetails(objSqliteDB, orderDO, stmtInsert, stmtInsertOrder);
								}
							}
						}
					}
				}
			}
			
			stmtInsert.close();
			stmtInsertOrder.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		return true;
	}
	
	public void insertAdvanceOrderDetails(SQLiteDatabase objSqliteDB, OrderDO orderDO, SQLiteStatement stmtInsert, SQLiteStatement stmtInsertOrder)
	{
		try 
		{
			if(objSqliteDB == null || !objSqliteDB.isOpen())
				objSqliteDB = DatabaseHelper.openDataBase();
			
			if(orderDO != null && stmtInsert != null && stmtInsertOrder != null)
			{
				stmtInsert.bindString(1, orderDO.OrderId);
				stmtInsert.bindString(2, orderDO.strUUID);
				stmtInsert.bindString(3, orderDO.JourneyCode);
				stmtInsert.bindString(4, orderDO.VisitCode);
				stmtInsert.bindString(5, orderDO.empNo);
				stmtInsert.bindString(6, orderDO.CustomerSiteId);
				stmtInsert.bindString(7, orderDO.InvoiceDate);
				stmtInsert.bindString(8, orderDO.orderType);
				stmtInsert.bindString(9, orderDO.orderSubType);
				stmtInsert.bindString(10, orderDO.CurrencyCode);
				stmtInsert.bindString(11, orderDO.PaymentType);
				stmtInsert.bindString(12, ""+orderDO.TotalAmount);
				stmtInsert.bindString(13, orderDO.TrxReasonCode);
				stmtInsert.bindString(14, orderDO.strCustomerSign);
				stmtInsert.bindString(15, orderDO.strPresellerSign);
				stmtInsert.bindString(16, orderDO.PaymentCode);
				stmtInsert.bindString(17, orderDO.LPOCode);
				
				stmtInsert.bindString(18, orderDO.DeliveryDate);
				stmtInsert.bindString(19, orderDO.StampDate);
				stmtInsert.bindString(20, orderDO.StampImage);
				stmtInsert.bindString(21, orderDO.TRXStatus);
				stmtInsert.bindString(22, ""+orderDO.pushStatus);
				
				stmtInsert.bindString(23, ""+orderDO.TRANSACTION_TYPE_VALUE);
				stmtInsert.bindString(24, ""+orderDO.TRANSACTION_TYPE_KEY);
				stmtInsert.bindString(25, ""+orderDO.Batch_Source_Name);
				stmtInsert.bindString(26, ""+orderDO.Trx_Type_Name);
				stmtInsert.bindString(27, ""+orderDO.strRoundOffVal);
				stmtInsert.bindString(28, ""+orderDO.LPOStatus);
//VatAmount,TotalAmountWithVat,ProrataTaxAmount
				stmtInsert.bindString(29, ""+orderDO.VatAmount);
				stmtInsert.bindString(30, ""+orderDO.TotalAmountWithVat);
				stmtInsert.bindString(31, ""+orderDO.ProrataTaxAmount);
				stmtInsert.bindString(32, ""+orderDO.TotalTax);
				for(ProductDO orderDetailsDO : orderDO.vecProductDO)
				{
					stmtInsertOrder.bindString(1, orderDetailsDO.LineNo);
					stmtInsertOrder.bindString(2, orderDO.OrderId);
					stmtInsertOrder.bindString(3, orderDetailsDO.SKU);
					stmtInsertOrder.bindString(4, orderDetailsDO.ItemType);
					stmtInsertOrder.bindString(5, orderDetailsDO.Description);
					stmtInsertOrder.bindString(6, ""+orderDetailsDO.itemPrice);
					stmtInsertOrder.bindString(7, ""+orderDetailsDO.unitSellingPrice);
					stmtInsertOrder.bindString(8, ""+orderDetailsDO.UOM);
					stmtInsertOrder.bindString(9, ""+orderDetailsDO.preCases);
					stmtInsertOrder.bindString(10, ""+orderDetailsDO.preUnits);
					stmtInsertOrder.bindString(11, ""+orderDetailsDO.totalCases);
					stmtInsertOrder.bindString(12, ""+orderDetailsDO.totalCases);
					
					stmtInsertOrder.bindString(13, ""+orderDetailsDO.totalPrice);
					stmtInsertOrder.bindString(14, ""+orderDetailsDO.invoiceAmount);
					stmtInsertOrder.bindString(15, ""+orderDetailsDO.TaxPercentage);
					stmtInsertOrder.bindString(16, ""+orderDetailsDO.discountAmount);
					
					stmtInsertOrder.bindString(17, ""+orderDO.pushStatus);
					stmtInsertOrder.bindString(18, "");
					stmtInsertOrder.bindString(19, "");
					stmtInsertOrder.bindString(20, ""+orderDO.TRXStatus);
					
					stmtInsertOrder.bindString(21, ""+orderDetailsDO.strExpiryDate);
					stmtInsertOrder.bindString(22, ""+orderDetailsDO.reason);
					stmtInsertOrder.bindString(23, ""+orderDetailsDO.RelatedLineId);
					stmtInsertOrder.bindString(24, ""+orderDO.OrderId);
					stmtInsertOrder.bindString(25, ""+orderDetailsDO.BatchCode);
					stmtInsertOrder.bindString(26, ""+orderDetailsDO.remarks);
					stmtInsertOrder.bindString(27, ""+orderDetailsDO.LotNumber);
					stmtInsertOrder.bindString(28, ""+orderDetailsDO.discountDesc);
//============================Added For VAT
					stmtInsertOrder.bindString(29, ""+orderDetailsDO.LineTaxAmount);
					stmtInsertOrder.bindString(30, ""+orderDetailsDO.ProrataTaxAmount);
					stmtInsertOrder.bindString(31, ""+orderDetailsDO.TotalTax);

					stmtInsertOrder.bindString(32, ""+orderDetailsDO.QuantityInStock);
					stmtInsertOrder.bindString(33, ""+orderDetailsDO.RefTrxCode);
//					"LineTaxAmount" FLOAT, "ProrataTaxAmount" FLOAT, "TotalTax" FLOAT)
								stmtInsertOrder.executeInsert();
				}
				stmtInsert.executeInsert();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public boolean updateStampImage(OrderDO orderDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtInsert = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET StampImage=? WHERE OrderId = ?");
			if(orderDO != null)
			{
				stmtInsert.bindString(1, orderDO.StampImage);
				stmtInsert.bindString(2, ""+orderDO.OrderId);
				stmtInsert.execute();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		
		return true;
	}
	
	public boolean updateInventoryInStatus(Vector<ProductDO> vecMainProducts, String empNo, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = 	null;
			Cursor cursor 			   = 	null;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblCheckINDemandStockInventory WHERE Date LIKE '%"+date+"%' AND EmpNo=? AND ItemCode = ?");
				String strUpdate 				= 	"Update tblCheckINDemandStockInventory set PrimaryQuantity=?, SecondaryQuantity=?, advcCases=?,advcUnits=?, pushStatus = 0 WHERE Date LIKE '%"+date+"%' AND EmpNo=? AND ItemCode = ?";
				
				SQLiteStatement stmtUpdateQty	= 	objSqliteDB.compileStatement(strUpdate);
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblCheckINDemandStockInventory " +
						                                                             "(Date,EmpNo,ItemCode,PrimaryQuantity,SecondaryQuantity,pushStatus,AdvcCases, AdvcUnits) VALUES(?,?,?,?,?,?,?,?)");
				
				if(vecMainProducts != null && vecMainProducts.size() > 0)
				{
					for(ProductDO productDO : vecMainProducts)
					{
						stmtSelectRec.bindString(1, empNo);
						stmtSelectRec.bindString(2, productDO.SKU);
						long count = stmtSelectRec.simpleQueryForLong();
						
						if(count > 0)
						{
							String strQuery 	= 	"SELECT PrimaryQuantity, SecondaryQuantity, advcCases, advcUnits From tblCheckINDemandStockInventory WHERE Date LIKE '%"+date+"%' AND EmpNo='"+empNo+"' AND ItemCode = '"+productDO.SKU+"'";
							cursor 	= objSqliteDB.rawQuery(strQuery, null);
							if(cursor.moveToFirst())
							{
								float primaryCase 	= cursor.getFloat(0);
								float primaryUnint 	= cursor.getFloat(1);
								float availCases 	= cursor.getFloat(2);
								float availUnits 	= cursor.getFloat(3);
								
								primaryCase			= primaryCase + StringUtils.getFloat(productDO.preCases);
								primaryUnint		= primaryUnint + StringUtils.getFloat(productDO.preCases);
								availCases			= availCases + StringUtils.getFloat(productDO.preCases);
								availUnits			= availUnits + StringUtils.getFloat(productDO.preUnits);
								
								stmtUpdateQty.bindString(1, ""+primaryCase);
								stmtUpdateQty.bindString(2, ""+primaryUnint);
								stmtUpdateQty.bindString(3, ""+availCases);
								stmtUpdateQty.bindString(4, ""+availUnits);
								stmtUpdateQty.bindString(5, ""+empNo);
								stmtUpdateQty.bindString(6, ""+productDO.SKU);
								stmtUpdateQty.execute();
								
								if(cursor != null && !cursor.isClosed())
									cursor.close();
							}
						}
						else
						{
							stmtInsert.bindString(1, date);
							stmtInsert.bindString(2, empNo);
							stmtInsert.bindString(3, productDO.SKU);
							stmtInsert.bindString(4, ""+productDO.preCases);
							stmtInsert.bindString(5, ""+productDO.preUnits);
							stmtInsert.bindString(6, "0");
							stmtInsert.bindString(7, ""+productDO.preCases);
							stmtInsert.bindString(8, ""+productDO.preUnits);
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public void deleteOrder(OrderDO orderDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = 	null;
			Cursor cursor 			   = 	null;
			try 
			{
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtDeleteOrder 	= 	objSqliteDB.compileStatement("Delete from tblOrderHeader Where OrderId = ?");
				SQLiteStatement stmtDeleteOrderDetail= 	objSqliteDB.compileStatement("Delete from tblOrderDetail Where OrderNo = ?");
				
				if(orderDO != null)
				{
					stmtDeleteOrder.bindString(1, orderDO.OrderId);
					stmtDeleteOrder.execute();
					
					stmtDeleteOrderDetail.bindString(1, orderDO.OrderId);
					stmtDeleteOrderDetail.execute();
				}
				stmtDeleteOrder.close();
				stmtDeleteOrderDetail.close();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public String getOrderId()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB  =  DatabaseHelper.openDataBase();
				String query =  "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) AND id NOT IN(SELECT InvoiceNumber FROM tblPendingInvoices) Order By id Limit 1";
				cursor       =  objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					orderId = cursor.getString(0);
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return orderId;
		}
	}
	
	public String getOrderCreditReciept()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB  =  DatabaseHelper.openDataBase();
				String query =  "SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
				cursor       =  objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					orderId = cursor.getString(0);
				
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return orderId;
		}
	}

	public void updateOrderNumber(String orderID) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderID+"'");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public void insertUpdateNonSellableItems(SQLiteDatabase objSqliteDB, SQLiteStatement stmtUpdateQty, 
			SQLiteStatement stmtInsert, ProductDO productDO, String UserCode, String vehicleCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				Cursor cursor 		= 	null;
				
				String strQuery 	= 	"SELECT ReceivedQty From tblNonSellableItems WHERE ItemCode = '"+productDO.SKU+"' AND ExpiryDate LIKE '"+productDO.strExpiryDate+"%' AND Reason= '"+productDO.reason+"'";
				cursor 				= 	objSqliteDB.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					float TotalQuantity 	= cursor.getFloat(0) + productDO.quantityBU;
					
					stmtUpdateQty.bindString(1, ""+TotalQuantity);
					stmtUpdateQty.bindString(2, ""+productDO.SKU);
					stmtUpdateQty.bindString(3, ""+productDO.strExpiryDate);
					stmtUpdateQty.bindString(4, ""+productDO.reason);
					stmtUpdateQty.execute();
				}
				else
				{
					stmtInsert.bindString(1, ""+(productDO.LineNo));
					stmtInsert.bindString(2, ""+UserCode);
					stmtInsert.bindString(3, ""+UserCode);
					stmtInsert.bindString(4, ""+productDO.SKU+"");
					stmtInsert.bindString(5, ""+productDO.quantityBU);
					stmtInsert.bindString(6, ""+0);
					stmtInsert.bindString(7, ""+productDO.LotNumber);
					stmtInsert.bindString(8, ""+productDO.strExpiryDate);
					stmtInsert.bindString(9, ""+vehicleCode);
					stmtInsert.bindString(10,""+productDO.UOM);
					stmtInsert.bindString(11,""+productDO.reason);
					stmtInsert.executeInsert();
				}
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}



//For EOT
	public String getSiteForEOT(String empNO)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String EMPNO = "";
			Cursor cursor = null;
			try
			{
				objSqliteDB  =  DatabaseHelper.openDataBase();
				String query =  "SELECT SiteNo from tblOrderHeader where  EmpNo='"+empNO+"' Limit 1";
				cursor       =  objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					EMPNO = cursor.getString(0);


				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(objSqliteDB != null)
					objSqliteDB.close();
			}

			return EMPNO;
		}
	}
	public String getCurrencyForEOT(String site)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String Cuurr = "";
			Cursor cursor = null;
			try
			{
				objSqliteDB  =  DatabaseHelper.openDataBase();
				String query =  "SELECT CurrencyCode from tblCustomer where  Site='"+site+"' Limit 1";
				cursor       =  objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					Cuurr = cursor.getString(0);


				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(objSqliteDB != null)
					objSqliteDB.close();
			}

			return Cuurr;
		}
	}

}
