package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;

import com.winit.baskinrobbin.salesman.utilities.CollectionUtils.SortedType;

public  class BaseComparableDO implements Comparable<BaseComparableDO>, Serializable
{
  public static SortedType comparableStringType = SortedType.DEFAULT;	
  public Object comparableValue = null;	
 
  public int compareTo(BaseComparableDO another) {return 0;}

  public Object getComparableValue() {return null;}
  
  public Object getValue(SortedType sortedType) {return null;}
}
