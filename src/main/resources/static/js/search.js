document.addEventListener("DOMContentLoaded", function () {
	const admButton = document.querySelector(".adm-button");
	const searchButton = document.querySelector(".search-button");
	const searchIcon = searchButton.querySelector("img");
	const inp = document.querySelector(".input");
	let state = { isSearch: true };

	admButton.addEventListener("click", function () {
		window.location.href = "/admin";
	});

	searchButton.addEventListener("click", function () {
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

	function handleSearch() {
		const inputValue = inp.value.trim();
		if (inputValue !== "") {
			if (state.isSearch) {
				window.location.href = "/search/results?query=" + inputValue;
			} else {
				window.location.href = "/search/sub-urls?url=" + inputValue;
			}
		}
	}

	inp.addEventListener("keypress", function (event) {
		if (event.key === "Enter") {
			handleSearch();
		}
	});
});
