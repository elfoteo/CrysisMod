package com.elfoteo.crysis;

import com.elfoteo.crysis.capability.INanosuitModeCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static com.elfoteo.crysis.CrysisMod.isWearingFullNanosuit;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class GlowEventHandler {
    @SubscribeEvent
    public static void onEntityRender(RenderLivingEvent.Pre<?, ?> event) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        LivingEntity entity = event.getEntity();

        if (player != null && entity != null && isWearingFullNanosuit(player)) {
            INanosuitModeCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
            if (nanosuitCapability.getMode().equals(NanosuitModes.VISOR) && entity != player){
                entity.setGlowing(true);
            }
            else {
                if (entity.getEffect(Effects.GLOWING) == null) {
                    entity.setGlowing(false);
                }
            }

            if (entity instanceof PlayerEntity){
                INanosuitModeCapability entityNanosuitCapability = entity.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                if (entityNanosuitCapability.getMode().equals(NanosuitModes.STEALTH)){
                    if (event.isCancelable()) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    private static void modifyMobBehavior() {
        World world = Minecraft.getInstance().level;  // Adjust to the appropriate dimension
        World world2 = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
        if (world != null && world2 != null) {
            List<? extends PlayerEntity> players = world.players();

            for (PlayerEntity player : players) {
                List<? extends LivingEntity> nearbyMonsters = world2.getNearbyEntities(
                        MonsterEntity.class,
                        new EntityPredicate(),
                        player,
                        player.getBoundingBox().inflate(10.0)
                );
                INanosuitModeCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                for (LivingEntity livingEntity : nearbyMonsters) {
                    if (livingEntity instanceof MonsterEntity) {
                        MonsterEntity monster = (MonsterEntity) livingEntity;
                        if (monster.getTarget() instanceof PlayerEntity){
                            if (nanosuitCapability.getMode() == NanosuitModes.STEALTH){
                                monster.setTarget(null);
                            }
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            modifyMobBehavior();
            World world = Minecraft.getInstance().level;
            if (world != null && world.getGameTime() % 10 == 0){
                List<? extends PlayerEntity> playerList = world.players();
                for (PlayerEntity player : playerList) {
                    if (isWearingFullNanosuit(player)){
                        INanosuitModeCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                        NanosuitModes mode = nanosuitCapability.getMode();
                        switch (mode){
                            case SPEED:
                                player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 40, 1, false, true));
                                break;
                            case ARMOR:
                                player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 40, 1, false, true));
                                player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 0, false, true));
                                break;
                            case STEALTH:
                                player.addEffect(new EffectInstance(Effects.INVISIBILITY, 40, 0, false, true));
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }




    @SubscribeEvent
    public void preRenderPlayer(RenderPlayerEvent.Pre event) {
        System.out.println("LOL1");
        event.getMatrixStack().scale(0.1f, 0.1f, 0.1f);
        event.getMatrixStack().translate(1, 1, 2);
    }

    @SubscribeEvent
    public void postRenderPlayer(RenderPlayerEvent.Post event) {
        System.out.println("LOL2");
        event.getMatrixStack().pushPose();
        event.getMatrixStack().scale(0.1f, 0.1f, 0.1f);
        event.getMatrixStack().translate(1, 1, 2);
        event.getMatrixStack().popPose();
        System.out.println("LOL3");
    }


}