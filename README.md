# Laggy Armor Stands

A Fabric performance mod for **Minecraft Java 1.21.11**. Version **1.0.1** fixes a startup crash caused by using a Minecraft 1.21.1 mixin target in a 1.21.11 installation.

## Optimizations

- **Client render culling:** prevents armor stands from entering the entity render pipeline. Armor stands remain in the world, targetable, and retain their data.
- **Server idle-tick reduction:** eligible stationary armor stands execute their entity tick once every `skipInterval` world ticks. The default interval of `20` reduces eligible idle-stand tick executions by approximately 95%.
- **Conservative safety guards:** moving, burning, mounted, passenger-carrying, hurt, airborne (unless NoGravity is enabled), and client-side stands are not skipped.

Tick reduction is inherently invasive to game logic: datapacks, command contraptions, and server plugins that depend on armor stands updating every tick may behave differently. Test it in the target world before using it permanently.

## Installation

1. Use Fabric Loader and Fabric API for **Minecraft 1.21.11**.
2. Put `laggyarmorstands-1.0.1.jar` in your client's `mods` folder to hide armor stands.
3. To reduce server entity ticking, install the mod on the server too. Singleplayer runs an integrated server, so a client installation handles both sides.
4. Start once, then edit `.minecraft/config/laggyarmorstands.json` if needed. Restart after changing settings.

## Configuration

The first launch creates `config/laggyarmorstands.json`.

| Option | Default | Purpose |
|---|---:|---|
| `enabled` | `true` | Master switch |
| `hideArmorStands` | `true` | Hide armor stands in client rendering |
| `optimizeStationaryTicks` | `true` | Reduce server tick work for idle stands |
| `skipInterval` | `20` | Tick eligible stands once every N world ticks (clamped to 2–100) |
| `skipWhenOnGround` | `true` | Allow skipping when standing on ground |
| `skipWhenNoGravity` | `true` | Allow skipping if NoGravity is enabled |
| `dontSkipIfRiding` | `true` | Keep ticking while riding another entity |
| `dontSkipIfHasPassengers` | `true` | Keep ticking while other entities ride the stand |
| `dontSkipIfOnFire` | `true` | Keep ticking while burning |
| `dontSkipIfMoving` | `true` | Keep ticking when velocity is non-zero |
| `dontSkipIfHurt` | `true` | Keep ticking during the normal hurt timer |

## Build

Requires JDK 21 and Gradle 9.2.1. Run `gradle build`; the JAR is generated in `build/libs/`. GitHub Actions also compiles the mod, validates its metadata and required Mixin classes, and uploads the JAR as a workflow artifact.

## Notes

- Rendering is client-side; a server does not need the mod just to hide stands for one player.
- Tick optimization is server-side; installing only on a multiplayer client cannot change the server's TPS.
- Hiding is visual only; it does not remove armor stands, disable hitboxes, or prevent interaction packets.
- This release targets Minecraft 1.21.11 specifically; the 1.21.1 JAR is incompatible.
