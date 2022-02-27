package de.quadrathelden.ostereier.commands;

public interface Action {

	public String getCommand();

	public int getParamCount();

	public ParamType getParam(int position);

	public int findPosition(ParamType param);

}
