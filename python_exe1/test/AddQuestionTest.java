

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import view.AddQuestion;

public class AddQuestionTest {

    private AddQuestion addQuestion;

    @BeforeEach
    void setUp() {
        addQuestion = new AddQuestion();
    }

    // ============================
    // VALID INPUT TEST
    // ============================
    @Test
    void testValidateInputs_valid() {
        addQuestion.idField.setText("10");
        addQuestion.questionArea.setText("What is Java?");
        addQuestion.option1Field.setText("A");
        addQuestion.option2Field.setText("B");
        addQuestion.option3Field.setText("C");
        addQuestion.option4Field.setText("D");
        addQuestion.diff1.setSelected(true);
        addQuestion.ansA.setSelected(true);

        assertDoesNotThrow(() -> addQuestion.validateInputs());
    }

    // ============================
    // ID TESTS
    // ============================
    @Test
    void testValidateInputs_missingID() {
        addQuestion.idField.setText(""); //empty ID

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Please enter Question ID.", e.getMessage());
    }

    @Test
    void testValidateInputs_invalidID() {
        addQuestion.idField.setText("abc"); // ID not number

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Invalid number format for ID.", e.getMessage());
    }
    
    @Test
    void testValidateInputs_negativeID() {
        addQuestion.idField.setText("-5");  // Negative ID 

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("ID must be a positive number.", e.getMessage());
    }

    @Test
    void testValidateInputs_zeroID() {
        addQuestion.idField.setText("0");  // ID = 0

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("ID must be a positive number.", e.getMessage());
    }


    // ============================
    // QUESTION TEXT TEST
    // ============================
    @Test
    void testValidateInputs_missingQuestionText() {
        addQuestion.idField.setText("1");
        addQuestion.questionArea.setText("");

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Please enter the question text.", e.getMessage());
    }

    // ============================
    // OPTIONS TESTS (SEPARATE)
    // ============================
    @Test
    void testValidateInputs_missingOption1() {
        addQuestion.idField.setText("1");
        addQuestion.questionArea.setText("Test?");
        addQuestion.option1Field.setText("");   //Option 1 missing
        addQuestion.option2Field.setText("B");
        addQuestion.option3Field.setText("C");
        addQuestion.option4Field.setText("D");
        addQuestion.diff1.setSelected(true);
        addQuestion.ansA.setSelected(true);

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Please fill Option 1.", e.getMessage());
    }

    @Test
    void testValidateInputs_missingOption2() {
        addQuestion.idField.setText("1");
        addQuestion.questionArea.setText("Test?");
        addQuestion.option1Field.setText("A");
        addQuestion.option2Field.setText("");   //Option 2 missing
        addQuestion.option3Field.setText("C");
        addQuestion.option4Field.setText("D");
        addQuestion.diff1.setSelected(true);
        addQuestion.ansA.setSelected(true);

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Please fill Option 2.", e.getMessage());
    }

    @Test
    void testValidateInputs_missingOption3() {
        addQuestion.idField.setText("1");
        addQuestion.questionArea.setText("Test?");
        addQuestion.option1Field.setText("A");
        addQuestion.option2Field.setText("B");
        addQuestion.option3Field.setText("");   //Option 3 missing
        addQuestion.option4Field.setText("D");
        addQuestion.diff1.setSelected(true);
        addQuestion.ansA.setSelected(true);

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Please fill Option 3.", e.getMessage());
    }

    @Test
    void testValidateInputs_missingOption4() {
        addQuestion.idField.setText("1");
        addQuestion.questionArea.setText("Test?");
        addQuestion.option1Field.setText("A");
        addQuestion.option2Field.setText("B");
        addQuestion.option3Field.setText("C");
        addQuestion.option4Field.setText("");   //Option 4 missing
        addQuestion.diff1.setSelected(true);
        addQuestion.ansA.setSelected(true);

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Please fill Option 4.", e.getMessage());
    }

    // ============================
    // DIFFICULTY TEST
    // ============================
    @Test
    void testValidateInputs_missingDifficulty() {
        addQuestion.idField.setText("1");
        addQuestion.questionArea.setText("Test");
        addQuestion.option1Field.setText("A");
        addQuestion.option2Field.setText("B");
        addQuestion.option3Field.setText("C");
        addQuestion.option4Field.setText("D");

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Please select a difficulty level.", e.getMessage());
    }

    // ============================
    // CORRECT ANSWER TEST
    // ============================
    @Test
    void testValidateInputs_missingCorrectAnswer() {
        addQuestion.idField.setText("1");
        addQuestion.questionArea.setText("Test");
        addQuestion.option1Field.setText("A");
        addQuestion.option2Field.setText("B");
        addQuestion.option3Field.setText("C");
        addQuestion.option4Field.setText("D");
        addQuestion.diff1.setSelected(true);

        Exception e = assertThrows(Exception.class, () -> addQuestion.validateInputs());
        assertEquals("Please select the correct answer.", e.getMessage());
    }

    // ============================
    // CLEAR FIELDS TEST
    // ============================
    @Test
    void testClearFields() {
        addQuestion.idField.setText("5");
        addQuestion.questionArea.setText("Test?");
        addQuestion.option1Field.setText("A");
        addQuestion.option2Field.setText("B");
        addQuestion.option3Field.setText("C");
        addQuestion.option4Field.setText("D");
        addQuestion.diff2.setSelected(true);
        addQuestion.ansA.setSelected(true);

        addQuestion.clearFields();

        assertEquals("", addQuestion.idField.getText());
        assertEquals("", addQuestion.questionArea.getText());
        assertEquals("", addQuestion.option1Field.getText());
        assertEquals("", addQuestion.option2Field.getText());
        assertEquals("", addQuestion.option3Field.getText());
        assertEquals("", addQuestion.option4Field.getText());

        assertFalse(addQuestion.diff1.isSelected());
        assertFalse(addQuestion.diff2.isSelected());
        assertFalse(addQuestion.diff3.isSelected());
        assertFalse(addQuestion.diff4.isSelected());

        assertFalse(addQuestion.ansA.isSelected());
        assertFalse(addQuestion.ansB.isSelected());
        assertFalse(addQuestion.ansC.isSelected());
        assertFalse(addQuestion.ansD.isSelected());
    }
}

