package textile.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Bootstrap;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class TextileRenderUtil {
	private static final Identifier WINDOW_BOX = new Identifier("textile", "textures/gui/box.png");
	
	public static void renderOutline(Tessellator tess, BufferBuilder builder, int x, int y, int z, int rgb, float scale) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >>  8) & 0xff;
		int b = (rgb      ) & 0xff;
		int a = 255;
		
		float x0 = x + 0.5f - scale / 2.0f;
		float x1 = x + 0.5f + scale / 2.0f;
		float y0 = y + 0.5f - scale / 2.0f;
		float y1 = y + 0.5f + scale / 2.0f;
		float z0 = z + 0.5f - scale / 2.0f;
		float z1 = z + 0.5f + scale / 2.0f;
		
		builder.vertex(x0, y0, z0).color(r,g,b,a).next();
		builder.vertex(x1, y0, z0).color(r,g,b,a).next();
		builder.vertex(x1, y0, z0).color(r,g,b,a).next();
		builder.vertex(x1, y1, z0).color(r,g,b,a).next();
		builder.vertex(x1, y1, z0).color(r,g,b,a).next();
		builder.vertex(x0, y1, z0).color(r,g,b,a).next();
		builder.vertex(x0, y1, z0).color(r,g,b,a).next();
		builder.vertex(x0, y0, z0).color(r,g,b,a).next();
		
		builder.vertex(x0, y0, z1).color(r,g,b,a).next();
		builder.vertex(x1, y0, z1).color(r,g,b,a).next();
		builder.vertex(x1, y0, z1).color(r,g,b,a).next();
		builder.vertex(x1, y1, z1).color(r,g,b,a).next();
		builder.vertex(x1, y1, z1).color(r,g,b,a).next();
		builder.vertex(x0, y1, z1).color(r,g,b,a).next();
		builder.vertex(x0, y1, z1).color(r,g,b,a).next();
		builder.vertex(x0, y0, z1).color(r,g,b,a).next();
		
		builder.vertex(x0, y0, z0).color(r,g,b,a).next();
		builder.vertex(x0, y0, z1).color(r,g,b,a).next();
		builder.vertex(x1, y0, z0).color(r,g,b,a).next();
		builder.vertex(x1, y0, z1).color(r,g,b,a).next();
		builder.vertex(x1, y1, z0).color(r,g,b,a).next();
		builder.vertex(x1, y1, z1).color(r,g,b,a).next();
		builder.vertex(x0, y1, z0).color(r,g,b,a).next();
		builder.vertex(x0, y1, z1).color(r,g,b,a).next();
	}
	
	public static void drawWindowBox(MatrixStack matrixStack, int x, int y, int width, int height, int scale) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, WINDOW_BOX);
		
		matrixStack.push();
		matrixStack.scale(scale, scale, scale);
		
		
		// top
		DrawableHelper.drawTexture(matrixStack, x, y, 4, 4, 0, 0, 4, 4, 16, 16);
		DrawableHelper.drawTexture(matrixStack, x+4, y, width-8, 4, 4, 0, 1, 4, 16, 16);
		DrawableHelper.drawTexture(matrixStack, x+width-4, y, 4, 4, 5, 0, 4, 4, 16, 16);
		
		// middle
		DrawableHelper.drawTexture(matrixStack, x, y+4, 4, height-8, 0, 5, 4, 1, 16, 16);
		DrawableHelper.drawTexture(matrixStack, x+4, y+4, width-8, height-8, 4, 4, 1, 1, 16, 16);
		DrawableHelper.drawTexture(matrixStack, x+width-4, y+4, 4, height-8, 5, 4, 4, 1, 16, 16);
		
		// bottom
		DrawableHelper.drawTexture(matrixStack, x, y+height-4, 4, 4, 0, 5, 4, 4, 16, 16);
		DrawableHelper.drawTexture(matrixStack, x+4, y+height-4, width-8, 4, 4, 5, 1, 4, 16, 16);
		DrawableHelper.drawTexture(matrixStack, x+width-4, y+height-4, 4, 4, 5, 5, 4, 4, 16, 16);
		
		matrixStack.pop();
	}
}
