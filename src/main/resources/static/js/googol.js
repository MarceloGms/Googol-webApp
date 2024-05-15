document.addEventListener("DOMContentLoaded", function () {
	const admButton = document.querySelector(".adm-button");
	const searchButton = document.querySelector(".search-button");
	const searchIcon = searchButton.querySelector("img");
	const inp = document.querySelector(".input");
	const idx = document.querySelector(".idx");
	let state = { isSearch: true };

	admButton.addEventListener("click", function () {
		window.location.href = "/admin";
	});

	function isValidURL(url) {
		var regex = /^(ftp|http|https):\/\/[^ "]+$/;

		return regex.test(url);
	}

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
				window.location.href = "/search?query=" + inputValue;
			} else {
				if (!isValidURL(inputValue)) {
					alert("Invalid URL");
				} else window.location.href = "/urls?url=" + inputValue;
			}
		}
	}

	inp.addEventListener("keypress", function (event) {
		if (event.key === "Enter") {
			handleSearch();
		}
	});

	idx.addEventListener("keypress", function () {
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
					.then((response) => response.text())
					.then((data) => {
						console.log(data);
						alert("URL sent to the server.");
					})
					.catch((error) => {
						console.error("Error:", error);
						alert("Failed to send URL to the server.");
					});
			}
		}
	});
});
