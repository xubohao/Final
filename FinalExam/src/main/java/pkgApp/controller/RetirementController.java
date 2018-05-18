package pkgApp.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.formula.functions.FinanceLib;

import com.sun.prism.paint.Color;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import javafx.beans.value.*;

import pkgApp.RetirementApp;
import pkgCore.Retirement;

public class RetirementController implements Initializable {

	private RetirementApp mainApp = null;
	@FXML
	private TextField txtSaveEachMonth;
	@FXML
	private TextField txtYearsToWork;
	@FXML
	private TextField txtAnnualReturnWorking;
	@FXML
	private TextField txtWhatYouNeedToSave;
	@FXML
	private TextField txtYearsRetired;
	@FXML
	private TextField txtAnnualReturnRetired;
	@FXML
	private TextField txtRequiredIncome;
	@FXML
	private TextField txtMonthlySSI;

	private HashMap<TextField, String> hmTextFieldRegEx = new HashMap<TextField, String>();
	
	/* add these two range map */
	private HashMap<TextField, Pair<Integer, Integer>> hmTextFieldIntRange 
	= new HashMap<TextField, Pair<Integer, Integer>>();
	private HashMap<TextField, Pair<Double, Double>> hmTextFieldFloatRange 
	= new HashMap<TextField, Pair<Double, Double>>();

