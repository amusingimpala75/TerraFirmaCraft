/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.dries007.tfc.mixin.fabric.client.gui.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.network.PacketDistributor;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PlayerInventoryTabButton extends ButtonWidget
{
    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/gui/icons.png");

    private final int textureU;
    private final int textureV;
    private final int iconU;
    private final int iconV;
    private int iconX;
    private int iconY;
    private int prevGuiLeft;
    private int prevGuiTop;
    private Runnable tickCallback;

    public PlayerInventoryTabButton(int guiLeft, int guiTop, int xIn, int yIn, int widthIn, int heightIn, int textureU, int textureV, int iconX, int iconY, int iconU, int iconV, SwitchInventoryTabPacket.Type type)
    {
        this(guiLeft, guiTop, xIn, yIn, widthIn, heightIn, textureU, textureV, iconX, iconY, iconU, iconV, button -> PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(type)));
    }

    public PlayerInventoryTabButton(int guiLeft, int guiTop, int xIn, int yIn, int widthIn, int heightIn, int textureU, int textureV, int iconX, int iconY, int iconU, int iconV, PressAction onPressIn)
    {
        super(guiLeft + xIn, guiTop + yIn, widthIn, heightIn, LiteralText.EMPTY, onPressIn);
        this.prevGuiLeft = guiLeft;
        this.prevGuiTop = guiTop;
        this.textureU = textureU;
        this.textureV = textureV;
        this.iconX = guiLeft + xIn + iconX;
        this.iconY = guiTop + yIn + iconY;
        this.iconU = iconU;
        this.iconV = iconV;
        this.tickCallback = () -> {};
    }

    public PlayerInventoryTabButton setRecipeBookCallback(InventoryScreen screen)
    {
        // Because forge is ass and removed the event for "button clicked", and I don't care to deal with the shit in MinecraftForge#5548, this will do for now
        this.tickCallback = new Runnable()
        {
            boolean recipeBookVisible = screen.getRecipeBookWidget().isOpen();

            @Override
            public void run()
            {
                boolean newRecipeBookVisible = screen.getRecipeBookWidget().isOpen();
                if (newRecipeBookVisible != recipeBookVisible)
                {
                    recipeBookVisible = newRecipeBookVisible;
                    PlayerInventoryTabButton.this.updateGuiSize(((ScreenAccessor) screen).accessor$x(), ((ScreenAccessor) screen).accessor$y());
                }
            }
        };
        return this;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        minecraft.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.disableDepthTest();

        tickCallback.run();

        drawTexture(matrixStack, x, y, 0, (float) textureU, (float) textureV, width, height, 256, 256);
        drawTexture(matrixStack, iconX, iconY, 16, 16, (float) iconU, (float) iconV, 32, 32, 256, 256);
        RenderSystem.enableDepthTest();
    }

    public void updateGuiSize(int guiLeft, int guiTop)
    {
        this.x += guiLeft - prevGuiLeft;
        this.y += guiTop - prevGuiTop;

        this.iconX += guiLeft - prevGuiLeft;
        this.iconY += guiTop - prevGuiTop;

        prevGuiLeft = guiLeft;
        prevGuiTop = guiTop;
    }
}