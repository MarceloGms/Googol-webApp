// Wait for the DOM content to be loaded before executing the script
document.addEventListener("DOMContentLoaded", function () {
	const admButton = document.querySelector(".adm-button");
	const searchButton = document.querySelector(".search-button");
	const searchIcon = searchButton.querySelector("img");
	const inp = document.querySelector(".input");
	const idx = document.querySelector(".idx");
	let state = { isSearch: true }; // State variable to track search mode

	// Event listener for admin button click
	admButton.addEventListener("click", function () {
		// Redirect to admin page
		window.location.href = "/admin";
	});

	// Function to check if a URL is valid
	function isValidURL(url) {
		var regex = /^(ftp|http|https):\/\/[^ "]+$/;
		return regex.test(url);
	}

	// Event listener for search button click
	searchButton.addEventListener("click", function () {
		// Toggle between search and URL mode
		if (searchIcon.src.endsWith("/img/search.png")) {
			searchIcon.src = "/img/url.png";
			inp.placeholder = "Enter URL...";
			state = { isSearch: false };
		} else {
			searchIcon.src = "/img/search.png";
			inp.placeholder = "Search...";
			state = { isSearch: true };
		}
		searchButton.classList.toggle("clicked");
	});

	// Function to handle search
	function handleSearch() {
		const inputValue = inp.value.trim();
		if (inputValue !== "") {
			if (state.isSearch) {
				// Redirect to search page
				window.location.href = "/search?query=" + inputValue;
			} else {
				if (!isValidURL(inputValue)) {
					alert("Invalid URL");
				} else {
					// Redirect to URLs page
					window.location.href = "/urls?url=" + inputValue;
				}
			}
		}
	}

	// Event listener for Enter key press on search input
	inp.addEventListener("keypress", function (event) {
		if (event.key === "Enter") {
			handleSearch();
		}
	});

	// Event listener for Enter key press on URL input
	idx.addEventListener("keypress", function (event) {
		if (event.key === "Enter") {
			const idxValue = idx.value.trim();
			if (!isValidURL(idxValue)) {
				alert("Invalid URL");
				return;
			}
			if (idxValue !== "") {
				// Send the URL to the server via REST
				fetch("/sendUrl", {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify({ url: idxValue }),
				})
					.then((response) => {
						if (!response.ok) {
							throw new Error("Failed to send URL to the server.");
						}
						return;
					})
					.then(() => {
						alert("URL sent to the server.");
					})
					.catch((error) => {
						console.error("Error:", error);
						alert("Failed to send URL to the server. Gateway may be down.");
					});
			}
		}
	});
});
