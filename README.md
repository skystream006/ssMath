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
   the wrong-answer limit (or an optional time limit), whichever comes first, the practice stops and shows how many you
   got right, plus every answered problem with a green check (correct) or a red X
   (wrong, with the correct answer if **Show correct answers** is enabled).
   Results are saved with the date and time.
   If all the chosen questions were answered, even with mistakes, one of eighteen randomly chosen
   congratulations animations plays: dolphins, whales, or anchovies jumping out
   of the water, a balloon-and-confetti party, a candy shower, Pikachu running toward
   the screen and zapping lightning, Squirtle shooting water from his mouth,
   Bulbasaur shooting leaves from his bulb, Charmander shooting fire into the air,
   Jigglypuff rolling and jumping, or Palafin, Finizen, Wailmer, Wailord, Bouffalant,
   Veluza, Mantyke, or Mantine celebrating. The thirteen Pokémon scenes belong to the
   **Pokémons** category and can only appear after completing **15 or more questions**.
   The remaining five scenes belong to **Other** and can appear at any practice length;
   below 15 questions, only **Other** scenes appear. Each scene says **Hurray!!**. These animations
   are drawn in the app and work offline. Press **View results** to
   skip the animation, or wait for it to finish.
   The wrong-answer limit is 5 for up to 50 questions; above 50 it is 10% of the
   chosen total, rounded up to a whole answer (51 questions allows 6 wrong;
   100 questions ends at 10 wrong).
   If the wrong-answer limit ends practice before the last question, a dialog says
   **Nice try! You got {XX} right out of {TOTAL}**, using the chosen number of
   questions as the total. Press **View results** to dismiss it.
   Press **Done** to return to the setup dialog.

The semi-transparent (50% opacity) settings button in the bottom-right corner opens
**Settings**:

- **App updates** – at the top, check GitHub for a newer release using the button
  beside the installed version, then download and install it.
- **My Rewards** – directly below App updates, view your collected prizes and
  one-third fragments in a four-column grid grouped by **Tier 1** and **Tier 2**,
  even when the rewards system is disabled.
  Tap **Use rewards** in the upper-right corner to see whole reward pictures and
  available counts grouped by tier. Tap a reward and confirm **Yes** to use one; **No** cancels.
  Only whole rewards can be used. Remaining counts are saved on this device;
  fragments and practice history are unchanged.
- **My Pokémons** – directly below My Rewards, collect a still image of each
  celebration you see after completing a practice, even when rewards are disabled.
  Each celebration is collected only once and labelled with its name, grouped under
  **Pokémons** or **Other**. Each section heading shows its own collected/total
  fraction (out of 13 Pokémon or 5 Other), including zero when none are collected.
  Tap an image to replay its animation; close it or wait for it to finish to return to your collection.
  The collection is saved on this device and is not removed when history is deleted.
- **Practice History** – review any previously saved result, delete one, or clear all.
- **Rewards system** – on by default. Earn
  prizes for completing **25 or more questions** with **more than 90% correct**.
  After the celebration, tap the hopping gift box to open it and release confetti.
  **Tier 1** rewards are for sessions of **25–49 questions**: a random Lollipop,
  Ice Cream Cone, Gummi Bear, or Ramen Fragment. **Tier 2** rewards are for sessions
  of **50 or more questions**: a Video Game Fragment.
  Fewer than 25 questions and exactly 90% correct do not qualify.
  Every 3 fragments of the same type automatically become 1 whole prize, with the
  fragment counter returning to 0. Prizes and their cumulative totals are saved
  with the result; deleting history does not remove collected rewards.
  A gift dismissed before opening can still be claimed from its history entry.
  All reward illustrations and animations are drawn in the app and work offline.
- **Show timer** – off by default. Show or hide the timer while practicing. The timer pauses while
  Settings, Practice History, My Rewards, or My Pokémons is open, or the app is in the background.
  When both Rewards system and Show timer are enabled, **Time limit** offers
  **None** or **5–60 minutes** in 5-minute increments. The selection applies to the
  next practice. A timed practice shows time remaining and ends when time runs out,
  saving the partial result without a prize. Changing settings during practice does
  not remove its active time limit.
- **Show correct answers** – off by default. Turn on to reveal correct answers after
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

Tap the description beginning **Off by default** below **Enable debug logging**
seven times to add all celebration animations to **My Pokémons**, without duplicating
ones already collected. This works even while debug logging and rewards are disabled.
