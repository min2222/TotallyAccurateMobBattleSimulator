package com.min01.tambs.gui.components;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

public class PairCheckbox extends Checkbox
{
	protected Checkbox other;
	
	public PairCheckbox(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected) 
	{
		super(pX, pY, pWidth, pHeight, pMessage, pSelected);
	}
	
	public void setOther(Checkbox other)
	{
		this.other = other;
	}
	
	@Override
	public void onPress() 
	{
		if(this.other != null && this.other.selected())
		{
			this.other.onPress();
		}
		super.onPress();
	}
}
