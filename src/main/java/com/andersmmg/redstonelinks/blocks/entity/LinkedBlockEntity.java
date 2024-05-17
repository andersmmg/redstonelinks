package com.andersmmg.redstonelinks.blocks.entity;

import com.andersmmg.redstonelinks.RedstoneLinks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkedBlockEntity extends BlockEntity {
    private List<BlockPos> linkedBlocks = new ArrayList<>();
    private final List<BooleanProperty> supportedProperties = new ArrayList<>(); // List of supported boolean properties
    private final List<String> propertyNames; // List of property names

    public LinkedBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LINKED_BLOCK_ENTITY, pos, state);
        this.propertyNames = List.of("powered", "open", "lit", "triggered");
        initSupportedProperties();
    }


    // Initialize supported properties based on provided property names
    private void initSupportedProperties() {
        for (String propertyName : propertyNames) {
            BooleanProperty property = BooleanProperty.of(propertyName);
            supportedProperties.add(property);
        }
    }

    public void updateLinkedBlocks(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }
        Iterator<BlockPos> iterator = linkedBlocks.iterator();
        while (iterator.hasNext()) {
            BlockPos linkedPos = iterator.next();
            BlockState linkedState = world.getBlockState(linkedPos);

            if (!canBePowered(linkedPos)) {
                iterator.remove();
                continue;
            }

            // Check if block is powered
            boolean powered = world.isReceivingRedstonePower(pos);

            // Check if block is a dispenser
            if (powered && linkedState.getBlock() instanceof DispenserBlock dispenserBlock) {
                DispenserBlockEntity dispenserEntity = (DispenserBlockEntity) world.getBlockEntity(linkedPos);
                if (dispenserEntity == null) {
                    continue;
                } else if (!dispenserEntity.getCachedState().get(DispenserBlock.TRIGGERED)) {
                    dispenserBlock.scheduledTick(linkedState, (ServerWorld) world, linkedPos, world.random);
                }
            }

            // Iterate over supported properties
            for (BooleanProperty property : supportedProperties) {
                if (linkedState.contains(property)) { // Check if property exists in the block state
                    BlockState updatedState = linkedState.with(property, powered);
                    world.setBlockState(linkedPos, updatedState, Block.NOTIFY_ALL);
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("linked_blocks", serializeLinkedBlocks(linkedBlocks));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("linked_blocks")) {
            NbtList list = nbt.getList("linked_blocks", 9); // Use 10 for NbtList type

            linkedBlocks = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                NbtList posTag = list.getList(i);
                int x = posTag.getInt(0);
                int y = posTag.getInt(1);
                int z = posTag.getInt(2);
                linkedBlocks.add(new BlockPos(x, y, z));
            }

            linkedBlocks.forEach(this::addLinkedBlock);
        }
        super.readNbt(nbt);
    }

    public NbtList serializeLinkedBlocks(List<BlockPos> linkedBlocks) {
        NbtList listTag = new NbtList();

        for (BlockPos pos : linkedBlocks) {
            NbtList posTag = new NbtList();
            posTag.add(NbtInt.of(pos.getX()));
            posTag.add(NbtInt.of(pos.getY()));
            posTag.add(NbtInt.of(pos.getZ()));
            listTag.add(posTag);
        }

        return listTag;
    }

    // Method to add a linked block position
    public boolean addLinkedBlock(BlockPos pos) {
        if (!linkedBlocks.contains(pos) && canBePowered(pos)) {
            linkedBlocks.add(pos);
            RedstoneLinks.LOGGER.info("Added block at position {} to linked blocks list.", pos);
            markDirty();
            return true;
        }
        return false;
    }

    // Method to remove a linked block position
    public void removeLinkedBlock(BlockPos pos) {
        linkedBlocks.remove(pos);
        RedstoneLinks.LOGGER.info("Removed block at position {} to linked blocks list.", pos);
        markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, LinkedBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        blockEntity.updateLinkedBlocks(world, pos);
    }

    private boolean canBePowered(BlockPos pos) {
        BlockState linkedState = world.getBlockState(pos);

        if (linkedState.isOf(Blocks.DISPENSER)) {
            return true;
        }
        for (BooleanProperty property : supportedProperties) {
            if (linkedState.contains(property)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isClient) {
            updateLinkedBlocks(world, pos);
        }
    }

    public List<BlockPos> getLinkedBlockPositions() {
        return linkedBlocks;
    }
}
