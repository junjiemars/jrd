package org.mars;
import static java.lang.System.out;


public class Main {
	public static final void main(String[] args) {
		double f0 = 1.0f;
		if (1.0f == f0)
			out.println("1.0f equal");
		else 
			out.println("not equal");

		if (1.000000001f == f0) 
			out.println("1.0000000f equal");
		else
			out.println("not equal");

		out.println(1.00000001f - f0);
		if (1.11f - f0 > 0.1f) 
			out.println("1.11f-f0>0.1f");
		/*
		
		List<String> l = Arrays.asList(ss);
		for (String s : l) 
			out.println(s);
		*/
		String[] ss = new String[]{"A","B","C"};
		out.println(ss.length);
	}

}
