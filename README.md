# IndieVLC — Android starter project

This is an early, unverified starter scaffold for the IndieVLC concept. It is not a finished player and the APK has not yet been built or device-tested.

## Current starter features
- Kotlin Android app shell with an AMOLED-black theme
- Android document picker for selecting local media
- LibVLC playback engine dependency
- GitHub Actions workflow to build a debug APK in the cloud

## Build in GitHub
1. Create a new public or private repository named `IndieVLC` (no README, license, or .gitignore needed).
2. Upload the contents of this folder to the repository root (not the containing folder itself).
3. Open the repository's **Actions** tab and enable workflows if prompted.
4. Run **Build IndieVLC APK** using **Run workflow**.
5. When complete, download the `IndieVLC-debug-apk` artifact from that workflow run.

## Important
- This scaffold is an initial engineering starting point, not a guarantee of playback for every codec or device.
- The first cloud build may reveal dependency/API issues that need fixing.
- The advanced library, Liquid Glass controls, layout editor, subtitle editor, gestures, and tablet-specific layouts are not implemented yet.
- The project name is provisional; check trademarks and avoid implying official affiliation with VideoLAN before public release.
