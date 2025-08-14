package com.cjg.home.util;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {
	
	public static List<Integer> getStartEndPage(int pageNumber, int  totalPage) {
		
		List<Integer> result = new ArrayList<>();
		int start=-1;
		int end=-1;
		
		int a = pageNumber / 10;
		int b = pageNumber % 10;
		
		if(a==0) {
			start = 1;
			end = 10;
		}else {
			if(b==0) {
				end = a * 10;
				start = end - 9; 
			}else {
				end = (a+1) * 10;
				start = end -9;
			}
		}
		
		if(end > totalPage) {
			end = totalPage;
		}
		
		for(int i=start; i<=end; i++) {
			result.add(i);
		}
		
		return result;
	}
}
