// =========STOPWATCH FUNCTION - SLEEP COUNTER=========
let isRunning = false;
let startTime, endTime;

const stopwatchBtn = document.getElementById('stopwatchBtn');
const labelBtn = document.getElementById('labelBtn');
const resultDisplay = document.getElementById('resultDisplay');
const timestampDisplay = document.getElementById('timestampDisplay');

stopwatchBtn.addEventListener('click', () => {
    if (!isRunning) {
        // START timestamp
        startTime = new Date();
        const startDateStr = `${startTime.getDate().toString().padStart(2, '0')}/${(startTime.getMonth() + 1).toString().padStart(2, '0')}/${startTime.getFullYear().toString().slice(-2)} ${startTime.getHours().toString().padStart(2, '0')}:${startTime.getMinutes().toString().padStart(2, '0')}`;
        timestampDisplay.textContent = `${startDateStr}`;
        stopwatchBtn.textContent = "😴";
        stopwatchBtn.style.backgroundColor = `--lilac`;
        labelBtn.textContent = "Tombol Bangun";
    } else {
        // STOP timestamp
        endTime = new Date();
        const elapsedTime = (endTime - startTime) / 1000; // Convert to seconds
        const hours = Math.floor(elapsedTime / 3600);
        const minutes = Math.floor((elapsedTime % 3600) / 60);

        // Format the elapsed time based on hours
        const elapsedTimeStr = hours > 0 ? `${hours} j ${minutes} m` : `${minutes} m`;
        durationDisplay.textContent = elapsedTimeStr;

        // Format stop timestamp
        const endDateStr = `${endTime.getDate().toString().padStart(2, '0')}/${(endTime.getMonth() + 1).toString().padStart(2, '0')}/${endTime.getFullYear().toString().slice(-2)} ${endTime.getHours().toString().padStart(2, '0')}:${endTime.getMinutes().toString().padStart(2, '0')}`;
        timestampDisplay.textContent += ` - ${endDateStr}`;

        stopwatchBtn.textContent = "🙂";
        labelBtn.textContent = "Tombol Tidur";
    }
    isRunning = !isRunning;
});

