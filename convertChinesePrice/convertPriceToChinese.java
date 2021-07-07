package demo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import tw.com.iisi.gaia.base.utils.GaiaMath;
import tw.com.iisi.gaia.base.utils.GaiaString;

public class convertPriceToChinese {

	static final String[] unit1l = { "", "拾", "佰", "仟" };

	static final String[] unit2l = { "", "萬", "億", "兆", "京" };

	static final char[] DEC = { '角', '分', '厘' };

	static final char DOLLAR = '元';

	static final String[] CHINESEMATH = new String[] { "零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖", "拾" };

	private static String changeHundardPrice(BigDecimal hundardprice) {
		BigDecimal tempPrice = hundardprice;
		StringBuilder srcPrice = new StringBuilder();
		String preStirng = "";
		for (int idx = 0, max = unit1l.length; idx < max; idx++) {
			BigDecimal charInt = tempPrice.remainder(new BigDecimal("10"));
			if (preStirng.equals("0") && srcPrice.length() != 0)
				srcPrice.insert(0, CHINESEMATH[0]);
			if (charInt.compareTo(BigDecimal.ZERO) == 1)
				srcPrice.insert(0, unit1l[idx]).insert(0, CHINESEMATH[charInt.intValue()]);
			preStirng = String.valueOf(charInt);
			tempPrice = tempPrice.divide(new BigDecimal("10"),0,BigDecimal.ROUND_DOWN);
			if (tempPrice.compareTo(BigDecimal.ZERO) == 0)
				break;
		}
		System.out.println(srcPrice.toString());
		return srcPrice.toString();
	}

	public static String convertPriceToChinese(String price) {
		if ("0".equals(price))
			return CHINESEMATH[0] + DOLLAR;
		String tempPrice = price.replaceAll(",", "");
		String subdec = "";
		if (tempPrice.matches("^[0-9]+\\.[0-9]+"))
			subdec = tempPrice.split("\\.")[1];
		BigDecimal priceb = new BigDecimal(tempPrice);

		StringBuilder srcPrice = new StringBuilder();

		unit21: for (int unitx = 0; unitx < unit2l.length; unitx++) {//
			if (priceb.compareTo(BigDecimal.ZERO) == 1)
				srcPrice.insert(0, unit2l[unitx]);

			String hundardPrice = changeHundardPrice(priceb.remainder(new BigDecimal("1000")));
			srcPrice.insert(0, hundardPrice);
			priceb = priceb.divide(new BigDecimal("1000"),0,BigDecimal.ROUND_DOWN);

			BigDecimal thousandInt = priceb.remainder(new BigDecimal("10"));
			priceb = priceb.divide(new BigDecimal("10"),0,BigDecimal.ROUND_DOWN);

			if (priceb.compareTo(BigDecimal.ZERO) == 0)
				break unit21;
			else {
				if (thousandInt.compareTo(BigDecimal.ZERO) == 0)
					srcPrice.insert(0, CHINESEMATH[thousandInt.intValue()]);
				else
					srcPrice.insert(0, unit1l[3]).insert(0, CHINESEMATH[thousandInt.intValue()]);
			}
		}
		//// 小數 處理
		StringBuilder subprice = new StringBuilder();

		if (subdec.isEmpty() == false) {
			for (int i = 0; i < subdec.length(); i++) {
				if ('0' == subdec.charAt(i))
					continue;
				int charToNum = Character.getNumericValue(subdec.charAt(i));
				subprice.append(CHINESEMATH[charToNum]).append(DEC[i]);
			}
		}
		return srcPrice.append(DOLLAR).append(subprice).toString();

	}

	public static void main(String[] args) {
		String chinesePrice = convertPriceToChinese("123456789123456789");
		System.out.println(chinesePrice);
	}

}
