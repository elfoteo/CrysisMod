package com.elfoteo.crysis;

import com.elfoteo.crysis.capability.INanosuitCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Objects;

import static com.elfoteo.crysis.CrysisMod.isWearingFullNanosuit;

@Mod.EventBusSubscriber()
public class NanosuitHandler {
    private static void modifyMobBehavior() {
        World world = Minecraft.getInstance().level;  // Adjust to the appropriate dimension
        World world2 = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
        if (world != null && world2 != null) {
            List<? extends PlayerEntity> players = world.players();

            for (PlayerEntity player : players) {
                INanosuitCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                try{
                    if (nanosuitCapability.getMode() == NanosuitModes.STEALTH) {
                        List<? extends LivingEntity> nearbyMonsters = world2.getNearbyEntities(
                                MonsterEntity.class,
                                new EntityPredicate(),
                                player,
                                player.getBoundingBox().inflate(24.0)
                        );

                        for (LivingEntity livingEntity : nearbyMonsters) {
                            if (livingEntity instanceof MonsterEntity) {
                                MonsterEntity monster = (MonsterEntity) livingEntity;
                                if (Objects.equals(monster.getTarget(), player)){
                                    monster.setTarget(null);
                                }
                            }
                        }
                    }
                }
                catch (Exception ignored){

                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            LivingEntity target = event.getEntityLiving();

            // Check if the attacker is a player and the target is not null
            if (player != null && target != null) {
                INanosuitCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                if (nanosuitCapability.getMode() == NanosuitModes.STEALTH){
                    nanosuitCapability.setEnergy(nanosuitCapability.getEnergy()-25);
                    nanosuitCapability.setMode(NanosuitModes.IDLE);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            modifyMobBehavior();
            World world = Minecraft.getInstance().level;
            if (world == null){
                return;
            }
            for (PlayerEntity player: world.players()){
                if (isWearingFullNanosuit(player)){
                    INanosuitCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                    nanosuitCapability.stepInvisTransition();
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.START){
            ServerWorld overworld = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
            updatePlayerNanosuit(event.player, overworld);
        }
    }

    private static void updatePlayerNanosuit(PlayerEntity player, World world){
        if (isWearingFullNanosuit(player)){
            INanosuitCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
            if (nanosuitCapability.getEnergy() <= 0){
                nanosuitCapability.setMode(NanosuitModes.IDLE);
            }
            NanosuitModes mode = nanosuitCapability.getMode();
            if (player instanceof ClientPlayerEntity){
                CrysisMod.setNanosuitMode(player, mode);
            }
            else{
                mode = CrysisMod.getNanosuitMode(player);
            }

            if (mode != NanosuitModes.ARMOR){
                player.addEffect(new EffectInstance(Effects.JUMP, 19, 0, true, true));
                if (mode != NanosuitModes.SPEED) {
                    player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 19, 0, true, true));
                }
            }
            Minecraft.getInstance().options.gamma = 1000;
            switch (mode) {
                case SPEED:
                    player.removeEffect(Effects.DAMAGE_RESISTANCE);
                    player.removeEffect(Effects.MOVEMENT_SLOWDOWN);
                    player.removeEffect(Effects.INVISIBILITY);
                    player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 19, 1, true, true));
                    if (player.isSprinting()) {
                        nanosuitCapability.setEnergy(nanosuitCapability.getEnergy() - 3);
                    } else if (isPlayerMoving(player)) {
                        nanosuitCapability.setEnergy(nanosuitCapability.getEnergy() - 2);
                    } else {
                        if (world.getGameTime() % 4 == 0) {
                            nanosuitCapability.setEnergy(nanosuitCapability.getEnergy() - 1);
                        }
                    }
                    break;
                case ARMOR:
                    player.removeEffect(Effects.MOVEMENT_SPEED);
                    player.removeEffect(Effects.INVISIBILITY);
                    player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 19, 1, true, true));
                    player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 19, 0, true, true));
                    if (isPlayerMoving(player)) {
                        nanosuitCapability.setEnergy(nanosuitCapability.getEnergy() - 2);
                    }
                    else{
                        nanosuitCapability.setEnergy(nanosuitCapability.getEnergy() - 1);
                    }
                    break;

                case STEALTH:
                    player.removeEffect(Effects.DAMAGE_RESISTANCE);
                    player.removeEffect(Effects.MOVEMENT_SLOWDOWN);
                    player.getActiveEffects().stream().filter(effect -> effect.getAmplifier() > 0).findAny().ifPresent(effect -> player.removeEffect(Effects.MOVEMENT_SPEED));
                    player.addEffect(new EffectInstance(Effects.INVISIBILITY, 19, 0, true, false));
                    if (isPlayerMoving(player)) {
                        nanosuitCapability.setEnergy(nanosuitCapability.getEnergy() - 2);
                    } else {
                        if (world.getGameTime() % 3 == 0) {
                            nanosuitCapability.setEnergy(nanosuitCapability.getEnergy() - 1);
                        }
                    }
                    break;

                case VISOR:
                    player.removeEffect(Effects.DAMAGE_RESISTANCE);
                    player.removeEffect(Effects.MOVEMENT_SLOWDOWN);
                    player.getActiveEffects().stream().filter(effect -> effect.getAmplifier() > 0).findAny().ifPresent(effect -> player.removeEffect(Effects.MOVEMENT_SPEED));
                    player.removeEffect(Effects.INVISIBILITY);
                    break; // Do not regenerate energy

                default:
                    player.removeEffect(Effects.DAMAGE_RESISTANCE);
                    player.removeEffect(Effects.MOVEMENT_SLOWDOWN);
                    player.getActiveEffects().stream().filter(effect -> effect.getAmplifier() > 0).findAny().ifPresent(effect -> player.removeEffect(Effects.MOVEMENT_SPEED));
                    player.removeEffect(Effects.INVISIBILITY);
                    nanosuitCapability.setEnergy(nanosuitCapability.getEnergy() + 4);
                    break;
            }
        }
        else{
            Minecraft.getInstance().options.gamma = CrysisMod.gammaWithoutNanosuit;
        }
    }

    private static boolean isPlayerMoving(PlayerEntity player) {
        // Check if the player's velocity in any direction is greater than a threshold
        double velocityThreshold = 0.01;

        return Math.abs(player.getDeltaMovement().x) > velocityThreshold ||
                Math.abs(player.getDeltaMovement().y) > velocityThreshold ||
                Math.abs(player.getDeltaMovement().z) > velocityThreshold;
    }
}