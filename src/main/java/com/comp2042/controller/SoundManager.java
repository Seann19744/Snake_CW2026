package com.comp2042.controller;

import javafx.scene.media.AudioClip;

/**
 * Manages game audio effects such as the coin collection sound.
 * Extracted from {@link GameController} to adhere to the Single Responsibility Principle (SRP)
 * and support Dependency Injection during testing.
 */
public class SoundManager {

    private static final String COIN_SOUND_RESOURCE = "/resources/views/res/coinSound.wav";

    private final Runnable coinSoundAction;

    /**
     * Constructs a SoundManager that loads and plays the default coin collection {@link AudioClip}.
     */
    public SoundManager() {
        AudioClip coinClip = new AudioClip(
                getClass().getResource(COIN_SOUND_RESOURCE).toExternalForm()
        );
        this.coinSoundAction = coinClip::play;
    }

    /**
     * Constructs a SoundManager with a custom playback action (useful for silent or deterministic unit testing).
     *
     * @param coinSoundAction callback invoked when {@link #playCoinSound()} is called
     */
    public SoundManager(Runnable coinSoundAction) {
        this.coinSoundAction = coinSoundAction != null ? coinSoundAction : () -> {};
    }

    /**
     * Plays the coin collection sound effect.
     */
    public void playCoinSound() {
        coinSoundAction.run();
    }
}
