// Get DOM elements
const addCityButton = document.querySelector("#add-button");
const savedCitySelect = document.querySelector("#saved-city-select");
const savedWeatherButton = document.querySelector("#saved-button");
const deleteCitySelect = document.querySelector("#delete-city-select");
const deleteCityButton = document.querySelector("#delete-button");
const savedDataDiv = document.querySelector("#saved-data");

// Add event listeners
addCityButton.addEventListener("click", addCity);
savedWeatherButton.addEventListener("click", getSavedWeatherData);
deleteCityButton.addEventListener("click", deleteCity);

// Populate the saved city select dropdown on page load
populateSavedCitySelect();

// Populate the delete city select dropdown on page load
populateDeleteCitySelect();

// Function to add a city to the database
function addCity() {
    const newCityInput = document.querySelector("#new-city");
    const newCity = newCityInput.value.trim();

    if (newCity === "") {
        alert("Please enter a city name.");
        return;
    }

    fetch(`/weather/${newCity}`, {
        method: "GET"
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            newCityInput.value = "";
            populateSavedCitySelect();
            populateDeleteCitySelect();
        })
        .catch(error => {
            alert(error.message);
        });
    }

// Function to get saved weather data for a selected city
function getSavedWeatherData() {
    const savedCitySelect = document.getElementById("saved-city-select");
    const selectedCity = savedCitySelect.options[savedCitySelect.selectedIndex].value;
    const savedDataDiv = document.getElementById("saved-data");

    if (selectedCity === "") {
        alert("Please select a city.");
        return;
    }

    fetch(`/weather/${selectedCity}/history`, {
        method: "GET"
    })
        .then(response => response.json())
        .then(data => {
            let savedDataHtml = "";
            let emojiFrequencies = {
                "🌞": 0,
                "🌥️": 0,
                "❄️": 0
            };
            data.forEach(weatherData => {
                const timestamp = new Date(weatherData.timestamp).toLocaleString();
                savedDataHtml += `<p>${weatherData.city}: ${weatherData.temperature}°C, ${weatherData.humidity}% humidity, ${weatherData.windSpeed} m/s wind speed, ${timestamp} ${getEmoji(weatherData.temperature)}</p>`;
                const emoji = getEmoji(weatherData.temperature);
                emojiFrequencies[emoji]++;
            });
            savedDataDiv.innerHTML = savedDataHtml;

            // Determine which emoji appears most often
            let mostFrequentEmoji = "🌞";
            Object.entries(emojiFrequencies).forEach(([emoji, frequency]) => {
                if (frequency > emojiFrequencies[mostFrequentEmoji]) {
                    mostFrequentEmoji = emoji;
                }
            });

            // Change background image based on most frequent emoji
            if (mostFrequentEmoji === "🌞") {
                document.body.style.backgroundImage = "url('tropics.jpg')";
            } else if (mostFrequentEmoji === "🌥️") {
                document.body.style.backgroundImage = "url('clouds.jpg')";
            } else {
                document.body.style.backgroundImage = "url('Arctic.jpg')";
            }
        })
        .catch(error => {
            alert(error.message);
        });
}

function getEmoji(temperature) {
    if (temperature < 0) {
        return "❄️";
    } else if (temperature > 20) {
        return "🌞";
    } else {
        return "🌥️";
    }
}

// Function to delete a city from the database
function deleteCity() {
    const deleteCitySelect = document.getElementById("delete-city-select");
    const selectedCity = deleteCitySelect.options[deleteCitySelect.selectedIndex].value;


    if (selectedCity === "") {
        alert("Please select a city.");
        return;
    }

    fetch(`/deleteweather/${selectedCity}`, {
        method: "DELETE"
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            deleteCitySelect.value = "";
            populateSavedCitySelect();
            populateDeleteCitySelect();
        })
        .catch(error => {
            alert(error.message);
        });
}

// Function to populate the saved city select dropdown
function populateSavedCitySelect() {
    fetch("/cities", {
        method: "GET"
    })
        .then(response => response.json())
        .then(cities => {
            let savedCitySelectHtml = "<option value=''>Select a city</option>";
            Array.from(cities).forEach(city => {
                savedCitySelectHtml += `<option value="${city}">${city}</option>`;
            });
            savedCitySelect.innerHTML = savedCitySelectHtml;
        })
        .catch(error => {
            alert(error.message);
        });
}

// Function to populate the delete city select dropdown
// Make a fetch request to the server to get the list of saved cities
fetch('/cities')
    .then(response => response.json())
    .then(cities => {
        // Loop through the list of cities and create an option element for each one
        Array.from(cities).forEach(city => {
            const option = document.createElement('option');
            option.value = city;
            option.text = city;
            deleteCitySelect.appendChild(option);
        });
        populateDeleteCitySelect(); // call populateDeleteCitySelect after both select elements have been populated
    })
    .catch(error => console.error(error));

// Function to populate the deleteCitySelect dropdown with cities from the database
function populateDeleteCitySelect() {
    fetch("/cities", {
        method: "GET"
    })
        .then(response => response.json())
        .then(cities => {
            let deleteCitySelectHtml = "";
            Array.from(cities).forEach(city => {
                deleteCitySelectHtml += `<option value="${city}">${city}</option>`;
            });
            deleteCitySelect.innerHTML = deleteCitySelectHtml;
        })
        .catch(error => {
            alert(error.message);
        });
}
// function checkWeather() {
//     // list of cities from the database
//     fetch("/cities")
//         .then(response => response.json())
//         .then(cities => {
//             // keep track of cities that have already been checked
//             const checkedCities = new Set();
//
//             // loop cities
//             cities.forEach(city => {
//                 // Check if the city has already been checked
//                 if (!checkedCities.has(city)) {
//                     // Mark the city as checked
//                     checkedCities.add(city);
//
//                     // fetch the weather data for the city
//                     fetch(`/weather/${city}`)
//                         .then(response => response.json())
//                         .then(data => {
//                             // weather data to the console for testing
//                             console.log(data);
//                         })
//                         .catch(error => {
//                             //error to the console
//                             console.error(error);
//                         });
//                 }
//             });
//         })
//         .catch(error => {
//             // error to the console
//             console.error(error);
//         });
// }

//setInterval(checkWeather, 900000 ); // 900000 milliseconds = 15 minutes
