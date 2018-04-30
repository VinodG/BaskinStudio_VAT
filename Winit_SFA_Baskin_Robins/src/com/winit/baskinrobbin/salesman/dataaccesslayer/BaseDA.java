package com.winit.baskinrobbin.salesman.dataaccesslayer;

import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rajeshc on 12/7/2017.
 */

public class BaseDA
{
    public void getTotalUnits(ProductDO objProduct)
    {
        objProduct.totalCases = StringUtils.getFloat(objProduct.preCases) + StringUtils.getFloat(objProduct.preUnits)/objProduct.UnitsPerCases;
        objProduct.totalUnits = Math.round(StringUtils.getFloat(objProduct.preUnits) + Math.abs(StringUtils.getFloat(objProduct.preCases)*objProduct.UnitsPerCases));
    }

    public static <T> Collection<T> filter(Collection<T> col,
                                           Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element : col) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public interface Predicate<T> {
        boolean apply(T type);
    }

    public  String getColumnsValueByCount(int count)
    {
        String columnValue="";

        for(int i=0;i<count;i++)
        {
            if(i==0)
                columnValue="?";
            else
                columnValue=columnValue+",?";
        }
        return columnValue;

    }


}
