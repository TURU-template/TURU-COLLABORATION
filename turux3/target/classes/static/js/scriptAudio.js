// Initialize audio objects
var sounds = {
    ice: new Audio("https://www.soundjay.com/nature/sounds/ice-collecting-01.mp3"),
    zipper: new Audio("https://www.soundjay.com/cloth/sounds/jacket-zipper-3.mp3"),
    fire: new Audio("https://www.soundjay.com/nature/sounds/fire-burning-01.mp3") // Example fire sound
};

// Set loop for each audio
Object.values(sounds).forEach(sound => sound.loop = true);

// Function to toggle individual audio
function toggleAudio(soundKey) {
    const sound = sounds[soundKey];
    if (sound.paused) {
        sound.play();
    } else {
        sound.pause();
    }
}

// Mute/Unmute all audios
function muteAll() {
    const isMuted = !sounds.ice.muted;  // Check current mute state
    Object.values(sounds).forEach(sound => sound.muted = isMuted);
}
