package group27.mad.com.baccalculator;
/*
Group 27
Homework 1
MainActivity.java
Nanda kishore Kolluru
Ravi Teja Yarlagadda
 */
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    char gender = '0';
    double weight = -1;
    double currentBac = 0;
    List<Integer> qtyList = new ArrayList<>();
    List<Double> percList = new ArrayList<>();

    public static final int QTY_1OZ = 1;
    public static final int QTY_5OZ = 5;
    public static final int QTY_12OZ = 12;
    public static final int BAC_PERC_MULTIPLIER = 5;
    public static final int BAC_PBAR_MULTIPLIER = 100;
    protected static final DecimalFormat decFmt = new DecimalFormat("0.00");

    private TextView textVAlcPerc;
    private Button btnSave, btnAddDrink, btnReset;
    private EditText etWeight;
    private Switch switGender;
    private RadioGroup rgAclQty;
    private TextView tvBacValue;
    private ProgressBar pbar;
    private SeekBar sbar;
    private TextView tvBacStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setIconInActionBar();
        hookInterestingComponents();
        setActionListeners();
    }

    private void setIconInActionBar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setIcon(R.mipmap.ic_launcher);
        }
    }

    private void setActionListeners() {
        setSeekBarAction();
        setSaveButtonAction();
        setAddDrinkAction();
        setResetAction();
    }

    private void setResetAction() {
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = '0';
                weight = -1;
                currentBac = 0;
                etWeight.setText("");
                qtyList.clear();
                percList.clear();
                switGender.setChecked(false);
                btnSave.setEnabled(true);
                btnAddDrink.setEnabled(true);
                rgAclQty.check(R.id.radio1oz);
                sbar.setProgress(1);
                tvBacValue.setText(getString(R.string.bac_level00_label_text));
                tvBacStatus.setText(getString(R.string.text_you_are_safe));
                tvBacStatus.setBackgroundResource(R.drawable.rounded_corners_safe);
                pbar.setProgress(0);
            }
        });
    }

    private void setAddDrinkAction() {
        btnAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    int qty = getAlcoholQty();
                    qtyList.add(qty);
                    double perc = getAlcPerc();
                    percList.add(perc);
                    calcBacAndUpdateStatus(qty, perc);
                } else {
                    etWeight.setError("Enter the weight in lbs and save.");
                }
            }
        });
    }

    private void calcBacAndUpdateStatus(int qty, double perc) {
        currentBac += calcBac(qty, perc);
        updateSafetyStatus();
    }

    private double calcBac(int qty, double perc) {
        double bac = new BloodAlcCalculationHelper(qty, perc, weight, gender).calcBac();
        return bac;
    }


    private void setSaveButtonAction() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = Double.parseDouble(etWeight.getText().toString());
                gender = switGender.isChecked() ? 'F' : 'M';
                Log.d("bacapp", "Values Saved " + weight + " " + gender);
                if (qtyList.size() > 0 && percList.size() > 0) {
                    currentBac = 0;
                    for (int i = 0; i < qtyList.size(); i++) {
                        currentBac += calcBac(qtyList.get(i), percList.get(i));
                    }
                    updateSafetyStatus();
                }
            }
        });

    }

    private void setSeekBarAction() {
        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textVAlcPerc.setText(String.valueOf(progress * 5) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    private void hookInterestingComponents() {
        textVAlcPerc = (TextView) findViewById(R.id.textViewAlcPercBarVal);
        sbar = (SeekBar) findViewById(R.id.seekBarAlcoholPerc);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnAddDrink = (Button) findViewById(R.id.btnAddDrink);
        btnReset = (Button) findViewById(R.id.btnReset);
        etWeight = (EditText) findViewById(R.id.editWeight);
        switGender = (Switch) findViewById(R.id.switchGender);
        rgAclQty = (RadioGroup) findViewById(R.id.radioGroup1);
        tvBacValue = (TextView) findViewById(R.id.textViewBacValue);
        tvBacStatus = (TextView) findViewById(R.id.textViewBacStatus);
        pbar = (ProgressBar) findViewById(R.id.progressBarBac);
    }

    private void updateSafetyStatus() {
        tvBacValue.setText(getString(R.string.bac_levelblank_label_text) + " " + decFmt.format(currentBac));
        Log.d("app", "current bac " + currentBac);
        pbar.setProgress((int) (currentBac * BAC_PBAR_MULTIPLIER));
        if (currentBac <= 0.08) {
            tvBacStatus.setText(getString(R.string.text_you_are_safe));
            tvBacStatus.setBackgroundResource(R.drawable.rounded_corners_safe);
        } else if (currentBac > 0.08 && currentBac < 0.2) {
            tvBacStatus.setText(getString(R.string.text_be_careful));
            tvBacStatus.setBackgroundResource(R.drawable.rounded_corners_careful);
        } else if (currentBac > 0.2) {
            tvBacStatus.setText(getString(R.string.text_over_the_limit));
            tvBacStatus.setBackgroundResource(R.drawable.rounded_corners_danger);
            disableIfOverLimit();
        }
    }

    private void disableIfOverLimit() {
        if (currentBac >= 0.25) {
            btnAddDrink.setEnabled(false);
            btnSave.setEnabled(false);
            showOverLimitToast();
        }
    }

    private double getAlcPerc() {
        return sbar.getProgress() * BAC_PERC_MULTIPLIER * 0.01;
    }

    private int getAlcoholQty() {
        int checked = rgAclQty.getCheckedRadioButtonId();
        if (checked == R.id.radio1oz) {
            return QTY_1OZ;
        } else if (checked == R.id.radio5oz) {
            return QTY_5OZ;
        } else if (checked == R.id.radio12oz) {
            return QTY_12OZ;
        }
        return 0;
    }

    private boolean isInputValid() {
        if (gender != '0' && weight != -1)
            return true;
        return false;
    }

    private void showOverLimitToast() {
        Context context = getApplicationContext();
        CharSequence text = "No more drinks for you!";
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }
}
