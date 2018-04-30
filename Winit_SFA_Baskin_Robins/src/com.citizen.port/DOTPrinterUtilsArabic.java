package com.citizen.port;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.ganesh.iarabic.arabic864;
import com.honeywell.mobility.print.LinePrinter;
import com.honeywell.mobility.print.LinePrinterException;
import com.honeywell.mobility.print.PrinterException;
import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.dataobject.Customer_InvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.EOTSummaryPrintDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentInvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.PendingInvicesDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.NumberToEnglish;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


/**
 * BluetoothConnectMenu
 * @author Abdul Raheem Khan && Abhishek Srivastava
 * @version 2011. 12. 21.
 */
public class DOTPrinterUtilsArabic {

	private boolean delayEnable = false;
	private int delay = 100;
	private Context context;
	private BluetoothAdapter mBAdap;
	//	boolean stopWorker;
//	private  BluetoothSocket mmSocket;
//	private InputStream 	mIStrm;
//	private OutputStream    mOStrm;
	private boolean status;
	private Preference preference;
	private DecimalFormat deffAmt, amountFormate, qtyFormater, qtyFormaterNew, percentFormat;
	private static int maxPageLenth = 66;
	private static int maxPrintLenth = AppConstants.MAX_PRINT_LENGTH;
	private static int maxbottomLenth = 20;
	private static int maxbottomLenthNew = 13;
	private static int maxTotalDetailLenth = 10;
	private int/* maxPrintLenth = 66,*/ pageCount = 0;

	LinePrinter linePrinter = null;
	private arabic864 Arabic6822;

	private int tempLenght = 48;

	PaymentHeaderDO paymentHeaderDOTwo;
	// For Sales Summary Report
	private float totalSalesCredit, totalSalesCollection;
	float totalSalesAmount = 0.0f;
	float totalDAmount = 0.0f;
	float totalGAmount = 0.0f;
	float totalDiscountAmount = 0.0f;
	private int linePrintedCount = 0;

	private String curencyCode = "";

	//-----------------------------
	private float totalPrice = 0.0f;
	private ArrayList<InventoryObject> arrInventory;
	private DecimalFormat deffAmt2;
	private String strOrderId = "", LPO = "";
	private String INV = "%1$-10.10s %2$-38.38s\r\n";
	private String strReceiptNo = "";
	boolean isPageFinished = false;
	private DecimalFormat deffStock;
	private float totalDiscount = 0.0f;
	private boolean isArabicEnable = true; // it is enable the arabic data to print dynamically
	private final static  int FOOTER_HEIGHT = 3;
	String line = "------------------------------" +
			"------------------------------" +
			"------------------------------" +
			"------------------------------" +
			"---------\r\n" ;
	private boolean endPageTAGEnable =true; //this is for testing only

	public int noOfRoundingOffdigits=2;
	//	int linenumber=0;
	public void setLinePrinter(LinePrinter lp) {
		linePrinter = lp;
	}

	public DOTPrinterUtilsArabic(Context context, String curencyCode) {
		this.context = context;
		this.curencyCode = curencyCode;
		Arabic6822 = new arabic864();
//		  linenumber=0;

//		NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en_us"));
//		NumberFormat nf1 = NumberFormat.getNumberInstance(new Locale("en_us"));
//		NumberFormat nf2 = NumberFormat.getNumberInstance(new Locale("en_us"));


		preference = new Preference(context);
		noOfRoundingOffdigits=new CommonDA().getRoundOffValueFromDatabaseBasedonCountry(preference.getStringFromPreference(Preference.CURRENCY_CODE,""));
		if(noOfRoundingOffdigits==0)
			noOfRoundingOffdigits=2;
//		deffAmt= (DecimalFormat) nf;
		deffAmt = new DecimalFormat("##.###");
//		deffAmt.setMinimumFractionDigits(2);
//		deffAmt.setMaximumFractionDigits(2);
		deffAmt.setMinimumFractionDigits(noOfRoundingOffdigits);
		deffAmt.setMaximumFractionDigits(noOfRoundingOffdigits);

		deffAmt2 = new DecimalFormat("##.###");
		deffAmt2.setMinimumFractionDigits(noOfRoundingOffdigits);
		deffAmt2.setMaximumFractionDigits(noOfRoundingOffdigits);

//		qtyFormater= (DecimalFormat) nf1;
		qtyFormater = new DecimalFormat("##.###");
//		qtyFormater.setMinimumFractionDigits(2);
//		qtyFormater.setMaximumFractionDigits(2);
		qtyFormater.setMinimumFractionDigits(noOfRoundingOffdigits);
		qtyFormater.setMaximumFractionDigits(noOfRoundingOffdigits);


//		amountFormate= (DecimalFormat) nf2;
		amountFormate = new DecimalFormat("#,##,##,##,###.###");
//		amountFormate.setMinimumFractionDigits(2);
//		amountFormate.setMaximumFractionDigits(2);
		amountFormate.setMinimumFractionDigits(noOfRoundingOffdigits);
		amountFormate.setMaximumFractionDigits(noOfRoundingOffdigits);

		percentFormat = new DecimalFormat("#,##,##,##,###.###");
		percentFormat.setMinimumFractionDigits(2);
		percentFormat.setMaximumFractionDigits(2);

		deffStock = new DecimalFormat("##.###");
		deffStock.setMinimumFractionDigits(noOfRoundingOffdigits);
		deffStock.setMaximumFractionDigits(noOfRoundingOffdigits);
//		deffStock = new DecimalFormat("##.###");
//		deffStock.setMinimumFractionDigits(3);
//		deffStock.setMaximumFractionDigits(3);
	}


	public boolean Connect(LinePrinter lp) {

		return establishedConnection(lp);
	}

