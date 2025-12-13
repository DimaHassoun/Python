package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import Model.Consts;

public class QuestionManagerLogic {

	//Load CSV into table model (including headers from first row)
	public static DefaultTableModel loadCSVToTable(String csvPath) throws IOException {
		DefaultTableModel model = new DefaultTableModel();

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(csvPath), "UTF-8")
				)) {
			String line;
			boolean firstLine = true;

			while ((line = br.readLine()) != null) {
				// Split the line by commas and handle quoted text
				String[] values = parseCSVLine(line);

				if (firstLine) {
					// Remove BOM from first header if present
					if (values.length > 0 && values[0].length() > 0 && values[0].charAt(0) == '\uFEFF') {
						values[0] = values[0].substring(1);
					}
					Vector<String> columns = new Vector<>();
					for (String value : values) columns.add(value);
					model.setColumnIdentifiers(columns);
					firstLine = false;
				} else {
					Vector<String> row = new Vector<>();
					for (String value : values) row.add(value);
					model.addRow(row);
				}
			}
		}

		return model;
	}


	//Add a question to CSV 
	public static boolean addToCSV(int id, String question, int difficulty,
			String option1, String option2, String option3, String option4,
			char correctAnswer) throws IOException {

		String filePath = Consts.getCSVPath();

		if (isIdExistsInCSV(filePath, id)) {
			throw new RuntimeException("ID already exists in CSV. Please choose another ID.");
		}
		String[] line = new String[] {
				String.valueOf(id),
				question,
				String.valueOf(difficulty),
				option1,
				option2,
				option3,
				option4,
				String.valueOf(correctAnswer)
		};
		try (PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8")
				)) {
			pw.println(toCSVLine(line));
		}
		return true;
	}

	//Check if ID already exists in CSV
	public static boolean isIdExistsInCSV(String filePath, int idToCheck) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) return false;

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF-8")
				)) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) { firstLine = false; continue; } // Skip header
				String[] values = parseCSVLine(line);
				if (values.length > 0) {
					try {
						int existingId = Integer.parseInt(values[0].trim());
						if (existingId == idToCheck) return true;
					} catch (NumberFormatException ignored) {}
				}
			}
		}

		return false;// no duplicate found
	}

	//Save improved version of table to CSV (after edit or delete)
	public static void saveTableToCSV(DefaultTableModel model, String csvPath) throws IOException {
		try (PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(csvPath), "UTF-8")
				)) {
			// Write header
			int colCount = model.getColumnCount();
			String[] header = new String[colCount];
			for (int i = 0; i < colCount; i++) {
				header[i] = model.getColumnName(i);
			}
			pw.println(toCSVLine(header));

			// Write rows
			for (int row = 0; row < model.getRowCount(); row++) {
				String[] rowData = new String[colCount];
				for (int col = 0; col < colCount; col++) {
					Object val = model.getValueAt(row, col);
					rowData[col] = (val != null ? val.toString() : "");
				}
				pw.println(toCSVLine(rowData));
			}
		}
	}

	//Convert array of strings to CSV line with escape handling 
	private static String toCSVLine(String[] values) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			sb.append(escape(values[i]));
			if (i < values.length - 1) sb.append(",");
		}
		return sb.toString();
	}

	//Escape commas and quotes 
	private static String escape(String s) {
		if (s == null) return "";
		if (s.contains(",") || s.contains("\"")) {
			s = s.replace("\"", "\"\""); // Double quotes
			return "\"" + s + "\"";      // wrap in quotes
		}
		return s;
	}


	//Parse CSV line into array of strings and unescape quotes
	private static String[] parseCSVLine(String line) {
		if (line == null || line.isEmpty()) return new String[0];
		Vector<String> values = new Vector<>();
		boolean inQuotes = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '"') {
				if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
					// Double quote => " inside text
					sb.append('"');
					i++;
				} else {
					inQuotes = !inQuotes; // Start/end of quote
				}
			} else if (c == ',' && !inQuotes) {
				values.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		values.add(sb.toString());
		return values.toArray(new String[0]);
	}

	//search Question By ID
	public static int searchQuestionByID(DefaultTableModel model, String searchID) {
		if (searchID == null || searchID.trim().isEmpty())
			return -1;

		for (int i = 0; i < model.getRowCount(); i++) {
			Object idObj = model.getValueAt(i, 1); // The ID column inside the deletion table
			if (idObj != null && idObj.toString().equalsIgnoreCase(searchID.trim())) {
				return i; // return the row number
			}
		}
		return -1; // ID not found
	}
	//For QQuation
	private static String Question_ID;
	private static String difficulty;
	private static String QuestionString;
	private static String aString;
	private static String bString;
	private static String cString;
	private static String dString;
	private static String Correct_Answer;

	//Dima --- New function to get a random question row ---
	public static String[] getRandomQuestion(String csvPath) throws IOException {
		DefaultTableModel model = loadCSVToTable(csvPath);
		int rowCount = model.getRowCount();
		if (rowCount == 0) return null; // no questions available

		Random rand = new Random();
		int randomRow = rand.nextInt(rowCount); // 0..rowCount-1

		int colCount = model.getColumnCount();
		String[] questionData = new String[colCount];
		for (int col = 0; col < colCount; col++) {
			Object val = model.getValueAt(randomRow, col);
			questionData[col] = (val != null ? val.toString() : "");
		}
		return questionData;
	}
	public static String fillQuestion() {
		try {
			String csvPath = Consts.getCSVPath();
			String[] randomQuestion =
					QuestionManagerLogic.getRandomQuestion(csvPath);

			if (randomQuestion == null) {
				return "No questions found in the CSV file.";
			}

			Question_ID = randomQuestion[0];
			difficulty = randomQuestion[2];
			QuestionString = randomQuestion[1];
			aString = randomQuestion[3];
			bString = randomQuestion[4];
			cString = randomQuestion[5];
			dString = randomQuestion[6];
			Correct_Answer = randomQuestion[7];

			return null; // ✅ success

		} catch (Exception e) {
			return "Failed to load question: " + e.getMessage();
		}
	}

	//Getters Setters
	public static String getQuestion_ID() {
		return Question_ID;
	}

	public static String getDifficulty() {
		return difficulty;
	}

	public static String getQuestionString() {
		return QuestionString;
	}

	public static String getaString() {
		return aString;
	}

	public static String getbString() {
		return bString;
	}

	public static String getcString() {
		return cString;
	}

	public static String getdString() {
		return dString;
	}

	public static String getCorrect_Answer() {
		return Correct_Answer;
	}


}
