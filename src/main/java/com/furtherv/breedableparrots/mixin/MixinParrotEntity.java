package com.furtherv.breedableparrots.mixin;

import com.furtherv.breedableparrots.block.ModBlocks;
import com.furtherv.breedableparrots.item.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(Parrot.class)
public abstract class MixinParrotEntity extends Animal {

    protected MixinParrotEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At("HEAD"), method = "registerGoals", cancellable = false)
    private void registerGoals(CallbackInfo callback) {
        Parrot thisParrot = (Parrot) (Object) this;
        thisParrot.goalSelector.addGoal(0, new BreedGoal(thisParrot, (double) 1.0F));
    }

    @Inject(at = @At("HEAD"), method = "isFood", cancellable = true)
    private void isFood(ItemStack itemStack, CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(itemStack.is(ModItems.PARROT_FOOD.get()));
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel pLevel, Animal pMate) {
        Parrot thisParrot = (Parrot) (Object) this;
        ItemEntity eggItem = EntityType.ITEM.create(pLevel);
        if(eggItem != null) {
            eggItem.setItem(new ItemStack(ModBlocks.PARROT_EGG_BLOCK.get()));
            eggItem.moveTo(thisParrot.getX(), thisParrot.getY(), thisParrot.getZ());
            pLevel.addFreshEntityWithPassengers(eggItem);
            this.finalizeSpawnChildFromBreeding(pLevel, pMate, null);
        }
    }

    @Override
    public void finalizeSpawnChildFromBreeding(ServerLevel pLevel, Animal pAnimal, @Nullable AgeableMob pBaby) {
        Parrot thisParrot = (Parrot) (Object) this;
        thisParrot.setAge(6000);
        pAnimal.setAge(6000);
        thisParrot.resetLove();
        pAnimal.resetLove();
        pLevel.broadcastEntityEvent(thisParrot, (byte)18);
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            pLevel.addFreshEntity(new ExperienceOrb(pLevel, thisParrot.getX(), thisParrot.getY(), thisParrot.getZ(), thisParrot.getRandom().nextInt(7) + 1));
        }

    }

//    @Inject(at = @At("HEAD"), method = "getBreedOffspring", cancellable = true)
//    private void getBreedOffspring(ServerLevel level, AgeableMob otherParentMob, CallbackInfoReturnable<AgeableMob> callback) {
//        if(!(otherParentMob instanceof Parrot otherParent)) {
//            callback.setReturnValue(null);
//            return;
//        }
//
//        ItemEntity eggItem = EntityType.ITEM.create(level);
//        if(eggItem != null) {
//            eggItem.setItem(new ItemStack(ModBlocks.PARROT_EGG_BLOCK.get()));
//            eggItem.moveTo(otherParentMob.position().add(0, 0.25, 0));
//            level.addFreshEntity(eggItem);
//        }
//
//        callback.setReturnValue(null);
//
////        Parrot thisParent = (Parrot) (Object) this;
////
////        Parrot baby = EntityType.PARROT.create(level);
////        if (baby != null) {
////            boolean useThisParentVariant = level.random.nextBoolean();
////            baby.setVariant(useThisParentVariant ? thisParent.getVariant() : otherParent.getVariant());
////        }
////        callback.setReturnValue(baby);
//    }

    @Inject(at = @At("HEAD"), method = "canMate", cancellable = true)
    private void canMate(Animal other, CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(super.canMate(other)); // Allow parrots to mate with other parrots
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    public void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
        Parrot thisParrot = (Parrot) (Object) this;
        ItemStack handItem = player.getItemInHand(hand);
        if (thisParrot.isFood(handItem)) {
            callback.setReturnValue(super.mobInteract(player, hand));
        }
    }
}