	/*
	 * Close the connection to bluetooth printer.
	 */
	@SuppressLint("NewApi")
	public void closeBT() throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
//			stopWorker = true;
//			if(mOStrm != null)
//				mOStrm.close();
//			if(mIStrm != null)
//				mIStrm.close();
//			if(mmSocket != null && mmSocket.isConnected())
//				mmSocket.close();
					if (linePrinter != null) {
						linePrinter.disconnect();  // Disconnects from the printer
						linePrinter.close();
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@SuppressLint("NewApi")
	private boolean establishedConnection(LinePrinter lp) {
		boolean status = true;

		try {
			linePrinter = lp;
		} catch (NullPointerException e) {
			status = false;
			e.printStackTrace();
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}
		return status;
	}


	//For Printing Daily Order summmery
	private float totalOrderAmount = 0;



	//	private void printOrderStatementInner(Vector<TrxHeaderDO> vecOrderList)
	private void printOrderStatementInner(Vector<OrderDO> vecOrderList) {
		try {

			String formateDataHeader = "%1$-5.5s %2$-16.16s %3$-16.16s %4$-16.16s %5$-67.67s %6$12.12s";
			String formateAmount = "%1$110.110s %2$-11.11s %3$1.1s %4$12.12s\r\n";
			writeText(String.format(formateDataHeader, "S No", "Invoice Date", "Invoice No", "Customer No", "Customer Name", "Amount  "));
			writehorinzontaldivider();
			for (int i = linePrintedCount; i < vecOrderList.size() && isPageAvailable(maxbottomLenth + maxTotalDetailLenth); i++, linePrintedCount++) {
//				TrxHeaderDO trxHeaderDO = vecOrderList.get(i);
				OrderDO trxHeaderDO = vecOrderList.get(i);
				int srNUmber = i + 1;
//				String invoieDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, ""+trxHeaderDO.trxDate);
				String invoieDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, "" + trxHeaderDO.InvoiceDate);
//				String amount = ""+deffAmt.format(StringUtils.getFloat(""+(trxHeaderDO.totalAmount-(trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalPromoDiscountAmount))));
//				writeText(String.format(formateDataHeader,""+srNUmber,""+invoieDate,""+trxHeaderDO.trxCode,trxHeaderDO.clientCode,trxHeaderDO.siteName,amount+"\r\n"));
				writeText(String.format(formateDataHeader, "" + srNUmber, "" + invoieDate, "" + trxHeaderDO.OrderId, trxHeaderDO.CustomerSiteId, "*SITENAME*", "*AMOUNT*" + "\r\n"));
//				totalOrderAmount = totalOrderAmount+StringUtils.getFloat(""+(trxHeaderDO.totalAmount-(trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalPromoDiscountAmount)));
				totalOrderAmount = totalOrderAmount + StringUtils.getFloat("" + (trxHeaderDO.TotalAmount/*-(trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalPromoDiscountAmount)*/));
			}
			if (linePrintedCount >= vecOrderList.size()) {
				linePrintedCount = 0;
				writehorinzontaldivider();
				writeText(String.format(formateAmount, "", "Total", ":", "" + deffAmt.format(StringUtils.getFloat("" + totalOrderAmount))));
			}
			while (isPageAvailable(maxbottomLenth)) {
				writeText("\r\n");

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sortByDate(Vector<Customer_InvoiceDO> vec) {
		Collections.sort(vec, new Comparator<Customer_InvoiceDO>() {
			@Override
			public int compare(Customer_InvoiceDO s1, Customer_InvoiceDO s2) {
				return s1.chequeDate.compareTo(s2.chequeDate);
			}
		});
	}

	public void sortByInvoiceReciept(Vector<Customer_InvoiceDO> vec) {
		Collections.sort(vec, new Comparator<Customer_InvoiceDO>() {
			@Override
			public int compare(Customer_InvoiceDO s1, Customer_InvoiceDO s2) {
				return s1.receiptNo.compareTo(s2.receiptNo);
			}
		});
	}

	public long dateDiffence(String before, String after) {
		SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
		long day = 0;

		try {
			Date date1;

			date1 = myFormat.parse(before);
			Date date2 = myFormat.parse(after);
			long diff = date2.getTime() - date1.getTime();
			day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return day;
	}

	public void writehorinzontaldividerSummary() throws UnsupportedEncodingException, IOException {
		String formatSeprator = "%1$-136.136s \r\n";
		String seprator = "____________________________________________________________________________________________________________________________________________________";
		writeTextBoldSummary(String.format(formatSeprator, seprator));
	}
	// For Summary End....


	public boolean isPageAvailable(int reqLenth) {
		return maxPrintLenth > reqLenth;
	}

	public void writehorinzontaldivider() throws UnsupportedEncodingException, IOException {
		String formatSeprator = "%1$-136.136s \r\n";
		String seprator = "____________________________________________________________________________________________________________________________________________________";
		writeTextCompressBold(String.format(formatSeprator, seprator));
	}

	public void writehorinzontaldividernew() throws UnsupportedEncodingException, IOException {
		String formatSeprator = "%1$-77.77s \r\n";
		String seprator = "____________________________________________________________________________________________________________________________________________________";
		writeTextCompressBold(String.format(formatSeprator, seprator));
	}

	public void writehorinzontaldividerlargenew() throws UnsupportedEncodingException, IOException {
		String formatSeprator = "%1$-68.68s \r\n";
		String seprator = "____________________________________________________________________________________________________________________________________________________";
		writeTextLargeBold(String.format(formatSeprator, seprator));
	}

	public String[] gethorinzontaldivider() throws UnsupportedEncodingException, IOException {
		String formatSeprator = "%1$-136.136s \r\n";
		String seprator = "____________________________________________________________________________________________________________________________________________________";
		return new String[]{formatSeprator, seprator};
	}


	public void sort(Vector<String> vec) {
		Collections.sort(vec, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
	}

	void enableBlueTooth() {

		mBAdap = BluetoothAdapter.getDefaultAdapter();


		if (mBAdap == null) {
			//	         myLabel.setText("No bluetooth adapter available");
			Toast.makeText(context, "No bluetooth  available", Toast.LENGTH_LONG).show();
		} else if (mBAdap != null && !mBAdap.isEnabled()) {
			//	         Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			Toast.makeText(context, "Enable Bluetooth ", Toast.LENGTH_LONG).show();

		}

	}

	public void Pageout() {
		try {
			closeBT();
			tempLenght = 45;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private void printCommonFooter(String strCust, String strSales, String strCopy) throws UnsupportedEncodingException, IOException {
		String formate = "";
		strCust = strCust.toUpperCase() + "____________";
		strSales = strSales.toUpperCase() + "____________";
		int sSL = strSales.length();
//		int sSP = (40-sSL)/2;
		int sCL = strCust.length();
//		int sCP = (40-sCL)/2;
		formate = "%1$2.2s %2$" + sCL + "." + sCL + "s %3$" + 2 + "." + 2 + "s %4$" + sSL + "." + sSL + "s \r\n";
		writeTextBold(String.format(formate, "", "" + strCust, "", "" + strSales));
		maxPrintLenth--;
		addNewLines(1);
		if (!strCopy.isEmpty()) {
			int strLenth = strCopy.length();
			//			int strPos = (66-strLenth)/2;
			int strPos = (60 - strLenth);
			formate = "%1$" + strPos + "." + strPos + "s %2$" + strLenth + "." + strLenth + "s \r\n";
			//			writeTextLarge(String.format(formate, "",""+strCopy.toUpperCase()));
			writeTextBold(String.format("%1$-30.30s %2$-10.10s\r\n", "", strCopy.toUpperCase()));
			maxPrintLenth--;
			//			HeaderBoldFont(String.format("%1$-55.55s %2$-10.10s\r\n" ,"",strCopy.toUpperCase()));
		}
//		while (isPageAvailable(0))
//		{
//			addNewLines(1);
//		}
		if (maxPrintLenth >= 6)
			addNewLines(maxPrintLenth - 6);
		maxPrintLenth = maxPageLenth;
//		cancleCondence();
	}



	public void printCustomerDetails(JourneyPlanDO mallsDetails) throws UnsupportedEncodingException, IOException {

		String Address = "";
		String Address1 = "";
		if (mallsDetails.addresss1 != null && !mallsDetails.addresss1.equalsIgnoreCase(""))
			Address += mallsDetails.addresss1 + " ";
		if (mallsDetails.addresss2 != null && !mallsDetails.addresss2.equalsIgnoreCase(""))
			Address += mallsDetails.addresss2 + " ";
		if (mallsDetails.addresss3 != null && !mallsDetails.addresss3.equalsIgnoreCase(""))
			Address += mallsDetails.addresss3 + "";
		if (mallsDetails.city != null && !mallsDetails.city.equalsIgnoreCase(""))
			Address1 += "" + mallsDetails.city + " ";
		/*if(mallsDetails.subAreaName !=null && !mallsDetails.subAreaName.equalsIgnoreCase(""))
			Address1 += mallsDetails.subAreaName+" ";
		if(mallsDetails.regionName !=null && !mallsDetails.regionName.equalsIgnoreCase(""))
			Address1 += mallsDetails.regionName + " ";//mallsDetails.regionCode+" ";
		if(mallsDetails.coutryCode !=null && !mallsDetails.coutryCode.equalsIgnoreCase(""))
			Address1 += mallsDetails.coutryCode+"";*/

		String formateHeader = "%1$-15.15s %2$-1.1s %3$-107.107s  \r\n";
		String formateHeader_address = "%1$-15.15s %2$-1.1s %3$-75.75s  \r\n";
		writeText(String.format(formateHeader, "Customer", ":", "" + mallsDetails.site + "       " + mallsDetails.siteName));
		writeText(String.format(formateHeader_address, "Address", ":", "" + Address));
		writeText(String.format(formateHeader_address, "", "", "" + Address1));
		addNewLines(1);
	}


	//For Printing Pending Invoices Summary
	private float totalPendingPaymentSummaryPrice;
	private float totalPendingInvoiceSummaryPrice;
	private float totalPendingRetrunSummaryPrice;


	public void printPendingPaymentSummaryInner(ArrayList<PendingInvicesDO> arrInvoiceNumbers) throws UnsupportedEncodingException, IOException {
		String formatFooter = "%1$136.136s\r\n";
		String formatItemheader = "%1$-11.11s %2$-30.30s %3$30.30s %4$20.20s %5$20.20s %6$20.20s\r\n";
		writeText(String.format(formatItemheader, "S1#", "Invoice No", "Invoice Type", "Invoice Dt", "Inv  Amount (" + curencyCode + ")", "Amount Due (" + curencyCode + ")"));
		writehorinzontaldivider();
		for (int i = linePrintedCount; i < arrInvoiceNumbers.size() && isPageAvailable(maxbottomLenth + maxTotalDetailLenth + 2); i++, linePrintedCount++) {
			PendingInvicesDO pendingInvicesDO = arrInvoiceNumbers.get(i);
			int srNUmber = i + 1;
			String invoieDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, pendingInvicesDO.invoiceDate);
			//				float amount = Math.abs(StringUtils.getFloat(pendingInvicesDO.balance));
			float amount = StringUtils.getFloat(pendingInvicesDO.balance);

			/*writeText( String.format(formatItemheader,""+srNUmber,""+pendingInvicesDO.invoiceNo,PendingInvicesDO.getPendingInvoiceType(pendingInvicesDO.INV_TYPE),""+invoieDate,""+deffAmt.format(Math.abs(StringUtils.getFloat(pendingInvicesDO.totalAmount))),""+deffAmt.format(amount)));
			if (pendingInvicesDO.INV_TYPE.equalsIgnoreCase(PendingInvicesDO.getReturnPendingInvoice()))
			{
				totalPendingPaymentSummaryPrice = totalPendingPaymentSummaryPrice-amount;
				totalPendingRetrunSummaryPrice += amount;
			}
			else if (pendingInvicesDO.INV_TYPE.equalsIgnoreCase(PendingInvicesDO.getReturnPendingInvoice()))
			{
				totalPendingPaymentSummaryPrice = totalPendingPaymentSummaryPrice-amount;
				totalPendingRetrunSummaryPrice += amount;
			}
			else if (pendingInvicesDO.INV_TYPE.equalsIgnoreCase(PendingInvicesDO.getSalesPendingInvoice()))
			{
				totalPendingPaymentSummaryPrice = totalPendingPaymentSummaryPrice+amount;
				totalPendingInvoiceSummaryPrice += amount;
			}
			else
			{
				totalPendingPaymentSummaryPrice = totalPendingPaymentSummaryPrice+StringUtils.getFloat(pendingInvicesDO.balance);
				if(StringUtils.getFloat(pendingInvicesDO.balance)>=0)
					totalPendingInvoiceSummaryPrice += amount;
				else
					totalPendingRetrunSummaryPrice += amount;
			}*/
		}
		if (linePrintedCount >= arrInvoiceNumbers.size()) {
			isPageFinished = true;
			writehorinzontaldivider();
			writeTextBold(String.format(formatFooter, "Total Pending Invoice (" + curencyCode + ") : " + deffAmt.format(totalPendingInvoiceSummaryPrice)));
			writeTextBold(String.format(formatFooter, "Total Credit Notes(" + curencyCode + ") : " + deffAmt.format(totalPendingRetrunSummaryPrice)));
			//				 writeText( String.format(formatFooter,"Total ("+curencyCode+") : "+deffAmt.format(totalPendingPaymentSummaryPrice)));
		}
		do {
			writeText("\r\n");
		}
		while (isPageAvailable(maxbottomLenth + maxTotalDetailLenth));
	}


	//	private void HeaderBoldFont(String txt) throws IOException {
//
//		mOStrm.write(27);
//		mOStrm.write(71);
//
//		mOStrm.write(27);
//		mOStrm.write(14);
//		mOStrm.write(txt.getBytes());
//		mOStrm.write(27);
//		mOStrm.write(20);
//		mOStrm.write(27);
//		mOStrm.write(72);
//
//	}

	public void writeTextSummary(String strText) {
		try {
			byte[] b = null;
			try {
				b = strText.getBytes("ASCII");
				writeBytes(b);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/////***************************************All write methods***************************************************************
	public void writeText(String strText) {
		maxPrintLenth = maxPrintLenth - 1;
//		maxPrintLenth=maxPrintLenth<1? AppConstants.PAGE_HEIGHT:maxPrintLenth;
		try {
			linePrinter.write(strText);

		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeTxt(String strText) {

		if(maxPrintLenth <=0) {
			printLines(6);
			adujstHalfLineHeightForEachPaper();
			maxPrintLenth= AppConstants.PRINTABLE_PAGE_HEIGHT ;
			giveDelay();
		}

			maxPrintLenth = maxPrintLenth - 1;
			try {
				linePrinter.write(strText);

			} catch (PrinterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public void write(String strText) {
		try {
			linePrinter.write(strText);
		} catch (PrinterException e) {
			e.printStackTrace();
		}

	}

	public void writeSpace(int s) {
		try {

			linePrinter.write(String.format("%1$-" + s + "." + s + "s", "*"));
		} catch (PrinterException e) {
			e.printStackTrace();
		}

	}

	public void writeTextCompress(String strText) {
		maxPrintLenth = maxPrintLenth - 1;
		try {
			linePrinter.setCompress(true);

			linePrinter.write(strText);
			linePrinter.setCompress(false);
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeTextCompressBold(String strText) {
//		maxPrintLenth = maxPrintLenth - 1;
		try {
			linePrinter.setCompress(true);
			linePrinter.setBold(true);
			linePrinter.write(strText);
			linePrinter.setBold(false);
			linePrinter.setCompress(false);
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeTextCompressBold(byte[] strText) {
//		maxPrintLenth = maxPrintLenth - 1;
		try {
			linePrinter.setCompress(true);
			linePrinter.setBold(true);
			linePrinter.write(strText);
			linePrinter.setBold(false);
			linePrinter.setCompress(false);
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeTextCompress(byte[] strText) {
//		maxPrintLenth = maxPrintLenth - 1;
		try {
			linePrinter.setCompress(true);
			linePrinter.write(strText);
			linePrinter.setCompress(false);
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeText(byte[] strText) {
//		maxPrintLenth = maxPrintLenth - 1;
		try {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			linePrinter.write(strText);
		} catch (PrinterException e) {
			e.printStackTrace();
		}
	}

	public void writeTextBold(String strText) {
		selectBold();
		writeText(strText.toUpperCase(Locale.ENGLISH));
		cancleBold();

	}

	public void writeTextBold(byte[] strText) {
		selectBold();
		writeText(strText);
		cancleBold();

	}

	//	public void writeTextLarge(String strText)
//	{
//		selectDoubleWidth();
//		writeText(strText);
//		cancleDoubleWidth();
//
//	}
//	public void writeTextLarge(byte[] strText)
//	{
//		selectDoubleWidth();
//		writeText(strText);
//		cancleDoubleWidth();
//
//	}
	//According to old code large is bold and vice versa
	public void writeTextLarge(String strText) {
		selectBold();
		writeText(strText);
		cancleBold();

	}

	public void writeTextLarge(byte[] strText) {
		selectBold();
		writeText(strText);
		cancleBold();

	}

	public void writeTextLargeBold(String strText) {
		selectDoubleWidth();
		writeTextBold(strText);
		cancleDoubleWidth();

	}

	public void writeTextLargeBold(byte[] strText) {
		selectDoubleWidth();
		writeTextBold(strText);
		cancleDoubleWidth();

	}

	public void selectBold() {
		try {
			linePrinter.setBold(true);
		} catch (LinePrinterException e) {
			e.printStackTrace();
		}

	}

	public void cancleBold() {
		try {
			linePrinter.setBold(false);
		} catch (LinePrinterException e) {
			e.printStackTrace();
		}
	}

	public void selectDoubleWidth() {
		try {
			linePrinter.setDoubleWide(true);
			linePrinter.setDoubleHigh(true);
		} catch (LinePrinterException e) {
			e.printStackTrace();
		}
	}

	public void cancleDoubleWidth() {
		try {
			linePrinter.setDoubleWide(false);
			linePrinter.setDoubleHigh(false);
		} catch (LinePrinterException e) {
			e.printStackTrace();
		}
	}

	private void addNewLines(int count) {
		try {
			maxPrintLenth--;
			linePrinter.newLine(count);
		} catch (LinePrinterException e) {
			e.printStackTrace();
		}
	}

	public void writeTextBoldSummary(String strText) {
		selectBold();
		writeTextSummary(strText.toUpperCase(Locale.ENGLISH));
		cancleBold();
	}

	public void writeTextLargeSummary(String strText) {
		selectDoubleWidth();
		writeTextSummary(strText);
		cancleDoubleWidth();
	}

	public void writeTextLargeBoldSummary(String strText) {
		selectDoubleWidth();
		writeTextBoldSummary(strText);
		cancleDoubleWidth();
	}

	public void writeBytes(byte[] b) {
		try {

			linePrinter.write(b);
		} catch (PrinterException e) {
			e.printStackTrace();
		}
	}

	private String printMobileNumber(JourneyPlanDO journeyPlanDO) {
		if (journeyPlanDO != null && !TextUtils.isEmpty(journeyPlanDO.SalesPersonMobileNumber))
			return journeyPlanDO.SalesPersonMobileNumber;
		return "N/A";
	}


	public void printPaymentReceipt(String s, String type, String strReceiptNo, JourneyPlanDO mallsDetails, PaymentHeaderDO objPaymentDO) {

		String formatHeader = "%1$68.68s";
		String lines =line ;// "================================================================================\r\n";
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		String price = "%1$-1.1s%2$-62.62s%3$62.62s\r\n";
		String formatInvoice = "%1$31.31s%2$31.31s%3$31.31s%4$31.31s\r\n";

		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
//		maxPrintLenth=maxPrintLenth-3;
		write("\r\n");
//		printHeader();
		setCompress();
		write(String.format(formatHeader,   type.toUpperCase() ));
		writeText("\r\n");


		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = objPaymentDO.ReceiptId;

		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = preference.getStringFromPreference(Preference.RECIEPT_NO, "");
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";

		writeText(String.format(columns2, "Receipt Number: " + strReceiptNo, "Date : " + mydate));

		writeText(String.format(columns2, "Site Name: " + mallsDetails.partyName, "Customer Name: " + mallsDetails.siteName));
		writeText(String.format(columns2, "Site No: " + mallsDetails.site, "Customer No: " + mallsDetails.customerId));
		writeText(String.format(columns2, mallsDetails.addresss1, "Receipt Number: " + strReceiptNo));
		writeText(String.format(columns2, mallsDetails.addresss2, "Collected By: " + preference.getStringFromPreference(Preference.USER_NAME, "")));
		writeText(String.format(columns2, mallsDetails.addresss3, "Date: " + mydate));

		if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			writeText(String.format(columns2, "Salesman : " + mallsDetails.salesmanName, ""));

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		writeText(String.format(columns2, "Sub-Inventory : " + subInventory, ""));

		writeText("\r\n");
		write(String.format(formatHeader,   s.toUpperCase() ));
//		writeText(String.format(formatHeader, "", s.toUpperCase(), ""));
		writeText("\r\n");
		writeText(lines);


		if (objPaymentDO != null && objPaymentDO.vecPaymentDetails != null && objPaymentDO.vecPaymentDetails.size() > 0) {
			for (PaymentDetailDO paymentDetailDO : objPaymentDO.vecPaymentDetails) {
				if (paymentDetailDO.PaymentTypeCode.equalsIgnoreCase("CHEQUE")) {
					writeTxt(String.format(columns2, "Payment Type            : ", paymentDetailDO.PaymentTypeCode));
					writeTxt(String.format(columns2, "Cheque No               : ", paymentDetailDO.ChequeNo));
					writeTxt(String.format(columns2, "Bank Name               : ", paymentDetailDO.BankName));
					writeTxt(String.format(columns2, "Cheque Date             : ", CalendarUtils.getFormatedDatefromStringPrint(paymentDetailDO.ChequeDate)));
					writeTxt(String.format(columns2, "Amount                  : ", paymentDetailDO.Amount + " " + curencyCode + ""));
				} else {
					writeTxt(String.format(columns2, "Payment Type            : ", paymentDetailDO.PaymentTypeCode));
					writeTxt(String.format(columns2, "Amount                  : ", paymentDetailDO.Amount));
					writeTxt("\r\n");
				}
				writeTxt(lines);
			}
		}

		float ftBalDue = 0f;
		writeText("\r\n");

		writeTxt(String.format(formatInvoice, "Invoice Number", "Total Amount", "Paid Amount", "Balance Amount"));
		writeTxt(lines);
		if (objPaymentDO.vecPaymentInvoices != null)
			for (int i = 0; i < objPaymentDO.vecPaymentInvoices.size(); i++) {
				PaymentInvoiceDO paymentInvoiceDO = objPaymentDO.vecPaymentInvoices.get(i);
				float ftTotal = StringUtils.getFloat(paymentInvoiceDO.totalAmt);
				float ftPaid = StringUtils.getFloat(paymentInvoiceDO.Amount);
				float balance = ftTotal - ftPaid;
				ftBalDue += ftPaid;

				writeTxt(String.format(formatInvoice, paymentInvoiceDO.TrxCode, deffAmt.format(ftTotal), deffAmt.format(ftPaid), deffAmt.format(balance)));
			}
		writeTxt(lines);
		writeTxt(String.format(formatInvoice, "Total ", deffAmt.format(ftBalDue) + " " + mallsDetails.currencyCode,"",""));
		writeTxt(lines);
		printFooter(3, " ","Customer Signature");

//		printFooter();
	}

	/*public void printReturnLoad(String from, ArrayList<InventoryObject> arrInventory) {
		totalPrice = 0.0f;

		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines = "================================================================================\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formateHeader = "%1$-2.2s %2$-10.10s %3$-13.13s %4$17.17s %5$7.7s %6$7.7s %7$9.9s %8$-9.9s\r\n";//73
		String formateHeader3 = "%1$-2.2s %2$-3.3s %3$-20.20s %4$20.20s %5$7.7s %6$7.7s\r\n";//73
		String formateHeader2 = "%1$-2.2s %2$-10.10s %3$-13.13s %4$27.27s %5$7.7s %6$7.7s  \r\n";//73
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatReason = "%1$-24.24s %2$-24.24s %3$-24.24s\r\n";
		String formatPriceBold = "%1$-10.10s %2$-17s";
		String formatPriceNormal = "%1$-10.10s %2$46s";
		String format = "%1$-2.2s %2$-5.5s %3$-15.15s %4$34.34s %5$7.7s %6$7.7s\r\n";

		writeText(String.format(formatHeader, "", from, ""));

//		printHeader();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

		writeText(String.format(columns2, "User Code: " + preference.getStringFromPreference(Preference.USER_ID, ""), "Date: " + mydate.substring(0, mydate.lastIndexOf(":"))));
		writeText(lines);

		writeText(String.format(format, "", "SR#", "ITEM CODE", "ITEM DESCRIPTION", "UOM", "Quantity"));
		writeText(lines);

		totalPrice = 0;
		for (int i = 0; arrInventory != null && i < arrInventory.size(); i++) {
			writeText(String.format(format, "", "" + (i + 1), arrInventory.get(i).itemCode, arrInventory.get(i).itemDescription, arrInventory.get(i).UOM, "" + deffAmt.format(arrInventory.get(i).PrimaryQuantity)));
			writeText(String.format(formatReason, "", "Ex. Date: " + arrInventory.get(i).expiryDate, "Reason: " + arrInventory.get(i).reason, ""));
		}
		writeText(lines);
//		printFooter();
	}*/

	/*public void printARPaymentSummary(String from, ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice, String strSelectedDateToPrint, String type) {

//		String formatHeader 	= "%1$-16.16s %2$-41.41s %3$-3.3s";
//		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
//		String format 			= "%1$-10.10s %2$-25.25s %3$-10.10s %4$-14.14s %5$-10.10s %6$12.12s\r\n";
//		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
//		String lines 			= "------------------------------------------------------------------------------------\r\n";
//		String singleLine       = "------------------------------------------------------------------------------------";
//		String dividerLine      = "|                                    |";
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines =            "================================================================================\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formateHeader = "%1$-2.2s %2$-10.10s %3$-13.13s %4$17.17s %5$7.7s %6$7.7s %7$9.9s %8$-9.9s\r\n";//73
		String formateHeader3 = "%1$-2.2s %2$-3.3s %3$-20.20s %4$20.20s %5$7.7s %6$7.7s\r\n";//73
		String formateHeader2 = "%1$-2.2s %2$-10.10s %3$-13.13s %4$27.27s %5$7.7s %6$7.7s  \r\n";//73
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatReason = "%1$-24.24s %2$-24.24s %3$-24.24s\r\n";
		String formatPriceBold = "%1$-10.10s %2$-17s";
		String formatPriceNormal = "%1$-10.10s %2$46s";
//		String format 			= "%1$-2.2s %2$-5.5s %3$-15.15s %4$34.34s %5$7.7s %6$7.7s\r\n";
				String format 			= "%1$-25.25s %2$-15.15s %3$-15.15s %4$-14.14s %5$-10.10s\r\n";


		writeText( String.format(formatHeader,"","RECEIPT SUMMARY","") );

//		printHeader();

		String mydate 		= 	java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

		writeText( String.format(columns2,"User Name:"+preference.getStringFromPreference(Preference.USER_NAME, ""),"Emp No.: "+preference.getStringFromPreference(Preference.EMP_NO, "")) );
		writeText( String.format(columns2,"Collected Date: "+(strSelectedDateToPrint != null ? strSelectedDateToPrint : mydate),"Print Date: "+mydate) );

		writeText( "\r\n");
		writeText( lines);
		boolean isCash = false, isCheque = false;
		if(arrayListCustomerInvoice!=null && arrayListCustomerInvoice.size()>0)
		{
			for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
			{
				if(cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					isCheque = true;
				else if(cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
					isCash = true;
			}
		}

		String currencyCode = "";
		if(isCheque)
		{
			writeText( String.format(format, "Site Name","Site No.","Receipt No.","Type","Amount") );
			writeText( lines);
			Float strTotalAmount = 0f;
			if(arrayListCustomerInvoice!=null && arrayListCustomerInvoice.size()>0)
			{
				for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
				{
					if(cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					{
						float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
						strTotalAmount += amount;
						writeText( String.format(format, cuInvoiceDO.siteName,cuInvoiceDO.customerSiteId,cuInvoiceDO.receiptNo,cuInvoiceDO.reciptType,deffAmt.format(amount)) );

						if(currencyCode == null || currencyCode.length() <= 0)
							currencyCode = cuInvoiceDO.currencyCode;
					}
				}

				if(strTotalAmount > 0)
				{
					writeText(lines);
 				}
			}
		}
		if(isCash)
		{
			Float strTotalCashAmount = 0f;
			writeText( String.format(format, "Site Name","Site No.","Receipt No.","Type","Amount") );
			writeText(lines);
			for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
			{
				if(cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
				{
					float amount 		= StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
					strTotalCashAmount += amount;
					writeText( String.format(format, cuInvoiceDO.siteName,cuInvoiceDO.customerSiteId,cuInvoiceDO.receiptNo,cuInvoiceDO.reciptType,deffAmt.format(amount)) );

					if(currencyCode == null || currencyCode.length() <= 0)
						currencyCode = cuInvoiceDO.currencyCode;
				}
			}
			if(strTotalCashAmount > 0)
			{
				writeText( lines);
				writeText( String.format(columns2,"Cash Total: ","","",currencyCode,deffAmt.format(strTotalCashAmount)) );
				writeText( lines);
			}
		}
		writeText( "\r\n");
		writeText( String.format(columns2,"AR Department","Salesman") );
		writeText( "\r\n\r\n\r\n");
		writeText( lines);
//		printFooter();
	}*/

	/*public void printCollectionPaymentSummary(String from, ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice, String strSelectedDateToPrint, String type) {


		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines = "================================================================================\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formateHeader = "%1$-2.2s %2$-10.10s %3$-13.13s %4$17.17s %5$7.7s %6$7.7s %7$9.9s %8$-9.9s\r\n";//73
		String formateHeader3 = "%1$-2.2s %2$-3.3s %3$-20.20s %4$20.20s %5$7.7s %6$7.7s\r\n";//73
		String formateHeader2 = "%1$-2.2s %2$-10.10s %3$-13.13s %4$27.27s %5$7.7s %6$7.7s  \r\n";//73
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatReason = "%1$-24.24s %2$-24.24s %3$-24.24s\r\n";
		String formatPriceBold = "%1$-10.10s %2$-17s";
		String formatPriceNormal = "%1$-10.10s %2$46s";
		String format = "%1$-16.16s %2$-7.7s %3$-10.10s %4$-10.10s %5$-15.15s %6$-12.12s\r\n";

		writeText(String.format(formatHeader, "", "RECEIPT SUMMARY", ""));

//		printHeader();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

		writeText(String.format(columns2, "User Name:" + preference.getStringFromPreference(Preference.USER_NAME, ""), "Emp No.: " + preference.getStringFromPreference(Preference.EMP_NO, "")));
		writeText(String.format(columns2, "Collected Date: " + (strSelectedDateToPrint != null ? strSelectedDateToPrint : mydate), "Print Date: " + mydate));

		writeText("\r\n");
		writeText(lines);

		boolean isCash = false, isCheque = false;
		if (arrayListCustomerInvoice != null && arrayListCustomerInvoice.size() > 0) {
			for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
				if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					isCheque = true;
				else if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
					isCash = true;
			}
		}

		String currencyCode = "";
		if (isCheque) {
			writeText(String.format(format, "Receipt No.", "Type", "Cheque No.", "Dated", "Bank Name", "Amount"));
			writeText(lines);
			Float strTotalAmount = 0f;
			if (arrayListCustomerInvoice != null && arrayListCustomerInvoice.size() > 0) {
				for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
					if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque")) {
						float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
						strTotalAmount += amount;
						writeText(String.format(format, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, cuInvoiceDO.chequeNo, cuInvoiceDO.chequeDate, cuInvoiceDO.bankName, deffAmt.format(amount)));

						if (currencyCode == null || currencyCode.length() <= 0)
							currencyCode = cuInvoiceDO.currencyCode;
					}
				}

				if (strTotalAmount > 0) {
					writeText(lines);
					writeText(String.format(format, "Cheque Total: ", "", "", "", "", currencyCode, deffAmt.format(strTotalAmount)));
					writeText(lines);
				}
			}
		}

		if (isCash) {
			writeText(String.format(format, "Receipt No.", "Type", "", "", "", "Amount"));
			writeText(lines);
			Float strTotalCashAmount = 0f;
			for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
				if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash")) {
					float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
					strTotalCashAmount += amount;
					writeText(String.format(format, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, "", "", "", deffAmt.format(amount)));

					if (currencyCode == null || currencyCode.length() <= 0)
						currencyCode = cuInvoiceDO.currencyCode;
				}
			}
			if (strTotalCashAmount > 0) {
				writeText(lines);
				writeText(String.format(format, "Cash Total: ", "", "", "", "", currencyCode, deffAmt.format(strTotalCashAmount)));
				writeText(lines);
			}
		}
		writeText("\r\n");
		writeText(String.format(columns2, "Collection Head", "Salesman"));
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",singleLine,singleLine), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",singleLine,singleLine), 0, true);
//		printFooter();
	}*/

	public void printPaymentReceiptSummary(JourneyPlanDO mallsDetails, Customer_InvoiceDO customer_InvoiceDO, int linenumber) {
//		maxPrintLenth = AppConstants.MAX_PRINT_LENGTH;
//		writeText("\r\n\r\n\r\n\r\n");
//		maxPrintLenth = maxPrintLenth - 2;
		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeaderr = "%1$62.62s";
		String strType = "Receipt Detail";
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines = "-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- \r\n" ;

		String columns2 = "%1$-62.62s %2$-62.62s\r\n";

		String price = "%1$-1.1s %2$-62.62s %3$62.62s\r\n";

		String formatInvoice = "%1$31.31s %2$31.31s %3$31.31s %4$31.31s\r\n";

		setCompress();
		write(String.format(formatHeaderr,   "PAYMENT RECEIPT" ));
		writeText("\r\n");

//		printHeader();

		if (TextUtils.isEmpty(customer_InvoiceDO.payment_Id))
			strReceiptNo = customer_InvoiceDO.payment_Id;
		else
			strReceiptNo = customer_InvoiceDO.receiptNo;

		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase("")) {
			if (customer_InvoiceDO.vecPaymentDetailDOs != null && customer_InvoiceDO.vecPaymentDetailDOs.size() > 0 && customer_InvoiceDO.vecPaymentDetailDOs.get(0) != null)
				strReceiptNo = new PaymentDetailDA().getReceiptNo(customer_InvoiceDO.vecPaymentDetailDOs.get(0).invoiceNumber);
		}

		writeText( String.format(columns2, "Receipt Number: " + strReceiptNo, " " ));

		writeText(String.format(columns2, "Site Name: " + mallsDetails.partyName, "Customer Name: " + mallsDetails.siteName));
		writeText(String.format(columns2, "Site No: " + mallsDetails.site, "Customer No: " + mallsDetails.customerId));
		writeText(String.format(columns2, mallsDetails.addresss1, "Receipt Number: " + strReceiptNo));
		writeText(String.format(columns2, mallsDetails.addresss2, "Collected By: " + preference.getStringFromPreference(Preference.USER_NAME, "")));
		writeText(String.format(columns2, mallsDetails.addresss3, "Date: " + CalendarUtils.getFormatedDatefromStringWithTime(customer_InvoiceDO.reciptDate)));

		if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			writeText(String.format(String.format(columns2, "Salesman : " + mallsDetails.salesmanName, "")));

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		writeText(String.format(String.format(columns2, "Sub-Inventory : " + subInventory, "")));
/*
		writeText(String.format(formatHeader, "", strType.toUpperCase(), ""));
		writeText(lines);*/

		write(String.format(formatHeaderr,   strType.toUpperCase() ));
		writeText("\r\n");
		writeText(lines);

		if (customer_InvoiceDO.reciptType.equalsIgnoreCase("CHEQUE")) {
			writeText(String.format(columns2, "Payment Type           : ", customer_InvoiceDO.reciptType));
			writeText(String.format(columns2, "Cheque No              : ", customer_InvoiceDO.chequeNo));
			writeText(String.format(columns2, "Bank Name              : ", customer_InvoiceDO.bankName));
			writeText(String.format(columns2, "Cheque Date            : ", CalendarUtils.getFormatedDatefromStringPrint(customer_InvoiceDO.chequeDate)));
			writeText(String.format(columns2, "Amount                 : ", customer_InvoiceDO.totalVal + " " + curencyCode + ""));
		} else {
			writeText(String.format(columns2, "Payment Type           : ", customer_InvoiceDO.reciptType));
			writeText(String.format(columns2, "Amount                 : ", customer_InvoiceDO.totalVal + " " + curencyCode + ""));
			writeText("\r\n");
		}
		writeText(lines);

		int footer_height = 6;
		float ftBalDue = 0f;
		writeText("\r\n");

		writeText(String.format(formatInvoice, "Invoice Number", "Total Amount", "Paid Amount", "Balance Amount"));
//		woosim.saveSpool(EUC_KR, String.format(formatInvoice,"","Invoice Number","Total Amount","Paid Amount", "Balance Amount"), 0, true);
		writeText(lines);
		int lineForTotalLine = 3;//1. line 2. total 3. line
		if (customer_InvoiceDO.vecPaymentDetailDOs != null)
			for (int i = linenumber; lineForTotalLine + footer_height + (AppConstants.MAX_PRINT_LENGTH - maxPrintLenth) < AppConstants.MAX_PRINT_LENGTH && i < customer_InvoiceDO.vecPaymentDetailDOs.size(); i = (++linenumber))
//			for( int i = 0 ; i < customer_InvoiceDO.vecPaymentDetailDOs.size() ; i++ )
			{
				PaymentDetailDO paymentDetailDO = customer_InvoiceDO.vecPaymentDetailDOs.get(i);
				float ftTotal = StringUtils.getFloat(paymentDetailDO.invoiceAmount);
				float ftPaid = StringUtils.getFloat(paymentDetailDO.invoiceAmount);
				float balance = ftTotal - ftPaid;
				ftBalDue += ftPaid;

				writeText(String.format(formatInvoice, paymentDetailDO.invoiceNumber, deffAmt.format(ftTotal), deffAmt.format(ftPaid), deffAmt.format(balance)));
			}

			/*if( linenumber == customer_InvoiceDO.vecPaymentDetailDOs.size())
			{
				linenumber--; //print last order in next page
			}
			else */
		if (linenumber == customer_InvoiceDO.vecPaymentDetailDOs.size()) {
			writeText(lines);
			writeText(String.format(price, "", "Total ", deffAmt.format(ftBalDue) + " " + mallsDetails.currencyCode));
			writeText(lines);
		}

//		writeText(String.format(price,"","","Customer Signature") );
//		printFooter();

//		if(footer_height +(AppConstants.MAX_PRINT_LENGTH- maxPrintLenth ) >= AppConstants.MAX_PRINT_LENGTH)
		if (linenumber < customer_InvoiceDO.vecPaymentDetailDOs.size()) {
			//		footer_height = 8
			printLines(maxPrintLenth   - lineForTotalLine-FOOTER_HEIGHT);
			writeText(String.format(price, "", "", "Customer Signature"));
			writeText(lines);
			printToEnd();
//			write("----------Paged End-----------"); //END
			printLines(5);
			giveDelay();
			printPaymentReceiptSummary(mallsDetails, customer_InvoiceDO, linenumber);

		} else {
			printLines(maxPrintLenth -   lineForTotalLine-FOOTER_HEIGHT);
		 	printLines(1);
			writeText(String.format(price, "", "", "Customer Signature"));
			writeText(lines);
			printToEnd();
//			write("----------Paged End-----------");//END
			printLines(5);
//			writeText("\r\n\r\n\r\n\r\n");
//		printFooter();
		}

	}

	public void printLoadInventory(ArrayList<VanLoadDO> vecOrdProduct, LoadRequestDO loadRequestDO, String strMovementId, int movementType, int linenumber) {

//		maxPrintLenth = AppConstants.MAX_PRINT_LENGTH;
//		writeText("\r\n\r\n\r\n\r\n");
//		maxPrintLenth = maxPrintLenth - 2;
//		write("\r\n");
		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;

		String formatHeader = "%1$68.68s";
		String lines=line;
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		String price = "%1$-1.1s%2$-62.62s%3$62.62s\r\n";

		String formateHeader = "%1$-3.3s%2$-7.7s%3$-22.22s%4$22.22s%5$22.22s%6$22.22s%7$22.22s\r\n";//73
		String formatReason = "%1$-41.41s%2$-42.42s%3$-42.42s\r\n";

		deffStock = new DecimalFormat("##.###");
		deffStock.setMinimumFractionDigits(3);
		deffStock.setMaximumFractionDigits(3);

		totalPrice = 0.0f;

		String strTitle = "";

		if (loadRequestDO.MovementType.equalsIgnoreCase("" + AppStatus.LOAD_STOCK))
			strTitle = "Load Request";
		else
			strTitle = "Unload Request";

		setCompress();
		write (String.format(formatHeader, "" + strTitle ));
		writeText("\r\n");

//		printHeader();

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");

		if (strMovementId == null || strMovementId.equalsIgnoreCase(""))
			strMovementId = "";

		writeText(String.format(columns2, "Movement Code: " + strMovementId, ""));

		String date = "";
		if (loadRequestDO.MovementDate.contains("-"))
			date = CalendarUtils.getFormatedDatefromString(loadRequestDO.MovementDate);
		else
			date = CalendarUtils.getFormatedDatefromString_(loadRequestDO.MovementDate);

		writeText(String.format(columns2, "Requested No: " + loadRequestDO.MovementCode, "Date : " + date));
		writeText(String.format(columns2, "Requested By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Sub-Inventory : " + subInventory));
		writeText(lines);

		writeText(String.format(formateHeader, "", "SR#", "ITEM CODE", "ITEM DESCRIPTION", "UOM", "Req. Qty", "App. Qty"));
		writeText(lines);

		totalPrice = 0;
		int count = 1;
		//		footer_height=8
		int footerTotal = 2;
		int signature_height=3;
		for (int i = linenumber; signature_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecOrdProduct.size(); i = (++linenumber)) {
//		for (int i = linenumber; footer_height + (AppConstants.MAX_PRINT_LENGTH - maxPrintLenth) < AppConstants.MAX_PRINT_LENGTH && i < vecOrdProduct.size(); i = (++linenumber)) {
			VanLoadDO vanLoadDO = vecOrdProduct.get(i);
			float qty = 0;
			//if(loadRequestDO != null && (loadRequestDO.MovementStatus.contains(""+AppConstants.APPROVED_MOVEMENT_STATUS) || loadRequestDO.MovementStatus.contains("100")))
			if (loadRequestDO != null && (loadRequestDO.MovementStatus.contains(LoadRequestDO.STATUS_APPROVED_FROM_ERP) || loadRequestDO.MovementStatus.contains(LoadRequestDO.STATUS_SHIPPED)))
				if (movementType != AppStatus.LOAD_STOCK)
					qty = vanLoadDO.inProccessQty;
				else
					qty = vanLoadDO.ShippedQuantity;

			writeText(String.format(formateHeader, "", "" + (linenumber + 1), vanLoadDO.ItemCode, vanLoadDO.Description, vanLoadDO.UOM, "" + deffStock.format(vanLoadDO.SellableQuantity), "" + deffStock.format(qty)));
			if (loadRequestDO.MovementType.equalsIgnoreCase("" + AppStatus.UNLOAD_STOCK))
//				writeText(  String.format(formatReason,"","Ex. Date: "+arrInventory.get(i).expiryDate,"Reason: "+arrInventory.get(i).reason, "") );
				writeText(String.format(formatReason, "", "Ex. Date: " + vanLoadDO.ExpiryDate, "Reason: " + vanLoadDO.reason, ""));
		}
		writeText(lines);


//		if (footer_height + (AppConstants.MAX_PRINT_LENGTH - maxPrintLenth) >= AppConstants.MAX_PRINT_LENGTH) {
		if (linenumber<vecOrdProduct.size()) {

			printFooter( 3, "Storekeeper Signature", "Salesman Signature");
			giveDelay();
			printLoadInventory(vecOrdProduct, loadRequestDO, strMovementId, movementType, linenumber);

		} else {
			printFooter( 3, "Storekeeper Signature", "Salesman Signature");
		}


	}

	public void printPaymentInVoiceReceipt2(JourneyPlanDO mallsDetails, String strType, PaymentHeaderDO objPaymentDO, int linenumber) {
		/*maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines = "================================================================================\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formateHeader = "%1$-2.2s %2$-5.5s %3$-13.13s %4$17.17s %5$4.4s %6$4.4s %7$7.7s %8$-7.7s%9$-7.7s \r\n";
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatInvoice = "%1$16.16s %2$16.16s %3$16.16s %4$16.16s\r\n";

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
		//---------------------------
		writeText(String.format(formatHeader, "", strType.toUpperCase(), ""));
		writeText(lines);


		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = objPaymentDO.ReceiptId;

		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = preference.getStringFromPreference(Preference.RECIEPT_NO, "");

		writeText(String.format(columns2, "Receipt Number: " + strReceiptNo, "Date : " + mydate));

		writeText(lines);
		writeText(String.format(formatInvoice, "Invoice Number", "Total Amount", "Paid Amount", "Balance Amount"));
		writeText(lines);
		float ftBalDue = 0.0f;
		if (objPaymentDO.vecPaymentInvoices != null)
			for (int i = 0; i < objPaymentDO.vecPaymentInvoices.size(); i++) {
				PaymentInvoiceDO paymentInvoiceDO = objPaymentDO.vecPaymentInvoices.get(i);
				float ftTotal = StringUtils.getFloat(paymentInvoiceDO.totalAmt);
				float ftPaid = StringUtils.getFloat(paymentInvoiceDO.Amount);
				float balance = ftTotal - ftPaid;
				ftBalDue += ftPaid;

				writeText(String.format(formatInvoice, paymentInvoiceDO.TrxCode, deffAmt.format(ftTotal), deffAmt.format(ftPaid), deffAmt.format(balance)));
			}
		writeText(lines);
		writeText(String.format(columns2, "Total ", deffAmt.format(ftBalDue) + " " + mallsDetails.currencyCode));
		writeText(lines);


		int footer_height = 4;
		int footerTotal = 2;

		if (objPaymentDO != null && objPaymentDO.vecPaymentDetails != null && objPaymentDO.vecPaymentDetails.size() > 0) {
//			for(PaymentDetailDO paymentDetailDO : objPaymentDO.vecPaymentDetails)
			for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < objPaymentDO.vecPaymentDetails.size(); i++, linenumber++) {
				PaymentDetailDO paymentDetailDO = objPaymentDO.vecPaymentDetails.get(i);
				if (paymentDetailDO.PaymentTypeCode.equalsIgnoreCase("CHEQUE")) {
					writeText(String.format(columns2, "Payment Type            : ", paymentDetailDO.PaymentTypeCode));
					writeText(String.format(columns2, "Cheque No               : ", paymentDetailDO.ChequeNo));
					writeText(String.format(columns2, "Bank Name               : ", paymentDetailDO.BankName));
					writeText(String.format(columns2, "Cheque Date             : ", CalendarUtils.getFormatedDatefromStringPrint(paymentDetailDO.ChequeDate)));
					writeText(String.format(columns2, "Amount                  : ", paymentDetailDO.Amount + " " + curencyCode + ""));
				} else {
					writeText(String.format(columns2, "Payment Type            : ", paymentDetailDO.PaymentTypeCode));
					writeText(String.format(columns2, "Amount                  : ", paymentDetailDO.Amount));
					writeText("\r\n");
				}
				writeText(lines);
			}
		}
//		float ftBalDue = 0f ;

//		writeText(String.format(price,"","","Customer Signature") );
		////----------------------

		writeText(lines);
		if (linenumber < objPaymentDO.vecPaymentDetails.size()) {
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}

			writeText("\r\n\r\n"); //2
			writeText(String.format(price, "", "", "Customer Signature"));
			writeText(lines);//1
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n "); //spaces for (header +footer)
			giveDelay();
			printPaymentInVoiceReceipt2(mallsDetails, strType, objPaymentDO, linenumber);

		} else {

			//--------------
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height - footerTotal);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}
			writeText("\r\n\r\n"); //2
			writeText(String.format(price, "", "", "Customer Signature"));
			writeText(lines); //1
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
		}*/
		write("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
//		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";


		String lines = "------------------------------" +
				"------------------------------" +
				"------------------------------" +
				"------------------------------" +
				"---------\r\n" ;
//		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
//		String formateHeader = "%1$-2.2s %2$-5.5s %3$-13.13s %4$17.17s %5$4.4s %6$4.4s %7$7.7s %8$-7.7s%9$-7.7s \r\n";
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
//		String formatInvoice = "%1$16.16s %2$16.16s %3$16.16s %4$16.16s\r\n";

		String formatHeader = "%1$-55.55s %2$-41.41s  %3$-3.3s\r\n";
		String columns2 = "%1$-64.64s %2$-64.64s\r\n";
		String formatInvoice = "%1$32.32s%2$32.32s%3$32.32s%4$32.32s\r\n";

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
		//---------------------------
		writeText(String.format(formatHeader, "", strType.toUpperCase(), ""));
		writeText(lines);


		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = objPaymentDO.ReceiptId;

		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = preference.getStringFromPreference(Preference.RECIEPT_NO, "");

		writeText(String.format(columns2, "Receipt Number: " + strReceiptNo, "Date : " + mydate));

		writeText(lines);
		writeText(String.format(formatInvoice, "Invoice Number", "Total Amount", "Paid Amount", "Balance Amount"));
		writeText(lines);
		float ftBalDue = 0.0f;
		if (objPaymentDO.vecPaymentInvoices != null)
			for (int i = 0; i < objPaymentDO.vecPaymentInvoices.size(); i++) {
				PaymentInvoiceDO paymentInvoiceDO = objPaymentDO.vecPaymentInvoices.get(i);
				float ftTotal = StringUtils.getFloat(paymentInvoiceDO.totalAmt);
				float ftPaid = StringUtils.getFloat(paymentInvoiceDO.Amount);
				float balance = ftTotal - ftPaid;
				ftBalDue += ftPaid;

				writeText(String.format(formatInvoice, paymentInvoiceDO.TrxCode, deffAmt.format(ftTotal), deffAmt.format(ftPaid), deffAmt.format(balance)));
			}
		writeText(lines);
		writeText(String.format(columns2, "Total ", deffAmt.format(ftBalDue) + " " + mallsDetails.currencyCode));
		writeText(lines);


		int footer_height = 4;
		int footerTotal = 2;

		if (objPaymentDO != null && objPaymentDO.vecPaymentDetails != null && objPaymentDO.vecPaymentDetails.size() > 0) {
//			for(PaymentDetailDO paymentDetailDO : objPaymentDO.vecPaymentDetails)
			for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < objPaymentDO.vecPaymentDetails.size(); i++, linenumber++) {
				PaymentDetailDO paymentDetailDO = objPaymentDO.vecPaymentDetails.get(i);
				if (paymentDetailDO.PaymentTypeCode.equalsIgnoreCase("CHEQUE")) {
					writeText(String.format(columns2, "Payment Type            : ", paymentDetailDO.PaymentTypeCode));
					writeText(String.format(columns2, "Cheque No               : ", paymentDetailDO.ChequeNo));
					writeText(String.format(columns2, "Bank Name               : ", paymentDetailDO.BankName));
					writeText(String.format(columns2, "Cheque Date             : ", CalendarUtils.getFormatedDatefromStringPrint(paymentDetailDO.ChequeDate)));
					writeText(String.format(columns2, "Amount                  : ", paymentDetailDO.Amount + " " + curencyCode + ""));
				} else {
					writeText(String.format(columns2, "Payment Type            : ", paymentDetailDO.PaymentTypeCode));
					writeText(String.format(columns2, "Amount                  : ", paymentDetailDO.Amount));
					writeText("\r\n");
				}
				writeText(lines);
			}
		}
//		float ftBalDue = 0f ;

//		writeText(String.format(price,"","","Customer Signature") );
		////----------------------

		writeText(lines);
		if (linenumber < objPaymentDO.vecPaymentDetails.size()) {
			/*try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}*/
			printLines(maxPrintLenth  - footer_height-FOOTER_HEIGHT);
			printLines(2);
			writeText(String.format(price, "", "", "Customer Signature"));
			writeText(lines);//1
			printToEnd();
			write(" \r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer) //END
			printPaymentInVoiceReceipt2(mallsDetails, strType, objPaymentDO, linenumber);

		} else {

			//--------------
			/*try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height - footerTotal);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}*/
			printLines( maxPrintLenth  - footer_height   -FOOTER_HEIGHT);
			printLines(2);
			writeText(String.format(price, "", "", "Customer Signature"));
			writeText(lines); //1
			printToEnd();
			write(" \r\n\r\n\r\n\r\n\r\n\r\n");//END
//			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
		}
	}
	public void printReceiptOnly(JourneyPlanDO mallsDetails, String strType, PaymentHeaderDO objPaymentDO, int linenumber) {
//		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$-55.55s %2$-41.41s  %3$-3.3s\r\n";
		String columns2 = "%1$-64.64s %2$-64.64s\r\n";
		String formatInvoice = "%1$32.32s%2$32.32s%3$32.32s%4$32.32s\r\n";

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
		//---------------------------
		writeText(String.format(formatHeader, "", strType.toUpperCase(), ""));
		String lines = "-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- \r\n" ;
		writeText(lines);


		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = objPaymentDO.ReceiptId;

		if (strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = preference.getStringFromPreference(Preference.RECIEPT_NO, "");

		writeText(String.format(columns2, "Receipt Number: " + strReceiptNo, "Date : " + mydate));

		writeText(lines);
		writeText(String.format(formatInvoice, "Invoice Number", "Total Amount", "Paid Amount", "Balance Amount"));
		writeText(lines);
		float ftBalDue = 0.0f;
		if (objPaymentDO.vecPaymentInvoices != null)
			for (int i = 0; i < objPaymentDO.vecPaymentInvoices.size(); i++) {
				PaymentInvoiceDO paymentInvoiceDO = objPaymentDO.vecPaymentInvoices.get(i);
				float ftTotal = StringUtils.getFloat(paymentInvoiceDO.totalAmt);
				float ftPaid = StringUtils.getFloat(paymentInvoiceDO.Amount);
				float balance = ftTotal - ftPaid;
				ftBalDue += ftPaid;

				writeText(String.format(formatInvoice, paymentInvoiceDO.TrxCode, deffAmt.format(ftTotal), deffAmt.format(ftPaid), deffAmt.format(balance)));
			}
		writeText(lines);
		writeText(String.format(columns2, "Total ", deffAmt.format(ftBalDue) + " " + mallsDetails.currencyCode));
		writeText(lines);


		int footer_height = 4;
		int footerTotal = 2;

		if (objPaymentDO != null && objPaymentDO.vecPaymentDetails != null && objPaymentDO.vecPaymentDetails.size() > 0) {
//			for(PaymentDetailDO paymentDetailDO : objPaymentDO.vecPaymentDetails)
			for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < objPaymentDO.vecPaymentDetails.size(); i++, linenumber++) {
				PaymentDetailDO paymentDetailDO = objPaymentDO.vecPaymentDetails.get(i);
				if (paymentDetailDO.PaymentTypeCode.equalsIgnoreCase("CHEQUE")) {
					writeText(String.format(columns2, "Payment Type            : ", paymentDetailDO.PaymentTypeCode));
					writeText(String.format(columns2, "Cheque No               : ", paymentDetailDO.ChequeNo));
					writeText(String.format(columns2, "Bank Name               : ", paymentDetailDO.BankName));
					writeText(String.format(columns2, "Cheque Date             : ", CalendarUtils.getFormatedDatefromStringPrint(paymentDetailDO.ChequeDate)));
					writeText(String.format(columns2, "Amount                  : ", paymentDetailDO.Amount + " " + curencyCode + ""));
				} else {
					writeText(String.format(columns2, "Payment Type            : ", paymentDetailDO.PaymentTypeCode));
					writeText(String.format(columns2, "Amount                  : ", paymentDetailDO.Amount));
					writeText("\r\n");
				}
				writeText(lines);
			}
		}
	}

	public void printLPOOrder(boolean isSummary, Vector<ProductDO> vecSalesOrderProducts, JourneyPlanDO mallsDetails, OrderDO orderDO, int linenumber, float totalPrice) {

		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines = "================================================================================\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formateHeader = "%1$-2.2s %2$-10.10s %3$-13.13s %4$17.17s %5$7.7s %6$7.7s %7$9.9s %8$-9.9s \r\n";//73
		String formateHeader2 = "%1$-2.2s %2$-10.10s %3$-13.13s %4$27.27s %5$7.7s %6$7.7s  \r\n";//73
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatPriceBold = "%1$-10.10s %2$-17s";
		String formatPriceNormal = "%1$-10.10s %2$46s";
		String formatInvoice = "%1$18.18s %2$18.18s %3$18.18s %4$18.18s\r\n";
		String LINE = "%1$-2.2s %2$-75.75s\r\n";

		writeText(String.format(formatHeader, "", "DELIVERY NOTE", ""));
		writeText(String.format(columns2, "Invoice No: " + orderDO.OrderId, ""));
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
		writeText(String.format(columns2, "BILL TO:", "SHIP TO:"));
		writeText(String.format(columns2, mallsDetails.partyName, mallsDetails.siteName));
		writeText(String.format(columns2, "Customer No: " + mallsDetails.customerId, "Site No: " + mallsDetails.site));
		if (mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss1, mallsDetails.addresss1));
		if (mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss2, mallsDetails.addresss2));
		if (mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss3, mallsDetails.addresss3));
		if (mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			writeText(String.format(columns2, mallsDetails.poNumber, mallsDetails.poNumber));
		if (mallsDetails.city != null && mallsDetails.city.length() > 0)
			writeText(String.format(columns2, mallsDetails.city, mallsDetails.city));
		if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Salesman : " + mallsDetails.salesmanName));
		else
			writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""));
		writeText(String.format(columns2, "Invoice No: " + orderDO.OrderId, "Delivery Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM));
		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");

		if (!TextUtils.isEmpty(orderDO.LPOCode)) {
			writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "LPO : " + orderDO.LPOCode));
			writeText(String.format(columns2, "Sales Person Mobile : " + printMobileNumber(mallsDetails), ""));
		} else
			writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "Sales Person Mobile : " + printMobileNumber(mallsDetails)));
		writeText("\r\n");
		writeText(lines);
		writeText(String.format(formateHeader2, "", "SR#", "ITEM CODE", "DESCRIPTION", "UOM", "QTY"));
		writeText(lines);

		int footer_height = 4;
		int footerTotal = 0;

		if (!isSummary) {
//			for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
			for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecSalesOrderProducts.size(); i++, linenumber++)
				writeText(String.format(formateHeader2, "", "" + (i + 1), vecSalesOrderProducts.get(i).SKU, vecSalesOrderProducts.get(i).Description, "" + vecSalesOrderProducts.get(i).UOM, vecSalesOrderProducts.get(i).preUnits));
		} else {
//			for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
			for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecSalesOrderProducts.size(); i++, linenumber++)
				writeText(String.format(formateHeader2, "", "" + (i + 1), vecSalesOrderProducts.get(i).SKU, vecSalesOrderProducts.get(i).Description, "" + vecSalesOrderProducts.get(i).UOM, vecSalesOrderProducts.get(i).units));
		}
		//-------------------
		//---------------------
		writeText(lines);
		if (linenumber < vecSalesOrderProducts.size()) {
			try {
				linePrinter.newLine(maxPrintLenth + -footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}

			writeText("\r\n\r\n");
			writeText(String.format(columns2, "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)"));
			writeText(lines);
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n ");
			giveDelay();
			printLPOOrder(isSummary, vecSalesOrderProducts, mallsDetails, orderDO, linenumber, totalPrice);
		} else {

			//--------------
			try {
				linePrinter.newLine(maxPrintLenth - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}
			writeText("\r\n\r\n");
			writeText(String.format(columns2, "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)"));
			writeText(lines);
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
		}


//		writeText( "\r\n\r\n\r\n\r\n" );
//		writeText(String.format(columns2 ,"REMARKS","") );
//		writeText(lines);
//		writeText(String.format(LINE  ,"","1. Received complete invoiced quantity in good condition.") );
//		writeText(String.format(LINE  ,"","2. Official receipt is mandatory for payments.") );
//		if(mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
//			writeText(String.format(LINE  ,"","3. Check should be issued in favor of ?Galadari Ice Cream Co. Ltd. (L.L.C)?.") );

	}

	public void printReplacementOrderSummary(Vector<ProductDO> vecSalesOrderProducts, String type, String strOrderId, JourneyPlanDO mallsDetails, String from, int linenumber, boolean isPDUser) {
//		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
//		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
//		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
//		String format 			= "%1$-15s %2$-4s %3$-25.25s %4$-25.25s %5$-5.5s %6$-5.5s \r\n";
//		String formatNewDesc 	= "%1$-15s %2$-25.25s %3$-25.25s \r\n";
//		String formater			= "%1$-10.10s";
//		String lines 			= "====================================================================================\r\n";
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formatPriceBold = "%1$-10.10s %2$-17s";
		String formatPriceNormal = "%1$-10.10s %2$46s";
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatReason = "%1$-10.10s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";

		String formateHeader = "%1$-2.2s %2$-10.10s %3$-17.17s %4$7.7s %5$7.7s %6$7.7s %7$7.7s %8$-7.7s\r\n";//73
		String lines = "================================================================================\r\n";

		String format = "%1$-2.2s %2$-4.4s %3$-25.25s %4$-25.25s %5$-5.5s %6$-5.5s \r\n";
		String formatNewDesc = "%1$-6.6s %2$-25.25s %3$-25.25s \r\n";

		writeText(String.format(formatHeader, "", "REPLACEMENT ORDER", ""));
		writeText(String.format(formatHeader, "", "DUPLICATE COPY", ""));


		writeText(String.format(columns2, "Repl. No: " + strOrderId, ""));

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";

		writeText(String.format(columns2, "BILL TO:", "SHIP TO:"));
		writeText(String.format(columns2, mallsDetails.siteName, mallsDetails.partyName));
		writeText(String.format(columns2, "Customer No: " + mallsDetails.customerId, "Site No: " + mallsDetails.site));

		if (mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss1, mallsDetails.addresss1));

		if (mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss2, mallsDetails.addresss2));

		if (mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss3, mallsDetails.addresss3));

		if (mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss4, mallsDetails.addresss4));

		if (mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			writeText(String.format(columns2, mallsDetails.poNumber, mallsDetails.poNumber));

		if (mallsDetails.city != null && mallsDetails.city.length() > 0)
			writeText(String.format(columns2, mallsDetails.city, mallsDetails.city + "\r\n"));

		if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Salesman : " + mallsDetails.salesmanName));
		else
			writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""));

		writeText(String.format(columns2, "Repl. No: " + strOrderId, "Repl. Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM));

		writeText(lines);
		int footer_height = 4;
		int footerTotal = 0;

//		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecSalesOrderProducts.size(); i++, linenumber++) {
			ProductDO productDO = vecSalesOrderProducts.get(i);
			writeText(String.format(format, "", "" + (i + 1), productDO.SKU, productDO.RelatedLineId, productDO.UOM, "" + productDO.units));
			writeText(String.format(formatNewDesc, "", productDO.Description, productDO.Description1));
		}
		writeText(lines);


		//---------------------
		writeText(lines);
		if (linenumber < vecSalesOrderProducts.size()) {
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}

			writeText("\r\n\r\n"); //2
			if (isPDUser)
				writeText(String.format(columns2, "Receiverd By", "Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"));
			else
				writeText(String.format(columns2, "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"));
			writeText(lines);
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n ");
			giveDelay();
			printReplacementOrderSummary(vecSalesOrderProducts, type, strOrderId, mallsDetails, from, linenumber, isPDUser);
		} else {


			//--------------
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}
			writeText("\r\n\r\n"); //2
			if (isPDUser)
				writeText(String.format(columns2, "Receiverd By", "Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"));
			else
				writeText(String.format(columns2, "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"));
			writeText(lines); //1
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
		}


//		writeText( "\r\n\r\n\r\n\r\n" );
//		writeText(String.format(columns2 ,"REMARKS","") );
//		writeText(lines);
//		writeText(String.format(LINE  ,"","1. Received complete invoiced quantity in good condition.") );
//		writeText(String.format(LINE  ,"","2. Official receipt is mandatory for payments.") );


	}


	/*public void printARPaymentSummary(String from, ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice, String strSelectedDateToPrint, String type, int linenumber) {

//		String formatHeader 	= "%1$-16.16s %2$-41.41s %3$-3.3s";
//		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
//		String format 			= "%1$-10.10s %2$-25.25s %3$-10.10s %4$-14.14s %5$-10.10s %6$12.12s\r\n";
//		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
//		String lines 			= "------------------------------------------------------------------------------------\r\n";
//		String singleLine       = "------------------------------------------------------------------------------------";
//		String dividerLine      = "|                                    |";
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines = "================================================================================\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formateHeader = "%1$-2.2s %2$-10.10s %3$-13.13s %4$17.17s %5$7.7s %6$7.7s %7$9.9s %8$-9.9s\r\n";//73
		String formateHeader3 = "%1$-2.2s %2$-3.3s %3$-20.20s %4$20.20s %5$7.7s %6$7.7s\r\n";//73
		String formateHeader2 = "%1$-2.2s %2$-10.10s %3$-13.13s %4$27.27s %5$7.7s %6$7.7s  \r\n";//73
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatReason = "%1$-24.24s %2$-24.24s %3$-24.24s\r\n";
		String formatPriceBold = "%1$-10.10s %2$-17s";
		String formatPriceNormal = "%1$-10.10s %2$46s";
//		String format 			= "%1$-2.2s %2$-5.5s %3$-15.15s %4$34.34s %5$7.7s %6$7.7s\r\n";
		String format = "%1$-25.25s %2$-15.15s %3$-15.15s %4$-14.14s %5$-10.10s\r\n";


		writeText(String.format(formatHeader, "", "RECEIPT SUMMARY", ""));

//		printHeader();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

		writeText(String.format(columns2, "User Name:" + preference.getStringFromPreference(Preference.USER_NAME, ""), "Emp No.: " + preference.getStringFromPreference(Preference.EMP_NO, "")));
		writeText(String.format(columns2, "Collected Date: " + (strSelectedDateToPrint != null ? strSelectedDateToPrint : mydate), "Print Date: " + mydate));

		writeText("\r\n");
		writeText(lines);
		boolean isCash = false, isCheque = false;
		if (arrayListCustomerInvoice != null && arrayListCustomerInvoice.size() > 0) {
			for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
				if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					isCheque = true;
				else if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
					isCash = true;
			}
		}

		String currencyCode = "";
		if (isCheque) {
			writeText(String.format(format, "Site Name", "Site No.", "Receipt No.", "Type", "Amount"));
			writeText(lines);
			Float strTotalAmount = 0f;
			if (arrayListCustomerInvoice != null && arrayListCustomerInvoice.size() > 0) {
				for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
					if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque")) {
						float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
						strTotalAmount += amount;
						writeText(String.format(format, cuInvoiceDO.siteName, cuInvoiceDO.customerSiteId, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, deffAmt.format(amount)));

						if (currencyCode == null || currencyCode.length() <= 0)
							currencyCode = cuInvoiceDO.currencyCode;
					}
				}

				if (strTotalAmount > 0) {
					writeText(lines);
				}
			}
		}
		if (isCash) {
			Float strTotalCashAmount = 0f;
			writeText(String.format(format, "Site Name", "Site No.", "Receipt No.", "Type", "Amount"));
			writeText(lines);
			for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
				if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash")) {
					float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
					strTotalCashAmount += amount;
					writeText(String.format(format, cuInvoiceDO.siteName, cuInvoiceDO.customerSiteId, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, deffAmt.format(amount)));

					if (currencyCode == null || currencyCode.length() <= 0)
						currencyCode = cuInvoiceDO.currencyCode;
				}
			}
			if (strTotalCashAmount > 0) {
				writeText(lines);
				writeText(String.format(columns2, "Cash Total: ", "", "", currencyCode, deffAmt.format(strTotalCashAmount)));
				writeText(lines);
			}
		}
		writeText("\r\n");
		writeText(String.format(columns2, "AR Department", "Salesman"));
		writeText("\r\n\r\n\r\n");
		writeText(lines);
//		printFooter();
	}*/

	public void PrintSalesOrder(Vector<ProductDO> vecSalesOrderProducts, String type, String strOrderId, String lpo, float roundOffVal, JourneyPlanDO mallsDetails, float totalPrice, float totalDiscount, int linenumber, String TRN, float totNetAmt, float totalTaxAmt) {
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;

//		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
//		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
//		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
//		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-25.25s %5$5.5s %6$-4.4s %7$6.6s %8$6.6s %9$8.8s \r\n";
//		String formatPriceNormal= "%1$-10.10s %2$46s";
//		String formater			= "%1$-10.10s";
//		String formatPriceBold  = "%1$-10.10s %2$-17s";
//		String price 			= "%1$-10.10s %2$-38.38s %3$38.38s\r\n";
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines = "================================================================================\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
//		String formateHeader = "%1$-2.2s %2$-5.5s %3$-13.13s %4$17.17s %5$4.4s %6$4.4s %7$7.7s %8$-7.7s%9$-7.7s \r\n";
		String formateHeader = "%1$-2.2s %2$-4.4s %3$-11.11s %4$10.10s %5$3.3s %6$4.4s %7$6.6s %8$-6.6s %9$5.5s %10$-5.5s %11$-7.7s \r\n";
//		String format1 			= "%1$-4.4s %2$-3.3s %3$-10.10s %4$-18.18s %5$4.4s %6$-4.4s %7$-6.6s %8$-6.6s %9$-6.6s %10$-6.6s %11$-7.7s \r\n";

		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatPriceBold = "%1$-10.10s %2$-17s";
		String formatPriceNormal = "%1$-10.10s %2$46s";
		String formatInvoice = "%1$18.18s %2$18.18s %3$18.18s %4$18.18s\r\n";
		String LINE = "%1$-2.2s %2$-75.75s\r\n";

		String strType = "RECEIPT DETAIL";
		String formatPriceNormal01 = "%1$-7.7s %2$-50.50s \r\n";
//		String formatInvoice 	= "%1$10.10s %2$15.15s %3$15.15s %4$15.15s %5$15.15s\r\n";
		String totalAmt = "%1$-10.10s %2$-38.38s %3$31.31s\r\n";

		if (mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
//			writeText(String.format(formatHeader, "", "CASH INVOICE", ""));
			writeText(String.format(formatHeader, "", "TAX INVOICE", ""));
		else
//			writeText(String.format(formatHeader, "", "CREDIT INVOICE", ""));
			writeText(String.format(formatHeader, "", "TAX INVOICE", ""));

		if (TextUtils.isEmpty(strOrderId))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");

		if (TRN.equalsIgnoreCase(""))
//			woosim.saveSpool(EUC_KR, String.format(INV,"","N/A"), 0x09, true);
			writeText(String.format(formatHeader, "", "N/A", ""));
		else
//			woosim.saveSpool(EUC_KR, String.format(INV,"",""+TRN), 0x09, true);
			writeText(String.format(formatHeader, "", "" + TRN, ""));

		writeText(String.format(formatHeader, "", "Tax Invoice No: " + strOrderId, ""));
//		woosim.saveSpool(EUC_KR, String.format(INV,"","Tax Invoice No: "+strOrderId), 0x09, true);

//		writeText(String.format(columns2, "Invoice No: " + strOrderId, ""));

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";

		writeText(String.format(columns2, "BILL TO:", "SHIP TO:"));
		writeText(String.format(columns2, mallsDetails.partyName, mallsDetails.siteName));
		writeText(String.format(columns2, "Customer No: " + mallsDetails.customerId, "Site No: " + mallsDetails.site));

		if (mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss1, mallsDetails.addresss1));

		if (mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss2, mallsDetails.addresss2));

		if (mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss3, mallsDetails.addresss3));

		if (mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			writeText(String.format(columns2, mallsDetails.poNumber, mallsDetails.poNumber));

		if (mallsDetails.city != null && mallsDetails.city.length() > 0)
			writeText(String.format(columns2, mallsDetails.city, mallsDetails.city));

		if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Salesman : " + mallsDetails.salesmanName));
		else
			writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""));

		writeText(String.format(columns2, "Invoice No: " + strOrderId, "Invoice Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM));

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");

		if (!TextUtils.isEmpty(LPO)) {
			writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "LPO : " + LPO));
			writeText(String.format(columns2, "Sales Person Mobile : " + printMobileNumber(mallsDetails), "Delivery/Supply Date :" + CalendarUtils.getCurrentDate()));
//			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sales Person Mobile : "+ printMobileNumber(mallsDetails), "Delivery/Supply Date :"+CalendarUtils.getCurrentDate()), 0, true);


		} else
			writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "Sales Person Mobile : " + printMobileNumber(mallsDetails)));


		writeText("\r\n");

		writeText(lines);
//		writeText(String.format(formateHeader, "", "SR#", "ITEM CODE", "DESCRIPTION", "QTY", "UOM", "RATE", "DISC", "AMOUNT"));
//		woosim.saveSpool(EUC_KR,String.format(format1,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","VAT","VAT","AMOUNT"), 0, true);

		writeText(String.format(formateHeader, "", "SR#", "ITEM CODE", "DESCRIPTION", "QTY", "UOM", "RATE", "DISC", "VAT", "VAT", "AMOUNT"));
		writeText(String.format(formateHeader, "", "", " ", "", "", "", "", "", "Rate", "Amount", ""));
		writeText(lines);

		int footer_height = 4;
//		int footerTotal = 10;// 2 lines are reduced
		int footerTotal = 8;

		totalTaxAmt = 0;
		totNetAmt = 0;

//		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecSalesOrderProducts.size(); i++, linenumber++) {
			{
				ProductDO productDO = vecSalesOrderProducts.get(i);


				totalPrice += (productDO.invoiceAmount - productDO.LineTaxAmount);
				totNetAmt += (productDO.invoiceAmount);
				totalTaxAmt += productDO.LineTaxAmount;
				totalDiscount += productDO.DiscountAmt * StringUtils.getFloat(productDO.preUnits);

//				woosim.saveSpool(EUC_KR,String.format(format1,"",""+(i+1),productDO.SKU,"",""+productDO.units,productDO.UOM,""+deffAmt.format(productDO.itemPrice),""+deffAmt.format(productDO.DiscountAmt*StringUtils.getDouble(productDO.units)),/*productDO.DiscountAmt*/""+deffAmt.format(productDO.TaxPercentage)+"%",""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.invoiceAmount)), 0, false);
//				writeText(String.format(formateHeader, "", "" + (i + 1), productDO.SKU, productDO.Description, "" + productDO.units, productDO.UOM, "" + deffAmt.format(productDO.itemPrice), "" + deffAmt.format(productDO.DiscountAmt * StringUtils.getFloat(productDO.preUnits)), "" + deffAmt.format(productDO.invoiceAmount)));
				writeText(String.format(formateHeader, "", "" + (i + 1), productDO.SKU, productDO.Description, "" + productDO.units, productDO.UOM, "" + deffAmt.format(productDO.itemPrice), "" + deffAmt.format(productDO.DiscountAmt * StringUtils.getFloat(productDO.preUnits)), "" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.LineTaxAmount), "" + deffAmt.format(productDO.invoiceAmount)));
				writeText(String.format(formatPriceNormal01, "", "" + productDO.Description));
			}
		}
		//---------------------
		writeText(lines);
		if (linenumber < vecSalesOrderProducts.size()) {
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}

			writeText("\r\n\r\n"); //2
			writeText(String.format(price, "", "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)"));
			writeText(lines);
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n ");
			PrintSalesOrder(vecSalesOrderProducts, type, strOrderId, lpo, roundOffVal, mallsDetails, totalPrice, totalDiscount, linenumber, TRN, totNetAmt, totalTaxAmt);


		} else {

			writeText(String.format(price, "", "Total Gross Amount", deffAmt.format(totalPrice + totalDiscount) + " " + mallsDetails.currencyCode));
			writeText(String.format(price, "", "Total Discount Amount", deffAmt.format(totalDiscount) + " " + mallsDetails.currencyCode));

//			writeText(String.format(price, "", "Total Amount(with discount)", deffAmt.format(totalPrice + totalDiscount - totalDiscount) + " " + mallsDetails.currencyCode));
			writeText(String.format(price, "", "Total VAT Amount", deffAmt.format(totalTaxAmt) + " " + mallsDetails.currencyCode));
//			writeText(String.format(price, "", "Taxed Amount ", deffAmt.format(totalPrice + totalDiscount - totalDiscount + VATamount) + " " + mallsDetails.currencyCode));
			writeText(String.format(price, "", "Round Off Amount", deffAmt.format(roundOffVal) + " " + mallsDetails.currencyCode));
			writeText(String.format(price, "", "Total Amount", deffAmt.format(totNetAmt + roundOffVal) + " " + mallsDetails.currencyCode));
			writeText(lines);
			writeText(String.format(formatPriceBold, "", "Amount in Words:"));
			writeText(String.format(formatPriceNormal, "", new NumberToEnglish().changeCurrencyToWords("" + deffAmt.format(totNetAmt + roundOffVal))) + "\r\n");
			maxPrintLenth++;
			writeText(lines);
			//--------------
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}
			writeText("\r\n\r\n"); //2
			writeText(String.format(price, "", "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)"));
			writeText(lines); //1
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
		}
		//---------------

	/*	writeText("\r\n");
		writeText(lines);

		writeText( "\r\n\r\n\r\n\r\n" );
		writeText(String.format(columns2 ,"REMARKS","") );
		writeText(lines);
		writeText(String.format(LINE  ,"","1. Received complete invoiced quantity in good condition.") );
		writeText(String.format(LINE  ,"","2. Official receipt is mandatory for payments.") );

		if(mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
			writeText(String.format(LINE  ,"","3. Check should be issued in favor of ?Galadari Ice Cream Co. Ltd. (L.L.C)?.") );

*/
	}


