package com.equilcraft.bigsteppa.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

public class BigFakePlayer extends FakePlayer {
	public BigFakePlayer(WorldServer world, GameProfile owner) {
		super(world, owner);
	}

	public BigFakePlayer(EntityPlayer player) {
		this((WorldServer) player.worldObj, player.getGameProfile());
	}
}
