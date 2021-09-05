package textile.utils;

import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import textile.data.BlockUpdateContainer;

import java.util.Map;
import java.util.function.Function;

public class RaycastUtil {
	public static BlockPos tryRaycast(Vector3d cam, Vector3d ray, double range, Function<BlockPos, Boolean> callback) {
		double ray_len = Math.sqrt(ray.x * ray.x + ray.y * ray.y + ray.z * ray.z);
		
		// Delta xyz length
		double dxl = ray_len / Math.abs(ray.x);
		double dyl = ray_len / Math.abs(ray.y);
		double dzl = ray_len / Math.abs(ray.z);
		
		// Delta xyz start
		double dxs = 0;
		double dys = 0;
		double dzs = 0;
		
		{
			double ttx = ((cam.x % 1.0D) + 1.0D) % 1.0D;
			double tty = ((cam.y % 1.0D) + 1.0D) % 1.0D;
			double ttz = ((cam.z % 1.0D) + 1.0D) % 1.0D;
			
			if(ray.x > 0) ttx = 1 - ttx;
			if(ray.y > 0) tty = 1 - tty;
			if(ray.z > 0) ttz = 1 - ttz;
			
			dxs = dxl * ttx;
			dys = dyl * tty;
			dzs = dzl * ttz;
		}
		
		// Directions
		int xd = ray.x < 0 ? -1:1;
		int yd = ray.y < 0 ? -1:1;
		int zd = ray.z < 0 ? -1:1;
		
		// Block start
		int bx = (int)cam.x - ((cam.x < 0) ? 1:0);
		int by = (int)cam.y - ((cam.y < 0) ? 1:0);
		int bz = (int)cam.z - ((cam.z < 0) ? 1:0);
		
		// Length values
		double xp = dxs;
		double yp = dys;
		double zp = dzs;
		
		for(int i = 0; i < range; i++) {
			if(xp < yp) {
				if(xp > zp) {
					bz += zd;
					zp += dzl;
				} else {
					bx += xd;
					xp += dxl;
				}
			} else {
				if(yp > zp) {
					bz += zd;
					zp += dzl;
				} else {
					by += yd;
					yp += dyl;
				}
			}
			
			BlockPos pos = new BlockPos(bx, by, bz);
			if(callback.apply(pos)) {
				return pos.toImmutable();
			}
		}
		
		return null;
	}
	
	
	private static Vec3d getClosestPointOnLine(Vec3d point, Vec3d X1, Vec3d X2) {
		Vec3d X0 = point;
		
		Vec3d X1X0 = X1.subtract(X0);
		Vec3d X2X1 = X2.subtract(X1);
		
		double t = (X1X0.dotProduct(X2X1) / X2X1.lengthSquared());
		return X1.subtract(X2X1.multiply(t));
	}
	
	public static BlockPos raycastSizedBox(Vector3d cam, Vector3d ray, double size, double range, Function<BlockPos, Boolean> callback) {
		double ray_len = Math.sqrt(ray.x * ray.x + ray.y * ray.y + ray.z * ray.z);
		
		// Delta xyz length
		double dxl = ray_len / Math.abs(ray.x);
		double dyl = ray_len / Math.abs(ray.y);
		double dzl = ray_len / Math.abs(ray.z);
		
		// Delta xyz start
		double dxs = 0;
		double dys = 0;
		double dzs = 0;
		
		{
			double ttx = ((cam.x % 1.0D) + 1.0D) % 1.0D;
			double tty = ((cam.y % 1.0D) + 1.0D) % 1.0D;
			double ttz = ((cam.z % 1.0D) + 1.0D) % 1.0D;
			
			if(ray.x > 0) ttx = 1 - ttx;
			if(ray.y > 0) tty = 1 - tty;
			if(ray.z > 0) ttz = 1 - ttz;
			
			dxs = dxl * ttx;
			dys = dyl * tty;
			dzs = dzl * ttz;
		}
		
		// Directions
		int xd = ray.x < 0 ? -1:1;
		int yd = ray.y < 0 ? -1:1;
		int zd = ray.z < 0 ? -1:1;
		
		// Block start
		int bx = (int)cam.x - ((cam.x < 0) ? 1:0);
		int by = (int)cam.y - ((cam.y < 0) ? 1:0);
		int bz = (int)cam.z - ((cam.z < 0) ? 1:0);
		
		// Length values
		double xp = dxs;
		double yp = dys;
		double zp = dzs;
		
		for(int i = 0; i < range; i++) {
			if(xp < yp) {
				if(xp > zp) {
					bz += zd;
					zp += dzl;
				} else {
					bx += xd;
					xp += dxl;
				}
			} else {
				if(yp > zp) {
					bz += zd;
					zp += dzl;
				} else {
					by += yd;
					yp += dyl;
				}
			}
			
			BlockPos pos = new BlockPos(bx, by, bz);
			
			Vec3d block_center = new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
			Vec3d camera_origin = new Vec3d(cam.x, cam.y, cam.z);
			Vec3d camera_direct = new Vec3d(ray.x, ray.y, ray.z).add(camera_origin);
			
			Vec3d closest = getClosestPointOnLine(block_center, camera_origin, camera_direct).subtract(block_center);
			
			if(closest.x * closest.x > size * size
			|| closest.y * closest.y > size * size
			|| closest.z * closest.z > size * size) {
				continue;
			}
			
			if(callback.apply(pos)) {
				return pos.toImmutable();
			}
		}
		
		return null;
	}
	
	/*
	public static UpdateNode raycastNodes(Camera camera, Set<UpdateNode> nodes, float distance) {
		Vec3d pos = camera.getPos();
		Vec3d dir;
		{
			Vec3f tmp = new Vec3f(0, 0, 1);
			tmp.rotate(camera.getRotation());
			dir = new Vec3d(tmp);
		}
		
		BlockPos raycast = raycastSizedBox(
			new Vector3d(pos.x, pos.y, pos.z),
			new Vector3d(dir.x, dir.y, dir.z),
			0.75 / 2.0,
			distance,
			i -> nodes.contains(NodePos.wrap(i))
		);
		
		if(raycast != null) {
			return TextileUpdates.getNode(NodePos.wrap(raycast));
		}
		
		return null;
	}
	*/
	
	public static BlockUpdateContainer raycastContainers(Camera camera, Map<BlockPos, BlockUpdateContainer> map, float distance) {
		Vec3d pos = camera.getPos();
		Vec3d dir;
		{
			Vec3f tmp = new Vec3f(0, 0, 1);
			tmp.rotate(camera.getRotation());
			dir = new Vec3d(tmp);
		}
		
		BlockPos raycast = raycastSizedBox(
			new Vector3d(pos.x, pos.y, pos.z),
			new Vector3d(dir.x, dir.y, dir.z),
			0.75 / 2.0,
			distance,
			map::containsKey
		);
		
		return raycast != null ? map.get(raycast):null;
	}
}
