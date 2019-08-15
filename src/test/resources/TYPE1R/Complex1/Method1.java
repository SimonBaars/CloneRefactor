public class Method1 {
public boolean toggleMode(Player player, Stick stick, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.TOGGLE_MODE.getPermission()))
			return false;

		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		if (!stick.isEnabled()) {
			stick.toggle();
			sendToggleMessage(player, stick);
			return true;
		} else if (stick.getMode() == Stick.REMOVE_MODE) {
			stick.setMode(Stick.REPLACE_MODE);
		} else if (stick.getMode() == Stick.REPLACE_MODE){
			stick.setMode(Stick.BUILD_MODE);
		} else {
			stick.setMode(Stick.REMOVE_MODE);
		}

		sendToggleMessage(player, stick);
		return true;
	}
}