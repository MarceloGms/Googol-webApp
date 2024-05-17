var stompClient = null;

function connect() {
	var socket = new SockJS("/ws");
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		console.log("Connected: " + frame);
		stompClient.subscribe("/topic/barrelUpdates", function (message) {
			// Updates for active barrels table
			var data = JSON.parse(message.body);
			updateBarrelsTable(data);
		});
		stompClient.subscribe("/topic/searchUpdates", function (message) {
			// Updates for top 10 searches table
			var data = JSON.parse(message.body);
			updateSearchesTable(data);
		});
	});
}

connect();

function updateBarrelsTable(data) {
	// Clear existing table rows
	var tableBody = document.getElementById("barrels-table-body");
	tableBody.innerHTML = "";

	// Update table
	data.forEach(function (barrel) {
		var row = tableBody.insertRow();
		var idCell = row.insertCell();
		var timeCell = row.insertCell();
		idCell.innerText = barrel.id;
		timeCell.innerText = barrel.time;
	});
}

function updateSearchesTable(data) {
	// Clear existing table rows
	var tableBody = document.getElementById("searches-table-body");
	tableBody.innerHTML = "";

	// Update table
	data.forEach(function (search) {
		var row = tableBody.insertRow();
		var queryCell = row.insertCell();
		var countCell = row.insertCell();
		queryCell.innerText = search.name;
		countCell.innerText = search.count;
	});
}
