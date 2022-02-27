package de.quadrathelden.ostereier.api;

public final class OstereierProvider {

	private static OstereierAPI ostereierAPI;

	private OstereierProvider() {
	}

	public static boolean hasAPI() {
		return ((ostereierAPI != null) && !ostereierAPI.isDisabled());
	}

	public static OstereierAPI getAPI() {
		if (hasAPI()) {
			return ostereierAPI;
		} else {
			return null;
		}
	}

	static boolean setAPI(OstereierAPI newAPI) {
		if (!hasAPI()) {
			ostereierAPI = newAPI;
			return true;
		} else {
			return false;
		}
	}

}
