package com.winit.baskinrobbin.salesman.utilities;

import android.content.Context;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.AppConstants;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * This code was migrated from C# and hence will be shared back to the C# owner.
 * This code is shared to the net.
 * 
 * @author Michael.Toledo
 *
 */
public class NumberToArabic {
	private static BigDecimal number;
	private static Currency currency;
	private static CurrencyInfo currencyInfo;	
	
	private static String englishPrefixText = "";
	private static String englishSuffixText = "only.";
	private static String arabicPrefixText = "فقط";
	private static String arabicSuffixText = "لا غير.";
	
	private static long _intergerValue;
	private static int _decimalValue;

	private static int PartPrecision=2;
    Context  context;
    private DecimalFormat deffAmt, amountFormate, qtyFormater, qtyFormaterNew, percentFormat;
    public NumberToArabic(Context baseActivity)
    {
        context=baseActivity;
    }

    /**
	 * BHD - not implemented
	 * JOD - not implemented
	 */
	public static enum Currency {
		AED, SYP, SAR, TND, XAU, JOD, BHD
	}
	
	private static String[] englishOnes =
        new String[] {
         "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
         "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

	private static String[] englishTens =
	     new String[] {
	     "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
	};
	
	private static String[] englishGroup =
	     new String[] {
	     "Hundred", "Thousand", "Million", "Billion", "Trillion", "Quadrillion", "Quintillion", "Sextillian",
	     "Septillion", "Octillion", "Nonillion", "Decillion", "Undecillion", "Duodecillion", "Tredecillion",
	     "Quattuordecillion", "Quindecillion", "Sexdecillion", "Septendecillion", "Octodecillion", "Novemdecillion",
	     "Vigintillion", "Unvigintillion", "Duovigintillion", "10^72", "10^75", "10^78", "10^81", "10^84", "10^87",
	     "Vigintinonillion", "10^93", "10^96", "Duotrigintillion", "Trestrigintillion"
	};
	
	private static String[] arabicOnes =
        new String[] {
         "", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة",
         "عشرة", "أحد عشر", "اثنا عشر", "ثلاثة عشر", "أربعة عشر", "خمسة عشر", "ستة عشر", "سبعة عشر", "ثمانية عشر", "تسعة عشر"
     };

     private static String[] arabicFeminineOnes =
        new String[] {
         "", "إحدى", "اثنتان", "ثلاث", "أربع", "خمس", "ست", "سبع", "ثمان", "تسع",
         "عشر", "إحدى عشرة", "اثنتا عشرة", "ثلاث عشرة", "أربع عشرة", "خمس عشرة", "ست عشرة", "سبع عشرة", "ثماني عشرة", "تسع عشرة"
     };

     private static String[] arabicTens =
         new String[] {
         "عشرون", "ثلاثون", "أربعون", "خمسون", "ستون", "سبعون", "ثمانون", "تسعون"
     };

     private static String[] arabicHundreds =
         new String[] {
         "", "مائة", "مئتان", "ثلاثمائة", "أربعمائة", "خمسمائة", "ستمائة", "سبعمائة", "ثمانمائة","تسعمائة"
     };

     private static String[] arabicAppendedTwos =
         new String[] {
         "مئتا", "ألفا", "مليونا", "مليارا", "تريليونا", "كوادريليونا", "كوينتليونا", "سكستيليونا"
     };

     private static String[] arabicTwos =
         new String[] {
         "مئتان", "ألفان", "مليونان", "ملياران", "تريليونان", "كوادريليونان", "كوينتليونان", "سكستيليونان"
     };

     private static String[] arabicGroup =
         new String[] {
         "مائة", "ألف", "مليون", "مليار", "تريليون", "كوادريليون", "كوينتليون", "سكستيليون"
     };

     private static String[] arabicAppendedGroup =
         new String[] {
         "", "ألفاً", "مليوناً", "ملياراً", "تريليوناً", "كوادريليوناً", "كوينتليوناً", "سكستيليوناً"
     };

     private static String[] arabicPluralGroups =
         new String[] {
         "", "آلاف", "ملايين", "مليارات", "تريليونات", "كوادريليونات", "كوينتليونات", "سكستيليونات"
     };
	
    public NumberToArabic() {	
    	 
    }
     
	public NumberToArabic(BigDecimal number, Currency currency) {	
		NumberToArabic.number = number;		
		NumberToArabic.currency = currency;
		NumberToArabic.currencyInfo = new CurrencyInfo(currency);
		
		numberToArabic(number, currencyInfo, englishPrefixText, englishSuffixText, arabicPrefixText, arabicSuffixText);
	}
	
	public NumberToArabic(BigDecimal number, Currency currency, String englishPrefixText, String englishSuffixText, String arabicPrefixText, String arabicSuffixText) {
		NumberToArabic.number = number;
		NumberToArabic.currency = currency;
		NumberToArabic.currencyInfo = new CurrencyInfo(currency);
		
		numberToArabic(number, currencyInfo, englishPrefixText, englishSuffixText, arabicPrefixText, arabicSuffixText);
	}
	
	private static void numberToArabic(BigDecimal number, CurrencyInfo currency, String englishPrefixText, String englishSuffixText, String arabicPrefixText, String arabicSuffixText) {		
        NumberToArabic.englishPrefixText = englishPrefixText;
        NumberToArabic.englishSuffixText = englishSuffixText;
        NumberToArabic.arabicPrefixText = arabicPrefixText;
        NumberToArabic.arabicSuffixText = arabicSuffixText;

        extractIntegerAndDecimalParts();
	}
	
	private static void extractIntegerAndDecimalParts() {		
        String[] splits = number.toString().split("\\.");

        _intergerValue = Long.valueOf(splits[0]).longValue();

        if (splits.length > 1)
            _decimalValue = Integer.valueOf(getDecimalValue(splits[1]));
        else 
        	_decimalValue = 0;
    }
	
	private static String getDecimalValue(String decimalPart) {
		String result = "";

        if (currencyInfo.getPartPrecision() != decimalPart.length()) {
                int decimalPartLength = decimalPart.length();

                for (int i = 0; i <  PartPrecision - decimalPartLength; i++)
                {
                    decimalPart += "0"; //Fix for 1 number after decimal ( 10.5 , 1442.2 , 375.4 ) 
                }

				int dec = decimalPart.length() <= currencyInfo.getPartPrecision()  ?  decimalPart.length() : currencyInfo.getPartPrecision();  
            result = decimalPart.substring(0, dec);
        }
        else
            result = decimalPart;

        for (int i = result.length(); i < currencyInfo.getPartPrecision(); i++) {
            result += "0";
        }

        return result;
    }
	
    private static String processGroup(int groupNumber) {
        int tens = groupNumber % 100;

        int hundreds = groupNumber / 100;

        String retVal = "";

        if (hundreds > 0) {
            retVal = String.format("%s %s", englishOnes[hundreds], englishGroup[0]);
        }
        if (tens > 0) {
            if (tens < 20) {
                retVal += ((retVal != "") ? " " : "") + englishOnes[tens];
            }
            else {
                int ones = tens % 10;

                tens = (tens / 10) - 2; // 20's offset

                retVal += ((retVal != "") ? " " : "") + englishTens[tens];

                if (ones > 0) {
                    retVal += ((retVal != "") ? " " : "") + englishOnes[ones];
                }
            }
        }

        return retVal;
    }	
    
    public static String convertToEnglish(BigDecimal value, String currencyCode) {    	   	
    	currency = Currency.valueOf(currencyCode);
    	currencyInfo = (new NumberToArabic()).new CurrencyInfo(currency);
    	number = value.setScale(currencyInfo.getPartPrecision(), BigDecimal.ROUND_HALF_DOWN);
    	
    	numberToArabic(number, currencyInfo, englishPrefixText, englishSuffixText, arabicPrefixText, arabicSuffixText);
    	
		return convertToEnglish();
    }
	
    public static String convertToEnglish() {
        BigDecimal tempNumber = number;

        if (tempNumber.compareTo(new BigDecimal(0)) == 0)
            return "Zero";

        String decimalString = processGroup(_decimalValue);

        String retVal = "";

        int group = 0;

        if (tempNumber.compareTo(new BigDecimal(0)) < 1)  {
            retVal = englishOnes[0];
        }
        else {
            while (tempNumber.compareTo(new BigDecimal(0)) > 0) {
                int numberToProcess = tempNumber.remainder(new BigDecimal(1000)).intValue();

                tempNumber = tempNumber.divideToIntegralValue(new BigDecimal(1000));

                String groupDescription = processGroup(numberToProcess);

                if (groupDescription != "") {
                    if (group > 0) {
                        retVal = String.format("%s %s", englishGroup[group], retVal);
                    }

                    retVal = String.format("%s %s", groupDescription, retVal);
                }

                group++;
            }
        }

        String formattedNumber = "";
        formattedNumber += (englishPrefixText != "") ? String.format("%s ", englishPrefixText) : "";
        formattedNumber += (retVal != "") ? retVal : "";
        formattedNumber += (retVal != "") ? (_intergerValue == 1 ? currencyInfo.englishCurrencyName : currencyInfo.englishPluralCurrencyName) : "";
        formattedNumber += (decimalString != "") ? " and " : "";
        formattedNumber += (decimalString != "") ? decimalString : "";
        formattedNumber += (decimalString != "") ? " " + (_decimalValue == 1 ? currencyInfo.englishCurrencyPartName : currencyInfo.englishPluralCurrencyPartName) : "";
        formattedNumber += (englishSuffixText != "") ? String.format(" %s", englishSuffixText) : "";

        return formattedNumber;
    }
    
    private static String getDigitFeminineStatus(int digit, int groupLevel) {
        if (groupLevel == -1) { // if it is in the decimal part
            if (currencyInfo.isCurrencyPartNameFeminine)
                return arabicFeminineOnes[digit]; // use feminine field
            else
                return arabicOnes[digit];
        }
        else
            if (groupLevel == 0) {
                if (currencyInfo.isCurrencyNameFeminine)
                    return arabicFeminineOnes[digit];// use feminine field
                else
                    return arabicOnes[digit];
            }
            else
                return arabicOnes[digit];
    }
    
    private static String processArabicGroup(int groupNumber, int groupLevel, BigDecimal remainingNumber) {
        int tens = groupNumber % 100;

        int hundreds = groupNumber / 100;

        String retVal = "";

        if (hundreds > 0) {
            if (tens == 0 && hundreds == 2) // حالة المضاف
                retVal = String.format("%s", arabicAppendedTwos[0]);
            else //  الحالة العادية
                retVal = String.format("%s", arabicHundreds[hundreds]);
        }

        if (tens > 0) {
            if (tens < 20) { // if we are processing under 20 numbers
                if (tens == 2 && hundreds == 0 && groupLevel > 0) { // This is special case for number 2 when it comes alone in the group
                    if (_intergerValue == 2000 || _intergerValue == 2000000 || _intergerValue == 2000000000 || _intergerValue == 2000000000000L || _intergerValue == 2000000000000000L || _intergerValue == 2000000000000000000L)
                        retVal = String.format("%s", arabicAppendedTwos[groupLevel]); // في حالة الاضافة
                    else
                        retVal = String.format("%s", arabicTwos[groupLevel]);//  في حالة الافراد
                }
                else { // General case
                    if (retVal != "")
                        retVal += " و ";

                    if (tens == 1 && groupLevel > 0 && hundreds == 0)
                        retVal += " ";
                    else
                        if ((tens == 1 || tens == 2) && (groupLevel == 0 || groupLevel == -1) && hundreds == 0 && remainingNumber.compareTo(new BigDecimal(0)) == 0)
                            retVal += ""; // Special case for 1 and 2 numbers like: ليرة سورية و ليرتان سوريتان
                        else
                            retVal += getDigitFeminineStatus(tens, groupLevel);// Get Feminine status for this digit
                }
            }
            else {
                int ones = tens % 10;
                tens = (tens / 10) - 2; // 20's offset

                if (ones > 0) {
                    if (retVal != "")
                        retVal += " و ";

                    // Get Feminine status for this digit
                    retVal += getDigitFeminineStatus(ones, groupLevel);
                }

                if (retVal != "")
                    retVal += " و ";

                // Get Tens text
                retVal += arabicTens[tens];
            }
        }

        return retVal;
    }
    
    
    public static String convertToArabic(BigDecimal value, String currencyCode) {
    	currency = Currency.valueOf(currencyCode);
    	currencyInfo = (new NumberToArabic()).new CurrencyInfo(currency);
    	number = value.setScale(currencyInfo.getPartPrecision(), BigDecimal.ROUND_HALF_DOWN);
    	
    	numberToArabic(number, currencyInfo, englishPrefixText, englishSuffixText, arabicPrefixText, arabicSuffixText);
    	
		return convertToArabic();
    }
    
    public static String convertToArabic()
    {
        BigDecimal tempNumber = number;

        if (tempNumber.compareTo(new BigDecimal(0)) == 0)
            return "صفر";

        // Get Text for the decimal part
        String decimalString = processArabicGroup(_decimalValue, -1, new BigDecimal(0));

        String retVal = ""; 
        Byte group = 0;
        while (tempNumber.compareTo(new BigDecimal(0)) > 0)
        {
            // seperate number into groups
            int numberToProcess = tempNumber.remainder(new BigDecimal(1000)).intValue();

            tempNumber = tempNumber.divideToIntegralValue(new BigDecimal(1000));

            // convert group into its text
            String groupDescription = processArabicGroup(numberToProcess, group, new BigDecimal(Math.floor(tempNumber.doubleValue())));

            if (groupDescription != "")
            { // here we add the new converted group to the previous concatenated text
                if (group > 0)
                {
                    if (retVal != "")
                        retVal = String.format("%s %s", "و", retVal);

                    if (numberToProcess != 2)
                    {
                        if (numberToProcess % 100 != 1)
                        {
                            if (numberToProcess >= 3 && numberToProcess <= 10) // for numbers between 3 and 9 we use plural name
                                retVal = String.format("%s %s", arabicPluralGroups[group], retVal);
                            else
                            {
                                if (retVal != "") // use appending case
                                    retVal = String.format("%s %s", arabicAppendedGroup[group], retVal);
                                else
                                    retVal = String.format("%s %s", arabicGroup[group], retVal); // use normal case
                            }
                        }
						else
							retVal = String.format("%s %s", arabicGroup[group], retVal); // use normal case
                    }
                }

                retVal = String.format("%s %s", groupDescription, retVal);
            }

            group++;
        }

        String formattedNumber = "";
        formattedNumber += (arabicPrefixText != "") ? String.format("%s ", arabicPrefixText) : "";
        formattedNumber += (retVal != "") ? retVal : "";
        if (_intergerValue != 0)
        { // here we add currency name depending on _intergerValue : 1 ,2 , 3--->10 , 11--->99
            int remaining100 = (int)(_intergerValue % 100);

            if (remaining100 == 0)
                formattedNumber += currencyInfo.arabic1CurrencyName;
            else
                if (remaining100 == 1)
                    formattedNumber += currencyInfo.arabic1CurrencyName;
                else
                    if (remaining100 == 2)
                    {
                        if (_intergerValue == 2)
                            formattedNumber += currencyInfo.arabic2CurrencyName;
                        else
                            formattedNumber += currencyInfo.arabic1CurrencyName;
                    }
                    else
                        if (remaining100 >= 3 && remaining100 <= 10)
                            formattedNumber += currencyInfo.arabic310CurrencyName;
                        else
                            if (remaining100 >= 11 && remaining100 <= 99)
                                formattedNumber += currencyInfo.arabic1199CurrencyName;
        }
        formattedNumber += (_decimalValue != 0) ? " و " : "";
        formattedNumber += (_decimalValue != 0) ? decimalString : "";
        if (_decimalValue != 0)
        { // here we add currency part name depending on _intergerValue : 1 ,2 , 3--->10 , 11--->99
            formattedNumber += " ";

            int remaining100 = (int)(_decimalValue % 100);

            if (remaining100 == 0)
                formattedNumber += currencyInfo.arabic1CurrencyPartName;
            else
                if (remaining100 == 1)
                    formattedNumber += currencyInfo.arabic1CurrencyPartName;
                else
                    if (remaining100 == 2)
                        formattedNumber += currencyInfo.arabic2CurrencyPartName;
                    else
                        if (remaining100 >= 3 && remaining100 <= 10)
                            formattedNumber += currencyInfo.arabic310CurrencyPartName;
                        else
                            if (remaining100 >= 11 && remaining100 <= 99)
                                formattedNumber += currencyInfo.arabic1199CurrencyPartName;
        }
        formattedNumber += (arabicSuffixText != "") ? String.format(" %s", arabicSuffixText) : "";

        return formattedNumber;
    }
    
    
    
	class CurrencyInfo {
		Currency currencyID;
		String currencyCode;
        boolean isCurrencyNameFeminine;
        String englishCurrencyName;
        String englishPluralCurrencyName;
        String englishCurrencyPartName;
        String englishPluralCurrencyPartName;
        String arabic1CurrencyName;
        String arabic2CurrencyName;
        String arabic310CurrencyName;
        String arabic1199CurrencyName;
        String arabic1CurrencyPartName;
        String arabic2CurrencyPartName;
        String arabic310CurrencyPartName;
        String arabic1199CurrencyPartName;
        int partPrecision;
        boolean isCurrencyPartNameFeminine;
		
        public Currency getCurrencyID() {
			return currencyID;
		}

		public void setCurrencyID(Currency currencyID) {
			this.currencyID = currencyID;
		}

		public String getCurrencyCode() {
			return currencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}

		public boolean isCurrencyNameFeminine() {
			return isCurrencyNameFeminine;
		}

		public void setCurrencyNameFeminine(boolean isCurrencyNameFeminine) {
			this.isCurrencyNameFeminine = isCurrencyNameFeminine;
		}

		public String getEnglishCurrencyName() {
			return englishCurrencyName;
		}

		public void setEnglishCurrencyName(String englishCurrencyName) {
			this.englishCurrencyName = englishCurrencyName;
		}

		public String getEnglishPluralCurrencyName() {
			return englishPluralCurrencyName;
		}

		public void setEnglishPluralCurrencyName(String englishPluralCurrencyName) {
			this.englishPluralCurrencyName = englishPluralCurrencyName;
		}

		public String getEnglishCurrencyPartName() {
			return englishCurrencyPartName;
		}

		public void setEnglishCurrencyPartName(String englishCurrencyPartName) {
			this.englishCurrencyPartName = englishCurrencyPartName;
		}

		public String getEnglishPluralCurrencyPartName() {
			return englishPluralCurrencyPartName;
		}

		public void setEnglishPluralCurrencyPartName(
				String englishPluralCurrencyPartName) {
			this.englishPluralCurrencyPartName = englishPluralCurrencyPartName;
		}

		public String getArabic1CurrencyName() {
			return arabic1CurrencyName;
		}

		public void setArabic1CurrencyName(String arabic1CurrencyName) {
			this.arabic1CurrencyName = arabic1CurrencyName;
		}

		public String getArabic2CurrencyName() {
			return arabic2CurrencyName;
		}

		public void setArabic2CurrencyName(String arabic2CurrencyName) {
			this.arabic2CurrencyName = arabic2CurrencyName;
		}

		public String getArabic310CurrencyName() {
			return arabic310CurrencyName;
		}

		public void setArabic310CurrencyName(String arabic310CurrencyName) {
			this.arabic310CurrencyName = arabic310CurrencyName;
		}

		public String getArabic1199CurrencyName() {
			return arabic1199CurrencyName;
		}

		public void setArabic1199CurrencyName(String arabic1199CurrencyName) {
			this.arabic1199CurrencyName = arabic1199CurrencyName;
		}

		public String getArabic1CurrencyPartName() {
			return arabic1CurrencyPartName;
		}

		public void setArabic1CurrencyPartName(String arabic1CurrencyPartName) {
			this.arabic1CurrencyPartName = arabic1CurrencyPartName;
		}

		public String getArabic2CurrencyPartName() {
			return arabic2CurrencyPartName;
		}

		public void setArabic2CurrencyPartName(String arabic2CurrencyPartName) {
			this.arabic2CurrencyPartName = arabic2CurrencyPartName;
		}

		public String getArabic310CurrencyPartName() {
			return arabic310CurrencyPartName;
		}

		public void setArabic310CurrencyPartName(String arabic310CurrencyPartName) {
			this.arabic310CurrencyPartName = arabic310CurrencyPartName;
		}

		public String getArabic1199CurrencyPartName() {
			return arabic1199CurrencyPartName;
		}

		public void setArabic1199CurrencyPartName(String arabic1199CurrencyPartName) {
			this.arabic1199CurrencyPartName = arabic1199CurrencyPartName;
		}

		public int getPartPrecision() {
			return partPrecision;
		}

		public void setPartPrecision(int partPrecision) {
			this.partPrecision = partPrecision;
		}

		public boolean isCurrencyPartNameFeminine() {
			return isCurrencyPartNameFeminine;
		}

		public void setCurrencyPartNameFeminine(boolean isCurrencyPartNameFeminine) {
			this.isCurrencyPartNameFeminine = isCurrencyPartNameFeminine;
		}
		
		public CurrencyInfo(Currency currency) {
			switch (currency) {
				case AED : 	currencyID = currency;
                			currencyCode = currency.toString();
                			isCurrencyNameFeminine = false;
                			englishCurrencyName = "UAE Dirham";
                			englishPluralCurrencyName = "UAE Dirhams";
                			englishCurrencyPartName = "Fils";
                			englishPluralCurrencyPartName = "Fils";
                			arabic1CurrencyName = "درهم إماراتي";
                			arabic2CurrencyName = "درهمان إماراتيان";
                			arabic310CurrencyName = "دراهم إماراتية";
                			arabic1199CurrencyName = "درهماً إماراتياً";
                			arabic1CurrencyPartName = "فلس";
                			arabic2CurrencyPartName = "فلسان";
                			arabic310CurrencyPartName = "فلوس";
                			arabic1199CurrencyPartName = "فلساً";
                			partPrecision = 2;
                			isCurrencyPartNameFeminine = false;					
					break;
				case JOD :  currencyID = currency;
    						currencyCode = currency.toString();
    						isCurrencyNameFeminine = false;
    						englishCurrencyName = "Jordanian Dinar";
    						englishPluralCurrencyName = "Jordanian Dinars";
    						englishCurrencyPartName = "Fils";
    						englishPluralCurrencyPartName = "Fils";
    						arabic1CurrencyName = "دينار أردني";
    						arabic2CurrencyName = "ديناران أردنيان";
    						arabic310CurrencyName = "دنانير أردنية";
    						arabic1199CurrencyName = "ديناراً أردنياً";
    						arabic1CurrencyPartName = "فلس";
    						arabic2CurrencyPartName = "فلسان";
    						arabic310CurrencyPartName = "فلوس";
    						arabic1199CurrencyPartName = "فلساً";
    						partPrecision = 3;
    						isCurrencyPartNameFeminine = false;
    				break;
				case BHD : 	currencyID = currency;
							currencyCode = currency.toString();
							isCurrencyNameFeminine = false;
							englishCurrencyName = "Bahraini Dinar";
							englishPluralCurrencyName = "Bahraini Dinars";
							englishCurrencyPartName = "Fils";
							englishPluralCurrencyPartName = "Fils";
							arabic1CurrencyName = "دينار بحريني";
							arabic2CurrencyName = "ديناران بحرينيان";
							arabic310CurrencyName = "دنانير بحرينية";
							arabic1199CurrencyName = "ديناراً بحرينياً";
							arabic1CurrencyPartName = "فلس";
							arabic2CurrencyPartName = "فلسان";
							arabic310CurrencyPartName = "فلوس";
							arabic1199CurrencyPartName = "فلساً";
							partPrecision = 3;
							isCurrencyPartNameFeminine = false;
					break;
				case SAR :  currencyID = currency;
                			currencyCode = currency.toString();	
                			isCurrencyNameFeminine = false;
                			englishCurrencyName = "Saudi Riyal";
                			englishPluralCurrencyName = "Saudi Riyals";
                			englishCurrencyPartName = "Halala";
                			englishPluralCurrencyPartName = "Halalas";
                			arabic1CurrencyName = "ريال سعودي";
                			arabic2CurrencyName = "ريالان سعوديان";
                			arabic310CurrencyName = "ريالات سعودية";
                			arabic1199CurrencyName = "ريالاً سعودياً";
                			arabic1CurrencyPartName = "هللة";
                			arabic2CurrencyPartName = "هللتان";
                			arabic310CurrencyPartName = "هللات";
                			arabic1199CurrencyPartName = "هللة";
                			partPrecision = 2;
                			isCurrencyPartNameFeminine = true;
					break;
				case SYP : 	currencyID = currency;
                			currencyCode = currency.toString();
                			isCurrencyNameFeminine = true;
                			englishCurrencyName = "Syrian Pound";
                			englishPluralCurrencyName = "Syrian Pounds";
                			englishCurrencyPartName = "Piaster";
                			englishPluralCurrencyPartName = "Piasteres";
                			arabic1CurrencyName = "ليرة سورية";
                			arabic2CurrencyName = "ليرتان سوريتان";
                			arabic310CurrencyName = "ليرات سورية";
                			arabic1199CurrencyName = "ليرة سورية";
                			arabic1CurrencyPartName = "قرش";
                			arabic2CurrencyPartName = "قرشان";
                			arabic310CurrencyPartName = "قروش";
                			arabic1199CurrencyPartName = "قرشاً";
                			partPrecision = 2;
                			isCurrencyPartNameFeminine = false; 
					break;
				case TND : 	currencyID = currency;
                			currencyCode = currency.toString();
                			isCurrencyNameFeminine = false;
                			englishCurrencyName = "Tunisian Dinar";
                			englishPluralCurrencyName = "Tunisian Dinars";
                			englishCurrencyPartName = "milim";
                			englishPluralCurrencyPartName = "millimes";
                			arabic1CurrencyName = "درهم إماراتي";
                			arabic2CurrencyName = "درهمان إماراتيان";
                			arabic310CurrencyName = "دراهم إماراتية";
                			arabic1199CurrencyName = "درهماً إماراتياً";
                			arabic1CurrencyPartName = "فلس";
                			arabic2CurrencyPartName = "فلسان";
                			arabic310CurrencyPartName = "فلوس";
                			arabic1199CurrencyPartName = "فلساً";
                			partPrecision = 3;
                			isCurrencyPartNameFeminine = false;
					break;
				case XAU : 	currencyID = currency;
                			currencyCode = currency.toString();
                			isCurrencyNameFeminine = false;
                			englishCurrencyName = "Gram";
                			englishPluralCurrencyName = "Grams";
                			englishCurrencyPartName = "Milligram";
                			englishPluralCurrencyPartName = "Milligrams";
                			arabic1CurrencyName = "جرام";
                			arabic2CurrencyName = "جرامان";
                			arabic310CurrencyName = "جرامات";
                			arabic1199CurrencyName = "جراماً";
                			arabic1CurrencyPartName = "ملجرام";
                			arabic2CurrencyPartName = "ملجرامان";
                			arabic310CurrencyPartName = "ملجرامات";
                			arabic1199CurrencyPartName = "ملجراماً";
                			partPrecision = 2;
                			isCurrencyPartNameFeminine = false;
					break;
                  default  : currencyID = currency;
                            currencyCode = currency.toString();
                            isCurrencyNameFeminine = false;
                            englishCurrencyName = "Jordanian Dinar";
                            englishPluralCurrencyName = "Jordanian Dinars";
                            englishCurrencyPartName = "Fils";
                            englishPluralCurrencyPartName = "Fils";
                            arabic1CurrencyName = "دينار أردني";
                            arabic2CurrencyName = "ديناران أردنيان";
                            arabic310CurrencyName = "دنانير أردنية";
                            arabic1199CurrencyName = "دينارا أردنيا";
                            arabic1CurrencyPartName = "فلس";
                            arabic2CurrencyPartName = "فلسان";
                            arabic310CurrencyPartName = "فلس";
                            arabic1199CurrencyPartName = "فلسا";
                            partPrecision = 3;
                            isCurrencyPartNameFeminine = false;
			}
		}
	}
	//----------------
    public   String convertNumberToArabicWords(String number) throws NumberFormatException {

        // check if the input string is number or not
        Double.parseDouble(number);

        // check if its floating point number or not
        if (number.contains(".")) { // yes
            // the number
            amountFormate = new DecimalFormat("#,##,##,##,###.###");
//		amountFormate.setMinimumFractionDigits(2);
//		amountFormate.setMaximumFractionDigits(2);
            amountFormate.setMinimumFractionDigits(AppConstants.NUMBER_OF_DECIMALS_TO_ROUND);
            amountFormate.setMaximumFractionDigits(AppConstants.NUMBER_OF_DECIMALS_TO_ROUND);
            number=amountFormate.format(StringUtils.getFloat(number));
            String theNumber = number.substring(0, number.indexOf('.'));
            // the floating point number
            String theFloat = number.substring(number.indexOf('.') + 1);
            // check how many digits in the number 1:x 2:xx 3:xxx 4:xxxx 5:xxxxx
            // 6:xxxxxx
            theFloat=amountFormate.format(StringUtils.getFloat(theFloat));
            switch (theNumber.length()) {
                case 1:
                    return convertOneDigits(theNumber) +   ( context).getResources().getString(R.string.c_ar)+ convertTwoDigits(theFloat);
                case 2:
                    return convertTwoDigits(theNumber) +  ( context).getResources().getString(R.string.c_ar)  + convertTwoDigits(theFloat);
                case 3:
                    return convertThreeDigits(theNumber) +  ( context).getResources().getString(R.string.c_ar) + convertTwoDigits(theFloat);
                case 4:
                    return convertFourDigits(theNumber) +  (  context).getResources().getString(R.string.c_ar) + convertTwoDigits(theFloat);
                case 5:
                    return convertFiveDigits(theNumber) +(  context).getResources().getString(R.string.c_ar) + convertTwoDigits(theFloat);
                case 6:
                    return convertSixDigits(theNumber) + (  context).getResources().getString(R.string.c_ar) + convertTwoDigits(theFloat);
                default:
                    return "";
            }
        }

        else {
            switch (number.length()) {
                case 1:
                    return convertOneDigits(number);
                case 2:
                    return convertTwoDigits(number);
                case 3:
                    return convertThreeDigits(number);
                case 4:
                    return convertFourDigits(number);
                case 5:
                    return convertFiveDigits(number);
                case 6:
                    return convertSixDigits(number);
                default:
                    return "";
            }

        }
    }

    // -------------------------------------------

    private   String convertOneDigits(String oneDigit) {
        switch (Integer.parseInt(oneDigit)) {
            case 1:
                return ((BaseActivity)context).getResources().getString(R.string.one_ar);
            case 2:
                return ((BaseActivity)context).getResources().getString(R.string.two_ar);
            case 3:
                return ((BaseActivity)context).getResources().getString(R.string.three_ar);
            case 4:
                return ((BaseActivity)context).getResources().getString(R.string.four_ar);
            case 5:
                return ((BaseActivity)context).getResources().getString(R.string.five_ar);
            case 6:
                return ((BaseActivity)context).getResources().getString(R.string.six_ar);
            case 7:
                return ((BaseActivity)context).getResources().getString(R.string.seven_ar);
            case 8:
                return ((BaseActivity)context).getResources().getString(R.string.eight_ar);
            case 9:
                return ( context).getResources().getString(R.string.nine_ar);
            default:
                return "";
        }
    }

    private   String convertTwoDigits(String twoDigits) {
        String returnAlpha = "00";
        // check if the first digit is 0 like 0x
        if (twoDigits.charAt(0) == '0' && twoDigits.charAt(1) != '0') { // yes
            // convert two digits to one
            return convertOneDigits(String.valueOf(twoDigits.charAt(1)));
        } else { // no
            // check the first digit 1x 2x 3x 4x 5x 6x 7x 8x 9x
            switch (getIntVal(twoDigits.charAt(0))) {
                case 1: { // 1x
                    if (getIntVal(twoDigits.charAt(1)) == 1) {
                        return ( context).getResources().getString(R.string.ca_ar);
                    }
                    if (getIntVal(twoDigits.charAt(1)) == 2) {
                        return   ( context).getResources().getString(R.string.caa_ar);
                    } else {
                        return convertOneDigits(String.valueOf(twoDigits.charAt(1))) + " " + ( context).getResources().getString(R.string.ctb_ar);
                    }
                }
                case 2: // 2x x:not 0
                    returnAlpha =( context).getResources().getString(R.string.ctb_two_ar);
                    break;
                case 3: // 3x x:not 0
                    returnAlpha =( context).getResources().getString(R.string.ctb_three_ar);
                    break;
                case 4: // 4x x:not 0
                    returnAlpha =( context).getResources().getString(R.string.ctb_four_ar);
                    break;
                case 5: // 5x x:not 0
                    returnAlpha =( context).getResources().getString(R.string.ctb_five_ar);
                    break;
                case 6: // 6x x:not 0
                    returnAlpha =( context).getResources().getString(R.string.ctb_six_ar);
                    break;
                case 7: // 7x x:not 0
                    returnAlpha =( context).getResources().getString(R.string.ctb_seven_ar);
                    break;
                case 8: // 8x x:not 0
                    returnAlpha =( context).getResources().getString(R.string.ctb_eight_ar);
                    break;
                case 9: // 9x x:not 0
                    returnAlpha =( context).getResources().getString(R.string.ctb_nine_ar);
                    break;
                default:
                    returnAlpha = "";
                    break;
            }
        }

        // 20 - 99
        // x0 x:not 0,1
        if (convertOneDigits(String.valueOf(twoDigits.charAt(1))).length() == 0) {
            return returnAlpha;
        } else { // xx x:not 0
            return convertOneDigits(String.valueOf(twoDigits.charAt(1))) + ( context).getResources().getString(R.string.cha_ar) + returnAlpha;
        }
    }

    private   String convertThreeDigits(String threeDigits) {

        // check the first digit x00
        switch (getIntVal(threeDigits.charAt(0))) {

            case 1: { // 100 - 199
                if (getIntVal(threeDigits.charAt(1)) == 0) { // 10x
                    if (getIntVal(threeDigits.charAt(2)) == 0) { // 100 ch_ar
                        return ( context).getResources().getString(R.string.ch_ar);
                    } else { // 10x x: is not 0
                        return ( context).getResources().getString(R.string.ch_ar)+ ( context).getResources().getString(R.string.cha_ar)+ convertOneDigits(String.valueOf(threeDigits.charAt(2)));
                    }
                } else {// 1xx x: is not 0
                    return  ( context).getResources().getString(R.string.ch_ar)+ ( context).getResources().getString(R.string.cha_ar) + convertTwoDigits(threeDigits.substring(1, 3));
                }
            }
            case 2: { // 200 - 299
                if (getIntVal(threeDigits.charAt(1)) == 0) { // 20x
                    if (getIntVal(threeDigits.charAt(2)) == 0) { // 200
                        return  ( context).getResources().getString(R.string.chb_ar);
                    } else { // 20x x:not 0
                        return  ( context).getResources().getString(R.string.chb_ar) + ( context).getResources().getString(R.string.cha_ar) + convertOneDigits(String.valueOf(threeDigits.charAt(2)));
                    }
                } else { // 2xx x:not 0
                    return ( context).getResources().getString(R.string.chb_ar) + ( context).getResources().getString(R.string.cha_ar)+ convertTwoDigits(threeDigits.substring(1, 3));
                }
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9: { // 300 - 999
                if (getIntVal(threeDigits.charAt(1)) == 0) { // x0x x:not 0
                    if (getIntVal(threeDigits.charAt(2)) == 0) { // x00 x:not 0 chc_ar
                        return convertOneDigits(String.valueOf(threeDigits.charAt(1) +  ( context).getResources().getString(R.string.chc_ar)));
                    } else { // x0x x:not 0
                        return convertOneDigits(String.valueOf(threeDigits.charAt(0))) +  ( context).getResources().getString(R.string.chc_ar) + ( context).getResources().getString(R.string.cha_ar)
                                + convertOneDigits(String.valueOf(threeDigits.charAt(2)));
                    }
                } else { // xxx x:not 0
                    return convertOneDigits(String.valueOf(threeDigits.charAt(0))) +  ( context).getResources().getString(R.string.chc_ar) + ( context).getResources().getString(R.string.cha_ar)
                            + convertTwoDigits(threeDigits.substring(1, 3));
                }
            }

            case 0: { // 000 - 099
                if (threeDigits.charAt(1) == '0') { // 00x
                    if (threeDigits.charAt(2) == '0') { // 000
                        return "";
                    } else { // 00x x:not 0
                        return convertOneDigits(String.valueOf(threeDigits.charAt(2)));
                    }
                } else { // 0xx x:not 0
                    return convertTwoDigits(threeDigits.substring(1, 3));
                }
            }
            default:
                return "";
        }
    }

    private   String convertFourDigits(String fourDigits) {
        // xxxx
        switch (getIntVal(fourDigits.charAt(0))) {

            case 1: { // 1000 - 1999
                if (getIntVal(fourDigits.charAt(1)) == 0) { // 10xx x:not 0
                    if (getIntVal(fourDigits.charAt(2)) == 0) { // 100x x:not 0
                        if (getIntVal(fourDigits.charAt(3)) == 0) { // 1000
                            return  ( context).getResources().getString(R.string.cf_ar);
                        } else { // 100x x:not 0
                            return ( context).getResources().getString(R.string.cf_ar) +( context).getResources().getString(R.string.cha_ar) + convertOneDigits(String.valueOf(fourDigits.charAt(3)));
                        }
                    } else { // 10xx x:not 0
                        return ( context).getResources().getString(R.string.cf_ar) + ( context).getResources().getString(R.string.cha_ar) + convertTwoDigits(fourDigits.substring(2, 4));
//                        return ( context).getResources().getString(R.string.cf_ar) + ( context).getResources().getString(R.string.cha_ar) + convertTwoDigits(fourDigits.substring(2, 3));
                    }
                } else { // 1xxx x:not 0
                    return ( context).getResources().getString(R.string.cf_ar)+ ( context).getResources().getString(R.string.cha_ar)+ convertThreeDigits(fourDigits.substring(1, 4));
                }
            }
            case 2: { // 2000 - 2999
                if (getIntVal(fourDigits.charAt(1)) == 0) { // 20xx
                    if (getIntVal(fourDigits.charAt(2)) == 0) { // 200x
                        if (getIntVal(fourDigits.charAt(3)) == 0) { // 2000
                            return  (context).getResources().getString(R.string.cfa_ar);
                        } else { // 200x x:not 0
                            return (context).getResources().getString(R.string.cfa_ar) + ( context).getResources().getString(R.string.cha_ar) + convertOneDigits(String.valueOf(fourDigits.charAt(3)));
                        }
                    } else { // 20xx x:not 0
                        return (context).getResources().getString(R.string.cfa_ar) + ( context).getResources().getString(R.string.cha_ar) + convertTwoDigits(fourDigits.substring(2, 4));
//                        return (context).getResources().getString(R.string.cfa_ar) + ( context).getResources().getString(R.string.cha_ar) + convertTwoDigits(fourDigits.substring(2, 3));
                    }
                } else { // 2xxx x:not 0
                    return (context).getResources().getString(R.string.cfa_ar)+ ( context).getResources().getString(R.string.cha_ar) + convertThreeDigits(fourDigits.substring(1, 4));
                }
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9: { // 3000 - 9999
                if (getIntVal(fourDigits.charAt(1)) == 0) { // x0xx x:not 0
                    if (getIntVal(fourDigits.charAt(2)) == 0) { // x00x x:not 0
                        if (getIntVal(fourDigits.charAt(3)) == 0) { // x000 x:not 0 cfb_ar
                            return convertOneDigits(String.valueOf(fourDigits.charAt(0))) +  ( context).getResources().getString(R.string.cfb_ar);
                        } else { // x00x x:not 0
                            return convertOneDigits(String.valueOf(fourDigits.charAt(0))) +( context).getResources().getString(R.string.cfb_ar) + ( context).getResources().getString(R.string.cha_ar)
                                    + convertOneDigits(String.valueOf(fourDigits.charAt(3)));
                        }
                    } else { // x0xx x:not 0
                        return convertOneDigits(String.valueOf(fourDigits.charAt(0))) + ( context).getResources().getString(R.string.cfb_ar) + ( context).getResources().getString(R.string.cha_ar)
                                + convertTwoDigits(fourDigits.substring(2, 4));
//                                + convertTwoDigits(fourDigits.substring(2, 3));
                    }
                } else { // xxxx x:not 0
                    return convertOneDigits(String.valueOf(fourDigits.charAt(0))) + ( context).getResources().getString(R.string.cfb_ar) + ( context).getResources().getString(R.string.cha_ar)
                            + convertThreeDigits(fourDigits.substring(1, 4));
                }
            }

            default:
                return "";
        }
    }

    private   String convertFiveDigits(String fiveDigits) {
        if (convertThreeDigits(fiveDigits.substring(2, 5)).length() == 0) { // xx000
            // x:not
            // 0 cv_ar
            return convertTwoDigits(fiveDigits.substring(0, 2)) +  ( context).getResources().getString(R.string.cv_ar);
        } else { // xxxxx x:not 0
            return convertTwoDigits(fiveDigits.substring(0, 2)) +  ( context).getResources().getString(R.string.cva_ar)+ ( context).getResources().getString(R.string.cha_ar)
                    + convertThreeDigits(fiveDigits.substring(2, 5));
        }
    }

    private   String convertSixDigits(String sixDigits) {

        if (convertThreeDigits(sixDigits.substring(2, 5)).length() == 0) { // xxx000
            // x:not
            // 0
            return convertThreeDigits(sixDigits.substring(0, 3)) +  ( context).getResources().getString(R.string.cs_ar);
        } else { // xxxxxx x:not 0
            return convertThreeDigits(sixDigits.substring(0, 3)) + ( context).getResources().getString(R.string.csa_ar) + ( context).getResources().getString(R.string.cha_ar)
                    + convertThreeDigits(sixDigits.substring(3, 6));
        }
    }

    private static int getIntVal(char c) {
        return Integer.parseInt(String.valueOf(c));
    }

    // ----------------------------------------------------------
}
