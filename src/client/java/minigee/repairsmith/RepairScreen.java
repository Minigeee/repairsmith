package minigee.repairsmith;

import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.buffer.Unpooled;
import minigee.repairsmith.network.RepairItemsPacketPayload;
import minigee.repairsmith.screen.RepairScreenHandler;
import minigee.repairsmith.util.InventoryUtil;
import minigee.repairsmith.util.NetworkUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class RepairScreen extends HandledScreen<RepairScreenHandler> {
	private static final Identifier GUI_TEXTURE = Identifier.of(Repairsmith.MOD_ID, "textures/gui/repairsmith.png");
	private static final Identifier ICONS_TEXTURE = Identifier.of(Repairsmith.MOD_ID, "textures/gui/icons.png");

	protected static final Text COST_TEXT = Text.translatable("text.repairsmith.cost");
    private static final Text SEPARATOR_TEXT = Text.literal(" - ");

	private static final int COST_X = 104;
	private static final int COST_Y = 52;
	private static final int REPAIR_BTN_X = 104;
	private static final int REPAIR_BTN_Y = 28;

	private RepairButton repairButton;
	private boolean canAfford = false;

	public RepairScreen(RepairScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, GUI_TEXTURE);
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context, mouseX, mouseY, delta);

		// Update repair btn status
		this.repairButton.active = this.handler.getRepairCost() > 0 && this.handler.hasMoreOffers();
		if (this.repairButton.active) {
			this.canAfford = InventoryUtil.canAfford(client.player.getInventory(), this.handler.getRepairCost());
			this.repairButton.active = this.canAfford;
		}

		super.render(context, mouseX, mouseY, delta);

		// Repair items text
		context.drawText(this.textRenderer, Text.translatable("text.repairsmith.repair_items"), this.x + 7,
				this.y + REPAIR_BTN_Y - 11, 0x404040, false);

		// Cost
		int cost = this.handler.getRepairCost();
		if (cost > 0) {
			int goldCost = cost / 10000;
			int silverCost = (cost % 10000) / 100;
			int bronzeCost = cost % 100;
			context.drawText(this.textRenderer, COST_TEXT, this.x + COST_X, this.y + COST_Y, 0x404040, false);
			int offset = 0;
			if (goldCost > 0) {
				ItemStack goldStack = new ItemStack(NumismaticOverhaulItems.GOLD_COIN, goldCost);
				context.drawItem(goldStack, this.x + COST_X, this.y + COST_Y + 8);
				context.drawItemInSlot(this.textRenderer, goldStack, this.x + COST_X,this.y + COST_Y + 8);
				offset += 16;
			}
			if (silverCost > 0) {
				ItemStack silverStack = new ItemStack(NumismaticOverhaulItems.SILVER_COIN, silverCost);
				context.drawItem(silverStack, this.x + COST_X + offset, this.y + COST_Y + 8);
				context.drawItemInSlot(this.textRenderer, silverStack, this.x + COST_X + offset,this.y + COST_Y + 8);
				offset += 16;
			}
			if (bronzeCost > 0) {
				ItemStack bronzeStack = new ItemStack(NumismaticOverhaulItems.BRONZE_COIN, bronzeCost);
				context.drawItem(bronzeStack, this.x + COST_X + offset, this.y + COST_Y + 8);
				context.drawItemInSlot(this.textRenderer, bronzeStack, this.x + COST_X + offset,this.y + COST_Y + 8);
			}
		}

		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void init() {
		super.init();

		titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

		this.repairButton = this.addDrawableChild(new RepairButton(this.x + REPAIR_BTN_X, this.y + REPAIR_BTN_Y,
			Text.translatable("text.repairsmith.repair"), (button) -> {
				if (!button.active)
					return;

				RepairItemsPacketPayload repairPacket = new RepairItemsPacketPayload(this.handler.syncId);
				ClientPlayNetworking.send(repairPacket);
			}));
	}

	public class RepairButton extends ButtonWidget {
		public RepairButton(int x, int y, Text text, ButtonWidget.PressAction onPress) {
			super(x, y, 60, 18, text, onPress, DEFAULT_NARRATION_SUPPLIER);
		}

		@Override
		public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
			int v = 18;
			if (!this.active)
				v = 0;
			else if (this.isHovered())
				v = 36;
			context.drawTexture(ICONS_TEXTURE, this.getX(), this.getY(), 0, v, this.width, this.height);

			int o = this.active ? 0xFFFFFF : 0xA0A0A0;
			context.drawCenteredTextWithShadow(textRenderer, this.getMessage(), this.getX() + this.width / 2,
					this.getY() + (this.height - 8) / 2, o | MathHelper.ceil(this.alpha * 255.0f) << 24);

			// Disabled tooltip
			if (!this.active && this.isHovered()) {
				final var handler = RepairScreen.this.handler;
				Text text = null;

				if (!handler.hasMoreOffers())
					text = Text.translatable("text.repairsmith.no_more_offers");
				else if (handler.getRepairCost() == 0)
					text = Text.translatable("text.repairsmith.no_items");
				else if (!RepairScreen.this.canAfford)
					text = Text.translatable("text.repairsmith.not_affordable");

				if (text != null)
					context.drawTooltip(textRenderer, text, mouseX, mouseY);
			}
		}

	}
}