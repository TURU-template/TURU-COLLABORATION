// ========== Radio - Audio Player ==========
// Single global audio element
const audioElement = new Audio();
let currentTrackKey = null; // Keeps track of the currently playing sound

// Define all audio sources
const soundSources = {
    ice: "https://www.soundjay.com/nature/sounds/ice-collecting-01.mp3",
    zipper: "https://www.soundjay.com/cloth/sounds/jacket-zipper-3.mp3",
    fire: "https://cdn.freesound.org/previews/499/499032_10791958-lq.mp3",
     white: "https://whitenoise.tmsoft.com/wntv/noise_white-0.mp3",
    brown: "https://whitenoise.tmsoft.com/wntv/noise_brown-0.mp3",
    blue: "https://whitenoise.tmsoft.com/wntv/noise_blue-0.mp3",
    pink: "https://whitenoise.tmsoft.com/wntv/noise_pink-0.mp3",
    bird: "https://www.freesoundslibrary.com/wp-content/uploads/2018/02/birds-chirping-sound-effect.mp3",
    jangkrik: "../asset/songs/Cicada.mp3",
    twilight: "https://audio.jukehost.co.uk/api/external/download/0t0DjzJIiWeYJz7LNdS0VBZY609YLqrm.mp3",
    monoman: "https://archive.org/download/monoman_202302/Monoman.mp3",
    yasumu: "https://archive.org/download/Yasumu/Yasumu.mp3"
};

// Ensure audio loops
audioElement.loop = true;

// Function to toggle audio and update button styles
function toggleAudio(soundKey) {
    const button = document.getElementById(`${soundKey}-btn`);

    if (currentTrackKey === soundKey) {
        // Pause if the same button is clicked
        audioElement.pause();
        setButtonState(button, false);
        currentTrackKey = null;
        savePlaybackState(null);
    } else {
        // Play the selected audio
        if (soundSources[soundKey]) {
            audioElement.src = soundSources[soundKey];
            audioElement.play();
            savePlaybackState(soundKey);

            // Update button states
            if (currentTrackKey) {
                const previousButton = document.getElementById(`${currentTrackKey}-btn`);
                setButtonState(previousButton, false);
            }
            setButtonState(button, true);
            currentTrackKey = soundKey;
        }
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
        setButtonState(document.getElementById(`${currentTrackKey}-btn`), true);
    }
}

// Save playback state
function savePlaybackState(soundKey) {
    const playbackState = {
        soundKey: currentTrackKey,
        currentTime: audioElement.currentTime
    };
    localStorage.setItem("radioPlaybackState", JSON.stringify(playbackState));
}


// Update button states with dynamic color styling
function setButtonState(button, isActive) {
    if (button) {
        if (isActive) {
            button.style.color = "white";
            button.style.background = getComputedStyle(document.documentElement).getPropertyValue(
                `--${button.dataset.color}`
            );
            button.classList.add("active");
        } else {
            button.style.color = "gray";
            button.style.background = "whitesmoke";
            button.classList.remove("active");
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
