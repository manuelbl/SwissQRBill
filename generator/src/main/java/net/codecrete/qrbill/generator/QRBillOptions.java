package net.codecrete.qrbill.generator;

/**
 * Advanced options for generating the {@link Bill}
 * <p>
 * Use {@link QRBillOptions#builder()} to build a new instance
 */
public class QRBillOptions {


	private final String addressNameDisplayLineBreakOn;

	private QRBillOptions(String addressNameDisplayLineBreakOn) {
		this.addressNameDisplayLineBreakOn = addressNameDisplayLineBreakOn;
	}


	/**
	 * @return new builder instance
	 */
	public static QRBillOptionsBuilder builder() {
		return new QRBillOptionsBuilder();
	}

	/**
	 * Line break char sequence to display {@link Address#getName()} on multiple lines
	 *
	 * @return display split char sequence
	 */
	public String getAddressNameDisplayLineBreakOn() {
		return addressNameDisplayLineBreakOn;
	}

	/**
	 * @return whether the AddressNameDisplayLineBreakOn is enabled or not
	 */
	public boolean hasAddressNameDisplayLineBreakOn() {
		return addressNameDisplayLineBreakOn != null;
	}


	/**
	 * Builder for {@link QRBillOptions}
	 */
	public static class QRBillOptionsBuilder {
		private String addressNameDisplayLineBreakOn = null;

		QRBillOptionsBuilder() {
		}

		/**
		 * Line break char sequence to display {@link Address#getName()} on multiple lines
		 *
		 * @param lineBreakSequence sequence used to split the {@link Address#getName()} to a new line
		 * @return builder instance
		 */
		public QRBillOptionsBuilder addressNameDisplayLineBreakOn(String lineBreakSequence) {
			this.addressNameDisplayLineBreakOn = lineBreakSequence;
			return this;
		}

		/**
		 * @return configured {@link QRBillOptions}
		 */
		public QRBillOptions build() {
			return new QRBillOptions(addressNameDisplayLineBreakOn);
		}
	}
}