package com.elfoteo.crysis;

import com.elfoteo.crysis.capability.INanosuitModeCapability;
import com.elfoteo.crysis.capability.NanosuitModeCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import com.elfoteo.crysis.capability.NanosuitModeStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod("crysis")
public class CrysisMod {
    public static final String MOD_ID = "crysis";
    public static final KeyBinding CYCLE_POWER = new KeyBinding("key.crysis.cycle_power", GLFW.GLFW_KEY_K, "key.category.crysis");
    public static float globalRot = 0;
    public CrysisMod() {
        // Register the mod for modloading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CrysisMod::onCommonSetup);

        // Register the event handler
        MinecraftForge.EVENT_BUS.register(GlowEventHandler.class);
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        MinecraftForge.EVENT_BUS.register(ForgeEventSubscriber.class);
        ModItems.RegisterArmor(modEventBus);
        ClientRegistry.registerKeyBinding(CYCLE_POWER);
    }
    public static String modeToString(NanosuitModes mode){
        switch (mode){
            case STEALTH:
                return "Stealth";
            case ARMOR:
                return "Armor";
            case SPEED:
                return "Speed";
            case VISOR:
                return "Visor";
            default:
                return "Idle";
        }
    }
    public static boolean isWearingFullNanosuit(PlayerEntity player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlotType.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlotType.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlotType.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);

