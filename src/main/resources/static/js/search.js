const results = document.querySelectorAll(".resultItem");
const prevButton = document.getElementById("prevPage");
const nextButton = document.getElementById("nextPage");
const currentPageDisplay = document.getElementById("currentPage");

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
