package de.quadrathelden.ostereier.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public record TextKey(String name, String locale) {

	public static TextKey from(String key) {
		List<String> keyParts = Arrays.asList(key.split("_"));
		List<String> keyNames = new ArrayList<>();
		List<String> keyLocales = new ArrayList<>();
		boolean isCapitalFirst = false;
		boolean isLast = false;
		for (String myPart : keyParts) {
			if (isLast) {
				keyLocales.add(myPart);
			} else {
				String myUpperPart = myPart.toUpperCase(Locale.ROOT);
				if (myPart.equals(myUpperPart)) {
					isCapitalFirst = true;
					keyNames.add(myPart);
				} else {
					isLast = true;
					if (isCapitalFirst) {
						keyLocales.add(myPart);
					} else {
						keyNames.add(myPart);
					}
				}
			}
		}
		return new TextKey(String.join("_", keyNames), String.join("_", keyLocales));
	}

}
