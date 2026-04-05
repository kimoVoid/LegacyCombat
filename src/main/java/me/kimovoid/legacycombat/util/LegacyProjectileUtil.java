package me.kimovoid.legacycombat.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LegacyProjectileUtil {

    /**
     * This would make sense as a class that extends AABB
     * but this works just fine so it is what it is :)
     */
    public static final LegacyProjectileUtil INSTANCE = new LegacyProjectileUtil();

    public Optional<Vec3> clip(AABB bb, Vec3 from, Vec3 to) {
        Vec3 min = this.clipX(bb, bb.minX, from, to);
        Vec3 max = this.clipX(bb, bb.maxX, from, to);

        if (max != null && this.isCloser(from, min, max)) {
            min = max;
        }

        max = this.clipY(bb, bb.minY, from, to);
        if (max != null && this.isCloser(from, min, max)) {
            min = max;
        }

        max = this.clipY(bb, bb.maxY, from, to);
        if (max != null && this.isCloser(from, min, max)) {
            min = max;
        }

        max = this.clipZ(bb, bb.minZ, from, to);
        if (max != null && this.isCloser(from, min, max)) {
            min = max;
        }

        max = this.clipZ(bb, bb.maxZ, from, to);
        if (max != null && this.isCloser(from, min, max)) {
            min = max;
        }

        return min == null ? Optional.empty() : Optional.of(min);
    }

    boolean isCloser(Vec3 from, @Nullable Vec3 newTo, Vec3 to) {
        return newTo == null || from.distanceToSqr(to) < from.distanceToSqr(newTo);
    }

    public Vec3 clipX(AABB bb, double x, Vec3 from, Vec3 to) {
        Vec3 vec = this.intermediateWithX(from, to, x);
        return vec != null && this.containsYZ(bb, vec) ? vec : null;
    }

    public Vec3 clipY(AABB bb, double y, Vec3 from, Vec3 to) {
        Vec3 vec = this.intermediateWithY(from, to, y);
        return vec != null && this.containsXZ(bb, vec) ? vec : null;
    }

    public Vec3 clipZ(AABB bb, double z, Vec3 from, Vec3 to) {
        Vec3 vec = this.intermediateWithZ(from, to, z);
        return vec != null && this.containsXY(bb, vec) ? vec : null;
    }

    public boolean containsYZ(AABB bb, Vec3 vec) {
        return vec.y >= bb.minY && vec.y <= bb.maxY && vec.z >= bb.minZ && vec.z <= bb.maxZ;
    }

    public boolean containsXZ(AABB bb, Vec3 vec) {
        return vec.x >= bb.minX && vec.x <= bb.maxX && vec.z >= bb.minZ && vec.z <= bb.maxZ;
    }

    public boolean containsXY(AABB bb, Vec3 vec) {
        return vec.x >= bb.minX && vec.x <= bb.maxX && vec.y >= bb.minY && vec.y <= bb.maxY;
    }

    @Nullable
    public Vec3 intermediateWithX(Vec3 from, Vec3 vec, double x) {
        double e = vec.x - from.x;
        double f = vec.y - from.y;
        double g = vec.z - from.z;
        if (e * e < 1.0E-7F) {
            return null;
        } else {
            double h = (x - from.x) / e;
            return !(h < 0.0) && !(h > 1.0) ? new Vec3(from.x + e * h, from.y + f * h, from.z + g * h) : null;
        }
    }

    @Nullable
    public Vec3 intermediateWithY(Vec3 from, Vec3 vec, double y) {
        double e = vec.x - from.x;
        double f = vec.y - from.y;
        double g = vec.z - from.z;
        if (f * f < 1.0E-7F) {
            return null;
        } else {
            double h = (y - from.y) / f;
            return !(h < 0.0) && !(h > 1.0) ? new Vec3(from.x + e * h, from.y + f * h, from.z + g * h) : null;
        }
    }

    @Nullable
    public Vec3 intermediateWithZ(Vec3 from, Vec3 vec, double z) {
        double e = vec.x - from.x;
        double f = vec.y - from.y;
        double g = vec.z - from.z;
        if (g * g < 1.0E-7F) {
            return null;
        } else {
            double h = (z - from.z) / g;
            return !(h < 0.0) && !(h > 1.0) ? new Vec3(from.x + e * h, from.y + f * h, from.z + g * h) : null;
        }
    }
}