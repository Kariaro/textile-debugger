package textile.commands;

import carpet.CarpetSettings;
import carpet.settings.SettingsManager;
import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BlockEventCommand {
	public static boolean HAS_FROZEN_EVENTS = false;
	public static int DO_TICKS = 0;
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder = literal("be").
				requires((player) -> SettingsManager.canUseCommand(player, CarpetSettings.commandTick)).
				then(literal("freeze").executes((c)-> toggle_freeze(c.getSource(), false))).
				then(literal("list").executes((c)-> list_events(c.getSource()))).
				then(literal("step").
						executes((c) -> step_event(1)).
						then(argument("ticks", integer(1,72000)).
								suggests((c, b) -> suggestMatching(new String[] { "20" }, b)).
								executes((c) -> step_event(getInteger(c, "ticks")))));
		
		
		dispatcher.register(literalargumentbuilder);
	}
	
	public static int toggle_freeze(ServerCommandSource source, boolean isDeep) {
		HAS_FROZEN_EVENTS = !HAS_FROZEN_EVENTS;
		if(HAS_FROZEN_EVENTS) {
			Messenger.m(source, "gi Blocks events is frozen");
		} else {
			Messenger.m(source, "gi Blocks events have begun to move again");
		}
		
		
		
		return 1;
	}
	
	public static int list_events(ServerCommandSource source) {
		
		return 1;
	}
	
	private static int step_event(int count) {
		DO_TICKS = count;
		return count;
	}
}