        return helmet.getItem() instanceof ArmorItem && ((ArmorItem) helmet.getItem()).getMaterial() == NanosuitArmorMaterial.NANOSUIT &&
                chestplate.getItem() instanceof ArmorItem && ((ArmorItem) chestplate.getItem()).getMaterial() == NanosuitArmorMaterial.NANOSUIT &&
                leggings.getItem() instanceof ArmorItem && ((ArmorItem) leggings.getItem()).getMaterial() == NanosuitArmorMaterial.NANOSUIT &&
                boots.getItem() instanceof ArmorItem && ((ArmorItem) boots.getItem()).getMaterial() == NanosuitArmorMaterial.NANOSUIT;
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(INanosuitModeCapability.class, new NanosuitModeStorage(), NanosuitModeCapability::new);
    }

    public static void renderSquare(MatrixStack matrixStack, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int red, int green, int blue, RenderType renderType,
                                    float xRot, float yRot, float zRot, double x, double y, double z, boolean first)
    {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder builder = buffer.getBuffer(renderType);

        matrixStack.pushPose();

        Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        matrixStack.translate(-cam.x+x, -cam.y+y, -cam.z+z);

        if (zRot != 0.0F) {
            matrixStack.mulPose(Vector3f.ZP.rotation(zRot));
        }

        if (yRot != 0.0F) {
            matrixStack.mulPose(Vector3f.YP.rotation(yRot));
        }

        if (xRot != 0.0F) {
            matrixStack.mulPose(Vector3f.XP.rotation(xRot));
        }

        Matrix4f mat = matrixStack.last().pose();

        // Render a cube with size of minX, minY, minZ, maxX, maxY, maxZ
        builder.vertex(mat, minX, minY, minZ).color(255, blue, green, red).uv(0, 0).endVertex();
        builder.vertex(mat, maxX, minY, minZ).color(255, blue, green, red).uv(1, 0).endVertex();
        builder.vertex(mat, maxX, minY, maxZ).color(255, blue, green, red).uv(1, 1).endVertex();
        builder.vertex(mat, minX, minY, maxZ).color(255, blue, green, red).uv(0, 1).endVertex();

        builder.vertex(mat, minX, maxY, minZ).color(255, blue, green, red).uv(0, 0).endVertex();
        builder.vertex(mat, maxX, maxY, minZ).color(255, blue, green, red).uv(1, 0).endVertex();
        builder.vertex(mat, maxX, maxY, maxZ).color(255, blue, green, red).uv(1, 1).endVertex();
        builder.vertex(mat, minX, maxY, maxZ).color(255, blue, green, red).uv(0, 1).endVertex();


        builder.vertex(mat, minX, minY, minZ).color(255, blue, green, red).uv(1, 1).endVertex();
        builder.vertex(mat, minX, maxY, minZ).color(255, blue, green, red).uv(0, 1).endVertex();
        builder.vertex(mat, maxX, maxY, minZ).color(255, blue, green, red).uv(1, 0).endVertex();
        builder.vertex(mat, maxX, minY, minZ).color(255, blue, green, red).uv(0, 0).endVertex();

        builder.vertex(mat, maxX, minY, maxZ).color(255, blue, green, red).uv(1, 1).endVertex();
        builder.vertex(mat, maxX, maxY, maxZ).color(255, blue, green, red).uv(0, 1).endVertex();
        builder.vertex(mat, minX, maxY, maxZ).color(255, blue, green, red).uv(1, 0).endVertex();
        builder.vertex(mat, minX, minY, maxZ).color(255, blue, green, red).uv(0, 0).endVertex();


        builder.vertex(mat, minX, minY, minZ).color(255, blue, green, red).uv(1, 1).endVertex();
        builder.vertex(mat, minX, maxY, minZ).color(255, blue, green, red).uv(0, 1).endVertex();
        builder.vertex(mat, minX, maxY, maxZ).color(255, blue, green, red).uv(1, 0).endVertex();
        builder.vertex(mat, minX, minY, maxZ).color(255, blue, green, red).uv(0, 0).endVertex();

        builder.vertex(mat, maxX, minY, maxZ).color(255, blue, green, red).uv(1, 1).endVertex();
        builder.vertex(mat, maxX, maxY, maxZ).color(255, blue, green, red).uv(0, 1).endVertex();
        builder.vertex(mat, maxX, maxY, minZ).color(255, blue, green, red).uv(1, 0).endVertex();
        builder.vertex(mat, maxX, minY, minZ).color(255, blue, green, red).uv(0, 0).endVertex();

        builder.vertex(mat, minX, maxY, maxZ).color(255, blue, green, red).uv(0, 0).endVertex();
        builder.vertex(mat, minX, maxY, minZ).color(255, blue, green, red).uv(1, 0).endVertex();
        builder.vertex(mat, maxX, maxY, maxZ).color(255, blue, green, red).uv(1, 1).endVertex();
        builder.vertex(mat, maxX, maxY, minZ).color(255, blue, green, red).uv(0, 1).endVertex();

        builder.vertex(mat, minX, minY, maxZ).color(255, blue, green, red).uv(0, 0).endVertex();
        builder.vertex(mat, minX, minY, minZ).color(255, blue, green, red).uv(1, 0).endVertex();
        builder.vertex(mat, maxX, minY, maxZ).color(255, blue, green, red).uv(1, 1).endVertex();
        builder.vertex(mat, maxX, minY, minZ).color(255, blue, green, red).uv(0, 1).endVertex();

        matrixStack.popPose();

        buffer.endBatch(renderType);
    }

    @Mod.EventBusSubscriber(modid = "crysis", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEventSubscriber {
        protected static final RenderState.CullState NO_CULL = new RenderState.CullState(false);
        protected static final RenderState.DepthTestState NO_DEPTH_TEST = new RenderState.DepthTestState("always", 519);
        protected static final RenderState.AlphaState DEFAULT_ALPHA = new RenderState.AlphaState(0.003921569F);

        protected static final RenderState.TexturingState OUTLINE_TEXTURING = new RenderState.TexturingState("outline_texturing", () -> {
            RenderSystem.setupOutline();
        }, () -> {
            RenderSystem.teardownOutline();
        });

        protected static final RenderState.FogState NO_FOG = new RenderState.FogState("no_fog", () -> {
        }, () -> {
        });

        protected static final RenderState.TargetState OUTLINE_TARGET = new RenderState.TargetState("outline_target", () -> {
            Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false);
        }, () -> {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        });

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof PlayerEntity) {
                event.addCapability(new ResourceLocation(CrysisMod.MOD_ID, "diseases"), new NanosuitModeProvider());
            }
        }

        @SubscribeEvent
        public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {
            // Get the player's world and the overworld
            World world = Minecraft.getInstance().level;  // Adjust to the appropriate dimension
            World overworld = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);

            // Check if both worlds are not null
            if (world != null && overworld != null) {
                // Get a list of all players in the world
                List<? extends PlayerEntity> players = world.players();

                // Iterate over each player in the world
                for (PlayerEntity player : players) {
                    // Get nearby living entities within a certain range
                    List<? extends LivingEntity> nearbyEntities = overworld.getNearbyEntities(
                            LivingEntity.class,
                            new EntityPredicate(),
                            player,
                            player.getBoundingBox().inflate(35.0)
                    );

                    // Get the Nanosuit mode capability of the player
                    INanosuitModeCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);

                    // Iterate over each nearby living entity
                    for (LivingEntity livingEntity : nearbyEntities) {
                        // Check if the Nanosuit is in VISOR mode, the entity is not the player itself, and it's an AgeableEntity
                        if (nanosuitCapability.getMode() == NanosuitModes.VISOR
                                && !livingEntity.getStringUUID().equals(player.getStringUUID())
                                && livingEntity instanceof AgeableEntity) {

                            // Get the entity renderer and create a glowing render type
                            EntityRendererManager renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
                            RenderType glowing = RenderType.create("outline", DefaultVertexFormats.POSITION_COLOR, 7, 256,
                                    RenderType.State.builder()
                                            .setCullState(NO_CULL)
                                            .setDepthTestState(NO_DEPTH_TEST)
                                            .setAlphaState(DEFAULT_ALPHA)
                                            .setTexturingState(OUTLINE_TEXTURING)
                                            .setFogState(NO_FOG)
                                            .setOutputState(OUTLINE_TARGET)
                                            .createCompositeState(true));

                            EntityRenderer entityRenderer = renderManager.getRenderer(livingEntity);

                            // Check if the entity renderer is a LivingRenderer
                            if (entityRenderer instanceof LivingRenderer) {
                                LivingRenderer livingRenderer = (LivingRenderer) entityRenderer;
                                EntityModel entityModel = livingRenderer.getModel();

                                // Check if the entity model is an AgeableModel
                                if (entityModel instanceof AgeableModel) {
                                    AgeableModel ageableModel = (AgeableModel) entityModel;

                                    // Check if the entity model implements AgeableModelInterface
                                    if (ageableModel instanceof AgeableModelInterface) {
                                        AgeableModelInterface ageableModelInterface = (AgeableModelInterface) ageableModel;

                                        // Iterate over each head part and render the model box
                                        for (ModelRenderer headPart : ageableModelInterface.getHeadParts()) {
                                            renderModelBox(headPart, event.getMatrixStack(), glowing,
                                                    livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), true);
                                        }

                                        // Iterate over each body part and render the model box
                                        for (ModelRenderer bodyPart : ageableModelInterface.getBodyParts()) {
                                            renderModelBox(bodyPart, event.getMatrixStack(), glowing,
                                                    livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Method to render a model box
        public static void renderModelBox(ModelRenderer modelRenderer, MatrixStack matrixStack, RenderType renderType,
                                          double x, double y, double z, boolean first) {
            // Check if the model renderer implements ModelRendererInterface
            ModelRendererInterface modelRendererInterface = (ModelRendererInterface) modelRenderer;

            // Iterate over each model box and render a square
            for (ModelRenderer.ModelBox modelBox : modelRendererInterface.forge1_16_5Test$getCubes()) {
                CrysisMod.renderSquare(
                        matrixStack,
                        modelBox.maxX / 16, modelBox.maxY / 16, modelBox.maxZ / 16,
                        modelBox.minX / 16, modelBox.minY / 16, modelBox.minZ / 16,
                        200, 50, 50, renderType,
                        modelRenderer.xRot, modelRenderer.yRot, modelRenderer.zRot,
                        x + modelRenderer.x / 16, y + modelRenderer.y / 16, z + modelRenderer.z / 16,
                        first);  // <-- gets rendered upside down
            }

            // Iterate over each child model and recursively call renderModelBox
            for (ModelRenderer childModel : modelRendererInterface.forge1_16_5Test$getChildren()) {
                renderModelBox(childModel, matrixStack, renderType, x + modelRenderer.x / 16, y + modelRenderer.y / 16, z + modelRenderer.z / 16, false);
            }
        }
    }
}
