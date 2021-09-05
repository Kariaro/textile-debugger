package textile;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.settings.SettingsManager;
import com.mojang.brigadier.CommandDispatcher;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.command.ServerCommandSource;
import textile.commands.BlockEventCommand;
import textile.renderer.RenderHandler;


public class TextileDebuggerInit implements ModInitializer, IInitializationHandler, CarpetExtension {
	private static final SettingsManager settings;
	
	static {
		settings = new SettingsManager("1.0", "textile", "TextileDebugger");
	}
	
	@Override
	public void onInitialize() {
		InitializationHandler.getInstance().registerInitializationHandler(this);
		CarpetServer.manageExtension(this);
	}
	
	@Override
	public void onGameStarted() {
		CarpetServer.settingsManager.parseSettingsClass(TextileSettings.class);
	}
	
	@Override
	public void registerModHandlers() {
		IRenderer renderer = new RenderHandler();
		RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
		RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);
		
	}
	
	@Override
	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		BlockEventCommand.register(dispatcher);
	}
}