	public void PrintMoveOrder(Vector<ProductDO> vecSalesOrderProducts, String type, String strOrderId, String lpo, float roundOffVal, JourneyPlanDO mallsDetails, float totalPrice, int linenumber) {

		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$68.68s\r\n";
		String lines = line;// "================================================================================\r\n";
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		String formateHeader2 = "%1$-2.2s%2$-3.3s%3$-30.30s%4$30.30s%5$30.30s%6$30.30s\r\n";//73



		setCompress();
		writeText(String.format(formatHeader,  "MOVE ORDER" ));

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";

		writeText(String.format(columns2, "BILL TO:", "SHIP TO:"));
		writeText(String.format(columns2, mallsDetails.partyName, mallsDetails.siteName));
		writeText(String.format(columns2, "Customer No: " + mallsDetails.customerId, "Site No: " + mallsDetails.site));


		if (mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss1, mallsDetails.addresss1));

		if (mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss2, mallsDetails.addresss2));

		if (mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss3, mallsDetails.addresss3));

		if (mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			writeText(String.format(columns2, mallsDetails.poNumber, mallsDetails.poNumber));

		if (mallsDetails.city != null && mallsDetails.city.length() > 0)
			writeText(String.format(columns2, mallsDetails.city, mallsDetails.city));

		writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""));

		if (strOrderId == null || strOrderId.equalsIgnoreCase(""))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");

		writeText(String.format(columns2, "Move Order No: " + strOrderId, "Order Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM));

		writeText("\r\n");

		writeText(lines);
		writeText(String.format(formateHeader2, "", "SR#", "ITEM CODE", "DESCRIPTION", "UOM", "QTY"));
		writeText(lines);

		int footer_height = 4;
		int footerTotal = 0;
		for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecSalesOrderProducts.size(); i++, linenumber++)
			writeText(String.format(formateHeader2, "", "" + (i + 1), vecSalesOrderProducts.get(i).SKU, vecSalesOrderProducts.get(i).Description, vecSalesOrderProducts.get(i).UOM, "" + vecSalesOrderProducts.get(i).preUnits));

		writeText(lines);
		//---------------------
		writeText(lines);
		if (linenumber < vecSalesOrderProducts.size()) {
			printFooter(footer_height,"Receiverd By","Galadari Ice Cream Co. Ltd. (L.L.C)" );
			giveDelay();
			PrintMoveOrder(vecSalesOrderProducts, type, strOrderId, lpo, roundOffVal, mallsDetails, totalPrice, linenumber);
		} else {
			printFooter(footer_height,"Receiverd By","Galadari Ice Cream Co. Ltd. (L.L.C)" );
		}
	}


	public void printEOTSummary(EOTSummaryPrintDO eotSummaryPrintDO, String type) {

		String formatStock = "%1$-1.1s%2$-4.4s%3$-30.30s%4$-30.30s%5$-30.30s%6$-30.30s\r\n";
		String format = "%1$-5.5s%2$-24.24s%3$-24.24s%4$-24.24s%5$-24.24s%6$24.24s\r\n";
		String formatNonSeleble = "%1$-1.1s%2$-4s%3$-30.30s%4$-30.30s%5$-30.30s%6$-30.30s\r\n";

		String formatReason = "%1$-5.5s%2$-40.40s%3$-40.40s%4$-40.40s\r\n";
		String formatReplacement = "%1$-2.2s%2$-8.8s%3$-22.22s4$-22.22s%5$-22.22s%6$-22.22s%7$22.22s\r\n";
		String formatNewDesc = "%1$-10.10s%2$-30.30s%3$-30.30s%4$-30.30s\r\n";

		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$68.68s\r\n";
		String lines = "--------------------------------------------------------------------------------\r\n";
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		String formatInvoice1 = "%1$5.5s%2$40.40s%3$40.40s%4$40.40s\r\n";

		setCompress();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		writeTxt(String.format(formatHeader,  "EOT SUMMARY" ));
//		woosim.saveSpool(EUC_KR, String.format(formatHeaderN,"",""+mydate,""), 0, true);
		writeTxt(String.format(columns2, "" + CalendarUtils.getCurrentDate() + " " + CalendarUtils.getCurrentTime(), ""));
		//woosim.saveSpool(EUC_KR, String.format(formatHeader,""+CalendarUtils.getCurrentDate(),""+CalendarUtils.getCurrentTime(),""), 0x09, true);


		writeTxt(String.format(columns2, "User Name:" + preference.getStringFromPreference(Preference.USER_NAME, ""), "Emp No.: " + preference.getStringFromPreference(Preference.EMP_NO, "")));

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		writeTxt(String.format(columns2, "Sub-Inventory : " + subInventory, ""));


		String currencyCode = "";
		if (eotSummaryPrintDO != null) {
			HashMap<String, Vector<OrderDO>> hmOrders = eotSummaryPrintDO.hmOrders;
			if (hmOrders != null && hmOrders.size() > 0) {

				Set<String> keys = hmOrders.keySet();
				int count = 0;
				if (keys != null && keys.size() > 0) {

					for (String string : keys) {
						String invoiceType = "";
						String TITLE = "";

						if (string.equalsIgnoreCase(AppConstants.HHOrder)) {
							invoiceType = "Sales Order";
							TITLE = "SALES ORDER SUMMARY";//20
						} else if (string.equalsIgnoreCase(AppConstants.RETURNORDER)) {
							invoiceType = "Return Order";
							TITLE = "RETURN ORDER SUMMARY";//20
						} else if (string.equalsIgnoreCase(AppConstants.LPO_ORDER)) {
							invoiceType = string;
							TITLE = "                    LPO ORDER SUMMARY";
						} else if (string.equalsIgnoreCase(AppConstants.HOLD_ORDER)) {
							invoiceType = string;
							TITLE = "                    HOLD ORDER SUMMARY";
						}
						double taxAmt=0.0f;
						Vector<OrderDO> vecOrderDOs = hmOrders.get(string);
						if (vecOrderDOs != null && vecOrderDOs.size() > 0) {
							if (!string.equalsIgnoreCase(AppConstants.REPLACEMETORDER)) {
								float totalAmt = 0;
								writeTxt(lines);
								writeTxt(String.format(formatHeader,   TITLE + "" ));
								writeTxt("\r\n");
								writeTxt(lines);
								writeTxt(String.format(formatInvoice1, "SR#", "INVOICE NUMBER", "CUSTOMER NAME", "AMOUNT"));
								count = 0;

								for (OrderDO orderDO : vecOrderDOs) {
									count++;
//									writeText(String.format(formatInvoice1, count + "", orderDO.OrderId + "", orderDO.strCustomerName + "", deffAmt.format(orderDO.TotalAmount) + ""));
									double onlyTaxAmt =0.0;
									if (string.equalsIgnoreCase(AppConstants.RETURNORDER))
									{
										onlyTaxAmt=-orderDO.VatAmount;
									}
									else {
										onlyTaxAmt = orderDO.VatAmount;

									}
									taxAmt += onlyTaxAmt;
									writeTxt(String.format(formatInvoice1, count + "", orderDO.OrderId + "", orderDO.strCustomerName + "", deffAmt.format(orderDO.TotalAmount+onlyTaxAmt) + ""));
									totalAmt += orderDO.TotalAmount;
								}

								if (currencyCode == null || currencyCode.length() <= 0)
									currencyCode = "SAR";

								if (totalAmt > 0) {
									writeTxt(lines);
//									writeText(String.format(formatInvoice1, "Total Amount: ", "", "", currencyCode, deffAmt.format(totalAmt)));

									writeTxt(String.format(format, "", "Total Amount: ", "", "",currencyCode, deffAmt.format(totalAmt+taxAmt)));
								}
							}
						}
					}
				}
			}

			if (eotSummaryPrintDO.vecReplaceOrder != null && eotSummaryPrintDO.vecReplaceOrder.size() > 0) {
				String TITLE = "REPLACEMENT ORDER SUMMARY"; //16

				writeTxt(lines);
				writeTxt(String.format(formatHeader,   TITLE + "" ));
				writeTxt(lines);
				writeTxt(String.format(formatReplacement, "", "SR#", "INVOICE NO.", "RECEIPT ITEM DESC", "ISSUE ITEM DESC", "UOM", "QTY"));

				int count = 0;
				for (ProductDO productDO : eotSummaryPrintDO.vecReplaceOrder) {
					count++;
					writeTxt(String.format(formatReplacement, "", "" + count, "" + productDO.OrderNo, productDO.SKU, productDO.RelatedLineId, productDO.UOM, "" + productDO.units));
					writeTxt(String.format(formatNewDesc, "", "", productDO.Description, productDO.Description1));
				}

				writeTxt(lines);
			}


			if (eotSummaryPrintDO.hmPayments != null && eotSummaryPrintDO.hmPayments.size() > 0) {
				writeTxt(lines);
				writeTxt(String.format(formatHeader,   "RECEIPT SUMMARY" ));
				writeTxt(lines);


				ArrayList<Customer_InvoiceDO> arr = new ArrayList<Customer_InvoiceDO>();
				Set<String> keys = eotSummaryPrintDO.hmPayments.keySet();
				for (String key : keys)
					arr.addAll(eotSummaryPrintDO.hmPayments.get(key));

				boolean isCash = false, isCheque = false;
				if (arr != null && arr.size() > 0) {
					for (Customer_InvoiceDO cuInvoiceDO : arr) {
						if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
							isCheque = true;
						else if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
							isCash = true;
					}
				}

				float totalCollections = 0;
				if (isCheque) {
					writeTxt(String.format(format, "", "Site Name", "Site No.", "Receipt No.", "Type", "Amount"));
					writeTxt(lines);
					float strTotalAmount = 0f;
					if (arr != null && arr.size() > 0) {
						for (Customer_InvoiceDO cuInvoiceDO : arr) {
							if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque")) {
								float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
								strTotalAmount += amount;
								writeTxt(String.format(format, "", cuInvoiceDO.siteName, cuInvoiceDO.customerSiteId, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, deffAmt.format(amount)));

								if (currencyCode == null || currencyCode.length() <= 0)
									currencyCode = cuInvoiceDO.currencyCode;
							}
						}

						if (strTotalAmount > 0) {
							totalCollections += strTotalAmount;
							writeTxt(lines);
							writeTxt(String.format(format, "", "Cheque Total: ", "", "", currencyCode, deffAmt.format(strTotalAmount)));
							writeTxt(lines);
						}
					}
				}
				if (isCash) {
					writeTxt(String.format(format, "", "Site Name", "Site No.", "Receipt No.", "Type", "Amount"));
					writeTxt(lines);
					float strTotalCashAmount = 0f;
					for (Customer_InvoiceDO cuInvoiceDO : arr) {
						if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash")) {
							float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
							strTotalCashAmount += amount;
							writeTxt(String.format(format, "", cuInvoiceDO.siteName, cuInvoiceDO.customerSiteId, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, deffAmt.format(amount)));

							if (currencyCode == null || currencyCode.length() <= 0)
								currencyCode = cuInvoiceDO.currencyCode;
						}
					}
					if (strTotalCashAmount > 0) {
						totalCollections += strTotalCashAmount;
						writeTxt(lines);
						writeTxt(String.format(format, "", "Cash Total: ", "", "", currencyCode, deffAmt.format(strTotalCashAmount)));
						writeTxt(lines);
					}
				}

				if (totalCollections > 0) {
					writeTxt(String.format(format, "", "Total Collections: ", "", "", currencyCode, deffAmt.format(totalCollections)));
					writeTxt(lines);
				}
			}

			if (eotSummaryPrintDO.vecInventoryItems != null && eotSummaryPrintDO.vecInventoryItems.size() > 0) {
				writeTxt(lines);
				writeTxt(String.format(formatHeader , "STOCK SUMMARY" ));
				writeTxt(lines);
				deffStock = new DecimalFormat("##.###");
				deffStock.setMinimumFractionDigits(3);
				deffStock.setMaximumFractionDigits(3);


				writeTxt(String.format(formatStock, "", "SR#", "ITEM CODE", "ITEM DESCRIPTION", "UOM", "Quantity"));

				totalPrice = 0;
				int count = 0;
				for (InventoryObject vnLoad : eotSummaryPrintDO.vecInventoryItems) {
					count++;
					writeTxt(String.format(formatStock, "", "" + count, vnLoad.itemCode, vnLoad.itemDescription, vnLoad.UOM, "" + deffStock.format(StringUtils.getDouble(vnLoad.availQty+""))));
				}


				writeTxt(lines);
			}

			if (eotSummaryPrintDO.vecNonInventoryItems != null && eotSummaryPrintDO.vecNonInventoryItems.size() > 0) {
				writeTxt("\r\n");
				writeTxt(lines);
				writeTxt(String.format(formatHeader,   "NON SALEABLE STOCK SUMMARY" ));
				writeTxt(lines);


				writeTxt(String.format(formatNonSeleble, "", "SR#", "ITEM CODE", "ITEM DESCRIPTION", "UOM", "Quantity"));
				for (int i = 0; eotSummaryPrintDO.vecNonInventoryItems != null && i < eotSummaryPrintDO.vecNonInventoryItems.size(); i++) {
					writeTxt(String.format(formatNonSeleble, "", "" + (i + 1), eotSummaryPrintDO.vecNonInventoryItems.get(i).itemCode, eotSummaryPrintDO.vecNonInventoryItems.get(i).itemDescription, eotSummaryPrintDO.vecNonInventoryItems.get(i).UOM, "" + deffStock.format(eotSummaryPrintDO.vecNonInventoryItems.get(i).PrimaryQuantity)));
					writeTxt(String.format(formatReason, "", "Exp. Date: " + eotSummaryPrintDO.vecNonInventoryItems.get(i).expiryDate, "Reason: " + eotSummaryPrintDO.vecNonInventoryItems.get(i).reason, ""));
				}
				writeTxt(lines);
			}


		}
		printFooter( 3 , " ", " ");
	}
	public void PrintLoad(ArrayList<VanLoadDO> vecOrdProduct, String strMovementId, int movementType, float totalPrice,int linenumber ) {

		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$68.68s";
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";

		String lines =line;
		String format = "%1$-1.1s%2$-4.4s%3$-30.30s%4$-30.30s%5$-30.30s%6$-30.30s\r\n";
		setCompress();

		write(String.format(formatHeader,  "STOCK VERIFICATION " ));
		writeText("\r\n");


		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());


		if (strMovementId == null || strMovementId.equalsIgnoreCase(""))
			strMovementId = "";

		writeText(String.format(columns2, "Movement Code: " + strMovementId, ""));

		writeText(String.format(columns2, "Verified By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Date: " + mydate.substring(0, mydate.lastIndexOf(":"))));
		writeText(lines);

		writeText(String.format(format, "", "SR#", "ITEM CODE", "ITEM DESCRIPTION", "UOM", "Quantity"));

		int signature_height=3;
		int footerTotal=0;
		if (movementType != AppStatus.LOAD_STOCK) {
			for (int i = linenumber; signature_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecOrdProduct.size(); i = (++linenumber)) {
//			for (int i = 0; vecOrdProduct != null && i < vecOrdProduct.size(); i++)
				writeText(String.format(format, "", "" + (i + 1), vecOrdProduct.get(i).ItemCode, vecOrdProduct.get(i).Description, vecOrdProduct.get(i).UOM, "" + deffAmt.format(vecOrdProduct.get(i).SellableQuantity)));
			}
		} else {
//			for (int i = 0; vecOrdProduct != null && i < vecOrdProduct.size(); i++)
			for (int i = linenumber; signature_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecOrdProduct.size(); i = (++linenumber)) {
				writeText(String.format(format, "", "" + (i + 1), vecOrdProduct.get(i).ItemCode, vecOrdProduct.get(i).Description, vecOrdProduct.get(i).UOM, "" + deffAmt.format(vecOrdProduct.get(i).ShippedQuantity)));
			}
			//woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecOrdProduct.get(i).ItemCode,vecOrdProduct.get(i).Description,vecOrdProduct.get(i).UOM,""+deffAmt.format(vecOrdProduct.get(i).SellableQuantity)), 0, false);
		}
		if(linenumber < vecOrdProduct.size())
		{

			printFooter(signature_height, " "," ");
			PrintLoad(  vecOrdProduct,   strMovementId,   movementType,   totalPrice,  linenumber );
			giveDelay();
		}
		else
		{
			printFooter(3, " "," ");
		}
	}





	public void printReplacementOrder(Vector<ProductDO> vecSalesOrderProducts, float totalPrice, String type, String strOrderId, JourneyPlanDO mallsDetails, String from, int linenumber) {

		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String lines = "================================================================================\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formateHeader3 = "%1$-2.2s %2$-3.3s %3$-20.20s %4$20.20s %5$7.7s %6$7.7s\r\n";//73



		writeText(String.format(formatHeader, "", "REPLACEMENT ORDER", ""));
//		printHeader();

		writeText(String.format(columns2, "Repl. No: " + strOrderId, ""));
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
		if (strOrderId == null || strOrderId.equalsIgnoreCase(""))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
		writeText(String.format(columns2, "BILL TO:", "SHIP TO:"));
		writeText(String.format(columns2, mallsDetails.partyName, mallsDetails.siteName));
		writeText(String.format(columns2, "Customer No: " + mallsDetails.customerId, "Site No: " + mallsDetails.site));
		if (strOrderId == null || strOrderId.equalsIgnoreCase(""))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
		if (mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss1, mallsDetails.addresss1));
		if (mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss2, mallsDetails.addresss2));
		if (mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss3, mallsDetails.addresss3));
		if (mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss4, mallsDetails.addresss4));
		if (mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			writeText(String.format(columns2, mallsDetails.poNumber, mallsDetails.poNumber));
		if (mallsDetails.city != null && mallsDetails.city.length() > 0)
			writeText(String.format(columns2, mallsDetails.city, mallsDetails.city));

		if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Salesman : " + mallsDetails.salesmanName));
		else
			writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""));

		writeText(String.format(columns2, "Repl. No: " + strOrderId, "Repl. Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM));

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");

		if (!TextUtils.isEmpty(LPO)) {
			writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "LPO : " + LPO));
			writeText(String.format(columns2, "Sales Person Mobile : " + printMobileNumber(mallsDetails), ""));
		} else
			writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "Sales Person Mobile : " + printMobileNumber(mallsDetails)));


		writeText(lines);
		writeText(String.format(formateHeader3, "", "SR#", "RECEIPT ITEM CODE & DESC", "ISSUE ITEM CODE & DESC", "UOM", "QTY"));
		writeText(lines);

		int footer_height = 4;
		int footerTotal = 0;
