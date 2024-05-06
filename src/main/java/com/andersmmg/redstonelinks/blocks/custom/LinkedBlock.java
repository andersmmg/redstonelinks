package com.andersmmg.redstonelinks.blocks.custom;

import com.andersmmg.redstonelinks.blocks.entity.LinkedBlockEntity;
import com.andersmmg.redstonelinks.blocks.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkedBlock extends Block implements BlockEntityProvider {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public LinkedBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LinkedBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (!player.isSneaking()) {
            return ActionResult.PASS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LinkedBlockEntity) {
            List<String> linkedBlockNames = getLinkedBlocksNames(world, pos);
            String blockNames = String.join(", ", linkedBlockNames);
            sendChatMessage(player, Text.translatable("block.redstonelinks.linked_blocks_list", blockNames));
        }
        return ActionResult.CONSUME;
    }

    private List<String> getLinkedBlocksNames(World world, BlockPos pos) {
        LinkedBlockEntity linkedBlockEntity = (LinkedBlockEntity) world.getBlockEntity(pos);
        if (linkedBlockEntity == null) {
            return new ArrayList<>();
        }
        List<BlockPos> linkedBlockPositions = linkedBlockEntity.getLinkedBlockPositions();
        Map<Block, Integer> linkedBlockCounts = new HashMap<>();
        for (BlockPos linkedBlockPos : linkedBlockPositions) {
            Block block = world.getBlockState(linkedBlockPos).getBlock();
            linkedBlockCounts.put(block, linkedBlockCounts.getOrDefault(block, 0) + 1);
        }
        List<String> linkedBlockNames = new ArrayList<>();
        for (Map.Entry<Block, Integer> entry : linkedBlockCounts.entrySet()) {
            Block block = entry.getKey();
            int count = entry.getValue();
            linkedBlockNames.add(block.getName().getString() + " (x" + count + ")");
        }
        return linkedBlockNames;
    }

    private void updateLinkedBlocks(World world, BlockPos pos) {
        LinkedBlockEntity linkedBlockEntity = (LinkedBlockEntity) world.getBlockEntity(pos);
        if (linkedBlockEntity == null) {
            return;
        }
        linkedBlockEntity.updateLinkedBlocks(world, pos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        updateLinkedBlocks(world, pos);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.LINKED_BLOCK_ENTITY, LinkedBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            if (world.isReceivingRedstonePower(pos)) {
                BlockState blockState = world.getBlockState(pos);
                if (blockState.contains(POWERED)) {
                    boolean powered = blockState.get(POWERED);
                    if (!powered) {
                        world.setBlockState(pos, blockState.with(POWERED, true), Block.NOTIFY_ALL);
                    }
                }
            } else {
                BlockState blockState = world.getBlockState(pos);
                if (blockState.contains(POWERED)) {
                    boolean powered = blockState.get(POWERED);
                    if (powered) {
                        world.setBlockState(pos, blockState.with(POWERED, false), Block.NOTIFY_ALL);
                    }
                }
            }
            updateLinkedBlocks(world, pos);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (state.contains(POWERED)) {
                BlockState blockState = world.getBlockState(pos);
                if (blockState.contains(POWERED)) {
                    boolean powered = blockState.get(POWERED);
                    if (powered) {
                        world.setBlockState(pos, blockState.with(POWERED, false), Block.NOTIFY_ALL);
                    }
                }
            }
        }
        updateLinkedBlocks(world, pos);
    }

    private static void sendChatMessage(PlayerEntity player, Text message) {
        ((ServerPlayerEntity)player).sendMessageToClient(message, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
}
