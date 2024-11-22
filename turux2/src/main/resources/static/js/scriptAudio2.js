// ========= Radio - Audio Player =========
// Single global audio element
const audioElement = new Audio();
let currentTrackKey = null; // Keeps track of the currently playing sound

// Initialize audio objects
const sounds = {
    ice: new Audio("https://www.soundjay.com/nature/sounds/ice-collecting-01.mp3"),
    zipper: new Audio("https://www.soundjay.com/cloth/sounds/jacket-zipper-3.mp3"),
    fire: new Audio("https://cdn.freesound.org/previews/499/499032_10791958-lq.mp3"),
    brown: new Audio("https://whitenoise.tmsoft.com/wntv/noise_brown-0.mp3"),
    blue: new Audio("https://whitenoise.tmsoft.com/wntv/noise_blue-0.mp3"),
    pink: new Audio("https://whitenoise.tmsoft.com/wntv/noise_pink-0.mp3"),
    bird: new Audio("https://www.freesoundslibrary.com/wp-content/uploads/2018/02/birds-chirping-sound-effect.mp3"),
    jangkrik: new Audio("https://www.freesoundslibrary.com/wp-content/uploads/2022/11/crickets-chirping-sounds-of-night.mp3"),
    twilight: new Audio("https://audio.jukehost.co.uk/api/external/download/0t0DjzJIiWeYJz7LNdS0VBZY609YLqrm.mp3"),
    monoman: new Audio("https://archive.org/download/monoman_202302/Monoman.mp3"),
    yasumu: new Audio("https://archive.org/download/Yasumu/Yasumu.mp3"),
    //lorem: new Audio("https://whitenoise.tmsoft.com/wntv/noise_pink-0.mp3"),
    white: new Audio("https://whitenoise.tmsoft.com/wntv/noise_white-0.mp3")
};

// // Ensure all sounds loop
Object.values(sounds).forEach(sound => (sound.loop = true));
// Ensure audio loops
audioElement.loop = true;

// Function to toggle individual audio
function toggleAudio(soundKey) {
    const sound = sounds[soundKey];

    if (sound.paused) {
        // Stop sounds based on exclusivity rules
        if (isColorSong(soundKey)) {
            stopAllAmbientSounds();
            stopAllColorSongs();
        } else {
            stopAllColorSongs();
        }

        // Play the selected sound and update UI
        sound.play();
        setActiveButton(soundKey, true);
    } else {
        // Pause the sound and reset UI
        sound.pause();
        setActiveButton(soundKey, false);
    }
}

// Check if the sound is a color song
function isColorSong(soundKey) {
    return ["brown", "blue", "pink", "white"].includes(soundKey);
}

// Stop all color songs
function stopAllColorSongs() {
    ["brown", "blue", "pink", "white"].forEach(key => {
        sounds[key].pause();
        setActiveButton(key, false);
    });
}

// Stop all ambient sounds
function stopAllAmbientSounds() {
    ["fire", "ice"].forEach(key => {
        sounds[key].pause();
        setActiveButton(key, false);
    });
}

// Set button active or reset
function setActiveButton(soundKey, isActive) {
    const button = document.getElementById(`${soundKey}-btn`);
    if (isActive) {
        button.classList.remove("btn-outline");
        button.classList.add(`btn-${getButtonColor(soundKey)}`);
    } else {
        button.classList.add("btn-outline");
        button.classList.remove(`btn-${getButtonColor(soundKey)}`);
    }
}

// Get button color based on sound key
function getButtonColor(soundKey) {
    return {
        white: "primary",
        brown: "secondary",
        pink: "danger",
        blue: "info",
        fire: "warning",
        ice: "success"
    }[soundKey];
}

// Mute/Unmute all audios
function muteAll() {
    const isMuted = !sounds.ice.muted; // Check the current mute state
    Object.values(sounds).forEach(sound => (sound.muted = isMuted));
}