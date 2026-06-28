package com.equilcraft.bigsteppa.client.render;

import com.equilcraft.bigsteppa.client.model.ModelArmorStand;
import com.equilcraft.bigsteppa.common.tile.TileArmorStand;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileArmorStandRenderer extends TileEntitySpecialRenderer {

    private final ModelArmorStand standModel = new ModelArmorStand();
    private final ArmorStandBipedRenderer armorRenderer = new ArmorStandBipedRenderer();
    private EntityLiving armorDummy;
    private World dummyWorld;

    public TileArmorStandRenderer() {
        armorRenderer.setRenderManager(RenderManager.instance);
    }

    @Override
    public void renderTileEntityAt(
        TileEntity tile,
        double x,
        double y,
        double z,
        float partialTicks
    ) {
        if (!(tile instanceof TileArmorStand)) {
            return;
        }
        TileArmorStand armorStand = (TileArmorStand) tile;
        int rotation = tile.getBlockMetadata() & 3;
        float yaw = rotation * 90.0F;

        renderStand(x, y, z, yaw);
        renderArmor(armorStand, x, y, z, yaw, partialTicks);
    }

    private void renderStand(double x, double y, double z, float yaw) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.48F, 0.29F, 0.13F, 1.0F);
        standModel.render(0.0625F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private void renderArmor(
        TileArmorStand armorStand,
        double x,
        double y,
        double z,
        float yaw,
        float partialTicks
    ) {
        ensureDummy(armorStand.getWorldObj());
        armorDummy.setCurrentItemOrArmor(4, armorStand.getStackInSlot(TileArmorStand.SLOT_HEAD));
        armorDummy.setCurrentItemOrArmor(3, armorStand.getStackInSlot(TileArmorStand.SLOT_CHEST));
        armorDummy.setCurrentItemOrArmor(2, armorStand.getStackInSlot(TileArmorStand.SLOT_LEGS));
        armorDummy.setCurrentItemOrArmor(1, armorStand.getStackInSlot(TileArmorStand.SLOT_FEET));
        armorDummy.setPosition(
            armorStand.xCoord + 0.5D,
            armorStand.yCoord,
            armorStand.zCoord + 0.5D
        );
        armorDummy.prevRenderYawOffset = yaw;
        armorDummy.renderYawOffset = yaw;
        armorDummy.prevRotationYawHead = yaw;
        armorDummy.rotationYawHead = yaw;
        armorDummy.prevRotationYaw = yaw;
        armorDummy.rotationYaw = yaw;
        armorDummy.prevRotationPitch = 0.0F;
        armorDummy.rotationPitch = 0.0F;
        armorDummy.limbSwing = 0.0F;
        armorDummy.limbSwingAmount = 0.0F;
        armorDummy.prevLimbSwingAmount = 0.0F;

        GL11.glPushMatrix();
        armorRenderer.doRender(
            armorDummy,
            x + 0.5D,
            y,
            z + 0.5D,
            yaw,
            partialTicks
        );
        GL11.glPopMatrix();
    }

    private void ensureDummy(World world) {
        if (armorDummy == null || dummyWorld != world) {
            dummyWorld = world;
            armorDummy = new EntityLiving(world) {
            };
        }
    }
}