	public RetirementApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(RetirementApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Adding an entry in the hashmap for each TextField control I want to validate
		// with a regular expression
		// "\\d*?" - means any decimal number
		// "\\d*(\\.\\d*)?" means any decimal, then optionally a period (.), then
		// decmial
		
		/* these are whole numbers */
		hmTextFieldRegEx.put(txtYearsToWork, "\\d*?");
		hmTextFieldRegEx.put(txtYearsRetired, "\\d*?");
		hmTextFieldRegEx.put(txtRequiredIncome, "\\d*?");
		hmTextFieldRegEx.put(txtMonthlySSI, "\\d*?");

		/* these are decimal numbers */
		hmTextFieldRegEx.put(txtAnnualReturnWorking, "\\d*(\\.\\d*)?");
		hmTextFieldRegEx.put(txtAnnualReturnRetired, "\\d*(\\.\\d*)?");

		// Check out these pages (how to validate controls):
		// https://stackoverflow.com/questions/30935279/javafx-input-validation-textfield
		// https://stackoverflow.com/questions/40485521/javafx-textfield-validation-decimal-value?rq=1
		// https://stackoverflow.com/questions/8381374/how-to-implement-a-numberfield-in-javafx-2-0
		// There are some examples on how to validate / check format

		Iterator it = hmTextFieldRegEx.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			TextField txtField = (TextField) pair.getKey();
			String strRegEx = (String) pair.getValue();

			txtField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
						Boolean newPropertyValue) {
					// If newPropertyValue = true, then the field HAS FOCUS
					// If newPropertyValue = false, then field HAS LOST FOCUS
					if (!newPropertyValue) {
						if (!txtField.getText().matches(strRegEx)) {
							txtField.setText("");
							txtField.requestFocus();
						}
					}
				}
			});
		}

		// **DONE!
		// DONE: Validate Working Annual Return %, accept only numbers and decimals
		// DONE: Validate Years retired, accepted only decimals
		// DONE: Validate Retired Annual Return %, accept only numbers and deciamls
		// DONE: Validate Required Income, accept only decimals
		// DONE: Validate Monthly SSI, accept only decimals
		
		/* Check for the data range. */
		// I add range maps that add listeners to the text fields in a similar way above.
		hmTextFieldIntRange.put(txtYearsToWork, 	new Pair<Integer, Integer>(0, 40));
		hmTextFieldIntRange.put(txtYearsRetired, 	new Pair<Integer, Integer>(0, 20));
		hmTextFieldIntRange.put(txtRequiredIncome, 	new Pair<Integer, Integer>(2642, 10000));
		hmTextFieldIntRange.put(txtMonthlySSI, 		new Pair<Integer, Integer>(0, 2642));

		hmTextFieldFloatRange.put(txtAnnualReturnWorking, new Pair<Double, Double>(0.0, 10.0));
		hmTextFieldFloatRange.put(txtAnnualReturnRetired, new Pair<Double, Double>(0.0, 10.0));
		
		it = hmTextFieldIntRange.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			TextField txtField = (TextField) pair.getKey();
			Pair<Integer, Integer> range = (Pair<Integer, Integer>) pair.getValue();
			int lowerBound = range.getKey();
			int upperBound = range.getValue();

			txtField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
						Boolean newPropertyValue) {
					String text = txtField.getText();
					if (!newPropertyValue && !text.isEmpty()) {
						int value = Integer.parseInt(text);
						if (!(value >= lowerBound && value <= upperBound)) {
							txtField.setText("");
							txtField.requestFocus();
						}
					}
				}
			});
		}		
		it = hmTextFieldFloatRange.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			TextField txtField = (TextField) pair.getKey();
			Pair<Double, Double> range = (Pair<Double, Double>) pair.getValue();
			double lowerBound = range.getKey();
			double upperBound = range.getValue();

			txtField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
						Boolean newPropertyValue) {
					String text = txtField.getText();
					if (!newPropertyValue && !text.isEmpty()) {
						double value = Double.parseDouble(text);
						if (!(value >= lowerBound && value <= upperBound)) {
							txtField.setText("");
							txtField.requestFocus();
						}
					}
				}
			});
		}
	}

	@FXML
	public void btnClear(ActionEvent event) {
		System.out.println("Clear pressed");

		// disable read-only controls
		txtSaveEachMonth.setDisable(true);
		txtWhatYouNeedToSave.setDisable(true);
		/* I think it's reasonable to clear read-only controls, too */
		txtSaveEachMonth.clear();
		txtWhatYouNeedToSave.clear();
		
		// Clear, enable txtYearsToWork
		//txtYearsToWork.clear();
		//txtYearsToWork.setDisable(false);

		// **DONE!
		// TODO: Clear, enable the rest of the input controls. Hint! You already have a
		// HashMap of all the input controls....!!!!
		/* thanks for the hint~ */
		Iterator it = hmTextFieldRegEx.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			TextField txtField = (TextField) pair.getKey();
			txtField.clear();
			txtField.setDisable(false);
		}
	}

	@FXML
	public void btnCalculate() {
		
		/* check: if all entries have valid values */
		if (txtYearsToWork.getText().isEmpty() ||
				txtAnnualReturnWorking.getText().isEmpty() ||
				txtYearsRetired.getText().isEmpty() ||
				txtAnnualReturnRetired.getText().isEmpty() ||
				txtRequiredIncome.getText().isEmpty() ||
				txtMonthlySSI.getText().isEmpty())
		{
			System.err.println("Error: not all entries have valid values when request calculation!");
			return;
		}

		System.out.println("calculating");

		txtSaveEachMonth.setDisable(false);
		txtWhatYouNeedToSave.setDisable(false);

		// **DONE!
		// DONE: Calculate txtWhatYouNeedToSave value...
		// DONE: Then calculate txtSaveEachMonth, using amount from txtWhatYouNeedToSave
		// as input
		
		Retirement rtm = new Retirement(
				Integer.parseInt(txtYearsToWork.getText()),
				Double.parseDouble(txtAnnualReturnWorking.getText()) / 100, 
				Integer.parseInt(txtYearsRetired.getText()), 
				Double.parseDouble(txtAnnualReturnRetired.getText()) / 100, 
				Integer.parseInt(txtRequiredIncome.getText()), 
				Integer.parseInt(txtMonthlySSI.getText())
			);
		txtSaveEachMonth.setText(String.valueOf(rtm.MonthlySavings()));
		txtWhatYouNeedToSave.setText(String.valueOf(rtm.TotalAmountToSave()));
	}
}
