# Perfect Sound - Music for Better Sleep

**Perfect Sound** is an Android app designed to help people sleep better by playing relaxing and soothing music. The app contains a variety of national melodies from around the world, offering a calming and peaceful experience for users. Perfect for helping you fall asleep, stay relaxed, and enjoy a deeper sleep.

## Features

- **Diverse Music Collections**: Enjoy a wide selection of national melodies from different cultures, all curated for relaxation and sleep.
- **Customizable Playlists**: Create personalized playlists by selecting your favorite tracks.
- **Sleep Timer**: Set a timer to automatically stop the music after a specific time, so you don't have to worry about turning it off.
- **Background Playback**: Continue playing music even when the app is minimized or the screen is off.
- **Soothing Sounds**: Carefully chosen tracks designed to promote relaxation and improve sleep quality.
- **Simple & Intuitive UI**: User-friendly interface designed for a calm and smooth experience.

## Installation

### Prerequisites

- **Android Studio**: You need to have Android Studio installed. Download it from [here](https://developer.android.com/studio).
- **Android SDK**: Ensure that the necessary Android SDK packages are installed.

### Getting Started

1. Clone this repository:
    ```bash
    git clone git@github.com:herdsman-qk/PerfectSoundAndroidPro.git
    cd PerfectSoundAndroidPro
    ```

2. Open the project in **Android Studio**:
   - Launch Android Studio and select **Open an existing project**.
   - Navigate to the directory where you cloned the project and open it.

3. Sync the project with Gradle:
   - Android Studio will automatically sync the project. If not, click **Sync Now** in the top bar.

4. Build and Run:
   - Select your target device (emulator or connected Android device).
   - Click **Run** or use `Shift + F10` to build and run the app.

## Usage

1. **Launch the App**: Open the **Perfect Sound** app on your Android device.
2. **Browse Music**: Browse through the available national melodies and choose the ones you want to listen to.
3. **Play Music**: Tap on the play button to start playing music. Adjust the volume to your comfort.
4. **Create Playlists**: Add your favorite tracks to a playlist for easy access.
5. **Set Sleep Timer**: Set a timer to automatically stop the music after a desired time period (e.g., 30 minutes or 1 hour).
6. **Background Play**: Music will continue to play in the background even if you minimize the app or turn off the screen.

## Project Structure

- `app/`: Contains the main app code.
    - `src/`: Java or Kotlin source files.
    - `res/`: Resources such as music files, images, layouts, and strings.
    - `AndroidManifest.xml`: The app’s manifest file.
- `gradle/`: Gradle configuration files.
- `build.gradle`: Build configuration for the app module.

## Dependencies

This project uses the following libraries:

- **ExoPlayer**: For seamless music playback and media handling.
- **Room**: For local storage and saving user preferences (e.g., playlists).
- **Retrofit**: For making network requests (if applicable).
- **Glide**: For image loading and caching.
- **Firebase**: For user authentication and cloud storage (if applicable).

You can find all the dependencies in the `build.gradle` files.

## Testing

To run the tests:

1. **Unit Tests**: Run unit tests located in the `src/test/java/` directory.
2. **UI Tests**: Run UI tests in the `src/androidTest/java/` directory using Espresso or UI Automator.

To run the tests from Android Studio:
- Right-click on the test class or folder and click **Run**.

## Contributing

We welcome contributions to **Perfect Sound**! To contribute:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Open a pull request.

## Acknowledgements

- **Android Developers Documentation**: For providing comprehensive resources for Android app development.
- **Firebase**: For providing cloud-based services (authentication, storage) to enhance the app.
- **Room**: For offering a simple local database solution to manage data storage.

## Contact

For any questions or issues, feel free to contact me at [quinn.k.2500@gmail.com].

---

Thank you for using **Perfect Sound**! We hope it helps you get a restful and peaceful sleep.
