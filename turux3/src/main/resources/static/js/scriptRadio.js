// ========== Radio - Audio Player ==========
// Single global audio element for Color and Lo-Fi
const audioElement = new Audio();
let currentTrackKey = null; // Keeps track of the currently playing sound for Color and Lo-Fi

// Separate audio elements for Ambient Sounds
const ambientAudioElements = {};

// Define all audio sources
const soundSources = {
  // Color
  white: {
    url: "https://whitenoise.tmsoft.com/wntv/noise_white-0.mp3",
    volume: 0.2,
  },
  brown: {
    url: "https://whitenoise.tmsoft.com/wntv/noise_brown-0.mp3",
    volume: 0.2,
  },
  blue: {
    url: "https://whitenoise.tmsoft.com/wntv/noise_blue-0.mp3",
    volume: 0.2,
  },
  pink: {
    url: "https://whitenoise.tmsoft.com/wntv/noise_pink-0.mp3",
    volume: 0.2,
  },

  // Ambient
  jangkrik: { url: "../asset/songs/Jangkrik.mp3", volume: 0.1 }, // 30% volume
  ombak: { url: "../asset/songs/Ombak.mp3", volume: 0.3 }, // Default
  api: { url: "../asset/songs/Api.mp3", volume: 1.0 },
  hujan: { url: "../asset/songs/Hujan.mp3", volume: 0.2 },
  burung: { url: "../asset/songs/Burung.mp3", volume: 0.3 },

  // Lo-Fi
  twilight: { url: "../asset/songs/Twilight.mp3", volume: 0.5 },
  monoman: { url: "../asset/songs/Monoman.mp3", volume: 0.7 }, // 70% volume
  yasumu: { url: "../asset/songs/Yasumu.mp3", volume: 0.5 },
};

// Ensure audio loops
audioElement.loop = true;

// Function to toggle audio playback
function toggleAudio(soundKey) {
  const button = document.getElementById(`${soundKey}-btn`);

  if (isAmbientSound(soundKey)) {
    // Ambient Sound Logic
    if (!ambientAudioElements[soundKey]) {
      ambientAudioElements[soundKey] = new Audio(soundSources[soundKey].url);
      ambientAudioElements[soundKey].loop = true;
      ambientAudioElements[soundKey].volume = soundSources[soundKey].volume; // Default volume
    }

    const audio = ambientAudioElements[soundKey];
    if (audio.paused) {
      stopNonAmbientSounds(); // Stop Color/Lo-Fi music before playing Ambient
      audio.play();
      setButtonState(button, true);
    } else {
      audio.pause();
      setButtonState(button, false);
    }
  } else {
    // Color and Lo-Fi Logic
    if (currentTrackKey === soundKey) {
      // Pause if the same button is clicked
      audioElement.pause();
      setButtonState(button, false);
      currentTrackKey = null;
      savePlaybackState(null);
    } else {
      // Play the selected audio
      if (soundSources[soundKey]) {
        stopAllAmbientSounds(); // Stop all Ambient sounds before playing Color/Lo-Fi
        audioElement.src = soundSources[soundKey].url;
        audioElement.volume = soundSources[soundKey].volume; // Default volume
        audioElement.play();
        savePlaybackState(soundKey);

        // Update button states
        if (currentTrackKey) {
          const previousButton = document.getElementById(
            `${currentTrackKey}-btn`
          );
          setButtonState(previousButton, false);
        }
        setButtonState(button, true);
        currentTrackKey = soundKey;
      }
    }
  }
}

// Helper to check if a sound is Ambient
function isAmbientSound(soundKey) {
  return ["jangkrik", "ombak", "api", "hujan", "burung"].includes(soundKey);
}

// Stop all Ambient Sounds
function stopAllAmbientSounds() {
  for (const key in ambientAudioElements) {
    const audio = ambientAudioElements[key];
    if (!audio.paused) {
      audio.pause();
      const button = document.getElementById(`${key}-btn`);
      setButtonState(button, false);
    }
  }
}

// Stop all non-Ambient (Color/Lo-Fi) sounds
function stopNonAmbientSounds() {
  if (currentTrackKey) {
    audioElement.pause();
    const button = document.getElementById(`${currentTrackKey}-btn`);
    setButtonState(button, false);
    currentTrackKey = null;
  }
}

