package com.andersmmg.redstonelinks.items.custom;

import com.andersmmg.redstonelinks.blocks.custom.LinkedBlock;
import com.andersmmg.redstonelinks.blocks.entity.LinkedBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LinkerItem extends Item {
    public LinkerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient()) {
            return ActionResult.SUCCESS;
        }
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        if (world != null && pos != null && player != null) {
            BlockState state = world.getBlockState(pos);
            if (!context.getPlayer().isSneaking() && state.getBlock() instanceof LinkedBlock linkedBlock) {
                stack.getOrCreateNbt().putLong("linkedBlockPos", pos.asLong());
                sendMessage(player, Text.translatable("item.redstonelinks.linked_block_set", pos.getX(), pos.getY(), pos.getZ()));
                world.setBlockState(pos, state.with(LinkedBlock.ENABLED, true), Block.NOTIFY_ALL);
                return ActionResult.SUCCESS;
            } else {
                BlockPos linkedBlockPos = BlockPos.fromLong(stack.getOrCreateNbt().getLong("linkedBlockPos"));
                BlockState linkedBlockState = world.getBlockState(linkedBlockPos);
                if (linkedBlockState.getBlock() instanceof LinkedBlock) {
                    LinkedBlockEntity linkedBlockEntity = (LinkedBlockEntity) world.getBlockEntity(linkedBlockPos);
                    if (linkedBlockEntity != null) {
                        if (linkedBlockEntity.addLinkedBlock(pos)) {
                            sendMessage(player, Text.translatable("item.redstonelinks.linked_block_added", pos.getX(), pos.getY(), pos.getZ()));
                        }
                        return ActionResult.SUCCESS;
                    } else {
                        sendMessage(player, Text.translatable("item.redstonelinks.no_linked_block"));
                    }
                } else {
                    sendMessage(player, Text.translatable("item.redstonelinks.no_linked_block"));
                }
            }
        }
        return ActionResult.FAIL;
    }

    private static void sendMessage(PlayerEntity player, Text message) {
        ((ServerPlayerEntity)player).sendMessageToClient(message, true);
    }
}
