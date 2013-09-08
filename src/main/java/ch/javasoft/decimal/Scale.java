package ch.javasoft.decimal;



public class Scale {
	
	public static final class Scale1f extends Scale {
		public static final Scale1f INSTANCE = new Scale1f();
		private Scale1f() {super(1);}
	}
	public static final class Scale2f extends Scale {
		public static final Scale2f INSTANCE = new Scale2f();
		private Scale2f() {super(2);}
	}
	public static final class Scale3f extends Scale {
		public static final Scale3f INSTANCE = new Scale3f();
		private Scale3f() {super(3);}
	}
	public static final class Scale4f extends Scale {
		public static final Scale4f INSTANCE = new Scale4f();
		private Scale4f() {super(4);}
	}
	public static final class Scale5f extends Scale {
		public static final Scale5f INSTANCE = new Scale5f();
		private Scale5f() {super(5);}
	}
	public static final class Scale6f extends Scale {
		public static final Scale6f INSTANCE = new Scale6f();
		private Scale6f() {super(6);}
	}
	public static final class Scale7f extends Scale {
		public static final Scale7f INSTANCE = new Scale7f();
		private Scale7f() {super(7);}
	}
	public static final class Scale8f extends Scale {
		public static final Scale8f INSTANCE = new Scale8f();
		private Scale8f() {super(8);}
	}
	public static final class Scale9f extends Scale {
		public static final Scale9f INSTANCE = new Scale9f();
		private Scale9f() {super(9);}
	}
	public static final class Scale10f extends Scale {
		public static final Scale10f INSTANCE = new Scale10f();
		private Scale10f() {super(10);}
	}
	public static final class Scale11f extends Scale {
		public static final Scale11f INSTANCE = new Scale11f();
		private Scale11f() {super(11);}
	}
	public static final class Scale12f extends Scale {
		public static final Scale12f INSTANCE = new Scale12f();
		private Scale12f() {super(12);}
	}
	public static final class Scale13f extends Scale {
		public static final Scale13f INSTANCE = new Scale13f();
		private Scale13f() {super(13);}
	}
	public static final class Scale14f extends Scale {
		public static final Scale14f INSTANCE = new Scale14f();
		private Scale14f() {super(14);}
	}
	public static final class Scale15f extends Scale {
		public static final Scale15f INSTANCE = new Scale15f();
		private Scale15f() {super(15);}
	}
	public static final class Scale16f extends Scale {
		public static final Scale16f INSTANCE = new Scale16f();
		private Scale16f() {super(16);}
	}
	public static final class Scale17f extends Scale {
		public static final Scale17f INSTANCE = new Scale17f();
		private Scale17f() {super(17);}
	}
	public static final class Scale18f extends Scale {
		public static final Scale18f INSTANCE = new Scale18f();
		private Scale18f() {super(18);}
	}

	private final int fractionDigits;
	private Scale(int fractionDigits) {
		this.fractionDigits = fractionDigits;
	}
	public int getFractionDigits() {
		return fractionDigits;
	}
	public long getScaleFactor() {
		int factor = 1;
		for (int i = 0; i < fractionDigits; i++) {
			factor *= 10;
		}
		return factor;
	}
	//Long.MAX_VALUE: 9,223,372,036,854,775,807
}
