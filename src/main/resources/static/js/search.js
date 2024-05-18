// Execute when the DOM content is loaded
document.addEventListener("DOMContentLoaded", () => {
	const results = document.querySelectorAll(".resultItem");
	const prevButton = document.getElementById("prevPage");
	const nextButton = document.getElementById("nextPage");
	const hackerNewsButton = document.getElementById("hackerNews");
	const adviceButton = document.getElementById("advice");
	const currentPageDisplay = document.getElementById("currentPage");
	const query = window.location.search.split("=")[1];

	// Initialize current page
	let currentPage = 1;

	// Check if there are results available
	if (
		groupData !== null &&
		groupData !== "No results found." &&
		groupData !== "No barrels available." &&
		groupData !== "Error occurred during search."
	) {
		const resultsPerPage = 10;
		const totalPages = Math.ceil(results.length / resultsPerPage);

		// Function to display a page of results
		function showPage(page) {
			for (let i = 0; i < results.length; i++) {
				results[i].style.display = "none";
			}
			const startIndex = (page - 1) * resultsPerPage;
			const endIndex = startIndex + resultsPerPage;
			for (let i = startIndex; i < endIndex && i < results.length; i++) {
				results[i].style.display = "block";
			}
			currentPageDisplay.textContent = `Page ${page}`;
		}

		// Show the initial page
		showPage(currentPage);

		// Event listener for previous page button
		prevButton.addEventListener("click", () => {
			if (currentPage > 1) {
				currentPage--;
				showPage(currentPage);
			}
		});

		// Event listener for next page button
		nextButton.addEventListener("click", () => {
			if (currentPage < totalPages) {
				currentPage++;
				showPage(currentPage);
			}
		});
	}

	// Event listener for Hacker News button click
	hackerNewsButton.addEventListener("click", () => {
		// Send query to server to fetch Hacker News stories
		fetch("/sendHackerNews", {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({ query: query }),
		})
			.then((response) => {
				if (!response.ok) {
					return response.text().then((text) => {
						throw new Error(text);
					});
				}
				return response.text();
			})
			.then((message) => {
				alert("Success: " + message);
			})
			.catch((error) => {
				console.error("Error:", error);
				alert("Failed: " + error.message);
			});
	});

	// Event listener for Advice button click
	adviceButton.addEventListener("click", () => {
		// Fetch random advice from server
		fetch("/advice", {
			method: "GET",
			headers: {
				"Content-Type": "application/json",
			},
		})
			.then((response) => {
				if (!response.ok) {
					throw new Error("Network response was not ok " + response.statusText);
				}
				return response.text();
			})
			.then((advice) => {
				alert(advice);
			})
			.catch((error) => {
				console.error(
					"There has been a problem with your fetch operation:",
					error
				);
			});
	});
});
