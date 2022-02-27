package de.quadrathelden.ostereier.commands;

public enum ParamStartStop {

	START("start"),
	STOP("stop");

	private final String param;

	private ParamStartStop(String param) {
		this.param = param;
	}

	public boolean hasText(String paramText) {
		return (paramText.equalsIgnoreCase(param));
	}

	public String getParam() {
		return param;
	}

}
