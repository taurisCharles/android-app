# Snakerito Modernization Roadmap

Goal: turn the working prototype into a polished, playful Android game with stronger identity, better feedback, and a more satisfying gameplay loop.

## Product Direction

Snakerito should feel like a quick, charming arcade snack: easy to start, funny enough to show someone, and polished enough to keep on a phone.

Core vibe:

- Warm taqueria counter, not generic snake board.
- Burrito/salsa identity everywhere: visuals, scoring, copy, sounds.
- Fast rounds with immediate restart.
- Simple controls that feel responsive on real phones.

## Phase 1: Visual Polish

Upgrade the current Compose canvas from basic shapes to a cohesive game scene.

Tasks:

- Add a richer board background with subtle tile/table texture.
- Improve burrito segments with direction-aware head art.
- Add toppings/fillings details: rice, beans, cilantro, salsa.
- Add food variants: salsa, guac, queso, hot sauce.
- Add score/header styling with a compact arcade HUD.
- Add start and game-over overlays that feel designed, not default.
- Add light animation: pulsing food, snake movement easing, game-over shake.
- Add haptic feedback on eat and crash.

## Phase 2: Gameplay Feel

Make the game more rewarding and less flat.

Tasks:

- Add increasing speed as score rises.
- Add best score stored locally.
- Add pause/resume.
- Add countdown before start/restart.
- Add edge warning or subtle board boundary glow.
- Add combo scoring for quick consecutive salsa grabs.
- Add difficulty modes: Mild, Medium, Spicy.

## Phase 3: Audio And Feedback

Add delight without becoming annoying.

Tasks:

- Add short sound effects for eat, turn, crash, restart.
- Add mute toggle.
- Add haptic patterns for direction, salsa, and crash.
- Add celebratory feedback at score milestones.

## Phase 4: App Identity

Make it feel like a real installed app.

Tasks:

- Refine launcher icon into a cleaner burrito mark.
- Add splash screen branding.
- Rename package/application namespace away from `com.example`.
- Add app versioning.
- Add screenshots for sharing/store listing.

## Phase 5: Polish For Release

Prepare for distribution or sharing.

Tasks:

- Add release build signing notes.
- Add privacy-friendly no-data policy note.
- Add Play Store style description and screenshots.
- Add basic smoke-test checklist for phone installs.
- Consider simple analytics only if needed later.

## First Sprint

Recommended first sprint: make the game feel dramatically better without changing its architecture.

Scope:

1. Redesign the game screen HUD and overlays.
2. Add best score persistence.
3. Add speed ramping.
4. Add haptic feedback.
5. Add a few food variants with distinct colors.
6. Improve the burrito rendering with direction-aware head details.

Success criteria:

- App still builds with `gradlew.bat assembleDebug`.
- App installs and launches on the connected Samsung phone.
- The first screen looks intentional.
- Eating salsa feels rewarding.
- Restarting after game over is obvious and instant.

## Nice-To-Have Ideas

- Character name: Snakerito.
- Food names in HUD: Salsa, Guac, Queso, Pico.
- Score copy: `Salsa: 12`, `Best: 24`, `Heat: Spicy`.
- Crash copy: `Tortilla Trouble`.
- Milestone copy: `Extra Guac!`, `Combo Plate!`, `Too Hot!`

## Technical Notes

- Keep using Jetpack Compose and Canvas for now.
- Avoid adding a game engine unless the interaction complexity grows.
- Keep state in `GameViewModel`.
- Add small model types for food variants and difficulty.
- Prefer tiny, focused changes that keep phone installs easy.
