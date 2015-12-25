package com.diya;

import java.util.ArrayList;
import java.util.EnumSet;

import com.badlogic.gdx.math.Vector2;

public interface ConstructionMenuInterface {
	public EnumSet<ConstructionMenuOption> getMenuOptions();
	public String getSelectedOptions();
	public void setSelectedOption(ConstructionMenuOption option, CharSequence text);
	public void setActive();
	public void setInactive();
	public float preferredMenuPositionX();
	public float preferredMenuPositionY();
}
