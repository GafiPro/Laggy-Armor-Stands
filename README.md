# Laggy Armor Stands

A small Fabric performance mod for **Minecraft Java 1.21.1**. The mod has two independent optimizations:

- **Client render culling:** hides armor stands before their renderer, name label, and shadow are processed. The stands still exist, remain targetable, and keep their server-side data. This option is enabled by default.
- **Server idle-tick reduction:** only ticks stationary armor stands once every `skipInterval` world ticks. With the default interval of `20`, eligible stands skip roughly 95% of their normal entity ticks. Moving or otherwise active stands continue ticking normally.

The tick optimization is deliberately conservative: it does not skip client-side ticks and refuses to skip stands that are moving, burning, mounted, carrying passengers, or recently hit. As with any skipped-tick optimization, unusual datapacks or command-block contraptions that rely on per-tick armor-stand behavior should be tested with it enabled.

## Installation

1. Install Fabric Loader for Minecraft 1.21.1 and Fabric API.
2. Put `laggyarmorstands-1.0.0.jar` in the `mods` folder on the client to hide armor stands.
3. To reduce server entity ticking, install the same mod on the server too. In singleplayer, one installation covers both sides.
4. Start the game once, then edit `.minecraft/config/laggyarmorstands.json` to change options. Restart the game/server after editing.

## Configuration

The first launch creates `config/laggyarmorstands.json`. Defaults:

| Option | Default | Purpose |
|---|---:|---|
| `enabled` | `true` | Master switch |
| `hideArmorStands` | `true` | Do not render armor stands on clients |
| `optimizeStationaryTicks` | `true` | Reduce server tick work for idle stands |
| `skipInterval` | `20` | Tick eligible stands once every N ticks (clamped to 2–100) |
| `skipWhenOnGround` | `true` | Allow skipping when standing on solid ground |
| `skipWhenNoGravity` | `true` | Allow skipping when `NoGravity` is enabled |
| `dontSkipIfRiding` | `true` | Keep ticking when the stand rides another entity |
| `dontSkipIfHasPassengers` | `true` | Keep ticking when another entity rides the stand |
| `dontSkipIfOnFire` | `true` | Keep ticking while burning |
| `dontSkipIfMoving` | `true` | Keep ticking when velocity is non-zero |
| `dontSkipIfRecentlyHit` | `true` | Keep ticking briefly after interaction/damage |

## Build from source

Requires JDK 21. Run `gradle build`; the mod JAR is generated in `build/libs/`. GitHub Actions also builds the project on every push and pull request, then stores the JAR as a workflow artifact.

## Notes

- Rendering is client-side; the server does not need the mod for the visual hiding feature.
- Server tick reduction is server-side; installing only on the client cannot change server TPS.
- Hiding is visual only: it does not remove armor stands, disable hitboxes, or prevent interaction packets.
- The project targets Fabric and Minecraft 1.21.1. Other Minecraft versions need their own compile and runtime checks.
