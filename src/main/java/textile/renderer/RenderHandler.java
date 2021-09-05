package textile.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import textile.data.*;
import textile.utils.RaycastUtil;

import javax.sound.sampled.Line;
import java.util.*;
import java.util.function.Supplier;

// textile debugger
public class RenderHandler implements IRenderer {
	private static final RenderHandler INSTANCE = new RenderHandler();
	
	public static RenderHandler getInstance() {
		return INSTANCE;
	}
	
	@Override
	public Supplier<String> getProfilerSectionSupplier() {
		return () -> "textile-debugger-render";
	}
	
	/*
	public void renderOldSystem() {
		MinecraftClient mc = MinecraftClient.getInstance();
		Camera camera = mc.gameRenderer.getCamera();
		
		// Creates a copy of the list each tick :)
		Set<UpdateNode> nodes = TextileUpdates.getNodes();
		
		int updates = 0;
		{
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder builder = tess.getBuffer();
			
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			
			MatrixStack modelStack = RenderSystem.getModelViewStack();
			modelStack.push();
			{
				Vec3d pos = camera.getPos();
				modelStack.translate(-(float)pos.x, -(float)pos.y, -(float)pos.z);
			}
			RenderSystem.applyModelViewMatrix();
			
			RenderSystem.lineWidth(6.0f);
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			
			{
				builder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
				for(UpdateNode node : nodes) {
					BlockPos pos = node.getPosition();
					updates += node.count();
					TextileRenderUtil.renderOutline(tess, builder, pos.getX(), pos.getY(), pos.getZ(), 0xffffff, 0.7f);
				}
				
				tess.draw();
				modelStack.pop();
			}
			
			{
				modelStack.push();
				
				RenderSystem.setShader(GameRenderer::getRenderTypeTextShader);
				
				for(UpdateNode node : nodes) {
					BlockPos pos = node.getPosition();
					String text = Integer.toString(node.count());
					
					RenderUtils.drawTextPlate(List.of(text), pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f,
							camera.getYaw(),
							camera.getPitch(),
							0.025f,
							0xffffff,
							0,
							false
					);
				}
				
				
				{
					Vec3d pos = camera.getPos();
					Vec3d dir;
					{
						Vec3f tmp = new Vec3f(0, 0, 1);
						tmp.rotate(camera.getRotation());
						dir = new Vec3d(tmp);
					}
					
					
					UpdateNode node = RaycastUtil.raycastNodes(camera, nodes, 30.0f);
					
					RenderUtils.drawTextPlate(List.of(
							"(%.3f, %.3f, %.3f)".formatted(dir.x, dir.y, dir.z),
							"(%.3f, %.3f, %.3f)".formatted(pos.x, pos.y, pos.z),
							"Updates: %d".formatted(updates)
							), 0, 6, 0,
							camera.getYaw(),
							camera.getPitch(),
							0.025f,
							0xffffff,
							0,
							false
					);
					
					if(node != null) {
						Set<UpdateData> set = node.getSet();
						BlockPos hit = node.getPosition();
						
						RenderUtils.drawTextPlate(List.of("[Node]"), hit.getX() + 0.5f, hit.getY() + 0.5f + 0.25f, hit.getZ() + 0.5f,
								camera.getYaw(), camera.getPitch(), 0.025f, 0xffffff, 0xff000000, false
						);
					}
				}
				
				modelStack.pop();
			}
			
			RenderSystem.applyModelViewMatrix();
		}
	}
	*/
	
	@Override
	public void onRenderWorldLast(MatrixStack matrixStack, Matrix4f projMatrix) {
		//renderOldSystem();
		
		MinecraftClient mc = MinecraftClient.getInstance();
		Camera camera = mc.gameRenderer.getCamera();
		
		Map<BlockPos, BlockUpdateContainer> nodes_map = TextileUpdates.getContainers();
		Collection<BlockUpdateContainer> nodes = nodes_map.values();
		
		int updates = 0;
		{
			MatrixStack modelStack = RenderSystem.getModelViewStack();
			{
				modelStack.push();
				Vec3d pos = camera.getPos();
				modelStack.translate(-(float)pos.x, -(float)pos.y, -(float)pos.z);
				RenderSystem.applyModelViewMatrix();
			}
			
			{
				Tessellator tess = Tessellator.getInstance();
				BufferBuilder builder = tess.getBuffer();
				
				RenderSystem.setShader(GameRenderer::getPositionColorShader);
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				builder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
				for(BlockUpdateContainer node : nodes) {
					if(node == null) continue;
					
					BlockPos pos = node.getPosition();
					TextileRenderUtil.renderOutline(tess, builder, pos.getX(), pos.getY(), pos.getZ(), 0xffffff, 0.7f);
				}
				
				tess.draw();
				modelStack.pop();
			}
			
			{
				modelStack.push();
				RenderSystem.setShader(GameRenderer::getRenderTypeTextShader);
				
				for(BlockUpdateContainer node : nodes) {
					if(node == null) continue;
					
					BlockPos pos = node.getPosition();
					int node_updates = node.countUpdates();
					updates += node_updates;
					
					String text = Integer.toString(node_updates);
					
					RenderUtils.drawTextPlate(List.of(text), pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f,
						camera.getYaw(),
						camera.getPitch(),
						0.025f,
						0xffffff,
						0,
						false
					);
				}
				
				{
					Vec3d pos = camera.getPos();
					Vec3d dir;
					{
						Vec3f tmp = new Vec3f(0, 0, 1);
						tmp.rotate(camera.getRotation());
						dir = new Vec3d(tmp);
					}
					
					BlockUpdateContainer node = RaycastUtil.raycastContainers(camera, nodes_map, 30.0f);
					
					RenderUtils.drawTextPlate(List.of(
							"(%.3f, %.3f, %.3f)".formatted(dir.x, dir.y, dir.z),
							"(%.3f, %.3f, %.3f)".formatted(pos.x, pos.y, pos.z),
							"Updates: %d".formatted(updates)
							), 0, 6, 0,
							camera.getYaw(),
							camera.getPitch(),
							0.025f,
							0xffffff,
							0,
							false
					);
					
					if(node != null) {
						BlockPos hit = node.getPosition();
						
						RenderUtils.drawTextPlate(List.of("[Node]"), hit.getX() + 0.5f, hit.getY() + 0.5f + 0.25f, hit.getZ() + 0.5f,
							camera.getYaw(), camera.getPitch(), 0.025f, 0xffffff, 0xff000000, false
						);
					}
				}
				
				modelStack.pop();
			}
			
			RenderSystem.applyModelViewMatrix();
		}
	}
	
