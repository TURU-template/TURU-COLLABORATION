// ========== Radio - Audio Player ==========
// Single global audio element
const audioElement = new Audio();
let currentTrackKey = null; // Keeps track of the currently playing sound

// Define all audio sources
const soundSources = {
    ice: "https://www.soundjay.com/nature/sounds/ice-collecting-01.mp3",
    zipper: "https://www.soundjay.com/cloth/sounds/jacket-zipper-3.mp3",
    fire: "https://cdn.freesound.org/previews/499/499032_10791958-lq.mp3",
    brown: "https://whitenoise.tmsoft.com/wntv/noise_brown-0.mp3",
    blue: "https://whitenoise.tmsoft.com/wntv/noise_blue-0.mp3",
    pink: "https://whitenoise.tmsoft.com/wntv/noise_pink-0.mp3",
    bird: "https://www.freesoundslibrary.com/wp-content/uploads/2018/02/birds-chirping-sound-effect.mp3",
    jangkrik: "https://www.freesoundslibrary.com/wp-content/uploads/2022/11/crickets-chirping-sounds-of-night.mp3",
    twilight: "https://audio.jukehost.co.uk/api/external/download/0t0DjzJIiWeYJz7LNdS0VBZY609YLqrm.mp3",
    monoman: "https://archive.org/download/monoman_202302/Monoman.mp3",
    yasumu: "https://archive.org/download/Yasumu/Yasumu.mp3",
    white: "https://whitenoise.tmsoft.com/wntv/noise_white-0.mp3"
};

// Ensure audio loops
audioElement.loop = true;

// Function to play or toggle audio
function toggleAudio(soundKey) {
    if (currentTrackKey === soundKey) {
        // Pause if the same button is clicked
        audioElement.pause();
        setActiveButton(currentTrackKey, false);
        currentTrackKey = null;
        savePlaybackState(null);
    } else {
        // Play the selected audio
        audioElement.src = soundSources[soundKey];
        audioElement.play();
        savePlaybackState(soundKey);

        // Update button states
        if (currentTrackKey) setActiveButton(currentTrackKey, false); // Reset the previous button
        setActiveButton(soundKey, true);
        currentTrackKey = soundKey;
    }
}

// Restore playback state on page load
function restorePlaybackState() {
    const playbackState = JSON.parse(localStorage.getItem("radioPlaybackState"));
    if (playbackState && playbackState.soundKey && soundSources[playbackState.soundKey]) {
        currentTrackKey = playbackState.soundKey;
        audioElement.src = soundSources[currentTrackKey];
        audioElement.currentTime = playbackState.currentTime || 0;
        audioElement.play();
        setActiveButton(currentTrackKey, true);
    }
}

// Save playback state before navigating away
function savePlaybackState(soundKey) {
    const playbackState = {
        soundKey: currentTrackKey,
        currentTime: audioElement.currentTime
    };
    localStorage.setItem("radioPlaybackState", JSON.stringify(playbackState));
}


// Update button states
function setActiveButton(soundKey, isActive) {
    const button = document.getElementById(`${soundKey}-btn`);
    if (button) {
        if (isActive) {
            button.classList.remove("btn-outline");
            button.classList.add(`btn-${getButtonColor(soundKey)}`);
        } else {
            button.classList.add("btn-outline");
            button.classList.remove(`btn-${getButtonColor(soundKey)}`);
        }
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
        ice: "success",
        bird: "success",
        twilight: "info",
        monoman: "secondary",
        yasumu: "primary"
    }[soundKey];
}

// Add event listener for page unload (for saving state)
window.onbeforeunload = () => savePlaybackState(currentTrackKey);

// Restore playback state on page load
window.onload = restorePlaybackState;

// // Save state before leaving the page
// window.onbeforeunload = () => {
//     savePlaybackState(currentTrackKey);
// };
