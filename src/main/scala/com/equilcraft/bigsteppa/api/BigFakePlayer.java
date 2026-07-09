package com.equilcraft.bigsteppa.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.HashMap;

public class BigFakePlayer extends FakePlayer {
	public static final HashMap<GameProfile, BigFakePlayer> fakePlayers = new HashMap<>();

	public static BigFakePlayer getFakePlayer(World world, GameProfile gameProfile) {
		BigFakePlayer fakePlayer = fakePlayers.get(gameProfile);

		if (fakePlayer == null) {
			fakePlayer = new BigFakePlayer((WorldServer) world, gameProfile);
			fakePlayers.put(gameProfile, fakePlayer);
		}

		return fakePlayer;
	}

	public BigFakePlayer(WorldServer world, GameProfile owner) {
		super(world, owner);
	}

	public BigFakePlayer(EntityPlayer player) {
		this((WorldServer) player.worldObj, player.getGameProfile());
	}
}
