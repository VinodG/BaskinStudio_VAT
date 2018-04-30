package com.winit.baskinrobbin.salesman.common;


import android.app.Activity;

import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;


public class ProductArray extends Activity
{
	private static String productCategoryName[]={"Water","Juice ","Chips ","Tissue "," Boutique "," Rice "," Robinson "};
	static String[] Mineral_Water={"Masafi Mineral Water 1.5 Ltr Ctn x 12","Masafi Mineral Water 1.5 Ltr S/W x 6*2","Masafi Mineral Water 500ml Ctn x 24",
			 "Masafi Mineral Water 500ml X 6 pack X 4","Masafi Mineral Water 330 ml Normal Logo X 24",
			 "Masafi Mineral Water 330ml X 6 pack X 4","Masafi Mineral Water 330 ml Kids Logo X 24",
			 "Masafi Mineral Water 330 ml Kids Logo 4 X 6pack (Sports)","Masafi Mineral Water 125 ml cups X 45",
			 "Masafi Mineral Water 200 ml cups X 30","Masafi Mineral Water 250 ml cups X 30",
			 "Masafi Mineral Water 4 Gallon","Masafi flv water-Lemon 12X500ml","Masafi flv. Water-Peach 12x500ml","Masafi flv. Water-Strawberry 12x500ml",
			 "Masafi flv. Water-Mint-Lemon 12x500ml"};
	
	static int[] Mineral_Water_Images = {};
	 
	 static String[] juice_Orange={"Masafi juice Orange 200ml X 24 (PET)","Masafi juice Apple 200 X 24 (PET)","Masafi juice Mango 200ml X 24 (PET)","Masafi juice Tropical 200ml X 24 (PET)",
			 "Masafi juice Black Berry 200ml X 24 (PET)","Masafi juice Citrus 200ml X 24 (PET)","Masafi juice Mellow Melon 200ml X 24 (PET)","Masafi juice Pineapple 200ml X 24 (PET)",
			 "Masafi juice Orange 1.0L X 6 pack (PET","Masafi juice Apple 1.0L X 6 pack (PET)","Masafi juice Mango 1.0L X 6 pack (PET)","Masafi juice Tropical 1.0L X 6 pack (PET)",
			 "Masafi juice Berry 1.0L X 6 pack (PET)","Masafi juice Citrus 1.0L X 6 pack (PET)","Masafi juice Melon 1.0L X 6 pack (PET)","Masafi juice Cranberry 1.0L X 6 (PET)","Masafi juice Pink Grapefruit 1.0L X 6 (PET)",
			 "Masafi juice Pineapple 1.0L X 6 (PET)","Masafi juice Orange 2.0L X 4 pack (PET)","Masafi juice Mango 2.0L X 4 pack (PET)","Masafi juice Tropical 2.0L X 4 pack (PET)",
			 "Masafi juice Citrus 2.0L X 4 pack (PET)","Masafi juice Melon 2.0L X 4 pack (PET)","Masafi juice Pineapple 2.0L X 4 (PET)","Masafi TP Juice Apple 250ml X 9 X 3 (Normal )",
			 "Masafi TP Juice Orange 250ml X 9 X 3 (Normal )","Masafi TP Juice Guava Lychee Pear 250ml X9 X 3 ( Normal )",
			 "Masafi TP Juice Pomegranate Raspberry Cranberry Acai 250ml X 9 X 3 ( Normal )","Masafi TP Juice Honey Dew Melon 250ml X 9 X 3 ( Normal )","Masafi TP Juice Banana Drink 250ml X 9 X 3 ( Normal )",
			 "Masafi TP Juice Apple 250ml X 9 X 3 ( KIDS )","Masafi TP Juice Orange 250ml X 9 X 3 ( KIDS )",
			 "Masafi TP Juice Guava Lychee Pear 250ml X9 X 3 ( KIDS )","Masafi TP Juice Strawberry Kiwi 250ml X 9 X 3 ( KIDS )","Masafi TP Juice Apple 1L X 12",
			 "Masafi TP Juice Orange 1L X 12","Masafi TP Juice Guava Lychee Pear 1L X 12","Masafi TP Juice Pomegranate Raspberry Cranberry Acai 1L X 12",
			 "Masafi TP Juice Honey Dew Melon 1L X 12","Masafi TP Juice Banana Drink 1L X 12"};
	 static int[] juice_Orange_Images = {};
	 static String[] BASMATI_RICE={"MASAFI BASMATI RICE 2kg X 10 Bags","MASAFI BASMATI RICE 5kg X 4 Bags","MASAFI BASMATI RICE 10kg X 2 Bags","MASAFI BASMATI RICE 20kg X 1 Bag"};
	 
