package com.min01.tambs.gui.tab;

import java.util.List;
import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.min01.tambs.gui.components.CheckEditBox;
import com.min01.tambs.gui.components.DragBox;
import com.min01.tambs.gui.components.PairCheckbox;
import com.min01.tambs.gui.components.SuggestionEditBox;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.network.AddTeamPacket;
import com.min01.tambs.network.MoveMobPacket;
import com.min01.tambs.network.RemoveTeamPacket;
import com.min01.tambs.network.TAMBSNetwork;
import com.min01.tambs.util.TAMBSClientUtil;
import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;

public class ToolTab extends TAMBSTab
{
	private PairCheckbox teleportBox;
	private PairCheckbox teamBox;
	private SuggestionEditBox teamName;
	private SuggestionEditBox teamColor;
	private CheckEditBox mobGriefing;
	private CheckEditBox mobKill;
	private CheckEditBox mobEffect;
	
	private final DragBox dragBox = new DragBox();
	private final TAMBSScreen screen;
	
	public ToolTab(TAMBSScreen screen)
	{
		super(screen.width / 4, screen.height - (TAMBSScreen.TAB_HEIGHT + 45), screen.width, screen.height, Component.translatable("tambs.tab.tools"));
		this.teamBox = new PairCheckbox(5, screen.height - (TAMBSScreen.TAB_HEIGHT + 15), 20, 20, Component.translatable("tambs.button.team_setting"), false);
		this.teleportBox = new PairCheckbox(this.teamBox.getX() + 100, screen.height - (TAMBSScreen.TAB_HEIGHT + 15), 20, 20, Component.translatable("tambs.button.mob_teleport"), false);
		this.teamBox.setOther(this.teleportBox);
		this.teleportBox.setOther(this.teamBox);
		this.teamName = new SuggestionEditBox(Minecraft.getInstance().font, 5, this.teamBox.getY() + 40, 100, 20, Component.translatable("tambs.button.name"), 10, false, SuggestionEditBox.ofString(Minecraft.getInstance().level.getScoreboard().getTeamNames()));
		List<String> colors = List.of(ChatFormatting.values()).stream().filter(t -> t.getColor() != null).map(t -> t.getName()).toList();
		this.teamColor = new SuggestionEditBox(Minecraft.getInstance().font, 5, this.teamName.getY() + 40, 100, 20, Component.translatable("tambs.button.color"), 5, false, SuggestionEditBox.ofString(colors));
		this.mobGriefing = new CheckEditBox(Minecraft.getInstance().font, this.teamName.getX() + 120, this.teleportBox.getY() + 40, 100, 20, Component.translatable("tambs.button.mob_griefing"), 5, false, CheckEditBox.blocks());
		this.mobKill = new CheckEditBox(Minecraft.getInstance().font, this.teamName.getX() + 120, this.mobGriefing.getY() + 40, 100, 20, Component.translatable("tambs.button.mob_kill"), 5, false, CheckEditBox.entities());
		this.mobEffect = new CheckEditBox(Minecraft.getInstance().font, this.mobGriefing.getX() + 120, this.teleportBox.getY() + 40, 100, 20, Component.translatable("tambs.button.mob_effect"), 5, false, CheckEditBox.mobEffects());
		if(this.teamName.getValue().isEmpty())
		{
			this.teamName.setValue("Mob1");
			PlayerTeam team = Minecraft.getInstance().level.getScoreboard().getPlayerTeam("Mob1");
			if(team != null && team.getColor().getColor() != null)
			{
				this.teamColor.setValue(team.getColor().getName());
				this.dragBox.setColor(team.getColor().getColor());
			}
			else
			{
				this.dragBox.setColor(ChatFormatting.WHITE.getColor());
			}
		}
		this.teamName.setResponder(t -> 
		{
			PlayerTeam team = Minecraft.getInstance().level.getScoreboard().getPlayerTeam(t);
			if(team != null && team.getColor().getColor() != null)
			{
				this.teamColor.setValue(team.getColor().getName());
				this.dragBox.setColor(team.getColor().getColor());
			}
			else
			{
				this.dragBox.setColor(ChatFormatting.WHITE.getColor());
			}
		});
		this.teamColor.setResponder(t ->
		{
			Integer color = this.getColor(t).getColor();
			if(color == null)
			{
				color = ChatFormatting.WHITE.getColor();
			}
			this.dragBox.setColor(color);
		});
		this.screen = screen;
	}
	
	public ChatFormatting getColor(String name)
	{
		ChatFormatting color = ChatFormatting.getByName(name);
		return color != null && color.getColor() != null ? color : ChatFormatting.RESET;
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer) 
	{
    	pConsumer.accept(this.teamBox);
    	pConsumer.accept(this.teleportBox);
    	pConsumer.accept(this.teamName);
    	pConsumer.accept(this.teamColor);
    	pConsumer.accept(this.mobGriefing);
    	pConsumer.accept(this.mobKill);
    	pConsumer.accept(this.mobEffect);
		super.visitChildren(pConsumer);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		this.teamName.updateSuggestions(SuggestionEditBox.ofString(Minecraft.getInstance().level.getScoreboard().getTeamNames()));
		if(!this.isActive())
		{
			if(this.teamBox.selected() || this.teleportBox.selected())
			{
				TAMBSClientUtil.hover();
			}
		}
	}
	
