package xyz.nickelulz.glasshousetweaks.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.datatypes.CommandBase;

public class TestCommand extends CommandBase {
    public TestCommand() {
        super("test", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GRAY + "This is a test message. Also, AHGAIEGJREAIGRJAEIGJAREOR");
        return true;
    }

    @Override
    public String getUsage() {
        return "/test";
    }
}
