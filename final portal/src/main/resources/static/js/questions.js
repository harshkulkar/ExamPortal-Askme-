var questions_table;
var candidates_table;
$(document).ready(function() {
	questions_table = $('#questions-data-table').DataTable({
		responsive: true,
		stateSave: true
	});
	candidates_table = $('#candidates-data-table').DataTable({
		responsive: true,
		stateSave: true
	});



});

function initModals(questions) {
	$('#questions-data-table tbody').on('click', '.editBtn', function() {
		var closestRow = $(this).closest('tr');
		var data = questions_table.row($(this).parents(closestRow)).data();
		var question_number = data[0];
		var question = data[1];
		var points = data[2];
		var questionId = data[3];
		var answerId1 = data[4];
		var answerId2 = data[5];
		var answerId3 = data[6];
		var answerId4 = data[7];
		$("#editAnswerId1").val(answerId1);
		$("#editAnswerId2").val(answerId2);
		$("#editAnswerId3").val(answerId3);
		$("#editAnswerId4").val(answerId4);
		$("#editQuestionId").val(questionId);
		$("#questionTobeEdited").val(question);
		$('h5.modal-title').html('Edit Question: ' + question_number);
		$('#editQuestion').val(question);
		for (let i = 0; i < questions.length; i++) {
			if (questions[i].text.trim() == question.trim()) {
				$('#editOption1').val(questions[i].answers[0].text);
				if (questions[i].answers[0].correct == true) $('#editCorrectAnswers option:eq(0)').prop('selected', true).change();
				$('#editOption2').val(questions[i].answers[1].text);
				if (questions[i].answers[1].correct == true) $('#editCorrectAnswers option:eq(1)').prop('selected', true).change();
				$('#editOption3').val(questions[i].answers[2].text);
				if (questions[i].answers[2].correct == true) $('#editCorrectAnswers option:eq(2)').prop('selected', true).change();
				$('#editOption4').val(questions[i].answers[3].text);
				if (questions[i].answers[3].correct == true) $('#editCorrectAnswers option:eq(3)').prop('selected', true).change();
			}
		}
		$('#editPoints').val(points);
		$('#editModal').modal('show');
	});
	//  reset modal on hiding to prevent data overlap
	$("#editModal").on('hidden.bs.modal', function() {
		$("#editForm").trigger("reset");
	});
	$('#questions-data-table tbody').on('click', '.deleteBtn', function() {
		var closestRow = $(this).closest('tr');
		var data = questions_table.row($(this).parents(closestRow)).data();
		var question_number = data[0];
		var question_Id = data[3];
		$('#confirmQuestionNumber').html(question_number);
		$('#deleteQuestionId').val(question_Id);
		$('#deleteModal').modal('show');
	});
}