//			for (int i = 0; i < vecSalesOrderProducts.size(); i++) {
		for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecSalesOrderProducts.size(); i++, linenumber++) {
			writeText(String.format(formateHeader3, "", "" + (i + 1), vecSalesOrderProducts.get(i).SKU, vecSalesOrderProducts.get(i).RelatedLineId, vecSalesOrderProducts.get(i).UOM, "" + vecSalesOrderProducts.get(i).preUnits));
			writeText(String.format(formateHeader3, "", "", "" + vecSalesOrderProducts.get(i).Description, vecSalesOrderProducts.get(i).Description1, "", ""));
		}
		writeText(lines);
		boolean isPDUser = false;
		if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_PD))
			// hide pricing
			isPDUser = true;
		else
			isPDUser = false;


		writeText(lines);
		if (linenumber < vecSalesOrderProducts.size()) {
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}

			writeText("\r\n\r\n"); //2
			if (isPDUser)
				writeText(String.format(columns2, "Receiverd By", "Galadari Ice Cream Co. Ltd. (L.L.C)"));
			else
				writeText(String.format(columns2, "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)"));
			writeText(lines);
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n ");
			giveDelay();
			printReplacementOrder(vecSalesOrderProducts, totalPrice, type, strOrderId, mallsDetails, from, linenumber);
