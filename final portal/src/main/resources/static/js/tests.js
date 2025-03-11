var table;
$(document).ready(function() {
	table = $('#tests-data-table').DataTable({
		responsive: true,
		stateSave: true,
		refresh: true
	});

	$('#tests-data-table tbody').on('click', '.editBtn', function() {
		var closestRow = $(this).closest('tr');
		var data = table.row($(this).parents(closestRow)).data();
		var test_name = data[1];
		var time = data[3];
		var test_Id = data[4];
		//Prefill the fields with the gathered information
		$('h5.modal-title').html('Edit Test: ' + test_name);
		$('#editTestId').val(test_Id);
		$('#editTestName').val(test_name);
		$('#editTime').val(time);
		$('#editModal').modal('show');
	});

	$('#tests-data-table tbody').on('click', '.deleteBtn', function() {
		var closestRow = $(this).closest('tr');
		var data = table.row($(this).parents(closestRow)).data();

		var test_name = data[1];
		var test_Id = data[4];


		$('#confirmTestName').html(test_name);
		$('#deleteTestId').val(test_Id);
		$('#deleteModal').modal('show');
	});


});