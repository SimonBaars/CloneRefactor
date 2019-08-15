public class Method2 {
public boolean toggleBuild(Player player, Stick stick, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.BUILD.getPermission()))
			return false;

		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		if (stick.getItem() == null) {
			MessageUtils.send(player, "Invalid item usage.");
			return false;
		} else {
			stick.setMode(2);
			sendToggleMessage(player, stick);
		}

		return true;
	}
}