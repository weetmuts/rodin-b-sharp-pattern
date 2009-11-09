package ch.ethz.eventb.pattern;

import ch.ethz.eventb.internal.pattern.Data;

public class DataFactory {

	public static IData createData() {
		return new Data();
	}
}
