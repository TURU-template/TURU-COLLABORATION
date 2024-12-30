let startTime, endTime;
let run =
  document.getElementById("stateContainer").getAttribute("data-state") ===
  "true";

let state = document
  .getElementById("stateContainer")
  .getAttribute("data-state");

const stopwatchBtn = document.getElementById("stopwatchBtn");
const labelBtn = document.getElementById("labelBtn");
const durationDisplay = document.getElementById("durationDisplay");
const timestampDisplay = document.getElementById("timestampDisplay");
let intervalId; // Declare interval ID for duration updates

if (run) {
  labelBtn.textContent = "Tombol Bangun";
  document.getElementById("durationDisplay").classList.add("blinking");
  document.getElementById("mascotNameDisplay").classList.add("blinking");

  // Retrieve and calculate the ongoing duration from the backend-provided start time
  const isoStartTime = document
    .getElementById("stateContainer")
    .getAttribute("data-start-time");
  startTime = new Date(isoStartTime);

  // Calculate and display the elapsed time
  intervalId = setInterval(() => {
    const now = new Date();
    const elapsedTime = (now - startTime) / 1000; // Elapsed time in seconds
    const hours = Math.floor(elapsedTime / 3600);
    const minutes = Math.floor((elapsedTime % 3600) / 60);

    const elapsedTimeStr =
      hours > 0 ? `${hours} jam ${minutes} menit` : `${minutes} menit`;
    durationDisplay.textContent = elapsedTimeStr;
  }, 1000);
} else {
  labelBtn.textContent = "Tombol Tidur";
  document.getElementById("durationDisplay").classList.remove("blinking");
  document.getElementById("mascotNameDisplay").classList.remove("blinking");

  durationDisplay.textContent = ""; // Clear duration if not running
}

// Button click event
stopwatchBtn.addEventListener("click", () => {
  if (!run) {
    // START timestamp
    document.getElementById("durationDisplay").classList.add("blinking");
    document.getElementById("mascotNameDisplay").classList.add("blinking");

    startTime = new Date();
    const isoStartTime = startTime.toISOString();
    const startDateStr = `${startTime.getDate().toString().padStart(2, "0")}/${(
      startTime.getMonth() + 1
    )
      .toString()
      .padStart(2, "0")}/${startTime
      .getFullYear()
      .toString()
      .slice(-2)} ${startTime
      .getHours()
      .toString()
      .padStart(2, "0")}:${startTime.getMinutes().toString().padStart(2, "0")}`;
    timestampDisplay.textContent = `Mulai: ${startDateStr}`;
    stopwatchBtn.textContent = "😴";
    labelBtn.textContent = "Tombol Bangun";

    // Send the start time to the backend
    fetch("/api/add-start", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(isoStartTime),
    })
      .then((response) => {
        if (response.ok) {
          return response.text();
        }
        throw new Error("Failed to call addStart.");
      })
      .then((data) => {
        console.log("Response from addStart:", data);
      })
      .catch((error) => console.error("Error:", error));

    // Start updating the duration every second
    intervalId = setInterval(() => {
      const now = new Date();
      const elapsedTime = (now - startTime) / 1000; // Convert to seconds
      const hours = Math.floor(elapsedTime / 3600);
      const minutes = Math.floor((elapsedTime % 3600) / 60);

      const elapsedTimeStr =
        hours > 0 ? `${hours} j ${minutes} m` : `${minutes} m`;
      durationDisplay.textContent = elapsedTimeStr;
    }, 1000);
  } else {
    // STOP timestamp
    document.getElementById("durationDisplay").classList.remove("blinking");
    document.getElementById("mascotNameDisplay").classList.remove("blinking");

    endTime = new Date();
    clearInterval(intervalId); // Stop the real-time updates

    const elapsedTime = (endTime - startTime) / 1000; // Convert to seconds
    const hours = Math.floor(elapsedTime / 3600);
    const minutes = Math.floor((elapsedTime % 3600) / 60);

    const elapsedTimeStr =
      hours > 0 ? `${hours} j ${minutes} m` : `${minutes} m`;
    durationDisplay.textContent = elapsedTimeStr;

    const endDateStr = `${endTime.getDate().toString().padStart(2, "0")}/${(
      endTime.getMonth() + 1
    )
      .toString()
      .padStart(2, "0")}/${endTime.getFullYear().toString().slice(-2)} ${endTime
      .getHours()
      .toString()
      .padStart(2, "0")}:${endTime.getMinutes().toString().padStart(2, "0")}`;
    timestampDisplay.textContent += ` — ${endDateStr}`;

    stopwatchBtn.textContent = "🙂";
    labelBtn.textContent = "Tombol Tidur";

    // Send the end time to the backend
    const isoEndTime = endTime.toISOString();
    const requestPayload = { endTime: isoEndTime };
    fetch("/api/add-end", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestPayload),
    })
      .then((response) => response.json()) // Expecting a JSON response
      .then((data) => {
        console.log("Response from addEnd:", data);

        // Check if the data was deleted and trigger an alert if so
        if (data.isDeleted) {
          alert("Data ditolak karena durasi tidur dibawah 15 menit");
        }

        // Refresh page
        window.location.reload();
      })
      .catch((error) => console.error("Error:", error));
  }
  run = !run;
});
