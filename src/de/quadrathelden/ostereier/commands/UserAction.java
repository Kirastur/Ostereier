package de.quadrathelden.ostereier.commands;

import static de.quadrathelden.ostereier.commands.ParamType.CONFIG;
import static de.quadrathelden.ostereier.commands.ParamType.NONE;
import static de.quadrathelden.ostereier.commands.ParamType.STARTSTOP;
import static de.quadrathelden.ostereier.commands.ParamType.STARTSTOPAUTO;
import static de.quadrathelden.ostereier.commands.ParamType.TEMPLATES;

public enum UserAction implements Action {

	EDITOR("editor", STARTSTOP),
	TEMPLATE("template", TEMPLATES),
	GAME("game", STARTSTOPAUTO),
	SHOP("shop", NONE),
	LIST("list", CONFIG),
	INFO("info", NONE),
	RELOAD("reload", NONE),
	HELP("help", NONE);

	private final String command;
	private final ParamType param1;

	private UserAction(String command, ParamType param1) {
		this.command = command;
		this.param1 = param1;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public int getParamCount() {
		if (param1 != NONE) {
			return 1;
		}
		return 0;
	}

	@Override
	public ParamType getParam(int position) {
		if (position == 1) {
			return param1;
		}
		return NONE;
	}

	@Override
	public int findPosition(ParamType param) {
		if (param == param1) {
			return 1;
		}
		return 0;
	}

}
