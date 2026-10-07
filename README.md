# ssMath

A native Kotlin / Jetpack Compose Android app for practicing math. Android 8.0
(API 26) or newer; targets Android 16 (API 36).

## How it works

1. **Setup dialog** – choose what to practice (**Addition**, **Subtraction**,
   **Multiplication** or **Division**), enter the **Minimum number** (defaults to 1)
   above the **Maximum number** (both 1 to 10,000; minimum cannot exceed maximum),
   and answer **How many questions would you like?** using the **Number of questions**
   field (1 to 1,000; defaults to 10), then press **Submit**.
   Your choices are remembered for next time.
2. **Press Start when Ready** – press **Start** to begin. The timer starts now.
3. **Practice** – every problem uses two random numbers between the minimum
   and maximum, inclusive. Subtraction never goes below zero and division always has a
   whole-number answer. A correct answer earns a point; a wrong answer is added to
   the **Wrong** tally in the bottom-left corner. Either way, a new problem of the
   same type follows, with your question progress shown above it.
4. **Results** – after the chosen number of questions (correct or wrong), or
   5 wrong answers, whichever comes first, the practice stops and shows how many you
   got right, plus every answered problem with a green check (correct) or a red X
   (wrong, with the correct answer). Results are saved with the date and time.
   If all the chosen questions were answered, even with mistakes, one of four randomly chosen
   congratulations animations plays: dolphins, whales, or anchovies jumping out
   of the water saying **Hurray!!**, or a balloon-and-confetti party. These
   animations are drawn in the app and work offline. Press **View results** to
   skip the animation, or wait for it to finish.
   If 5 wrong answers end practice before the last question, a dialog says
   **Nice try! You got {XX} right out of {TOTAL}**, using the chosen number of
   questions as the total. Press **View results** to dismiss it.
   Press **Done** to return to the setup dialog.

The semi-transparent (50% opacity) settings button in the bottom-right corner opens
**Settings**:

- **App updates** – at the top, check GitHub for a newer release using the button
  beside the installed version, then download and install it.
- **Practice History** – review any previously saved result, delete one, or clear all.
- **Show timer** – show or hide the timer while practicing. The timer pauses while
  Settings or Practice History is open, or the app is in the background.
- **Show correct answers** – on by default. Turn off to hide correct answers after
  mistakes during practice, in Results, and in Practice History. Wrong answers are
  still marked and counted, and saved attempts are unchanged. This choice is remembered.
- **Appearance** – the **Blue Wave** look or a **Color theme** (midnight, royal
  purple, gold, green, pink, black) with a **Dark appearance** switch.
- **Text size** – use the slider under Appearance to adjust app text from 80% to
  200% in 10% steps (default 100%). Changes apply immediately, work with your
  device's font-size setting, and are remembered for next time.
- **Skins** – optional background images (Cherry Blossom Sunset, Starry City
  Sunset, Ocean Wave, Ocean Moonlight, Galaxy, Tropical, Mechanics). Your color
  theme stays the same.
- **Debug logging** – off by default. See [Debug logging](#debug-logging).

## Build and Install

Open this folder as a project in Android Studio, select a Java 17 Gradle JDK,
install Android SDK Platform 36, and let Gradle sync. Alternatively, with Java 17
on `JAVA_HOME` and the SDK on `ANDROID_HOME`:

```sh
sh ./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

On Windows use `.\gradlew.bat` with the same tasks. The checked-in Gradle wrapper
verifies its distribution's SHA-256 checksum.

The installable development APK is `app/build/outputs/apk/debug/app-debug.apk`:

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Android CI** runs the unit tests, lint and a debug build for every pull request
and push to `main`, and uploads the debug APK as an artifact.

### Versions

Build from a full Git checkout (`git fetch --unshallow` if needed). Versions advance
automatically with every pull request merged into `main` (each merge adds one
first-parent commit). The version name is `0.01.NN`, starting at **v0.01.00** for the
first merged pull request, and the version code is `<first-parent commit count> + 1`.
Rebuilding the same commit keeps its version.

### Debug keystore

Every build is signed with the repository's checked-in debug key
(`app/debug.keystore`, alias `androiddebugkey`, passwords `android`), so local
builds, CI builds and manual releases all share one signing certificate and can
update each other. `applicationId` is `com.ssmath.app`.

### APK updates

The app checks GitHub's latest release when it opens and shows a notice when a
newer version is available. Open **Settings > App updates**, select **Check for
updates**, then **Download and install** and confirm the download.

The download shows progress and can be cancelled. The app checks the package,
version and signing certificate before handing the APK to Android. If prompted,
allow **Install unknown apps** for ssMath, then return to the app to confirm
installation. Android still asks for final approval; a compatible update keeps your
settings and practice history.

For the first installation, download `ssMath-v<version>.apk` from this repository's
**Releases** and open it on the phone. Extract Actions artifact ZIPs before opening
an APK.

### Publishing update-compatible releases

Run **Actions > Manual Android Release > Run workflow** to build latest `main`,
verify the signed APK, and publish one universal APK as the latest `v0.01.NN`
release. The in-app updater reads this repository's latest release, not Actions
artifacts. Re-running a published version leaves its release unchanged; merge a
newer pull request for a new update.

After a PR merges into `main`, **PR Release Reminder** posts a comment linking to
**Manual Android Release**. Follow the link and select **Run workflow** on `main`
when ready; the reminder does not start a release.

The release workflow uses the bundled debug key by default. For a stronger
production key instead, configure these optional repository Actions secrets:

| Secret | Value |
| --- | --- |
| `APK_SIGNING_KEYSTORE_BASE64` | Base64-encoded keystore containing the permanent app signing key |
| `APK_SIGNING_STORE_PASSWORD` | Keystore password |
| `APK_SIGNING_KEY_ALIAS` | Signing key alias |
| `APK_SIGNING_KEY_PASSWORD` | Signing key password |

Configure all four together, or leave all four unset to use the bundled debug key;
a partial set fails the workflow. Never commit a distribution keystore or its
passwords. Local release builds use the same configuration through
`APK_SIGNING_STORE_FILE` and the three password/alias environment variables above.

Switching between the bundled debug key and a distribution key changes the app's
signing certificate. If an update reports a signature mismatch, obtain an APK signed
with the same key as the installed app; do not uninstall to bypass it, as that
removes your practice history.

## Debug logging

**Settings > Debug logging** is off by default. When enabled, choose **Full**
(rotating log files, up to 64 KB) or **Reactive** (only the latest 100 events).
Only event names, status numbers and exception class names are recorded — never
answers, practice results or other personal content. Logs are stored privately on
the device and can be viewed, shared or cleared from Settings.
