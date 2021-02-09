/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.mojang.blaze3d.systems.RenderSystem;

public abstract class TFCContainerScreen<C extends ScreenHandler> extends HandledScreen<C>
{
    protected final Identifier texture;

    public TFCContainerScreen(C container, PlayerInventory playerInventory, Text name, Identifier texture)
    {
        super(container, playerInventory, name);
        this.texture = texture;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground(matrixStack);
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    protected void drawDefaultBackground(MatrixStack matrixStack)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(texture);
        drawTexture(matrixStack, x, y, 0, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }
}