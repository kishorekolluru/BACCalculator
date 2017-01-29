package group27.mad.com.baccalculator;
/*
Group 27
Homework 1
BloodAlcCalculationHelper.java
Nanda kishore Kolluru
Ravi Teja Yarlagadda
 */

public class BloodAlcCalculationHelper {
    private static final double bacConstant = 6.24;
    private static final double maleConst = 0.68;
    private static final double femaleConst = 0.55;

    double alcOunce;
    double alcPerc;
    double weight;
    char gender;

    private BloodAlcCalculationHelper(){}

    public BloodAlcCalculationHelper(double alcOunce, double percent, double weight, char gender){
        this.alcOunce = alcOunce;
        this.alcPerc = percent;
        this.weight=weight;
        this.gender=gender;
    }
    public double calcBac() {
        if (gender == 'M')
            return (alcOunce * alcPerc * bacConstant) / (weight * maleConst);
        else
            return (alcOunce * alcPerc * bacConstant) / (weight * femaleConst);
    }

}
