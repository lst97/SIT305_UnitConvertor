package com.example.unitconvertor

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView

 class UnitType{
    companion object{
        const val LENGTH = "Length"
        const val WEIGHT = "Weight"
        const val TEMP = "Temperature"
    }
}

 interface  IUnits{
    companion object
    var value:Double
    val type:String
    // convert any unit in the class to one main unit
    fun convert():Double
    fun convert(toType: String) :Double
}

// store unit info that waiting to be calculate.
 interface IUnitsConvertor{
    val fromUnit : IUnits
    val toType: String


    fun convert() :Double
}

 class UnitsConvertor// for example: can not convert kg to cm
     (from: IUnits, to: String) : IUnitsConvertor{
     override val fromUnit:IUnits = from
     override val toType: String = to

    override fun convert(): Double {
        return fromUnit.convert(toType)
    }

}

class Length(override var value: Double, override val type: String) : IUnits{
    companion object {
        const val INCH = "inch"
        const val FOOT = "foot"
        const val YARD = "yard"
        const val MILE = "mile"
        const val CM = "cm"
        const val M = "m"
        const val KM = "km"
    }


    override fun convert(): Double {
        // convert any unit to cm
        return when(type){
            INCH -> value * 2.54
            FOOT -> value * 30.48
            YARD -> value * 91.44
            MILE -> value * 160934.4
            CM -> value
            M -> value * 100
            KM -> value * 100000
            else -> throw IllegalArgumentException("Invalid unit: $type")
        }

    }

    override fun convert(toType:String) :Double {
        val cm = convert()
        val result : Double = when(toType){
            INCH -> cm * 0.39370
            FOOT -> cm * 0.032808
            YARD -> cm * 0.010936
            MILE -> cm * 6.2137e-6
            CM -> cm
            M -> cm / 100
            KM -> cm / 100000
            else -> throw IllegalArgumentException("Invalid unit: $toType")
        }
        return result
    }

}

class Weight(override var value: Double, override val type: String) : IUnits {
    companion object {
        const val POUND = "pound"
        const val OUNCE = "ounce"
        const val TON = "ton"
        const val G = "g"
        const val KG = "kg"
    }

    override fun convert(): Double {
        return when(type){
            POUND -> value * 453.59237
            OUNCE -> value * 28.3495231
            TON -> value * 1000000
            G -> value
            KG -> value * 1000
            else -> throw IllegalArgumentException("Invalid unit: $type")
        }

    }

    override fun convert(toType: String): Double {
        val g = convert()
        val result :Double = when (toType){
            POUND -> g * 0.0022046
            OUNCE -> g * 0.035274
            TON -> g * 1e-6
            G -> g
            KG -> g / 1000
            else -> throw IllegalArgumentException("Invalid unit: $toType")

        }
        return result
    }
}

class Temp(override var value: Double, override val type: String) : IUnits{
    companion object {
        const val C = "C"
        const val K = "K"
        const val F = "F"
    }

    override fun convert(): Double {
        // convert any unit to cm
        return when(type){
            C -> value
            K -> value - 273.15
            F -> (value - 32) * 5/9
            else -> throw IllegalArgumentException("Invalid unit: $type")
        }

    }

    override fun convert(toType: String): Double {
        val c = convert()
        return when(toType){
            C -> c
            K -> c + 273.15
            F -> (c * 1.8) + 32
            else -> throw IllegalArgumentException("Invalid unit: $toType")
        }
    }

}

class MainActivity : AppCompatActivity() {
    // radios
    private lateinit var radioGroup : RadioGroup
    private lateinit var radioButtonLength :RadioButton
    private lateinit var radioButtonWeight:RadioButton
    private lateinit var radioButtonTemp:RadioButton

    // spinners
    private lateinit var fromSpinner:Spinner
    private lateinit var toSpinner:Spinner
    private val lengthValuesFrom = arrayOf(Length.INCH, Length.FOOT, Length.YARD, Length.MILE)
    private val lengthValuesTo = arrayOf(Length.CM, Length.M, Length.KM)
    private val weightValuesFrom = arrayOf(Weight.POUND, Weight.OUNCE, Weight.TON)
    private val weightValuesTo = arrayOf(Weight.G, Weight.KG)
    private val tempValues = arrayOf(Temp.C, Temp.K, Temp.F)

    // buttons
    private lateinit var calculateButton :Button

    // text input
    private lateinit var textInput :EditText
    private lateinit var textView : TextView

    private fun initComponent(){
        // radios
        radioGroup = findViewById(R.id.radio_group)
        radioButtonLength = findViewById(R.id.radio_btn_length)
        radioButtonWeight = findViewById(R.id.radio_btn_weight)
        radioButtonTemp = findViewById(R.id.radio_btn_temperature)

        // spinners
        fromSpinner = findViewById(R.id.spinner_from)
        toSpinner = findViewById(R.id.spinner_to)

        // buttons
        calculateButton = findViewById(R.id.btn_calculate)
        calculateButton.isEnabled = false

        // text input
        textInput = findViewById(R.id.input_value)
        textView = findViewById(R.id.label_result)


    }
    private fun initComponentsText(){
        radioButtonLength.text = UnitType.LENGTH
        radioButtonWeight.text = UnitType.WEIGHT
        radioButtonTemp.text = UnitType.TEMP
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponent()
        initComponentsText()


        calculateButton.setOnClickListener{
            val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                throw IllegalStateException("No Radio Button Selected")
            }

            val selectedValueFrom = fromSpinner.selectedItem.toString()
            val selectedValueTo = toSpinner.selectedItem.toString()

            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val selectedRadioButtonText: String = selectedRadioButton.text.toString()

            try {
                val inputValue = textInput.text.toString().toDouble()
                val unitsConvertor :UnitsConvertor = when (selectedRadioButtonText){
                    UnitType.LENGTH -> {
                         UnitsConvertor(Length(inputValue, selectedValueFrom), selectedValueTo)

                    }
                    UnitType.WEIGHT ->{
                          UnitsConvertor(Weight(inputValue, selectedValueFrom), selectedValueTo)


                    }
                    UnitType.TEMP -> {
                        UnitsConvertor(Temp(inputValue, selectedValueFrom), selectedValueTo)

                    }
                    else -> throw IllegalStateException("Type not accepted")
                }
                val result = unitsConvertor.convert()
                textView.text = result.toString()


            }catch (e: java.lang.NumberFormatException){
                textView.text = "Not A Number"
            }

        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Handle the checked change event here
            // Set spinner to specify options
            calculateButton.isEnabled = true
            when (checkedId) {
                R.id.radio_btn_length -> {
                    fromSpinner.adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, lengthValuesFrom)
                    toSpinner.adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, lengthValuesTo)

                }
                R.id.radio_btn_weight -> {
                    fromSpinner.adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, weightValuesFrom)
                    toSpinner.adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, weightValuesTo)

                }
                R.id.radio_btn_temperature -> {
                    fromSpinner.adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tempValues)
                    toSpinner.adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tempValues)
                }
            }
        }
    }
}