function restorePlaybackState() {
  const playbackState = JSON.parse(localStorage.getItem("radioPlaybackState"));
  console.log("Restoring Playback State:", playbackState);

  if (playbackState && playbackState.soundKey) {
    const { soundKey, currentTime, volume, isAmbient } = playbackState;

    if (isAmbient) {
      // Check and create audio element if it doesn't exist
      if (!ambientAudioElements[soundKey]) {
        ambientAudioElements[soundKey] = new Audio(soundSources[soundKey].url);
        ambientAudioElements[soundKey].loop = true;
        ambientAudioElements[soundKey].volume = soundSources[soundKey].volume;
      }

      const ambientAudio = ambientAudioElements[soundKey];

      // Set properties and play
      ambientAudio.currentTime = currentTime || 0;
      ambientAudio.volume = volume || soundSources[soundKey].volume;

      // Attempt to play the audio
      ambientAudio.play().catch((error) => {
        console.error(`Failed to play ambient audio: ${error.message}`);
      });

      // Update button state
      const button = document.getElementById(`${soundKey}-btn`);
      if (button) {
        setButtonState(button, true);
      }
    } else {
      // Restore Color/Lo-Fi sounds
      currentTrackKey = soundKey;
      audioElement.src = soundSources[soundKey].url;
      audioElement.volume = volume || soundSources[soundKey].volume;
      audioElement.currentTime = currentTime || 0;

      // Attempt to play the audio
      audioElement.play().catch((error) => {
        console.error(`Failed to play Color/Lo-Fi audio: ${error.message}`);
      });

      // Update button state
      const button = document.getElementById(`${soundKey}-btn`);
      if (button) {
        setButtonState(button, true);
      }
    }
  }
}

function savePlaybackState(soundKey) {
  const playbackState = {
    soundKey,
    currentTime:
      isAmbientSound(soundKey) && ambientAudioElements[soundKey]
        ? ambientAudioElements[soundKey].currentTime
        : audioElement.currentTime,
    volume:
      soundKey && soundSources[soundKey] ? soundSources[soundKey].volume : 1,
    isAmbient: isAmbientSound(soundKey), // Track if it's an Ambient sound
  };

  localStorage.setItem("radioPlaybackState", JSON.stringify(playbackState));
}

// Update button states with dynamic color styling
function setButtonState(button, isActive) {
  if (button) {
    if (isActive) {
      button.style.color = "white";
      button.style.background = getComputedStyle(
        document.documentElement
      ).getPropertyValue(`--${button.dataset.color}`);
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
  const buttons = document.querySelectorAll(".song-btn");
  buttons.forEach((button) => {
    const colorVar = `--${button.dataset.color}`;
    const borderColor = getComputedStyle(
      document.documentElement
    ).getPropertyValue(colorVar);
    button.style.setProperty("--button-border-color", borderColor);
  });
}

// Handle hover effect
function handleHover(button, isHovered) {
  const colorVar = `--${button.dataset.color}`;
  const hoverColor = getComputedStyle(
    document.documentElement
  ).getPropertyValue(colorVar);
  button.style.setProperty("--button-border-color", hoverColor);
}

// Attach hover event listeners
function attachHoverListeners() {
  const buttons = document.querySelectorAll(".song-btn");
  buttons.forEach((button) => {
    button.addEventListener("mouseenter", () => handleHover(button, true));
    button.addEventListener("mouseleave", () => handleHover(button, false));
  });
}

window.onload = () => {
  initializeButtons();
  attachHoverListeners();
  restorePlaybackState(); // Restore audio state if needed
};

// ======================= SLEEP TIMER =============================
function showTimerOptions() {
  const timerOptions = document.getElementById("timer-options");
  timerOptions.style.display =
    timerOptions.style.display === "flex" ? "none" : "flex";
}

// Set a timer and shut down audio when the timer ends
function setTimer(menit) {
  showAlert(`Sleep Timer dengan durasi ${menit} menit!`, "success", 5000);
  document.getElementById("timer-options").style.display = "none";

  setTimeout(() => {
    shutdownAudio();
    showAlert("Sleep Timer selesai, musik berhenti", "danger", 5000);
  }, menit * 60000); // kalau menit 60000, detik 1000
}

// Function to stop all audio playback
function shutdownAudio() {
  if (!audioElement.paused) {
    audioElement.pause(); // Pause the audio
    audioElement.currentTime = 0; // Reset the playback time
    if (currentTrackKey) {
      const button = document.getElementById(`${currentTrackKey}-btn`);
      setButtonState(button, false); // Reset button state
    }
    currentTrackKey = null; // Reset the current track key
  }
}

function showAlert(message, alertType = "danger", duration = 3000) {
  const alertContainer = document.getElementById("alert-container");
  const alertElement = document.createElement("div");
  alertElement.className = `alert alert-${alertType} d-flex align-items-center fade show`;
  alertElement.setAttribute("role", "alert");

  alertElement.innerHTML = `
        <i class="bi bi-stopwatch flex-shrink-0 me-2" style="font-size: 1.5rem;"></i>
        <div>${message}</div>
    `;

  alertContainer.appendChild(alertElement);

  setTimeout(() => {
    alertElement.classList.remove("show");
    alertElement.classList.add("hide");
    setTimeout(() => alertElement.remove(), 300);
  }, duration);
}
