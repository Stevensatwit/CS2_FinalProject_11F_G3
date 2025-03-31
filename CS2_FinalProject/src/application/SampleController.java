package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.Random;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SampleController {
	@FXML private ImageView rouletteImage;
	@FXML private Label balanceLabel;
	@FXML private Label resultLabel;
	
	private int balance = 1000;
	private Random random = new Random();
	private Bet currentBet;
	
	
	
	// Collection to track past spin results
	private List<Integer> spinResults = new ArrayList<>(); 
	private Map<Integer, Long> frequency = new HashMap<>();
	
	@FXML
	public void initialize() {
		String imagePath = "/images/roulette.png"; 
		try {
			Image image = new Image(getClass().getResourceAsStream(imagePath));
			rouletteImage.setImage(image);
		} catch (Exception e) {
			System.out.println("Image file not found: " + imagePath);
			e.printStackTrace();
		}
	}
	
	// Abstract Bet Class (Polymorphism)
	abstract class Bet {
		protected int betAmount;
		
		public void placeBet() {
			getBetAmount();
		}
		
		abstract boolean checkWin(int result);
		public void updateBalance(boolean win, int multiplier) {
			if (win) {
				balance += betAmount * multiplier;
			}
			updateBalanceLabel();
		}
	}
	
	// Red/Black Bet Input
	class ColorBet extends Bet {
		private String chosenColor;
		
		public ColorBet(String color) {
			this.chosenColor = color;
		}
		
		@Override 
		public boolean checkWin(int result) {
			String resultColor = (result == 0) ? "green" : (result % 2 == 0 ? "black" : "red");
			return resultColor.equals(chosenColor);
		}
	}
	
	// Green Bet Class 
	class GreenBet extends Bet {
		@Override 
		public boolean checkWin(int result) {
			return result ==  0;
		}
	}
	
	// Odd/Even Bet Class 
	class OddEvenBet extends Bet {
		private String chosenOddEven;
		
		public OddEvenBet(String choice) {
			this.chosenOddEven = choice;
		}
		
		@Override 
		public boolean checkWin(int result) {
			return result != 0 && ((result % 2 ==0 && chosenOddEven.equals("even")) ||
					(result % 2 == 1 && chosenOddEven.equals("odd")));
		}
	}
	
	// Number Bet Class
	class NumberBet extends Bet {
		private int chosenNumber;
		
		public NumberBet(int number) {
			this.chosenNumber = number;
		}
		
		@Override 
		public boolean checkWin(int result) {
			return result == chosenNumber;
		}
	}
	
	// Red/Black Bet Input
	@FXML
	private void placeRedBlackBet() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Choose Color");
		dialog.setHeaderText("Would you like to bet on Red or Black: ");
		dialog.getEditor().setText("red");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			if (value.equalsIgnoreCase("red") || value.equalsIgnoreCase("black")) {
				currentBet = new ColorBet(value.toLowerCase());
				currentBet.placeBet();
			} else {
				resultLabel.setText("Invalid color! Please choose 'Red' or 'Black'.");
			}
			
		});
	}
	
	// Green Bet Input
	@FXML
	private void placeGreenBet() {
		currentBet = new GreenBet(); 
		currentBet.placeBet();
	}
	
	// Odd/Even Bet Input
	@FXML
	private void placeOddEvenBet() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Choose Odd or Even");
		dialog.setHeaderText("Would you like to bet on Odd or Even: ");
		dialog.getEditor().setText("odd");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			if (value.equalsIgnoreCase("odd") || value.equalsIgnoreCase("even")) {
				currentBet = new OddEvenBet(value.toLowerCase());
				currentBet.placeBet();
			} else {
				resultLabel.setText("Invalid choice! Please choose 'Odd' or 'Even'.");
			}
		});
	}
	
	// Number Bet Input
	@FXML 
	private void placeNumberBet() {
		getBetAmount();
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Number Bet");
		dialog.setHeaderText("Choose a number (0-36): ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			try {
				int number = Integer.parseInt(value);
				if (number >= 0 && number <= 36) { // validate the input
					currentBet = new NumberBet(number);
					currentBet.placeBet();
				} else {
					resultLabel.setText("Invalid number! Choose a number from 0 - 36.");
				}
			} catch (NumberFormatException e) {
				resultLabel.setText("Invalid input! Enter a number.");
			}
			
		});
	}
	
	// Recursion method for bet amount input
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
					getBetAmount(); //Recursively call if invalid input
				} else if (amount <= 0) {
					resultLabel.setText("Bet must be greater than 0!");
					getBetAmount(); //Recursively call if invalid input
				} else {
					currentBet.betAmount = amount;
					balance -= amount;
					updateBalanceLabel();
				}
			} catch (NumberFormatException e) {
				resultLabel.setText("Invalid input! Enter a number.");
				getBetAmount(); //Recursively call if input is not a valid number
			}
			
		});
		
		
	}
	
	
	@FXML
	private void spinWheel() {
		if (currentBet == null) {
			resultLabel.setText("No bet placed!");
			return;
		}
		
		int result = random.nextInt(37); //Random number between 0 and 36
		boolean win = currentBet.checkWin(result);
		int multiplier = (currentBet instanceof NumberBet) ? 36 : (currentBet instanceof GreenBet ? 14 : 2);
		currentBet.updateBalance(win, multiplier);
		
		String resultColor = (result == 0) ? "green" : (result % 2 == 0 ? "black" : "red");
		resultLabel.setText(win ? "You win! The ball landed on " + resultColor + " " + result
								: "You lose! The ball landed on " + resultColor + " " + result);
		
		displayMostFrequentNumber(result);
		currentBet = null; // Reset bet after spin
	}	
	
	// Display the most frequent number spun
	private void displayMostFrequentNumber(int result) {
		spinResults.add(result); // stores the result of the current spin
		
		// Update frequency map with the current spin results
		frequency = spinResults.stream()
				.collect(Collectors.groupingBy(n -> n, Collectors.counting()));
	
			
		Optional<Map.Entry<Integer, Long>> mostFrequentSpin = frequency.entrySet().stream()
				.max(Map.Entry.comparingByValue());
			
		//Displays most frequent number in the console
		mostFrequentSpin.ifPresent(entry -> {
			String frequencyMessage = entry.getValue() > 1
				? "appeared " + entry.getValue() + " times."
				: "appeared " + entry.getValue() + " time.";
			System.out.println("Most frequent number: " + entry.getKey() + " " + frequencyMessage);
		});
	}
	

	private void updateBalanceLabel() {
		balanceLabel.setText("Balance: $" + balance);
	}
	
	
}
