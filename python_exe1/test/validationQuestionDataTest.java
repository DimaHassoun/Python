import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.QuestionManagerLogic;
public class validationQuestionDataTest {

    @BeforeEach
    void setUp() {
        QuestionManagerLogic.lastErrorMessage = "";
    }

    @Test
    void testValidateQuestionData_valid() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "10",
                "What is Java?",
                "A", "B", "C", "D",
                true,
                true
        );

        assertTrue(result);
        assertEquals("", QuestionManagerLogic.lastErrorMessage);
    }

    // ============================
    // ID TESTS
    // ============================
    @Test
    void testValidateQuestionData_missingID() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "",
                "Question?",
                "A", "B", "C", "D",
                true,
                true
        );

        assertFalse(result);
        assertEquals("Please enter Question ID.", QuestionManagerLogic.lastErrorMessage);
    }

    @Test
    void testValidateQuestionData_invalidID() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "abc",
                "Question?",
                "A", "B", "C", "D",
                true,
                true
        );

        assertFalse(result);
        assertEquals("Invalid number format for ID.", QuestionManagerLogic.lastErrorMessage);
    }

    @Test
    void testValidateQuestionData_negativeID() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "-5",
                "Question?",
                "A", "B", "C", "D",
                true,
                true
        );

        assertFalse(result);
        assertEquals("ID must be a positive number.", QuestionManagerLogic.lastErrorMessage);
    }
    
    @Test
    void testValidateQuestionData_zeroID() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "0",
                "Question?",
                "A", "B", "C", "D",
                true,
                true
        );

        assertFalse(result);
        assertEquals("ID must be a positive number.", QuestionManagerLogic.lastErrorMessage);
    }

    // ============================
    // QUESTION TEXT TEST 
    // ============================
    @Test
    void testValidateQuestionData_missingQuestionText() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "1",
                "",
                "A", "B", "C", "D",
                true,
                true
        );

        assertFalse(result);
        assertEquals("Please enter the question text.", QuestionManagerLogic.lastErrorMessage);
    }

    // ============================
    // OPTIONS TESTS
    // ============================
    @Test
    void testValidateQuestionData_missingOption1() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "1",
                "Test?",
                "",
                "B", "C", "D",
                true,
                true
        );

        assertFalse(result);
        assertEquals("Please fill Option A.", QuestionManagerLogic.lastErrorMessage);
    }

    @Test
    void testValidateQuestionData_missingOption2() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "1",
                "Test?",
                "A",
                "",
                "C", "D",
                true,
                true
        );

        assertFalse(result);
        assertEquals("Please fill Option B.", QuestionManagerLogic.lastErrorMessage);
    }

  @Test
    void testValidateQuestionData_missingOption3() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "1",
                "Test?",
                "A", "B",
                "",
                "D",
                true,
                true
        );

        assertFalse(result);
        assertEquals("Please fill Option C.", QuestionManagerLogic.lastErrorMessage);
    }

    @Test
    void testValidateQuestionData_missingOption4() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "1",
                "Test?",
                "A", "B", "C",
                "",
                true,
                true
        );

        assertFalse(result);
        assertEquals("Please fill Option D.", QuestionManagerLogic.lastErrorMessage);
    }

    // ============================
    // DIFFICULTY TEST
    // ============================
    @Test
    void testValidateQuestionData_missingDifficulty() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "1",
                "Test?",
                "A", "B", "C", "D",
                false,
                true
        );

        assertFalse(result);
        assertEquals("Please select a difficulty level.", QuestionManagerLogic.lastErrorMessage);
    }

    // ============================
    // CORRECT ANSWER TEST 
    // ============================
    @Test
    void testValidateQuestionData_missingCorrectAnswer() {
        boolean result = QuestionManagerLogic.validateQuestionData(
                "1",
                "Test?",
                "A", "B", "C", "D",
                true,
                false
        );

        assertFalse(result);
        assertEquals("Please select the correct answer.", QuestionManagerLogic.lastErrorMessage);
    }
}

