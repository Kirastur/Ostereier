package de.quadrathelden.ostereier.config.design;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigHead {

	public static final String DATA_PATTERN = "^..textures....SKIN....url...http://textures.minecraft.net/texture/.*";

	protected final String name;
	protected String data;

	protected ConfigHead(String name) {
		this.name = name;
	}

	public ConfigHead(String name, String data) throws OstereierException {
		this.name = name;
		this.data = data;
		validate();
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	protected void validate() throws OstereierException {
		if ((data == null) || data.isEmpty()) {
			throw new OstereierException(name, Message.CONFIG_HEAD_DATA_WRONG, null);
		}
		String decodedString;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(data);
			decodedString = new String(decodedBytes);
		} catch (Exception e) {
			throw new OstereierException(name, Message.CONFIG_HEAD_DATA_WRONG, e.getMessage(), e);
		}
		if (decodedString.isEmpty()) {
			throw new OstereierException(name, Message.CONFIG_HEAD_DATA_WRONG, null);
		}
		Pattern r = Pattern.compile(DATA_PATTERN);
		Matcher m = r.matcher(decodedString);
		if (!m.matches()) {
			throw new OstereierException(name, Message.CONFIG_HEAD_DATA_WRONG, null);
		}
	}

}
