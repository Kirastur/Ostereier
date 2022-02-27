package de.quadrathelden.ostereier.commands;

public enum ParamStartStopAuto {

	START("start"),
	STOP("stop"),
	AUTO("auto");

	private final String param;

	private ParamStartStopAuto(String param) {
		this.param = param;
	}

	public boolean hasText(String paramText) {
		return (paramText.equalsIgnoreCase(param));
	}

	public String getParamName() {
		return param;
	}

}
