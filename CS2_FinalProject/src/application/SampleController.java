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
import java.io.InputStream;
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
	private List<Bet> currentBets = new ArrayList<>(); // Collection to store multiple bets
	
	// Collection to track past spin results
	private List<Integer> spinResults = new ArrayList<>(); // Collection to store spin results
	private Map<Integer, Long> frequency = new HashMap<>(); // Track frequency of each result
	
	private final Map<Integer, String> numberToColor = Map.ofEntries(
			Map.entry(0, "green"), Map.entry(1, "red"), Map.entry(2, "black"), Map.entry(3, "red"), 
			Map.entry(4, "black"), Map.entry(5, "red"), Map.entry(6, "black"), Map.entry(7, "red"),
			Map.entry(8, "black"), Map.entry(9, "red"), Map.entry(10, "black"), Map.entry(11, "black"), 
			Map.entry(12, "red"), Map.entry(13, "black"), Map.entry(14, "red"), Map.entry(15, "black"), 
			Map.entry(16, "red"), Map.entry(17, "black"), Map.entry(18, "red"), Map.entry(19, "red"), 
			Map.entry(20, "black"), Map.entry(21, "red"), Map.entry(22, "black"), Map.entry(23, "red"), 
			Map.entry(24, "black"), Map.entry(25, "red"), Map.entry(26, "black"), Map.entry(27, "red"), 
			Map.entry(28, "black"), Map.entry(29, "black"), Map.entry(30, "red"), Map.entry(31, "black"), 
			Map.entry(32, "red"), Map.entry(33, "black"), Map.entry(34, "red"), Map.entry(35, "black"), 
			Map.entry(36, "red")
		);
	
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
			getBetAmount(this);
		}
		
		abstract boolean checkWin(int result);
		abstract int getMultiplier();
	}
	
	// First-12, Second-12, Final-12 Bet Class
	class DozenBet extends Bet {
		private int dozen;
		
		public DozenBet(int dozen) {
			this.dozen = dozen;
		}
		
		@Override 
		public boolean checkWin(int result) {
			return result >= (dozen - 1) * 12 + 1 && result <= dozen * 12;
		}
		
		@Override
		public int getMultiplier() {
			return 3; // Multiplier for Dozen Bet
		}
	}
	
	// 1 to 18 / 19 to 36 Bet Class
	class RangeBet extends Bet {
		private int lowerBound, upperBound;
		
		public RangeBet(int lowerBound, int upperBound) {
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}
		
		@Override 
		public boolean checkWin(int result) {
			return result >= lowerBound && result <= upperBound;
		}
		
		@Override 
		public int getMultiplier() {
			return 2; // Multiplier for Range Bet
		}
	}
	
	
	// 2 to 1 Column Bet
	class ColumnBet extends Bet {
		private int column;
		
		public ColumnBet(int column) {
			this.column = column; 
		}
		
		@Override 
		public boolean checkWin(int result) {
			return result != 0 && (result - 1) % 3 == column -1;
		}
		
		@Override 
		public int getMultiplier() {
			return 3; // Multiplier for Columnn Bet
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
			return numberToColor.get(result).equals(chosenColor);
		}
		
		@Override 
		public int getMultiplier() {
			return 2; // Multiplier of Red/Black bet
		}
	}
	
	// Green Bet Class 
	class GreenBet extends Bet {
		@Override 
		public boolean checkWin(int result) {
			return result ==  0;
		}
		
		@Override 
		public int getMultiplier() {
			return 36; // Multiplier for Green bet
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
		
		@Override 
		public int getMultiplier() {
			return 2; // Multiplier for Odd/Even bet
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
		
		@Override 
		public int getMultiplier() {
			return 36; // Multiplier for Number Bet
		}
	}
	
	// Place Dozen Bet
	@FXML 
	private void placeDozenBet() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Dozen Bet");
		dialog.setHeaderText("Choose First (1), Second (2), or Third (3) Dozen: ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> { 
			try {
				int dozen = Integer.parseInt(value);
				if (dozen >= 1 && dozen <= 3) {
					currentBets.add(new DozenBet(dozen));
					currentBets.get(currentBets.size() -1).placeBet();
				} else {
					resultLabel.setText("Invalid choice! Choose 1, 2, or 3."); 
				}
			} catch (NumberFormatException e) {
				resultLabel.setText("Invalid input! Enter 1, 2, or 3.");
			}
		});
	}
		
	// Place Range Bet
	@FXML
	private void placeRangeBet() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Range Bet");
		dialog.setHeaderText("Choose 1-18 or 19-36: ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> { 
			if (value.equals("1-18")) {
				currentBets.add(new RangeBet(1, 18));
			} else if (value.equals("19-36")) {
				currentBets.add(new RangeBet(19, 36));
			} else {
				resultLabel.setText("Invalid choice! Choose '1-18' or '19-36'."); 
				return;
			}
			currentBets.get(currentBets.size() - 1).placeBet();
		});
	}
		
	// Place Column Bets
	@FXML 
	private void placeColumnBet() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Column Bet");
		dialog.setHeaderText("Choose column (1,  2, or 3): ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> { 
			try {
				int column = Integer.parseInt(value);
				if (column >= 1 && column <= 3) {
					currentBets.add(new ColumnBet(column));
					currentBets.get(currentBets.size() -1).placeBet();
				} else {
					resultLabel.setText("Invalid choice! Choose 1, 2, or 3."); 
				}
			} catch (NumberFormatException e) {
				resultLabel.setText("Invalid input! Enter 1, 2, or 3.");
			}
		});
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
				currentBets.add(new ColorBet(value.toLowerCase()));
				currentBets.get(currentBets.size() - 1).placeBet();
			} else {
				resultLabel.setText("Invalid color! Please choose 'Red' or 'Black'.");
			}
			
		});
	}
	
	// Green Bet Input
	@FXML
	private void placeGreenBet() {
		currentBets.add(new GreenBet()); 
		currentBets.get(currentBets.size() - 1).placeBet();
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
				currentBets.add(new OddEvenBet(value.toLowerCase()));
				currentBets.get(currentBets.size() - 1).placeBet();
			} else {
				resultLabel.setText("Invalid choice! Please choose 'Odd' or 'Even'.");
			}
		});
	}
	
	// Number Bet Input
	@FXML 
	private void placeNumberBet() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Number Bet");
		dialog.setHeaderText("Choose a number (0-36): ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			try {
				int number = Integer.parseInt(value);
				if (number >= 0 && number <= 36) { // validate the input
					currentBets.add(new NumberBet(number));
					currentBets.get(currentBets.size() - 1).placeBet();
				} else {
					resultLabel.setText("Invalid number! Choose a number from 0 - 36.");
				}
			} catch (NumberFormatException e) {
				resultLabel.setText("Invalid input! Enter a number.");
			}
			
		});
	}
	
	private void placeSpecificBet(Bet bet) { 
		currentBets.add(bet);
		bet.placeBet();
	}
	
	// Recursion method for bet amount input
	private void getBetAmount(Bet bet) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Place Bet");
		dialog.setHeaderText("Enter your bet amount: ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			try {
				int amount = Integer.parseInt(value);
				if (amount > balance) {
					resultLabel.setText("Not enough balance!");
					getBetAmount(bet); //Recursively call if invalid input
				} else if (amount <= 0) {
					resultLabel.setText("Bet must be greater than 0!");
					getBetAmount(bet); //Recursively call if invalid input
				} else {
					bet.betAmount = amount;
					balance -= amount;
					updateBalanceLabel();
				}
			} catch (NumberFormatException e) {
				resultLabel.setText("Invalid input! Enter a number.");
				getBetAmount(bet); //Recursively call if input is not a valid number
			}
			
		});
		
	}
	
	
	@FXML
	private void spinWheel() {
		if (currentBets.isEmpty()) {
			resultLabel.setText("No bet placed!");
			return;
		}
		
		int result = random.nextInt(37); //Random number between 0 and 36
		int totalWinnings = currentBets.stream().mapToInt(bet -> bet.checkWin(result) ? bet.betAmount * bet.getMultiplier() : 0).sum();
		balance += totalWinnings;
		
		// Display winning number
		resultLabel.setText("Winning number: " + result);
		
		// Updates the roulette image
		updateRouletteImage(result);
		
		// Update balance label
		updateBalanceLabel();
		
		// Display the most frequent number
		displayMostFrequentNumber(result);
		
		// Reset the bets after the spin
		currentBets.clear();
		
	}	
	
	// Method to update the roulette image based on the spin result
	private void updateRouletteImage(int result) {
		String imagePath = ""; // displays the image of the ball on the result
		
		// Set the image path based on the result
		if (result == 0) {
			imagePath = "/images/0.png";
		} else if (result == 1) {
			imagePath = "/images/1.png";
		} else if (result == 2) {
			imagePath = "/images/2.png";
		} else if (result == 3) {
			imagePath = "/images/3.png";
		} else if (result == 4) {
			imagePath = "/images/4.png";
		} else if (result == 5) {
			imagePath = "/images/5.png";
		} else if (result == 6) {
			imagePath = "/images/6.png";
		} else if (result == 7) {
			imagePath = "/images/7.png";
		} else if (result == 8) {
			imagePath = "/images/8.png";
		} else if (result == 9) {
			imagePath = "/images/9.png";
		} else if (result == 10) {
			imagePath = "/images/10.png";
		} else if (result == 11) {
			imagePath = "/images/11.png";
		} else if (result == 12) {
			imagePath = "/images/12.png";
		} else if (result == 13) {
			imagePath = "/images/13.png";
		} else if (result == 14) {
			imagePath = "/images/14.png";
		} else if (result == 15) {
			imagePath = "/images/15.png";
		} else if (result == 16) {
			imagePath = "/images/16.png";
		} else if (result == 17) {
			imagePath = "/images/17.png";
		} else if (result == 18) {
			imagePath = "/images/18.png";
		} else if (result == 19) {
			imagePath = "/images/19.png";
		} else if (result == 20) {
			imagePath = "/images/20.png";
		} else if (result == 21) {
			imagePath = "/images/21.png";
		} else if (result == 22) {
			imagePath = "/images/22.png";
		} else if (result == 23) {
			imagePath = "/images/23.png";
		} else if (result == 24) {
			imagePath = "/images/24.png";
		} else if (result == 25) {
			imagePath = "/images/25.png";
		} else if (result == 26) {
			imagePath = "/images/26.png";
		} else if (result == 27) {
			imagePath = "/images/27.png";
		} else if (result == 28) {
			imagePath = "/images/28.png";
		} else if (result == 29) {
			imagePath = "/images/29.png";
		} else if (result == 30) {
			imagePath = "/images/30.png";
		} else if (result == 31) {
			imagePath = "/images/31.png";
		} else if (result == 31) {
			imagePath = "/images/31.png";
		} else if (result == 32) {
			imagePath = "/images/32.png";
		} else if (result == 33) {
			imagePath = "/images/33.png";
		} else if (result == 34) {
			imagePath = "/images/34.png";
		} else if (result == 35) {
			imagePath = "/images/35.png";
		} else if (result == 36) {
			imagePath = "/images/36.png";
		} else {
			imagePath = "/images/roulette.png"; //For any result not specifically handled 
		}
		
		try {
			// Try to load the image
			InputStream imageStream = getClass().getResourceAsStream(imagePath);
			if (imageStream == null) {
				System.out.println("Image file not found for result: " + result);
			} else { 
				Image image = new Image(imageStream);
				rouletteImage.setImage(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error loading image for result: " + result);
		}
		
		
	//	try {
	//		Image image = new Image(getClass().getResourceAsStream(imagePath));
	//		if (image.isError()) {
	//			throw new Exception("Error loading image: " + imagePath);
	//		}
	//		rouletteImage.setImage(image);
	//	} catch (Exception e) {
	//		System.out.println("Image file not found for result: " + result);
	//		e.printStackTrace();
	//	}
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
