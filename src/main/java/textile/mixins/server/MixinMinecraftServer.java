package textile.mixins.server;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import textile.data.TextileUpdates;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	
	@Inject(method = "tick",
		at = @At(value = "HEAD")
	)
	private void preTick(BooleanSupplier keepTicking, CallbackInfo ci) {
		TextileUpdates.preTick();
	}
	
	
	@Inject(method = "tick",
		at = @At(value = "RETURN")
	)
	private void postTick(BooleanSupplier keepTicking, CallbackInfo ci) {
		TextileUpdates.postTick();
	}
}