	/*
	private void old_renderOverlay(MatrixStack matrixStack) {
		// Render game overlay
		
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer textRenderer = mc.textRenderer;
		if(textRenderer == null) return;
		
		if(mc.getCameraEntity() instanceof ClientPlayerEntity) {
			ClientPlayerEntity player = (ClientPlayerEntity)mc.getCameraEntity();
			ItemStack heldItem = player.getInventory().getMainHandStack();
			
			if(heldItem.getItem() != Items.REDSTONE_TORCH) {
				return;
			}
		}
		
		matrixStack.push();
		
		double scale_factor = MinecraftClient.getInstance().getWindow().getScaleFactor();
		
		if(scale_factor > 1) {
			float factor = (float)((scale_factor - 1) / scale_factor);
			matrixStack.scale(factor, factor, factor);
		}
		
		RenderUtils.renderText(2, 2, 0xffffff, "Looking at 0 nodes, scale=%.4f".formatted(scale_factor), matrixStack);
		
		Camera camera = mc.gameRenderer.getCamera();
		
		// Creates a copy of the list each tick :)
		Set<UpdateNode> nodes = TextileUpdates.getNodes();
		UpdateNode node = RaycastUtil.raycastNodes(camera, nodes, 30.0f);
		
		if(node != null) {
			Set<UpdateData> set = node.getSet();
			
			BlockPos hit = node.getPosition();
			
			int idx = 0;
			for(UpdateData data : set) {
				if(idx++ != 0) {
					continue;
				}
				
				String trace = data.getTrace();
				
				RenderUtils.renderText(2, 20, 0xffffff, trace, matrixStack);
				
				break;
			}
		}
		
		matrixStack.pop();
	}
	*/
	
	@Override
	public void onRenderGameOverlayPost(MatrixStack matrixStack) {
		//old_renderOverlay(matrixStack);
		
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer textRenderer = mc.textRenderer;
		if(textRenderer == null) return;
		
		if(mc.getCameraEntity() instanceof ClientPlayerEntity) {
			ClientPlayerEntity player = (ClientPlayerEntity)mc.getCameraEntity();
			ItemStack heldItem = player.getInventory().getMainHandStack();
			
			if(heldItem.getItem() != Items.REDSTONE_TORCH) {
				return;
			}
		}
		
		matrixStack.push();
		
		double scale_factor = MinecraftClient.getInstance().getWindow().getScaleFactor();
		
		if(scale_factor > 1) {
			float factor = (float)((scale_factor - 1) / scale_factor);
			matrixStack.scale(factor, factor, factor);
		}
		
		RenderUtils.renderText(2, 2, 0xffffff, "Looking at 0 nodes, scale=%.4f".formatted(scale_factor), matrixStack);
		
		Camera camera = mc.gameRenderer.getCamera();
		
		// Creates a copy of the list each tick :)
		Map<BlockPos, BlockUpdateContainer> nodes = TextileUpdates.getContainers();
		BlockUpdateContainer node = RaycastUtil.raycastContainers(camera, nodes, 30.0f);
		
		if(node != null) {
			Iterator<BlockUpdateObject> set = node.getIterator();
			
			BlockPos hit = node.getPosition();
			
			int idx = 0;
			while(set.hasNext()) {
				BlockUpdateObject data = set.next();
				if(data == null) break;
				
				String trace = data.getTrace();
				
				RenderUtils.renderText(2, 20, 0xffffff, trace, matrixStack);
				
				break;
			}
		}
		
		matrixStack.pop();
	}
}