	 static String[] Orange_Barley={"Robinson Orange Barley 1.0L X 12","Robinson Lemon Barley 1.0L X 12","Robinson Peach Fruit Barley (NAS) 1.0L X 12",
			 "Robinson Orange Fruit Barley (NAS) 1.0L X 12","Robinson Summer Fruit Barley (NAS) 1.0L X 12","Robinson Orange - (NAS) 1.0L X 12","Robinson Lemon (NAS) 1.0L X 12",
			 "Robinson Apple & Black Current (NAS) 1.0L X 12","Robinson Summer Fruit (NAS) 1.0L X 12","Robinson Orange & Pineapple (NAS) 1.0L X 12",
			 "Robinson Orange Fruit Squash 1.0L X 12","Robinson Apple & Black Currant Fruit Squash 1.0L X 12",
			 "Robinson Orange & Pineapple Fruit Squash 1.0L X 12","Robinson Orange 300ML X 24",
			 "Robinson Apple & Blackcurrant300ML X 24","Robinson Strawberry & Rasberry 300ML X 24",
			 "Robinson Orange 200ML X 6 X 4","Robinson Apple & Blackcurrant 200ML X 6 X 4"};
	 
	 static String[] Masafi_Chips = {"Masafi Chips - Sea Salt 25g X 60","Masafi Chips - Sea Salt 100g X 48",
			 				  "Masafi Chips - Four Cheese 25g X 60","Masafi Chips - Four Cheese 100g X 48",
			 				  "Masafi Chips - Sweet Chilli 25g X 60","Masafi Chips - Sweet Chilli 100g X 48",
			 				  "Masafi Chips - Labneh & Zaatar 25g X 60","Masafi Chips - Labneh & Zaatar 100g X 48",
			 				  "Masafi Chips - Salt & Vinegar 25g X 60","Masafi Chips - Salt & Vinegar 100g X 48"};
	 
	 static String[] Masafi_Tissue = {"Masafi Tissue White 150 x 5 x 6","Masafi Tissue Floral 150 x 5 x 6",
			 				  "Masafi Tissue White 200 x 4 x 6","Masafi Tissue Floral 200 x 4 x 6",
			 				  "Masafi Car Tissue 70 x 36","Masafi Kids Tissue 100 x 24",
			 				  "Masafi Boutique White Tissue 100 x 24","Masafi Boutique-Rose 100x24",
			 				  "Masafi boutique-Oud 100x24","Masafi boutique-Sense of Spring 100x24",
			 				  "Masafi boutique-Lavender 100x24","Masafi Tissue White 150 x 6 x 5 ( 5 + 1 )",
			 				  "Masafi Tissue Floral 150 x 6 x 5 ( 5 + 1 )","Masafi Tissue White 200 x 6 x 4 ( 4 + 1)",
			 				  "Masafi Tissue Floral 200 x 6 x 4 ( 4 + 1)","Masafi Tissue Floral 150 x 6 x 5 ( 4 + 1 )"};
	 
