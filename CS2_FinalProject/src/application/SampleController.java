package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.Random;

public class SampleController {
	@FXML private ImageView rouletteImage;
	@FXML private Label balanceLabel;
	@FXML private Label resultLabel;
	
	private int balance = 1000;
	private Random random = new Random();
	private String betType;
	private int betAmount;
	private int chosenNumber;
	private String chosenColor;
	private String chosenOddEven;
	
	@FXML
	public void initialize() {
		String imagePath = "/images/roulette.png"; 
		try {
			Image image = new Image(getClass().getResourceAsStream(imagePath));
			if (image.isError()) {
				System.out.println("Error loading image: " + imagePath);
			} else {
				rouletteImage.setImage(image);
				System.out.println("Image loaded successfully!");
			}
		} catch (Exception e) {
			System.out.println("Image file not found: " + imagePath);
			e.printStackTrace();
		}
	}
	
	@FXML
	private void placeRedBlackBet() {
		betType = "color";
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Choose Color");
		dialog.setHeaderText("Would you like to bet on Red or Black: ");
		dialog.getEditor().setText("red");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			if (value.equalsIgnoreCase("red") || value.equalsIgnoreCase("black")) {
				chosenColor = value.toLowerCase();
				getBetAmount();
			} else {
				resultLabel.setText("Invalid color! Please choose 'Red' or 'Black'.");
			}
			
		});
	}
	
	@FXML
	private void placeGreenBet() {
		betType = "green"; // Set bet type to green 
		getBetAmount();
	}
	
	@FXML
	private void placeOddEvenBet() {
		betType = "oddEven";
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Choose Odd or Even");
		dialog.setHeaderText("Would you like to bet on Odd or Even: ");
		dialog.getEditor().setText("odd");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			if (value.equalsIgnoreCase("odd") || value.equalsIgnoreCase("even")) {
				chosenOddEven = value.toLowerCase();
				getBetAmount();
			} else {
				resultLabel.setText("Invalid choice! Please choose 'Odd' or 'Even'.");
			}
		});
	}
	
	@FXML 
	private void placeNumberBet() {
		betType = "number";
		getBetAmount();
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Number Bet");
		dialog.setHeaderText("Choose a number (0-36): ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			int number = Integer.parseInt(value);
			if (number >= 0 && number <= 36) { // validate the input
				chosenNumber = number;
			} else {
				resultLabel.setText("Invalid number! Choose a number from 0 - 36.");
			}
		});
	}
	
	private void getBetAmount() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Place Bet");
		dialog.setHeaderText("Enter your bet amount: ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			try {
				int amount = Integer.parseInt(value);
				if (amount > balance) {
					resultLabel.setText("Not enough balance!");
				} else if (amount <= 0) {
					resultLabel.setText("Bet must be greater than 0!");
				} else {
					betAmount = amount;
					balance -= betAmount;
					updateBalance();
				}
			} catch (NumberFormatException e) {
				resultLabel.setText("Invalid input! Enter a number.");
			}
			
		});
		
		
	}
	
	
	private void updateBalance() {
		balanceLabel.setText("Balance: $" + balance);
	}
	
	@FXML
	private void spinWheel() {
		int result = random.nextInt(37); //Random number between 0 and 36
		String resultColor;
		
		if (result == 0) {
			resultColor = "green"; // Green for zero
		} else {
			resultColor = (result % 2 == 0) ? "black" : "red"; // Even = black, Odd = red
		}
		
		// Red or Black bet
		if (betType.equals("color")) {
			if (result == 0) {
				resultLabel.setText("You lose! The ball landed on GREEN 0.");
			} else if (resultColor.equals(chosenColor)) {
				balance += betAmount * 2;
				resultLabel.setText("You win! The ball landed on " + resultColor + " " + result);
			} else {
				resultLabel.setText("You lose! The ball landed on " + resultColor + " " + result);
			}
		} 
		
		// Odd and Even bet
		else if (betType.equals("oddEven")) {
			if (result == 0) {
				resultLabel.setText("You lose! The ball landed on GREEN 0.");
			} else if ((result % 2 == 0 && chosenOddEven.equals("even")) || (result % 2 == 1 && chosenOddEven.equals("odd"))) {
				balance += betAmount * 2;
				resultLabel.setText("You win! The ball landed on " + (result % 2 == 0 ? "even" : "odd") + " " + result);
			} else {
				resultLabel.setText("You lose! The ball landed on " + (result % 2 == 0 ? "even" : "odd") + " " + result);
			}
		} 
		
		// Number bet
		else if (betType.equals("number")) {
			if (result == chosenNumber) {
				balance += betAmount * 36;
				resultLabel.setText("Jackpot! You win! The ball landed on " + result);
			} else {
				resultLabel.setText("You lose! The ball landed on " + result);
			}
		} 
		
		// Green bet
		else if (betType.equals("green")) {
			if (result == 0) {
				balance += betAmount * 14;
				resultLabel.setText("You win! The ball landed on Green 0!");
			} else {
				resultLabel.setText("You lose! The ball landed on " + resultColor + " " + result);
			}
		}
		
		updateBalance();
		
	}
	
}
