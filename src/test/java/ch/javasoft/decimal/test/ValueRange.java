package ch.javasoft.decimal.test;

public enum ValueRange {
	ALL,
	NON_NEGATIVE {
		@Override
		public boolean include(long value) {
			return value >= 0;
		}
	};
	
	public boolean include(long value) {
		return true;
	}
}
