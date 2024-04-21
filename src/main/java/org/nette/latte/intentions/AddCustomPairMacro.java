package org.nette.latte.intentions;

import org.nette.latte.settings.LatteTagSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Registers custom macro which can be used only as both classic pair macro and attribute macro.
 */
public class AddCustomPairMacro extends AddCustomMacro {

	public AddCustomPairMacro(String macroName) {
		super(macroName);
	}

	@NotNull
	@Override
	public LatteTagSettings.Type getMacroType() {
		return LatteTagSettings.Type.PAIR;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom pair tag {" + macro.getMacroName() + "}...{/" + macro.getMacroName() + "} and n:" + macro.getMacroName();
	}
}
