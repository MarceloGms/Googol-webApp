const results = document.querySelectorAll(".resultItem");
const prevButton = document.getElementById("prevPage");
const nextButton = document.getElementById("nextPage");
const hackerNewsButton = document.getElementById("hackerNews");
const adviceButton = document.getElementById("advice");
const currentPageDisplay = document.getElementById("currentPage");
const query = window.location.search.split("=")[1];

let currentPage = 1;
const resultsPerPage = 10;
const totalPages = Math.ceil(results.length / resultsPerPage);

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

showPage(currentPage);

prevButton.addEventListener("click", () => {
	if (currentPage > 1) {
		currentPage--;
		showPage(currentPage);
	}
});

nextButton.addEventListener("click", () => {
	if (currentPage < totalPages) {
		currentPage++;
		showPage(currentPage);
	}
});

hackerNewsButton.addEventListener("click", () => {
	fetch("/sendHackerNews", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({ query: query }),
	})
		.then((response) => {
			if (!response.ok) {
				throw new Error("Failed access hacker news API.");
			}
			return;
		})
		.then(() => {
			alert("Success.");
		})
		.catch((error) => {
			console.error("Error:", error);
			alert("Failed access hacker news API.");
		});
});

adviceButton.addEventListener("click", () => {
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
