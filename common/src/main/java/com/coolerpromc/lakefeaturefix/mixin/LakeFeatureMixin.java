package com.coolerpromc.lakefeaturefix.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation")
@Mixin(LakeFeature.class)
public class LakeFeatureMixin {
    @Unique
    private static final ThreadLocal<BlockPos> ps$origin = ThreadLocal.withInitial(() -> BlockPos.ZERO);

    @Inject(method = "place", at = @At("HEAD"))
    private void captureOrigin(FeaturePlaceContext<LakeFeature.Configuration> context, CallbackInfoReturnable<Boolean> cir) {
        ps$origin.set(context.origin());
    }

    // LakeFeature.place() calls getBiome() on positions that may be outside the WorldGenRegion's valid chunk range, causing a crash. Fall back to the feature origin's biome (always within bounds) when that happens.
    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    private Holder<Biome> safeGetBiome(WorldGenLevel level, BlockPos pos) {
        try {
            return level.getBiome(pos);
        } catch (RuntimeException ignored) {
            return level.getBiome(ps$origin.get());
        }
    }
}