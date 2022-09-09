package net.codecrete.qrbill.generator;

/**
 * Advanced options for generating the {@link Bill}
 * <p>
 * Use {@link QRBillOptions#builder()} to build a new instance
 */
public class QRBillOptions {

	/**
	 * Default local country code for determining if an {@link Address} is local or foreign.
	 */
	public static final String DEFAULT_LOCAL_COUNTRY_CODE = "CH";

	private final String addressNameDisplayLineBreakOn;
	private final String localCountryCode;

	private QRBillOptions(String addressNameDisplayLineBreakOn, String localCountryCode) {
		this.addressNameDisplayLineBreakOn = addressNameDisplayLineBreakOn;
		this.localCountryCode = localCountryCode;
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
	 * Local country code used to determine foreign addresses
	 *
	 * @return local country code
	 */
	public String getLocalCountryCode() {
		return localCountryCode;
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
		private String localCountryCode = DEFAULT_LOCAL_COUNTRY_CODE;

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
		 * Specify the local country code used to determine if the addresses are foreign or local
		 * <p>
		 * Default is {@link QRBillOptions#DEFAULT_LOCAL_COUNTRY_CODE}
		 *
		 * @param localCountryCode the local <a href="https://en.wikipedia.org/wiki/ISO_3166-2">ISO 3166-2</a> country code
		 * @return builder instance
		 */
		public QRBillOptionsBuilder localCountryCode(String localCountryCode) {
			this.localCountryCode = localCountryCode;
			return this;
		}

		/**
		 * @return configured {@link QRBillOptions}
		 */
		public QRBillOptions build() {
			return new QRBillOptions(addressNameDisplayLineBreakOn, localCountryCode);
		}
	}
}