// ========== Radio - Audio Player ==========
// Single global audio element
const audioElement = new Audio();
let currentTrackKey = null; // Keeps track of the currently playing sound

// Define all audio sources
const soundSources = {
    white: "https://whitenoise.tmsoft.com/wntv/noise_white-0.mp3",
    brown: "https://whitenoise.tmsoft.com/wntv/noise_brown-0.mp3",
    blue: "https://whitenoise.tmsoft.com/wntv/noise_blue-0.mp3",
    pink: "https://whitenoise.tmsoft.com/wntv/noise_pink-0.mp3",
   
    jangkrik: "../asset/songs/Jangkrik.mp3",
    ombak: "../asset/songs/Ombak.mp3",
    api: "../asset/songs/Api.mp3",
    burung: "../asset/songs/burung.mp3",
    
    twilight: "../asset/songs/twilight.mp3",
    monoman: "../asset/songs/Monoman.mp3",
    yasumu: "../asset/songs/Yasumu.mp3"
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


// Add event listener for page unload (for saving state)
window.onbeforeunload = () => savePlaybackState(currentTrackKey);

// Set default button styles on page load
function initializeButtons() {
    const buttons = document.querySelectorAll('.song-btn');
    buttons.forEach((button) => {
        const colorVar = `--${button.dataset.color}`;
        const borderColor = getComputedStyle(document.documentElement).getPropertyValue(colorVar);
        button.style.setProperty('--button-border-color', borderColor);
    });
}

// Handle hover effect
function handleHover(button, isHovered) {
    const colorVar = `--${button.dataset.color}`;
    const hoverColor = getComputedStyle(document.documentElement).getPropertyValue(colorVar);
    if (isHovered) {
        button.style.setProperty('--button-border-color', hoverColor);
    } else {
        button.style.setProperty('--button-border-color', hoverColor);
    }
}

// Attach hover event listeners
function attachHoverListeners() {
    const buttons = document.querySelectorAll('.song-btn');
    buttons.forEach((button) => {
        button.addEventListener('mouseenter', () => handleHover(button, true));
        button.addEventListener('mouseleave', () => handleHover(button, false));
    });
}

// Initialize buttons and hover listeners on page load
window.onload = () => {
    initializeButtons();
    attachHoverListeners();
    restorePlaybackState(); // Restore audio state if needed
};
