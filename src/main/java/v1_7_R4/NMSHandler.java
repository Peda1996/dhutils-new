package v1_7_R4;

import me.desht.dhutils.nms.api.NMSAbstraction;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;

public class NMSHandler implements NMSAbstraction {

	@Override
    public boolean setBlockFast(World world, int x, int y, int z, int blockId, byte data) {
        net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        int combined = blockId + (data << 12);
        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
        return chunk.a(bp, ibd).getBlock().g();
    }


	@Override
	public void forceBlockLightLevel(World world, int x, int y, int z, int level) {
		//net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
		//w.b(EnumSkyBlock.BLOCK, x, y, z, level);
	}

	@Override
	public int getBlockLightEmission(int blockId) {
	    return Block.getById(blockId).k();
		//return Block.getById(blockId).m();
	}

	@Override
	public int getBlockLightBlocking(int blockId) {
		return Block.getById(blockId).k();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void queueChunkForUpdate(Player player, int cx, int cz) {
		((CraftPlayer) player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
	}

	@Override
        public Vector[] getBlockHitbox(org.bukkit.block.Block block) {
		net.minecraft.server.v1_8_R3.World w = ((CraftWorld)block.getWorld()).getHandle();
        BlockPosition pos = new BlockPosition(block.getX(),block.getY(),block.getZ());
		Block b = (Block) w.getType(pos).getBlock();
        b.updateState(b.getBlockData(),w, pos);

        return new Vector[] {
				new Vector(block.getX(), block.getY(), block.getZ() + b.B()),
				new Vector(block.getX() , block.getY() , block.getZ() + b.C())
		};
	}

    @Override
    public void recalculateBlockLighting(World world, int x, int y, int z) {
        // Don't consider blocks that are completely surrounded by other non-transparent blocks
       /* if (!canAffectLighting(world, x, y, z)) {
            return;
        }

        int i = x & 0x0F;
        int j = y & 0xFF;
        int k = z & 0x0F;
        CraftChunk craftChunk = (CraftChunk)world.getChunkAt(x >> 4, z >> 4);
        Chunk nmsChunk = craftChunk.getHandle();

        int i1 = k << 4 | i;
        int maxY = nmsChunk.heightMap[i1];

        Block block = nmsChunk.getType(i, j, k);
        int j2 = block.k();

        if (j2 > 0) {
            if (j >= maxY) {
                invokeNmsH(nmsChunk, i, j + 1, k);
            }
        } else if (j == maxY - 1) {
            invokeNmsH(nmsChunk,i, j, k);
        }

        if (nmsChunk.getBrightness(EnumSkyBlock.SKY, i, j, k) > 0 || nmsChunk.getBrightness(EnumSkyBlock.BLOCK, i, j, k) > 0) {
            invokeNmsE(nmsChunk, i, k);
        }

        net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();

        w.c(EnumSkyBlock.BLOCK, i, j, k);*/
    }

    private Method h;
    private void invokeNmsH(Chunk nmsChunk, int i, int j, int k) {
        try {
            if (h == null) {
                Class[] classes = {int.class, int.class, int.class};
                h = Chunk.class.getDeclaredMethod("h", classes);
                h.setAccessible(true);
            }
            h.invoke(nmsChunk, i, j, k);
        } catch (Exception e) {
            System.out.println("Reflection exception: " + e);
        }
    }

    private Method e;
    private void invokeNmsE(Chunk nmsChunk, int i, int j) {
        try {
            if (e == null) {
                Class[] classes = {int.class, int.class};
                e = Chunk.class.getDeclaredMethod("e", classes);
                e.setAccessible(true);
            }
            e.invoke(nmsChunk, i, j);
        } catch (Exception e) {
            System.out.println("Reflection exception: " + e);
        }
    }

    private boolean canAffectLighting(World world, int x, int y, int z) {
        org.bukkit.block.Block base  = world.getBlockAt(x, y, z);
        org.bukkit.block.Block east  = base.getRelative(BlockFace.EAST);
        org.bukkit.block.Block west  = base.getRelative(BlockFace.WEST);
        org.bukkit.block.Block up    = base.getRelative(BlockFace.UP);
        org.bukkit.block.Block down  = base.getRelative(BlockFace.DOWN);
        org.bukkit.block.Block south = base.getRelative(BlockFace.SOUTH);
        org.bukkit.block.Block north = base.getRelative(BlockFace.NORTH);

        return east.getType().isTransparent() ||
                west.getType().isTransparent() ||
                up.getType().isTransparent() ||
                down.getType().isTransparent() ||
                south.getType().isTransparent() ||
                north.getType().isTransparent();
    }
}
