/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.SimpleContainer;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.KoppenClimateClassification;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ClimateScreen extends TFCContainerScreen<SimpleContainer>
{
    public static final Identifier BACKGROUND = new Identifier(MOD_ID, "textures/gui/player_climate.png");

    public ClimateScreen(SimpleContainer container, PlayerInventory playerInv, Text name)
    {
        super(container, playerInv, name, BACKGROUND);
    }

    @Override
    public void init()
    {
        super.init();
        addButton(new PlayerInventoryTabButton(x, y, 176, 4, 20, 22, 128, 0, 1, 3, 0, 0, button -> {
            playerInventory.player.currentScreenHandler = playerInventory.player.playerScreenHandler;
            MinecraftClient.getInstance().openScreen(new InventoryScreen(playerInventory.player));
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.INVENTORY));
        }));
        addButton(new PlayerInventoryTabButton(x, y, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR));
        addButton(new PlayerInventoryTabButton(x, y, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addButton(new PlayerInventoryTabButton(x, y, 176 - 3, 73, 20 + 3, 22, 128 + 20, 0, 1, 3, 96, 0, button -> {}));
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        super.drawForeground(matrixStack, mouseX, mouseY);

        // Climate at the current player
        float averageTemp = ClimateRenderCache.INSTANCE.getAverageTemperature();
        float rainfall = ClimateRenderCache.INSTANCE.getRainfall();
        float currentTemp = ClimateRenderCache.INSTANCE.getTemperature();

        String climateType = I18n.translate("tfc.tooltip.climate_koppen_climate_classification") + I18n.translate(Helpers.getEnumTranslationKey(KoppenClimateClassification.classify(averageTemp, rainfall)));
        String plateTectonics = I18n.translate("tfc.tooltip.climate_plate_tectonics_classification") + I18n.translate(Helpers.getEnumTranslationKey(ClimateRenderCache.INSTANCE.getPlateTectonicsInfo()));
        String averageTempTooltip = I18n.translate("tfc.tooltip.climate_average_temperature", String.format("%.1f", averageTemp));
        String rainfallTooltip = I18n.translate("tfc.tooltip.climate_annual_rainfall", String.format("%.1f", rainfall));
        String currentTempTooltip = I18n.translate("tfc.tooltip.climate_current_temp", String.format("%.1f", currentTemp));

        textRenderer.draw(matrixStack, climateType, (backgroundWidth - textRenderer.getWidth(climateType)) / 2f, 25, 0x404040);
        textRenderer.draw(matrixStack, plateTectonics, (backgroundWidth - textRenderer.getWidth(plateTectonics)) / 2f, 34, 0x404040);
        textRenderer.draw(matrixStack, averageTempTooltip, (backgroundWidth - textRenderer.getWidth(averageTempTooltip)) / 2f, 43, 0x404040);
        textRenderer.draw(matrixStack, rainfallTooltip, (backgroundWidth - textRenderer.getWidth(rainfallTooltip)) / 2f, 52, 0x404040);
        textRenderer.draw(matrixStack, currentTempTooltip, (backgroundWidth - textRenderer.getWidth(currentTempTooltip)) / 2f, 61, 0x404040);
    }
}