	 static String[] Masafi_FREEZ = {"FREEZ LEMON ICE 275ml X 24","FREEZ STRAWBERRY 275ml X 24",
			 				  "FREEZ GRENADIN 275ml X 24","FREEZ BLACK BERRY 275ml X 24",
			 				  "FREEZ APPLE 275ml X 24","FREEZ PINEAPPLE 275ml X 24",
			 				  "FREEZ PEACH 275ml X 24","FREEZ TAMARIND 275ml X 24",
			 				  "FREEZ KIWI 275ml X 24","FREEZ LEMON MINT 275ml X 24",
			 				  "FREEZ MANDRIAN 275ml X 24","FREEZ LEMON ICE DIET 275ml X 24",
			 				  "FREEZ GRENADIN DIET 275ml X 24","FREEZ BLUE HAWAII 275ml X 24",
			 				  "FREEZ ENERGY DRINK 275ml X 24","FRUTO APPLE 300 ML x 24",
			 				  "FRUTO EXITOC 300 ML x 24","FRUTO MANGO 300 ML x 24",
			 				  "FRUTO GUAVA 300 ML x 24","FRUTO ORANGE 300 ML x 24",
			 				  "FRUTO ORANGE &CARROTS 300 ML x 24","FRUTO PINEAPPLE 300 ML x 24",
			 				  "FRUTO STRAWBERRY 300 ML x 24","ROCKSTAR REGULAR Energy Drink 500ML X 24",
			 				  "ROCKSTAR GUAVA Energy Drink 500ML X 24","ROCKSTAR REGULAR Energy Drink 250ML X 24" };
	 
	 static String[] Masafi_Robinson = {"Robinson Orange Barley 1.0L X 12","Robinson Lemon Barley 1.0L X 12",
			 					 "Robinson Peach Fruit Barley (NAS) 1.0L X 12","Robinson Orange Fruit Barley (NAS) 1.0L X 12",
			 					 "Robinson Summer Fruit Barley (NAS) 1.0L X 12","Robinson Orange - (NAS) 1.0L X 12",
			 					 "Robinson Lemon (NAS) 1.0L X 12","Robinson Apple & Black Current (NAS) 1.0L X 12",
			 					 "Robinson Summer Fruit (NAS) 1.0L X 12","Robinson Orange & Pineapple (NAS) 1.0L X 12",
			 					 "Robinson Orange Fruit Squash 1.0L X 12","Robinson Apple & Black Currant Fruit Squash 1.0L X 12",
			 					 "Robinson Orange & Pineapple Fruit Squash 1.0L X 12","Robinson Orange 300ML X 24",
			 					 "Robinson Apple & Blackcurrant300ML X 24","Robinson Strawberry & Rasberry 300ML X 24",
			 					 "Robinson Orange 200ML X 6 X 4","Robinson Apple & Blackcurrant 200ML X 6 X 4"};

	 
	 public static int [] arrJuices = {R.drawable.sp};
	 
	 public static int [] arrWater = {R.drawable.sp};

	 public static int [] arrDrinks = {R.drawable.sp};
	 
	 public static String[] getDescription(String strCategoryName)
	 {
		 if(strCategoryName.equalsIgnoreCase(productCategoryName[0]))
		 {
			 return Mineral_Water;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[1]))
		 {
			 return juice_Orange;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[2]))
		 {
			 return Masafi_FREEZ;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[3]))
		 {
			 return juice_Orange;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[4]))
		 {
			 return Mineral_Water;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[5]))
		 {
			 return Masafi_FREEZ;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[6]))
		 {
			 return Mineral_Water;
		 }
		 
		return Mineral_Water;
	 }
	 public static int[] getImages(String strCategoryName)
	 {
		 LogUtils.infoLog("strCategoryName",""+strCategoryName);
		 if(strCategoryName.equalsIgnoreCase(productCategoryName[0]))
		 {
			 return arrWater;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[1]))
		 {
			 return arrJuices;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[2]))
		 {
			 return arrDrinks;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[3]))
		 {
			 return arrJuices;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[4]))
		 {
			 return arrWater;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[5]))
		 {
			 return arrDrinks;
		 }
		 else if(strCategoryName.equalsIgnoreCase(productCategoryName[6]))
		 {
			 return arrWater;
		 }
		 
		return arrWater;
	 }
}
