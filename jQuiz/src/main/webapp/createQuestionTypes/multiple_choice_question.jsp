<label for="multipleChoiceQuestion">Question:</label><br>
<input type="text" id="multipleChoiceQuestion" name="multipleChoiceQuestion"><br><br>

<label for="multipleChoiceAnswers">Answers:</label><br>
<div id="answerContainer">
    <input type="text" id="multipleChoiceAnswer1" name="multipleChoiceAnswers"><br>
</div>
<button type="button" onclick="addChoice()">Add Choice</button><br><br>

<script>
    let choiceCount = 1;

    function addChoice() {
        choiceCount++;
        let newInput = document.createElement('input');
        newInput.type = 'text';
        newInput.id = 'multipleChoiceAnswer' + choiceCount;
        newInput.name = 'multipleChoiceAnswers';
        newInput.placeholder = 'Choice ' + choiceCount;
        document.getElementById('answerContainer').appendChild(newInput);
        document.getElementById('answerContainer').appendChild(document.createElement('br'));
    }
</script>