	@Override
	public void load(Options options) 
	{
		this.mobGriefing.checked.addAll(options.mob_griefing);
		this.mobKill.checked.addAll(options.mob_kill);
		this.mobEffect.checked.addAll(options.mob_effect);
	}
	
	@Override
	public void save(Options options) 
	{
		options.mob_griefing.clear();
		options.mob_kill.clear();
		options.mob_effect.clear();
		options.mob_griefing.addAll(this.mobGriefing.checked);
		options.mob_kill.addAll(this.mobKill.checked);
		options.mob_effect.addAll(this.mobEffect.checked);
	}
	
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
	{
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		if(!this.isActive() && this.teamBox.selected() && TAMBSClientData.isPaused() && !TAMBSClientUtil.isCameraMoving())
		{
			this.dragBox.render(pGuiGraphics, this.screen.width, this.screen.height);
		}
		if(this.isActive())
		{
			Minecraft minecraft = Minecraft.getInstance();
			Font font = minecraft.font;
			pGuiGraphics.drawString(font, this.teamName.getMessage(), this.teamName.getX(), this.teamName.getY() - 13, 0xFFFFFF);
			pGuiGraphics.drawString(font, this.teamColor.getMessage(), this.teamColor.getX(), this.teamColor.getY() - 13, 0xFFFFFF);
			pGuiGraphics.drawString(font, this.mobGriefing.getMessage(), this.mobGriefing.getX(), this.mobGriefing.getY() - 13, 0xFFFFFF);
			pGuiGraphics.drawString(font, this.mobKill.getMessage(), this.mobKill.getX(), this.mobKill.getY() - 13, 0xFFFFFF);
			pGuiGraphics.drawString(font, this.mobEffect.getMessage(), this.mobEffect.getX(), this.mobEffect.getY() - 13, 0xFFFFFF);
		}
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) 
	{
		if(!this.isActive() && TAMBSClientData.isPaused())
		{
			if(this.teleportBox.selected() && pButton == 1)
			{
				TAMBSClientData.selectUUID(null);
            	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
			}
			TAMBSClientUtil.raycast(t ->
			{
				if(t instanceof LivingEntity living)
				{
					if(pButton == 0)
					{
						if(this.teleportBox.selected())
						{
			            	TAMBSClientData.selectUUID(living.getUUID());
			            	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
						}
						else if(this.teamBox.selected())
						{
		    				TAMBSNetwork.sendToServer(new AddTeamPacket(living.getUUID(), this.teamName.getValue(), this.getColor(this.teamColor.getValue())));
			            	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
			            	TAMBSClientData.LAST_PLACED = living.blockPosition();
						}
					}
					else if(pButton == 1)
					{
						if(this.teamBox.selected())
						{
		    				TAMBSNetwork.sendToServer(new RemoveTeamPacket(living.getUUID()));
			            	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
			            	TAMBSClientData.LAST_PLACED = living.blockPosition();
						}
					}
				}
			}, t -> 
			{
				if(pButton == 0 && this.teleportBox.selected())
				{
					if(TAMBSClientData.SELECTED_UUID != null)
					{
		            	BlockPos blockPos = t.getBlockPos();
		                Direction direction = t.getDirection();
		                blockPos = blockPos.relative(direction);
		    			Entity entity = TAMBSUtil.getEntityByUUID(Minecraft.getInstance().level, TAMBSClientData.SELECTED_UUID);
		            	TAMBSNetwork.sendToServer(new MoveMobPacket(TAMBSClientData.SELECTED_UUID, blockPos));
		    			if(entity != null)
		    			{
		    				entity.setPos(Vec3.atBottomCenterOf(blockPos));
		    				entity.setOldPosAndRot();
		    			}
		            	TAMBSClientData.selectUUID(null);
		            	TAMBSClientData.LAST_PLACED = entity.blockPosition();
					}
				}
			});
			return false;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(!this.isActive() && this.teamBox.selected() && TAMBSClientData.isPaused() && !TAMBSClientUtil.isCameraMoving())
		{
			this.dragBox.enable(pMouseX, pMouseY);
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
	{
		if(this.teamBox.selected())
		{
			this.dragBox.disable(t -> 
			{
				if(t instanceof LivingEntity living)
				{
					if(pButton == 0)
					{
						TAMBSNetwork.sendToServer(new AddTeamPacket(living.getUUID(), this.teamName.getValue(), this.getColor(this.teamColor.getValue())));
					}
					else if(pButton == 1)
					{
	    				TAMBSNetwork.sendToServer(new RemoveTeamPacket(living.getUUID()));
					}
				}
			});
		}
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean renderBlockHighlight() 
	{
		return this.teleportBox.selected() && TAMBSClientData.SELECTED_UUID != null;
	}
}
