package com.furtherv.breedableparrots.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class MixinItemEntity {

    //@Inject(at = @At("HEAD"), method = "fireImmune", cancellable = true)
    private void fireImmune(CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(true);
    }
}
