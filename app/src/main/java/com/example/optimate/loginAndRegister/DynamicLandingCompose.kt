package com.example.optimate.loginAndRegister

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.optimate.R
import com.example.optimate.businessOwner.AccountsActivity
import com.example.optimate.businessOwner.FinancesActivity
import com.example.optimate.businessOwner.Requests
import com.example.optimate.businessOwner.SchedulerActivity
import com.example.optimate.businessOwner.TitlesActivity
import com.example.optimate.employeeFlow.ClockModule
import com.example.optimate.employeeFlow.PayStub
import com.example.optimate.employeeFlow.ScheduleModule
import com.example.optimate.employeeFlow.ViewTimeOffRequests

private val managerAccessList = listOf(
    "Scheduling",
    "Finances",
    "View Employees",
    "Time-off Requests Approval"
)

private val employeeAccessList = listOf(
    "View Schedule",
    "Clock-in/out",
    "View Payroll",
    "Request Time-off",
    "Add Expense",
)

private val businessOwnerAccessList= listOf(
    "Titles",
    "Accounts",
    "Finances",
    "Scheduling",
    "Requests",
)
@Composable
fun DynamicLandingScreen(accessList: List<String>, title: String) {
    val businessOwnerBackgroundColor = Color(0xFFFFD7D7)
    val employeeBackgroundColor = Color(0xFFC4F0E6)
    val managerBackgroundColor = Color(0xFFE2EFFF)
    val businessOwnerFrameColor = Color(0xFFFF5E5E)
    val employeeFrameColor = Color(0xFF14B8A6)
    val managerFrameColor = Color(0xFF84BDFF)
    Scaffold(
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {  // Apply the padding to the Box
                if (title == "businessOwner") {
                    ButtonList(AccessList = businessOwnerAccessList, BackgroundColor=businessOwnerBackgroundColor, FrameColor=businessOwnerFrameColor)
                    Log.d("compose", "access: $accessList")
                }
                else if (title == "Employee") {
                    ButtonList(AccessList = accessList, BackgroundColor=employeeBackgroundColor, FrameColor=employeeFrameColor)
                    Log.d("compose", "access: $accessList")
                } else {
                    ButtonList(AccessList = accessList, BackgroundColor=managerBackgroundColor, FrameColor=managerFrameColor)
                    Log.d("compose", "access: $accessList")
                }
            }
        }
    )
}

@Composable
fun ButtonList(AccessList: List<String>, BackgroundColor: Color, FrameColor: Color) {
    val context = LocalContext.current

    val modifiedList = AccessList.filter { item ->
        item != "Request Time-off" && item != "Add Expense"
    }

    ElevatedCard(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundColor),
    ) {
        Box(
            contentAlignment = Alignment.Center, // This centers its children
            modifier = Modifier.fillMaxSize() // This ensures the Box occupies the entire ElevatedCard
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier.fillMaxWidth() // You might adjust this for better centering, depending on your design
            ) {
                itemsIndexed(modifiedList) { index, item ->
                    EachButton(
                        text = item,
                        onClick = { when (item) {
                            "Titles" -> context.startActivity(Intent(context, TitlesActivity::class.java))
                            "Accounts" -> context.startActivity(Intent(context, AccountsActivity::class.java))
                            "Finances" -> context.startActivity(Intent(context, FinancesActivity::class.java))
                            "Clock-in/out" -> context.startActivity(Intent(context, ClockModule::class.java))
                            "Schedule" -> context.startActivity(Intent(context, ScheduleModule::class.java))
                            "Scheduling" -> context.startActivity(Intent(context, SchedulerActivity::class.java))
                            "Requests" -> context.startActivity(Intent(context, Requests::class.java))
                            "View Employees" -> context.startActivity(Intent(context, AccountsActivity::class.java))
                            "View Schedule" -> context.startActivity(Intent(context, ScheduleModule::class.java))
                            "View Payroll" -> context.startActivity(Intent(context, PayStub::class.java))


                        }},
                        modifier = Modifier.fillMaxWidth(),
                        FrameColor = FrameColor
                    )
                }
            }
        }
    }
}


@Composable
fun EachButton(text: String, onClick: () -> Unit, modifier: Modifier, FrameColor: Color) {
    val frameColor = Color(0xFF6750A4)
    val buttonImg = when (text) {
        "Titles" -> painterResource(id =R.drawable.ic_accounts_foreground)
        "Accounts" -> painterResource(id =R.drawable.accounts)
        "Finances" -> painterResource(id =R.drawable.ic_finances_foreground)
        "Schedule" -> painterResource(id =R.drawable.ic_schedule_foreground)
        "Scheduling" -> painterResource(id =R.drawable.scheduling)
        "Requests" -> painterResource(id =R.drawable.ic_requests_foreground)
        "View Employees" -> painterResource(id =R.drawable.ic_roles_foreground)
        "Time-off Requests Approval" -> painterResource(id =R.drawable.ic_requests_foreground)
        "View Schedule" -> painterResource(id =R.drawable.ic_schedule_foreground)
        "Clock-in/out" -> painterResource(id =R.drawable.clock)
        "View Payroll" -> painterResource(id =R.drawable.payroll)
        else -> painterResource(id =R.drawable.ic_roles_foreground)
    }
    val buttonText = when(text) {
        "Scheduling" -> "Scheduler"
        "View Employees" -> "Employees"
        "View Schedule" -> "Schedule"
        "Clock-in/out" -> "Clock-In/Out"
        "View Payroll" -> "Payroll"
        "Time-off Requests Approval" -> "Requests"
        else -> text
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .wrapContentSize()
                .height(80.dp)
                .width(80.dp),
                //.border(3.dp, FrameColor, shape = RoundedCornerShape(15.dp)),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(FrameColor),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 16.dp)

        ) {

            Icon(
                painter = buttonImg,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = Color.White
            )

        }
        Text(
            text = buttonText,
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }

}

@Preview
@Composable
fun BusinessOwnerButtonListPreview() {
    DynamicLandingScreen(managerAccessList, "businessOwner")
}