//			printOrderSummary(orderDO, mallsDetails, type, roundOffVal, linenumber, totalPrice, totalDiscount);
		} else {


			//--------------
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}
			writeText("\r\n\r\n"); //2
			if (isPDUser)
				writeText(String.format(columns2, "Receiverd By", "Galadari Ice Cream Co. Ltd. (L.L.C)"));
			else
				writeText(String.format(columns2, "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)"));
			writeText(lines); //1
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
		}


	}



	public void printARPaymentSummary(String from, ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice, String strSelectedDateToPrint, String type, int linenumber) {

		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		String format = "%1$-25.25s%2$-25.25s%3$-25.25s%4$-25.25s%5$-25.25s\r\n";


		String formatHeaderr = "%1$62.62s";

		setCompress();
		write(String.format(formatHeaderr,   "PAYMENT RECEIPT" ));
		writeText("\r\n");


		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

		writeText(String.format(columns2, "User Name:" + preference.getStringFromPreference(Preference.USER_NAME, ""), "Emp No.: " + preference.getStringFromPreference(Preference.EMP_NO, "")));
		writeText(String.format(columns2, "Collected Date: " + (strSelectedDateToPrint != null ? strSelectedDateToPrint : mydate), "Print Date: " + mydate));

		writeText(line );
		boolean isCash = false, isCheque = false;
		if (arrayListCustomerInvoice != null && arrayListCustomerInvoice.size() > 0) {
			for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
				if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					isCheque = true;
				else if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
					isCash = true;
			}
		}

		String currencyCode = "";
		if (isCheque) {
			writeText(String.format(format, "Site Name", "Site No.", "Receipt No.", "Type", "Amount"));
			writeText(line );
			Float strTotalAmount = 0f;
			if (arrayListCustomerInvoice != null && arrayListCustomerInvoice.size() > 0) {
				for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
					if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque")) {
						float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
						strTotalAmount += amount;
						writeText(String.format(format, cuInvoiceDO.siteName, cuInvoiceDO.customerSiteId, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, deffAmt.format(amount)));

						if (currencyCode == null || currencyCode.length() <= 0)
							currencyCode = cuInvoiceDO.currencyCode;
					}
				}

				if (strTotalAmount > 0) {
					writeText(line );
				}
			}
		}
		if (isCash) {
			Float strTotalCashAmount = 0f;
			writeText(String.format(format, "Site Name", "Site No.", "Receipt No.", "Type", "Amount"));
			writeText(line );
			for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
				if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash")) {
					float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
					strTotalCashAmount += amount;
					writeText(String.format(format, cuInvoiceDO.siteName, cuInvoiceDO.customerSiteId, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, deffAmt.format(amount)));

					if (currencyCode == null || currencyCode.length() <= 0)
						currencyCode = cuInvoiceDO.currencyCode;
				}
			}
			if (strTotalCashAmount > 0) {
				writeText(line );
				writeText(String.format(format, "Cash Total: ", "", "", currencyCode, deffAmt.format(strTotalCashAmount)));
				writeText(line );
			}
		}


		printFooter(3,"AR Department","Salesman");


	}



	public void printCollectionPaymentSummary(String from, ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice, String strSelectedDateToPrint, String type) {
		String formatHeaderr = "%1$62.62s";

		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;


		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		String format = "%1$-21.21s%2$-21.21s%3$-21.21s%4$-21.21s%5$-21.21s%6$-20.20s\r\n";


		setCompress();
		write(String.format(formatHeaderr,   "RECEIPT SUMMARY" ));
		writeText("\r\n");

//		printHeader();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

		writeText(String.format(columns2, "User Name:" + preference.getStringFromPreference(Preference.USER_NAME, ""), "Emp No.: " + preference.getStringFromPreference(Preference.EMP_NO, "")));
		writeText(String.format(columns2, "Collected Date: " + (strSelectedDateToPrint != null ? strSelectedDateToPrint : mydate), "Print Date: " + mydate));


		writeText(line);

		boolean isCash = false, isCheque = false;
		if (arrayListCustomerInvoice != null && arrayListCustomerInvoice.size() > 0) {
			for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
				if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					isCheque = true;
				else if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
					isCash = true;
			}
		}

		String currencyCode = "";
		if (isCheque) {
			writeText(String.format(format, "Receipt No.", "Type", "Cheque No.", "Dated", "Bank Name", "Amount"));
			writeText(line);
			Float strTotalAmount = 0f;
			if (arrayListCustomerInvoice != null && arrayListCustomerInvoice.size() > 0) {
				for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
					if (cuInvoiceDO.reciptType.equalsIgnoreCase("cheque")) {
						float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
						strTotalAmount += amount;
						writeText(String.format(format, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, cuInvoiceDO.chequeNo, cuInvoiceDO.chequeDate, cuInvoiceDO.bankName, deffAmt.format(amount)));

						if (currencyCode == null || currencyCode.length() <= 0)
							currencyCode = cuInvoiceDO.currencyCode;
					}
				}

				if (strTotalAmount > 0) {
					writeText(line);
					writeText(String.format(format, "Cheque Total: ", "", "", "", "", currencyCode, deffAmt.format(strTotalAmount)));
					writeText(line);
				}
			}
		}

		if (isCash) {
			writeText(String.format(format, "Receipt No.", "Type", "", "", "", "Amount"));
			writeText(line);
			Float strTotalCashAmount = 0f;
			for (Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice) {
				if (cuInvoiceDO.reciptType.equalsIgnoreCase("Cash")) {
					float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
					strTotalCashAmount += amount;
					writeText(String.format(format, cuInvoiceDO.receiptNo, cuInvoiceDO.reciptType, "", "", "", deffAmt.format(amount)));

					if (currencyCode == null || currencyCode.length() <= 0)
						currencyCode = cuInvoiceDO.currencyCode;
				}
			}
			if (strTotalCashAmount > 0) {
				writeText(line);
				writeText(String.format(format, "Cash Total: ", "", "", "", currencyCode, deffAmt.format(strTotalCashAmount)));
				writeText(line);
			}
		}
		printFooter(3,"Collection Head","Salesman");
	}

	void printOrderSummary(OrderDO orderDO, JourneyPlanDO mallsDetails, String type, float roundOffVal, int linenumber, float totalPrice, float totalDiscount, String TRN, float totalTaxAmt, float totNetAmt) {
//		totalPrice = 0.0f;
//		if(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
//		{
//			isFromOS=true;
//			PrintReturnOrderNew();
//		}

		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$-25.25s %2$-41.41s  %3$-3.3s\r\n";
		String columns2 = "%1$-36.36s %2$-36.36s\r\n";
		String formatPriceBold = "%1$-10.10s %2$-17s";
		String formatPriceNormal = "%1$-10.10s %2$46s";
		String price = "%1$-1.1s %2$-38.38s %3$38.38s\r\n";
		String formatReason = "%1$-10.10s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";
//		String formateHeader = "%1$-2.2s %2$-10.10s %3$-17.17s %4$7.7s %5$7.7s %6$7.7s %7$7.7s %8$-7.7s\r\n";//73
		String formateHeader = "%1$-2.2s %2$-4.4s %3$-11.11s %4$10.10s %5$3.3s %6$4.4s %7$6.6s %8$-6.6s %9$5.5s %10$-5.5s %11$-7.7s \r\n";
		String formatPriceNorma0l = "%1$-17.17s %2$-40.40s \r\n";

//		String formateHeader = "%1$-2.2s %2$-10.10s %3$-23.23s %4$7.7s %5$7.7s %6$7.7s %7$7.7s %8$-7.7s\r\n";//73

		if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
			writeText(String.format(formatHeader, "", "RETURN ORDER", ""));

		else {
			if (mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
				writeText(String.format(formatHeader, "", "CASH INVOICE", ""));
			else
				writeText(String.format(formatHeader, "", "CREDIT INVOICE", ""));
		}

		if (type == null)
			type = AppConstants.DUPLICATE_COPY;

		writeText(String.format(formatHeader, "", "" + type.toUpperCase(), ""));

		if (TRN.equalsIgnoreCase(""))
			writeText(String.format(formatHeader, "", "N/A", ""));
		else
			writeText(String.format(formatHeader, "", "" + TRN, ""));

		if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
			writeText(String.format(formatHeader, "", "Tax Credit Note#: " + orderDO.OrderId, ""));
		else
			writeText(String.format(formatHeader, "", "Tax Invoice No:  " + orderDO.OrderId, ""));
		writeText(String.format(columns2, "BILL TO:", "SHIP TO:"));
		writeText(String.format(columns2, mallsDetails.partyName + "", "" + mallsDetails.siteName));
		writeText(String.format(columns2, "Customer No: " + mallsDetails.customerId, "Site No: " + mallsDetails.site));

		if (mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss1, mallsDetails.addresss1));

		if (mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss2, mallsDetails.addresss2));

		if (mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss3, mallsDetails.addresss3));

		if (mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
			writeText(String.format(columns2, mallsDetails.addresss4, mallsDetails.addresss4));

		if (mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			writeText(String.format(columns2, mallsDetails.poNumber, mallsDetails.poNumber));

		if (mallsDetails.city != null && mallsDetails.city.length() > 0)
			writeText(String.format(columns2, mallsDetails.city, mallsDetails.city + ""));
		writeText("\r\n");
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";

		if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER)) {
			if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
				writeText(String.format(columns2, "Collected By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Salesman : " + mallsDetails.salesmanName));
			else
				writeText(String.format(columns2, "Collected By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""));


			if (mallsDetails.VatNumber.equalsIgnoreCase(""))
				writeText(String.format(columns2, "TRN#: N/A", "Order Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM));
			else
				writeText(String.format(columns2, "TRN#:" + mallsDetails.VatNumber, "Order Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM));


			String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");

			if (!TextUtils.isEmpty(LPO)) {
				writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "Customer GRV No : " + LPO));
				writeText(String.format(columns2, "Sales Person Mobile : " + printMobileNumber(mallsDetails), ""));
			} else
				writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "Customer GRV No : : " + orderDO.LPOCode));

		} else {


			if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
				writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Salesman : " + mallsDetails.salesmanName));
			else
				writeText(String.format(columns2, "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""));

			if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
				writeText(String.format(columns2, "GRV No: " + orderDO.OrderId, "GRV Date: " + CalendarUtils.getFormatedDatefromStringWithTime(orderDO.InvoiceDate)));
			else {
//			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Invoice No: "+orderDO.OrderId,"Invoice Date: "+CalendarUtils.getFormatedDatefromStringWithTime(orderDO.InvoiceDate)), 0, true);
				if (mallsDetails.VatNumber.equalsIgnoreCase(""))
					writeText(String.format(columns2, "Customer TRN#:N/A", "Invoice Date: " + CalendarUtils.getFormatedDatefromStringWithTime(orderDO.InvoiceDate)));
				else
					writeText(String.format(columns2, "Customer TRN#: " + mallsDetails.VatNumber, "Invoice Date: " + CalendarUtils.getFormatedDatefromStringWithTime(orderDO.InvoiceDate)));
			}

			String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
			writeText(String.format(columns2, "Sub-Inventory : " + subInventory, "Sales Person Mobile : " + printMobileNumber(mallsDetails)));
			writeText(String.format(columns2, "Delivery|Supply Date :" + CalendarUtils.getCurrentDate(), "LPO : " + orderDO.LPOCode));
		}

		writeText("\r\n");
		String lines = "================================================================================\r\n";
		writeText(lines);
//		writeText(String.format(formateHeader, "SR#", "ITEM CODE", "ITEM DESCRIPTION", "QTY", "UOM", "RATE", "DISC", "AMOUNT"));
		writeText(String.format(formateHeader, "", "SR#", "ITEM CODE", "ITEM", "QTY", "UOM", "RATE", "DISC", "VAT", "VAT ", "AMOUNT"));
		writeText(String.format(formateHeader, "", "", "", "DESCRIPTION", "", "", "", "", "RATE", "AMOUNT", ""));

		writeText(lines);
//		totalPrice = 0;
//		totalDiscount = 0;
		int footer_height = 4;
//		int footerTotal = 10;
		int footerTotal = 8;


		totalTaxAmt = 0;

		for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < orderDO.vecProductDO.size(); i++, linenumber++) {
			ProductDO productDO = orderDO.vecProductDO.get(i);
//			String arabicDescription = "???? ??? ???????";
			totalDiscount += productDO.DiscountAmt;


			totalPrice += (productDO.invoiceAmount - productDO.LineTaxAmount);
			totNetAmt += (productDO.invoiceAmount);
			totalTaxAmt += productDO.LineTaxAmount;


//			writeText(String.format(formateHeader, "" + (linenumber + 1), productDO.SKU, productDO.Description, "" + productDO.units, productDO.UOM, "" + deffAmt.format(productDO.itemPrice), "" + deffAmt.format(productDO.DiscountAmt),/*productDO.DiscountAmt*/
//					"" + deffAmt.format(productDO.invoiceAmount)));
/* commented for Arabic
/*int padLength = 22 - arabicDescription.length();
			if (padLength <= 0)
				padLength = 1;
			writeTextCompress(String.format("%" + padLength + "s", ""));
			if (!arabicDescription.equalsIgnoreCase("N/A")) {
				writeTextCompress(Arabic6822.Convert(arabicDescription, false));
			} else
				writeTextCompress(arabicDescription);*/
			writeText(String.format(formateHeader, "", "" + (i + 1), productDO.SKU, "", "" + productDO.units, productDO.UOM, "" + deffAmt.format(productDO.itemPrice), "" + deffAmt.format(productDO.DiscountAmt),/*productDO.DiscountAmt*/"" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.LineTaxAmount), "" + deffAmt.format(productDO.invoiceAmount)));
			writeText(String.format(formatPriceNorma0l, "", "" + productDO.Description));

			if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
				writeText(String.format(formatReason, "", "Ex. Date: " + productDO.strExpiryDate, "Reason: " + productDO.reason, "Lot. No.: " + productDO.LotNumber));
		}
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//---------------------
		writeText(lines);
		if (linenumber < orderDO.vecProductDO.size()) {
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}

			writeText("\r\n\r\n"); //2
			writeText(String.format(price, "", " ", "Salesman Signature"));
			writeText(lines);
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n ");
			printOrderSummary(orderDO, mallsDetails, type, roundOffVal, linenumber, totalPrice, totalDiscount, TRN, totalTaxAmt, totNetAmt);
		} else {

			writeText(String.format(price, "", "Total Gross Amount", deffAmt.format(totalPrice + totalDiscount) + " " + mallsDetails.currencyCode));
			writeText(String.format(price, "", "Total Discount Amount", deffAmt.format(totalDiscount) + " " + mallsDetails.currencyCode));
//			writeText(String.format(price, "", "Total Amount(with discount)", deffAmt.format(totalPrice + totalDiscount - totalDiscount) + " " + mallsDetails.currencyCode));
//			float VATamount = 1.0f;
			writeText(String.format(price, "", "Total VAT Amount", deffAmt.format(totalTaxAmt) + " " + mallsDetails.currencyCode));
//			writeText(String.format(price, "", "Taxed Amount ", deffAmt.format(totalPrice + totalDiscount - totalDiscount + VATamount) + " " + mallsDetails.currencyCode));
			if (!(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))) {
				writeText(String.format(price, "", "Round Off Amount", deffAmt.format(roundOffVal) + " " + mallsDetails.currencyCode));
			}
			writeText(lines);
			writeText(String.format(price, "", "Total Amount", deffAmt.format(totNetAmt) + " " + mallsDetails.currencyCode));
			writeText(lines);
			writeText(String.format(formatPriceBold, "", "Amount in Words:"));
			float total = totNetAmt + roundOffVal;
			total = total < 0 ? -total : total;
			writeText(String.format(formatPriceNormal, "", new NumberToEnglish().changeCurrencyToWords("" + deffAmt.format(total))) + "\r\n");
			maxPrintLenth++;
			writeText(lines);
			//--------------
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}
			writeText("\r\n\r\n"); //2
			writeText(String.format(price, "", " ", "Salesman Signature")); //1
			writeText(lines); //1
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
		}
	}

	public void printInventory(ArrayList<InventoryObject> arrInventory, String from, CONSTANTOBJ obj, float totstrQty, float totstrPCQty, float totalStockInventorySummaryInventory, float total, int linenumber) {

		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$68.68s";
		String lines = line;// "------------------------------------------------------------------------------\r\n";
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		String formateHeader = "%1$-5.5s%2$-20.20s%3$-20.20s%4$20.20s%5$20.20s%6$20.20s%7$20.20s\r\n";//73
setCompress();
		write(String.format(formatHeader,  from ));
		writeText("\r\n");
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		writeText(String.format(columns2, "User Code: " + preference.getStringFromPreference(Preference.USER_ID, ""), "Date: " + mydate.substring(0, mydate.lastIndexOf(":"))));
		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		writeText(String.format(columns2, "Sub-Inventory : " + subInventory, ""));

		writeText(lines);
		writeText(String.format(formateHeader, "SR#", "ITEM CODE", "ITEM DESCRIPTION", "UOM", "TOTAL QTY", "DLVRD. QTY", "AVAIL. QTY"));
		writeText(lines);

		int signature_height = 4;
		int footerTotal = 3;
		for (int i = linenumber; signature_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < arrInventory.size(); i++, linenumber++) {
			InventoryObject invObject = arrInventory.get(i);
			writeText(String.format(formateHeader, "" + (i + 1), invObject.itemCode, invObject.itemDescription, invObject.UOM, "" + deffAmt2.format(invObject.availCases >= 0 ? invObject.availCases : 0), "" + deffAmt2.format(invObject.deliveredCases >= 0 ? invObject.deliveredCases : 0), "" + deffAmt2.format(invObject.availQty >= 0 ? invObject.availQty : 0)));

			totstrQty += StringUtils.getFloat(invObject.UOM);
			totstrPCQty += invObject.availCases;
			totalStockInventorySummaryInventory += invObject.deliveredCases;
			total += invObject.availQty;

		}
		writeText(lines);


		//---------------------

		if (linenumber < arrInventory.size()) {
			printFooter(signature_height," ", "Signature");
			giveDelay();
			printInventory(arrInventory, from, obj, totstrQty, totstrPCQty, totalStockInventorySummaryInventory, total, linenumber);
		} else {

			if (obj == CONSTANTOBJ.PRINT_INVENTORY || obj == CONSTANTOBJ.PRINT_VERIFY_INVENTOTY) {

				writeText(lines); //2
				if (obj == CONSTANTOBJ.PRINT_INVENTORY)
					writeText(String.format(formateHeader, "", "", "Total", "" + totstrQty, "" + totstrPCQty, "" + totalStockInventorySummaryInventory, "" + total));
				else
					writeText(String.format(formateHeader, "", "", "Total", "", "" + totalStockInventorySummaryInventory, "", "" + total));
				writeText(lines); //1
			}
			printFooter(signature_height," ", "Signature");
		}

	}

	public void printReturnLoad(String from, ArrayList<InventoryObject> arrInventory, int linenumber) {

		writeText("\r\n");
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;

		String formatHeader = "%1$68.68s";
		String lines = line;//"================================================================================\r\n";
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		String formatReason = "%1$-40.40s %2$-40.40s %3$-40.40s\r\n";
		String format = "%1$-1.1s%2$-4.4s%3$-30.30s%4$30.30s%5$30.30s%6$30.30s\r\n";
		String price = "%1$-1.1s%2$-62.62s%3$62.62s\r\n";

		setCompress();
		write(String.format(formatHeader, from ));
		writeText("\r\n");
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		writeText(String.format(columns2, "User Code: " + preference.getStringFromPreference(Preference.USER_ID, ""), "Date: " + mydate.substring(0, mydate.lastIndexOf(":"))));
		writeText(lines);
		writeText(String.format(format, "", "SR#", "ITEM CODE", "ITEM DESCRIPTION", "UOM", "Quantity"));
		writeText(lines);

		int footer_height = 4;
		int footerTotal = 0;
//		for (int i = 0; arrInventory != null && i < arrInventory.size(); i++)
		for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < arrInventory.size(); i++, linenumber++) {
			writeText(String.format(format, "", "" + (i + 1), arrInventory.get(i).itemCode, arrInventory.get(i).itemDescription, arrInventory.get(i).UOM, "" + deffAmt.format(arrInventory.get(i).PrimaryQuantity)));
			writeText(String.format(formatReason, "", "Ex. Date: " + arrInventory.get(i).expiryDate, "Reason: " + arrInventory.get(i).reason, ""));
		}
		writeText(lines);


		//---------------------

		if (linenumber < arrInventory.size()) {
			printFooter(footer_height, " ", "Signature");
			giveDelay();
			printReturnLoad(from, arrInventory, linenumber);
		} else {
			printFooter(footer_height, " ", "Signature");
		}
	}

	public void setCompress() {
		try {
			linePrinter.setCompress(true);
		} catch (LinePrinterException e) {
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

/*
	public boolean  printPaymentInVoiceReceipt1(boolean forDiscount,boolean isOrderSummary, OrderDO orderDO, JourneyPlanDO mallsDetails, float roundOffVal, Vector<ProductDO> vecSalesOrderProducts, String TRN,String strType, PaymentHeaderDO objPaymentDO , int linenumber, float grand_total, float totalDiscount, float totalTaxAmt) {

		boolean isPaymentPrinted= false;
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$-50.50s %2$-41.41s  %3$-3.3s\r\n";
		String formatHeaderArabic = "%1$62.62s";
		String lines = "================================================================================\r\n";
		String dotlines = ".. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. \r\n";
		String total = "%1$-80.80s %2$20.20s %3$25.25s \r\n";

		setCompress();

		String address = "%1$12.12s %2$-45.45s %3$50.50s %4$-13.13s  \r\n";
		String strOrderId = orderDO.OrderId;

		String containerNo = strOrderId;

		writeText(String.format(formatHeaderArabic, "" + TRN + "/"));
		writeArabic(R.string.trn_header);
		writeText("\r\n");
		if (mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
			write(String.format(formatHeaderArabic, "TAX INVOICE/"));
			writeArabic(R.string.tax_invoice);
			writeText("\r\n");
		} else {
			write(String.format(formatHeaderArabic, "TAX CREDIT INVOICE/"));
			writeArabic(R.string.tax_credit_invoice);
			writeText("\r\n");
		}

		writeText(dotlines);
		int a = 20, b = 42, c = 25, d = 37;
		writeArAlignment(a, R.string.bill_to);
		write(-b, "  Bill To : " + mallsDetails.partyName);
		writeArAlignment(c, R.string.invoice_no);
		write(-d, "  Invoice No  : " + strOrderId);
		writeText("\r\n");
		mallsDetails.partyNameArabic = mallsDetails.SiteNameInArabic; // need to be removed


		//bill to Address
		String customerAddress [] = {mallsDetails.addresss1+","+mallsDetails.addresss2+"," ,
				""+mallsDetails.addresss3+","+mallsDetails.addresss4+","
						,""+mallsDetails.city+","+mallsDetails.poNumber};

		writeText(customerAddress[0]+"\r\n");
		writeText(customerAddress[1]+"\r\n");
		writeText(customerAddress[2]+"\r\n");
		String customerAddressArabic [] ={mallsDetails.partyNameArabic+","+mallsDetails.Address1_AR+",",
				 ""+mallsDetails.Address2_AR+","+mallsDetails.Address3_AR+",",
				 ""+mallsDetails.AreaName_AR+","+mallsDetails.PostalCode_AR+"",
				 ","+mallsDetails.LocationName_AR};
		if(isArabicEnable)
		{
			writeArabic( customerAddressArabic[0]);writeText("\r\n");
			writeArabic( customerAddressArabic[1]);writeText("\r\n");
			writeArabic( customerAddressArabic[2]);writeText("\r\n");
		}

		writeText("\r\n");
		//ship to address
		writeArAlignment(a, R.string.address);
		write( -b,"  Address : " );
		String customerAddressArabicShipTo []  ={ mallsDetails.SiteNameInArabic+","+mallsDetails.Address1_AR+",",
				 mallsDetails.Address2_AR+","+mallsDetails.Address3_AR+",",
				 mallsDetails.AreaName_AR+","+mallsDetails.PostalCode_AR+",",
				  mallsDetails.PostalCode_AR};
		writeArAlignment(c, R.string.invoice_date);
		write(-d, "  Invoice Date : " + orderDO.InvoiceDate);
		writeText("\r\n");
		if(isArabicEnable)
		{
			writeArabic(customerAddressArabicShipTo[0]);writeText("\r\n");
			writeArabic(customerAddressArabicShipTo[1]); writeText("\r\n");
			writeArabic(customerAddressArabicShipTo[2]); writeText("\r\n");
			writeArabic(customerAddressArabicShipTo[3]);writeText("\r\n");
		}


		writeArAlignment(a, R.string.customer_phone_number);
		write(-b, "  Cust Po No : " + orderDO.LPOCode);

		writeText("\r\n");
		writeText("\r\n");
		writeArAlignment(a, R.string.trn_header);
		write(-b, " " + mallsDetails.VatNumber);
		writeArAlignment(c, R.string.due_date);
		write(-d, "Due Date : " + orderDO.InvoiceDate);

		writeText("\r\n");

		writeArAlignment(a, R.string.sales_person);
		write(-b, "  Sales Person: " + mallsDetails.salesmanName);
		writeArAlignment(c, R.string.container_no);
		write(-d, "  Container : " + containerNo);

		writeText("\r\n");


		try {
			linePrinter.flush();
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
//		writeText(mydate.lastIndexOf(":") + AM_PM);
		//125 or 130
		int h1 = 6, h2 = 12, h3 = 15, h4 = 7, h5 = 10, h6 = 10, h7 = 10, h8 = 10, h9 = 10, h10 = 12, h11 = 12, h12 = 15;
		String orderHeader = "%1$" + h1 + "." + h1 + "s" +
				"%2$" + h2 + "." + h2 + "s" +
				"%3$" + h3 + "." + h3 + "s" +
				"%4$" + h4 + "." + h4 + "s" +
				"%5$" + h5 + "." + h5 + "s" +
				"%6$" + h6 + "." + h6 + "s" +
				"%7$" + h7 + "." + h7 + "s" +
				"%8$" + h8 + "." + h8 + "s" +
				"%9$" + h9 + "." + h9 + "s" +
				"%10$" + h10 + "." + h10 + "s" +
				"%11$" + h11 + "." + h11 + "s" +
				"%12$" + h12 + "." + h12 + "s\r\n";

//		writeText(String.format(orderHeader," ","| ", "| ", "| ","| ","|Price"," |","Amount","","|Vat","|VAT","|Total"));
//		writeText(String.format(orderHeader,"SrNO","|Item", "|Description", "|QTY","|UOM","|Gross","Net|","Gross","Net","|Rate","|Amount","|Net Total"));
		writeText(dotlines);
		writeText(String.format(orderHeader, " ", " ", "  ", "  ", " ", "Price", " ", "Amount", "", "Vat", "VAT", " "));
		writeText(String.format(orderHeader, "SrNO", "Item", "Description", "QTY", "UOM", "Gross", "Net", "Gross", "Net", "Rate", "Amount", "Net Total"));
		writeText(dotlines);

		writeArAlignment(h1, R.string.s_no);
		writeArAlignment(h2, R.string.item);
		writeArAlignment(h3, R.string.description);
		writeArAlignment(h4, R.string.qty);
		writeArAlignment(h5, R.string.uom1);
		writeArAlignment(h6, R.string.gross);
		writeArAlignment(h7, R.string.net);
		writeArAlignment(h8, R.string.gross);
		writeArAlignment(h9, R.string.net);
		writeArAlignment(h10, R.string.vat_rate1); //10th line
		writeArAlignment(h11, R.string.vat_amt1);
		writeArAlignment(h12, R.string.net_total);
		writeText("\r\n");
		writeArAlignment(h1 + h2 + h3 + h4 + h5, R.string.uom2);
		writeArAlignment(h6 + h7 + h8 + h9 + h10, R.string.vat_rate2); //10th line
		writeArAlignment(h11, R.string.vat_amt2);
		writeText("\r\n");
		writeText(dotlines);
		int footer_height = 3;
		int footerTotal = 7;


		for (int i = linenumber; footer_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecSalesOrderProducts.size(); i++, linenumber++) {
//            writeText(String.format(orderHeader, " ", "| ", "| ", "| ", "|", "|", "|", "", "", "|", "|", "|  "));
//            writeText(String.format(orderHeader,"SrNO","|Item", "|Description", "|QTY","|UOM","|Gross","Net|","Gross","Net","|Rate","|Amount","|Net Total"));
			ProductDO productDO = vecSalesOrderProducts.get(i);
			totalPrice += productDO.invoiceAmount;
//			totalDiscount += productDO.discountAmount;
			totalPrice += (productDO.invoiceAmount + productDO.LineTaxAmount); //changed from - to +
			totalTaxAmt += productDO.LineTaxAmount;

			float unit = 0;
			float discount=0;
			if(forDiscount)
			{
				discount = productDO.DiscountAmt * StringUtils.getFloat(productDO.preUnits);
			}else
			{
				discount = productDO.DiscountAmt;
			}
//			discount = productDO.DiscountAmt;
			totalDiscount+=discount;
			if (!isOrderSummary) {
				unit = StringUtils.getFloat(productDO.preUnits);
			}
			else {
				unit = StringUtils.getFloat(productDO.units);
			}


			grand_total += unit * productDO.itemPrice;
//			woosim.saveSpool(EUC_KR,String.format(format1,"",""+(i+1),productDO.SKU,"",""+productDO.units,productDO.UOM,""+deffAmt.format(productDO.itemPrice),""+deffAmt.format(productDO.DiscountAmt*StringUtils.getDouble(productDO.units)),*//*productDO.DiscountAmt*//*""+deffAmt.format(productDO.TaxPercentage)+"%",""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.invoiceAmount)), 0, false);

//			writeText(String.format(formateHeader, "","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","VAT","VAT","AMOUNT"));
//			writeText(String.format(formateHeader, "", "" + (i + 1),productDO.SKU,"",""+productDO.units,productDO.UOM,""+deffAmt.format(productDO.itemPrice),""+deffAmt.format(productDO.DiscountAmt*StringUtils.getDouble(productDO.units)),*//**//**//**//**//**//**//**//*productDO.DiscountAmt*//**//**//**//**//**//**//**//*""+deffAmt.format(productDO.TaxPercentage)+"%",""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.invoiceAmount)
//			writeText(String.format(orderHeader, "SrNO", "|Item", "|Description", "|QTY", "|UOM", "|Gross", "Net|", "Gross", "Net", "|Rate", "|Amount", "|Net Total"));
			writeText(String.format(orderHeader, " " + (i + 1), productDO.SKU + "", productDO.Description + "", "" + unit, "" + productDO.UOM, "" + deffAmt.format(productDO.itemPrice), deffAmt.format(productDO.itemPrice - discount / unit) + "", "" + deffAmt.format(productDO.itemPrice * unit), "" + deffAmt.format(unit * (productDO.itemPrice) - discount), "" + deffAmt.format(productDO.TaxPercentage), "" + deffAmt.format(productDO.LineTaxAmount), "" + deffAmt.format(productDO.invoiceAmount)));
			write("                       ");
			if(isArabicEnable)
				writeArAlignment((TextUtils.isEmpty(productDO.Description1)!=true? productDO.Description1.length()%100 : 0), productDO.Description1  );
			writeText("\r\n");
		}
		writeText(dotlines);

		if (linenumber < orderDO.vecProductDO.size()) {
			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}

			write(".. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. \r\n");
			write(-20, " Received By ");
			writeArAlignment(25, R.string.received_by);
			write(45, " ");
			write(40, "Jumeirah Trading Co.Ltd");
			writeText("\r\n");

			write(-25, " Receivers Sign & Seal");
			writeArAlignment(25, R.string.receivers_sign);
			write(40, " ");
			writeArAlignment(40, R.string.jum_trading);
			writeText("\r\n");
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n ");
			isPaymentPrinted=printPaymentInVoiceReceipt1(forDiscount,isOrderSummary, orderDO, mallsDetails, roundOffVal, vecSalesOrderProducts, TRN, strType,       objPaymentDO,linenumber, grand_total, totalDiscount, totalTaxAmt);
		} else {
			write("\r\n");
			write(".. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. ");
			writeArAlignment(23, R.string.grand_total);
			write(-20, " Grand Total : ");
			write(12, "" + deffAmt.format(grand_total));
			write("\r\n");
			String amountInWords= new NumberToEnglish().changeCurrencyToWords("" + deffAmt.format(grand_total - totalDiscount + totalTaxAmt));

			if (amountInWords != null && amountInWords.length() > 9) {
				amountInWords = amountInWords.substring(0, amountInWords.length() - 9)+" Halala only";
			}

			write("SAR : " +amountInWords+ "\r\n");
			writeSpace(29);
			writeArabic(((BaseActivity)context).numToArabicWord((grand_total - totalDiscount + totalTaxAmt)+""));
			writeText("\r\n");

			write(60, " ");
			writeArAlignment(28, R.string.discount);
			write(-20, " Discount  : ");
			write(12, "" + deffAmt.format(totalDiscount));
			write("\r\n");



			write(60, " ");
			writeArAlignment(28, R.string.sub_total);
			write(-20, " Sub Total : ");
			write(12, "" + deffAmt.format(grand_total - totalDiscount));
			write("\r\n");

			write(60, " ");
			writeArAlignment(28, R.string.vat_total);
			write(-20, " VAT Total : ");
			write(12, "" + deffAmt.format(totalTaxAmt));
			write("\r\n");





			write(60, " ");
			writeArAlignment(28, R.string.net_total);
			write(-20, " Net Total : ");
			write(12, "" + deffAmt.format(grand_total - totalDiscount + totalTaxAmt));
			write("\r\n");


			//----Logic for payment
			int numberOfOrdersForReceipt = objPaymentDO !=null && objPaymentDO.vecPaymentInvoices != null ? objPaymentDO.vecPaymentInvoices.size():0;
			int lineToBePrinted =maxPrintLenth + 1 - (footer_height+2+15+numberOfOrdersForReceipt);
			if(lineToBePrinted>0)
			{
				isPaymentPrinted =true;
				printReceiptOnly(mallsDetails,strType,objPaymentDO,0);
				//print receipt
			}


			//----------------
			write(".. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. \r\n");
			write(-20, " Received By ");
			writeArAlignment(25, R.string.received_by);
			write(45, " ");
			write(40, "Jumeirah Trading Co.Ltd");
			writeText("\r\n");

			write(-25, " Receivers Sign & Seal");
			writeArAlignment(25, R.string.receivers_sign);
			write(40, " ");
			writeArAlignment(40, R.string.jum_trading);
			writeText("\r\n");

			try {
				linePrinter.newLine(maxPrintLenth + 1 - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}
			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
			return  isPaymentPrinted;
		}
		return  isPaymentPrinted;

	}*/

public String getDate(String str)
{
	return (str !=null && (! TextUtils.isEmpty(str))? str.substring(0,10) : "");

}


	public boolean  printPaymentInVoiceReceipt1(boolean forDiscount,boolean isOrderSummary, OrderDO orderDO, JourneyPlanDO mallsDetails, float roundOffVal, Vector<ProductDO> vecSalesOrderProducts, String TRN,String strType, PaymentHeaderDO objPaymentDO , int linenumber, double grand_total, double totalDiscount, double totalTaxAmt,double tot_amt) {

		boolean isPaymentPrinted= false;
		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		String formatHeader = "%1$-50.50s %2$-41.41s  %3$-3.3s\r\n";
		String formatHeaderArabic = "%1$62.62s";
		String lines = "================================================================================\r\n";
		String dotlines = "-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- -- -- -- -- -- -- -- " +
				"-- -- -- \r\n" ;
            /*".. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. \r\n";*/
		String total = "%1$-80.80s %2$20.20s %3$25.25s \r\n";

		setCompress();

		String address = "%1$12.12s %2$-45.45s %3$50.50s %4$-13.13s  \r\n";
		String strOrderId = orderDO.OrderId;

		String containerNo = strOrderId;

		write(String.format(formatHeaderArabic, "" + TRN + "/"));
		writeArabic(R.string.trn_header);
		writeText("\r\n");
		if (mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
			write(String.format(formatHeaderArabic, "TAX INVOICE/"));
			writeArabic(R.string.tax_invoice);
			writeText("\r\n");
		} else {
			write(String.format(formatHeaderArabic, "TAX CREDIT INVOICE/"));
			writeArabic(R.string.tax_credit_invoice);
			writeText("\r\n");
		}

		writeText(dotlines);
		int a = 20, b = 42, c = 25, d = 37;
		int s1=14,s2=50,s3=15,s4=19,s5=15,s6=15;


		write(-s1, " Bill To : " );
		write(-s2, ""+mallsDetails.siteName );
		writeArAlignment( s3,R.string.bill_to);

		write(s4, "Invoice No : ");
		write(-s5, "" + strOrderId);
		writeArAlignment(-s6, R.string.invoice_no);
		writeText("\r\n");

		mallsDetails.partyNameArabic = mallsDetails.SiteNameInArabic; // need to be removed

		if(isArabicEnable)
		{
//       write(-s1, "         : " );
			writeArabic(mallsDetails.SiteNameInArabic+" ");
			writeText("\r\n");
		}



		//bill to Address
      /*String customerAddress [] = {mallsDetails.addresss1+" "+mallsDetails.addresss2+" "+mallsDetails.addresss3,
             mallsDetails.addresss4+" "+mallsDetails.city+" "+mallsDetails.poNumber};

//Bill to (Sitename) Address in English
      if(customerAddress[0].length()+customerAddress[1].length()>AppConstants.CHAR_IN_LINE)
      {
         writeText(customerAddress[0] + "\r\n");
         writeText(customerAddress[1] + "\r\n");
      }
      else
      {
         writeText(customerAddress[0] + " " +customerAddress[1] + "\r\n");
      }

      String customerAddressArabic [] ={mallsDetails.partyNameArabic+" "+mallsDetails.Address1_AR+" " +mallsDetails.Address2_AR,
             mallsDetails.Address3_AR+" " +mallsDetails.AreaName_AR+" "+mallsDetails.PostalCode_AR+" "+mallsDetails.LocationName_AR};
      //Bill to Address in Arabic
      if(isArabicEnable)
      {
         if(customerAddressArabic[0].length()+customerAddressArabic[1].length()>AppConstants.CHAR_IN_LINE) {
            writeArabic(customerAddressArabic[0]);
            writeText("\r\n");
            writeArabic(customerAddressArabic[1]);
            writeText("\r\n");
         }else
         {
            writeArabic(customerAddressArabic[0]+" " +customerAddressArabic[1]);
            writeText("\r\n");

         }
      }
*/
		//ship to address

		write(-s1, " Ship To : " );
		write(-s2, ""+mallsDetails.partyName );
		writeArAlignment( s3,R.string.address);
		write(s4, "Invoice Date : ");
		write(-s5, "" + getDate( orderDO.InvoiceDate));
		writeArAlignment(-s6, R.string.invoice_date);
		writeText("\r\n");

		//shift  to Address in English
		String customerAddress [] = {mallsDetails.addresss1+" "+mallsDetails.addresss2+" "+mallsDetails.addresss3,
				mallsDetails.addresss4+" "+mallsDetails.city+" "+mallsDetails.poNumber};

//Bill to (Sitename) Address in English
		if(customerAddress[0].length()+customerAddress[1].length()>AppConstants.CHAR_IN_LINE)
		{
			writeText(customerAddress[0] + "\r\n");
			writeText(customerAddress[1] + "\r\n");
		}
		else
		{
			writeText(customerAddress[0] + " " +customerAddress[1] + "\r\n");
		}



		String customerAddressArabicShipTo []  ={ mallsDetails.partyNameArabic+" "+mallsDetails.Address1_AR+" "+ mallsDetails.Address2_AR+" "+mallsDetails.Address3_AR+" ",
				mallsDetails.AreaName_AR+" "+mallsDetails.PostalCode_AR+" " };

		if(isArabicEnable)
		{
			if( customerAddressArabicShipTo[0].length()+customerAddressArabicShipTo[1].length()+1>AppConstants.CHAR_IN_LINE) {
//          writeArabic(customerAddressArabicShipTo[0]);
				writeArabic(mallsDetails.partyNameArabic +" ");
				writeArabic(mallsDetails.Address1_AR +" ");
				writeArabic(mallsDetails.Address2_AR +" ");
				writeArabic(mallsDetails.Address3_AR  );
				writeText("\r\n");
//          writeArAlignment(-(s1+s2+s3),customerAddressArabicShipTo[1]);
				writeArabic( mallsDetails.AreaName_AR+" ");
				writeArAlignment(-(s1+s2+s3-((mallsDetails.AreaName_AR !=null ? mallsDetails.AreaName_AR.length():0) )), mallsDetails.PostalCode_AR+" ");
			}else
			{
				if(customerAddressArabicShipTo[0].length()+customerAddressArabicShipTo[1].length()+1<(s1+s2+s3)) {
//          writeArAlignment(-(s1+s2+s3),customerAddressArabicShipTo[0]+" "+customerAddressArabicShipTo[1]);
					writeArabic(mallsDetails.partyNameArabic +" ");
					writeArabic(mallsDetails.Address1_AR +" ");
					writeArabic(mallsDetails.Address2_AR +" ");
					writeArabic(mallsDetails.Address3_AR +" " );
					writeArabic(mallsDetails.AreaName_AR  +" ");
					writeArabic(mallsDetails.PostalCode_AR  );
					write((s1+s2+s3) -(customerAddressArabicShipTo[0].length()+customerAddressArabicShipTo[1].length()+1)," ");



				}
				else
				{
//             writeArAlignment(-(s1+s2+s3),customerAddressArabicShipTo[0]+" "+customerAddressArabicShipTo[1]);
					writeArabic(mallsDetails.partyNameArabic +" ");
					writeArabic(mallsDetails.Address1_AR +" ");
					writeArabic(mallsDetails.Address2_AR +" ");
					writeArabic(mallsDetails.Address3_AR +" " );
					writeArabic(mallsDetails.AreaName_AR  +" ");
					writeArabic(mallsDetails.PostalCode_AR  );
					writeText("\r\t");
					write(-(s1 + s2 + s3 )," ");
				}

			}
		}
		else{
			write(-(s1+s2+s3),"");
		}

		write(s4, "  Cust Po No : ");
		write(-s5, "" + orderDO.LPOCode);
		writeArAlignment(-s6, R.string.customer_phone_number);
		writeText("\r\n");

		write(-s1, "    TRN# : " );
		write(-s2, ""+mallsDetails.VatNumber );
		writeArAlignment( s3,R.string.trn_header);
		write(s4, " Due Date : ");
		write(-s5, "" +getDate( orderDO.InvoiceDate));
		writeArAlignment(-s6, R.string.due_date);
		writeText("\r\n");

		write(-s1, "Sales Person: " );
		write(-s2, ""+mallsDetails.salesmanName );
		writeArAlignment( s3,R.string.sales_person);
		write(s4, "Container No : ");
		write(-s5, "" + containerNo);
		writeArAlignment(-s6, R.string.container_no);
		writeText("\r\n");


		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
//    writeText(mydate.lastIndexOf(":") + AM_PM);
		//125 or 130 h2 = 12,h3 = 15
//    int h1 = 6, h2 = 12, h3 = 15, h4 = 7, h5 = 10, h6 = 10, h7 = 10, h8 = 10, h9 = 10, h10 = 12, h11 = 12, h12 = 15;
		int h1 = 6, h2 = 24, h3 = 3, h4 = 7, h5 = 10, h6 = 10, h7 = 10, h8 = 10, h9 = 10, h10 = 12, h11 = 12, h12 = 15;
		String orderHeader = "%1$" + h1 + "." + h1 + "s" +
				"%2$" + h2 + "." + h2 + "s" +
				"%3$" + h3 + "." + h3 + "s" +
				"%4$" + h4 + "." + h4 + "s" +
				"%5$" + h5 + "." + h5 + "s" +
				"%6$" + h6 + "." + h6 + "s" +
				"%7$" + h7 + "." + h7 + "s" +
				"%8$" + h8 + "." + h8 + "s" +
				"%9$" + h9 + "." + h9 + "s" +
				"%10$" + h10 + "." + h10 + "s" +
				"%11$" + h11 + "." + h11 + "s" +
				"%12$" + h12 + "." + h12 + "s\r\n";

		writeText(dotlines);
		writeArAlignment(h1, R.string.s_no);
//    writeArAlignment(h2, R.string.item);
		writeArAlignment(h2+h3, context.getResources().getString( R.string.item)+" "+context.getResources().getString( R.string.description));
//    writeArAlignment(h3, R.string.description);
		writeArAlignment(h4, R.string.qty);
		writeArAlignment(h5, R.string.uom1);
		writeArAlignment(h6, R.string.gross);
		writeArAlignment(h7, R.string.net);
		writeArAlignment(h8, R.string.gross);
		writeArAlignment(h9, R.string.net);
		writeArAlignment(h10, R.string.vat_rate1); //10th line
		writeArAlignment(h11, R.string.vat_amt1);
		writeArAlignment(h12, R.string.net_total);
		writeText("\r\n");
		writeArAlignment(h1 + h2 + h3 + h4 + h5, R.string.uom2);
		writeArAlignment(h6 + h7 + h8 + h9 + h10, R.string.vat_rate2); //10th line
		writeArAlignment(h11, R.string.vat_amt2);
		writeText("\r\n");

		writeText(String.format(orderHeader, " ", " ", "  ", "  ", " ", "Price", " ", "Amount", "", "Vat", "VAT", " "));
		writeText(String.format(orderHeader, "SrNO", "Item/ Description", "", "QTY", "UOM", "Gross", "Net", "Gross", "Net", "Rate", "Amount", "Net Total"));
		writeText(dotlines);

		int signature_height = 3;
		int footerTotal = 7;


		for (int i = linenumber; signature_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 1) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecSalesOrderProducts.size(); i++, linenumber++) {
			ProductDO productDO = vecSalesOrderProducts.get(i);
			totalPrice += productDO.invoiceAmount;
//       totalDiscount += productDO.discountAmount;
			totalPrice += (productDO.invoiceAmount + productDO.LineTaxAmount); //changed from - to +
			totalTaxAmt += productDO.LineTaxAmount;

			float unit = 0;
			double discount=0;
			if(forDiscount)
			{
				if(mallsDetails.customerType !=null &&  mallsDetails.customerType.equalsIgnoreCase("CASH") && isOrderSummary)
					discount = productDO.DiscountAmt;
				else if(productDO.quantityBU==0)
					discount = productDO.DiscountAmt;
					else
					discount = productDO.DiscountAmt * StringUtils.getFloat(productDO.preUnits);
			}else
			{
				discount = productDO.DiscountAmt;
			}


			totalDiscount+=discount;
			if (!isOrderSummary) {
				unit = StringUtils.getFloat(productDO.preUnits);
			}
			else {
				unit = StringUtils.getFloat(productDO.units);
			}


			grand_total += unit * productDO.itemPrice;
			tot_amt=tot_amt+productDO.invoiceAmount;
//       writeText(String.format(orderHeader, " " + (i + 1), productDO.SKU + "", productDO.Description + "", "" + unit, "" + productDO.UOM, "" + deffAmt.format(productDO.itemPrice), deffAmt.format(productDO.itemPrice - discount / unit) + "", "" + deffAmt.format(productDO.itemPrice * unit), "" + deffAmt.format(unit * (productDO.itemPrice) - discount), "" + deffAmt.format(productDO.TaxPercentage), "" + deffAmt.format(productDO.LineTaxAmount), "" + deffAmt.format(productDO.invoiceAmount)));
			writeText(String.format(orderHeader, " " + (i + 1), productDO.SKU + "",   "", "" + unit, "" + productDO.UOM, "" + deffAmt.format(productDO.itemPrice), deffAmt.format(productDO.itemPrice - discount / unit) + "", "" + deffAmt.format(productDO.itemPrice * unit), "" + deffAmt.format(unit * (productDO.itemPrice) - discount), "" + deffAmt.format(productDO.TaxPercentage), "" + deffAmt.format(productDO.LineTaxAmount), "" + deffAmt.format(productDO.invoiceAmount)));
//       write("                       ");
//       if(isArabicEnable)
//          writeArAlignment((TextUtils.isEmpty(productDO.Description1)!=true? productDO.Description1.length()%100 : 0), productDO.Description1  );
//       writeText("\r\n");

			//newly added for english and arabic description
			String str = productDO.Description;
			String strArr = productDO.Description1;
//       for (int  x=0;x<10;x++)
//       {
//          str+=productDO.Description;
//          strArr +=productDO.Description1;
//       }
			int  halfsize =0;
			int SIZE=AppConstants.CHAR_IN_LINE;
			ArrayList<String> arrStr  = splitStringBySize(str,SIZE);
			ArrayList<String> arrArabicStr  = new ArrayList<String>();

			if(str.length()>AppConstants.CHAR_IN_LINE) {
				for (int k = 0; k < str.length() / SIZE; k++) {
					writeText(arrStr.get(k) + "\r\n");
//          System.out.println(arrStr.get(i));
				}
				write(arrStr.get(str.length() / SIZE) );

			}
			else
			{
				write(arrStr.get(str.length() / SIZE) );

			}
//			if(isArabicEnable)
//				writeArAlignment((TextUtils.isEmpty(productDO.Description1)!=true? productDO.Description1.length()%100 : 0), productDO.Description1  );
//			writeText("\r\n");

			 if(!TextUtils.isEmpty(strArr) && isArabicEnable)
//       if(!strArr.isEmpty()  )
			{
//				writeText(" ");
				write(" ");
//          System.out.println(" ");

				if(arrStr.size()>0)
					halfsize=arrStr.get(arrStr.size()-1).length()+1;
				int sizeOfFirstArabicString= SIZE-halfsize;
				String halfString ="";
				if(strArr.length()<sizeOfFirstArabicString)
				{
					halfString=strArr;
					arrArabicStr .add(strArr);
				}
				else
				{
					halfString =  strArr.substring(0,sizeOfFirstArabicString) ;
					if(!halfString.isEmpty())
					{
						arrArabicStr.add(halfString);
//                arrArabicStr.addAll( splitStringBySize( strArr.substring(sizeOfFirstArabicString,strArr.length()-1-halfString.length()) ,SIZE) );
						arrArabicStr.addAll( splitStringBySize( strArr.substring(sizeOfFirstArabicString,strArr.length()-1 ) ,SIZE) );
					}
					else
					{
						arrArabicStr.addAll( splitStringBySize( strArr.substring(sizeOfFirstArabicString,strArr.length()-1-halfString.length()) ,SIZE) );
					}
				}
				for(int k=0;k<=(strArr.length()-sizeOfFirstArabicString)/SIZE;k++)
				{
             writeArabic(arrArabicStr.get(k));writeText("\r\n");
//					write(arrArabicStr.get(k));writeText("\r\n");
//             System.out.println(arrArabicStr.get(i));
				}
			}
			else
			{
				writeText("\r\n");
			}
			//============================================
		}
		writeText(dotlines);

		if (linenumber < orderDO.vecProductDO.size()) {
			write("\r\n");

/*
			 try {
				linePrinter.newLine(maxPrintLenth   - footer_height+1);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			} */
			printLines(maxPrintLenth   - signature_height-FOOTER_HEIGHT);

			write(-50,"---------------------------------------------");
			write(76,"---------------------------------------------");writeText("\r\n");
			write("Received By(");  //12
			writeArabic( R.string.received_by); //13 chars
			write(")");  //1
			write(100, "Jumeirah Trading Co.Ltd");
			writeText("\r\n");

			write(  "Receivers Sign & Seal("); //22
			writeArabic(  R.string.receivers_sign); //18
			write(")");  //1
			writeArAlignment(85, R.string.jum_trading);
			writeText("\r\n");

			printToEnd();
			printEndPageTAG();
			printLines(6);
			adujstHalfLineHeightForEachPaper();

//			printLines();
			giveDelay();
			isPaymentPrinted=printPaymentInVoiceReceipt1(forDiscount,isOrderSummary, orderDO, mallsDetails, roundOffVal, vecSalesOrderProducts, TRN, strType,       objPaymentDO,linenumber, grand_total, totalDiscount, totalTaxAmt,tot_amt);
		} else {
			write("\r\n");
			String amountInWords= new NumberToEnglish().changeCurrencyToWords("" + deffAmt.format(grand_total - totalDiscount + totalTaxAmt));
			amountInWords = amountInWords.contains(" and ")?amountInWords.replace(" and "," riyal and "):amountInWords;
			if (amountInWords != null && amountInWords.length() > 9) {
				amountInWords = amountInWords.substring(0, amountInWords.length() - 9)+" Halala only";
			}
//       int f1=75,f2=15;
			int f1=75,f2=25,f3=15,f4=10;


			if(amountInWords.length()>f1)
			{
				write(-f1,"" +amountInWords.substring(0,f1) );
				writeText("\r\n");
				write(-f1,"" +amountInWords.substring(f1,amountInWords.length()) );
			}
			else
			{
				write(-f1, amountInWords  );
			}



			writeArAlignment(f2,R.string.grand_total);//14
			write(   f3,"Grand Total:");
			write( f4,"" + deffAmt.format(grand_total));
			writeText("\r\n");
			String arabic_total = ((BaseActivity)context).numToArabicWord(  ""+tot_amt  );
			if(arabic_total !=null && arabic_total.length() > 0)
				writeArAlignment( -f1,arabic_total);
			else
				write( -f1," ");

			writeArAlignment(f2,R.string.discount);
			write(   f3,"   Discount:");
			write(f4, "" + deffAmt.format(totalDiscount));
			writeText("\r\n");

			write(-f1,"");
			writeArAlignment(f2,R.string.sub_total);
			write(   f3,"  Sub Total:");
			write(f4, "" + deffAmt.format(grand_total - totalDiscount));
			writeText("\r\n");

			write(-f1,"");
			writeArAlignment(f2,R.string.vat_total);
			write(   f3,"  VAT Total:");
			write(f4, "" + deffAmt.format(totalTaxAmt));
			writeText("\r\n");

			write(-f1,"");
			writeArAlignment(f2,R.string.net_total);
			write(   f3," Net Total:");
//       write(f4, "" + deffAmt.format(grand_total - totalDiscount + totalTaxAmt));
			write(f4, "" + deffAmt.format(tot_amt));
			writeText("\r\n");

			writeText(dotlines);

			//----Logic for payment
      /* int numberOfOrdersForReceipt = objPaymentDO !=null && objPaymentDO.vecPaymentInvoices != null ? objPaymentDO.vecPaymentInvoices.size():0;
         int lineToBePrinted =maxPrintLenth + 1 - (footer_height+2+15+numberOfOrdersForReceipt);
         if(lineToBePrinted>0&& objPaymentDO !=null)
         {
            isPaymentPrinted =true;
            printReceiptOnly(mallsDetails,strType,objPaymentDO,0);//print receipt
         }
*/

			//----------------
			/*try {
				linePrinter.newLine(maxPrintLenth  +1  - footer_height);
			} catch (LinePrinterException e) {
				e.printStackTrace();
			}*/
			printLines(maxPrintLenth   - signature_height-1-FOOTER_HEIGHT);
			write(-50,"---------------------------------------------");
			write(76,"---------------------------------------------"); writeText("\r\n");
			write("Received By(");  //12
			writeArabic( R.string.received_by); //13 chars
			write(")");  //1
			write(100, "Jumeirah Trading Co.Ltd");
			writeText("\r\n");

			write(  "Receivers Sign & Seal("); //22
			writeArabic(  R.string.receivers_sign); //18
			write(")");  //1
			writeArAlignment(85, R.string.jum_trading);
			writeText("\r\n");

         /* enable this when we print receipt in same page
         try {
            linePrinter.newLine(maxPrintLenth  +1  - footer_height);
         } catch (LinePrinterException e) {
            e.printStackTrace();
         }*/
			printToEnd();
			printEndPageTAG();
			printLines(6);
			adujstHalfLineHeightForEachPaper();


//			printLines();
//			writeText("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"); //spaces for (header +footer)
			return  isPaymentPrinted;
		}
		return  isPaymentPrinted;

	}

	public void writeArabic(int strId) {

		try {
			linePrinter.write(Arabic6822.Convert(((BaseActivity) context).getResources().getString(strId), false));
		} catch (PrinterException e) {
			e.printStackTrace();
		} catch (Exception e) {
			try {
				linePrinter.write("*");
			} catch (PrinterException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void writeArabic(String str) {
		if (str != null && str.length() > 0) {

			try {
				linePrinter.write(Arabic6822.Convert(str, false));
			} catch (PrinterException e) {
				e.printStackTrace();
			} catch (Exception e) {
				try {
					String s = "%1$" + str.length() + "." + str.length() + "s";
					linePrinter.write(String.format(s,"*"));
				} catch (PrinterException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void printGarbage() {

		try {
			linePrinter.write("*");
		} catch (PrinterException ex) {
			ex.printStackTrace();
		}
	}


	//	written by vinod
	public void writeArAlignment(int size, int id) {
//		size = Math.abs(size);
		String arabic = ((BaseActivity) context).getResources().getString(id);
		int length = arabic.length();
		int spaceSize = Math.abs(Math.abs(size) - length);
		String strformnat = "";
		if (spaceSize == 0) {
			try {

				linePrinter.write(Arabic6822.Convert(arabic, false));
			} catch (PrinterException e) {
				e.printStackTrace();
				printGarbage();
			}

		} else if (length < Math.abs(size)) {

			if (size < 0) {
				strformnat = "%1$-" + spaceSize + "." + spaceSize + "s";
				try {

					linePrinter.write(Arabic6822.Convert(arabic, false));
					linePrinter.write(String.format(strformnat, " "));
				} catch (PrinterException e) {
					e.printStackTrace();
					printGarbage();
				}

			} else {
				strformnat = "%1$" + spaceSize + "." + spaceSize + "s";
				try {

					linePrinter.write(String.format(strformnat, " "));
					linePrinter.write(Arabic6822.Convert(arabic, false));
				} catch (PrinterException e) {
					e.printStackTrace();
					printGarbage();
				}

			}
		} else {
			try {

				linePrinter.write(Arabic6822.Convert(arabic.substring(0, Math.abs(size) - 1), false));
			} catch (PrinterException e) {
				e.printStackTrace();
				printGarbage();
			}
		}

	}

	public void writeArAlignment(int size, String arabic) {

		if (arabic != null && arabic.length() > 0) {
			int length = arabic.length();
			int spaceSize = Math.abs(Math.abs(size) - length  );
			String strformnat = "";
			if (length <= Math.abs(size)) {
				if (spaceSize == 0) {
					try {

						linePrinter.write(Arabic6822.Convert(arabic, false));
					} catch (PrinterException e) {
						e.printStackTrace();
						printGarbage();
					}

				} else	if (size < 0) {
					strformnat = "%1$-" + spaceSize + "." + spaceSize + "s";
					try {
						linePrinter.write(Arabic6822.Convert(arabic, false));
						linePrinter.write(String.format(strformnat, " "));
					} catch (PrinterException e) {
						e.printStackTrace();
						printGarbage();
					}

				} else {
					strformnat = "%1$" + spaceSize + "." + spaceSize + "s";
					try {
						linePrinter.write(String.format(strformnat, " "));
						linePrinter.write(Arabic6822.Convert(arabic, false));
					} catch (PrinterException e) {
						e.printStackTrace();
						printGarbage();
					}

				}
			}
			else
			{
				try {

					linePrinter.write(Arabic6822.Convert(arabic.substring(0, Math.abs(size) - 1), false));
				} catch (PrinterException e) {
					e.printStackTrace();
					printGarbage();
				}

			}
		}
	}

	public void write(int size, String str) {
//		size = Math.abs(size);
		if (!TextUtils.isEmpty(str)) {
			int length = str.length();
			int spaceSize = Math.abs(Math.abs(size) - length);
			String strformnat = "";
			if (spaceSize == 0) {
				try {
					linePrinter.write(str);
				} catch (PrinterException e) {
					e.printStackTrace();
				}

			} else if (length <= Math.abs(size)) {
				if (size < 0) {
					strformnat = "%1$-" + spaceSize + "." + spaceSize + "s";
					try {
						linePrinter.write(str);
						linePrinter.write(String.format(strformnat, " "));
					} catch (PrinterException e) {
						e.printStackTrace();
						printGarbage();
					}

				} else {
					strformnat = "%1$" + spaceSize + "." + spaceSize + "s";
					try {
						linePrinter.write(String.format(strformnat, " "));
						linePrinter.write(str);
					} catch (PrinterException e) {
						e.printStackTrace();
						printGarbage();
					}

				}
			} else {
				try {
					linePrinter.write(str.substring(0, Math.abs(size) - 1));
				} catch (PrinterException e) {
					e.printStackTrace();
				}

			}
		}
		else
		{

			size =m(size);
			String strformnat = "%1$" + size + "." + size + "s";
			try {
				linePrinter.write(String.format(strformnat, " "));
			} catch (PrinterException e) {
				e.printStackTrace();
				printGarbage();
			}

		}
	}



	private int m(int i) {
		return Math.abs(i);
	}


	public void printRetOrder(String TRN, boolean isFromOS, JourneyPlanDO mallsDetails, OrderDO orderDO, Vector<ProductDO> vecProductDO, String TrxReasonCode, int linenumber, double grand_total, float totalDiscount, float totalTaxAmt) {



		maxPrintLenth = AppConstants.PRINTABLE_PAGE_HEIGHT;
		maxPrintLenth--;
//		String dotlines = ".. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. \r\n";
		if (strOrderId == null || strOrderId.equalsIgnoreCase(""))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
		String documentNo = strOrderId;
		String cNameEng = mallsDetails.siteName;
//		String customerAddress =mallsDetails.addresss1+","+mallsDetails.addresss2+","+mallsDetails.addresss3+","+mallsDetails.addresss4;
		String collectedBy = preference.getStringFromPreference(Preference.USER_NAME, "");
		String grvNo = orderDO.LPOCode;
		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		String customerTRN = mallsDetails.VatNumber;
//	 	float totalTaxAmt=0;
		String reasonForCredit = "";
//		reasonForCredit = isFromOS ? reasonForCredit = orderDO.TrxReasonCode : TrxReasonCode;
		reasonForCredit =   orderDO.TrxReasonCode+"";
		setCompress();

		write(70, TRN + "");
		writeArabic(R.string.return_order_header);
		writeText("\r\n");
		writeArAlignment(63, R.string.tax_credit_note);
		write(-30, "TAX  CREDIT NOTE");
		writeText("\r\n");

		write("(Document No)/");//14
		writeArabic(R.string.document_no);//11
		write(-50, " : " + documentNo);

		write("DATE:");
		writeArAlignment(8, R.string.date);
		write(mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM);
		writeText("\r\n");

		writeText(line);
		writeText("\r\n");
		write("CUSTOMER NAME : ");
		write(cNameEng);
		writeText("\r\n");
		writeArabic(R.string.customer_name);
		write(": ");
		if(isArabicEnable)
		{
			writeArabic(mallsDetails.SiteNameInArabic); //CUSTOMER NAME in Arabic
		}
		write("\r\n");

		write("CUSTOMER ADDRESS/");
		writeArabic(R.string.customer_address);
		writeSpace(5);
		if(isArabicEnable)
		{

			writeArabic(mallsDetails.Address1_AR);write(",");
			writeArabic(mallsDetails.Address2_AR);write(",");
			writeText("\r\n");
			writeArabic(mallsDetails.Address3_AR);write(",");
			writeArabic(mallsDetails.AreaName_AR);write(",");
			writeText("\r\n");
			writeArabic(mallsDetails.PostalCode_AR);write(",");
			writeArabic(mallsDetails.LocationName_AR);write(",");
			writeText("\r\n");
			writeArabic(mallsDetails.City_AR);
			writeText("\r\n");
		}

		writeText("\r\n");
//		writeSpace(34);
////		write( customerAddress);
//		writeText("\r\n");

//		writeArabic(R.string.customer_address);		//CUSTOMER ADDRESS in Arabic
//		writeText("\r\n");

		write("Collected By/"); //13
		writeArabic(R.string.collected_by); //16
		write(-40, collectedBy);
		write("Customer GRV No : "); //18
		write(grvNo);
		writeText("\r\n");

		write("Sub Inventory/"); //14
		writeArabic(R.string.collected_by); //15
		write(-40, subInventory);
		writeArabic(R.string.customer_grv_no);
		writeText("\r\n");

		write("CUST TRN# /");
		writeArabic(R.string.customer_trn_no);
		write(customerTRN);
		writeText("\r\n");
		write(line );
		writeText("\r\n");

		writeText("Please note that your account has been credited as follows:\r\n ");
		writeArabic(R.string.account_has_been);
		writeText("\r\n");

		int h2 = 15;//Inv Ref
		int h1 = 40;//particular
		int h3 = 8;//UOM
		int h4 = 8;//qty
		int h5 = 7;//rate
		int h6 = 10;//discount
		int h7 = 9;//Vat Rate
		int h8 = 9;//Vat Amt
		int h9 = 10;//total

		String header = "%1$" + h1 + "." + h1 + "s" +
				"%2$" + h2 + "." + h2 + "s" +
				"%3$" + h3 + "." + h3 + "s" +
				"%4$" + h4 + "." + h4 + "s" +
				"%5$" + h5 + "." + h5 + "s" +
				"%6$" + h6 + "." + h6 + "s" +
				"%7$" + h7 + "." + h7 + "s" +
				"%8$" + h8 + "." + h8 + "s" +
				"%9$" + h9 + "." + h9 + "s" +
				"\r\n";
		writeText(line );
		writeText(String.format(header, "Particulars", "Inv Ref", "UOM", "QTY", "RATE", "Discount", "VAT Rate", "VAT Amt", "Total"));
		writeText(line );
		writeArAlignment(h1, R.string.particulars);
		writeArAlignment(h2, R.string.inv_ref);
		writeArAlignment(h3, R.string.uom1);
		writeArAlignment(h4, R.string.qty);
		writeArAlignment(h5, R.string.rate);
		writeArAlignment(h6, R.string.discount_ret);
		writeArAlignment(h7, R.string.vat_rate1);
		writeArAlignment(h8, R.string.vat_amt1);
		writeArAlignment(h9, R.string.total);
		writeText("\r\n");

		writeArAlignment(h1 + h2 + h3, R.string.uom2);
		writeArAlignment(h4 + h5 + h6 + h7, R.string.vat_rate2);
		writeArAlignment(h8, R.string.vat_amt1);
		writeText("\r\n");
		writeText(line );

		String  detailAmount="";
		String detailDisc="";
//		float totalTAX=0;
//		float grand_total=0;
		int signature_height = 3;
		int footerTotal = 12;

		for (int i = linenumber; signature_height + footerTotal + (AppConstants.PRINTABLE_PAGE_HEIGHT - maxPrintLenth + 3) <= AppConstants.PRINTABLE_PAGE_HEIGHT && i < vecProductDO.size(); i++, linenumber++) {
//		for( int i = 0 ; i < vecProductDO.size() ; i++ ){

			ProductDO productDO = vecProductDO.get(i);
//			 detailAmount=new CaptureInventryDA().getOriginalAmount(productDO.SKU,productDO.RefTrxCode) ;
//			detailDisc=new CaptureInventryDA().getOriginalDisc(productDO.SKU,productDO.RefTrxCode);


			detailAmount=new CaptureInventryDA().getOriginalAmount(productDO.SKU,productDO.RefTrxCode);
			detailDisc=new CaptureInventryDA().getOriginalDisc(productDO.SKU,productDO.RefTrxCode);
			double CNAmt=StringUtils.getFloat(productDO.preUnits)*(productDO.itemPrice);
			totalPrice         += productDO.invoiceAmount;
			totalDiscount      += productDO.DiscountAmt*StringUtils.getFloat(productDO.preUnits);
//

			totalPrice += productDO.invoiceAmount;
			double discount = 0;
			double total = 0;
			if (isFromOS) {
				discount = Math.abs((StringUtils.getInt(productDO.units) * productDO.itemPrice) -Math.abs(productDO.invoiceAmount) + productDO.LineTaxAmount);
				totalDiscount += discount;
				total = productDO.invoiceAmount;//StringUtils.getInt(productDO.preUnits) * productDO.itemPrice + productDO.LineTaxAmount - discount;
				writeText(String.format(header, productDO.SKU,
						productDO.RefTrxCode,
						productDO.UOM,
						"" + productDO.units,
						"" + deffAmt.format(CNAmt),
						deffAmt.format(discount),
						"" + deffAmt.format(productDO.TaxPercentage),
						"" + deffAmt.format(productDO.LineTaxAmount),
						"" + deffAmt.format(total )));

//						"" + deffAmt.format(productDO.LineTaxAmount / StringUtils.getFloat(productDO.units)),

				totalTaxAmt += productDO.LineTaxAmount;
			} else {
//				discount = Math.abs((StringUtils.getInt(productDO.preUnits) * productDO.itemPrice) -Math.abs(productDO.invoiceAmount) + productDO.LineTaxAmount);
				discount = productDO.discountAmount;
				totalDiscount += discount;
				total = productDO.invoiceAmount;//StringUtils.getInt(productDO.preUnits) * productDO.itemPrice + productDO.OriginalLineTaxAmount - discount;
				writeText(String.format(header, productDO.SKU,
						productDO.RefTrxCode,
						productDO.UOM,
						"" + productDO.preUnits,
						"" + deffAmt.format(CNAmt),
						"" + deffAmt.format(discount),
//						"" + deffAmt.format(productDO.OriginalLineTaxAmount / StringUtils.getFloat(productDO.preUnits)),
						"" + deffAmt.format(productDO.TaxPercentage ),
						"" + deffAmt.format(productDO.OriginalLineTaxAmount),
						"" + deffAmt.format(total)));
				totalTaxAmt += productDO.OriginalLineTaxAmount;
			}

			writeText(productDO.Description+"\r\n");
			if(isArabicEnable)
			{
				writeArabic(productDO.Description1);
				writeText("\r\n");
			}

			write( "Ex. Date: ");
			writeArabic(R.string.ex_date);
			write(vecProductDO.get(i).strExpiryDate + ",  ");
			write("Lot. No.: ");
			writeArabic(R.string.lot_no);
			write(vecProductDO.get(i).LotNumber);
			writeText("\r\n");



			grand_total = grand_total + CNAmt;
		}
		writeText(line );
//----------------------------------
		if (linenumber < orderDO.vecProductDO.size())
		{
			printLines(maxPrintLenth- signature_height);
			writeText("\r\n");
			write("Customer Signature & seal");
			write(100, "Jumeirah Trading Co.Ltd");
			writeText("\r\n");
			writeArabic(R.string.customer_sign);
			writeArAlignment(100, R.string.jum_trading);
			writeText("\r\n");

			printToEnd();
//			write("----END--Return");//END
			printLines(5);
			adujstHalfLineHeightForEachPaper();
			giveDelay();
			printRetOrder( TRN,   isFromOS,   mallsDetails,   orderDO,  vecProductDO,   TrxReasonCode,  linenumber,   grand_total,   totalDiscount,   totalTaxAmt);
		} else {
			//----------------
			totalDiscount=0;
			writeArAlignment(88, R.string.grand_total);
			write(-20, " Grand Total : ");
			write(12, "" + deffAmt.format(grand_total));
			writeText("\r\n");

			writeArAlignment(88, R.string.discount);
			write(-20, " Discount  : ");
			write(12, "" + deffAmt.format(totalDiscount));
			writeText("\r\n");


			writeArAlignment(88, R.string.sub_total);
			write(-20, " Sub Total : ");
			write(12, "" + deffAmt.format(grand_total - totalDiscount));
			writeText("\r\n");

			writeArAlignment(88, R.string.vat_total);
			write(-20, " VAT Total : ");
			write(12, "" + deffAmt.format(totalTaxAmt));
			writeText("\r\n");

			writeArAlignment(88, R.string.net_total);
			write(-20, " Net Total : ");
			write(12, "" + deffAmt.format(grand_total - totalDiscount + totalTaxAmt));
			writeText("\r\n");

			writeArabic(R.string.credit_in_words);
			writeText("\r\n");

			String amountInWords= new NumberToEnglish().changeCurrencyToWords("" + deffAmt.format(grand_total - totalDiscount + totalTaxAmt));

			if (amountInWords != null && amountInWords.length() > 9) {
				amountInWords = amountInWords.substring(0, amountInWords.length() - 9)+" Halala only";
			}

			writeText("Amount of Credit in words : " + amountInWords+ "\r\n");


			writeSpace(29);
			writeArabic(((BaseActivity)context).numToArabicWord((grand_total - totalDiscount + totalTaxAmt)+""));
			writeText("\r\n");

			writeText("Reson for the Credit: " + reasonForCredit+"\r\n");
			writeArabic(R.string.reason_for_credit);
			writeText("\r\n");

			writeText("\r\n");

			write("Customer Signature & seal");
			write(100, "Jumeirah Trading Co.Ltd");
			writeText("\r\n");
			writeArabic(R.string.customer_sign);
			writeArAlignment(100, R.string.jum_trading);
			writeText("\r\n");

			printToEnd();
//			write("----END---Return");//END
			printLines(5);
			adujstHalfLineHeightForEachPaper();
		}



		/*	write("Customer Signature & seal");
			write(100, "Jumeirah Trading Co.Ltd");
			writeText("\r\n");
			writeArabic(R.string.customer_sign);
			writeArAlignment(100, R.string.jum_trading);
			writeText("\r\n");*/


	}
	private static  ArrayList<String>  splitStringBySize(String str, int size) {
		ArrayList<String> split = new ArrayList<String>();
		for (int i = 0; i <= str.length() / size; i++) {
			split.add(str.substring(i * size, Math.min((i + 1) * size, str.length())));
		}
		return split;
	}
	private void giveDelay()
	{
		try {
			Thread.sleep(10000);
			linePrinter.flush();
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	void printToEnd()
	{
		while(maxPrintLenth>0)
		{
			writeText("\r\n");
		}

	}
	void printLines(int noLines)
	{
		while(noLines>0)
		{
			writeText("\r\n");
			noLines--;
		}

	}


	private void printFooter(int signature_height, String str1, String str2)
	{
		printLines(maxPrintLenth   - signature_height-FOOTER_HEIGHT);
		writeText("\r\n"); //2
		String columns2 = "%1$-62.62s %2$-62.62s\r\n";
		str1 = (TextUtils.isEmpty(str1)|| str1.toLowerCase().equals("null")) ? "":str1;
		str2 = (TextUtils.isEmpty(str2)|| str2.toLowerCase().equals("null")) ? "":str2;
		writeText(String.format(columns2, str1, str2));
		writeText(line); //1
		printToEnd();
		printEndPageTAG();
		printLines(5);
		adujstHalfLineHeightForEachPaper();
	}



	void adujstHalfLineHeightForEachPaper()
	{
		PrinterConnectorArabic.PAGE_INDEX ++;
		//every 2 papers one extra line we need to print since it has been printing 59 and half lines for each paper
//		59.5+59.5=109 but it prints 108 lines. every even paper we need print extra emptyline
		if(PrinterConnectorArabic.PAGE_INDEX % 2 == 0)
		{
			writeText("\r\n");
		}

	}
	private void printEndPageTAG()
	{
		if(endPageTAGEnable)
		{
			write("---------------------END");//END
		}
	}


}


