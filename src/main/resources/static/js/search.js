const results = document.querySelectorAll(".resultItem");
const prevButton = document.getElementById("prevPage");
const nextButton = document.getElementById("nextPage");
const currentPageDisplay = document.getElementById("currentPage");
const HackerNewsButton = document.getElementById("fixedButton");

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

HackerNewsButton.addEventListener("click", () => {
	window.location.href = "https://news.ycombinator.com/";
});
