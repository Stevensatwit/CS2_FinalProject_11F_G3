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
	
	@FXML
	public void initialize() {
		rouletteImage.setImage(new Image(getClass().getResourceAsStream(""))); //import the image
	}
	
	@FXML
	private void placeRedBlackBet() {
		betType = "color";
		getBetAmount();
	}
	
	@FXML
	private void placeOddEvenBet() {
		betType = "oddEven";
		getBetAmount();
	}
	
	@FXML 
	private void placeNumberBet() {
		betType = "number";
		getBetAmount();
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Number Bet");
		dialog.setHeaderText("Choose a number (0-36): ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> chosenNumber = Integer.parseInt(value));
	}
	
	private void getBetAmount() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Place Bet");
		dialog.setHeaderText("Enter your bet amount: ");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(value -> {
			int amount = Integer.parseInt(value);
			if (amount > balance) {
				resultLabel.setText("Not enough balance!");
			} else {
				betAmount = amount;
				balance -= betAmount;
				updateBalance();
			}
		});
		
		
	}
	
	// add spinWheel
		// add win/loss results
	
	
	
	private void updateBalance() {
		balanceLabel.setText("Balance: $" + balance);
	}
	
}
