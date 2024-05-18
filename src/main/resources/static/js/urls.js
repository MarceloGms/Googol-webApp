// Execute when the DOM content is loaded
document.addEventListener("DOMContentLoaded", () => {
	const results = document.querySelectorAll(".resultItem");
	const prevButton = document.getElementById("prevPage");
	const nextButton = document.getElementById("nextPage");
	const currentPageDisplay = document.getElementById("currentPage");

	// Initialize current page
	let currentPage = 1;

	// Check if there are results available
	if (
		urlsData !== null &&
		urlsData !== "No results found." &&
		urlsData !== "No barrels available." &&
		urlsData !== "Error occurred getting sub links." &&
		urlsData !== "Invalid URL."
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
});
