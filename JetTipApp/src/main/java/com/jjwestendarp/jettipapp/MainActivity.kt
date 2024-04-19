package com.jjwestendarp.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jjwestendarp.jettipapp.components.InputField
import com.jjwestendarp.jettipapp.ui.theme.PortfolioTheme
import com.jjwestendarp.jettipapp.util.calculateTipPercentage
import com.jjwestendarp.jettipapp.util.calculateTotalPerPerson
import com.jjwestendarp.jettipapp.util.calculateTotalTip
import com.jjwestendarp.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    PortfolioTheme {
        Surface(
            color = MaterialTheme.colorScheme.primary
        ) {
            content()
        }
    }

}

@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = MaterialTheme.colorScheme.secondary
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun MainContent() {
    Column {
        val splitByState = remember {
            mutableIntStateOf(1)
        }
        val tipAmountState = remember {
            mutableDoubleStateOf(0.0)
        }
        val totalPerPersonState = remember {
            mutableDoubleStateOf(0.0)
        }
        TopHeader(totalPerPersonState.doubleValue)
        BillForm(
            splitByState = splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState
        ) {
        }
    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }
    val tipPercentageState = remember {
        mutableIntStateOf(0)
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            // Split Row
            if (validState) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                if (splitByState.value > range.first) {
                                    splitByState.value - 1
                                } else
                                    1
                                tipPercentageState.intValue = calculateTipPercentage(sliderPositionState.floatValue)
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBillState.value.toDouble(),
                                    splitByState.value,
                                    tipPercentageState.intValue
                                )
                            })
                        Text(
                            text = "${splitByState.value}", modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 9.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value += 1
                                } else {
                                    splitByState.value = range.last
                                }
                                tipPercentageState.intValue = calculateTipPercentage(sliderPositionState.floatValue)
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBillState.value.toDouble(),
                                    splitByState.value,
                                    tipPercentageState.intValue
                                )
                            })
                    }
                }
                // Tip Row
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }
                // Percentage Column
                Column(
                    modifier = Modifier.padding(3.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "${tipPercentageState.intValue}%")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(
                        value = sliderPositionState.floatValue, onValueChange = { newVal ->
                            sliderPositionState.floatValue = newVal
                            tipPercentageState.intValue = calculateTipPercentage(sliderPositionState.floatValue)
                            Log.d("Slider", "BillForm: ${tipPercentageState.intValue}")
                            tipAmountState.value =
                                calculateTotalTip(totalBillState.value.toDouble(), tipPercentageState.intValue)
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBillState.value.toDouble(),
                                splitByState.value,
                                tipPercentageState.intValue
                            )
                            Log.d(
                                "Slider",
                                "BillForm: slider value = $newVal, tip: ${tipPercentageState.intValue}"
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        steps = 5
                    )
                }
            } else {
                Box {
                    // Show nothing
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApp {
        MainContent()
    }
}